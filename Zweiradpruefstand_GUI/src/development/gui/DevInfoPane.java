package development.gui;

import development.gui.model.InfoPaneModel;
import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author emil
 */
public class DevInfoPane extends javax.swing.JDialog {

    private static final Logger LOG = Logger.getLogger(DevInfoPane.class.getName());
    
    private final InfoPaneModel model = new InfoPaneModel();

    /**
     * Creates new form DevInfoPane
     * @param parent
     * @param modal
     */
    public DevInfoPane(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        jList.setModel(model);
    }
    
    public void addElement(String s) {
        model.add(s);
    }
    
    public void rmAll() {
        model.rmAll();
    }
    
    public void setAppearance(boolean dark) {
        if (dark) {
            jInfoPane.setBackground(Color.DARK_GRAY);
            jList.setBackground(Color.DARK_GRAY);
            jPanButtons.setBackground(Color.DARK_GRAY);
            jPanList.setBackground(Color.DARK_GRAY);
            
            jList.setForeground(Color.WHITE);
        } else {
            jInfoPane.setBackground(Color.WHITE);
            jList.setBackground(Color.WHITE);
            jPanButtons.setBackground(Color.WHITE);
            jPanList.setBackground(Color.WHITE);
            
            jList.setForeground(Color.BLACK);
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

        jPanButtons = new javax.swing.JPanel();
        jbutSave = new javax.swing.JButton();
        jbutOk = new javax.swing.JButton();
        jPanList = new javax.swing.JPanel();
        jInfoPane = new javax.swing.JScrollPane();
        jList = new javax.swing.JList<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanButtons.setLayout(new java.awt.GridBagLayout());

        jbutSave.setText("Speichern");
        jbutSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbutSaveActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.ipady = 1;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanButtons.add(jbutSave, gridBagConstraints);

        jbutOk.setText("OK");
        jbutOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbutOkActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.ipady = 1;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanButtons.add(jbutOk, gridBagConstraints);

        getContentPane().add(jPanButtons, java.awt.BorderLayout.SOUTH);

        jPanList.setLayout(new java.awt.GridLayout(1, 0));

        jList.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jInfoPane.setViewportView(jList);

        jPanList.add(jInfoPane);

        getContentPane().add(jPanList, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbutOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbutOkActionPerformed
        dispose();
    }//GEN-LAST:event_jbutOkActionPerformed

    private void jbutSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbutSaveActionPerformed
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Text-Datei (*.txt)", "txt"));

        File file = null;
        File home;
        File folder;
        Date date = Calendar.getInstance().getTime();
        DateFormat df = new SimpleDateFormat("yy.mm.DD-HH:mm:ss");

        try {
            home = new File(System.getProperty("user.home"));
        } catch (Exception e) {
            home = null;
        }

        if (home != null && home.exists()) {
            folder = new File(home + File.separator + "Bike-Files" + File.separator + "Service_Files");
            if (!folder.exists()) {
                folder.mkdir();
            }
            file = new File(folder + File.separator + "ListContent_" + df.format(date) + ".txt");
        }

        chooser.setSelectedFile(file);

        int rv = chooser.showSaveDialog(this);
        if (rv == JFileChooser.APPROVE_OPTION) {
            file = chooser.getSelectedFile();

            try (BufferedWriter w = new BufferedWriter(new FileWriter(file))) {
                for (int i=0; i<model.getSize(); i++) {
                    w.write((String) model.getElementAt(i));
                    w.newLine();
                }
            } catch (Exception ex) {
                LOG.severe(ex);
            }
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
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(DevInfoPane.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DevInfoPane.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DevInfoPane.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DevInfoPane.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(() -> {
            DevInfoPane dialog = new DevInfoPane(new javax.swing.JFrame(), true);
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
    private javax.swing.JScrollPane jInfoPane;
    private javax.swing.JList<String> jList;
    private javax.swing.JPanel jPanButtons;
    private javax.swing.JPanel jPanList;
    private javax.swing.JButton jbutOk;
    private javax.swing.JButton jbutSave;
    // End of variables declaration//GEN-END:variables
}
