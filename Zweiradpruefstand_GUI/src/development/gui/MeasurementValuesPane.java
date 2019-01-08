package development.gui;

import data.Database;
import development.CommunicationLogger;
import development.gui.model.MeasurementValuesModel;
import java.awt.Color;
import java.awt.Dimension;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author emil
 */
public class MeasurementValuesPane extends javax.swing.JDialog {

    private static final Logger LOG = Logger.getLogger(MeasurementValuesPane.class.getName());

    private final List<Double> engPowerList = Database.getInstance().getEngPowerList();
    private final List<Double> wheelPowerList = Database.getInstance().getWheelPowerList();
    
    private final List<Integer> engRpmList = Database.getInstance().getEngRpmList();
    private final List<Integer> wheelRpmList = Database.getInstance().getWheelRpmList();
    
    private final List<Double> engTorList = Database.getInstance().getEngTorList();
    private final List<Double> wheelTorList = Database.getInstance().getWheelTorList();
    
    private final List<Double> velList = Database.getInstance().getVelList();
    
    private final MeasurementValuesModel model = new MeasurementValuesModel(engPowerList, wheelPowerList, engRpmList, wheelRpmList, engTorList, wheelTorList, velList);

    /**
     * Creates new form MeasurementValuesPane
     */
    public MeasurementValuesPane(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setSize(new Dimension(1000, 500));
        jTable.setModel(model);
    }
    
    public void setAppearance(boolean dark) {
        if (dark) {
            jPanButtons.setBackground(Color.DARK_GRAY);
            jPanMain.setBackground(Color.DARK_GRAY);
            
            jTable.setBackground(Color.DARK_GRAY);
            jTable.setGridColor(Color.DARK_GRAY);
            jTable.getTableHeader().setBackground(Color.DARK_GRAY);
            
            jScrollPane.setBackground(Color.DARK_GRAY);
            jScrollPane.getViewport().setBackground(Color.DARK_GRAY);
            
            jTable.setForeground(Color.WHITE);
            jTable.getTableHeader().setForeground(Color.WHITE);
        } else {
            jPanButtons.setBackground(Color.WHITE);
            jPanMain.setBackground(Color.WHITE);
            
            jTable.setBackground(Color.WHITE);
            jTable.setGridColor(Color.WHITE);
            jTable.getTableHeader().setBackground(Color.WHITE);
            
            jScrollPane.setBackground(Color.WHITE);
            jScrollPane.getViewport().setBackground(Color.WHITE);
            
            jTable.setForeground(Color.BLACK);
            jTable.getTableHeader().setForeground(Color.BLACK);
        }
    }
    
    private void writeOutFile(BufferedWriter w) throws IOException {
        w.write("ENGINE POWER");
        w.newLine();
        for (Double engPower : engPowerList) {
            w.write(engPower + "");
            w.newLine();
        }
        w.newLine();
        
        w.write("WHEEL POWER");
        w.newLine();
        for (Double wheelPower : wheelPowerList) {
            w.write(wheelPower + "");
            w.newLine();
        }
        w.newLine();
        
        w.write("ENGINE RPM");
        w.newLine();
        for (Integer engRpm : engRpmList) {
            w.write(engRpm + "");
            w.newLine();
        }
        w.newLine();
        
        w.write("WHEEL RPM");
        w.newLine();
        for (Integer wheelRpm : wheelRpmList) {
            w.write(wheelRpm + "");
            w.newLine();
        }
        w.newLine();
        
        w.write("ENGINE TORQUE");
        w.newLine();
        for (Double engTor : engTorList) {
            w.write(engTor + "");
            w.newLine();
        }
        w.newLine();
        
        w.write("WHEEl TORQUE");
        w.newLine();
        for (Double wheelTor : wheelTorList) {
            w.write(wheelTor + "");
            w.newLine();
        }
        w.newLine();
        
        w.write("VELOCITY");
        w.newLine();
        for (Double velo : velList) {
            w.write(velo + "");
            w.newLine();
        }
    }
    
    private void save() throws Exception {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Log-Datei (*.log)", "log"));

        File measureFile = null;
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
            folder = new File(home + File.separator + "Bike-Files" + File.separator + "Test_Files");
            if (!folder.exists()) {
                if (!folder.mkdir()) {
                    throw new Exception("Internal Error");
                }
            }
            measureFile = new File(folder + File.separator + "MeasureLog_" + df.format(date) + ".log");
        }

        chooser.setSelectedFile(measureFile);
        int rv = chooser.showSaveDialog(this);
        if (rv == JFileChooser.APPROVE_OPTION) {
            measureFile = chooser.getSelectedFile();

            try (BufferedWriter w = new BufferedWriter(new FileWriter(measureFile))) {
                writeOutFile(w);
            } catch (Exception ex) {
                LOG.severe(ex);
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

        jPanMain = new javax.swing.JPanel();
        jScrollPane = new javax.swing.JScrollPane();
        jTable = new javax.swing.JTable();
        jPanButtons = new javax.swing.JPanel();
        jbutSave = new javax.swing.JButton();
        jbutOk = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));

        jPanMain.setBackground(new java.awt.Color(255, 255, 255));
        jPanMain.setLayout(new java.awt.GridLayout());

        jTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane.setViewportView(jTable);

        jPanMain.add(jScrollPane);

        getContentPane().add(jPanMain, java.awt.BorderLayout.CENTER);

        jPanButtons.setBackground(new java.awt.Color(255, 255, 255));
        jPanButtons.setLayout(new java.awt.GridLayout());

        jbutSave.setText("Speichern");
        jbutSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbutSaveActionPerformed(evt);
            }
        });
        jPanButtons.add(jbutSave);

        jbutOk.setText("OK");
        jbutOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbutOkActionPerformed(evt);
            }
        });
        jPanButtons.add(jbutOk);

        getContentPane().add(jPanButtons, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbutOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbutOkActionPerformed
        dispose();
    }//GEN-LAST:event_jbutOkActionPerformed

    private void jbutSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbutSaveActionPerformed
        try {
            save();
        } catch (Exception ex) {
            LOG.severe(ex);
        }
        dispose();
    }//GEN-LAST:event_jbutSaveActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
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
            java.util.logging.Logger.getLogger(MeasurementValuesPane.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MeasurementValuesPane.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MeasurementValuesPane.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MeasurementValuesPane.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(() -> {
            MeasurementValuesPane dialog = new MeasurementValuesPane(new javax.swing.JFrame(), true);
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
    private javax.swing.JPanel jPanButtons;
    private javax.swing.JPanel jPanMain;
    private javax.swing.JScrollPane jScrollPane;
    private javax.swing.JTable jTable;
    private javax.swing.JButton jbutOk;
    private javax.swing.JButton jbutSave;
    // End of variables declaration//GEN-END:variables
}
