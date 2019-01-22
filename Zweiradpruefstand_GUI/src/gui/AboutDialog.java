package gui;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.ImageIcon;
import main.BESDyno.OS;
import data.Config;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import logging.Logger;

/**
 *
 * @author emil
 */
public class AboutDialog extends javax.swing.JDialog {

    private static final Logger LOG = Logger.getLogger(AboutDialog.class.getName());

    /**
     * Creates new form about
     */
    public AboutDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        //jLabelTitle.setIcon(
        //new javax.swing.ImageIcon(
        Object x = getClass();
        Object y = getClass().getResource("/icons/logo128.png");
        initComponents();
        writeVersion();
        setTitle("Zweiradprüfstand - About");
        setLocationRelativeTo(null);
        setSize(new Dimension(1300, 800));

    }

    private static void openURL(String url) {
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

    private void writeVersion() {
        jLabelVersion.setText("BES Dyno v1.0");
        jLabelOsName.setText(System.getProperty("os.name"));
        jLabelOsVersion.setText(System.getProperty("os.version"));
        jLabelVmName.setText(System.getProperty("java.vm.name"));
        jLabelVmVendor.setText(System.getProperty("java.vm.vendor"));
        jLabelVmVersion.setText(System.clearProperty("java.vm.version"));
        jLabelRtName.setText(System.getProperty("java.runtime.name"));
        jLabelRtVersion.setText(System.getProperty("java.runtime.version"));
    }

    public void writeDevice(String dn) {
        jLabelDevice.setText(dn);
        if (Config.getInstance().getArduinoVersion() > 0) {
            jLabelArduino.setText(String.format("BES Measurement v%.1f", Config.getInstance().getArduinoVersion()));
        }
    }

    public void setAppearance(boolean dark) {
        if (dark == true) {
            jLabelTitle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/logo128_white.png")));
            jPanAuthor.setBackground(Color.darkGray);
            jPanCopyright.setBackground(Color.darkGray);
            jPanDevice.setBackground(Color.darkGray);
            jPanHeader.setBackground(Color.darkGray);
            jPanInfo.setBackground(Color.darkGray);
            jPanJava.setBackground(Color.darkGray);
            jPanOS.setBackground(Color.darkGray);
            jPanRuntime.setBackground(Color.darkGray);
            jPanSysInfo.setBackground(Color.darkGray);
            jPanVersion.setBackground(Color.darkGray);

            jLabelAuthor.setForeground(Color.white);
            jLabelDevelopers.setForeground(Color.white);
            jLabelDevice.setForeground(Color.white);
            jLabelDeviceT.setForeground(Color.white);
            jLabelOsName.setForeground(Color.white);
            jLabelOsNameT.setForeground(Color.white);
            jLabelOsVersion.setForeground(Color.white);
            jLabelOsVersionT.setForeground(Color.white);
            jLabelRtName.setForeground(Color.white);
            jLabelRtNameT.setForeground(Color.white);
            jLabelRtVersion.setForeground(Color.white);
            jLabelRtVersionT.setForeground(Color.white);
            jLabelTitle.setForeground(Color.white);
            jLabelVersion.setForeground(Color.white);
            jLabelVersionT.setForeground(Color.white);
            jLabelVmName.setForeground(Color.white);
            jLabelVmNameT.setForeground(Color.white);
            jLabelVmVendor.setForeground(Color.white);
            jLabelVmVendorT.setForeground(Color.white);
            jLabelVmVersion.setForeground(Color.white);
            jLabelVmVersionT.setForeground(Color.white);
            jLabelWarning.setForeground(Color.white);
            jLabelArduinoT.setForeground(Color.white);
            jLabelArduino.setForeground(Color.white);
            jLabelMade.setForeground(Color.white);
        } else {
            jLabelTitle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/logo128.png")));
            jPanAuthor.setBackground(Color.white);
            jPanCopyright.setBackground(Color.white);
            jPanDevice.setBackground(Color.white);
            jPanHeader.setBackground(Color.white);
            jPanInfo.setBackground(Color.white);
            jPanJava.setBackground(Color.white);
            jPanOS.setBackground(Color.white);
            jPanRuntime.setBackground(Color.white);
            jPanSysInfo.setBackground(Color.white);
            jPanVersion.setBackground(Color.white);

            jLabelAuthor.setForeground(Color.black);
            jLabelDevelopers.setForeground(Color.black);
            jLabelDevice.setForeground(Color.black);
            jLabelDeviceT.setForeground(Color.black);
            jLabelOsName.setForeground(Color.black);
            jLabelOsNameT.setForeground(Color.black);
            jLabelOsVersion.setForeground(Color.black);
            jLabelOsVersionT.setForeground(Color.black);
            jLabelRtName.setForeground(Color.black);
            jLabelRtNameT.setForeground(Color.black);
            jLabelRtVersion.setForeground(Color.black);
            jLabelRtVersionT.setForeground(Color.black);
            jLabelTitle.setForeground(Color.black);
            jLabelVersion.setForeground(Color.black);
            jLabelVersionT.setForeground(Color.black);
            jLabelVmName.setForeground(Color.black);
            jLabelVmNameT.setForeground(Color.black);
            jLabelVmVendor.setForeground(Color.black);
            jLabelVmVendorT.setForeground(Color.black);
            jLabelVmVersion.setForeground(Color.black);
            jLabelVmVersionT.setForeground(Color.black);
            jLabelWarning.setForeground(Color.black);
            jLabelArduinoT.setForeground(Color.black);
            jLabelArduino.setForeground(Color.black);
            jLabelMade.setForeground(Color.black);
        }
    }

    public void setOSIcon(OS os) {
        switch (os) {
            case MACOS:
                jLabelOS.setIcon(new ImageIcon(getClass().getResource("/icons/mac50.png")));
                break;
            case LINUX:
                jLabelOS.setIcon(new ImageIcon(getClass().getResource("/icons/linux50.png")));
                break;
            case WINDOWS:
                jLabelOS.setIcon(new ImageIcon(getClass().getResource("/icons/windows50.png")));
                break;
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

        jPanHeader = new javax.swing.JPanel();
        jLabelTitle = new javax.swing.JLabel();
        jPanInfo = new javax.swing.JPanel();
        jLabelDevelopers = new javax.swing.JLabel();
        jLabelWarning = new javax.swing.JLabel();
        jPanAuthor = new javax.swing.JPanel();
        jPanSysInfo = new javax.swing.JPanel();
        jPanVersion = new javax.swing.JPanel();
        jLabelVersionT = new javax.swing.JLabel();
        jLabelVersion = new javax.swing.JLabel();
        jPanOS = new javax.swing.JPanel();
        jLabelOsNameT = new javax.swing.JLabel();
        jLabelOsName = new javax.swing.JLabel();
        jLabelOS = new javax.swing.JLabel();
        jLabelOsVersionT = new javax.swing.JLabel();
        jLabelOsVersion = new javax.swing.JLabel();
        jPanJava = new javax.swing.JPanel();
        jLabelVmNameT = new javax.swing.JLabel();
        jLabelVmName = new javax.swing.JLabel();
        jLabelVmVendorT = new javax.swing.JLabel();
        jLabelVmVendor = new javax.swing.JLabel();
        jLabelVmVersionT = new javax.swing.JLabel();
        jLabelVmVersion = new javax.swing.JLabel();
        jPanRuntime = new javax.swing.JPanel();
        jLabelRtNameT = new javax.swing.JLabel();
        jLabelRtName = new javax.swing.JLabel();
        jLabelRtVersionT = new javax.swing.JLabel();
        jLabelRtVersion = new javax.swing.JLabel();
        jPanDevice = new javax.swing.JPanel();
        jLabelDeviceT = new javax.swing.JLabel();
        jLabelDevice = new javax.swing.JLabel();
        jLabelArduinoT = new javax.swing.JLabel();
        jLabelArduino = new javax.swing.JLabel();
        jPanCopyright = new javax.swing.JPanel();
        jLabelAuthor = new javax.swing.JLabel();
        jLabelMade = new javax.swing.JLabel();
        jbutSourceLink = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanHeader.setBackground(new java.awt.Color(255, 255, 255));
        jPanHeader.setLayout(new java.awt.BorderLayout());

        jLabelTitle.setFont(new java.awt.Font("Lucida Grande", 0, 48)); // NOI18N
        jLabelTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelTitle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/logo128.png"))); // NOI18N
        jLabelTitle.setText("Zweiradprüfstand");
        jLabelTitle.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15));
        jPanHeader.add(jLabelTitle, java.awt.BorderLayout.PAGE_START);

        getContentPane().add(jPanHeader, java.awt.BorderLayout.PAGE_START);

        jPanInfo.setBackground(new java.awt.Color(255, 255, 255));
        jPanInfo.setLayout(new java.awt.GridLayout(1, 0));

        jLabelDevelopers.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        jLabelDevelopers.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelDevelopers.setText("<html>  <center> <b>Diplomanden: </b> <br>  Berger Emil - Software<br>  Ehmann Julian - Elektronik<br>  Sammer Daniel - Mechanik<br>  </center>");
        jLabelDevelopers.setAutoscrolls(true);
        jLabelDevelopers.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        jPanInfo.add(jLabelDevelopers);

        jLabelWarning.setBackground(new java.awt.Color(255, 255, 255));
        jLabelWarning.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelWarning.setText("<html> <left> Die Nutzung des Prüfstandes geschieht auf eigene Gefahr! <br> Die Diplomanden übernehmen keine Haftung für eventuell entstandene Sach- oder Personenschäden. <br> Der/Die Nutzer/in des Prüfstandes erhält eine lebenslange Lizenz zur Benutzung der Software. <br> Alle Rechte an der Software verbleiben bei Berger Emil. <br> Alle Rechte der Elektronik verbleiben bei Ehmann Julian.");
        jLabelWarning.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Warnung", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 0, 14), new java.awt.Color(255, 0, 0))); // NOI18N
        jPanInfo.add(jLabelWarning);

        getContentPane().add(jPanInfo, java.awt.BorderLayout.CENTER);

        jPanAuthor.setBackground(new java.awt.Color(255, 255, 255));
        jPanAuthor.setLayout(new java.awt.BorderLayout());

        jPanSysInfo.setBackground(new java.awt.Color(255, 255, 255));
        jPanSysInfo.setLayout(new java.awt.GridLayout(5, 1));

        jPanVersion.setBackground(new java.awt.Color(255, 255, 255));
        jPanVersion.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Software-Version", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 0, 13), new java.awt.Color(255, 0, 0))); // NOI18N
        jPanVersion.setLayout(new java.awt.GridLayout(1, 0));

        jLabelVersionT.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabelVersionT.setText("Version:");
        jPanVersion.add(jLabelVersionT);

        jLabelVersion.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelVersion.setText("jLabel1");
        jPanVersion.add(jLabelVersion);

        jPanSysInfo.add(jPanVersion);

        jPanOS.setBackground(new java.awt.Color(255, 255, 255));
        jPanOS.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Betriebssystem", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 0, 13), new java.awt.Color(255, 0, 0))); // NOI18N
        jPanOS.setLayout(new java.awt.GridBagLayout());

        jLabelOsNameT.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabelOsNameT.setText("Betriebssystem:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        jPanOS.add(jLabelOsNameT, gridBagConstraints);

        jLabelOsName.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelOsName.setText("jLabel1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        jPanOS.add(jLabelOsName, gridBagConstraints);

        jLabelOS.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        jPanOS.add(jLabelOS, gridBagConstraints);

        jLabelOsVersionT.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabelOsVersionT.setText("OS-Version:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        jPanOS.add(jLabelOsVersionT, gridBagConstraints);

        jLabelOsVersion.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelOsVersion.setText("jLabel1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        jPanOS.add(jLabelOsVersion, gridBagConstraints);

        jPanSysInfo.add(jPanOS);

        jPanJava.setBackground(new java.awt.Color(255, 255, 255));
        jPanJava.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Java-VM", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 0, 13), new java.awt.Color(255, 0, 0))); // NOI18N
        jPanJava.setLayout(new java.awt.GridLayout(3, 0));

        jLabelVmNameT.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabelVmNameT.setText("Java-VM:");
        jPanJava.add(jLabelVmNameT);

        jLabelVmName.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelVmName.setText("jLabel1");
        jPanJava.add(jLabelVmName);

        jLabelVmVendorT.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabelVmVendorT.setText("Hersteller:");
        jPanJava.add(jLabelVmVendorT);

        jLabelVmVendor.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelVmVendor.setText("jLabel1");
        jPanJava.add(jLabelVmVendor);

        jLabelVmVersionT.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabelVmVersionT.setText("VM-Version:");
        jPanJava.add(jLabelVmVersionT);

        jLabelVmVersion.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelVmVersion.setText("jLabel1");
        jPanJava.add(jLabelVmVersion);

        jPanSysInfo.add(jPanJava);

        jPanRuntime.setBackground(new java.awt.Color(255, 255, 255));
        jPanRuntime.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Java-RE", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 0, 13), new java.awt.Color(255, 0, 0))); // NOI18N
        jPanRuntime.setLayout(new java.awt.GridLayout(2, 0));

        jLabelRtNameT.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabelRtNameT.setText("Runtime-Environment:");
        jPanRuntime.add(jLabelRtNameT);

        jLabelRtName.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelRtName.setText("jLabel1");
        jPanRuntime.add(jLabelRtName);

        jLabelRtVersionT.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabelRtVersionT.setText("RE-Version:");
        jPanRuntime.add(jLabelRtVersionT);

        jLabelRtVersion.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelRtVersion.setText("jLabel1");
        jPanRuntime.add(jLabelRtVersion);

        jPanSysInfo.add(jPanRuntime);

        jPanDevice.setBackground(new java.awt.Color(255, 255, 255));
        jPanDevice.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Endgerät", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 0, 13), new java.awt.Color(255, 0, 0))); // NOI18N
        jPanDevice.setLayout(new java.awt.GridLayout(2, 2));

        jLabelDeviceT.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabelDeviceT.setText("Verbundener Prüfstand: ");
        jPanDevice.add(jLabelDeviceT);

        jLabelDevice.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelDevice.setText("Kein Prüfstand verbunden...");
        jPanDevice.add(jLabelDevice);

        jLabelArduinoT.setText("Prüfstand-Firmware-Software:");
        jPanDevice.add(jLabelArduinoT);

        jLabelArduino.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelArduino.setText("Kein Prüfstand verbunden...");
        jPanDevice.add(jLabelArduino);

        jPanSysInfo.add(jPanDevice);

        jPanAuthor.add(jPanSysInfo, java.awt.BorderLayout.CENTER);

        jPanCopyright.setBackground(new java.awt.Color(255, 255, 255));
        jPanCopyright.setLayout(new java.awt.GridBagLayout());

        jLabelAuthor.setText("© Emil Berger 2018-2019");
        jLabelAuthor.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanCopyright.add(jLabelAuthor, gridBagConstraints);

        jLabelMade.setText("Made with love in Europe on  macOS");
        jLabelMade.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanCopyright.add(jLabelMade, gridBagConstraints);

        jbutSourceLink.setText("Quellcode");
        jbutSourceLink.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onSourceLink(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanCopyright.add(jbutSourceLink, gridBagConstraints);

        jPanAuthor.add(jPanCopyright, java.awt.BorderLayout.PAGE_END);

        getContentPane().add(jPanAuthor, java.awt.BorderLayout.PAGE_END);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void onSourceLink(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onSourceLink
        openURL("https://github.com/beremm14/Zweiradpruefstand");
    }//GEN-LAST:event_onSourceLink

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
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(() -> {
            AboutDialog dialog = new AboutDialog(new javax.swing.JFrame(), true);
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
    private javax.swing.JLabel jLabelArduino;
    private javax.swing.JLabel jLabelArduinoT;
    private javax.swing.JLabel jLabelAuthor;
    private javax.swing.JLabel jLabelDevelopers;
    private javax.swing.JLabel jLabelDevice;
    private javax.swing.JLabel jLabelDeviceT;
    private javax.swing.JLabel jLabelMade;
    private javax.swing.JLabel jLabelOS;
    private javax.swing.JLabel jLabelOsName;
    private javax.swing.JLabel jLabelOsNameT;
    private javax.swing.JLabel jLabelOsVersion;
    private javax.swing.JLabel jLabelOsVersionT;
    private javax.swing.JLabel jLabelRtName;
    private javax.swing.JLabel jLabelRtNameT;
    private javax.swing.JLabel jLabelRtVersion;
    private javax.swing.JLabel jLabelRtVersionT;
    private javax.swing.JLabel jLabelTitle;
    private javax.swing.JLabel jLabelVersion;
    private javax.swing.JLabel jLabelVersionT;
    private javax.swing.JLabel jLabelVmName;
    private javax.swing.JLabel jLabelVmNameT;
    private javax.swing.JLabel jLabelVmVendor;
    private javax.swing.JLabel jLabelVmVendorT;
    private javax.swing.JLabel jLabelVmVersion;
    private javax.swing.JLabel jLabelVmVersionT;
    private javax.swing.JLabel jLabelWarning;
    private javax.swing.JPanel jPanAuthor;
    private javax.swing.JPanel jPanCopyright;
    private javax.swing.JPanel jPanDevice;
    private javax.swing.JPanel jPanHeader;
    private javax.swing.JPanel jPanInfo;
    private javax.swing.JPanel jPanJava;
    private javax.swing.JPanel jPanOS;
    private javax.swing.JPanel jPanRuntime;
    private javax.swing.JPanel jPanSysInfo;
    private javax.swing.JPanel jPanVersion;
    private javax.swing.JButton jbutSourceLink;
    // End of variables declaration//GEN-END:variables
}
