package gui;

import data.Bike;
import data.Config;
import data.Database;
import data.DialData;
import development.TestCSV;
import eu.hansolo.steelseries.gauges.Linear;
import eu.hansolo.steelseries.gauges.Radial;
import eu.hansolo.steelseries.tools.ColorDef;
import eu.hansolo.steelseries.tools.FrameDesign;
import eu.hansolo.steelseries.tools.KnobStyle;
import eu.hansolo.steelseries.tools.KnobType;
import eu.hansolo.steelseries.tools.NumberFormat;
import eu.hansolo.steelseries.tools.ThresholdType;
import eu.hansolo.steelseries.tools.TicklabelOrientation;
import eu.hansolo.steelseries.tools.TickmarkType;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import logging.Logger;
import javax.swing.JOptionPane;
import main.BESDyno;
import measure.MeasurementWorker;
import measure.MeasurementWorker.Status;

/**
 *
 * @author emil
 */
public class MeasureDialog extends javax.swing.JDialog {

    private static final Logger LOG = Logger.getLogger(MeasureDialog.class.getName());

    private boolean finished = false;

    private final Radial veloGauge = new Radial();
    private final Radial rpmGauge = new Radial();

    private final Linear engThermo = new Linear();
    private final Linear exhThermo = new Linear();

    private int count = 0;

    private boolean warning = false;

    private MyMeasurementWorker worker;

    /**
     * Creates new form MeasureDialog
     */
    public MeasureDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);

        setTitle("Messung: " + Bike.getInstance().getVehicleName());
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(0);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (!finished) {
                    handleCanceling();
                }
            }
        });

        initComponents();

        String unit = null;
        int highScaleEnd = 150;
        switch (Config.getInstance().getVelocity()) {
            case MPS:
                unit = "m/s";
                highScaleEnd = 70;
                break;
            case MIH:
                unit = "mi/h";
                highScaleEnd = 120;
                break;
            case KMH:
                unit = "km/h";
                highScaleEnd = 150;
                break;
        }

        createVelocityGauge(highScaleEnd, unit);
        if (Bike.getInstance().isMeasRpm()) {
            createRPMGauge();
        } else {
            jPanDial.remove(jPanRPM);
            setSize(new Dimension(500, 500));
        }
        if (Bike.getInstance().isMeasTemp()) {
            setSize(new Dimension(1330, 500));
            createEngThermo();
            createExhThermo();
        } else {
            jPanDial.remove(jPanEngThermo);
            jPanDial.remove(jPanExhThermo);
            setSize(new Dimension(620, 500));
        }
        LOG.info("Start Measurement Chain");
        handleMeasurementChain();

    }

    private void handleMeasurementChain() {
        jpbMeasure.setIndeterminate(true);
        worker = new MyMeasurementWorker();
        worker.execute();
    }

    private void updateGaugeValue(Radial gauge, double value, double ref, Color colour) {
        gauge.setValue(value);
        gauge.setLcdValue(value);
        gauge.setThreshold(ref);
        if (warning) {
            gauge.setGlowColor(Color.RED);
        } else {
            gauge.setGlowColor(colour);
        }
    }

    private void updateThermoValue(Linear thermo, double value, double max, Color colour) {
        thermo.setValue(value);
            if (value > max) {
                thermo.setGlowColor(Color.RED);
                setWarningAppearance();
                warning = true;
            } else {
            thermo.setGlowColor(colour);
        }
    }

    private Color updateColour(Status status) {
        switch (status) {
            case SHIFT_UP:
                return Color.CYAN;
            case WAIT:
                return Color.ORANGE;
            case READY:
                return new Color(30, 200, 30);
            case MEASURE:
                return new Color(30, 200, 30);
            case FINISH:
                return Color.RED;
            default:
                return Color.CYAN;
        }
    }

    private void createVelocityGauge(double maxValue, String unit) {

        veloGauge.setTitle("Velocity");
        veloGauge.setUnitString(unit);

        //veloGauge.setArea3DEffectVisible(true);
        if (Config.getInstance().isDark()) {
            veloGauge.setFrameDesign(FrameDesign.CHROME);
        } else {
            veloGauge.setFrameDesign(FrameDesign.BLACK_METAL);
        }

        veloGauge.setNiceScale(true);
        veloGauge.setLedVisible(false);

        veloGauge.setGlowing(true);
        veloGauge.setGlowColor(Color.CYAN);
        veloGauge.setGlowVisible(true);

        veloGauge.setKnobStyle(KnobStyle.SILVER);
        veloGauge.setKnobType(KnobType.METAL_KNOB);

        veloGauge.setMaxValue(maxValue);
        veloGauge.setMajorTickSpacing(10);
        veloGauge.setMinorTickSpacing(5);
        veloGauge.setMajorTickmarkType(TickmarkType.TRIANGLE);
        veloGauge.setTickmarkColor(Color.RED);
        veloGauge.setTicklabelOrientation(TicklabelOrientation.HORIZONTAL);
        veloGauge.setLabelNumberFormat(eu.hansolo.steelseries.tools.NumberFormat.STANDARD);
        veloGauge.setLabelColor(Color.RED);
        veloGauge.setMaxMeasuredValueVisible(true);

        veloGauge.setThresholdColor(ColorDef.ORANGE);
        veloGauge.setThresholdType(ThresholdType.ARROW);
        veloGauge.setThresholdVisible(true);

        jPanVelo.setSize(new Dimension(500, 500));
        jPanVelo.add(veloGauge, BorderLayout.CENTER);
    }

    private void createRPMGauge() {

        rpmGauge.setTitle("RPM");
        rpmGauge.setUnitString("U/min x 1000");

        rpmGauge.setArea3DEffectVisible(true);

        if (Config.getInstance().isDark()) {
            rpmGauge.setFrameDesign(FrameDesign.CHROME);
        } else {
            rpmGauge.setFrameDesign(FrameDesign.BLACK_METAL);
        }

        rpmGauge.setNiceScale(true);
        rpmGauge.setLedVisible(false);

        rpmGauge.setGlowing(true);
        rpmGauge.setGlowColor(Color.CYAN);
        rpmGauge.setGlowVisible(true);

        rpmGauge.setKnobStyle(KnobStyle.SILVER);
        rpmGauge.setKnobType(KnobType.METAL_KNOB);

        rpmGauge.setMaxValue(15);
        rpmGauge.setMajorTickSpacing(1);
        rpmGauge.setMinorTickSpacing(0.1);
        rpmGauge.setMajorTickmarkType(TickmarkType.TRIANGLE);
        rpmGauge.setTickmarkColor(Color.RED);
        rpmGauge.setTicklabelOrientation(TicklabelOrientation.HORIZONTAL);
        rpmGauge.setLabelNumberFormat(eu.hansolo.steelseries.tools.NumberFormat.STANDARD);
        rpmGauge.setLabelColor(Color.RED);
        rpmGauge.setMaxMeasuredValueVisible(true);

        rpmGauge.setLcdDecimals(3);

        jPanRPM.setSize(new Dimension(500, 500));
        jPanRPM.add(rpmGauge, BorderLayout.CENTER);
    }

    private void createEngThermo() {
        engThermo.setTitle("Motortemperatur");
        engThermo.setUnitString(Config.getInstance().getTempUnit());

        if (Config.getInstance().isDark()) {
            engThermo.setFrameDesign(FrameDesign.CHROME);
        } else {
            engThermo.setFrameDesign(FrameDesign.BLACK_METAL);
        }
        
        engThermo.setNiceScale(true);
        engThermo.setLedVisible(false);
        engThermo.setLcdVisible(false);
        
        if (Config.getInstance().isCelcius()) {
            engThermo.setMaxValue(150);
            engThermo.setMajorTickSpacing(20);
            engThermo.setMinorTickSpacing(10);
        } else {
            engThermo.setMaxValue(300);
            engThermo.setMajorTickSpacing(50);
            engThermo.setMinorTickSpacing(25);
        }
        
        engThermo.setMajorTickmarkType(TickmarkType.TRIANGLE);
        engThermo.setTickmarkColor(Color.RED);
        engThermo.setTicklabelsVisible(true);
        engThermo.setLabelNumberFormat(NumberFormat.STANDARD);
        engThermo.setLabelColor(Color.RED);

        jPanEngThermo.add(engThermo, BorderLayout.CENTER);
    }
    
    private void createExhThermo() {
        exhThermo.setTitle("Abgastemperatur");
        exhThermo.setUnitString(Config.getInstance().getTempUnit());

        if (Config.getInstance().isDark()) {
            exhThermo.setFrameDesign(FrameDesign.CHROME);
        } else {
            exhThermo.setFrameDesign(FrameDesign.BLACK_METAL);
        }

        exhThermo.setNiceScale(true);
        exhThermo.setLedVisible(false);
        exhThermo.setLcdVisible(false);
        
        if (Config.getInstance().isCelcius()) {
            exhThermo.setMaxValue(750);
            exhThermo.setMajorTickSpacing(50);
            exhThermo.setMinorTickSpacing(25);
        } else {
            exhThermo.setMaxValue(1300);
            exhThermo.setMajorTickSpacing(100);
            exhThermo.setMinorTickSpacing(50);
        }
        
        exhThermo.setMajorTickmarkType(TickmarkType.TRIANGLE);
        exhThermo.setTickmarkColor(Color.RED);
        exhThermo.setTicklabelsVisible(true);
        exhThermo.setLabelNumberFormat(NumberFormat.STANDARD);
        exhThermo.setLabelColor(Color.RED);

        jPanExhThermo.add(exhThermo, BorderLayout.CENTER);
    }
    
    private double convertToFahrenheit(double celcius) {
        return (celcius * (9.0 / 5.0)) + 32.0;
    }

    private void setWarningAppearance() {
        jPanControls.setBackground(Color.RED);
        jPanDial.setBackground(Color.RED);
        jPanMain.setBackground(Color.RED);
        jPanStatus.setBackground(Color.RED);
        jPanStatusText.setBackground(Color.RED);
        jPanVelo.setBackground(Color.RED);
        jPanRPM.setBackground(Color.RED);
        jPanEngThermo.setBackground(Color.RED);
        jPanExhThermo.setBackground(Color.RED);
    }

    //Sets Appearance like at the Main-GUI
    public void setAppearance(boolean dark) {
        if (dark) {
            jPanControls.setBackground(Color.darkGray);
            jPanDial.setBackground(Color.darkGray);
            jPanMain.setBackground(Color.darkGray);
            jPanStatus.setBackground(Color.darkGray);
            jPanStatusText.setBackground(Color.darkGray);
            jPanVelo.setBackground(Color.darkGray);
            jPanRPM.setBackground(Color.darkGray);
            jPanEngThermo.setBackground(Color.darkGray);
            jPanExhThermo.setBackground(Color.darkGray);

            jLabelCount.setForeground(Color.white);
            jLabelCountT.setForeground(Color.white);
            jLabelStatus.setForeground(Color.white);
            jLabelStatusT.setForeground(Color.white);
        } else {
            jPanControls.setBackground(Color.white);
            jPanDial.setBackground(Color.white);
            jPanMain.setBackground(Color.white);
            jPanStatus.setBackground(Color.white);
            jPanStatusText.setBackground(Color.white);
            jPanVelo.setBackground(Color.white);
            jPanRPM.setBackground(Color.white);
            jPanEngThermo.setBackground(Color.white);
            jPanExhThermo.setBackground(Color.white);

            jLabelCount.setForeground(Color.black);
            jLabelCountT.setForeground(Color.black);
            jLabelStatus.setForeground(Color.black);
            jLabelStatusT.setForeground(Color.black);
        }
    }

    public boolean isFinished() {
        return finished;
    }

    private void handleCanceling() {
        int answ = JOptionPane.showConfirmDialog(this, "Möchten Sie die Messung abbrechen?", "Abbruch der Messung", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (answ == JOptionPane.YES_OPTION) {
            worker.cancel(true);
            finished = false;
            dispose();
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

        jPanMain = new javax.swing.JPanel();
        jPanStatus = new javax.swing.JPanel();
        jPanStatusText = new javax.swing.JPanel();
        jLabelCountT = new javax.swing.JLabel();
        jpbMeasure = new javax.swing.JProgressBar();
        jLabelStatusT = new javax.swing.JLabel();
        jLabelStatus = new javax.swing.JLabel();
        jLabelCount = new javax.swing.JLabel();
        jPanStatusColour = new javax.swing.JPanel();
        jLabelDo = new javax.swing.JLabel();
        jPanDial = new javax.swing.JPanel();
        jPanRPM = new javax.swing.JPanel();
        jPanVelo = new javax.swing.JPanel();
        jPanEngThermo = new javax.swing.JPanel();
        jPanExhThermo = new javax.swing.JPanel();
        jPanControls = new javax.swing.JPanel();
        jbutCancel = new javax.swing.JButton();
        jbutFinish = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));

        jPanMain.setBackground(new java.awt.Color(255, 255, 255));
        jPanMain.setLayout(new java.awt.BorderLayout());

        jPanStatus.setBackground(new java.awt.Color(255, 255, 255));
        jPanStatus.setLayout(new java.awt.GridLayout(2, 1));

        jPanStatusText.setBackground(new java.awt.Color(255, 255, 255));
        jPanStatusText.setLayout(new java.awt.GridBagLayout());

        jLabelCountT.setText("Anzahl der Messpunkte: ");
        jLabelCountT.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        jPanStatusText.add(jLabelCountT, gridBagConstraints);

        jpbMeasure.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jpbMeasure.setIndeterminate(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanStatusText.add(jpbMeasure, gridBagConstraints);

        jLabelStatusT.setText("Status: ");
        jLabelStatusT.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        jPanStatusText.add(jLabelStatusT, gridBagConstraints);

        jLabelStatus.setText("HOCHSCHALTEN");
        jLabelStatus.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        jPanStatusText.add(jLabelStatus, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        jPanStatusText.add(jLabelCount, gridBagConstraints);

        jPanStatus.add(jPanStatusText);

        jPanStatusColour.setBackground(new java.awt.Color(255, 255, 255));
        jPanStatusColour.setLayout(new java.awt.GridLayout(1, 0));

        jLabelDo.setFont(new java.awt.Font("Lucida Grande", 0, 16)); // NOI18N
        jLabelDo.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jPanStatusColour.add(jLabelDo);

        jPanStatus.add(jPanStatusColour);

        jPanMain.add(jPanStatus, java.awt.BorderLayout.NORTH);

        jPanDial.setBackground(new java.awt.Color(255, 255, 255));
        jPanDial.setLayout(new java.awt.GridLayout(1, 0));

        jPanRPM.setBackground(new java.awt.Color(255, 255, 255));
        jPanRPM.setLayout(new java.awt.BorderLayout());
        jPanDial.add(jPanRPM);

        jPanVelo.setBackground(new java.awt.Color(255, 255, 255));
        jPanVelo.setLayout(new java.awt.BorderLayout());
        jPanDial.add(jPanVelo);

        jPanEngThermo.setBackground(new java.awt.Color(255, 255, 255));
        jPanEngThermo.setLayout(new java.awt.BorderLayout());
        jPanDial.add(jPanEngThermo);

        jPanExhThermo.setBackground(new java.awt.Color(255, 255, 255));
        jPanExhThermo.setLayout(new java.awt.BorderLayout());
        jPanDial.add(jPanExhThermo);

        jPanMain.add(jPanDial, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanMain, java.awt.BorderLayout.CENTER);

        jPanControls.setBackground(new java.awt.Color(255, 255, 255));

        jbutCancel.setText("Abbrechen");
        jbutCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbutCancelActionPerformed(evt);
            }
        });
        jPanControls.add(jbutCancel);

        jbutFinish.setText("Fertigstellen");
        jbutFinish.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbutFinishActionPerformed(evt);
            }
        });
        jPanControls.add(jbutFinish);

        getContentPane().add(jPanControls, java.awt.BorderLayout.PAGE_END);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbutFinishActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbutFinishActionPerformed
        finished = true;
        dispose();
    }//GEN-LAST:event_jbutFinishActionPerformed

    private void jbutCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbutCancelActionPerformed
        handleCanceling();
    }//GEN-LAST:event_jbutCancelActionPerformed

    private class MyMeasurementWorker extends MeasurementWorker {

        @Override
        protected void done() {
            if (BESDyno.getInstance().isTestMode()) {
                TestCSV csv = new TestCSV();
                csv.writeFiles();
            }

            if (!isCancelled()) {
                int answ = JOptionPane.showConfirmDialog(MeasureDialog.this, "Möchten Sie die Messung abschließen?", "Fertigstellen", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (answ == JOptionPane.YES_OPTION) {
                    finished = true;
                    dispose();
                }
            }
        }

        @Override
        protected void process(List<DialData> chunks) {

            for (DialData dd : chunks) {

                updateGaugeValue(rpmGauge, dd.getEngRpm() / 1000.0, dd.getEngRef() / 1000.0, updateColour(dd.getStatus()));
                updateGaugeValue(veloGauge, dd.getWheelVelo(), dd.getWheelRef(), updateColour(dd.getStatus()));
                
                if (Bike.getInstance().isMeasTemp()) {
                    double engTemp = Config.getInstance().isCelcius() ? dd.getEngTemp() : convertToFahrenheit(dd.getEngTemp());
                    double exhTemp = Config.getInstance().isCelcius() ? dd.getFumeTemp() : convertToFahrenheit(dd.getFumeTemp());
                    updateThermoValue(engThermo, engTemp, Config.getInstance().getWarningEngTemp(), updateColour(dd.getStatus()));
                    updateThermoValue(exhThermo, exhTemp, Config.getInstance().getWarningExhTemp(), updateColour(dd.getStatus()));
                }

                count++;
                jLabelCount.setText(String.format("%d", count));

                try {
                    jLabelStatus.setText(dd.getStatusText());
                } catch (Exception ex) {
                    LOG.warning(ex);
                }

                switch (dd.getStatus()) {
                    case SHIFT_UP:
                        jPanStatusColour.setBackground(Color.CYAN);
                        if (count < 10) {
                            jLabelDo.setText("Gas geben und in den letzten Gang schalten...");
                        } else {
                            if (Bike.getInstance().isMeasRpm()) {
                                jLabelDo.setText("Fortsetzen: UNTER " + Config.getInstance().getStartRpm() + " U/min gelangen (oranger Pfeil)!");
                            } else {
                                jLabelDo.setText("Fortsetzen: UNTER " + Config.getInstance().getStartVelo() + " " + Config.getInstance().getVeloUnit() + " gelangen (oranger Pfeil)!");
                            }
                        }
                        break;
                    case WAIT:
                        jPanStatusColour.setBackground(Color.ORANGE);
                        if (Bike.getInstance().isMeasRpm()) {
                            jLabelDo.setText("Initialisieren... Drehzahl UNTER " + Config.getInstance().getIdleRpm() + " U/min halten, ±" + Config.getInstance().getHysteresisRpm() + " U/min");
                        } else {
                            jLabelDo.setText("Initialisieren... Drehzahl UNTER " + Config.getInstance().getIdleVelo() + " "
                                    + Config.getInstance().getVeloUnit() + " halten, ±" + Config.getInstance().getHysteresisVelo() + " " + Config.getInstance().getVeloUnit());
                        }
                        break;
                    case READY:
                        jPanStatusColour.setBackground(new Color(30, 200, 30));
                        jLabelDo.setText("Bereit - Gas geben!");
                        break;
                    case MEASURE:
                        jPanStatusColour.setBackground(new Color(30, 200, 30));
                        if (Bike.getInstance().isStartStopMethod()) {
                            if (Bike.getInstance().isMeasRpm()) {
                                jLabelDo.setText("Abschließen: ÜBER " + Config.getInstance().getStopRpm() + " U/min gelangen!");
                            } else {
                                jLabelDo.setText("Abschließen: ÜBER " + Config.getInstance().getStopVelo() + " " + Config.getInstance().getVeloUnit() + " gelangen!");
                            }
                        } else {
                            String preText;
                            if (Database.getInstance().getSchleppPreList().size() > 0) {
                                preText = "Abfallen lassen!";
                            } else {
                                preText = "Gas geben!";
                            }

                            if (Bike.getInstance().isMeasRpm()) {
                                jLabelDo.setText(preText + " | Abschließen: UNTER " + Config.getInstance().getStartRpm() + " U/min gelangen!");
                            } else {
                                jLabelDo.setText(preText + " | Abschließen: UNTER " + Config.getInstance().getStartVelo() + " " + Config.getInstance().getVeloUnit() + " gelangen!");
                            }

                        }
                        break;
                    case FINISH:
                        jPanStatusColour.setBackground(Color.RED);
                        jLabelDo.setText("Messung des Zweirads " + Bike.getInstance().getVehicleName() + " ist abgeschlossen...");
                        break;
                }

            }
        }

    }

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
                } else if (System.getProperty("os.name").startsWith("Windows ")) {
                    if ("com.sun.java.swing.plaf.windows.WindowsLookAndFeel".equals(info.getName())) {
                        javax.swing.UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                } else if (System.getProperty("os.name").startsWith("Linux")) {
                    if ("Nimbus".equals(info.getName())) {
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
            MeasureDialog dialog = new MeasureDialog(new javax.swing.JFrame(), true);
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
    private javax.swing.JLabel jLabelCount;
    private javax.swing.JLabel jLabelCountT;
    private javax.swing.JLabel jLabelDo;
    private javax.swing.JLabel jLabelStatus;
    private javax.swing.JLabel jLabelStatusT;
    private javax.swing.JPanel jPanControls;
    private javax.swing.JPanel jPanDial;
    private javax.swing.JPanel jPanEngThermo;
    private javax.swing.JPanel jPanExhThermo;
    private javax.swing.JPanel jPanMain;
    private javax.swing.JPanel jPanRPM;
    private javax.swing.JPanel jPanStatus;
    private javax.swing.JPanel jPanStatusColour;
    private javax.swing.JPanel jPanStatusText;
    private javax.swing.JPanel jPanVelo;
    private javax.swing.JButton jbutCancel;
    private javax.swing.JButton jbutFinish;
    private javax.swing.JProgressBar jpbMeasure;
    // End of variables declaration//GEN-END:variables
}
