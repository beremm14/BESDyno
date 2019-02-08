package gui;

import data.Bike;
import data.Config;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JOptionPane;
import main.BESDyno;
import main.BESDyno.OS;

/**
 *
 * @author emil
 */
public class VehicleSetDialog extends javax.swing.JDialog {

    private boolean pressedOK;

    /**
     * Creates new form VehicleSetDialog
     */
    public VehicleSetDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setTitle("Zweirad registrieren...");
        setSize(new Dimension(330, 315));
        if (BESDyno.getInstance().getOs() == OS.LINUX) {
            setSize(new Dimension(350, 330));
        }
        setLocationRelativeTo(null);
        setResizable(false);
    }

    //Getter
    public boolean isPressedOK() {
        return pressedOK;
    }

    //Sets Appearance like at the Main-GUI
    public void setAppearance(boolean dark) {
        if (dark) {
            jPanButtons.setBackground(Color.darkGray);
            jPanControls.setBackground(Color.darkGray);
            jPanMain.setBackground(Color.darkGray);
            jPanName.setBackground(Color.darkGray);
            jPanRPM.setBackground(Color.darkGray);
            jPanStroke.setBackground(Color.darkGray);
            jPanTransmission.setBackground(Color.darkGray);
            jPanMethod.setBackground(Color.darkGray);

            jcbEnableRPM.setForeground(Color.white);
            jcbEnableTemp.setForeground(Color.white);
            jrb2Stroke.setForeground(Color.white);
            jrb4Stroke.setForeground(Color.white);
            jrbAutomatic.setForeground(Color.white);
            jrbManual.setForeground(Color.white);
            jrbStartStop.setForeground(Color.white);
            jrbDrop.setForeground(Color.white);
            
            jcbEnableRPM.setBackground(Color.darkGray);
            jcbEnableTemp.setBackground(Color.darkGray);
            jrb2Stroke.setBackground(Color.darkGray);
            jrb4Stroke.setBackground(Color.darkGray);
            jrbAutomatic.setBackground(Color.darkGray);
            jrbManual.setBackground(Color.darkGray);
            jrbStartStop.setBackground(Color.darkGray);
            jrbDrop.setBackground(Color.darkGray);

            jtfVehicleName.setBackground(Color.darkGray);

            jtfVehicleName.setForeground(Color.white);
        } else {
            jPanButtons.setBackground(Color.white);
            jPanControls.setBackground(Color.white);
            jPanMain.setBackground(Color.white);
            jPanName.setBackground(Color.white);
            jPanRPM.setBackground(Color.white);
            jPanStroke.setBackground(Color.white);
            jPanTransmission.setBackground(Color.white);
            jPanMethod.setBackground(Color.white);

            jcbEnableRPM.setForeground(Color.black);
            jcbEnableTemp.setForeground(Color.black);
            jrb2Stroke.setForeground(Color.black);
            jrb4Stroke.setForeground(Color.black);
            jrbAutomatic.setForeground(Color.black);
            jrbManual.setForeground(Color.black);
            jrbStartStop.setForeground(Color.black);
            jrbDrop.setForeground(Color.black);
            
            jcbEnableRPM.setBackground(Color.white);
            jcbEnableTemp.setBackground(Color.white);
            jrb2Stroke.setBackground(Color.white);
            jrb4Stroke.setBackground(Color.white);
            jrbAutomatic.setBackground(Color.white);
            jrbManual.setBackground(Color.white);
            jrbStartStop.setBackground(Color.white);
            jrbDrop.setBackground(Color.white);

            jtfVehicleName.setBackground(Color.white);

            jtfVehicleName.setForeground(Color.black);
        }
    }

    //writeOut data
    private void confirm() {
        boolean error = false;

        if ((!jrb2Stroke.isSelected() && !jrb4Stroke.isSelected())
                || (!jrbAutomatic.isSelected() && !jrbManual.isSelected())) {
            JOptionPane.showMessageDialog(this, "Bitte alle Felder anwählen...", "Eingabefehler...", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Bike.getInstance().setMeasRpm(jcbEnableRPM.isSelected());
        Bike.getInstance().setMeasTemp(jcbEnableTemp.isSelected());
        Bike.getInstance().setTwoStroke(jrb2Stroke.isSelected());
        Bike.getInstance().setAutomatic(jrbAutomatic.isSelected());
        Bike.getInstance().setStartStopMethode(jrbStartStop.isSelected());

        if (jtfVehicleName.getText().length() > 25) {
            error = true;
            JOptionPane.showMessageDialog(this,
                    "Fahrzeugname darf nur maximal 25 Zeichen lang sein\n"
                    + "Aktuelle Zeichenlänge: "
                    + jtfVehicleName.getName().length(),
                    "Fahrzeugname zu lang!",
                    JOptionPane.ERROR_MESSAGE);
            jtfVehicleName.requestFocusInWindow();
        } else {
            if (jtfVehicleName.getText().isEmpty()) {
                error = true;
                JOptionPane.showMessageDialog(this,
                        "Bitte Fahrzeugnamen eingeben!",
                        "Kein Fahrzeugname eingegeben",
                        JOptionPane.ERROR_MESSAGE);
                jtfVehicleName.requestFocusInWindow();
            } else {
                Bike.getInstance().setVehicleName(jtfVehicleName.getText());
            }
        }

        if (Bike.getInstance().isMeasTemp()) {
            int answ = JOptionPane.showConfirmDialog(VehicleSetDialog.this, "Die kontinuierliche Messung der Temperaturen am Motorrad\n"
                    + "wird nur auf leistungsstarken Endgeräten empfohlen\n"
                    + "und kann die Performance der Messung beeinflussen.\n\n"
                    + "Möchten Sie die Temperaturen messen?", "Warnung: Eventueller Performance-Einbruch", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (answ == JOptionPane.NO_OPTION) {
                Bike.getInstance().setMeasTemp(false);
            }
        }

        if (Config.getInstance().getPeriod() < 20) {
            int answ = JOptionPane.showConfirmDialog(VehicleSetDialog.this, "Ein Zeitintervall von <20ms trägt zu einer besseren Messung bei,\n"
                    + "kann aber schwache Endgeräte stark auslasten und zum Absturz führen.\n\n"
                    + "Möchten Sie fortfahren?", "Warnung: Eventueller Performance-Einbruch", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (answ == JOptionPane.NO_OPTION) {
                BESDyno.getInstance().getSettingsDialog().setVisible(true);
                BESDyno.getInstance().getSettingsDialog().changePeriodTime();
                error = true;
                dispose();
            }
        }

        if (!error) {
            pressedOK = true;
            dispose();
        } else {
            pressedOK = false;
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

        bgStroke = new javax.swing.ButtonGroup();
        bgTransmission = new javax.swing.ButtonGroup();
        bgMethod = new javax.swing.ButtonGroup();
        jPanMain = new javax.swing.JPanel();
        jPanName = new javax.swing.JPanel();
        jtfVehicleName = new javax.swing.JTextField();
        jPanMethod = new javax.swing.JPanel();
        jrbStartStop = new javax.swing.JRadioButton();
        jrbDrop = new javax.swing.JRadioButton();
        jPanRPM = new javax.swing.JPanel();
        jcbEnableRPM = new javax.swing.JCheckBox();
        jcbEnableTemp = new javax.swing.JCheckBox();
        jPanStroke = new javax.swing.JPanel();
        jrb2Stroke = new javax.swing.JRadioButton();
        jrb4Stroke = new javax.swing.JRadioButton();
        jPanTransmission = new javax.swing.JPanel();
        jrbManual = new javax.swing.JRadioButton();
        jrbAutomatic = new javax.swing.JRadioButton();
        jPanControls = new javax.swing.JPanel();
        jPanButtons = new javax.swing.JPanel();
        jbutCancel = new javax.swing.JButton();
        jbutStart = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));

        jPanMain.setBackground(new java.awt.Color(255, 255, 255));
        jPanMain.setLayout(new java.awt.GridBagLayout());

        jPanName.setBackground(new java.awt.Color(255, 255, 255));
        jPanName.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Fahrzeugname", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 0, 13), java.awt.Color.red)); // NOI18N
        jPanName.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanName.add(jtfVehicleName, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 7, 0, 7);
        jPanMain.add(jPanName, gridBagConstraints);

        jPanMethod.setBackground(new java.awt.Color(255, 255, 255));
        jPanMethod.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Messmethode", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 0, 13), java.awt.Color.red)); // NOI18N
        jPanMethod.setLayout(new java.awt.GridBagLayout());

        bgMethod.add(jrbStartStop);
        jrbStartStop.setSelected(true);
        jrbStartStop.setText("Start-Stop (mit Enddrehzahl)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanMethod.add(jrbStartStop, gridBagConstraints);

        bgMethod.add(jrbDrop);
        jrbDrop.setText("Drop-RPM (ohne Enddrehzahl)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanMethod.add(jrbDrop, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanMain.add(jPanMethod, gridBagConstraints);

        jPanRPM.setBackground(new java.awt.Color(255, 255, 255));
        jPanRPM.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Messung", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 0, 13), java.awt.Color.red)); // NOI18N
        jPanRPM.setLayout(new java.awt.GridBagLayout());

        jcbEnableRPM.setSelected(true);
        jcbEnableRPM.setText("Motordrehzahl messen");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanRPM.add(jcbEnableRPM, gridBagConstraints);

        jcbEnableTemp.setSelected(true);
        jcbEnableTemp.setText("Motor- & Abgastemperaturen messen");
        jcbEnableTemp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbEnableTempActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        jPanRPM.add(jcbEnableTemp, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.weightx = 1.0;
        jPanMain.add(jPanRPM, gridBagConstraints);

        jPanStroke.setBackground(new java.awt.Color(255, 255, 255));
        jPanStroke.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Taktanzahl", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 0, 13), java.awt.Color.red)); // NOI18N
        jPanStroke.setLayout(new java.awt.GridBagLayout());

        bgStroke.add(jrb2Stroke);
        jrb2Stroke.setSelected(true);
        jrb2Stroke.setText("2 Takt");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanStroke.add(jrb2Stroke, gridBagConstraints);

        bgStroke.add(jrb4Stroke);
        jrb4Stroke.setText("4 Takt");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanStroke.add(jrb4Stroke, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.weightx = 1.0;
        jPanMain.add(jPanStroke, gridBagConstraints);

        jPanTransmission.setBackground(new java.awt.Color(255, 255, 255));
        jPanTransmission.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Getriebeart", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 0, 13), java.awt.Color.red)); // NOI18N
        jPanTransmission.setLayout(new java.awt.GridBagLayout());

        bgTransmission.add(jrbManual);
        jrbManual.setSelected(true);
        jrbManual.setText("Schaltgetriebe");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanTransmission.add(jrbManual, gridBagConstraints);

        bgTransmission.add(jrbAutomatic);
        jrbAutomatic.setText("Automatik");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanTransmission.add(jrbAutomatic, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.weightx = 1.0;
        jPanMain.add(jPanTransmission, gridBagConstraints);

        getContentPane().add(jPanMain, java.awt.BorderLayout.CENTER);

        jPanControls.setBackground(new java.awt.Color(255, 255, 255));

        jPanButtons.setBackground(new java.awt.Color(255, 255, 255));
        jPanButtons.setLayout(new java.awt.GridLayout(1, 0));

        jbutCancel.setText("Abbrechen");
        jbutCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbutCancelActionPerformed(evt);
            }
        });
        jPanButtons.add(jbutCancel);

        jbutStart.setText("Messung starten");
        jbutStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbutStartActionPerformed(evt);
            }
        });
        jPanButtons.add(jbutStart);

        jPanControls.add(jPanButtons);

        getContentPane().add(jPanControls, java.awt.BorderLayout.PAGE_END);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbutStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbutStartActionPerformed
        confirm();
    }//GEN-LAST:event_jbutStartActionPerformed

    private void jbutCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbutCancelActionPerformed
        pressedOK = false;
        dispose();
    }//GEN-LAST:event_jbutCancelActionPerformed

    private void jcbEnableTempActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbEnableTempActionPerformed
        if (jcbEnableTemp.isSelected()) {
            jcbEnableRPM.setSelected(true);
            jcbEnableRPM.setEnabled(false);
        } else {
            jcbEnableRPM.setEnabled(true);
        }
    }//GEN-LAST:event_jcbEnableTempActionPerformed

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
            VehicleSetDialog dialog = new VehicleSetDialog(new javax.swing.JFrame(), true);
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
    private javax.swing.ButtonGroup bgMethod;
    private javax.swing.ButtonGroup bgStroke;
    private javax.swing.ButtonGroup bgTransmission;
    private javax.swing.JPanel jPanButtons;
    private javax.swing.JPanel jPanControls;
    private javax.swing.JPanel jPanMain;
    private javax.swing.JPanel jPanMethod;
    private javax.swing.JPanel jPanName;
    private javax.swing.JPanel jPanRPM;
    private javax.swing.JPanel jPanStroke;
    private javax.swing.JPanel jPanTransmission;
    private javax.swing.JButton jbutCancel;
    private javax.swing.JButton jbutStart;
    private javax.swing.JCheckBox jcbEnableRPM;
    private javax.swing.JCheckBox jcbEnableTemp;
    private javax.swing.JRadioButton jrb2Stroke;
    private javax.swing.JRadioButton jrb4Stroke;
    private javax.swing.JRadioButton jrbAutomatic;
    private javax.swing.JRadioButton jrbDrop;
    private javax.swing.JRadioButton jrbManual;
    private javax.swing.JRadioButton jrbStartStop;
    private javax.swing.JTextField jtfVehicleName;
    // End of variables declaration//GEN-END:variables
}
