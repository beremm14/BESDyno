package gui;

import data.Bike;
import data.BikePower;
import data.Config;
import java.awt.Color;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;
import data.Environment;
import serial.Port;

/**
 *
 * @author emil
 */
public class BESDyno extends javax.swing.JFrame {

    //Data-Objects
    private Bike bike = Bike.getInstance();
    private BikePower power = BikePower.getInstance();
    private Config config = Config.getInstance();
    private Environment env = Environment.getInstance();
    private Port port = Port.getInstance();
    
    //JDialog-Objects
    private AboutDialog about = new AboutDialog(this, false);
    private HelpDialog help = new HelpDialog(this, false);
    private VehicleSetDialog vehicle = new VehicleSetDialog(this, true);
    private MeasureDialog measure = new MeasureDialog(this, true);
    private SettingsDialog settings = new SettingsDialog(this, true);
    
    //Object-Variables
    private File file;
    
    //Variables
    private boolean dark = false;
    
    /**
     * Creates new form Gui
     */
    public BESDyno() {
        initComponents();
        setTitle("BESDyno - Zweiradprüfstand");
        setLocationRelativeTo(null);
        setSize(new Dimension(1200, 750));
        jtfStatus.setEditable(false);
        jtfStatus.setText("Willkommen! Bitte verbinden Sie Ihr Gerät...");
        refreshPorts();
        try {
            loadConfig();
        } catch (Exception ex) {
            jtfStatus.setText("Fehler bei Config-Datei! Bitte Einstellungen aufrufen und Prüfstand konfigurieren!");
            showThrowable(ex, "Fehler bei Config-Datei! Bitte Einstellungen aufrufen und Prüfstand konfigurieren!", JOptionPane.WARNING_MESSAGE);
            ex.printStackTrace(System.err);
        }
        
        refreshGui();
        
        if(config.isDark()) {
            dark = config.isDark();
            setAppearance(dark);
        }
        jcbmiDarkMode.setState(dark);
    }

    private void refreshGui() {
        jmiSave.setEnabled(false);
        jmiPrint.setEnabled(false);
        jmiStartSim.setEnabled(false);
        jbutStartSim.setEnabled(false);
        jmiConnect.setEnabled(false);
        jbutConnect.setEnabled(false);
        jmiDisconnect.setEnabled(false);
        jbutDisconnect.setEnabled(false);
        jcbSerialDevices.setEnabled(false);
        jcbmiDarkMode.setState(false);
        jmiRefresh.setEnabled(true);
        jbutRefresh.setEnabled(true);

        //Wennn Ports gefunden werden
        if (jcbSerialDevices.getModel().getSize() > 0) {
            jcbSerialDevices.setEnabled(true);
            jmiConnect.setEnabled(true);
            jbutConnect.setEnabled(true);
        }

        //Wenn ein Port geöffnet wurde
        if (port.getPort() != null && port.getPort().isOpened()) {
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

    //Status-Textfeld
    private void writeOutThrowable(Throwable th) {
        th.printStackTrace(System.err);
        String msg = th.getMessage();
        if (msg == null || msg.isEmpty()) {
            msg = th.getClass().getSimpleName();
        }
        jtfStatus.setText(msg);
    }

    //JOptionPane
    private void showThrowable(Throwable th) {
        th.printStackTrace(System.err);
        String msg = th.getMessage();
        if (msg == null || msg.isEmpty()) {
            msg = th.getClass().getSimpleName();
        }
        JOptionPane.showMessageDialog(this, msg, "Fehler ist aufgereten", JOptionPane.ERROR_MESSAGE);
    }
    
    //JOptionPane + Message
    private void showThrowable(Throwable th, String msg, int symbol) {
        th.printStackTrace(System.err);
        JOptionPane.showMessageDialog(this, msg, "Fehler ist aufgereten", symbol);
    }

    //Serial-Methods
    private void refreshPorts() {
        final String[] ports = jssc.SerialPortList.getPortNames();

        String preferedPort = null;
        for (String p : ports) {
            if (p.contains("USB")) {
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

    //Mit Ctrl+D kann das Erscheinungbild der Oberfläche geändert werden
    private void setAppearance(boolean dark) {
        if (dark) {

            setBackground(Color.darkGray);
            jPanChart.setBackground(Color.darkGray);
            jPanStatus.setBackground(Color.darkGray);
            jPanTools.setBackground(Color.darkGray);

            jLabelDevice.setForeground(Color.white);

            jtfStatus.setBackground(Color.darkGray);
            jtfStatus.setForeground(Color.white);
        } else {
            setBackground(Color.white);
            jPanChart.setBackground(Color.white);
            jPanStatus.setBackground(Color.white);
            jPanTools.setBackground(Color.white);

            jLabelDevice.setForeground(Color.black);

            jtfStatus.setBackground(Color.white);
            jtfStatus.setForeground(Color.black);
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
            folder = new File(home + "/Bike-Files");
            if (!folder.exists()) {
                if (!folder.mkdir()) {
                    throw new Exception("Internal Error");
                }
            }
            file = new File(folder + bike.getVehicleName() + ".bes");
        } else {
            file = new File(bike.getVehicleName() + "bes");
        }
        chooser.setSelectedFile(file);
        
        int rv = chooser.showSaveDialog(this);
        if (rv == JFileChooser.APPROVE_OPTION) {
            file = chooser.getSelectedFile();
            if (!file.getName().contains(".bes")) {
                showThrowable(new Exception("Das ist keine bes-Datei"));
            }

            try (BufferedWriter w = new BufferedWriter(new FileWriter(file))) {
                bike.writeFile(w);
            } catch (Exception ex) {
                writeOutThrowable(ex);
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
            folder = new File(home + "/Bike-Files");
            if (!folder.exists()) {
                if (!folder.mkdir()) {
                    throw new Exception("Internal Error");
                }
            }
            file = new File(folder + bike.getVehicleName() + ".bes");
        } else {
            file = new File(bike.getVehicleName() + "bes");
        }
        chooser.setSelectedFile(file);

        int rv = chooser.showOpenDialog(this);
        if (rv == JFileChooser.APPROVE_OPTION) {
            file = chooser.getSelectedFile();
            if (!file.getName().contains(".bes")) {
                showThrowable(new Exception("Das ist keine bes-Datei"));
            }
            try (BufferedReader r = new BufferedReader(new FileReader(file))) {
                bike.readFile(r);
            } catch (Exception ex) {
                writeOutThrowable(ex);
            }
        }
    }
    
    //Config
    private void loadConfig() throws FileNotFoundException, IOException, Exception {
        File home;
        File folder;
        File ConfigFile;

        try {
            home = new File(System.getProperty("user.home"));
        } catch (Exception e) {
            home = null;
        }

        if (home != null && home.exists()) {
            folder = new File(home + "/.Bike");
            if (!folder.exists()) {
                if (!folder.mkdir()) {
                    throw new Exception("Internal Error");
                }
            }
            ConfigFile = new File(folder + "/Bike.config");
        } else {
            ConfigFile = new File("Bike.config");
        }

        if (ConfigFile.exists()) {
            try (BufferedReader r = new BufferedReader(new FileReader(ConfigFile))) {
                config.readConfig(r);
            }
        }
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
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        jmiRefresh = new javax.swing.JMenuItem();
        jmiConnect = new javax.swing.JMenuItem();
        jmiDisconnect = new javax.swing.JMenuItem();
        jmenuAppearance = new javax.swing.JMenu();
        jcbmiDarkMode = new javax.swing.JCheckBoxMenuItem();
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
                jmiHelpActionPerformed(evt);
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
        } catch (IOException ex) {
            writeOutThrowable(ex);
        } catch (Exception ex) {
            writeOutThrowable(ex);
        }
    }//GEN-LAST:event_jmiSaveActionPerformed

    private void jmiPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiPrintActionPerformed

    }//GEN-LAST:event_jmiPrintActionPerformed

    private void jmiSettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiSettingsActionPerformed
        settings.setAppearance(dark);
        settings.setVisible(true);

        if (settings.isPressedOK()) {
            dark = settings.isDark();
            setAppearance(dark);
            config = settings.getConfig();
        }
    }//GEN-LAST:event_jmiSettingsActionPerformed

    private void jmiQuitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiQuitActionPerformed

    }//GEN-LAST:event_jmiQuitActionPerformed

    private void jmiStartSimActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiStartSimActionPerformed
        vehicle.setAppearance(dark);
        vehicle.setVisible(true);
        
        if(vehicle.isPressedOK()) {
            measure.setAppearance(dark);
            measure.setVisible(true);
        }
    }//GEN-LAST:event_jmiStartSimActionPerformed

    private void jmiRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiRefreshActionPerformed
        refreshPorts();
    }//GEN-LAST:event_jmiRefreshActionPerformed

    private void jmiConnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiConnectActionPerformed
        try {
            port.connectPort((String) jcbSerialDevices.getSelectedItem());
            refreshGui();
        } catch (Throwable ex) {
            writeOutThrowable(ex);
        }
    }//GEN-LAST:event_jmiConnectActionPerformed

    private void jmiDisconnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiDisconnectActionPerformed
        try {
            port.disconnectPort();
            refreshGui();
        } catch (Throwable ex) {
            writeOutThrowable(ex);
        }
    }//GEN-LAST:event_jmiDisconnectActionPerformed

    private void jmiAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiAboutActionPerformed
        about.setAppearance(dark);
        about.setVisible(true);
        if (port.getPort().isOpened()) {
            about.writeDevice((String)jcbSerialDevices.getSelectedItem());
        } else {
            about.writeDevice("Kein Prüfstand verbunden...");
        }
    }//GEN-LAST:event_jmiAboutActionPerformed

    private void jbutConnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbutConnectActionPerformed
        try {
            port.connectPort((String)jcbSerialDevices.getSelectedItem());
            refreshGui();
        } catch (Throwable ex) {
            writeOutThrowable(ex);
        }
    }//GEN-LAST:event_jbutConnectActionPerformed

    private void jbutDisconnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbutDisconnectActionPerformed
        try {
            port.disconnectPort();
            refreshGui();
        } catch (Throwable ex) {
            writeOutThrowable(ex);
        }
    }//GEN-LAST:event_jbutDisconnectActionPerformed

    private void jbutRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbutRefreshActionPerformed
        refreshPorts();
    }//GEN-LAST:event_jbutRefreshActionPerformed

    private void jmiHelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiHelpActionPerformed
        help.setAppearance(dark);
        help.setVisible(true);
    }//GEN-LAST:event_jmiHelpActionPerformed

    private void jbutStartSimActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbutStartSimActionPerformed
        vehicle.setAppearance(dark);
        vehicle.setVisible(true);
        
        if (vehicle.isPressedOK()) {
            measure.setAppearance(dark);
            measure.setVisible(true);
        }
    }//GEN-LAST:event_jbutStartSimActionPerformed

    private void jcbmiDarkModeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbmiDarkModeActionPerformed
        dark = jcbmiDarkMode.getState();
        setAppearance(dark);
        config.setDark(dark);
        try {
            settings.saveConfig(config);
        } catch (Exception e) {
            writeOutThrowable(new Exception("Fehler beim Speichern der Config-Datei!"));
            e.printStackTrace(System.err);
        }
    }//GEN-LAST:event_jcbmiDarkModeActionPerformed

    private void jmiExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiExportActionPerformed

    }//GEN-LAST:event_jmiExportActionPerformed

    private void jmiOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiOpenActionPerformed
        try {
            open();
        } catch (IOException ex) {
            writeOutThrowable(ex);
        } catch (Exception ex) {
            writeOutThrowable(ex);
        }
    }//GEN-LAST:event_jmiOpenActionPerformed

    /**
     * @param args the command line arguments
     * @throws javax.swing.UnsupportedLookAndFeelException
     */
    public static void main(String args[]) throws UnsupportedLookAndFeelException {
        //Menu-Bar support for macOS
        if (System.getProperty("os.name").contains("Mac OS X")) {
            try {
                System.setProperty("apple.laf.useScreenMenuBar", "true");
                System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Zweiradprüfstand");
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
                java.util.logging.Logger.getLogger(BESDyno.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
            javax.swing.SwingUtilities.invokeLater(() -> {
                new BESDyno().setVisible(true);
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
                java.util.logging.Logger.getLogger(BESDyno.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }

            java.awt.EventQueue.invokeLater(() -> {
                new BESDyno().setVisible(true);
            });
        }
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
    private javax.swing.JSlider jSlider;
    private javax.swing.JButton jbutConnect;
    private javax.swing.JButton jbutDisconnect;
    private javax.swing.JButton jbutRefresh;
    private javax.swing.JButton jbutStartSim;
    private javax.swing.JComboBox<String> jcbSerialDevices;
    private javax.swing.JCheckBoxMenuItem jcbmiDarkMode;
    private javax.swing.JMenu jmenuAbout;
    private javax.swing.JMenu jmenuAppearance;
    private javax.swing.JMenu jmenuFile;
    private javax.swing.JMenu jmenuSimulation;
    private javax.swing.JMenuItem jmiAbout;
    private javax.swing.JMenuItem jmiConnect;
    private javax.swing.JMenuItem jmiDisconnect;
    private javax.swing.JMenuItem jmiExport;
    private javax.swing.JMenuItem jmiHelp;
    private javax.swing.JMenuItem jmiOpen;
    private javax.swing.JMenuItem jmiPrint;
    private javax.swing.JMenuItem jmiQuit;
    private javax.swing.JMenuItem jmiRefresh;
    private javax.swing.JMenuItem jmiSave;
    private javax.swing.JMenuItem jmiSettings;
    private javax.swing.JMenuItem jmiStartSim;
    private javax.swing.JProgressBar jpbStatus;
    private javax.swing.JTextField jtfStatus;
    // End of variables declaration//GEN-END:variables
}
