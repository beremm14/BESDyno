package main;

import data.Bike;
import data.Config;
import data.Database;
import data.Environment;
import development.CommunicationLogger;
import development.TestCSV;
import development.gui.DevInfoPane;
import development.gui.LoggedCommPane;
import gui.AboutDialog;
import gui.ElectronicDialog;
import gui.HelpDialog;
import gui.MeasureDialog;
import gui.ResultDialog;
import gui.SettingsDialog;
import gui.VehicleSetDialog;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.TooManyListenersException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;
import jssc.SerialPortException;
import logging.LogBackgroundHandler;
import logging.LogOutputStreamHandler;
import logging.Logger;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
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
import serial.UARTManager;
import serial.requests.Request;
import serial.requests.Request.Status;
import serial.requests.RequestEngine;
import serial.requests.RequestInit;
import serial.requests.RequestKill;
import serial.requests.RequestMeasure;
import serial.requests.RequestMeasureno;
import serial.requests.RequestStart;
import serial.requests.RequestVersion;
import serial.Telegram;
import serial.requests.RequestAll;

/**
 *
 * @author emil
 */
public class BESDyno extends javax.swing.JFrame {

    private static BESDyno instance;

    private static final Logger LOG;
    private static final Logger LOGP;

    //JDialog-Objects
    private final AboutDialog about = new AboutDialog(this, false);
    private final HelpDialog help = new HelpDialog(this, false);
    private final VehicleSetDialog vehicle = new VehicleSetDialog(this, true);
    private final SettingsDialog settings = new SettingsDialog(this, true);
    private final DevInfoPane infoPane = new DevInfoPane(this, false);
    private final LoggedCommPane commPane = new LoggedCommPane(this, false);
    private final ResultDialog result = new ResultDialog(this, true);
    private final ElectronicDialog electronic = new ElectronicDialog(this, false);

    private MeasureDialog measure;

    //Object-Variables
    private SwingWorker activeWorker;
    private MyTelegram telegram;
    private UARTManager portManager;
    private final JFreeChart chart;

    //Variables
    private static boolean devMode = false;
    private boolean testMode = false;
    private static OS os = OS.OTHER;
    private boolean connection = false;
    private boolean activity = false;
    private boolean secondTry = true;
    private boolean measurementFinished = false;
    private final double reqArduVers = 1.0;
    private int timeouts = 0;

    //Communication
    public final List<Request> pendingRequests = new LinkedList<>();
    private final Object syncObj = new Object();

    //LineChart
    private final LineChart lc = new LineChart();
    private ChartPanel chartPanel;

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

        setOSNativeKeyStroke();

        //Check for multi-platform!!!
        //Works on: macOS, ?, ?
        setTitle("🦅 BESDyno - Zweiradprüfstand 🦅");
        setLocationRelativeTo(null);
        setSize(new Dimension(1200, 750));

        jtfStatus.setEditable(false);

        jcbmiDevMode.setState(false);
        jcbmiDebugLogging.setState(false);
        devMode = jcbmiDevMode.getState();
        LOG.setDebugMode(jcbmiDebugLogging.getState());

        addLogFileHandler(devMode);

        refreshPorts();

        refreshGui();

        setAppearance(Config.getInstance().isDark());

        jcbmiDarkMode.setState(Config.getInstance().isDark());
        userLog(getSalutation() + "Bitte verbinden Sie Ihr Gerät...", LogLevel.INFO);

        chart = lc.initChart();
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
        jmiEnvironment.setEnabled(false);
        jmiEngineTemp.setEnabled(false);

        //Development Tools
        jmiShowPendingRequests.setEnabled(false);
        jmiShowLoggedComm.setEnabled(false);
        jcbmiSaveLoggedComm.setEnabled(false);
        jcbmiDebugLogging.setEnabled(false);
        jcbmiTestMode.setEnabled(false);
        jmenuRequests.setEnabled(false);
        if (devMode) {
            jmiShowPendingRequests.setEnabled(true);
            jmiShowLoggedComm.setEnabled(true);
            jcbmiSaveLoggedComm.setEnabled(true);
            jcbmiDebugLogging.setEnabled(true);
            jcbmiTestMode.setEnabled(true);
            if (connection) {
                jmenuRequests.setEnabled(true);
            }
        }

        if (activeWorker != null) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            jpbStatus.setIndeterminate(true);
            return;
        } else {
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            jpbStatus.setIndeterminate(false);
        }

        if (activity) {
            jpbStatus.setIndeterminate(true);
        } else {
            jpbStatus.setIndeterminate(false);
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
        if (portManager != null) {
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
            jmiEnvironment.setEnabled(true);
            jmiEngineTemp.setEnabled(true);
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

    private void setOSNativeKeyStroke() {
        if (os == OS.MACOS) {
            jmiSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.META_MASK));
            jmiExport.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.META_MASK));
            jmiPrint.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.META_MASK));
            jmiSettings.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_COMMA, InputEvent.META_MASK));
            jmiStartSim.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.META_MASK));
            jmiRefresh.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, InputEvent.META_MASK));
            jmiConnect.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.META_MASK));
            jmiDisconnect.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, InputEvent.META_MASK));
            jcbmiDarkMode.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.META_MASK));
            jcbmiDevMode.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.META_MASK));
            jcbmiTestMode.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.META_MASK));
            jcbmiSaveLoggedComm.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.ALT_MASK | InputEvent.META_MASK));
            jmiAbout.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PERIOD, InputEvent.META_MASK));
            jmiHelp.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
        } else {
            jmiSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
            jmiExport.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_MASK));
            jmiPrint.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_MASK));
            jmiSettings.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_COMMA, InputEvent.CTRL_MASK));
            jmiStartSim.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_MASK));
            jmiRefresh.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, InputEvent.CTRL_MASK));
            jmiConnect.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_MASK));
            jmiDisconnect.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, InputEvent.CTRL_MASK));
            jcbmiDarkMode.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_MASK));
            jcbmiDevMode.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));
            jcbmiTestMode.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.CTRL_MASK));
            jcbmiSaveLoggedComm.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.ALT_MASK | InputEvent.CTRL_MASK));
            jmiAbout.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PERIOD, InputEvent.CTRL_MASK));
            jmiHelp.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
        }
    }

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

    //Config
    private void loadConfig() throws IOException {
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
                    throw new IOException("Internal Error");
                }
            }
            configFile = new File(folder + File.separator + "Config.json");
        } else {
            configFile = new File("Config.json");
        }

        if (configFile.exists()) {
            try {
                Config.getInstance().readJson(new FileInputStream(configFile));
            } catch (Exception ex) {
                Config.getInstance().createConfig(new BufferedWriter(new FileWriter(configFile)));
            }
            jcbmiDarkMode.setState(Config.getInstance().isDark());
        } else {
            Config.getInstance().createConfig(new BufferedWriter(new FileWriter(configFile)));
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

    //Files
    private void exportChart() {
        File file;

        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Portable Network Graphic (*.png)", "png");
        chooser.setFileFilter(filter);
        int rv = chooser.showSaveDialog(this);
        if (rv == JFileChooser.APPROVE_OPTION) {
            file = chooser.getSelectedFile();
            if (!file.getName().endsWith(".png")) {
                file = new File(file.getPath() + ".png");
            }

            try {
                ChartUtilities.saveChartAsPNG(file, chart, Config.getInstance().getPngWidth(), Config.getInstance().getPngHeight());
                LOG.fine("PNG saved: " + file.getPath());
            } catch (IOException ex) {
                userLog("Fehler beim Speichern", LogLevel.WARNING);
            } catch (NullPointerException ex)//Fehler beim Pfad
            {
                userLog("Fehler beim Pfad", LogLevel.WARNING);
            } catch (Exception ex) {
                userLog("Fehler aufgetreten!", LogLevel.SEVERE);
            }
        }
    }

    private void saveCSVData() {
        File file;

        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Comma Seperated Values (*.csv)", "csv");
        chooser.setFileFilter(filter);

        int rv = chooser.showSaveDialog(this);
        if (rv == JFileChooser.APPROVE_OPTION) {
            file = chooser.getSelectedFile();
            if (!file.getName().endsWith(".csv")) {
                file = new File(file.getPath() + ".csv");
            }

            try (BufferedWriter w = new BufferedWriter(new FileWriter(file))) {
                for (int i = 0; i < Database.getInstance().getDataList().size() - 1; i++) {
                    w.write(String.format(Locale.UK, "%.2f", Database.getInstance().getDataList().get(i).getPower()));
                    w.write(',');
                    w.write(String.format(Locale.UK, "%.2f", Database.getInstance().getDataList().get(i).getTorque()));
                    w.write(',');
                    w.write(String.format(Locale.UK, "%.2f", Database.getInstance().getPreOrFilteredList().get(i).getEngRpm()));
                    w.write(',');
                    w.write(String.format(Locale.UK, "%.2f", Database.getInstance().getPreOrFilteredList().get(i).getWheelRpm()));
                    w.newLine();
                }
                LOG.fine("CSV-Datei saved: " + file.getPath());
            } catch (IOException ex) {
                userLog("Fehler beim Speichern", LogLevel.WARNING);
            }
        }

    }

    //Online-Manual
    private void openURL(String url) {
        URI uri = null;
        try {
            uri = new URI(url);
        } catch (URISyntaxException ex) {
            LOG.warning(ex);
        }
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(uri);
            } catch (IOException ex) {
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

    public boolean isTestMode() {
        return testMode;
    }

    public OS getOs() {
        return os;
    }

    public UARTManager getPort() {
        return portManager;
    }

    public double getReqArduVers() {
        return reqArduVers;
    }

    public boolean hasConnection() {
        return connection;
    }

    public SettingsDialog getSettingsDialog() {
        return settings;
    }

    //Setter
    public void setConnection(boolean connection) {
        this.connection = connection;
    }

    public void setPort(UARTManager portManager) {
        this.portManager = portManager;
    }

    //Communication
    private static boolean readInRxTxComm() {
        boolean returnValue = false;
        File libFile = new File(System.getProperty("user.home") + "/.Bike/librxtxSerial.jnilib");
        InputStream input;
        FileOutputStream output = null;
        input = BESDyno.class.getResourceAsStream("librxtxSerial.jnilib");
        if (input != null) {
            int read;
            byte[] buffer = new byte[4096];
            try {
                output = new FileOutputStream(System.getProperty("user.home") + "/.Bike/librxtxSerial.jnilib");
                while ((read = input.read(buffer)) != -1) {
                    output.write(buffer, 0, read);
                }
                output.close();
                input.close();
                returnValue = true;
            } catch (Exception ex) {
                try {
                    output.close();
                    if (libFile.exists()) {
                        libFile.delete();
                    }
                } catch (Exception ex_out) {
                    //Do nothing
                }
                try {
                    input.close();
                } catch (Exception ex_in) {
                    //Do nothing
                }
            }
        }
        return returnValue;
    }

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
        jmiSave = new javax.swing.JMenuItem();
        jmiExport = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jmiPrint = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jmiSettings = new javax.swing.JMenuItem();
        jmenuSimulation = new javax.swing.JMenu();
        jmiStartSim = new javax.swing.JMenuItem();
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
        jcbmiTestMode = new javax.swing.JCheckBoxMenuItem();
        jmiSaveCSV = new javax.swing.JMenuItem();
        jSeparator7 = new javax.swing.JPopupMenu.Separator();
        jmiShowPendingRequests = new javax.swing.JMenuItem();
        jmiShowLoggedComm = new javax.swing.JMenuItem();
        jcbmiSaveLoggedComm = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        jmenuRequests = new javax.swing.JMenu();
        jmiInit = new javax.swing.JMenuItem();
        jmiVersion = new javax.swing.JMenuItem();
        jmiStart = new javax.swing.JMenuItem();
        jmiEngine = new javax.swing.JMenuItem();
        jmiKill = new javax.swing.JMenuItem();
        jmiAll = new javax.swing.JMenuItem();
        jmiMeasure = new javax.swing.JMenuItem();
        jmiMeasureno = new javax.swing.JMenuItem();
        jmiFine = new javax.swing.JMenuItem();
        jmiWarning = new javax.swing.JMenuItem();
        jmiSevere = new javax.swing.JMenuItem();
        jmiMaxProblems = new javax.swing.JMenuItem();
        jmenuAbout = new javax.swing.JMenu();
        jmiAbout = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
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
        jmenuDeveloper.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleTestMode(evt);
            }
        });

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

        jcbmiTestMode.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, java.awt.event.InputEvent.META_MASK));
        jcbmiTestMode.setText("Testmodus");
        jmenuDeveloper.add(jcbmiTestMode);

        jmiSaveCSV.setText("Test-Daten speichern");
        jmiSaveCSV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onSaveCSV(evt);
            }
        });
        jmenuDeveloper.add(jmiSaveCSV);
        jmenuDeveloper.add(jSeparator7);

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

        jmiAll.setText("ALL");
        jmiAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onTestAll(evt);
            }
        });
        jmenuRequests.add(jmiAll);

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

        jMenuItem1.setText("Elektroniker");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onElectronic(evt);
            }
        });
        jmenuAbout.add(jMenuItem1);

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
        saveCSVData();
    }//GEN-LAST:event_onSave

    private void onPrint(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onPrint
        chartPanel.createChartPrintJob();
    }//GEN-LAST:event_onPrint

    private void onSettings(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onSettings
        try {
            loadConfig();
        } catch (Exception ex) {
            LOG.warning(ex);
        }
        settings.setSwingValues(Config.getInstance());
        settings.setAppearance(Config.getInstance().isDark(), os);
        if (portManager != null) {
            if (portManager.getPort() instanceof gnu.io.SerialPort) {
                gnu.io.SerialPort port = (gnu.io.SerialPort) portManager.getPort();
                settings.writeDevice(port.getName());
            } else if (portManager.getPort() instanceof jssc.SerialPort) {
                jssc.SerialPort port = (jssc.SerialPort) portManager.getPort();
                settings.writeDevice(port.getPortName());
            }
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

    private void onStartSim(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onStartSim
        if (connection) {
            vehicle.setAppearance(Config.getInstance().isDark(), os);
            vehicle.setVisible(true);

            if (vehicle.isPressedOK()) {
                userLog("Start der Simulation", LogLevel.INFO);
                activity = true;

                measure = new MeasureDialog(this, true);
                measure.setAppearance(Config.getInstance().isDark());
                measure.setVisible(true);

                if (measure.isFinished()) {
                    measurementFinished = true;

                    result.setAppearance(Config.getInstance().isDark());
                    result.setValues();
                    result.setVisible(true);

                    lc.updateChartValues();

                    activity = false;
                } else {
                    activity = false;
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
        if (portManager != null) {
            if (portManager.getPort() instanceof gnu.io.SerialPort) {
                gnu.io.SerialPort port = (gnu.io.SerialPort) portManager.getPort();
                settings.writeDevice(port.getName());
            } else if (portManager.getPort() instanceof jssc.SerialPort) {
                jssc.SerialPort port = (jssc.SerialPort) portManager.getPort();
                settings.writeDevice(port.getPortName());
            }
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
        exportChart();
    }//GEN-LAST:event_onExport

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
            jcbmiTestMode.setState(false);
            testMode = false;
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
                    Environment.getInstance().getEnvTempC(), Environment.getInstance().getAirPress(), Environment.getInstance().getAltitude()),
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
                    Environment.getInstance().getEngTempC(), Environment.getInstance().getFumeTempC()),
                    "Zweiradtemperaturen aktualisiert", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_onEngineTemp

    private void toggleTestMode(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toggleTestMode
        testMode = jcbmiTestMode.getState();
        LOG.info("Switched to Test-Mode");
    }//GEN-LAST:event_toggleTestMode

    private void onSaveCSV(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onSaveCSV
        TestCSV csv = new TestCSV();
        csv.writeFiles();
    }//GEN-LAST:event_onSaveCSV

    private void onTestAll(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onTestAll
        LOG.info("Test Communication: ALL");
        addPendingRequest(telegram.all());
    }//GEN-LAST:event_onTestAll

    private void onElectronic(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onElectronic
        electronic.setVisible(true);
    }//GEN-LAST:event_onElectronic

    private class MyConnectPortWorker extends ConnectPortWorker {

        public MyConnectPortWorker(String port) {
            super(port);
        }

        @Override
        protected void done() {
            try {
                secondTry = true;
                portManager = get(2, TimeUnit.SECONDS);
                telegram = new MyTelegram();
                telegram.setSerialPort(portManager);
                LOG.info("setPort: RxTxWorker");
                telegram.execute();
                userLog("Warten Sie bitte, bis das Gerät bereit ist...", LogLevel.INFO);
                addPendingRequest(telegram.init());
            } catch (InterruptedException | TooManyListenersException | ExecutionException | TimeoutException | SerialPortException e) {
                userLog(e, "Gerät konnte nicht verbunden werden...", LogLevel.SEVERE);
            } finally {
                activeWorker = null;
                activity = true;
                refreshGui();
            }
        }
    }

    private class MyDisconnectPortWorker extends DisconnectPortWorker {

        @Override
        protected void done() {
            try {
                connection = false;
                portManager.closePort();
                portManager = null;
            } catch (Throwable th) {
                userLog(th, "Fehler beim Trennen des Geräts!", LogLevel.WARNING);
            } finally {
                if (telegram != null) {
                    try {
                        telegram.setSerialPort(new UARTManager());
                    } catch (SerialPortException | TooManyListenersException ex) {
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

        private void handleInitError() {
            if (portManager != null) {
                MyDisconnectPortWorker w = new MyDisconnectPortWorker();
                w.execute();
                activeWorker = w;
                refreshGui();
            } else {
                telegram.cancel(true);
            }
        }

        @Override
        protected void done() {
            activity = false;
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
                    //continue;
                }

                if (r.getStatus() == Status.DONE || r.getStatus() == Status.ERROR) {
                    LOG.debug("Request " + r.getReqName() + " removed from pendingRequests");
                    removePendingRequest(r);
                } else if (r.getStatus() == Status.TIMEOUT) {
                    timeouts++;
                    removePendingRequest(r);
                    if (r.timeOutIsComp()) {
                        LOG.warning("Timed out, second try... All Timeouts: " + timeouts);
                        addPendingRequest(retryTimeoutRequest(r));
                    } else {
                        LOG.severe("Request " + r.getReqName() + " timed out 2 times! All Timeouts: " + timeouts);
                        if (r instanceof RequestInit || r instanceof RequestVersion) {
                            handleInitError();
                        }
                    }
                }

                if (r.getStatus() == Status.ERROR && r.secondTryAllowed()) {
                    LOG.warning("Second Try for: " + r.getReqName());
                    retryErrorRequest(r);
                }

                if (r instanceof RequestInit) {
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
                        addPendingRequest(telegram.warning());
                    } else if (r.getStatus() == Status.TIMEOUT) {
                        userLogPane("Time-out bei der Initialisierung - Gerät defekt oder nicht verbunden...", LogLevel.SEVERE);
                    }

                } else if (r instanceof RequestStart) {
                    if (r.getStatus() == Status.DONE) {
                        userLog("Messung der Umweltdaten abgeschlossen.", LogLevel.FINE);
                    } else if (r.getStatus() == Status.ERROR) {
                        userLog("Umweltdaten möglicherweise fehlerhaft oder unvollständig...", LogLevel.WARNING);
                        LOG.warning("START returns ERROR: " + r.getResponse());
                    } else if (r.getStatus() == Status.TIMEOUT) {
                        userLog("Time-out beim Messen der Umweltdaten!", LogLevel.SEVERE);
                    }

                } else if (r instanceof RequestEngine) {
                    if (r.getStatus() == Status.DONE) {
                        userLog("Messung der Motorradtemperaturen abgeschlossen", LogLevel.FINE);
                        addPendingRequest(telegram.fine());
                    } else if (r.getStatus() == Status.ERROR) {
                        userLog("Motorradtemperaturen fehlerhaft - Thermoelemente überprüfen!", LogLevel.WARNING);
                        LOG.warning("ENGINE returns ERROR: " + r.getResponse());
                        addPendingRequest(telegram.warning());
                    } else if (r.getStatus() == Status.TIMEOUT) {
                        userLog("Time-out beim Messen der Zweirad-Temperaturen!", LogLevel.SEVERE);
                    }

                } else if (r instanceof RequestAll) {
                    if (r.getStatus() == Status.ERROR) {
                        LOG.warning("ALL returns ERROR: " + r.getResponse());
                    }

                } else if (r instanceof RequestMeasure) {
                    if (r.getStatus() == Status.ERROR) {
                        LOG.warning("MEASURE returns ERROR: " + r.getResponse());
                    }

                } else if (r instanceof RequestMeasureno) {
                    if (r.getStatus() == Status.ERROR) {
                        LOG.warning("MEASURENO returns ERROR: " + r.getResponse());
                    }

                } else if (r instanceof RequestKill) {
                    if (r.getStatus() == Status.DONE) {
                        if (Bike.getInstance().isMeasRpm()) {
                            addPendingRequest(telegram.measure());
                            LOG.info("KILL sent MEASURE");
                        } else {
                            addPendingRequest(telegram.measureno());
                            LOG.info("KILL sent MEASURENO");
                        }
                    }
                } else if (r instanceof RequestVersion) {
                    if (r.getStatus() == Status.DONE) {
                        userLog("Gerät ist einsatzbereit!", LogLevel.FINE);
                        LOG.info("Arduino-Version: " + Config.getInstance().getArduinoVersion());
                        connection = true;
                        addPendingRequest(telegram.fine());
                    } else if (r.getStatus() == Status.ERROR) {
                        if (Config.getInstance().getArduinoVersion() >= reqArduVers) {
                            LOG.warning("VERSION returns ERROR: " + r.getResponse());

                        } else {
                            addPendingRequest(telegram.maxProblems());
                            userLogPane("Die Firmware am Prüfstand ist veraltet, bitte laden Sie eine neue Version (mind. Version " + reqArduVers + ")!", LogLevel.SEVERE);
                            LOG.warning("VERSION returns ERROR: " + r.getResponse());
                        }
                    }
                    activity = false;
                    refreshGui();
                }
            }
        }
    }

    private class LineChart {

        private final Database data = Database.getInstance();
        private final Environment environment = Environment.getInstance();
        private final Bike bike = Bike.getInstance();
        private final Config config = Config.getInstance();

        private final Font font = new Font("sansserif", Font.BOLD, 15);

        private final ValueMarker maxPowerMarker = new ValueMarker(data.getBikePower());
        private final ValueMarker maxTorqueMarker = new ValueMarker(data.getBikeTorque());

        private XYSeries seriesTorque = new XYSeries("Drehmoment");
        private XYSeries seriesPower = new XYSeries("Leistung");
        private final XYSeriesCollection datasetPower = new XYSeriesCollection();
        private final XYSeriesCollection datasetTorque = new XYSeriesCollection();

        public JFreeChart initChart() {
            JFreeChart chart = org.jfree.chart.ChartFactory.createXYLineChart(bike.getVehicleName(),
                    "Motordrehzahl [U/min]",
                    "Leistung [" + config.getPowerUnit() + "]",
                    datasetPower,
                    PlotOrientation.VERTICAL,
                    true,
                    true,
                    false);

            ValueAxis torqueAxis = new NumberAxis("Drehmoment [Nm]");
            torqueAxis.setLabelFont(chart.getXYPlot().getDomainAxis().getLabelFont());

            seriesPower.setKey("Leistung");

            chart.getXYPlot().setDataset(1, datasetTorque);
            chart.getXYPlot().setRangeAxis(1, torqueAxis);
            chart.getXYPlot().mapDatasetToRangeAxis(0, 0);//1st dataset to 1st y-axis
            chart.getXYPlot().mapDatasetToRangeAxis(1, 1); //2nd dataset to 2nd y-axis

            // Hinzufuegen von series zu der Datenmenge dataset
            datasetPower.addSeries(seriesPower);
            datasetTorque.addSeries(seriesTorque);

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
            r1.setSeriesShapesVisible(0, true);
            r1.setSeriesStroke(0, new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            XYLineAndShapeRenderer r2 = new XYLineAndShapeRenderer();
            r2.setSeriesPaint(0, Color.red);
            r2.setSeriesShapesVisible(0, true);
            r2.setSeriesStroke(0, new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            chart.getXYPlot().setRenderer(0, r1);
            chart.getXYPlot().setRenderer(1, r2);

            chart.getXYPlot().addRangeMarker(0, maxPowerMarker, Layer.BACKGROUND);
            chart.getXYPlot().addRangeMarker(1, maxTorqueMarker, Layer.BACKGROUND);

            chartPanel = new ChartPanel(chart);

            TextTitle eco = new TextTitle("Temperatur: NaN "
                    + "Luftdruck: NaN "
                    + "Seehöhe: NaN");
            TextTitle eng = new TextTitle("Max. Motortemperatur : NaN "
                    + "Max. Abgastemperatur: NaN");
            TextTitle val = new TextTitle("Pmax: NaN "
                    + "Mmax: NaN "
                    + "Vmax: NaN");

            chart.addSubtitle(eco);
            chart.addSubtitle(eng);
            chart.addSubtitle(val);

            jPanChart.add(chartPanel);

            chart.fireChartChanged();

            return chart;
        }

        private void updateChartLabels() {
            double envTemp = config.isCelcius() ? environment.getEnvTempC() : environment.getEnvTempF();
            double engTemp = config.isCelcius() ? environment.getEngTempC() : environment.getEngTempF();
            double fumeTemp = config.isCelcius() ? environment.getFumeTempC() : environment.getFumeTempF();

            double bikePower = config.isPs() ? data.getBikePowerPS() : data.getBikePowerKW();

            maxPowerMarker.setValue(data.getBikePower());
            maxTorqueMarker.setValue(data.getBikeTorque());

            TextTitle eco = new TextTitle(String.format("Temperatur: %.1f" + config.getTempUnit() + " Luftdruck: %.1fhPa Seehöhe: %dm",
                    envTemp, environment.getAirPress() / 100, (int) Math.round(environment.getAltitude())));

            TextTitle eng = new TextTitle(String.format("Max. Motortemperatur: %d" + config.getTempUnit() + " Max. Abgastemperatur: %d" + config.getTempUnit(),
                    (int) Math.round(engTemp), (int) Math.round(fumeTemp)));
            TextTitle val = new TextTitle(String.format("Pmax: %.2f" + config.getPowerUnit() + " Mmax: %.2fNm Vmax: %.2f" + config.getVeloUnit(),
                    bikePower, data.getBikeTorque(), data.getBikeVelo()));

            chart.clearSubtitles();
            chart.addSubtitle(eco);
            chart.addSubtitle(eng);
            chart.addSubtitle(val);

            String strMaxPower = String.format("Maximale Leistung: %.2f %s ", bikePower, config.getPowerUnit());
            String strMaxTorque = String.format("Maximales Drehmoment: %.2f Nm", data.getBikeTorque());
            maxPowerMarker.setLabel(strMaxPower);
            maxTorqueMarker.setLabel(strMaxTorque);
            chart.getXYPlot().getRangeAxis().setLabel("Leistung [" + config.getPowerUnit() + "]");
            seriesPower.setKey("Leistung");

            chart.fireChartChanged();
        }

        public void updateChartValues() {
            datasetPower.removeAllSeries();
            datasetTorque.removeAllSeries();

            seriesPower = Database.getInstance().getSeriesPower();
            seriesTorque = Database.getInstance().getSeriesTorque();

            seriesTorque.setKey("Drehmoment [Nm]");

            datasetPower.addSeries(seriesPower);
            datasetTorque.addSeries(seriesTorque);

            if (bike.isMeasRpm()) {
                chart.getXYPlot().getDomainAxis().setLabel("Motordrehzahl [U/min]");
            } else {
                chart.getXYPlot().getDomainAxis().setLabel("Walzendrehzahl [U/min]");
            }

            updateChartLabels();

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
        } catch (FileNotFoundException ex) {

            final JsonObjectBuilder b = Json.createObjectBuilder();

            b.add("Dark", false)
                    .add("Hysteresis Velo", 4)
                    .add("Hysteresis Rpm", 400)
                    .add("Hysteresis Time", 2500)
                    .add("Idle Velo", 4)
                    .add("Idle Rpm", 1800)
                    .add("Inertia", 3.7017)
                    .add("Period", 40)
                    .add("PNG Height", 1080)
                    .add("PNG Width", 1920)
                    .add("Power Correction Factor", 1.0)
                    .add("PS", true)
                    .add("Celcius", true)
                    .add("Start Velo", 4)
                    .add("Start Rpm", 2500)
                    .add("Stop Velo", 80)
                    .add("Stop Rpm", 9000)
                    .add("Torque Correction Factor", 1)
                    .add("Velocity", 1)
                    .add("Engine Max Temp", 95)
                    .add("Exhaust Max Temp", 500);

            JsonObject obj = b.build();
            try (BufferedWriter w = new BufferedWriter(new FileWriter(getConfigFile()))) {
                w.write(obj.toString());
            } catch (Exception ex1) {
                LOG.severe(ex1);
            }

            try {
                Config.createInstance(new FileInputStream(getConfigFile()));
                LOG.info("Config-File written: " + obj.toString());
            } catch (Exception ex1) {
                LOG.severe(ex1);
            }

        } catch (Exception ex) {
            LOG.severe(ex);
        }

        //Menu-Bar support for macOS
        if (System.getProperty("os.name").contains("Mac OS X")) {
            os = OS.MACOS;
            try {
                if (readInRxTxComm()) {
                    try {
                        try {
                            Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
                            fieldSysPath.setAccessible(true);
                            fieldSysPath.set(null, null);
                        } catch (NoSuchFieldException
                                | SecurityException
                                | IllegalArgumentException
                                | IllegalAccessException ex) {
                            ex.printStackTrace(System.err);
                        }
                        //System.load(System.getProperty("user.home") + "/.Bike/librxtxSerial.jnilib");
                        System.setProperty("java.library.path", System.getProperty("user.home") + "/.Bike/");
                        System.loadLibrary("rxtxSerial");

                        /*
                        sudo mkdir /var/lock
                        sudo dscl . -append /groups/_uucp GroupMembership username 
                        sudo chgrp uucp /var/lock 
                        sudo chmod 775 /var/lock 
                         */
                        LOG.fine("RxTxComm Binary File for Mac implemented");
                    } catch (Exception ex) {
                        LOG.severe("Error at RxTxBinary File: Communication won't work!");
                    }
                } else {
                    LOG.severe("Error at RxTxBinary File: Communication won't work!");

                }

                System.setProperty("apple.awt.fileDialogForDirectories", "true");
                System.setProperty("apple.laf.useScreenMenuBar", "true");
                System.setProperty("com.apple.mrj.application.apple.menu.about.name", "BESDyno");
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
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                /*
                for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                    if ("com.sun.java.swing.plaf.windows.WindowsLookAndFeel".equals(info.getName())) {
                        javax.swing.UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }

                }*/
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
                /*
                for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        javax.swing.UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }*/
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
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
                /*
                for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        javax.swing.UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }*/
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
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
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanChart;
    private javax.swing.JPanel jPanStatus;
    private javax.swing.JPanel jPanTools;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JPopupMenu.Separator jSeparator7;
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
    private javax.swing.JCheckBoxMenuItem jcbmiTestMode;
    private javax.swing.JMenu jmenuAbout;
    private javax.swing.JMenu jmenuAppearance;
    private javax.swing.JMenu jmenuDeveloper;
    private javax.swing.JMenu jmenuFile;
    private javax.swing.JMenu jmenuRequests;
    private javax.swing.JMenu jmenuSimulation;
    private javax.swing.JMenuItem jmiAbout;
    private javax.swing.JMenuItem jmiAll;
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
    private javax.swing.JMenuItem jmiPrint;
    private javax.swing.JMenuItem jmiRefresh;
    private javax.swing.JMenuItem jmiSave;
    private javax.swing.JMenuItem jmiSaveCSV;
    private javax.swing.JMenuItem jmiSettings;
    private javax.swing.JMenuItem jmiSevere;
    private javax.swing.JMenuItem jmiShowLoggedComm;
    private javax.swing.JMenuItem jmiShowPendingRequests;
    private javax.swing.JMenuItem jmiStart;
    private javax.swing.JMenuItem jmiStartSim;
    private javax.swing.JMenuItem jmiVersion;
    private javax.swing.JMenuItem jmiWarning;
    private javax.swing.JProgressBar jpbStatus;
    private javax.swing.JTextField jtfStatus;
    // End of variables declaration//GEN-END:variables
}
