package gui;

import data.Bike;
import data.Config;
import data.DialData;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Point;
import java.text.NumberFormat;
import java.util.List;
import logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import measure.MeasurementWorker;
import measure.MeasurementWorker.Status;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.dial.DialBackground;
import org.jfree.chart.plot.dial.DialCap;
import org.jfree.chart.plot.dial.DialPlot;
import org.jfree.chart.plot.dial.DialPointer;
import org.jfree.chart.plot.dial.DialPointer.Pin;
import org.jfree.chart.plot.dial.DialTextAnnotation;
import org.jfree.chart.plot.dial.DialValueIndicator;
import org.jfree.chart.plot.dial.StandardDialFrame;
import org.jfree.chart.plot.dial.StandardDialScale;
import org.jfree.data.general.DefaultValueDataset;
import org.jfree.ui.GradientPaintTransformType;
import org.jfree.ui.StandardGradientPaintTransformer;

/**
 *
 * @author emil
 */
public class MeasureDialog extends javax.swing.JDialog {

    private static final Logger LOG = Logger.getLogger(MeasureDialog.class.getName());

    private boolean finished;

    private final DefaultValueDataset velo = new DefaultValueDataset(0);
    private final DefaultValueDataset rpm = new DefaultValueDataset(0);
    private final DefaultValueDataset engRef = new DefaultValueDataset(0);
    private final DefaultValueDataset wheelRef = new DefaultValueDataset(0);

    private MyMeasurementWorker worker;

    /**
     * Creates new form MeasureDialog
     */
    public MeasureDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);

        setTitle("Messung läuft...");
        setLocationRelativeTo(null);
        setResizable(false);
        setMinimumSize(new Dimension(620, 450));

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
        jLabelVelo1.setText(unit);

        createDial(velo, wheelRef, unit, 0, highScaleEnd, 10);
        if (Bike.getInstance().isMeasRpm()) {
            rpm.setValue(0);
            createDial(rpm, engRef, "U/min x 1000", 0, 15, 1);
        }
        testDials();
        //handleMeasurementChain();

    }

    private void handleMeasurementChain() {
        jpbMeasure.setIndeterminate(true);
        worker = new MyMeasurementWorker();
        worker.setStatus(Status.WAIT);
        worker.execute();
    }

    private void createDial(DefaultValueDataset value, DefaultValueDataset ref, String title, int min, int max, int tick) {

        DialPlot plot = new DialPlot();
        plot.setDataset(0, value);
        plot.setDataset(1, ref);
        plot.setDialFrame(new StandardDialFrame());
        plot.addLayer(new DialPointer.Pointer(0));

        DialPointer.Pin pin = new DialPointer.Pin(1);
        pin.setRadius(0.55000000000000004D);
        plot.addPointer(pin);

        /*
        DialTextAnnotation annotation = new DialTextAnnotation(title);
        annotation.setFont(new Font(null, Font.BOLD, 17));
        if (Config.getInstance().isDark()) {
            annotation.setPaint(new GradientPaint(new Point(), Color.WHITE, new Point(), Color.WHITE));
        } else {
            annotation.setPaint(new GradientPaint(new Point(), Color.BLACK, new Point(), Color.BLACK));
        }
        plot.addLayer(annotation);
         */
        GradientPaint gradientpaint = new GradientPaint(new Point(), new Color(255, 255, 255), new Point(), new Color(170, 170, 220));
        DialBackground dialbackground = new DialBackground(gradientpaint);

        dialbackground.setGradientPaintTransformer(new StandardGradientPaintTransformer(GradientPaintTransformType.VERTICAL));
        plot.setBackground(dialbackground);

        StandardDialScale scale0 = new StandardDialScale(min, max, -120, -300, tick, 4);
        scale0.setTickRadius(0.88);
        scale0.setTickLabelOffset(0.20);
        scale0.setMajorTickPaint(Color.RED);
        scale0.setMinorTickPaint(Color.BLACK);
        scale0.setTickLabelFormatter(NumberFormat.getIntegerInstance());

        plot.addScale(0, scale0);

        DialCap dialcap = new DialCap();
        dialcap.setRadius(0.10000000000000001D);
        plot.setCap(dialcap);

        JFreeChart chart = new JFreeChart(plot);
        if (Config.getInstance().isDark()) {
            chart.setBackgroundPaint(new GradientPaint(new Point(), Color.DARK_GRAY, new Point(), Color.DARK_GRAY));
        } else {
            chart.setBackgroundPaint(new GradientPaint(new Point(), Color.WHITE, new Point(), Color.WHITE));
        }

        chart.setTitle(title);
        if (Config.getInstance().isDark()) {
            chart.getTitle().setPaint(new GradientPaint(new Point(), Color.WHITE, new Point(), Color.WHITE));
        } else {
            chart.getTitle().setPaint(new GradientPaint(new Point(), Color.BLACK, new Point(), Color.BLACK));
        }
        ChartPanel chartPanel = new ChartPanel(chart);

        jPanDials.add(chartPanel);

        /*
        frame.setUI(null);
        frame.add(new ChartPanel(new JFreeChart(plot)));
        frame.pack();
        frame.setSize(500, 500);
         */
    }

    private void testDials() {
        rpm.setValue(0);
        velo.setValue(0);
        engRef.setValue(Config.getInstance().getStopRpm() / 1000);
        wheelRef.setValue(Config.getInstance().getStopVelo() / 1000);
    }

    //Sets Appearance like at the Main-GUI
    public void setAppearance(boolean dark) {
        if (dark == true) {
            jPanControls.setBackground(Color.darkGray);
            jPanDial.setBackground(Color.darkGray);
            jPanMain.setBackground(Color.darkGray);
            jPanStatus.setBackground(Color.darkGray);
            jPanStatusColour.setBackground(Color.darkGray);
            jPanStatusText.setBackground(Color.darkGray);
            jPanVelo.setBackground(Color.darkGray);
            jPanRPM.setBackground(Color.darkGray);
            jPanDials.setBackground(Color.darkGray);

            jLabelCount.setForeground(Color.white);
            jLabelStatus.setForeground(Color.white);
            jLabelStatusT.setForeground(Color.white);
            jLabelVelo.setForeground(Color.white);
            jLabelVelo1.setForeground(Color.white);
            jLabelRPM.setForeground(Color.white);
            jLabelRPM1.setForeground(Color.white);
        } else {
            jPanControls.setBackground(Color.white);
            jPanDial.setBackground(Color.white);
            jPanMain.setBackground(Color.white);
            jPanStatus.setBackground(Color.white);
            jPanStatusColour.setBackground(Color.white);
            jPanStatusText.setBackground(Color.white);
            jPanVelo.setBackground(Color.white);
            jPanRPM.setBackground(Color.white);
            jPanDials.setBackground(Color.white);

            jLabelCount.setForeground(Color.black);
            jLabelStatus.setForeground(Color.black);
            jLabelStatusT.setForeground(Color.black);
            jLabelVelo.setForeground(Color.black);
            jLabelVelo1.setForeground(Color.black);
            jLabelRPM.setForeground(Color.black);
            jLabelRPM1.setForeground(Color.black);
        }
    }

    public boolean isFinished() {
        return finished;
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
        jLabelCount = new javax.swing.JLabel();
        jpbMeasure = new javax.swing.JProgressBar();
        jLabelStatusT = new javax.swing.JLabel();
        jLabelStatus = new javax.swing.JLabel();
        jPanStatusColour = new javax.swing.JPanel();
        jPanDial = new javax.swing.JPanel();
        jPanVelo = new javax.swing.JPanel();
        jLabelVelo = new javax.swing.JLabel();
        jLabelVelo1 = new javax.swing.JLabel();
        jPanRPM = new javax.swing.JPanel();
        jLabelRPM = new javax.swing.JLabel();
        jLabelRPM1 = new javax.swing.JLabel();
        jPanDials = new javax.swing.JPanel();
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

        jLabelCount.setText("Anzahl der Messpunkte: ");
        jLabelCount.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        jPanStatusText.add(jLabelCount, gridBagConstraints);

        jpbMeasure.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jpbMeasure.setIndeterminate(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanStatusText.add(jpbMeasure, gridBagConstraints);

        jLabelStatusT.setText("Status: ");
        jLabelStatusT.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        jPanStatusText.add(jLabelStatusT, gridBagConstraints);

        jLabelStatus.setText("HOCHSCHALTEN");
        jLabelStatus.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        jPanStatusText.add(jLabelStatus, gridBagConstraints);

        jPanStatus.add(jPanStatusText);

        jPanStatusColour.setBackground(new java.awt.Color(255, 255, 255));
        jPanStatusColour.setLayout(new java.awt.GridLayout(1, 0));
        jPanStatus.add(jPanStatusColour);

        jPanMain.add(jPanStatus, java.awt.BorderLayout.NORTH);

        jPanDial.setBackground(new java.awt.Color(255, 255, 255));
        jPanDial.setLayout(new java.awt.GridBagLayout());

        jPanVelo.setBackground(new java.awt.Color(255, 255, 255));

        jLabelVelo.setFont(new java.awt.Font("Lucida Grande", 1, 24)); // NOI18N
        jLabelVelo.setText("50");
        jPanVelo.add(jLabelVelo);

        jLabelVelo1.setFont(new java.awt.Font("Lucida Grande", 1, 24)); // NOI18N
        jLabelVelo1.setText("km/h");
        jPanVelo.add(jLabelVelo1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanDial.add(jPanVelo, gridBagConstraints);

        jPanRPM.setBackground(new java.awt.Color(255, 255, 255));

        jLabelRPM.setFont(new java.awt.Font("Lucida Grande", 1, 24)); // NOI18N
        jLabelRPM.setText("12000");
        jPanRPM.add(jLabelRPM);

        jLabelRPM1.setFont(new java.awt.Font("Lucida Grande", 1, 24)); // NOI18N
        jLabelRPM1.setText("U/min");
        jPanRPM.add(jLabelRPM1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanDial.add(jPanRPM, gridBagConstraints);

        jPanDials.setBackground(new java.awt.Color(255, 255, 255));
        jPanDials.setLayout(new java.awt.GridLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanDial.add(jPanDials, gridBagConstraints);

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
        int answ = JOptionPane.showConfirmDialog(this, "Möchten Sie die Messung abbrechen?", "Abbruch der Messung", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (answ == JOptionPane.YES_OPTION) {
            worker.cancel(true);
            finished = false;
            dispose();
        }
    }//GEN-LAST:event_jbutCancelActionPerformed

    private class MyMeasurementWorker extends MeasurementWorker {

        @Override
        protected void done() {
        }

        @Override
        protected void process(List<DialData> chunks) {
            for (DialData dd : chunks) {
                rpm.setValue(dd.getEngRpm() / 1000);
                velo.setValue(dd.getWheelVelo());
                engRef.setValue(dd.getEngRef() / 1000);
                wheelRef.setValue(dd.getWheelRef());
                
                jLabelVelo.setText(String.format("%.1f", dd.getWheelVelo()));
                
                if (Bike.getInstance().isMeasRpm()) {
                    jLabelRPM.setText(String.format("%d", dd.getEngRpm()));
                }
                
                try {
                    jLabelStatus.setText(dd.getStatusText());
                } catch (Exception ex) {
                    LOG.warning(ex);
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
    private javax.swing.JLabel jLabelRPM;
    private javax.swing.JLabel jLabelRPM1;
    private javax.swing.JLabel jLabelStatus;
    private javax.swing.JLabel jLabelStatusT;
    private javax.swing.JLabel jLabelVelo;
    private javax.swing.JLabel jLabelVelo1;
    private javax.swing.JPanel jPanControls;
    private javax.swing.JPanel jPanDial;
    private javax.swing.JPanel jPanDials;
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
