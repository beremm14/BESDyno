package gui;

import data.Config;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.NoSuchElementException;
import java.util.Scanner;
import javax.swing.JOptionPane;

/**
 *
 * @author emil
 */
public class SettingsDialog extends javax.swing.JDialog {

    Config config = new Config();

    private boolean pressedOK;

    /**
     * Creates new form SettingsDialog
     */
    public SettingsDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        try {
            loadConfig();
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
    }

    public void setAppearance(boolean dark) {
        if (dark) {
            jPanAppearance.setBackground(Color.darkGray);
            jPanButtons.setBackground(Color.darkGray);
            jPanCorr.setBackground(Color.darkGray);
            jPanEast.setBackground(Color.darkGray);
            jPanMain.setBackground(Color.darkGray);
            jPanPNG.setBackground(Color.darkGray);
            jPanPower.setBackground(Color.darkGray);
            jPanSerial.setBackground(Color.darkGray);
            jPanWest.setBackground(Color.darkGray);

            jtfHysteresisKmh.setBackground(Color.darkGray);
            jtfHysteresisRpm.setBackground(Color.darkGray);
            jtfHysteresisTime.setBackground(Color.darkGray);
            jtfIdleKmh.setBackground(Color.darkGray);
            jtfIdleRpm.setBackground(Color.darkGray);
            jtfInertia.setBackground(Color.darkGray);
            jtfPeriod.setBackground(Color.darkGray);
            jtfPngX.setBackground(Color.darkGray);
            jtfPngY.setBackground(Color.darkGray);
            jtfPower.setBackground(Color.darkGray);
            jtfStartKmh.setBackground(Color.darkGray);
            jtfStartRpm.setBackground(Color.darkGray);
            jtfTorque.setBackground(Color.darkGray);

            jtfHysteresisKmh.setForeground(Color.white);
            jtfHysteresisRpm.setForeground(Color.white);
            jtfHysteresisTime.setForeground(Color.white);
            jtfIdleKmh.setForeground(Color.white);
            jtfIdleRpm.setForeground(Color.white);
            jtfInertia.setForeground(Color.white);
            jtfPeriod.setForeground(Color.white);
            jtfPngX.setForeground(Color.white);
            jtfPngY.setForeground(Color.white);
            jtfPower.setForeground(Color.white);
            jtfStartKmh.setForeground(Color.white);
            jtfStartRpm.setForeground(Color.white);
            jtfTorque.setForeground(Color.white);

            jrbDaymode.setForeground(Color.white);
            jrbNightmode.setForeground(Color.white);
            jrbKW.setForeground(Color.white);
            jrbPS.setForeground(Color.white);

            jLabelHysteresisKmh.setForeground(Color.white);
            jLabelHysteresisKmh2.setForeground(Color.white);
            jLabelHysteresisRpm.setForeground(Color.white);
            jLabelHysteresisRpm2.setForeground(Color.white);
            jLabelHysteresisTime.setForeground(Color.white);
            jLabelHysteresisTime2.setForeground(Color.white);
            jLabelIdlRpm.setForeground(Color.white);
            jLabelIdleKmh.setForeground(Color.white);
            jLabelIdleKmh2.setForeground(Color.white);
            jLabelIdleRpm2.setForeground(Color.white);
            jLabelInertia.setForeground(Color.white);
            jLabelInertia2.setForeground(Color.white);
            jLabelPeriod.setForeground(Color.white);
            jLabelPeriod2.setForeground(Color.white);
            jLabelPngX.setForeground(Color.white);
            jLabelPngY.setForeground(Color.white);
            jLabelPower.setForeground(Color.white);
            jLabelStartKmh.setForeground(Color.white);
            jLabelStartKmh2.setForeground(Color.white);
            jLabelStartRpm.setForeground(Color.white);
            jLabelStartRpm2.setForeground(Color.white);
            jLabelTorque.setForeground(Color.white);
        } else {
            jPanAppearance.setBackground(Color.white);
            jPanButtons.setBackground(Color.white);
            jPanCorr.setBackground(Color.white);
            jPanEast.setBackground(Color.white);
            jPanMain.setBackground(Color.white);
            jPanPNG.setBackground(Color.white);
            jPanPower.setBackground(Color.white);
            jPanSerial.setBackground(Color.white);
            jPanWest.setBackground(Color.white);

            jtfInertia.setBackground(Color.white);
            jtfInertia.setForeground(Color.black);

            jtfHysteresisKmh.setBackground(Color.white);
            jtfHysteresisRpm.setBackground(Color.white);
            jtfHysteresisTime.setBackground(Color.white);
            jtfIdleKmh.setBackground(Color.white);
            jtfIdleRpm.setBackground(Color.white);
            jtfInertia.setBackground(Color.white);
            jtfPeriod.setBackground(Color.white);
            jtfPngX.setBackground(Color.white);
            jtfPngY.setBackground(Color.white);
            jtfPower.setBackground(Color.white);
            jtfStartKmh.setBackground(Color.white);
            jtfStartRpm.setBackground(Color.white);
            jtfTorque.setBackground(Color.white);

            jtfHysteresisKmh.setForeground(Color.black);
            jtfHysteresisRpm.setForeground(Color.black);
            jtfHysteresisTime.setForeground(Color.black);
            jtfIdleKmh.setForeground(Color.black);
            jtfIdleRpm.setForeground(Color.black);
            jtfInertia.setForeground(Color.black);
            jtfPeriod.setForeground(Color.black);
            jtfPngX.setForeground(Color.black);
            jtfPngY.setForeground(Color.black);
            jtfPower.setForeground(Color.black);
            jtfStartKmh.setForeground(Color.black);
            jtfStartRpm.setForeground(Color.black);
            jtfTorque.setForeground(Color.black);

            jrbDaymode.setForeground(Color.black);
            jrbNightmode.setForeground(Color.black);
            jrbKW.setForeground(Color.black);
            jrbPS.setForeground(Color.black);

            jLabelHysteresisKmh.setForeground(Color.black);
            jLabelHysteresisKmh2.setForeground(Color.black);
            jLabelHysteresisRpm.setForeground(Color.black);
            jLabelHysteresisRpm2.setForeground(Color.black);
            jLabelHysteresisTime.setForeground(Color.black);
            jLabelHysteresisTime2.setForeground(Color.black);
            jLabelIdlRpm.setForeground(Color.black);
            jLabelIdleKmh.setForeground(Color.black);
            jLabelIdleKmh2.setForeground(Color.black);
            jLabelIdleRpm2.setForeground(Color.black);
            jLabelInertia.setForeground(Color.black);
            jLabelInertia2.setForeground(Color.black);
            jLabelPeriod.setForeground(Color.black);
            jLabelPeriod2.setForeground(Color.black);
            jLabelPngX.setForeground(Color.black);
            jLabelPngY.setForeground(Color.black);
            jLabelPower.setForeground(Color.black);
            jLabelStartKmh.setForeground(Color.black);
            jLabelStartKmh2.setForeground(Color.black);
            jLabelStartRpm.setForeground(Color.black);
            jLabelStartRpm2.setForeground(Color.black);
            jLabelTorque.setForeground(Color.black);
        }
    }

    public boolean isPressedOK() {
        return pressedOK;
    }

    public boolean isDark() {
        return jrbNightmode.isSelected();
    }

    public Config getConfig() {
        return config;
    }

    //Sets the Config-File
    private void getSwingValues(Config c) throws NoSuchElementException {
        c.setDark(jrbNightmode.isSelected());
        c.setHysteresisKmh(new Scanner(jtfHysteresisKmh.getText()).nextInt());
        c.setHysteresisRpm(new Scanner(jtfHysteresisKmh.getText()).nextInt());
        c.setHysteresisTime(new Scanner(jtfHysteresisKmh.getText()).nextInt());
        c.setIdleKmh(new Scanner(jtfIdleKmh.getText()).nextInt());
        c.setIdleRpm(new Scanner(jtfIdleRpm.getText()).nextInt());
        c.setInertiaCorr(new Scanner(jtfInertia.getText()).nextDouble());
        c.setPeriod(new Scanner(jtfPeriod.getText()).nextInt());
        c.setPngHeight(new Scanner(jtfPngY.getText()).nextInt());
        c.setPngWidth(new Scanner(jtfPngX.getText()).nextInt());
        c.setPowerCorr(new Scanner(jtfPower.getText()).nextInt());
        c.setPs(jrbPS.isSelected());
        c.setStartKmh(new Scanner(jtfStartKmh.getText()).nextInt());
        c.setStartRpm(new Scanner(jtfStartRpm.getText()).nextInt());
        c.setTorqueCorr(new Scanner(jtfTorque.getText()).nextInt());
    }

    //Sets jTFs & jRBs from Config-File
    private void setSwingValues(Config c) {
        jtfHysteresisKmh.setText(String.format("%d", c.getHysteresisKmh()));
        jtfHysteresisRpm.setText(String.format("%d", c.getHysteresisRpm()));
        jtfHysteresisTime.setText(String.format("%d", c.getHysteresisTime()));
        jtfIdleKmh.setText(String.format("%d", c.getIdleKmh()));
        jtfIdleRpm.setText(String.format("%d", c.getIdleRpm()));
        jtfInertia.setText(String.format("%f", c.getInertiaCorr()));
        jtfPeriod.setText(String.format("%d", c.getPeriod()));
        jtfPngX.setText(String.format("%d", c.getPngWidth()));
        jtfPngY.setText(String.format("%d", c.getPngHeight()));
        jtfPower.setText(String.format("%d", c.getPowerCorr()));
        jtfStartKmh.setText(String.format("%d", c.getStartKmh()));
        jtfStartRpm.setText(String.format("%d", c.getStartRpm()));
        jtfTorque.setText(String.format("%d", c.getTorqueCorr()));

        jrbDaymode.setSelected(!c.isDark());
        jrbNightmode.setSelected(c.isDark());
        jrbKW.setSelected(!c.isPs());
        jrbPS.setSelected(c.isPs());
    }

    //Loads Config-File from hidden Folder
    private void loadConfig() throws Exception {
        File home;
        File folder;
        File file;

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
            file = new File(folder + "/Bike.config");
        } else {
            file = new File("Bike.config");
        }

        if (file.exists()) {
            try (BufferedReader r = new BufferedReader(new FileReader(file))) {
                config.readConfig(r);
                setSwingValues(config);
            }
        }
    }

    //Saves Config-File after changing the settings
    private void saveConfig() throws Exception {
        File home;
        File folder;
        File file;

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
            file = new File(folder + "/Bike.config");
        } else {
            file = new File("Bike.config");
        }

        try (BufferedWriter w = new BufferedWriter(new FileWriter(file))) {
            config.writeConfig(w);
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

        jbgAppearance = new javax.swing.ButtonGroup();
        jbgPower = new javax.swing.ButtonGroup();
        jPanMain = new javax.swing.JPanel();
        jPanWest = new javax.swing.JPanel();
        jPanPower = new javax.swing.JPanel();
        jrbPS = new javax.swing.JRadioButton();
        jrbKW = new javax.swing.JRadioButton();
        jPanPNG = new javax.swing.JPanel();
        jtfPngX = new javax.swing.JTextField();
        jtfPngY = new javax.swing.JTextField();
        jLabelPngX = new javax.swing.JLabel();
        jLabelPngY = new javax.swing.JLabel();
        jPanCorr = new javax.swing.JPanel();
        jLabelPower = new javax.swing.JLabel();
        jtfPower = new javax.swing.JTextField();
        jLabelTorque = new javax.swing.JLabel();
        jtfTorque = new javax.swing.JTextField();
        jLabelInertia = new javax.swing.JLabel();
        jtfInertia = new javax.swing.JTextField();
        jLabelInertia2 = new javax.swing.JLabel();
        jPanEast = new javax.swing.JPanel();
        jPanAppearance = new javax.swing.JPanel();
        jrbDaymode = new javax.swing.JRadioButton();
        jrbNightmode = new javax.swing.JRadioButton();
        jPanSerial = new javax.swing.JPanel();
        jLabelPeriod = new javax.swing.JLabel();
        jLabelPeriod2 = new javax.swing.JLabel();
        jLabelHysteresisTime = new javax.swing.JLabel();
        jLabelHysteresisTime2 = new javax.swing.JLabel();
        jLabelIdleKmh = new javax.swing.JLabel();
        jLabelIdleKmh2 = new javax.swing.JLabel();
        jLabelHysteresisKmh = new javax.swing.JLabel();
        jLabelHysteresisKmh2 = new javax.swing.JLabel();
        jLabelStartKmh = new javax.swing.JLabel();
        jLabelStartKmh2 = new javax.swing.JLabel();
        jLabelIdlRpm = new javax.swing.JLabel();
        jLabelIdleRpm2 = new javax.swing.JLabel();
        jLabelHysteresisRpm = new javax.swing.JLabel();
        jLabelHysteresisRpm2 = new javax.swing.JLabel();
        jLabelStartRpm = new javax.swing.JLabel();
        jLabelStartRpm2 = new javax.swing.JLabel();
        jtfPeriod = new javax.swing.JTextField();
        jtfHysteresisTime = new javax.swing.JTextField();
        jtfHysteresisKmh = new javax.swing.JTextField();
        jtfStartKmh = new javax.swing.JTextField();
        jtfIdleRpm = new javax.swing.JTextField();
        jtfHysteresisRpm = new javax.swing.JTextField();
        jtfStartRpm = new javax.swing.JTextField();
        jtfIdleKmh = new javax.swing.JTextField();
        jPanButtons = new javax.swing.JPanel();
        jbutCancel = new javax.swing.JButton();
        jbutOK = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));

        jPanMain.setBackground(new java.awt.Color(255, 255, 255));
        jPanMain.setLayout(new java.awt.GridLayout(1, 0));

        jPanWest.setBackground(new java.awt.Color(255, 255, 255));
        jPanWest.setLayout(new java.awt.GridBagLayout());

        jPanPower.setBackground(new java.awt.Color(255, 255, 255));
        jPanPower.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Leistungseinheit", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 0, 13), new java.awt.Color(255, 0, 0))); // NOI18N
        jPanPower.setMinimumSize(new java.awt.Dimension(200, 47));
        jPanPower.setPreferredSize(new java.awt.Dimension(312, 47));
        jPanPower.setLayout(new java.awt.GridBagLayout());

        jbgPower.add(jrbPS);
        jrbPS.setText("PS");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 7);
        jPanPower.add(jrbPS, gridBagConstraints);

        jbgPower.add(jrbKW);
        jrbKW.setText("kW");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 7);
        jPanPower.add(jrbKW, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanWest.add(jPanPower, gridBagConstraints);

        jPanPNG.setBackground(new java.awt.Color(255, 255, 255));
        jPanPNG.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "PNG Auflösung", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 0, 13), new java.awt.Color(255, 0, 0))); // NOI18N
        jPanPNG.setMinimumSize(new java.awt.Dimension(200, 50));
        jPanPNG.setPreferredSize(new java.awt.Dimension(312, 80));
        jPanPNG.setLayout(new java.awt.GridBagLayout());

        jtfPngX.setText("1920");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanPNG.add(jtfPngX, gridBagConstraints);

        jtfPngY.setText("1080");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanPNG.add(jtfPngY, gridBagConstraints);

        jLabelPngX.setText("X");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        jPanPNG.add(jLabelPngX, gridBagConstraints);

        jLabelPngY.setText("Y");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        jPanPNG.add(jLabelPngY, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanWest.add(jPanPNG, gridBagConstraints);

        jPanCorr.setBackground(new java.awt.Color(255, 255, 255));
        jPanCorr.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Korrekturfaktoren", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 0, 13), new java.awt.Color(255, 0, 0))); // NOI18N
        jPanCorr.setPreferredSize(new java.awt.Dimension(312, 102));
        jPanCorr.setLayout(new java.awt.GridBagLayout());

        jLabelPower.setText("Leistung");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanCorr.add(jLabelPower, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanCorr.add(jtfPower, gridBagConstraints);

        jLabelTorque.setText("Drehmoment");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanCorr.add(jLabelTorque, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanCorr.add(jtfTorque, gridBagConstraints);

        jLabelInertia.setText("Trägheitsmoment");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanCorr.add(jLabelInertia, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanCorr.add(jtfInertia, gridBagConstraints);

        jLabelInertia2.setText("kgm2");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        jPanCorr.add(jLabelInertia2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanWest.add(jPanCorr, gridBagConstraints);

        jPanMain.add(jPanWest);

        jPanEast.setBackground(new java.awt.Color(255, 255, 255));
        jPanEast.setLayout(new java.awt.GridBagLayout());

        jPanAppearance.setBackground(new java.awt.Color(255, 255, 255));
        jPanAppearance.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Erscheinungsbild", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 0, 13), new java.awt.Color(255, 0, 0))); // NOI18N
        jPanAppearance.setPreferredSize(new java.awt.Dimension(312, 70));
        jPanAppearance.setLayout(new java.awt.GridBagLayout());

        jbgAppearance.add(jrbDaymode);
        jrbDaymode.setText("Hell");
        jrbDaymode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jrbDaymodeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 44);
        jPanAppearance.add(jrbDaymode, gridBagConstraints);

        jbgAppearance.add(jrbNightmode);
        jrbNightmode.setText("Dunkel");
        jrbNightmode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jrbNightmodeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 44);
        jPanAppearance.add(jrbNightmode, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanEast.add(jPanAppearance, gridBagConstraints);

        jPanSerial.setBackground(new java.awt.Color(255, 255, 255));
        jPanSerial.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Kommunikation", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 0, 13), new java.awt.Color(255, 0, 0))); // NOI18N
        jPanSerial.setPreferredSize(new java.awt.Dimension(312, 232));
        jPanSerial.setLayout(new java.awt.GridBagLayout());

        jLabelPeriod.setText("Zeitintervall");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 13);
        jPanSerial.add(jLabelPeriod, gridBagConstraints);

        jLabelPeriod2.setText("ms");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        jPanSerial.add(jLabelPeriod2, gridBagConstraints);

        jLabelHysteresisTime.setText("Hysterese Zeitspanne");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 13);
        jPanSerial.add(jLabelHysteresisTime, gridBagConstraints);

        jLabelHysteresisTime2.setText("ms");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        jPanSerial.add(jLabelHysteresisTime2, gridBagConstraints);

        jLabelIdleKmh.setText("Geschwindigkeit wenn bereit");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 13);
        jPanSerial.add(jLabelIdleKmh, gridBagConstraints);

        jLabelIdleKmh2.setText("Km/h");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        jPanSerial.add(jLabelIdleKmh2, gridBagConstraints);

        jLabelHysteresisKmh.setText("Hysterese Geschwindigkeit +/-");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 13);
        jPanSerial.add(jLabelHysteresisKmh, gridBagConstraints);

        jLabelHysteresisKmh2.setText("Km/h");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        jPanSerial.add(jLabelHysteresisKmh2, gridBagConstraints);

        jLabelStartKmh.setText("Startgeschwindigkeit");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 13);
        jPanSerial.add(jLabelStartKmh, gridBagConstraints);

        jLabelStartKmh2.setText("Km/h");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        jPanSerial.add(jLabelStartKmh2, gridBagConstraints);

        jLabelIdlRpm.setText("Motordrehzahl wenn bereit");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 13);
        jPanSerial.add(jLabelIdlRpm, gridBagConstraints);

        jLabelIdleRpm2.setText("U/min");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        jPanSerial.add(jLabelIdleRpm2, gridBagConstraints);

        jLabelHysteresisRpm.setText("Hysterese Motordrehzahl +/-");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 13);
        jPanSerial.add(jLabelHysteresisRpm, gridBagConstraints);

        jLabelHysteresisRpm2.setText("U/min");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        jPanSerial.add(jLabelHysteresisRpm2, gridBagConstraints);

        jLabelStartRpm.setText("Startmotordrehzahl");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 13);
        jPanSerial.add(jLabelStartRpm, gridBagConstraints);

        jLabelStartRpm2.setText("U/min");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        jPanSerial.add(jLabelStartRpm2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanSerial.add(jtfPeriod, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanSerial.add(jtfHysteresisTime, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanSerial.add(jtfHysteresisKmh, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanSerial.add(jtfStartKmh, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanSerial.add(jtfIdleRpm, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanSerial.add(jtfHysteresisRpm, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanSerial.add(jtfStartRpm, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanSerial.add(jtfIdleKmh, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanEast.add(jPanSerial, gridBagConstraints);

        jPanMain.add(jPanEast);

        getContentPane().add(jPanMain, java.awt.BorderLayout.CENTER);

        jPanButtons.setBackground(new java.awt.Color(255, 255, 255));
        jPanButtons.setLayout(new java.awt.GridBagLayout());

        jbutCancel.setText("Abbrechen");
        jbutCancel.setAlignmentX(1.0F);
        jbutCancel.setPreferredSize(new java.awt.Dimension(125, 29));
        jbutCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbutCancelActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
        jPanButtons.add(jbutCancel, gridBagConstraints);

        jbutOK.setText("Übernehmen");
        jbutOK.setAlignmentX(1.0F);
        jbutOK.setPreferredSize(new java.awt.Dimension(125, 29));
        jbutOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbutOKActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
        jPanButtons.add(jbutOK, gridBagConstraints);

        getContentPane().add(jPanButtons, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jrbDaymodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jrbDaymodeActionPerformed
        setAppearance(!jrbDaymode.isSelected());
    }//GEN-LAST:event_jrbDaymodeActionPerformed

    private void jrbNightmodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jrbNightmodeActionPerformed
        setAppearance(jrbNightmode.isSelected());
    }//GEN-LAST:event_jrbNightmodeActionPerformed

    private void jbutOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbutOKActionPerformed
        try {
            getSwingValues(config);
            saveConfig();
            dispose();
        } catch (NoSuchElementException ex) {
            JOptionPane.showMessageDialog(this, "Bitte alle Felder ausfüllen!", "Fehler!", JOptionPane.ERROR_MESSAGE);
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Fehler!", JOptionPane.ERROR_MESSAGE);
        }
        pressedOK = true;
    }//GEN-LAST:event_jbutOKActionPerformed

    private void jbutCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbutCancelActionPerformed
        pressedOK = false;
        dispose();
    }//GEN-LAST:event_jbutCancelActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the MAC OS X look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if (System.getProperty("os.name").contains("Mac OS X")) {
                    if ("MAC OS X".equals(info.getName())) {
                        javax.swing.UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                } else {
                    if ("Nimbus".equals(info.getName())) {
                        javax.swing.UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MeasureDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MeasureDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MeasureDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MeasureDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(() -> {
            SettingsDialog dialog = new SettingsDialog(new javax.swing.JFrame(), true);
            dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    System.exit(0);
                }
            });
            dialog.setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabelHysteresisKmh;
    private javax.swing.JLabel jLabelHysteresisKmh2;
    private javax.swing.JLabel jLabelHysteresisRpm;
    private javax.swing.JLabel jLabelHysteresisRpm2;
    private javax.swing.JLabel jLabelHysteresisTime;
    private javax.swing.JLabel jLabelHysteresisTime2;
    private javax.swing.JLabel jLabelIdlRpm;
    private javax.swing.JLabel jLabelIdleKmh;
    private javax.swing.JLabel jLabelIdleKmh2;
    private javax.swing.JLabel jLabelIdleRpm2;
    private javax.swing.JLabel jLabelInertia;
    private javax.swing.JLabel jLabelInertia2;
    private javax.swing.JLabel jLabelPeriod;
    private javax.swing.JLabel jLabelPeriod2;
    private javax.swing.JLabel jLabelPngX;
    private javax.swing.JLabel jLabelPngY;
    private javax.swing.JLabel jLabelPower;
    private javax.swing.JLabel jLabelStartKmh;
    private javax.swing.JLabel jLabelStartKmh2;
    private javax.swing.JLabel jLabelStartRpm;
    private javax.swing.JLabel jLabelStartRpm2;
    private javax.swing.JLabel jLabelTorque;
    private javax.swing.JPanel jPanAppearance;
    private javax.swing.JPanel jPanButtons;
    private javax.swing.JPanel jPanCorr;
    private javax.swing.JPanel jPanEast;
    private javax.swing.JPanel jPanMain;
    private javax.swing.JPanel jPanPNG;
    private javax.swing.JPanel jPanPower;
    private javax.swing.JPanel jPanSerial;
    private javax.swing.JPanel jPanWest;
    private javax.swing.ButtonGroup jbgAppearance;
    private javax.swing.ButtonGroup jbgPower;
    private javax.swing.JButton jbutCancel;
    private javax.swing.JButton jbutOK;
    private javax.swing.JRadioButton jrbDaymode;
    private javax.swing.JRadioButton jrbKW;
    private javax.swing.JRadioButton jrbNightmode;
    private javax.swing.JRadioButton jrbPS;
    private javax.swing.JTextField jtfHysteresisKmh;
    private javax.swing.JTextField jtfHysteresisRpm;
    private javax.swing.JTextField jtfHysteresisTime;
    private javax.swing.JTextField jtfIdleKmh;
    private javax.swing.JTextField jtfIdleRpm;
    private javax.swing.JTextField jtfInertia;
    private javax.swing.JTextField jtfPeriod;
    private javax.swing.JTextField jtfPngX;
    private javax.swing.JTextField jtfPngY;
    private javax.swing.JTextField jtfPower;
    private javax.swing.JTextField jtfStartKmh;
    private javax.swing.JTextField jtfStartRpm;
    private javax.swing.JTextField jtfTorque;
    // End of variables declaration//GEN-END:variables
}
