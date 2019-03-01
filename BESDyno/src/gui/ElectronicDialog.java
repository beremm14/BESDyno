package gui;

import java.awt.Dimension;

/**
 *
 * @author emil
 */
public class ElectronicDialog extends javax.swing.JDialog {

    /**
     * Creates new form ElectronicDialog
     */
    public ElectronicDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setSize(new Dimension(1200, 600));
        setResizable(false);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanInfo = new javax.swing.JPanel();
        jLabelInfo = new javax.swing.JLabel();
        jPanJuli = new javax.swing.JPanel();
        jLabelJuli = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanInfo.setBackground(new java.awt.Color(255, 255, 255));
        jPanInfo.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        jPanInfo.setLayout(new java.awt.GridLayout());

        jLabelInfo.setText("<html>\n<center>\n<h1>Julian Ehmann</h1>\n<p><b>Elektronik</b></p><br></br>\n<p>- Sensorik</p><br></br>\n<p>- Logikschaltungen</p><br></br>\n<p>- PCB-Design</p><br></br>\n<p>- Aufbauten</p><br></br>\n</center>\n</html>");
        jPanInfo.add(jLabelInfo);

        getContentPane().add(jPanInfo, java.awt.BorderLayout.WEST);

        jPanJuli.setLayout(new java.awt.BorderLayout());

        jLabelJuli.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/Werbung.JPG"))); // NOI18N
        jPanJuli.add(jLabelJuli, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanJuli, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
            java.util.logging.Logger.getLogger(ElectronicDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ElectronicDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ElectronicDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ElectronicDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(() -> {
            ElectronicDialog dialog = new ElectronicDialog(new javax.swing.JFrame(), true);
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
    private javax.swing.JLabel jLabelInfo;
    private javax.swing.JLabel jLabelJuli;
    private javax.swing.JPanel jPanInfo;
    private javax.swing.JPanel jPanJuli;
    // End of variables declaration//GEN-END:variables
}
