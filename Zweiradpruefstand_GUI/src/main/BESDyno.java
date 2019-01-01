package main;

import data.Bike;
import data.Config;
import development.CommunicationLogger;
import development.gui.DevInfoPane;
import development.gui.LoggedCommPane;
import gui.AboutDialog;
import gui.HelpDialog;
import gui.MeasureDialog;
import gui.SettingsDialog;
import gui.VehicleSetDialog;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
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
import serial.ConnectPortWorker;
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
    private MeasureDialog measure = new MeasureDialog(this, true);
    private SettingsDialog settings = new SettingsDialog(this, true);
    private DevInfoPane infoPane = new DevInfoPane(this, false);
    private LoggedCommPane commPane = new LoggedCommPane(this, false);

    //Object-Variables
    private File file;
    private SwingWorker activeWorker;
    private MyTelegram telegram;
    private jssc.SerialPort port;

    //Variables
    private static boolean devMode = true;
    private boolean secondTry = true;

    //Communication
    public final List<Request> pendingRequests = new LinkedList<>();

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

        jmiDevMode.setState(true);
        devMode = jmiDevMode.getState();
        LOG.setDevMode(devMode);

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
    }

    private void refreshGui() {
        jmiSave.setEnabled(false);
        jmiPrint.setEnabled(false);
        jmiStartSim.setEnabled(false);
        jbutStartSim.setEnabled(true);
        jmiConnect.setEnabled(false);
        jbutConnect.setEnabled(false);
        jmiDisconnect.setEnabled(false);
        jbutDisconnect.setEnabled(false);
        jcbSerialDevices.setEnabled(false);
        jcbmiDarkMode.setState(false);
        jmiRefresh.setEnabled(false);
        jbutRefresh.setEnabled(false);

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
            jmiStartSim.setEnabled(true);
            jbutStartSim.setEnabled(true);
        }
    }

    //Status-Textfeld: Logging for User
    public enum LogLevel {
        FINEST, FINE, INFO, WARNING, SEVERE
    };

    public void userLog(String msg, LogLevel level) {
        jtfStatus.setText(msg);
        switch (level) {
            case FINEST:
                LOG.finest(msg);
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
    private void save() throws IOException, Exception {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Bike-Datei (*.bes)", "bes"));

        File home;
        File folder;

        try {
            home = new File(System.getProperty("user.home"));
        } catch (Exception e) {
            home = null;
        }

        if (home != null && home.exists()) {
            folder = new File(home + File.separator + "Bike-Files");
            if (!folder.exists()) {
                if (!folder.mkdir()) {
                    throw new Exception("Internal Error");
                }
            }
            file = new File(folder + File.separator + Bike.getInstance().getVehicleName() + ".bes");
        } else {
            file = new File(Bike.getInstance().getVehicleName() + ".bes");
        }
        chooser.setSelectedFile(file);

        int rv = chooser.showSaveDialog(this);
        if (rv == JFileChooser.APPROVE_OPTION) {
            file = chooser.getSelectedFile();
            if (!file.getName().contains(".bes")) {
                userLogPane("Dies ist keine BES-Datei", LogLevel.WARNING);
            }

            try (BufferedWriter w = new BufferedWriter(new FileWriter(file))) {
                Bike.getInstance().writeFile(w);
            } catch (Exception ex) {
                LOG.severe(ex);
            }
        }

    }

    // Saves the Communication Log
    public void saveComm() throws Exception {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Text-Datei (*.txt)", "txt"));

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
            comfile = new File(folder + File.separator + "CommLog_" + df.format(date) + ".txt");
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

    private static void saveCommAuto() {
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
                    comfile = new File(folder + File.separator + "CommLog_" + df.format(date) + ".txt");
                    LOG.info(df.format(date));
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

    private void open() throws FileNotFoundException, IOException, Exception {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Bike-Datei (*.bes)", "bes"));

        File home;
        File folder;

        try {
            home = new File(System.getProperty("user.home"));
        } catch (Exception e) {
            home = null;
        }

        if (home != null && home.exists()) {
            folder = new File(home + File.separator + "Bike-Files");
            if (!folder.exists()) {
                if (!folder.mkdir()) {
                    LOG.severe("Internal Error");
                }
            }
            file = new File(folder + File.separator + Bike.getInstance().getVehicleName() + ".bes");
        } else {
            file = new File(Bike.getInstance().getVehicleName() + ".bes");
        }
        chooser.setSelectedFile(file);

        int rv = chooser.showOpenDialog(this);
        if (rv == JFileChooser.APPROVE_OPTION) {
            file = chooser.getSelectedFile();
            if (!file.getName().contains(".bes")) {
                userLogPane("Dies ist keine BES-Datei", LogLevel.WARNING);
            }
            try (BufferedReader r = new BufferedReader(new FileReader(file))) {
                Bike.getInstance().readFile(r);
            } catch (Exception ex) {
                userLog(ex, "Fehler beim Einlesen der Datei", LogLevel.WARNING);
            }
        }
    }

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

    //Communication
    public void addPendingRequest(Request request) {
        try {
            LOG.debug(request.getReqMessage() + " added to pedningRequests");
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
        commPane.addRequest("REQUESTS:");
        CommunicationLogger.getInstance().getReqList().forEach((s) -> {
            commPane.addRequest(s);
        });

        //Responses
        commPane.addResponse("RESPONSES:");
        CommunicationLogger.getInstance().getResList().forEach((s) -> {
            commPane.addResponse(s);
        });
        commPane.setVisible(true);
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
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        jmiRefresh = new javax.swing.JMenuItem();
        jmiConnect = new javax.swing.JMenuItem();
        jmiDisconnect = new javax.swing.JMenuItem();
        jmenuAppearance = new javax.swing.JMenu();
        jcbmiDarkMode = new javax.swing.JCheckBoxMenuItem();
        jmenuDeveloper = new javax.swing.JMenu();
        jmiDevMode = new javax.swing.JCheckBoxMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        jmiShowPendingRequests = new javax.swing.JMenuItem();
        jmiShowLoggedComm = new javax.swing.JMenuItem();
        jcbmiSaveLoggedComm = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        jmenuRequests = new javax.swing.JMenu();
        jmiInit = new javax.swing.JMenuItem();
        jmiStart = new javax.swing.JMenuItem();
        jmiEngine = new javax.swing.JMenuItem();
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

        javax.swing.GroupLayout jPanChartLayout = new javax.swing.GroupLayout(jPanChart);
        jPanChart.setLayout(jPanChartLayout);
        jPanChartLayout.setHorizontalGroup(
            jPanChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 611, Short.MAX_VALUE)
        );
        jPanChartLayout.setVerticalGroup(
            jPanChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 281, Short.MAX_VALUE)
        );

        getContentPane().add(jPanChart, java.awt.BorderLayout.CENTER);

        jPanStatus.setBackground(new java.awt.Color(255, 255, 255));
        jPanStatus.setLayout(new java.awt.GridBagLayout());
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
                jbutStartSimActionPerformed(evt);
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
                jbutConnectActionPerformed(evt);
            }
        });
        jPanTools.add(jbutConnect, new java.awt.GridBagConstraints());

        jbutDisconnect.setText("Trennen");
        jbutDisconnect.setPreferredSize(new java.awt.Dimension(127, 29));
        jbutDisconnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbutDisconnectActionPerformed(evt);
            }
        });
        jPanTools.add(jbutDisconnect, new java.awt.GridBagConstraints());

        jbutRefresh.setText("Aktualisieren");
        jbutRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbutRefreshActionPerformed(evt);
            }
        });
        jPanTools.add(jbutRefresh, new java.awt.GridBagConstraints());

        getContentPane().add(jPanTools, java.awt.BorderLayout.PAGE_START);

        jmenuFile.setText("Datei");

        jmiOpen.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.META_MASK));
        jmiOpen.setText("Öffnen");
        jmiOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmiOpenActionPerformed(evt);
            }
        });
        jmenuFile.add(jmiOpen);

        jmiSave.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.META_MASK));
        jmiSave.setText("Speichern");
        jmiSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmiSaveActionPerformed(evt);
            }
        });
        jmenuFile.add(jmiSave);

        jmiExport.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.META_MASK));
        jmiExport.setText("Exportieren");
        jmiExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmiExportActionPerformed(evt);
            }
        });
        jmenuFile.add(jmiExport);
        jmenuFile.add(jSeparator1);

        jmiPrint.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.META_MASK));
        jmiPrint.setText("Drucken");
        jmiPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmiPrintActionPerformed(evt);
            }
        });
        jmenuFile.add(jmiPrint);
        jmenuFile.add(jSeparator2);

        jmiSettings.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_COMMA, java.awt.event.InputEvent.META_MASK));
        jmiSettings.setText("Einstellungen");
        jmiSettings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmiSettingsActionPerformed(evt);
            }
        });
        jmenuFile.add(jmiSettings);

        jmiQuit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.META_MASK));
        jmiQuit.setText("Beenden");
        jmiQuit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmiQuitActionPerformed(evt);
            }
        });
        jmenuFile.add(jmiQuit);

        jMenuBar.add(jmenuFile);

        jmenuSimulation.setText("Simulation");

        jmiStartSim.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.META_MASK));
        jmiStartSim.setText("Start Simulation");
        jmiStartSim.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmiStartSimActionPerformed(evt);
            }
        });
        jmenuSimulation.add(jmiStartSim);

        jmenuStatus.setText("Fehlercode - Status LED");

        jmiYellow.setText("Gelb - Warnung");
        jmiYellow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmiYellowActionPerformed(evt);
            }
        });
        jmenuStatus.add(jmiYellow);

        jmiRed.setText("Rot - Fehler");
        jmiRed.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmiRedActionPerformed(evt);
            }
        });
        jmenuStatus.add(jmiRed);

        jmiYellowRed.setText("Gelb & Rot - Schwerwiegender Fehler");
        jmiYellowRed.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmiYellowRedActionPerformed(evt);
            }
        });
        jmenuStatus.add(jmiYellowRed);

        jmenuSimulation.add(jmenuStatus);
        jmenuSimulation.add(jSeparator3);

        jmiRefresh.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_U, java.awt.event.InputEvent.META_MASK));
        jmiRefresh.setText("Aktualisieren");
        jmiRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmiRefreshActionPerformed(evt);
            }
        });
        jmenuSimulation.add(jmiRefresh);

        jmiConnect.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, java.awt.event.InputEvent.META_MASK));
        jmiConnect.setText("Verbinden");
        jmiConnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmiConnectActionPerformed(evt);
            }
        });
        jmenuSimulation.add(jmiConnect);

        jmiDisconnect.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_BACK_SPACE, java.awt.event.InputEvent.META_MASK));
        jmiDisconnect.setText("Trennen");
        jmiDisconnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmiDisconnectActionPerformed(evt);
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
                jcbmiDarkModeActionPerformed(evt);
            }
        });
        jmenuAppearance.add(jcbmiDarkMode);

        jMenuBar.add(jmenuAppearance);

        jmenuDeveloper.setText("Entwicklungstools");

        jmiDevMode.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.META_MASK));
        jmiDevMode.setSelected(true);
        jmiDevMode.setText("Entwicklungsmodus");
        jmiDevMode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmiDevModeActionPerformed(evt);
            }
        });
        jmenuDeveloper.add(jmiDevMode);
        jmenuDeveloper.add(jSeparator5);

        jmiShowPendingRequests.setText("Unfertige Requests anzeigen");
        jmiShowPendingRequests.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmiShowPendingRequestsActionPerformed(evt);
            }
        });
        jmenuDeveloper.add(jmiShowPendingRequests);

        jmiShowLoggedComm.setText("Kommunikations-Protokoll einsehen");
        jmiShowLoggedComm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmiShowLoggedCommActionPerformed(evt);
            }
        });
        jmenuDeveloper.add(jmiShowLoggedComm);

        jcbmiSaveLoggedComm.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.META_MASK));
        jcbmiSaveLoggedComm.setText("Kommunikations-Protokoll sichern");
        jcbmiSaveLoggedComm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbmiSaveLoggedCommActionPerformed(evt);
            }
        });
        jmenuDeveloper.add(jcbmiSaveLoggedComm);
        jmenuDeveloper.add(jSeparator4);

        jmenuRequests.setText("Request senden");

        jmiInit.setText("INIT");
        jmiInit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmiInitActionPerformed(evt);
            }
        });
        jmenuRequests.add(jmiInit);

        jmiStart.setText("START");
        jmiStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmiStartActionPerformed(evt);
            }
        });
        jmenuRequests.add(jmiStart);

        jmiEngine.setText("ENGINE");
        jmiEngine.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmiEngineActionPerformed(evt);
            }
        });
        jmenuRequests.add(jmiEngine);

        jmiMeasure.setText("MEASURE");
        jmiMeasure.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmiMeasureActionPerformed(evt);
            }
        });
        jmenuRequests.add(jmiMeasure);

        jmiMeasureno.setText("MEASURENO");
        jmiMeasureno.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmiMeasurenoActionPerformed(evt);
            }
        });
        jmenuRequests.add(jmiMeasureno);

        jmiFine.setText("FINE");
        jmiFine.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmiFineActionPerformed(evt);
            }
        });
        jmenuRequests.add(jmiFine);

        jmiWarning.setText("WARNING");
        jmiWarning.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmiWarningActionPerformed(evt);
            }
        });
        jmenuRequests.add(jmiWarning);

        jmiSevere.setText("SEVERE");
        jmiSevere.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmiSevereActionPerformed(evt);
            }
        });
        jmenuRequests.add(jmiSevere);

        jmiMaxProblems.setText("MAXPROBLEMS");
        jmiMaxProblems.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmiMaxProblemsActionPerformed(evt);
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
                jmiAboutActionPerformed(evt);
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

    private void jmiSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiSaveActionPerformed
        try {
            save();
            userLog("Datei erfolgreich gespeichert", LogLevel.FINE);
        } catch (Exception ex) {
            userLog(ex, "Fehler beim Speichern der Datei", LogLevel.WARNING);
        }
    }//GEN-LAST:event_jmiSaveActionPerformed

    private void jmiPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiPrintActionPerformed

    }//GEN-LAST:event_jmiPrintActionPerformed

    private void jmiSettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiSettingsActionPerformed
        try {
            loadConfig();
        } catch (Exception ex) {
            LOG.warning(ex);
        }
        settings.setSwingValues(Config.getInstance());
        settings.setAppearance(Config.getInstance().isDark());
        settings.setVisible(true);

        if (settings.isPressedOK()) {
            jcbmiDarkMode.setState(Config.getInstance().isDark());
            setAppearance(Config.getInstance().isDark());
            userLog("Einstellungen gespeichert", LogLevel.INFO);
        }
    }//GEN-LAST:event_jmiSettingsActionPerformed

    private void jmiQuitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiQuitActionPerformed

    }//GEN-LAST:event_jmiQuitActionPerformed

    private void jmiStartSimActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiStartSimActionPerformed
        vehicle.setAppearance(Config.getInstance().isDark());
        vehicle.setVisible(true);

        userLog("Start der Simulation", LogLevel.INFO);

        if (vehicle.isPressedOK()) {
            measure.setAppearance(Config.getInstance().isDark());
            measure.setVisible(true);
        }
    }//GEN-LAST:event_jmiStartSimActionPerformed

    private void jmiRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiRefreshActionPerformed
        refreshPorts();
        userLog("Port-Liste aktualisiert", LogLevel.INFO);
    }//GEN-LAST:event_jmiRefreshActionPerformed

    private void jmiConnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiConnectActionPerformed
        try {
            MyConnectPortWorker w = new MyConnectPortWorker((String) jcbSerialDevices.getSelectedItem());
            w.execute();
            jtfStatus.setText("Port erfolgreich geöffnet");
            activeWorker = w;
            refreshGui();
            userLog("Connected with " + jcbSerialDevices.getSelectedItem(), LogLevel.FINE);
        } catch (Throwable ex) {
            userLog(ex, "Fehler beim Verbinden", LogLevel.SEVERE);
        }
    }//GEN-LAST:event_jmiConnectActionPerformed

    private void jmiDisconnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiDisconnectActionPerformed
        try {
            telegram.setSerialPort(null);
            if (!telegram.cancel(true)) {
                LOG.warning("Fehler beim Beenden: SwingWorker -> RxTxWorker -> TelegramWorker -> MyTelegramWorker...");
                return;
            }
        } catch (Exception ex) {
            userLog(ex, "Fehler beim Schließen des Ports", LogLevel.WARNING);
        } finally {
            try {
                port.closePort();
                port = null;
            } catch (Throwable th) {
                userLog(th, "Fehler beim Schließen des Ports", LogLevel.WARNING);
            }
            userLog("Port geschlossen", LogLevel.INFO);
            try {
                telegram.setSerialPort(null);
            } catch (SerialPortException ex) {
                userLog(ex, "Interner Fehler: telegram.setSerialPort();", LogLevel.WARNING);
            }
            refreshGui();
        }
    }//GEN-LAST:event_jmiDisconnectActionPerformed

    private void jmiAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiAboutActionPerformed
        about.setAppearance(Config.getInstance().isDark());
        about.setVisible(true);
        if (port != null) {
            about.writeDevice(port.getPortName());
        } else {
            about.writeDevice("Kein Prüfstand verbunden...");
        }
    }//GEN-LAST:event_jmiAboutActionPerformed

    private void jbutConnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbutConnectActionPerformed
        jmiConnectActionPerformed(evt);
    }//GEN-LAST:event_jbutConnectActionPerformed

    private void jbutDisconnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbutDisconnectActionPerformed
        jmiDisconnectActionPerformed(evt);
    }//GEN-LAST:event_jbutDisconnectActionPerformed

    private void jbutRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbutRefreshActionPerformed
        refreshPorts();
        userLog("Port-Liste aktualisiert", LogLevel.INFO);
    }//GEN-LAST:event_jbutRefreshActionPerformed

    private void onHelp(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onHelp
        help.setAppearance(Config.getInstance().isDark());
        help.setVisible(true);
    }//GEN-LAST:event_onHelp

    private void jbutStartSimActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbutStartSimActionPerformed
        vehicle.setAppearance(Config.getInstance().isDark());
        vehicle.setVisible(true);

        LOG.info("Simulation started");

        if (vehicle.isPressedOK()) {
            measure.setAppearance(Config.getInstance().isDark());
            measure.setVisible(true);
        }
    }//GEN-LAST:event_jbutStartSimActionPerformed

    private void jcbmiDarkModeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbmiDarkModeActionPerformed
        Config.getInstance().setDark(jcbmiDarkMode.getState());
        setAppearance(Config.getInstance().isDark());
        try {
            settings.saveConfig(Config.getInstance());
        } catch (Exception e) {
            userLog(e, "Fehler beim Speichern der Config-File", LogLevel.WARNING);
        }
    }//GEN-LAST:event_jcbmiDarkModeActionPerformed

    private void jmiExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiExportActionPerformed

    }//GEN-LAST:event_jmiExportActionPerformed

    private void jmiOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiOpenActionPerformed
        try {
            open();
            userLog("Datei erfolgreich geöffnet", LogLevel.FINE);
        } catch (Exception ex) {
            userLog(ex, "Fehler beim Öffnen der Datei", LogLevel.WARNING);
        }
    }//GEN-LAST:event_jmiOpenActionPerformed

    private void jcbmiSaveLoggedCommActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbmiSaveLoggedCommActionPerformed
        try {
            saveComm();
        } catch (Exception ex) {
            userLog(ex, "Fehler beim Speichern des Kommunikationsprotokolls", LogLevel.WARNING);
        }
    }//GEN-LAST:event_jcbmiSaveLoggedCommActionPerformed

    private void jmiDevModeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiDevModeActionPerformed
        devMode = jmiDevMode.getState();
        LOG.setDevMode(devMode);
        addLogFileHandler(devMode);
    }//GEN-LAST:event_jmiDevModeActionPerformed

    private void jmiShowPendingRequestsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiShowPendingRequestsActionPerformed
        showPendingRequests();
    }//GEN-LAST:event_jmiShowPendingRequestsActionPerformed

    private void jmiShowLoggedCommActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiShowLoggedCommActionPerformed
        showLoggedComm();
    }//GEN-LAST:event_jmiShowLoggedCommActionPerformed

    private void jmiInitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiInitActionPerformed
        LOG.info("Test Communication: INIT");
        addPendingRequest(telegram.init());
    }//GEN-LAST:event_jmiInitActionPerformed

    private void jmiStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiStartActionPerformed
        LOG.info("Test Communication: START");
        addPendingRequest(telegram.start());
    }//GEN-LAST:event_jmiStartActionPerformed

    private void jmiEngineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiEngineActionPerformed
        LOG.info("Test Communication: ENGINE");
        addPendingRequest(telegram.engine());
    }//GEN-LAST:event_jmiEngineActionPerformed

    private void jmiMeasureActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiMeasureActionPerformed
        LOG.info("Test Communication: MEASURE");
        addPendingRequest(telegram.measure());
    }//GEN-LAST:event_jmiMeasureActionPerformed

    private void jmiMeasurenoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiMeasurenoActionPerformed
        LOG.info("Test Communication: MEASURENO");
        addPendingRequest(telegram.measureno());
    }//GEN-LAST:event_jmiMeasurenoActionPerformed

    private void jmiFineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiFineActionPerformed
        LOG.info("Test Communication: FINE");
        addPendingRequest(telegram.fine());
    }//GEN-LAST:event_jmiFineActionPerformed

    private void jmiWarningActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiWarningActionPerformed
        LOG.info("Test Communication: WARNING");
        addPendingRequest(telegram.warning());
    }//GEN-LAST:event_jmiWarningActionPerformed

    private void jmiSevereActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiSevereActionPerformed
        LOG.info("Test Communication: SEVERE");
        addPendingRequest(telegram.severe());
    }//GEN-LAST:event_jmiSevereActionPerformed

    private void jmiMaxProblemsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiMaxProblemsActionPerformed
        LOG.info("Test Communication: MAXPROBLEMS");
        addPendingRequest(telegram.maxProblems());
    }//GEN-LAST:event_jmiMaxProblemsActionPerformed

    private void jmiYellowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiYellowActionPerformed
        LOG.warning("User checked LED: WARNING");
    }//GEN-LAST:event_jmiYellowActionPerformed

    private void jmiRedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiRedActionPerformed
        LOG.warning("User checked LED: SEVERE");
    }//GEN-LAST:event_jmiRedActionPerformed

    private void jmiYellowRedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiYellowRedActionPerformed
        LOG.warning("User checked LED: MAXPROBLEMS");
        JOptionPane.showMessageDialog(this,
                "Wenn beide Kontrollleuchten (Gelb & Rot) leuchten ist ein schwerwiegender Fehler am Gerät oder im Programm aufgetreten...\n\n"
                + "Starten Sie die Software neu und drücken Sie den Reset-Button am Gerät.\n\n"
                + "Wenn der Fehler erneut oder öfters auftritt, starten Sie das Programm im Entwicklungsmodus\n"
                + "und senden Sie nach erneuter Ausführung alle Dateien im Ordner 'Service_Files' an den Entwickler!",
                "Gelbe & Rote LED: Schwerwiegender Fehler",
                JOptionPane.WARNING_MESSAGE);
    }//GEN-LAST:event_jmiYellowRedActionPerformed

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

    public class MyTelegram extends Telegram {

        @Override
        protected void done() {
        }

        @Override
        protected void process(List<Request> chunks) {
            for (Request r : chunks) {
                if (r.getStatus() == Status.DONE) {
                    LOG.debug("Request " + r.getReqName() + ": DONE");
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
                        userLog("Gerät ist einsatzbereit!", LogLevel.FINE);
                        addPendingRequest(telegram.fine());
                    } else if (r.getStatus() == Status.ERROR) {
                        if (secondTry) {
                            LOG.warning("INIT: Second try was required...");
                            addPendingRequest(telegram.init());
                            secondTry = false;
                        } else {
                            userLogPane("Gerät hat möglicherweise einen Fehler oder ist defekt. Keine Gewährleistung der Korrektheit der Messdaten - Status-LEDs kontrollieren!", LogLevel.WARNING);
                            addPendingRequest(telegram.warning());
                        }
                    }
                } else if (r.getVariety() == Variety.START) {
                    if (r.getStatus() == Status.DONE) {
                        userLog("Messung der Umweltdaten abgeschlossen.", LogLevel.FINE);
                    } else if (r.getStatus() == Status.ERROR) {
                        userLogPane("Umweltdaten möglicherweise fehlerhaft oder unvollständig...", LogLevel.WARNING);
                    }
                } else if (r.getVariety() == Variety.ENGINE) {
                    if (r.getStatus() == Status.DONE) {
                        userLog("Messung der Motorradtemperaturen abgeschlossen", LogLevel.FINE);
                    } else if (r.getStatus() == Status.ERROR) {
                        userLog("Motorradtemperaturen fehlerhaft - Thermoelemente überprüfen!", LogLevel.WARNING);
                        addPendingRequest(telegram.warning());
                    }
                }
            }
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
            try {
                System.setProperty("apple.laf.useScreenMenuBar", "true");
                System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Zweiradprüfstand");
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
                java.util.logging.Logger.getLogger(BESDyno.class
                        .getName()).log(java.util.logging.Level.SEVERE, null, ex);
                //LOG.severe(ex);
            }
            javax.swing.SwingUtilities.invokeLater(() -> {
                BESDyno besDyno = new BESDyno();
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
        } else {
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
                BESDyno besDyno = new BESDyno();
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

        LOG
                = Logger.getLogger(BESDyno.class
                        .getName());
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
    private javax.swing.JSlider jSlider;
    private javax.swing.JButton jbutConnect;
    private javax.swing.JButton jbutDisconnect;
    private javax.swing.JButton jbutRefresh;
    private javax.swing.JButton jbutStartSim;
    private javax.swing.JComboBox<String> jcbSerialDevices;
    private javax.swing.JCheckBoxMenuItem jcbmiDarkMode;
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
    private javax.swing.JCheckBoxMenuItem jmiDevMode;
    private javax.swing.JMenuItem jmiDisconnect;
    private javax.swing.JMenuItem jmiEngine;
    private javax.swing.JMenuItem jmiExport;
    private javax.swing.JMenuItem jmiFine;
    private javax.swing.JMenuItem jmiHelp;
    private javax.swing.JMenuItem jmiInit;
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
    private javax.swing.JMenuItem jmiShowPendingRequests;
    private javax.swing.JMenuItem jmiStart;
    private javax.swing.JMenuItem jmiStartSim;
    private javax.swing.JMenuItem jmiWarning;
    private javax.swing.JMenuItem jmiYellow;
    private javax.swing.JMenuItem jmiYellowRed;
    private javax.swing.JProgressBar jpbStatus;
    private javax.swing.JTextField jtfStatus;
    // End of variables declaration//GEN-END:variables
}
