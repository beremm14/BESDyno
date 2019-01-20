package main;

import data.Bike;
import data.Config;
import data.Database;
import data.Diagram;
import data.Environment;
import development.CommunicationLogger;
import development.gui.DevInfoPane;
import development.gui.LoggedCommPane;
import development.gui.MeasurementValuesPane;
import gui.AboutDialog;
import gui.DiagramSetDialog;
import gui.HelpDialog;
import gui.MeasureDialog;
import gui.ResultDialog;
import gui.SettingsDialog;
import gui.VehicleSetDialog;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;
import jssc.SerialPortException;
import logging.LogBackgroundHandler;
import logging.LogOutputStreamHandler;
import logging.Logger;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.Layer;
import org.jfree.ui.TextAnchor;
import serial.ConnectPortWorker;
import serial.DisconnectPortWorker;
import serial.requests.Request;
import serial.requests.Request.Status;
import serial.requests.Request.Variety;
import serial.Telegram;

/**
 *
 * @author emil
 */
public class BESDyno extends javax.swing.JFrame {

    private static BESDyno instance;

    private static final Logger LOG;
    private static final Logger LOGP;

    //JDialog-Objects
    private AboutDialog about = new AboutDialog(this, false);
    private HelpDialog help = new HelpDialog(this, false);
    private VehicleSetDialog vehicle = new VehicleSetDialog(this, true);
    private SettingsDialog settings = new SettingsDialog(this, true);
    private DevInfoPane infoPane = new DevInfoPane(this, false);
    private LoggedCommPane commPane = new LoggedCommPane(this, false);
    private ResultDialog result = new ResultDialog(this, true);
    private DiagramSetDialog diagramSet = new DiagramSetDialog(this, true);

    private MeasureDialog measure;

    //Object-Variables
    private File file;
    private SwingWorker activeWorker;
    private MyTelegram telegram;
    private jssc.SerialPort port;

    //Variables
    private static boolean devMode = false;
    private static OS os = OS.OTHER;
    private boolean connection = false;
    private boolean secondTry = true;
    private boolean measurementFinished = false;
    private double reqArduVers = 2.0;

    //Communication
    public final List<Request> pendingRequests = new LinkedList<>();
    private final Object syncObj = new Object();

    /**
     * Creates new form BESDyno
     *
     * @return
     */
    public static BESDyno getInstance() {
        if (instance == null) {
            instance = new BESDyno();
        }
        return instance;
    }

    private BESDyno() {
        initComponents();

        setTitle("BESDyno - Zweiradprüfstand");
        setLocationRelativeTo(null);
        setSize(new Dimension(1200, 750));

        jtfStatus.setEditable(false);

        jcbmiDevMode.setState(false);
        jcbmiDebugLogging.setState(false);
        devMode = jcbmiDevMode.getState();
        LOG.setDebugMode(jcbmiDebugLogging.getState());

        addLogFileHandler(devMode);

        refreshPorts();

        try {
            loadConfig();
        } catch (Exception ex) {
            userLogPane(ex, "Fehler bei Config-Datei! Bitte Einstellungen aufrufen und Prüfstand konfigurieren!", LogLevel.WARNING);
        }

        refreshGui();

        setAppearance(Config.getInstance().isDark());

        jcbmiDarkMode.setState(Config.getInstance().isDark());
        userLog(getSalutation() + "Bitte verbinden Sie Ihr Gerät...", LogLevel.INFO);

        LineChart lc = new LineChart();
        lc.initChart();
    }

    private void refreshGui() {
        devMode = jcbmiDevMode.getState();
        LOG.setDebugMode(jcbmiDebugLogging.getState());

        jmiSave.setEnabled(false);
        jmiExport.setEnabled(false);
        jmiPrint.setEnabled(false);
        jmiStartSim.setEnabled(false);
        jbutStartSim.setEnabled(false);
        jmiConnect.setEnabled(false);
        jbutConnect.setEnabled(false);
        jmiDisconnect.setEnabled(false);
        jbutDisconnect.setEnabled(false);
        jcbSerialDevices.setEnabled(false);
        jmiRefresh.setEnabled(false);
        jbutRefresh.setEnabled(false);

        //Development Tools
        jmiShowPendingRequests.setEnabled(false);
        jmiShowLoggedComm.setEnabled(false);
        jcbmiSaveLoggedComm.setEnabled(false);
        jcbmiDebugLogging.setEnabled(false);
        jmenuRequests.setEnabled(false);
        jmiShowMeasurementValues.setEnabled(false);
        if (devMode) {
            jmiShowPendingRequests.setEnabled(true);
            jmiShowLoggedComm.setEnabled(true);
            jcbmiSaveLoggedComm.setEnabled(true);
            jcbmiDebugLogging.setEnabled(true);
            jmenuRequests.setEnabled(true);
            jmiShowMeasurementValues.setEnabled(true);
        }

        if (activeWorker != null) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            return;
        } else {
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
        jmiRefresh.setEnabled(true);
        jbutRefresh.setEnabled(true);

        //Wennn Ports gefunden werden
        if (jcbSerialDevices.getModel().getSize() > 0) {
            jcbSerialDevices.setEnabled(true);
            jmiConnect.setEnabled(true);
            jbutConnect.setEnabled(true);
        }

        //Wenn ein Port geöffnet wurde
        if (port != null) {
            jbutDisconnect.setEnabled(true);
            jmiDisconnect.setEnabled(true);
            jcbSerialDevices.setEnabled(false);
            jmiRefresh.setEnabled(false);
            jbutRefresh.setEnabled(false);
            jmiConnect.setEnabled(false);
            jbutConnect.setEnabled(false);
        }

        if (connection) {
            jmiStartSim.setEnabled(true);
            jbutStartSim.setEnabled(true);
        }

        if (measurementFinished) {
            jmiSave.setEnabled(false);
            jmiExport.setEnabled(false);
            jmiPrint.setEnabled(false);
        }
    }

    //Operation System
    public enum OS {
        MACOS, LINUX, WINDOWS, OTHER
    };

    //Status-Textfeld: Logging for User
    public enum LogLevel {
        FINEST, FINER, FINE, INFO, WARNING, SEVERE
    };

    public void userLog(String msg, LogLevel level) {
        jtfStatus.setText(msg);
        switch (level) {
            case FINEST:
                LOG.finest(msg);
                break;
            case FINER:
                LOG.finer(msg);
                break;
            case FINE:
                LOG.fine(msg);
                break;
            case INFO:
                LOG.info(msg);
                break;
            case WARNING:
                LOG.warning(msg);
                break;
            case SEVERE:
                LOG.severe(msg);
                break;
        }
    }

    public void userLog(Throwable th, String msg, LogLevel level) {
        jtfStatus.setText(msg);
        switch (level) {
            case FINEST:
                LOG.finest(th);
                break;
            case FINER:
                LOG.finer(th);
                break;
            case FINE:
                LOG.fine(th);
                break;
            case INFO:
                LOG.info(th);
                break;
            case WARNING:
                LOG.warning(th);
                break;
            case SEVERE:
                LOG.severe(th);
                break;
        }
    }

    public void userLogPane(String msg, LogLevel level) {
        jtfStatus.setText(msg);
        JOptionPane.showMessageDialog(this, msg, "Fehler ist aufgetreten!", JOptionPane.ERROR_MESSAGE);
        switch (level) {
            case FINEST:
                LOG.finest(msg);
                break;
            case FINER:
                LOG.finer(msg);
                break;
            case FINE:
                LOG.fine(msg);
                break;
            case INFO:
                LOG.info(msg);
                break;
            case WARNING:
                LOG.warning(msg);
                break;
            case SEVERE:
                LOG.severe(msg);
                break;
        }
    }

    public void userLogPane(Throwable th, String msg, LogLevel level) {
        jtfStatus.setText(msg);
        JOptionPane.showMessageDialog(this, msg, "Fehler ist aufgetreten!", JOptionPane.ERROR_MESSAGE);
        switch (level) {
            case FINEST:
                LOG.finest(th);
                break;
            case FINER:
                LOG.finer(th);
                break;
            case FINE:
                LOG.fine(th);
                break;
            case INFO:
                LOG.info(th);
                break;
            case WARNING:
                LOG.warning(th);
                break;
            case SEVERE:
                LOG.severe(th);
                break;
        }
    }

    //Serial-Methods
    private void refreshPorts() {
        final String[] ports = jssc.SerialPortList.getPortNames();

        String preferedPort = null;
        for (String p : ports) {
            if (p.contains("usb") || p.contains("COM") || p.contains("tty") || p.contains("cu")) {
                preferedPort = p;
                break;
            }
        }

        jcbSerialDevices.setModel(new DefaultComboBoxModel<String>(ports));
        if (preferedPort != null) {
            jcbSerialDevices.setSelectedItem(preferedPort);
        }

        refreshGui();
    }

    //Mit Cmd+D kann das Erscheinungbild der Oberfläche geändert werden
    private void setAppearance(boolean dark) {
        if (dark) {
            userLog("Dark-Mode aktiviert", LogLevel.INFO);
            setBackground(Color.darkGray);
            jPanChart.setBackground(Color.darkGray);
            jPanStatus.setBackground(Color.darkGray);
            jPanTools.setBackground(Color.darkGray);

            jLabelDevice.setForeground(Color.white);

            jtfStatus.setBackground(Color.darkGray);
            jtfStatus.setForeground(Color.white);
        } else {
            userLog("Dark-Mode deaktiviert", LogLevel.INFO);
            setBackground(Color.white);
            jPanChart.setBackground(Color.white);
            jPanStatus.setBackground(Color.white);
            jPanTools.setBackground(Color.white);

            jLabelDevice.setForeground(Color.black);

            jtfStatus.setBackground(Color.white);
            jtfStatus.setForeground(Color.black);
        }
    }

    //Begruessung
    private String getSalutation() {
        Date date = Calendar.getInstance().getTime();
        DateFormat df = new SimpleDateFormat("HH");
        int hour = Integer.parseInt(df.format(date));
        if (hour < 12) {
            return "Guten Morgen! ";
        } else if (hour >= 18 && hour <= 22) {
            return "Guten Abend! ";
        } else if (hour > 22) {
            return "Gute Nacht! ";
        } else {
            return "Guten Tag! ";
        }
    }

    //File-Methods
//    private void save() throws IOException, Exception {
//        JFileChooser chooser = new JFileChooser();
//        chooser.setFileFilter(new FileNameExtensionFilter("Bike-Datei (*.bes)", "bes"));
//
//        File home;
//        File folder;
//
//        try {
//            home = new File(System.getProperty("user.home"));
//        } catch (Exception e) {
//            home = null;
//        }
//
//        if (home != null && home.exists()) {
//            folder = new File(home + File.separator + "Bike-Files");
//            if (!folder.exists()) {
//                if (!folder.mkdir()) {
//                    throw new Exception("Internal Error");
//                }
//            }
//            file = new File(folder + File.separator + Bike.getInstance().getVehicleName() + ".bes");
//        } else {
//            file = new File(Bike.getInstance().getVehicleName() + ".bes");
//        }
//        chooser.setSelectedFile(file);
//
//        int rv = chooser.showSaveDialog(this);
//        if (rv == JFileChooser.APPROVE_OPTION) {
//            file = chooser.getSelectedFile();
//            if (!file.getName().contains(".bes")) {
//                userLogPane("Dies ist keine BES-Datei", LogLevel.WARNING);
//            }
//
//            try (BufferedWriter w = new BufferedWriter(new FileWriter(file))) {
//                Bike.getInstance().writeFile(w);
//            } catch (Exception ex) {
//                LOG.severe(ex);
//            }
//        }
//
//    }
    // Saves the Communication Log
    public void saveComm() throws Exception {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Log-Datei (*.log)", "log"));

        File comfile = null;
        File home;
        File folder;
        Date date = Calendar.getInstance().getTime();
        DateFormat df = new SimpleDateFormat("yy.MM.dd-HH.mm.ss.SSS");

        try {
            home = new File(System.getProperty("user.home"));
        } catch (Exception e) {
            home = null;
        }

        if (home != null && home.exists()) {
            folder = new File(home + File.separator + "Bike-Files" + File.separator + "Service_Files");
            if (!folder.exists()) {
                if (!folder.mkdir()) {
                    throw new Exception("Internal Error");
                }
            }
            comfile = new File(folder + File.separator + "CommLog_" + df.format(date) + ".log");
        }

        chooser.setSelectedFile(comfile);

        int rv = chooser.showSaveDialog(this);
        if (rv == JFileChooser.APPROVE_OPTION) {
            comfile = chooser.getSelectedFile();

            try (BufferedWriter w = new BufferedWriter(new FileWriter(comfile))) {
                CommunicationLogger.getInstance().writeFile(w);
            } catch (Exception ex) {
                LOG.severe(ex);
            }
        }
    }

    public static void saveCommAuto() {
        if (isDevMode()) {
            try {
                File comfile;
                File home;
                File folder;
                Date date = Calendar.getInstance().getTime();
                DateFormat df = new SimpleDateFormat("yy.MM.dd-HH.mm.ss.SSS");

                home = new File(System.getProperty("user.home"));

                if (home != null && home.exists()) {
                    folder = new File(home + File.separator + "Bike-Files" + File.separator + "Service_Files");
                    if (!folder.exists()) {
                        if (!folder.mkdir()) {
                            throw new Exception("Internal Error");
                        }
                    }
                    comfile = new File(folder + File.separator + "CommLog_" + df.format(date) + ".log");
                    try (BufferedWriter w = new BufferedWriter(new FileWriter(comfile))) {
                        CommunicationLogger.getInstance().writeFile(w);
                        LOG.fine("Communication Log written...");
                    } catch (Exception ex) {
                        LOG.severe(ex);
                    }
                }
            } catch (Exception ex) {
                LOG.warning(ex);
            }
        }
    }

//    private void open() throws FileNotFoundException, IOException, Exception {
//        JFileChooser chooser = new JFileChooser();
//        chooser.setFileFilter(new FileNameExtensionFilter("Bike-Datei (*.bes)", "bes"));
//
//        File home;
//        File folder;
//
//        try {
//            home = new File(System.getProperty("user.home"));
//        } catch (Exception e) {
//            home = null;
//        }
//
//        if (home != null && home.exists()) {
//            folder = new File(home + File.separator + "Bike-Files");
//            if (!folder.exists()) {
//                if (!folder.mkdir()) {
//                    LOG.severe("Internal Error");
//                }
//            }
//            file = new File(folder + File.separator + Bike.getInstance().getVehicleName() + ".bes");
//        } else {
//            file = new File(Bike.getInstance().getVehicleName() + ".bes");
//        }
//        chooser.setSelectedFile(file);
//
//        int rv = chooser.showOpenDialog(this);
//        if (rv == JFileChooser.APPROVE_OPTION) {
//            file = chooser.getSelectedFile();
//            if (!file.getName().contains(".bes")) {
//                userLogPane("Dies ist keine BES-Datei", LogLevel.WARNING);
//            }
//            try (BufferedReader r = new BufferedReader(new FileReader(file))) {
//                Bike.getInstance().readFile(r);
//            } catch (Exception ex) {
//                userLog(ex, "Fehler beim Einlesen der Datei", LogLevel.WARNING);
//            }
//        }
//    }
    //Config
    private void loadConfig() throws FileNotFoundException, IOException, Exception {
        File home;
        File folder;
        File configFile;

        try {
            home = new File(System.getProperty("user.home"));
        } catch (Exception e) {
            home = null;
        }

        if (home != null && home.exists()) {
            folder = new File(home + File.separator + ".Bike");
            if (!folder.exists()) {
                if (!folder.mkdir()) {
                    throw new Exception("Internal Error");
                }
            }
            configFile = new File(folder + File.separator + "Config.json");
        } else {
            configFile = new File("Config.json");
        }

        if (configFile.exists()) {
            Config.getInstance().readJson(new FileInputStream(configFile));
            jcbmiDarkMode.setState(Config.getInstance().isDark());
        }
    }

    private static File getConfigFile() throws Exception {
        File home;
        File folder;
        File configFile;

        try {
            home = new File(System.getProperty("user.home"));
        } catch (Exception e) {
            home = null;
        }

        if (home != null && home.exists()) {
            folder = new File(home + File.separator + ".Bike");
            if (!folder.exists()) {
                if (!folder.mkdir()) {
                    throw new Exception("Internal Error");
                }
            }
            configFile = new File(folder + File.separator + "Config.json");
        } else {
            configFile = new File("Config.json");
        }
        return configFile;
    }

    private void addLogFileHandler(boolean enabled) {
        if (enabled) {
            try {
                File logfile = null;
                File home;
                File folder;
                Date date = Calendar.getInstance().getTime();
                DateFormat df = new SimpleDateFormat("yy.MM.dd-HH.mm.ss.SSS");

                try {
                    home = new File(System.getProperty("user.home"));
                } catch (Exception e) {
                    home = null;
                }

                if (home != null && home.exists()) {
                    folder = new File(home + File.separator + "Bike-Files" + File.separator + "Service_Files");
                    if (!folder.exists()) {
                        if (!folder.mkdir()) {
                            throw new Exception("Internal Error");
                        }
                    }
                    logfile = new File(folder + File.separator + "Log_" + df.format(date) + ".log");
                }
                LOGP.addHandler(new LogBackgroundHandler(new LogOutputStreamHandler(new BufferedOutputStream(new FileOutputStream(logfile.getPath())))));
            } catch (Exception ex) {
                LOG.warning(ex);
            }
        }
    }

    //Getter
    public MyTelegram getTelegram() {
        return telegram;
    }

    public boolean isDark() {
        return Config.getInstance().isDark();
    }

    public static boolean isDevMode() {
        return devMode;
    }

    public OS getOs() {
        return os;
    }

    public jssc.SerialPort getPort() {
        return port;
    }

    public double getReqArduVers() {
        return reqArduVers;
    }

    public boolean hasConnection() {
        return connection;
    }

    //Setter
    public void setConnection(boolean connection) {
        this.connection = connection;
    }

    public void setPort(jssc.SerialPort port) {
        this.port = port;
    }

    //Communication
    public void addPendingRequest(Request request) {
        try {
            LOG.debug(request.getReqMessage() + " added to pendingRequests");
            pendingRequests.add(request);
        } catch (Exception ex) {
            userLog(ex, "ERROR at Request: " + request.getReqName(), LogLevel.WARNING);
        } finally {
            refreshGui();
        }
    }

    public boolean removePendingRequest(Request request) {
        return pendingRequests.remove(request);
    }

    public void showPendingRequests() {
        infoPane.setAppearance(Config.getInstance().isDark());
        infoPane.rmAll();

        pendingRequests.forEach((r) -> {
            infoPane.addElement(r.getReqMessage());
        });
        infoPane.setVisible(true);
    }

    public void showLoggedComm() {
        commPane.setAppearance(Config.getInstance().isDark());
        commPane.rmAll();

        //Requests
        CommunicationLogger.getInstance().getReqList().forEach((lr) -> {
            commPane.addRequest(lr);
        });

        //Responses
        CommunicationLogger.getInstance().getResList().forEach((lr) -> {
            commPane.addResponse(lr);
        });
        commPane.setVisible(true);
    }

    public void showMeasurementData() {
        MeasurementValuesPane valuesPane = new MeasurementValuesPane(this, false);
        valuesPane.setAppearance(Config.getInstance().isDark());
        valuesPane.setVisible(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jSlider = new javax.swing.JSlider();
        jPanChart = new javax.swing.JPanel();
        jPanStatus = new javax.swing.JPanel();
        jtfStatus = new javax.swing.JTextField();
        jpbStatus = new javax.swing.JProgressBar();
        jbutStartSim = new javax.swing.JButton();
        jPanTools = new javax.swing.JPanel();
        jLabelDevice = new javax.swing.JLabel();
        jcbSerialDevices = new javax.swing.JComboBox<>();
        jbutConnect = new javax.swing.JButton();
        jbutDisconnect = new javax.swing.JButton();
        jbutRefresh = new javax.swing.JButton();
        jMenuBar = new javax.swing.JMenuBar();
        jmenuFile = new javax.swing.JMenu();
        jmiOpen = new javax.swing.JMenuItem();
        jmiSave = new javax.swing.JMenuItem();
        jmiExport = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jmiPrint = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jmiSettings = new javax.swing.JMenuItem();
        jmiQuit = new javax.swing.JMenuItem();
        jmenuSimulation = new javax.swing.JMenu();
        jmiStartSim = new javax.swing.JMenuItem();
        jmenuStatus = new javax.swing.JMenu();
        jmiYellow = new javax.swing.JMenuItem();
        jmiRed = new javax.swing.JMenuItem();
        jmiYellowRed = new javax.swing.JMenuItem();
        jmiEnvironment = new javax.swing.JMenuItem();
        jmiEngineTemp = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        jmiRefresh = new javax.swing.JMenuItem();
        jmiConnect = new javax.swing.JMenuItem();
        jmiDisconnect = new javax.swing.JMenuItem();
        jmenuAppearance = new javax.swing.JMenu();
        jcbmiDarkMode = new javax.swing.JCheckBoxMenuItem();
        jmenuDeveloper = new javax.swing.JMenu();
        jcbmiDevMode = new javax.swing.JCheckBoxMenuItem();
        jcbmiDebugLogging = new javax.swing.JCheckBoxMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        jmiShowPendingRequests = new javax.swing.JMenuItem();
        jmiShowLoggedComm = new javax.swing.JMenuItem();
        jcbmiSaveLoggedComm = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        jmiShowMeasurementValues = new javax.swing.JMenuItem();
        jSeparator6 = new javax.swing.JPopupMenu.Separator();
        jmenuRequests = new javax.swing.JMenu();
        jmiInit = new javax.swing.JMenuItem();
        jmiVersion = new javax.swing.JMenuItem();
        jmiStart = new javax.swing.JMenuItem();
        jmiEngine = new javax.swing.JMenuItem();
        jmiKill = new javax.swing.JMenuItem();
        jmiMeasure = new javax.swing.JMenuItem();
        jmiMeasureno = new javax.swing.JMenuItem();
        jmiFine = new javax.swing.JMenuItem();
        jmiWarning = new javax.swing.JMenuItem();
        jmiSevere = new javax.swing.JMenuItem();
        jmiMaxProblems = new javax.swing.JMenuItem();
        jmenuAbout = new javax.swing.JMenu();
        jmiAbout = new javax.swing.JMenuItem();
        jmiHelp = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));

        jPanChart.setBackground(new java.awt.Color(255, 255, 255));
        jPanChart.setLayout(new java.awt.GridLayout(1, 0));
        getContentPane().add(jPanChart, java.awt.BorderLayout.CENTER);

        jPanStatus.setBackground(new java.awt.Color(255, 255, 255));
        jPanStatus.setLayout(new java.awt.GridBagLayout());

        jtfStatus.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanStatus.add(jtfStatus, gridBagConstraints);

        jpbStatus.setBackground(new java.awt.Color(255, 255, 255));
        jpbStatus.setBorder(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanStatus.add(jpbStatus, gridBagConstraints);

        jbutStartSim.setText("Start Simulation");
        jbutStartSim.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onButStartSim(evt);
            }
        });
        jPanStatus.add(jbutStartSim, new java.awt.GridBagConstraints());

        getContentPane().add(jPanStatus, java.awt.BorderLayout.PAGE_END);

        jPanTools.setBackground(new java.awt.Color(255, 255, 255));
        jPanTools.setLayout(new java.awt.GridBagLayout());

        jLabelDevice.setText("Gerät wählen: ");
        jLabelDevice.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jPanTools.add(jLabelDevice, new java.awt.GridBagConstraints());

        jcbSerialDevices.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanTools.add(jcbSerialDevices, gridBagConstraints);

        jbutConnect.setText("Verbinden");
        jbutConnect.setPreferredSize(new java.awt.Dimension(127, 29));
        jbutConnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onButConnect(evt);
            }
        });
        jPanTools.add(jbutConnect, new java.awt.GridBagConstraints());

        jbutDisconnect.setText("Trennen");
        jbutDisconnect.setPreferredSize(new java.awt.Dimension(127, 29));
        jbutDisconnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onButDisconnect(evt);
            }
        });
        jPanTools.add(jbutDisconnect, new java.awt.GridBagConstraints());

        jbutRefresh.setText("Aktualisieren");
        jbutRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onButRefresh(evt);
            }
        });
        jPanTools.add(jbutRefresh, new java.awt.GridBagConstraints());

        getContentPane().add(jPanTools, java.awt.BorderLayout.PAGE_START);

        jmenuFile.setText("Datei");

        jmiOpen.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.META_MASK));
        jmiOpen.setText("Öffnen");
        jmiOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onOpen(evt);
            }
        });
        jmenuFile.add(jmiOpen);

        jmiSave.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.META_MASK));
        jmiSave.setText("Speichern");
        jmiSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onSave(evt);
            }
        });
        jmenuFile.add(jmiSave);

        jmiExport.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.META_MASK));
        jmiExport.setText("Exportieren");
        jmiExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onExport(evt);
            }
        });
        jmenuFile.add(jmiExport);
        jmenuFile.add(jSeparator1);

        jmiPrint.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.META_MASK));
        jmiPrint.setText("Drucken");
        jmiPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onPrint(evt);
            }
        });
        jmenuFile.add(jmiPrint);
        jmenuFile.add(jSeparator2);

        jmiSettings.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_COMMA, java.awt.event.InputEvent.META_MASK));
        jmiSettings.setText("Einstellungen");
        jmiSettings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onSettings(evt);
            }
        });
        jmenuFile.add(jmiSettings);

        jmiQuit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.META_MASK));
        jmiQuit.setText("Beenden");
        jmiQuit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onQuit(evt);
            }
        });
        jmenuFile.add(jmiQuit);

        jMenuBar.add(jmenuFile);

        jmenuSimulation.setText("Simulation");

        jmiStartSim.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.META_MASK));
        jmiStartSim.setText("Start Simulation");
        jmiStartSim.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onStartSim(evt);
            }
        });
        jmenuSimulation.add(jmiStartSim);

        jmenuStatus.setText("Fehlercode - Status LED");

        jmiYellow.setText("Gelb - Warnung");
        jmiYellow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onYellow(evt);
            }
        });
        jmenuStatus.add(jmiYellow);

        jmiRed.setText("Rot - Fehler");
        jmiRed.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onRed(evt);
            }
        });
        jmenuStatus.add(jmiRed);

        jmiYellowRed.setText("Gelb & Rot - Schwerwiegender Fehler");
        jmiYellowRed.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onYellowRed(evt);
            }
        });
        jmenuStatus.add(jmiYellowRed);

        jmenuSimulation.add(jmenuStatus);

        jmiEnvironment.setText("Umweltdaten aktualisieren");
        jmiEnvironment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onEnvironment(evt);
            }
        });
        jmenuSimulation.add(jmiEnvironment);

        jmiEngineTemp.setText("Zweiradtemperaturen messen");
        jmiEngineTemp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onEngineTemp(evt);
            }
        });
        jmenuSimulation.add(jmiEngineTemp);
        jmenuSimulation.add(jSeparator3);

        jmiRefresh.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_U, java.awt.event.InputEvent.META_MASK));
        jmiRefresh.setText("Aktualisieren");
        jmiRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onRefresh(evt);
            }
        });
        jmenuSimulation.add(jmiRefresh);

        jmiConnect.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, java.awt.event.InputEvent.META_MASK));
        jmiConnect.setText("Verbinden");
        jmiConnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onConnect(evt);
            }
        });
        jmenuSimulation.add(jmiConnect);

        jmiDisconnect.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_BACK_SPACE, java.awt.event.InputEvent.META_MASK));
        jmiDisconnect.setText("Trennen");
        jmiDisconnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onDisconnect(evt);
            }
        });
        jmenuSimulation.add(jmiDisconnect);

        jMenuBar.add(jmenuSimulation);

        jmenuAppearance.setText("Darstellung");

        jcbmiDarkMode.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.META_MASK));
        jcbmiDarkMode.setSelected(true);
        jcbmiDarkMode.setText("Dark Mode");
        jcbmiDarkMode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onDark(evt);
            }
        });
        jmenuAppearance.add(jcbmiDarkMode);

        jMenuBar.add(jmenuAppearance);

        jmenuDeveloper.setText("Entwicklungstools");

        jcbmiDevMode.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.META_MASK));
        jcbmiDevMode.setSelected(true);
        jcbmiDevMode.setText("Entwicklungsmodus");
        jcbmiDevMode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleDevMode(evt);
            }
        });
        jmenuDeveloper.add(jcbmiDevMode);

        jcbmiDebugLogging.setText("Debug Logging");
        jcbmiDebugLogging.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleDebugLogging(evt);
            }
        });
        jmenuDeveloper.add(jcbmiDebugLogging);
        jmenuDeveloper.add(jSeparator5);

        jmiShowPendingRequests.setText("Unfertige Requests anzeigen");
        jmiShowPendingRequests.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onShowPendingRequests(evt);
            }
        });
        jmenuDeveloper.add(jmiShowPendingRequests);

        jmiShowLoggedComm.setText("Kommunikations-Protokoll einsehen");
        jmiShowLoggedComm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onShowLoggedComm(evt);
            }
        });
        jmenuDeveloper.add(jmiShowLoggedComm);

        jcbmiSaveLoggedComm.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.META_MASK));
        jcbmiSaveLoggedComm.setText("Kommunikations-Protokoll sichern");
        jcbmiSaveLoggedComm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onSaveLoggedComm(evt);
            }
        });
        jmenuDeveloper.add(jcbmiSaveLoggedComm);
        jmenuDeveloper.add(jSeparator4);

        jmiShowMeasurementValues.setText("Messdaten anzeigen");
        jmiShowMeasurementValues.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onShowMeasurementValues(evt);
            }
        });
        jmenuDeveloper.add(jmiShowMeasurementValues);
        jmenuDeveloper.add(jSeparator6);

        jmenuRequests.setText("Request senden");

        jmiInit.setText("INIT");
        jmiInit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onTestInit(evt);
            }
        });
        jmenuRequests.add(jmiInit);

        jmiVersion.setText("VERSION");
        jmiVersion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onTestVersion(evt);
            }
        });
        jmenuRequests.add(jmiVersion);

        jmiStart.setText("START");
        jmiStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onTestStart(evt);
            }
        });
        jmenuRequests.add(jmiStart);

        jmiEngine.setText("ENGINE");
        jmiEngine.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onTestEngine(evt);
            }
        });
        jmenuRequests.add(jmiEngine);

        jmiKill.setText("KILL");
        jmiKill.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onTestKill(evt);
            }
        });
        jmenuRequests.add(jmiKill);

        jmiMeasure.setText("MEASURE");
        jmiMeasure.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onTestMeasure(evt);
            }
        });
        jmenuRequests.add(jmiMeasure);

        jmiMeasureno.setText("MEASURENO");
        jmiMeasureno.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onTestMeasureNo(evt);
            }
        });
        jmenuRequests.add(jmiMeasureno);

        jmiFine.setText("FINE");
        jmiFine.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onTestFine(evt);
            }
        });
        jmenuRequests.add(jmiFine);

        jmiWarning.setText("WARNING");
        jmiWarning.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onTestWarning(evt);
            }
        });
        jmenuRequests.add(jmiWarning);

        jmiSevere.setText("SEVERE");
        jmiSevere.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onTestSevere(evt);
            }
        });
        jmenuRequests.add(jmiSevere);

        jmiMaxProblems.setText("MAXPROBLEMS");
        jmiMaxProblems.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onTestMaxProblems(evt);
            }
        });
        jmenuRequests.add(jmiMaxProblems);

        jmenuDeveloper.add(jmenuRequests);

        jMenuBar.add(jmenuDeveloper);

        jmenuAbout.setText("Über");

        jmiAbout.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_PERIOD, java.awt.event.InputEvent.META_MASK));
        jmiAbout.setText("Über");
        jmiAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onAbout(evt);
            }
        });
        jmenuAbout.add(jmiAbout);

        jmiHelp.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        jmiHelp.setText("Hilfe");
        jmiHelp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onHelp(evt);
            }
        });
        jmenuAbout.add(jmiHelp);

        jMenuBar.add(jmenuAbout);

        setJMenuBar(jMenuBar);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void onSave(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onSave
        try {
            //save();
            userLog("Datei erfolgreich gespeichert", LogLevel.FINE);
        } catch (Exception ex) {
            userLog(ex, "Fehler beim Speichern der Datei", LogLevel.WARNING);
        }
    }//GEN-LAST:event_onSave

    private void onPrint(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onPrint

    }//GEN-LAST:event_onPrint

    private void onSettings(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onSettings
        try {
            loadConfig();
        } catch (Exception ex) {
            LOG.warning(ex);
        }
        settings.setSwingValues(Config.getInstance());
        settings.setAppearance(Config.getInstance().isDark());
        if (port != null) {
            settings.writeDevice(port.getPortName());
        } else {
            settings.writeDevice("Kein Prüfstand verbunden...");
        }
        settings.setVisible(true);

        if (settings.isPressedOK()) {
            jcbmiDarkMode.setState(Config.getInstance().isDark());
            setAppearance(Config.getInstance().isDark());
            userLog("Einstellungen gespeichert", LogLevel.INFO);
        }
    }//GEN-LAST:event_onSettings

    private void onQuit(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onQuit

    }//GEN-LAST:event_onQuit

    private void onStartSim(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onStartSim
        if (connection) {
            vehicle.setAppearance(Config.getInstance().isDark());
            vehicle.setVisible(true);

            userLog("Start der Simulation", LogLevel.INFO);

            if (vehicle.isPressedOK()) {
                measure = new MeasureDialog(this, true);
                measure.setAppearance(Config.getInstance().isDark());
                measure.setVisible(true);

                if (measure.isFinished()) {
                    measurementFinished = true;

                    result.setAppearance(Config.getInstance().isDark());
                    result.setValues();
                    result.setVisible(true);

                    diagramSet.setAppearance(Config.getInstance().isDark());
                    diagramSet.refreshGui();
                    diagramSet.setVisible(true);
                }
            }
        } else {
            userLogPane("Fehler beim Starten: Es wurde noch keine Verbindung aufgebaut...", LogLevel.SEVERE);
        }
        refreshGui();
    }//GEN-LAST:event_onStartSim

    private void onRefresh(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onRefresh
        refreshPorts();
        userLog("Port-Liste aktualisiert", LogLevel.INFO);
    }//GEN-LAST:event_onRefresh

    private void onConnect(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onConnect
        try {
            MyConnectPortWorker w = new MyConnectPortWorker((String) jcbSerialDevices.getSelectedItem());
            w.execute();
            jtfStatus.setText("Port wird geöffnet");
            activeWorker = w;
            refreshGui();
            //userLog("Connected with " + jcbSerialDevices.getSelectedItem(), LogLevel.FINE);
        } catch (Throwable ex) {
            userLog(ex, "Fehler beim Verbinden", LogLevel.SEVERE);
        }
    }//GEN-LAST:event_onConnect

    private void onDisconnect(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onDisconnect
        MyDisconnectPortWorker w = new MyDisconnectPortWorker();
        w.execute();
        activeWorker = w;
        refreshGui();
        userLog("Gerät wird getrennt...", LogLevel.INFO);
    }//GEN-LAST:event_onDisconnect

    private void onAbout(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onAbout
        about.setAppearance(Config.getInstance().isDark());
        about.setOSIcon(os);
        about.setVisible(true);
        if (port != null) {
            about.writeDevice(port.getPortName());
        } else {
            about.writeDevice("Kein Prüfstand verbunden...");
        }
    }//GEN-LAST:event_onAbout

    private void onButConnect(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onButConnect
        onConnect(evt);
    }//GEN-LAST:event_onButConnect

    private void onButDisconnect(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onButDisconnect
        onDisconnect(evt);
    }//GEN-LAST:event_onButDisconnect

    private void onButRefresh(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onButRefresh
        onRefresh(evt);
    }//GEN-LAST:event_onButRefresh

    private void onHelp(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onHelp
        help.setAppearance(Config.getInstance().isDark());
        help.setVisible(true);
    }//GEN-LAST:event_onHelp

    private void onButStartSim(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onButStartSim
        onStartSim(evt);
    }//GEN-LAST:event_onButStartSim

    private void onDark(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onDark
        Config.getInstance().setDark(jcbmiDarkMode.getState());
        setAppearance(Config.getInstance().isDark());
        try {
            settings.saveConfig(Config.getInstance());
        } catch (Exception e) {
            userLog(e, "Fehler beim Speichern der Config-File", LogLevel.WARNING);
        }
    }//GEN-LAST:event_onDark

    private void onExport(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onExport

    }//GEN-LAST:event_onExport

    private void onOpen(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onOpen
        try {
            //open();
            userLog("Datei erfolgreich geöffnet", LogLevel.FINE);
        } catch (Exception ex) {
            userLog(ex, "Fehler beim Öffnen der Datei", LogLevel.WARNING);
        }
    }//GEN-LAST:event_onOpen

    private void onSaveLoggedComm(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onSaveLoggedComm
        try {
            saveComm();
        } catch (Exception ex) {
            userLog(ex, "Fehler beim Speichern des Kommunikationsprotokolls", LogLevel.WARNING);
        }
    }//GEN-LAST:event_onSaveLoggedComm

    private void toggleDevMode(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toggleDevMode
        devMode = false;
        if (jcbmiDevMode.getState()) {
            int answ = JOptionPane.showConfirmDialog(this, "Möchten Sie in den Entwicklungsmodus wechseln?", "Entwicklunsmodus", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (answ == JOptionPane.YES_OPTION) {
                LOG.info("Switched to Development-Mode");
                devMode = true;
                addLogFileHandler(devMode);
            } else if (answ == JOptionPane.NO_OPTION) {
                jcbmiDevMode.setState(false);
                devMode = false;
            }
        } else {
            LOG.info("Switched to User-Mode");
            jcbmiDebugLogging.setState(false);
            LOG.setDebugMode(false);
        }
        refreshGui();
    }//GEN-LAST:event_toggleDevMode

    private void onShowPendingRequests(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onShowPendingRequests
        showPendingRequests();
    }//GEN-LAST:event_onShowPendingRequests

    private void onShowLoggedComm(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onShowLoggedComm
        showLoggedComm();
    }//GEN-LAST:event_onShowLoggedComm

    private void onTestInit(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onTestInit
        LOG.info("Test Communication: INIT");
        addPendingRequest(telegram.init());
    }//GEN-LAST:event_onTestInit

    private void onTestStart(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onTestStart
        LOG.info("Test Communication: START");
        addPendingRequest(telegram.start());
    }//GEN-LAST:event_onTestStart

    private void onTestEngine(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onTestEngine
        LOG.info("Test Communication: ENGINE");
        addPendingRequest(telegram.engine());
    }//GEN-LAST:event_onTestEngine

    private void onTestMeasure(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onTestMeasure
        LOG.info("Test Communication: MEASURE");
        addPendingRequest(telegram.measure());
    }//GEN-LAST:event_onTestMeasure

    private void onTestMeasureNo(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onTestMeasureNo
        LOG.info("Test Communication: MEASURENO");
        addPendingRequest(telegram.measureno());
    }//GEN-LAST:event_onTestMeasureNo

    private void onTestFine(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onTestFine
        LOG.info("Test Communication: FINE");
        addPendingRequest(telegram.fine());
    }//GEN-LAST:event_onTestFine

    private void onTestWarning(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onTestWarning
        LOG.info("Test Communication: WARNING");
        addPendingRequest(telegram.warning());
    }//GEN-LAST:event_onTestWarning

    private void onTestSevere(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onTestSevere
        LOG.info("Test Communication: SEVERE");
        addPendingRequest(telegram.severe());
    }//GEN-LAST:event_onTestSevere

    private void onTestMaxProblems(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onTestMaxProblems
        LOG.info("Test Communication: MAXPROBLEMS");
        addPendingRequest(telegram.maxProblems());
    }//GEN-LAST:event_onTestMaxProblems

    private void onYellow(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onYellow
        LOG.warning("User checked LED: WARNING");
    }//GEN-LAST:event_onYellow

    private void onRed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onRed
        LOG.warning("User checked LED: SEVERE");
    }//GEN-LAST:event_onRed

    private void onYellowRed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onYellowRed
        LOG.warning("User checked LED: MAXPROBLEMS");
        JOptionPane.showMessageDialog(this,
                "Wenn beide Kontrollleuchten (Gelb & Rot) leuchten ist ein schwerwiegender Fehler am Gerät oder im Programm aufgetreten...\n\n"
                + "Starten Sie die Software neu und drücken Sie den Reset-Button am Gerät.\n\n"
                + "Wenn der Fehler erneut oder öfters auftritt, starten Sie das Programm im Entwicklungsmodus\n"
                + "und senden Sie nach erneuter Ausführung alle Dateien im Ordner 'Service_Files' an den Entwickler!",
                "Gelbe & Rote LED: Schwerwiegender Fehler",
                JOptionPane.WARNING_MESSAGE);
    }//GEN-LAST:event_onYellowRed

    private void onTestKill(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onTestKill
        LOG.info("Test Communication: KILL");
        addPendingRequest(telegram.kill());
    }//GEN-LAST:event_onTestKill

    private void toggleDebugLogging(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toggleDebugLogging
        LOG.setDebugMode(jcbmiDebugLogging.getState());
        if (jcbmiDebugLogging.getState()) {
            LOG.info("Enabled Debug Logging");
        } else {
            LOG.info("Disabled Debug Logging");
        }
    }//GEN-LAST:event_toggleDebugLogging

    private void onTestVersion(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onTestVersion
        LOG.info("Test Communication: VERSION");
        addPendingRequest(telegram.version());
    }//GEN-LAST:event_onTestVersion

    private void onShowMeasurementValues(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onShowMeasurementValues
        showMeasurementData();
    }//GEN-LAST:event_onShowMeasurementValues

    private void onEnvironment(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onEnvironment
        addPendingRequest(telegram.start());
        synchronized (syncObj) {
            try {
                syncObj.wait(1000);
            } catch (InterruptedException ex) {
                LOG.warning(ex);
            }
            JOptionPane.showMessageDialog(this, String.format("Umweltdaten gemessen:\n"
                    + "Temperatur: %.2f\n"
                    + "Luftdruck: %.2f\n"
                    + "Seehöhe: %.2f",
                    Environment.getInstance().getEnvTemp(), Environment.getInstance().getAirPress(), Environment.getInstance().getAltitude()),
                    "Umweltdaten aktualisiert", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_onEnvironment

    private void onEngineTemp(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onEngineTemp
        addPendingRequest(telegram.engine());
        synchronized (syncObj) {
            try {
                syncObj.wait(1000);
            } catch (Exception ex) {
                LOG.warning(ex);
            }
            JOptionPane.showMessageDialog(this, String.format("Temperaturen gemessen:\n"
                    + "Motortemperatur: %.2f\n"
                    + "Abgastemperatur: %.2f\n",
                    Environment.getInstance().getEngTemp(), Environment.getInstance().getFumeTemp()),
                    "Zweiradtemperaturen aktualisiert", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_onEngineTemp

    private class MyConnectPortWorker extends ConnectPortWorker {

        public MyConnectPortWorker(String port) {
            super(port);
        }

        @Override
        protected void done() {
            try {
                secondTry = true;
                port = (jssc.SerialPort) get(2, TimeUnit.SECONDS);
                telegram = new MyTelegram();
                telegram.setSerialPort(port);
                LOG.info("setPort: RxTxWorker");
                telegram.execute();
                userLog("Warten Sie bitte, bis das Gerät bereit ist...", LogLevel.INFO);
                addPendingRequest(telegram.init());
            } catch (Exception e) {
                userLog(e, "Gerät konnte nicht verbunden werden...", LogLevel.SEVERE);
            } finally {
                activeWorker = null;
                refreshGui();
            }
        }
    }

    private class MyDisconnectPortWorker extends DisconnectPortWorker {

        @Override
        protected void done() {
            try {
                connection = false;
                port.closePort();
                port = null;
            } catch (Throwable th) {
                userLog(th, "Fehler beim Trennen des Geräts!", LogLevel.WARNING);
            } finally {
                if (telegram != null) {
                    try {
                        telegram.setSerialPort(null);
                    } catch (SerialPortException ex) {
                        LOG.warning(ex);
                    }
                    telegram = null;
                }
            }
            try {
                userLog("Gerät erfolgreich getrennt!", LogLevel.FINE);
                activeWorker = null;
                refreshGui();
            } catch (Exception ex) {
                LOG.severe(ex);
            }

        }

        @Override
        protected void process(List<Throwable> chunks) {
            chunks.forEach((th) -> {
                userLog(th, "Fehler beim Trennen des Geräts!", LogLevel.SEVERE);
            });
        }

    }

    public class MyTelegram extends Telegram {

        @Override
        protected void done() {
        }

        @Override
        protected void process(List<Request> chunks) {
            for (Request r : chunks) {
                if (r.getStatus() == Status.DONE) {
                    LOG.debug("Request " + r.getReqName() + ": DONE");
                    synchronized (syncObj) {
                        syncObj.notifyAll();
                    }
                } else if (r.getStatus() == Status.ERROR) {
                    LOG.debug("Request: " + r.getReqName() + ": ERROR");
                } else {
                    continue;
                }

                if (r.getStatus() == Status.DONE || r.getStatus() == Status.ERROR) {
                    LOG.debug("Request " + r.getReqName() + " removed from pendingRequests");
                    removePendingRequest(r);
                }

                if (r.getVariety() == Variety.INIT) {
                    if (r.getStatus() == Status.DONE) {
                        addPendingRequest(telegram.version());
                    } else if (r.getStatus() == Status.ERROR) {
                        if (secondTry) {
                            LOG.warning("INIT: Second try was required...");
                            addPendingRequest(telegram.init());
                            secondTry = false;
                        } else {
                            userLogPane("Gerät hat möglicherweise einen Fehler oder ist defekt. Keine Gewährleistung der Korrektheit der Messdaten - Status-LEDs kontrollieren!", LogLevel.WARNING);
                            LOG.warning("INIT returns ERROR: " + r.getResponse());
                            addPendingRequest(telegram.warning());
                        }
                    }

                } else if (r.getVariety() == Variety.START) {
                    if (r.getStatus() == Status.DONE) {
                        userLog("Messung der Umweltdaten abgeschlossen.", LogLevel.FINE);
                    } else if (r.getStatus() == Status.ERROR) {
                        userLogPane("Umweltdaten möglicherweise fehlerhaft oder unvollständig...", LogLevel.WARNING);
                        LOG.warning("START returns ERROR: " + r.getResponse());
                    }

                } else if (r.getVariety() == Variety.ENGINE) {
                    if (r.getStatus() == Status.DONE) {
                        userLog("Messung der Motorradtemperaturen abgeschlossen", LogLevel.FINE);
                        addPendingRequest(telegram.fine());
                    } else if (r.getStatus() == Status.ERROR) {
                        userLog("Motorradtemperaturen fehlerhaft - Thermoelemente überprüfen!", LogLevel.WARNING);
                        LOG.warning("ENGINE returns ERROR: " + r.getResponse());
                        addPendingRequest(telegram.warning());
                    }

                } else if (r.getVariety() == Variety.MEASURE) {
                    if (r.getStatus() == Status.ERROR) {
                        LOG.warning("MEASURE returns ERROR: " + r.getResponse());
                    }

                } else if (r.getVariety() == Variety.MEASURENO) {
                    if (r.getStatus() == Status.ERROR) {
                        LOG.warning("MEASURENO returns ERROR: " + r.getResponse());
                    }

                } else if (r.getVariety() == Variety.KILL) {
                    if (r.getStatus() == Status.DONE) {
                        if (Bike.getInstance().isMeasRpm()) {
                            addPendingRequest(telegram.measure());
                            LOG.info("KILL sent MEASURE");
                        } else {
                            addPendingRequest(telegram.measureno());
                            LOG.info("KILL sent MEASURENO");
                        }
                    }
                } else if (r.getVariety() == Variety.VERSION) {
                    if (r.getStatus() == Status.DONE) {
                        userLog("Gerät ist einsatzbereit!", LogLevel.FINE);
                        LOG.info("Arduino-Version: " + Config.getInstance().getArduinoVersion());
                        connection = true;
                        addPendingRequest(telegram.fine());
                    } else if (r.getStatus() == Status.ERROR) {
                        if (Config.getInstance().getArduinoVersion() >= reqArduVers) {
                            LOG.warning("VERSION returns ERROR: " + r.getResponse());
                        } else {
                            userLogPane("Die Firmware am Prüfstand ist veraltet, bitte laden Sie eine neue Version (mind. Version " + reqArduVers + ")!", LogLevel.SEVERE);
                            LOG.warning("VERSION returns ERROR: " + r.getResponse());
                        }
                    }
                }
            }
        }
    }

    private class LineChart {

        private final Diagram diagram = Diagram.getInstance();

        private final Database data = Database.getInstance();
        private final Bike bike = Bike.getInstance();
        private final Config config = Config.getInstance();

        private final Font font = new Font("sansserif", Font.BOLD, 15);

        private final ValueMarker maxPowerMarker = new ValueMarker(data.getBikePower());
        private final ValueMarker maxTorqueMarker = new ValueMarker(data.getBikeTorque());

        private XYSeries seriesTorque = new XYSeries("Drehmoment");
        private XYSeries seriesPower = new XYSeries("Leistung");
        private XYSeries series1 = new XYSeries("tempLeistung series");
        private XYSeries series2 = new XYSeries("tempDrehmoment series");
        private XYSeriesCollection dataset1 = new XYSeriesCollection();
        private XYSeriesCollection dataset2 = new XYSeriesCollection();

        public void initChart() {
            JFreeChart chart = org.jfree.chart.ChartFactory.createXYLineChart(bike.getVehicleName(),
                    "Motordrehzahl [U/min]",
                    "Leistung [" + config.getPowerUnit() + "]",
                    dataset1,
                    PlotOrientation.VERTICAL,
                    true,
                    true,
                    false);

            ValueAxis torqueAxis = new NumberAxis("Drehmoment [Nm]");
            torqueAxis.setLabelFont(chart.getXYPlot().getDomainAxis().getLabelFont());

            seriesPower.setKey("Leistung");

            chart.getXYPlot().setDataset(1, dataset2);
            chart.getXYPlot().setRangeAxis(1, torqueAxis);
            chart.getXYPlot().mapDatasetToRangeAxis(0, 0);//1st dataset to 1st y-axis
            chart.getXYPlot().mapDatasetToRangeAxis(1, 1); //2nd dataset to 2nd y-axis

            // Hinzufuegen von series zu der Datenmenge dataset
            dataset1.addSeries(seriesPower);
            dataset2.addSeries(seriesTorque);

            maxPowerMarker.setPaint(Color.darkGray);
            maxPowerMarker.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                    1.0f, new float[]{
                        6.0f, 7.0f
                    }, 0.0f));

            maxPowerMarker.setLabelTextAnchor(TextAnchor.BASELINE_LEFT);
            maxPowerMarker.setLabelFont(font);

            maxTorqueMarker.setPaint(Color.darkGray);
            maxTorqueMarker.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                    1.0f, new float[]{
                        6.0f, 7.0f
                    }, 0.0f));

            maxTorqueMarker.setLabelTextAnchor(TextAnchor.TOP_LEFT);
            maxTorqueMarker.setLabelFont(font);

            XYLineAndShapeRenderer r1 = new XYLineAndShapeRenderer();
            r1.setSeriesPaint(0, Color.blue);
            r1.setSeriesShapesVisible(0, false);
            r1.setSeriesStroke(0, new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            XYLineAndShapeRenderer r2 = new XYLineAndShapeRenderer();
            r2.setSeriesPaint(0, Color.red);
            r2.setSeriesShapesVisible(0, false);
            r2.setSeriesStroke(0, new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            chart.getXYPlot().setRenderer(0, r1);
            chart.getXYPlot().setRenderer(1, r2);

            chart.getXYPlot().addRangeMarker(0, maxPowerMarker, Layer.BACKGROUND);
            chart.getXYPlot().addRangeMarker(1, maxTorqueMarker, Layer.BACKGROUND);

            ChartPanel chartPanel = new ChartPanel(chart);

            TextTitle eco = new TextTitle("Temperatur: NaN "
                    + "Luftdruck: NaN "
                    + "Seehöhe: NaN");
            TextTitle eng = new TextTitle("Motortemperatur : NaN "
                    + "Abgastemperatur: NaN");
            TextTitle val = new TextTitle("Pmax: NaN "
                    + "Mmax: NaN "
                    + "Vmax: NaN");

            chart.addSubtitle(eco);
            chart.addSubtitle(eng);
            chart.addSubtitle(val);

            jPanChart.add(chartPanel);

            chart.fireChartChanged();
        }
    }

    /**
     * @param args the command line arguments
     * @throws javax.swing.UnsupportedLookAndFeelException
     */
    public static void main(String args[]) throws UnsupportedLookAndFeelException {
        LOGP.addHandler(new LogBackgroundHandler(new LogOutputStreamHandler(System.out)));
        LOG.info("Start of BESDyno");

        try {
            Config.createInstance(new FileInputStream(getConfigFile()));
        } catch (Exception ex) {
            LOG.warning(ex);
        }

        //Menu-Bar support for macOS
        if (System.getProperty("os.name").contains("Mac OS X")) {
            os = OS.MACOS;
            try {
                System.setProperty("apple.awt.fileDialogForDirectories", "true");
                System.setProperty("apple.laf.useScreenMenuBar", "true");
                System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Zweiradprüfstand");
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
                java.util.logging.Logger.getLogger(BESDyno.class
                        .getName()).log(java.util.logging.Level.SEVERE, null, ex);
                //LOG.severe(ex);
            }
            javax.swing.SwingUtilities.invokeLater(() -> {
                BESDyno besDyno = BESDyno.getInstance();
                besDyno.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        saveCommAuto();
                        LOG.info("End of BESDyno");
                    }
                });
                besDyno.setVisible(true);
            });
            //Other OS
        } else if (System.getProperty("os.name").startsWith("Windows ")) {
            os = OS.WINDOWS;
            try {
                for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                    if ("com.sun.java.swing.plaf.windows.WindowsLookAndFeel".equals(info.getName())) {
                        javax.swing.UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
                java.util.logging.Logger.getLogger(BESDyno.class
                        .getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }

            java.awt.EventQueue.invokeLater(() -> {
                BESDyno besDyno = BESDyno.getInstance();
                besDyno.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        saveCommAuto();
                        LOG.info("End of BESDyno");
                    }
                });
                besDyno.setVisible(true);
            });
        } else if (System.getProperty("os.name").startsWith("Linux")) {
            os = OS.LINUX;
            try {
                for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        javax.swing.UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
                java.util.logging.Logger.getLogger(BESDyno.class
                        .getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }

            java.awt.EventQueue.invokeLater(() -> {
                BESDyno besDyno = BESDyno.getInstance();
                besDyno.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        saveCommAuto();
                        LOG.info("End of BESDyno");
                    }
                });
                besDyno.setVisible(true);
            });
        } else {
            os = OS.OTHER;
            try {
                for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        javax.swing.UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
                java.util.logging.Logger.getLogger(BESDyno.class
                        .getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }

            java.awt.EventQueue.invokeLater(() -> {
                BESDyno besDyno = BESDyno.getInstance();
                besDyno.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        saveCommAuto();
                        LOG.info("End of BESDyno");
                    }
                });
                besDyno.setVisible(true);
            });
        }
    }

    static {
        //System.setProperty("logging.Logger.printStackTrace", "");
        System.setProperty("logging.LogOutputStreamHandler.showRecordHashcode", "false");
        //System.setProperty("logging.Logger.printAll", "");
        //System.setProperty("logging.LogRecordDataFormattedText.Terminal","NETBEANS");
        System.setProperty("logging.LogRecordDataFormattedText.Terminal", "LINUX");
        System.setProperty("logging.Logger.Level", "SEVERE");
        System.setProperty("logging.Logger.Level", "WARNING");
        System.setProperty("logging.Logger.Level", "INFO");
        System.setProperty("logging.Logger.Level", "FINE");
        System.setProperty("logging.Logger.Level", "FINER");
        System.setProperty("logging.Logger.Level", "FINEST");
        //System.setProperty("Test1.Logger.Level", "ALL");
        System.setProperty("test.Test.Logger.Level", "FINER");
        System.setProperty("test.*.Logger.Level", "FINE");
        //System.setProperty("test.*.Logger.Handlers", "test.MyHandler");
        //System.setProperty("test.*.Logger.Filter", "test.MyFilter");
        //System.setProperty("logging.LogOutputStreamHandler.colorize", "false");

        LOG = Logger.getLogger(BESDyno.class.getName());
        LOGP = Logger.getParentLogger();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabelDevice;
    private javax.swing.JMenuBar jMenuBar;
    private javax.swing.JPanel jPanChart;
    private javax.swing.JPanel jPanStatus;
    private javax.swing.JPanel jPanTools;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JPopupMenu.Separator jSeparator6;
    private javax.swing.JSlider jSlider;
    private javax.swing.JButton jbutConnect;
    private javax.swing.JButton jbutDisconnect;
    private javax.swing.JButton jbutRefresh;
    private javax.swing.JButton jbutStartSim;
    private javax.swing.JComboBox<String> jcbSerialDevices;
    private javax.swing.JCheckBoxMenuItem jcbmiDarkMode;
    private javax.swing.JCheckBoxMenuItem jcbmiDebugLogging;
    private javax.swing.JCheckBoxMenuItem jcbmiDevMode;
    private javax.swing.JMenuItem jcbmiSaveLoggedComm;
    private javax.swing.JMenu jmenuAbout;
    private javax.swing.JMenu jmenuAppearance;
    private javax.swing.JMenu jmenuDeveloper;
    private javax.swing.JMenu jmenuFile;
    private javax.swing.JMenu jmenuRequests;
    private javax.swing.JMenu jmenuSimulation;
    private javax.swing.JMenu jmenuStatus;
    private javax.swing.JMenuItem jmiAbout;
    private javax.swing.JMenuItem jmiConnect;
    private javax.swing.JMenuItem jmiDisconnect;
    private javax.swing.JMenuItem jmiEngine;
    private javax.swing.JMenuItem jmiEngineTemp;
    private javax.swing.JMenuItem jmiEnvironment;
    private javax.swing.JMenuItem jmiExport;
    private javax.swing.JMenuItem jmiFine;
    private javax.swing.JMenuItem jmiHelp;
    private javax.swing.JMenuItem jmiInit;
    private javax.swing.JMenuItem jmiKill;
    private javax.swing.JMenuItem jmiMaxProblems;
    private javax.swing.JMenuItem jmiMeasure;
    private javax.swing.JMenuItem jmiMeasureno;
    private javax.swing.JMenuItem jmiOpen;
    private javax.swing.JMenuItem jmiPrint;
    private javax.swing.JMenuItem jmiQuit;
    private javax.swing.JMenuItem jmiRed;
    private javax.swing.JMenuItem jmiRefresh;
    private javax.swing.JMenuItem jmiSave;
    private javax.swing.JMenuItem jmiSettings;
    private javax.swing.JMenuItem jmiSevere;
    private javax.swing.JMenuItem jmiShowLoggedComm;
    private javax.swing.JMenuItem jmiShowMeasurementValues;
    private javax.swing.JMenuItem jmiShowPendingRequests;
    private javax.swing.JMenuItem jmiStart;
    private javax.swing.JMenuItem jmiStartSim;
    private javax.swing.JMenuItem jmiVersion;
    private javax.swing.JMenuItem jmiWarning;
    private javax.swing.JMenuItem jmiYellow;
    private javax.swing.JMenuItem jmiYellowRed;
    private javax.swing.JProgressBar jpbStatus;
    private javax.swing.JTextField jtfStatus;
    // End of variables declaration//GEN-END:variables
}
