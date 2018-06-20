package gui;

import java.awt.Color;

/**
 *
 * @author emil
 */
public class SettingsDialog extends javax.swing.JDialog {

    /**
     * Creates new form SettingsDialog
     */
    public SettingsDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
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
            
            jtfInertia.setBackground(Color.darkGray);
            jtfInertia.setForeground(Color.white);
            
            jspHysteresisKmh.setBackground(Color.darkGray);
            jspHysteresisRpm.setBackground(Color.darkGray);
            jspHysteresisTime.setBackground(Color.darkGray);
            jspIdleKmh.setBackground(Color.darkGray);
            jspIdleRpm.setBackground(Color.darkGray);
            jspPeriod.setBackground(Color.darkGray);
            jspPower.setBackground(Color.darkGray);
            jspStartKmh.setBackground(Color.darkGray);
            jspStartRpm.setBackground(Color.darkGray);
            jspTorque.setBackground(Color.darkGray);
            
            jspHysteresisKmh.setForeground(Color.white);
            jspHysteresisRpm.setForeground(Color.white);
            jspHysteresisTime.setForeground(Color.white);
            jspIdleKmh.setForeground(Color.white);
            jspIdleRpm.setForeground(Color.white);
            jspPeriod.setForeground(Color.white);
            jspPower.setForeground(Color.white);
            jspStartKmh.setForeground(Color.white);
            jspStartRpm.setForeground(Color.white);
            jspTorque.setForeground(Color.white);
            
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
            
            jspHysteresisKmh.setBackground(Color.white);
            jspHysteresisRpm.setBackground(Color.white);
            jspHysteresisTime.setBackground(Color.white);
            jspIdleKmh.setBackground(Color.white);
            jspIdleRpm.setBackground(Color.white);
            jspPeriod.setBackground(Color.white);
            jspPower.setBackground(Color.white);
            jspStartKmh.setBackground(Color.white);
            jspStartRpm.setBackground(Color.white);
            jspTorque.setBackground(Color.white);
            
            jspHysteresisKmh.setForeground(Color.black);
            jspHysteresisRpm.setForeground(Color.black);
            jspHysteresisTime.setForeground(Color.black);
            jspIdleKmh.setForeground(Color.black);
            jspIdleRpm.setForeground(Color.black);
            jspPeriod.setForeground(Color.black);
            jspPower.setForeground(Color.black);
            jspStartKmh.setForeground(Color.black);
            jspStartRpm.setForeground(Color.black);
            jspTorque.setForeground(Color.black);
            
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
            jLabelPower.setForeground(Color.black);
            jLabelStartKmh.setForeground(Color.black);
            jLabelStartKmh2.setForeground(Color.black);
            jLabelStartRpm.setForeground(Color.black);
            jLabelStartRpm2.setForeground(Color.black);
            jLabelTorque.setForeground(Color.black);
        }
    }

    
    //Getter
    public Object getJcbPNG() {
        return jcbPNG.getSelectedItem();
    }

    public boolean getJrbNightmode() {
        return jrbNightmode.isSelected();
    }

    public boolean getJrbPS() {
        return jrbPS.isSelected();
    }

    public Object getJspHysteresisKmh() {
        return jspHysteresisKmh.getValue();
    }

    public Object getJspHysteresisRpm() {
        return jspHysteresisRpm.getValue();
    }

    public Object getJspHysteresisTime() {
        return jspHysteresisTime.getValue();
    }

    public Object getJspIdleKmh() {
        return jspIdleKmh.getValue();
    }

    public Object getJspIdleRpm() {
        return jspIdleRpm.getValue();
    }

    public Object getJspPeriod() {
        return jspPeriod.getValue();
    }

    public Object getJspPower() {
        return jspPower.getValue();
    }

    public Object getJspStartKmh() {
        return jspStartKmh.getValue();
    }

    public Object getJspStartRpm() {
        return jspStartRpm.getValue();
    }

    public Object getJspTorque() {
        return jspTorque.getValue();
    }

    public String getJtfInertia() {
        return jtfInertia.getText();
    }
    
    
    //Setter
    public void setJcbPNG(Object o) {
        jcbPNG.setSelectedItem(o);
    }

    public void setJrbNightmode(boolean dark) {
        jrbNightmode.setSelected(dark);
    }

    public void setJrbPS(boolean ps) {
        jrbPS.setSelected(ps);
    }

    public void setJspHysteresisKmh(Object o) {
        jspHysteresisKmh.setValue(o);
    }

    public void setJspHysteresisRpm(Object o) {
        jspHysteresisRpm.setValue(o);
    }

    public void setJspHysteresisTime(Object o) {
        jspHysteresisTime.setValue(o);
    }

    public void setJspIdleKmh(Object o) {
        jspIdleKmh.setValue(o);
    }

    public void setJspIdleRpm(Object o) {
        jspIdleRpm.setValue(o);
    }

    public void setJspPeriod(Object o) {
        jspPeriod.setValue(o);
    }

    public void setJspPower(Object o) {
        jspPower.setValue(o);
    }

    public void setJspStartKmh(Object o) {
        jspStartKmh.setValue(o);
    }

    public void setJspStartRpm(Object o) {
        jspStartRpm.setValue(o);
    }

    public void setJspTorque(Object o) {
        jspTorque.setValue(o);
    }

    public void setJtfInertia(String value) {
        jtfInertia.setText(value);
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
        jcbPNG = new javax.swing.JComboBox<>();
        jPanCorr = new javax.swing.JPanel();
        jLabelPower = new javax.swing.JLabel();
        jspPower = new javax.swing.JSpinner();
        jLabelTorque = new javax.swing.JLabel();
        jspTorque = new javax.swing.JSpinner();
        jLabelInertia = new javax.swing.JLabel();
        jtfInertia = new javax.swing.JTextField();
        jLabelInertia2 = new javax.swing.JLabel();
        jPanEast = new javax.swing.JPanel();
        jPanAppearance = new javax.swing.JPanel();
        jrbDaymode = new javax.swing.JRadioButton();
        jrbNightmode = new javax.swing.JRadioButton();
        jPanSerial = new javax.swing.JPanel();
        jLabelPeriod = new javax.swing.JLabel();
        jspPeriod = new javax.swing.JSpinner();
        jLabelPeriod2 = new javax.swing.JLabel();
        jLabelHysteresisTime = new javax.swing.JLabel();
        jspHysteresisTime = new javax.swing.JSpinner();
        jLabelHysteresisTime2 = new javax.swing.JLabel();
        jLabelIdleKmh = new javax.swing.JLabel();
        jspIdleKmh = new javax.swing.JSpinner();
        jLabelIdleKmh2 = new javax.swing.JLabel();
        jLabelHysteresisKmh = new javax.swing.JLabel();
        jspHysteresisKmh = new javax.swing.JSpinner();
        jLabelHysteresisKmh2 = new javax.swing.JLabel();
        jLabelStartKmh = new javax.swing.JLabel();
        jspStartKmh = new javax.swing.JSpinner();
        jLabelStartKmh2 = new javax.swing.JLabel();
        jLabelIdlRpm = new javax.swing.JLabel();
        jspIdleRpm = new javax.swing.JSpinner();
        jLabelIdleRpm2 = new javax.swing.JLabel();
        jLabelHysteresisRpm = new javax.swing.JLabel();
        jspHysteresisRpm = new javax.swing.JSpinner();
        jLabelHysteresisRpm2 = new javax.swing.JLabel();
        jLabelStartRpm = new javax.swing.JLabel();
        jspStartRpm = new javax.swing.JSpinner();
        jLabelStartRpm2 = new javax.swing.JLabel();
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
        jPanPNG.setLayout(new java.awt.GridBagLayout());

        jcbPNG.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jPanPNG.add(jcbPNG, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanWest.add(jPanPNG, gridBagConstraints);

        jPanCorr.setBackground(new java.awt.Color(255, 255, 255));
        jPanCorr.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Korrekturfaktoren", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 0, 13), new java.awt.Color(255, 0, 0))); // NOI18N
        jPanCorr.setLayout(new java.awt.GridBagLayout());

        jLabelPower.setText("Leistung");
        jPanCorr.add(jLabelPower, new java.awt.GridBagConstraints());
        jPanCorr.add(jspPower, new java.awt.GridBagConstraints());

        jLabelTorque.setText("Drehmoment");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        jPanCorr.add(jLabelTorque, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        jPanCorr.add(jspTorque, gridBagConstraints);

        jLabelInertia.setText("Trägheitsmoment");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        jPanCorr.add(jLabelInertia, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
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
        jPanAppearance.setLayout(new java.awt.GridBagLayout());

        jbgAppearance.add(jrbDaymode);
        jrbDaymode.setText("Hell");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 44);
        jPanAppearance.add(jrbDaymode, gridBagConstraints);

        jbgAppearance.add(jrbNightmode);
        jrbNightmode.setText("Dunkel");
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
        jPanSerial.setLayout(new java.awt.GridBagLayout());

        jLabelPeriod.setText("Zeitintervall");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 13);
        jPanSerial.add(jLabelPeriod, gridBagConstraints);

        jspPeriod.setToolTipText("");
        jspPeriod.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jPanSerial.add(jspPeriod, new java.awt.GridBagConstraints());

        jLabelPeriod2.setText("ms");
        gridBagConstraints = new java.awt.GridBagConstraints();
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
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        jPanSerial.add(jspHysteresisTime, gridBagConstraints);

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
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        jPanSerial.add(jspIdleKmh, gridBagConstraints);

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
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        jPanSerial.add(jspHysteresisKmh, gridBagConstraints);

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
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        jPanSerial.add(jspStartKmh, gridBagConstraints);

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
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        jPanSerial.add(jspIdleRpm, gridBagConstraints);

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
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        jPanSerial.add(jspHysteresisRpm, gridBagConstraints);

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
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        jPanSerial.add(jspStartRpm, gridBagConstraints);

        jLabelStartRpm2.setText("U/min");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        jPanSerial.add(jLabelStartRpm2, gridBagConstraints);

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
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
        jPanButtons.add(jbutCancel, gridBagConstraints);

        jbutOK.setText("Übernehmen");
        jbutOK.setAlignmentX(1.0F);
        jbutOK.setPreferredSize(new java.awt.Dimension(125, 29));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
        jPanButtons.add(jbutOK, gridBagConstraints);

        getContentPane().add(jPanButtons, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
    private javax.swing.JComboBox<String> jcbPNG;
    private javax.swing.JRadioButton jrbDaymode;
    private javax.swing.JRadioButton jrbKW;
    private javax.swing.JRadioButton jrbNightmode;
    private javax.swing.JRadioButton jrbPS;
    private javax.swing.JSpinner jspHysteresisKmh;
    private javax.swing.JSpinner jspHysteresisRpm;
    private javax.swing.JSpinner jspHysteresisTime;
    private javax.swing.JSpinner jspIdleKmh;
    private javax.swing.JSpinner jspIdleRpm;
    private javax.swing.JSpinner jspPeriod;
    private javax.swing.JSpinner jspPower;
    private javax.swing.JSpinner jspStartKmh;
    private javax.swing.JSpinner jspStartRpm;
    private javax.swing.JSpinner jspTorque;
    private javax.swing.JTextField jtfInertia;
    // End of variables declaration//GEN-END:variables
}
