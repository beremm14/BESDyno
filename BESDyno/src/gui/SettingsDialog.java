package gui;

import data.Bike;
import data.Config;
import data.Config.Velocity;
import java.awt.Color;
import java.awt.Dimension;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import javax.swing.JFileChooser;
import logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import main.BESDyno.OS;

/**
 *
 * @author emil
 */
public class SettingsDialog extends javax.swing.JDialog {

    private static final Logger LOG = Logger.getLogger(SettingsDialog.class.getName());

    private boolean pressedOK;

    private Velocity lastVelocity;

    /**
     * Creates new form SettingsDialog
     *
     * @param parent
     * @param modal
     */
    public SettingsDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        setSize(new Dimension(800, 500));
        setResizable(false);
        setTitle("Einstellungen - Konfiguration");
        setLocationRelativeTo(null);

        lastVelocity = Config.getInstance().getVelocity();

        jcbEnableInertiaEdit.setSelected(false);
        jtfInertia.setEnabled(false);
    }

    public void writeDevice(String dn) {
        jLabelDevice2.setText(dn);
        if (Config.getInstance().getArduinoVersion() == 0) {
            jLabelArduino2.setText("Kein Prüfstand verbunden...");
        } else {
            jLabelArduino2.setText(String.format(Locale.UK, "BES Measurement v%.1f", Config.getInstance().getArduinoVersion()));
        }
    }

    public void setAppearance(boolean dark, OS os) {
        if (os == OS.LINUX) {
            setSize(new Dimension(1000, 700));
        }

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
            jPanVelocity.setBackground(Color.darkGray);
            jPanDevice.setBackground(Color.darkGray);
            jPanLoad.setBackground(Color.darkGray);
            jPanConfirm.setBackground(Color.darkGray);
            jPanTemp.setBackground(Color.darkGray);
            jPanFilter.setBackground(Color.darkGray);

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
            jtfStopKmh.setBackground(Color.darkGray);
            jtfStopRpm.setBackground(Color.darkGray);
            jtfEngWarning.setBackground(Color.darkGray);
            jtfExhWarning.setBackground(Color.darkGray);
            jtfOrder.setBackground(Color.darkGray);
            jtfSmoothing.setBackground(Color.darkGray);

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
            jtfStopKmh.setForeground(Color.white);
            jtfStopRpm.setForeground(Color.white);
            jtfEngWarning.setForeground(Color.white);
            jtfExhWarning.setForeground(Color.white);
            jtfOrder.setForeground(Color.white);
            jtfSmoothing.setForeground(Color.white);

            jrbDaymode.setForeground(Color.white);
            jrbNightmode.setForeground(Color.white);
            jrbKW.setForeground(Color.white);
            jrbPS.setForeground(Color.white);
            jrbMPS.setForeground(Color.white);
            jrbKMH.setForeground(Color.white);
            jrbMIH.setForeground(Color.white);
            jrbCelcius.setForeground(Color.white);
            jrbFahrenheit.setForeground(Color.white);
            jrbAverage.setForeground(Color.white);
            jrbPoly.setForeground(Color.white);

            jcbEnableInertiaEdit.setForeground(Color.white);
            jcbFilter.setForeground(Color.white);

            jrbDaymode.setBackground(Color.darkGray);
            jrbNightmode.setBackground(Color.darkGray);
            jrbKW.setBackground(Color.darkGray);
            jrbPS.setBackground(Color.darkGray);
            jrbMPS.setBackground(Color.darkGray);
            jrbKMH.setBackground(Color.darkGray);
            jrbMIH.setBackground(Color.darkGray);
            jrbCelcius.setBackground(Color.darkGray);
            jrbFahrenheit.setBackground(Color.darkGray);
            jrbPoly.setBackground(Color.darkGray);
            jrbAverage.setBackground(Color.darkGray);

            jcbEnableInertiaEdit.setBackground(Color.darkGray);
            jcbFilter.setBackground(Color.darkGray);

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
            jLabelDevice1.setForeground(Color.white);
            jLabelDevice2.setForeground(Color.white);
            jLabelArduino1.setForeground(Color.white);
            jLabelArduino2.setForeground(Color.white);
            jLabelStopKmh.setForeground(Color.white);
            jLabelStopKmh2.setForeground(Color.white);
            jLabelStopRpm.setForeground(Color.white);
            jLabelStopRpm2.setForeground(Color.white);
            jLabelHelp.setForeground(Color.white);
            jLabelEngWarning.setForeground(Color.white);
            jLabelEngWarning2.setForeground(Color.white);
            jLabelExhWarning.setForeground(Color.white);
            jLabelExhWarning2.setForeground(Color.white);
            jLabelOrder.setForeground(Color.white);
            jLabelSmoothing.setForeground(Color.white);
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
            jPanVelocity.setBackground(Color.white);
            jPanDevice.setBackground(Color.white);
            jPanLoad.setBackground(Color.white);
            jPanConfirm.setBackground(Color.white);
            jPanTemp.setBackground(Color.white);
            jPanFilter.setBackground(Color.white);

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
            jtfStopKmh.setBackground(Color.white);
            jtfStopRpm.setBackground(Color.white);
            jtfEngWarning.setBackground(Color.white);
            jtfExhWarning.setBackground(Color.white);
            jtfOrder.setBackground(Color.white);
            jtfSmoothing.setBackground(Color.white);

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
            jtfStopKmh.setForeground(Color.black);
            jtfStopRpm.setForeground(Color.black);
            jtfEngWarning.setForeground(Color.black);
            jtfExhWarning.setForeground(Color.black);
            jtfOrder.setForeground(Color.black);
            jtfSmoothing.setForeground(Color.black);

            jrbDaymode.setForeground(Color.black);
            jrbNightmode.setForeground(Color.black);
            jrbKW.setForeground(Color.black);
            jrbPS.setForeground(Color.black);
            jrbKMH.setForeground(Color.black);
            jrbMPS.setForeground(Color.black);
            jrbMIH.setForeground(Color.black);
            jrbCelcius.setForeground(Color.black);
            jrbFahrenheit.setForeground(Color.black);
            jrbAverage.setForeground(Color.black);
            jrbPoly.setForeground(Color.black);
            
            jcbEnableInertiaEdit.setForeground(Color.black);
            jcbFilter.setForeground(Color.black);

            jrbDaymode.setBackground(Color.white);
            jrbNightmode.setBackground(Color.white);
            jrbKW.setBackground(Color.white);
            jrbPS.setBackground(Color.white);
            jrbMPS.setBackground(Color.white);
            jrbKMH.setBackground(Color.white);
            jrbMIH.setBackground(Color.white);
            jrbCelcius.setBackground(Color.white);
            jrbFahrenheit.setBackground(Color.white);
            jrbAverage.setBackground(Color.white);
            jrbPoly.setBackground(Color.black);

            jcbEnableInertiaEdit.setBackground(Color.white);
            jcbFilter.setBackground(Color.white);

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
            jLabelDevice1.setForeground(Color.black);
            jLabelDevice2.setForeground(Color.black);
            jLabelArduino1.setForeground(Color.black);
            jLabelArduino2.setForeground(Color.black);
            jLabelStopKmh.setForeground(Color.black);
            jLabelStopKmh2.setForeground(Color.black);
            jLabelStopRpm.setForeground(Color.black);
            jLabelStopRpm2.setForeground(Color.black);
            jLabelHelp.setForeground(Color.black);
            jLabelEngWarning.setForeground(Color.black);
            jLabelEngWarning2.setForeground(Color.black);
            jLabelExhWarning.setForeground(Color.black);
            jLabelExhWarning2.setForeground(Color.black);
            jLabelOrder.setForeground(Color.black);
            jLabelSmoothing.setForeground(Color.black);
        }
    }

    public boolean isPressedOK() {
        return pressedOK;
    }

    public boolean isDark() {
        return jrbNightmode.isSelected();
    }

    private String convertVelocity(Velocity from, Velocity to, int value) {
        switch (from) {
            case MIH:
                switch (to) {
                    case MIH:
                        return String.format("%d", value);
                    case KMH:
                        return String.format("%d", Math.round(((double) value) * 1.609));
                    case MPS:
                        return String.format("%d", Math.round(((double) value) / 2.237));
                }
            case KMH:
                switch (to) {
                    case MIH:
                        return String.format("%d", Math.round(((double) value) / 1.609));
                    case KMH:
                        return String.format("%d", value);
                    case MPS:
                        return String.format("%d", Math.round(((double) value) / 3.6));
                }
            case MPS:
                switch (to) {
                    case MIH:
                        return String.format("%d", Math.round(((double) value) * 2.237));
                    case KMH:
                        return String.format("%d", Math.round(((double) value) * 3.6));
                    case MPS:
                        return String.format("%d", value);
                }
        }
        return "";
    }

    private String convertTemperature(boolean toFahrenheit, int value) {
        if (toFahrenheit) {
            return String.format("%d", Math.round(((double) value) * (9.0 / 5.0) + 32.0));
        } else {
            return String.format("%d", Math.round((((double) value) - 32.0) * (5.0 / 9.0)));
        }
    }

    public void changePeriodTime() {
        jtfPeriod.requestFocusInWindow();
    }

    //Sets the Config-File
    private void confirm(Config c, boolean save) {

        boolean error = false;

        c.setDark(jrbNightmode.isSelected());
        c.setPs(jrbPS.isSelected());
        c.setCelcius(jrbCelcius.isSelected());
        if (jcbFilter.isSelected()) {
            c.setAverage(jrbAverage.isSelected());
            c.setPoly(jrbPoly.isSelected());
        } else {
            c.setAverage(false);
            c.setPoly(false);
        }
        if (jrbKMH.isSelected()) {
            c.setVelocity(Config.Velocity.KMH);
        } else if (jrbMPS.isSelected()) {
            c.setVelocity(Config.Velocity.MPS);
        } else if (jrbMIH.isSelected()) {
            c.setVelocity(Config.Velocity.MIH);
        } else {
            c.setVelocity(Config.Velocity.KMH);
        }

        try {
            c.setHysteresisKmh(Integer.parseInt(jtfHysteresisKmh.getText()));
            if (c.getHysteresisVelo() <= 0) {
                error = true;
                JOptionPane.showMessageDialog(this, "Hysterese Geschwindigkeit muss größer 0 sein...", "Eingabefehler!", JOptionPane.ERROR_MESSAGE);
                jtfHysteresisKmh.requestFocusInWindow();
            }
        } catch (NumberFormatException e) {
            error = true;
            JOptionPane.showMessageDialog(this, "Bitte alle Felder ausfüllen und ganzzahlige Werte eingeben!", "Eingabefehler!", JOptionPane.ERROR_MESSAGE);
            LOG.warning(e);
            jtfHysteresisKmh.requestFocusInWindow();
        }
        try {
            c.setHysteresisRpm(Integer.parseInt(jtfHysteresisRpm.getText()));
            if (c.getHysteresisRpm() <= 0) {
                error = true;
                JOptionPane.showMessageDialog(this, "Hysterese Drehzahl muss größer 0 sein...", "Eingabefehler!", JOptionPane.ERROR_MESSAGE);
                jtfHysteresisRpm.requestFocusInWindow();
            }
        } catch (NumberFormatException e) {
            error = true;
            JOptionPane.showMessageDialog(this, "Bitte alle Felder ausfüllen und ganzzahlige Werte eingeben!", "Eingabefehler!", JOptionPane.ERROR_MESSAGE);
            LOG.warning(e);
            jtfHysteresisRpm.requestFocusInWindow();
        }
        try {
            c.setHysteresisTime(Integer.parseInt(jtfHysteresisTime.getText()));
            if (c.getHysteresisTime() < 1000) {
                error = true;
                JOptionPane.showMessageDialog(this, "Hysterese Zeit ist " + c.getHysteresisTime() + ". Es wird eine Zeit von min. 1000ms empfohlen.", "Eingabefehler!", JOptionPane.ERROR_MESSAGE);
                jtfHysteresisTime.requestFocusInWindow();
            }
        } catch (NumberFormatException e) {
            error = true;
            JOptionPane.showMessageDialog(this, "Bitte alle Felder ausfüllen und ganzzahlige Werte eingeben!", "Eingabefehler!", JOptionPane.ERROR_MESSAGE);
            LOG.warning(e);
            jtfHysteresisTime.requestFocusInWindow();
        }
        try {
            c.setIdleKmh(Integer.parseInt(jtfIdleKmh.getText()));
        } catch (NumberFormatException e) {
            error = true;
            JOptionPane.showMessageDialog(this, "Bitte alle Felder ausfüllen und ganzzahlige Werte eingeben!", "Eingabefehler!", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace(System.err);
            jtfIdleKmh.requestFocusInWindow();
        }
        try {
            c.setIdleRpm(Integer.parseInt(jtfIdleRpm.getText()));
            if (c.getIdleRpm() <= 0) {
                error = true;
                JOptionPane.showMessageDialog(this, "Leerlaufdrehzahl zu klein!", "Eingabefehler!", JOptionPane.ERROR_MESSAGE);
                jtfIdleRpm.requestFocusInWindow();
            }
        } catch (NumberFormatException e) {
            error = true;
            JOptionPane.showMessageDialog(this, "Bitte alle Felder ausfüllen und ganzzahlige Werte eingeben!", "Eingabefehler!", JOptionPane.ERROR_MESSAGE);
            LOG.warning(e);
            jtfIdleRpm.requestFocusInWindow();
        }
        try {
            c.setPeriod(Integer.parseInt(jtfPeriod.getText()));
            if (c.getPeriod() <= 5) {
                error = true;
                JOptionPane.showMessageDialog(this, "Eine zu kleine Periodendauer wird den Computer überlasten... Min. 20ms werden empfohlen.", "Eingabefehler!", JOptionPane.ERROR_MESSAGE);
                jtfPeriod.requestFocusInWindow();
            }
        } catch (NumberFormatException e) {
            error = true;
            JOptionPane.showMessageDialog(this, "Bitte alle Felder ausfüllen und ganzzahlige Werte eingeben!", "Eingabefehler!", JOptionPane.ERROR_MESSAGE);
            LOG.warning(e);
            jtfPeriod.requestFocusInWindow();
        }
        try {
            c.setPngHeight(Integer.parseInt(jtfPngY.getText()));
            if (c.getPngHeight() < 480) {
                error = true;
                JOptionPane.showMessageDialog(this, "Bildhöhe muss min. 480px betragen...", "Eingabefehler!", JOptionPane.ERROR_MESSAGE);
                jtfPngY.requestFocusInWindow();
            }
        } catch (NumberFormatException e) {
            error = true;
            JOptionPane.showMessageDialog(this, "Bitte alle Felder ausfüllen und ganzzahlige Werte eingeben!", "Eingabefehler!", JOptionPane.ERROR_MESSAGE);
            LOG.warning(e);
            jtfPngY.requestFocusInWindow();
        }
        try {
            c.setPngWidth(Integer.parseInt(jtfPngX.getText()));
            if (c.getPngWidth() < 360) {
                error = true;
                JOptionPane.showMessageDialog(this, "Bildbreite muss min. 360px betragen...", "Eingabefehler!", JOptionPane.ERROR_MESSAGE);
                jtfPngX.requestFocusInWindow();
            }
        } catch (NumberFormatException e) {
            error = true;
            JOptionPane.showMessageDialog(this, "Bitte alle Felder ausfüllen und ganzzahlige Werte eingeben!", "Eingabefehler!", JOptionPane.ERROR_MESSAGE);
            LOG.warning(e);
            jtfPngX.requestFocusInWindow();
        }
        try {
            c.setPowerCorr(Double.parseDouble(jtfPower.getText()));
            if (c.getPowerCorr() <= 0) {
                error = true;
                JOptionPane.showMessageDialog(this, "Der Leistungskorrekturfaktor wird mit jedem Messpunkt multipliziert. Er darf nicht ≤0 sein", "Eingabefehler!", JOptionPane.ERROR_MESSAGE);
                jtfPower.requestFocusInWindow();
            }
        } catch (NumberFormatException e) {
            error = true;
            JOptionPane.showMessageDialog(this, "Bitte alle Felder ausfüllen und eine rationale Zahl eingeben!", "Eingabefehler!", JOptionPane.ERROR_MESSAGE);
            LOG.warning(e);
            jtfPower.requestFocusInWindow();
        }
        try {
            c.setStartKmh(Integer.parseInt(jtfStartKmh.getText()));
            if (c.getStartVelo() <= 0 || c.getStartVelo() < c.getIdleVelo()) {
                error = true;
                JOptionPane.showMessageDialog(this, "Startgeschwindigkeit darf nicht 0 oder kleiner als die Leerlaufgeschwindigkeit sein.", "Eingabefehler!", JOptionPane.ERROR_MESSAGE);
                jtfStartKmh.requestFocusInWindow();
            }
        } catch (NumberFormatException e) {
            error = true;
            JOptionPane.showMessageDialog(this, "Bitte alle Felder ausfüllen und ganzzahlige Werte eingeben!", "Eingabefehler!", JOptionPane.ERROR_MESSAGE);
            LOG.warning(e);
            jtfStartKmh.requestFocusInWindow();
        }
        try {
            c.setStopVelo(Integer.parseInt(jtfStopKmh.getText()));
            if (c.getStopVelo() <= 0 || c.getStopVelo() < c.getStartVelo()) {
                error = true;
                JOptionPane.showMessageDialog(this, "Stoppgeschwindigkeit darf nicht 0 oder kleiner als die Startgeschwindigkeit sein.", "Eingabefehler!", JOptionPane.ERROR_MESSAGE);
                jtfStopKmh.requestFocusInWindow();
            }
        } catch (NumberFormatException e) {
            error = true;
            JOptionPane.showMessageDialog(this, "Bitte alle Felder ausfüllen und ganzzahlige Werte eingeben!", "Eingabefehler!", JOptionPane.ERROR_MESSAGE);
            LOG.warning(e);
            jtfStartKmh.requestFocusInWindow();
        }
        try {
            c.setStartRpm(Integer.parseInt(jtfStartRpm.getText()));
            if (c.getStartRpm() <= 0 || c.getStartRpm() < c.getIdleRpm()) {
                error = true;
                JOptionPane.showMessageDialog(this, "Startdrehzahl darf nicht 0 oder kleiner als die Leerlaufdrehzahl sein.", "Eingabefehler!", JOptionPane.ERROR_MESSAGE);
                jtfStartRpm.requestFocusInWindow();
            }
        } catch (NumberFormatException e) {
            error = true;
            JOptionPane.showMessageDialog(this, "Bitte alle Felder ausfüllen und ganzzahlige Werte eingeben!", "Eingabefehler!", JOptionPane.ERROR_MESSAGE);
            LOG.warning(e);
            jtfStartRpm.requestFocusInWindow();
        }
        try {
            c.setStopRpm(Integer.parseInt(jtfStopRpm.getText()));
            if (c.getStopRpm() <= 0 || c.getStopRpm() < c.getStartRpm()) {
                error = true;
                JOptionPane.showMessageDialog(this, "Stoppdrehazhl darf nicht 0 oder kleiner als die Startdrehzahl sein.", "Eingabefehler!", JOptionPane.ERROR_MESSAGE);
                jtfStopRpm.requestFocusInWindow();
            }
        } catch (NumberFormatException e) {
            error = true;
            JOptionPane.showMessageDialog(this, "Bitte alle Felder ausfüllen und ganzzahlige Werte eingeben!", "Eingabefehler!", JOptionPane.ERROR_MESSAGE);
            LOG.warning(e);
            jtfStartRpm.requestFocusInWindow();
        }
        try {
            c.setTorqueCorr(Integer.parseInt(jtfTorque.getText()));
            if (c.getTorqueCorr() <= 0) {
                error = true;
                JOptionPane.showMessageDialog(this, "Der Drehmoment-Korrekturfaktor wird mit jedem Messpunkt multipliziert. Er darf nicht ≤0 sein", "Eingabefehler!", JOptionPane.ERROR_MESSAGE);
                jtfTorque.requestFocusInWindow();
            }
        } catch (NumberFormatException e) {
            error = true;
            JOptionPane.showMessageDialog(this, "Bitte alle Felder ausfüllen und ganzzahlige Werte eingeben!", "Eingabefehler!", JOptionPane.ERROR_MESSAGE);
            LOG.warning(e);
            jtfTorque.requestFocusInWindow();
        }

        try {
            c.setInertiaCorr(Double.parseDouble(jtfInertia.getText()));
            if (c.getInertia() <= 0) {
                error = true;
                JOptionPane.showMessageDialog(this, "Bitte korrektes Trägheitsmoment eingeben...", "Eingabefehler!", JOptionPane.ERROR_MESSAGE);
                jtfInertia.requestFocusInWindow();
            } else if (c.getInertia() != 3.7017) {
                JOptionPane.showMessageDialog(this, "Warnung: Anderes Trägheitsmoment als für das ursprüngliche System des BESDyno eingegeben...", "Warnung!", JOptionPane.WARNING_MESSAGE);
            }
        } catch (NumberFormatException e) {
            error = true;
            JOptionPane.showMessageDialog(this, "Bitte alle Felder ausfüllen und eine rationale Zahl eingeben!", "Eingabefehler!", JOptionPane.ERROR_MESSAGE);
            LOG.warning(e);
            jtfInertia.requestFocusInWindow();
        }

        try {
            c.setWarningEngTemp(Integer.parseInt(jtfEngWarning.getText()));
            if (c.getWarningEngTemp() <= 30) {
                error = true;
                JOptionPane.showMessageDialog(this, "Achtung: System warnt vor Temperaturen, die höher als Ihre eingegebene sind. Ihre jetzige Konfiguration ist zu klein.", "Eingabefehler!", JOptionPane.ERROR_MESSAGE);
                jtfEngWarning.requestFocusInWindow();
            }
        } catch (NumberFormatException e) {
            error = true;
            JOptionPane.showMessageDialog(this, "Bitte alle Felder ausfüllen und eine rationale Zahl eingeben!", "Eingabefehler!", JOptionPane.ERROR_MESSAGE);
            LOG.warning(e);
            jtfEngWarning.requestFocusInWindow();
        }

        try {
            c.setWarningExhTemp(Integer.parseInt(jtfExhWarning.getText()));
            if (c.getWarningExhTemp() <= 100) {
                error = true;
                JOptionPane.showMessageDialog(this, "Achtung: System warnt vor Temperaturen, die höher als Ihre eingegebene sind. Ihre jetzige Konfiguration ist zu klein.", "Eingabefehler!", JOptionPane.ERROR_MESSAGE);
                jtfExhWarning.requestFocusInWindow();
            }
        } catch (NumberFormatException e) {
            error = true;
            JOptionPane.showMessageDialog(this, "Bitte alle Felder ausfüllen und eine rationale Zahl eingeben!", "Eingabefehler!", JOptionPane.ERROR_MESSAGE);
            LOG.warning(e);
            jtfExhWarning.requestFocusInWindow();
        }

        try {
            c.setOrder(Integer.parseInt(jtfOrder.getText()));
            if (c.getOrder() < 0 || c.getOrder() > 10) {
                error = true;
                JOptionPane.showMessageDialog(this, "Geben Sie bitte einen Grad zwischen 1 und 5 ein!", "Eingabefehler!", JOptionPane.ERROR_MESSAGE);
                jtfOrder.requestFocusInWindow();
            }
        } catch (NumberFormatException e) {
            error = true;
            JOptionPane.showMessageDialog(this, "Bitte alle Felder ausfüllen und eine ganzzahlige Werte eingeben!", "Eingabefehler!", JOptionPane.ERROR_MESSAGE);
            LOG.warning(e);
            jtfOrder.requestFocusInWindow();
        }

        try {
            c.setSmoothing(Double.parseDouble(jtfSmoothing.getText()));
            if (c.getSmoothing() < 0 || c.getSmoothing() > 1) {
                error = true;
                JOptionPane.showMessageDialog(this, "Geben Sie bitte einen Multiplikator zwischen 0 und 1 ein!", "Eingabefehler!", JOptionPane.ERROR_MESSAGE);
                jtfSmoothing.requestFocusInWindow();
            }
        } catch (NumberFormatException e) {
            error = true;
            JOptionPane.showMessageDialog(this, "Bitte alle Felder ausfüllen und eine rationale Zahl eingeben!", "Eingabefehler!", JOptionPane.ERROR_MESSAGE);
            LOG.warning(e);
            jtfSmoothing.requestFocusInWindow();
        }

        if (!error) {
            try {
                if (save) {
                    saveBikeConfig(Config.getInstance());
                }
                saveConfig(Config.getInstance());
            } catch (Exception e) {
                error = true;
                JOptionPane.showMessageDialog(this, e.getMessage(), "Interner Fehler", JOptionPane.ERROR_MESSAGE);
                LOG.warning(e);
            }
        }
        if (!error) {
            pressedOK = true;
            dispose();
        } else {
            pressedOK = false;
        }
    }

    //Sets jTFs & jRBs from Config-File
    public void setSwingValues(Config c) {
        try {
            jtfHysteresisKmh.setText(String.format("%d", c.getHysteresisVelo()));
            jtfHysteresisRpm.setText(String.format("%d", c.getHysteresisRpm()));
            jtfHysteresisTime.setText(String.format("%d", c.getHysteresisTime()));
            jtfIdleKmh.setText(String.format("%d", c.getIdleVelo()));
            jtfIdleRpm.setText(String.format("%d", c.getIdleRpm()));
            jtfInertia.setText(Double.toString(c.getInertia()));
            jtfPeriod.setText(String.format("%d", c.getPeriod()));
            jtfPngX.setText(String.format("%d", c.getPngWidth()));
            jtfPngY.setText(String.format("%d", c.getPngHeight()));
            jtfPower.setText(String.format(Double.toString(c.getPowerCorr())));
            jtfStartKmh.setText(String.format("%d", c.getStartVelo()));
            jtfStartRpm.setText(String.format("%d", c.getStartRpm()));
            jtfStopKmh.setText(String.format("%d", c.getStopVelo()));
            jtfStopRpm.setText(String.format("%d", c.getStopRpm()));
            jtfTorque.setText(String.format("%d", c.getTorqueCorr()));
            jtfEngWarning.setText(String.format("%d", c.getWarningEngTemp()));
            jtfExhWarning.setText(String.format("%d", c.getWarningExhTemp()));
            jtfOrder.setText(String.format("%d", c.getOrder()));
            jtfSmoothing.setText(String.format(Locale.UK, "%.2f", c.getSmoothing()));

            jrbDaymode.setSelected(!c.isDark());
            jrbNightmode.setSelected(c.isDark());
            jrbKW.setSelected(!c.isPs());
            jrbPS.setSelected(c.isPs());

            jrbCelcius.setSelected(c.isCelcius());
            jrbFahrenheit.setSelected(!c.isCelcius());

            if (!c.isAverage() && !c.isPoly()) {
                jcbFilter.setSelected(false);
                jrbAverage.setSelected(false);
                jrbPoly.setSelected(false);
                jrbAverage.setEnabled(false);
                jrbPoly.setEnabled(false);
            } else {
                jrbAverage.setSelected(c.isAverage());
                jrbPoly.setSelected(c.isPoly());
            }

            if (c.isCelcius()) {
                jLabelEngWarning2.setText("°C");
                jLabelExhWarning2.setText("°C");
            } else {
                jLabelEngWarning2.setText("°F");
                jLabelExhWarning2.setText("°F");
            }

            switch (c.getVelocity()) {
                case MPS:
                    jrbMPS.setSelected(true);
                    jrbMIH.setSelected(false);
                    jrbKMH.setSelected(false);
                    jLabelIdleKmh2.setText("m/s");
                    jLabelStartKmh2.setText("m/s");
                    jLabelHysteresisKmh2.setText("m/s");
                    jLabelStopKmh2.setText("m/s");
                    break;
                case KMH:
                    jrbMPS.setSelected(false);
                    jrbMIH.setSelected(false);
                    jrbKMH.setSelected(true);
                    jLabelIdleKmh2.setText("km/h");
                    jLabelStartKmh2.setText("km/h");
                    jLabelHysteresisKmh2.setText("km/h");
                    jLabelStopKmh2.setText("km/h");
                    break;
                case MIH:
                    jrbMPS.setSelected(false);
                    jrbMIH.setSelected(true);
                    jrbKMH.setSelected(false);
                    jLabelIdleKmh2.setText("mi/h");
                    jLabelStartKmh2.setText("mi/h");
                    jLabelHysteresisKmh2.setText("mi/h");
                    jLabelStopKmh2.setText("mi/h");
                    break;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Fehler in der Konfigurationsdatei!", "Config-Error", JOptionPane.ERROR_MESSAGE);
            LOG.warning(ex);
        }
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
            folder = new File(home + File.separator + ".Bike");
            if (!folder.exists()) {
                if (!folder.mkdir()) {
                    throw new Exception("Internal Error");
                }
            }
            file = new File(folder + File.separator + "Config.json");
        } else {
            file = new File("Config.json");
        }

        if (file.exists()) {
            Config.getInstance().readJson(new FileInputStream(file));
            setSwingValues(Config.getInstance());
        }
    }

    //Saves Config-File after changing the settings
    public void saveConfig(Config config) throws Exception {
        File home;
        File folder;
        File file;

        try {
            home = new File(System.getProperty("user.home"));
        } catch (Exception e) {
            home = null;
        }

        if (home != null && home.exists()) {
            folder = new File(home + File.separator + ".Bike");
            if (!folder.exists()) {
                if (!folder.mkdir()) {
                    throw new Exception("Internal Error");
                }
            }
            file = new File(folder + File.separator + "Config.json");
        } else {
            file = new File("Config.json");
        }

        try (BufferedWriter w = new BufferedWriter(new FileWriter(file))) {
            config.writeJson(w);
            LOG.info("Config-File updated");
        }
    }

    //Loads Config from Config-Files Folder
    public void loadBikeConfig() throws Exception {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Bike-Config (*.json)", "json"));

        File file = null;
        File home;
        File folder;

        try {
            home = new File(System.getProperty("user.home"));
        } catch (Exception e) {
            home = null;
        }

        if (home != null && home.exists()) {
            folder = new File(home + File.separator + "Bike-Files" + File.separator + "Config_Files");
            if (!folder.exists()) {
                if (!folder.mkdir()) {
                    throw new Exception("Internal Error");
                }
            }
            file = new File(folder + File.separator + "_Config" + ".json");
        }

        chooser.setSelectedFile(file);

        int rv = chooser.showSaveDialog(this);
        if (rv == JFileChooser.APPROVE_OPTION) {
            file = chooser.getSelectedFile();

            if (file.exists()) {
                Config.getInstance().readJson(new FileInputStream(file));
                setSwingValues(Config.getInstance());
            }
        }
    }

    //Saves Config for special Bikes into Config-Files Folder
    public void saveBikeConfig(Config config) throws Exception {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Bike-Config (*.json)", "json"));

        File file = null;
        File home;
        File folder;
        Date date = Calendar.getInstance().getTime();
        DateFormat df = new SimpleDateFormat("yy.MM.dd-HH.mm");

        try {
            home = new File(System.getProperty("user.home"));
        } catch (Exception e) {
            home = null;
        }

        if (home != null && home.exists()) {
            folder = new File(home + File.separator + "Bike-Files" + File.separator + "Config_Files");
            if (!folder.exists()) {
                if (!folder.mkdir()) {
                    throw new Exception("Internal Error");
                }
            }
            file = new File(folder + File.separator + Bike.getInstance().getVehicleName() + "_Config_" + df.format(date) + ".json");
        }

        chooser.setSelectedFile(file);

        int rv = chooser.showSaveDialog(this);
        if (rv == JFileChooser.APPROVE_OPTION) {
            file = chooser.getSelectedFile();

            try (BufferedWriter w = new BufferedWriter(new FileWriter(file))) {
                config.writeJson(w);
                LOG.info("New Config-File saved: " + file.getPath());
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

        jbgAppearance = new javax.swing.ButtonGroup();
        jbgPower = new javax.swing.ButtonGroup();
        jbgVelocity = new javax.swing.ButtonGroup();
        jbgTemperature = new javax.swing.ButtonGroup();
        jbgFilter = new javax.swing.ButtonGroup();
        jPanMain = new javax.swing.JPanel();
        jPanWest = new javax.swing.JPanel();
        jPanAppearance = new javax.swing.JPanel();
        jrbDaymode = new javax.swing.JRadioButton();
        jrbNightmode = new javax.swing.JRadioButton();
        jPanVelocity = new javax.swing.JPanel();
        jrbMPS = new javax.swing.JRadioButton();
        jrbKMH = new javax.swing.JRadioButton();
        jrbMIH = new javax.swing.JRadioButton();
        jPanPower = new javax.swing.JPanel();
        jrbPS = new javax.swing.JRadioButton();
        jrbKW = new javax.swing.JRadioButton();
        jPanTemp = new javax.swing.JPanel();
        jrbCelcius = new javax.swing.JRadioButton();
        jrbFahrenheit = new javax.swing.JRadioButton();
        jPanCorr = new javax.swing.JPanel();
        jLabelPower = new javax.swing.JLabel();
        jtfPower = new javax.swing.JTextField();
        jLabelTorque = new javax.swing.JLabel();
        jtfTorque = new javax.swing.JTextField();
        jLabelInertia = new javax.swing.JLabel();
        jtfInertia = new javax.swing.JTextField();
        jLabelInertia2 = new javax.swing.JLabel();
        jcbEnableInertiaEdit = new javax.swing.JCheckBox();
        jPanEast = new javax.swing.JPanel();
        jPanPNG = new javax.swing.JPanel();
        jtfPngX = new javax.swing.JTextField();
        jtfPngY = new javax.swing.JTextField();
        jLabelPngX = new javax.swing.JLabel();
        jLabelPngY = new javax.swing.JLabel();
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
        jLabelStopRpm = new javax.swing.JLabel();
        jLabelStopRpm2 = new javax.swing.JLabel();
        jtfStopRpm = new javax.swing.JTextField();
        jLabelStopKmh = new javax.swing.JLabel();
        jtfStopKmh = new javax.swing.JTextField();
        jLabelStopKmh2 = new javax.swing.JLabel();
        jLabelEngWarning = new javax.swing.JLabel();
        jtfEngWarning = new javax.swing.JTextField();
        jLabelEngWarning2 = new javax.swing.JLabel();
        jLabelExhWarning = new javax.swing.JLabel();
        jtfExhWarning = new javax.swing.JTextField();
        jLabelExhWarning2 = new javax.swing.JLabel();
        jLabelHelp = new javax.swing.JLabel();
        jPanFilter = new javax.swing.JPanel();
        jLabelOrder = new javax.swing.JLabel();
        jtfOrder = new javax.swing.JTextField();
        jLabelSmoothing = new javax.swing.JLabel();
        jtfSmoothing = new javax.swing.JTextField();
        jcbFilter = new javax.swing.JCheckBox();
        jrbPoly = new javax.swing.JRadioButton();
        jrbAverage = new javax.swing.JRadioButton();
        jPanButtons = new javax.swing.JPanel();
        jPanLoad = new javax.swing.JPanel();
        jbutLoad = new javax.swing.JButton();
        jbutSave = new javax.swing.JButton();
        jPanConfirm = new javax.swing.JPanel();
        jbutCancel = new javax.swing.JButton();
        jbutOK = new javax.swing.JButton();
        jPanDevice = new javax.swing.JPanel();
        jLabelDevice1 = new javax.swing.JLabel();
        jLabelDevice2 = new javax.swing.JLabel();
        jLabelArduino1 = new javax.swing.JLabel();
        jLabelArduino2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));

        jPanMain.setBackground(new java.awt.Color(255, 255, 255));
        jPanMain.setLayout(new java.awt.GridLayout(1, 0));

        jPanWest.setBackground(new java.awt.Color(255, 255, 255));
        jPanWest.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jPanWest.setLayout(new java.awt.GridBagLayout());

        jPanAppearance.setBackground(new java.awt.Color(255, 255, 255));
        jPanAppearance.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Erscheinungsbild", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 0, 13), new java.awt.Color(255, 0, 0))); // NOI18N
        jPanAppearance.setPreferredSize(new java.awt.Dimension(330, 70));
        jPanAppearance.setLayout(new java.awt.GridBagLayout());

        jbgAppearance.add(jrbDaymode);
        jrbDaymode.setText("Hell");
        jrbDaymode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jrbDaymodeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.ipady = 1;
        gridBagConstraints.weightx = 2.0;
        gridBagConstraints.weighty = 1.0;
        jPanAppearance.add(jrbDaymode, gridBagConstraints);

        jbgAppearance.add(jrbNightmode);
        jrbNightmode.setText("Dunkel");
        jrbNightmode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jrbNightmodeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.ipady = 1;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanAppearance.add(jrbNightmode, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanWest.add(jPanAppearance, gridBagConstraints);

        jPanVelocity.setBackground(new java.awt.Color(255, 255, 255));
        jPanVelocity.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Geschwindigkeitseinheit", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 0, 13), java.awt.Color.red)); // NOI18N
        jPanVelocity.setLayout(new java.awt.GridBagLayout());

        jbgVelocity.add(jrbMPS);
        jrbMPS.setText("Meter/Sekunde (m/s)");
        jrbMPS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jrbMPSActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanVelocity.add(jrbMPS, gridBagConstraints);

        jbgVelocity.add(jrbKMH);
        jrbKMH.setText("Kilometer/Stunde (km/h)");
        jrbKMH.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jrbKMHActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanVelocity.add(jrbKMH, gridBagConstraints);

        jbgVelocity.add(jrbMIH);
        jrbMIH.setText("Meilen/Stunde (mi/h)");
        jrbMIH.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jrbMIHActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanVelocity.add(jrbMIH, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanWest.add(jPanVelocity, gridBagConstraints);

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
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanWest.add(jPanPower, gridBagConstraints);

        jPanTemp.setBackground(new java.awt.Color(255, 255, 255));
        jPanTemp.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Temperatureinheit", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 0, 13), java.awt.Color.red)); // NOI18N
        jPanTemp.setLayout(new java.awt.GridBagLayout());

        jbgTemperature.add(jrbCelcius);
        jrbCelcius.setText("Celcius °C");
        jrbCelcius.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jrbCelciusActionPerformed(evt);
            }
        });
        jPanTemp.add(jrbCelcius, new java.awt.GridBagConstraints());

        jbgTemperature.add(jrbFahrenheit);
        jrbFahrenheit.setText("Fahrenheit °F");
        jrbFahrenheit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jrbFahrenheitActionPerformed(evt);
            }
        });
        jPanTemp.add(jrbFahrenheit, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanWest.add(jPanTemp, gridBagConstraints);

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

        jcbEnableInertiaEdit.setText("Trägheitsmoment bearbeiten");
        jcbEnableInertiaEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbEnableInertiaEditActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanCorr.add(jcbEnableInertiaEdit, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanWest.add(jPanCorr, gridBagConstraints);

        jPanMain.add(jPanWest);

        jPanEast.setBackground(new java.awt.Color(255, 255, 255));
        jPanEast.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jPanEast.setLayout(new java.awt.GridBagLayout());

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
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanEast.add(jPanPNG, gridBagConstraints);

        jPanSerial.setBackground(new java.awt.Color(255, 255, 255));
        jPanSerial.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Kommunikation", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 0, 13), new java.awt.Color(255, 0, 0))); // NOI18N
        jPanSerial.setMinimumSize(new java.awt.Dimension(350, 232));
        jPanSerial.setPreferredSize(new java.awt.Dimension(330, 232));
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
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 13);
        jPanSerial.add(jLabelIdlRpm, gridBagConstraints);

        jLabelIdleRpm2.setText("U/min");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        jPanSerial.add(jLabelIdleRpm2, gridBagConstraints);

        jLabelHysteresisRpm.setText("Hysterese Motordrehzahl +/-");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 13);
        jPanSerial.add(jLabelHysteresisRpm, gridBagConstraints);

        jLabelHysteresisRpm2.setText("U/min");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        jPanSerial.add(jLabelHysteresisRpm2, gridBagConstraints);

        jLabelStartRpm.setText("Start-Motordrehzahl");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 13);
        jPanSerial.add(jLabelStartRpm, gridBagConstraints);

        jLabelStartRpm2.setText("U/min");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 8;
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
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanSerial.add(jtfIdleRpm, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanSerial.add(jtfHysteresisRpm, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
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

        jLabelStopRpm.setText("Stop-Motordrehzahl");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 13);
        jPanSerial.add(jLabelStopRpm, gridBagConstraints);

        jLabelStopRpm2.setText("U/min");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        jPanSerial.add(jLabelStopRpm2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanSerial.add(jtfStopRpm, gridBagConstraints);

        jLabelStopKmh.setText("Stoppgeschwindigkeit");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 13);
        jPanSerial.add(jLabelStopKmh, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanSerial.add(jtfStopKmh, gridBagConstraints);

        jLabelStopKmh2.setText("Km/h");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        jPanSerial.add(jLabelStopKmh2, gridBagConstraints);

        jLabelEngWarning.setText("Max. Motortemperatur");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 13);
        jPanSerial.add(jLabelEngWarning, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanSerial.add(jtfEngWarning, gridBagConstraints);

        jLabelEngWarning2.setText("°C");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        jPanSerial.add(jLabelEngWarning2, gridBagConstraints);

        jLabelExhWarning.setText("Max. Abgastemperatur");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 13);
        jPanSerial.add(jLabelExhWarning, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanSerial.add(jtfExhWarning, gridBagConstraints);

        jLabelExhWarning2.setText("°C");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        jPanSerial.add(jLabelExhWarning2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanEast.add(jPanSerial, gridBagConstraints);

        jLabelHelp.setFont(new java.awt.Font("Lucida Grande", 2, 11)); // NOI18N
        jLabelHelp.setText("Hilfe beim Setzen der Parameter finden Sie im Help-Menü.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        jPanEast.add(jLabelHelp, gridBagConstraints);

        jPanFilter.setBackground(new java.awt.Color(255, 255, 255));
        jPanFilter.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Filter", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 0, 13), java.awt.Color.red)); // NOI18N
        jPanFilter.setLayout(new java.awt.GridBagLayout());

        jLabelOrder.setText("Grad des Filters");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanFilter.add(jLabelOrder, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanFilter.add(jtfOrder, gridBagConstraints);

        jLabelSmoothing.setText("Multiplikator");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanFilter.add(jLabelSmoothing, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanFilter.add(jtfSmoothing, gridBagConstraints);

        jcbFilter.setText("Weiterer Filter");
        jcbFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbFilterActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanFilter.add(jcbFilter, gridBagConstraints);

        jbgFilter.add(jrbPoly);
        jrbPoly.setText("Näherungs-Polynom");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanFilter.add(jrbPoly, gridBagConstraints);

        jbgFilter.add(jrbAverage);
        jrbAverage.setText("Gleitender Mittelwert");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanFilter.add(jrbAverage, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanEast.add(jPanFilter, gridBagConstraints);

        jPanMain.add(jPanEast);

        getContentPane().add(jPanMain, java.awt.BorderLayout.CENTER);

        jPanButtons.setBackground(new java.awt.Color(255, 255, 255));
        jPanButtons.setLayout(new java.awt.GridLayout(1, 0));

        jPanLoad.setBackground(new java.awt.Color(255, 255, 255));
        jPanLoad.setLayout(new java.awt.GridBagLayout());

        jbutLoad.setText("Laden");
        jbutLoad.setPreferredSize(new java.awt.Dimension(125, 29));
        jbutLoad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onLoad(evt);
            }
        });
        jPanLoad.add(jbutLoad, new java.awt.GridBagConstraints());

        jbutSave.setText("Speichern");
        jbutSave.setPreferredSize(new java.awt.Dimension(125, 29));
        jbutSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onSave(evt);
            }
        });
        jPanLoad.add(jbutSave, new java.awt.GridBagConstraints());

        jPanButtons.add(jPanLoad);

        jPanConfirm.setBackground(new java.awt.Color(255, 255, 255));
        jPanConfirm.setLayout(new java.awt.GridBagLayout());

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
        jPanConfirm.add(jbutCancel, gridBagConstraints);

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
        jPanConfirm.add(jbutOK, gridBagConstraints);

        jPanButtons.add(jPanConfirm);

        getContentPane().add(jPanButtons, java.awt.BorderLayout.SOUTH);

        jPanDevice.setBackground(new java.awt.Color(255, 255, 255));
        jPanDevice.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20), javax.swing.BorderFactory.createTitledBorder(null, "Endgerät", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 0, 13), java.awt.Color.red))); // NOI18N
        jPanDevice.setLayout(new java.awt.GridBagLayout());

        jLabelDevice1.setText("Verbundener Prüfstand:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        jPanDevice.add(jLabelDevice1, gridBagConstraints);

        jLabelDevice2.setText("jLabel1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        jPanDevice.add(jLabelDevice2, gridBagConstraints);

        jLabelArduino1.setText("Prüfstand-Firmware-Version:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        jPanDevice.add(jLabelArduino1, gridBagConstraints);

        jLabelArduino2.setText("jLabel2");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        jPanDevice.add(jLabelArduino2, gridBagConstraints);

        getContentPane().add(jPanDevice, java.awt.BorderLayout.NORTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jrbDaymodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jrbDaymodeActionPerformed
        setAppearance(!jrbDaymode.isSelected(), null);
    }//GEN-LAST:event_jrbDaymodeActionPerformed

    private void jrbNightmodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jrbNightmodeActionPerformed
        setAppearance(jrbNightmode.isSelected(), null);
    }//GEN-LAST:event_jrbNightmodeActionPerformed

    private void jbutOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbutOKActionPerformed
        confirm(Config.getInstance(), false);
    }//GEN-LAST:event_jbutOKActionPerformed

    private void jbutCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbutCancelActionPerformed
        pressedOK = false;
        dispose();
    }//GEN-LAST:event_jbutCancelActionPerformed

    private void jrbMPSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jrbMPSActionPerformed
        jLabelIdleKmh2.setText("m/s");
        jLabelStartKmh2.setText("m/s");
        jLabelStopKmh2.setText("m/s");
        jLabelHysteresisKmh2.setText("m/s");

        jtfIdleKmh.setText(convertVelocity(lastVelocity, Velocity.MPS, Integer.parseInt(jtfIdleKmh.getText())));
        jtfStartKmh.setText(convertVelocity(lastVelocity, Velocity.MPS, Integer.parseInt(jtfStartKmh.getText())));
        jtfStopKmh.setText(convertVelocity(lastVelocity, Velocity.MPS, Integer.parseInt(jtfStopKmh.getText())));
        jtfHysteresisKmh.setText(convertVelocity(lastVelocity, Velocity.MPS, Integer.parseInt(jtfHysteresisKmh.getText())));

        lastVelocity = Velocity.MPS;
    }//GEN-LAST:event_jrbMPSActionPerformed

    private void jrbKMHActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jrbKMHActionPerformed
        jLabelIdleKmh2.setText("km/h");
        jLabelStartKmh2.setText("km/h");
        jLabelStopKmh2.setText("km/h");
        jLabelHysteresisKmh2.setText("km/h");

        jtfIdleKmh.setText(convertVelocity(lastVelocity, Velocity.KMH, Integer.parseInt(jtfIdleKmh.getText())));
        jtfStartKmh.setText(convertVelocity(lastVelocity, Velocity.KMH, Integer.parseInt(jtfStartKmh.getText())));
        jtfStopKmh.setText(convertVelocity(lastVelocity, Velocity.KMH, Integer.parseInt(jtfStopKmh.getText())));
        jtfHysteresisKmh.setText(convertVelocity(lastVelocity, Velocity.KMH, Integer.parseInt(jtfHysteresisKmh.getText())));

        lastVelocity = Velocity.KMH;
    }//GEN-LAST:event_jrbKMHActionPerformed

    private void jrbMIHActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jrbMIHActionPerformed
        jLabelIdleKmh2.setText("mi/h");
        jLabelStartKmh2.setText("mi/h");
        jLabelStopKmh2.setText("mi/h");
        jLabelHysteresisKmh2.setText("mi/h");

        jtfIdleKmh.setText(convertVelocity(lastVelocity, Velocity.MIH, Integer.parseInt(jtfIdleKmh.getText())));
        jtfStartKmh.setText(convertVelocity(lastVelocity, Velocity.MIH, Integer.parseInt(jtfStartKmh.getText())));
        jtfStopKmh.setText(convertVelocity(lastVelocity, Velocity.MIH, Integer.parseInt(jtfStopKmh.getText())));
        jtfHysteresisKmh.setText(convertVelocity(lastVelocity, Velocity.MIH, Integer.parseInt(jtfHysteresisKmh.getText())));

        lastVelocity = Velocity.MIH;
    }//GEN-LAST:event_jrbMIHActionPerformed

    private void onLoad(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onLoad
        try {
            loadBikeConfig();
        } catch (Exception ex) {
            LOG.warning(ex);
        }
    }//GEN-LAST:event_onLoad

    private void onSave(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onSave
        confirm(Config.getInstance(), true);
    }//GEN-LAST:event_onSave

    private void jrbCelciusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jrbCelciusActionPerformed
        jLabelEngWarning2.setText("°C");
        jLabelExhWarning2.setText("°C");

        jtfEngWarning.setText(convertTemperature(false, Integer.parseInt(jtfEngWarning.getText())));
        jtfExhWarning.setText(convertTemperature(false, Integer.parseInt(jtfExhWarning.getText())));
    }//GEN-LAST:event_jrbCelciusActionPerformed

    private void jrbFahrenheitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jrbFahrenheitActionPerformed
        jLabelEngWarning2.setText("°F");
        jLabelExhWarning2.setText("°F");

        jtfEngWarning.setText(convertTemperature(true, Integer.parseInt(jtfEngWarning.getText())));
        jtfExhWarning.setText(convertTemperature(true, Integer.parseInt(jtfExhWarning.getText())));
    }//GEN-LAST:event_jrbFahrenheitActionPerformed

    private void jcbEnableInertiaEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbEnableInertiaEditActionPerformed
        if (jcbEnableInertiaEdit.isSelected()) {
            jtfInertia.setEnabled(true);
        } else {
            jtfInertia.setEnabled(false);
        }
    }//GEN-LAST:event_jcbEnableInertiaEditActionPerformed

    private void jcbFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbFilterActionPerformed
        if (jcbFilter.isSelected()) {
            jrbPoly.setEnabled(true);
            jrbAverage.setEnabled(true);
        } else {
            jrbPoly.setEnabled(false);
            jrbAverage.setEnabled(false);
            jrbPoly.setSelected(false);
            jrbAverage.setSelected(false);
        }
    }//GEN-LAST:event_jcbFilterActionPerformed

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
    private javax.swing.JLabel jLabelArduino1;
    private javax.swing.JLabel jLabelArduino2;
    private javax.swing.JLabel jLabelDevice1;
    private javax.swing.JLabel jLabelDevice2;
    private javax.swing.JLabel jLabelEngWarning;
    private javax.swing.JLabel jLabelEngWarning2;
    private javax.swing.JLabel jLabelExhWarning;
    private javax.swing.JLabel jLabelExhWarning2;
    private javax.swing.JLabel jLabelHelp;
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
    private javax.swing.JLabel jLabelOrder;
    private javax.swing.JLabel jLabelPeriod;
    private javax.swing.JLabel jLabelPeriod2;
    private javax.swing.JLabel jLabelPngX;
    private javax.swing.JLabel jLabelPngY;
    private javax.swing.JLabel jLabelPower;
    private javax.swing.JLabel jLabelSmoothing;
    private javax.swing.JLabel jLabelStartKmh;
    private javax.swing.JLabel jLabelStartKmh2;
    private javax.swing.JLabel jLabelStartRpm;
    private javax.swing.JLabel jLabelStartRpm2;
    private javax.swing.JLabel jLabelStopKmh;
    private javax.swing.JLabel jLabelStopKmh2;
    private javax.swing.JLabel jLabelStopRpm;
    private javax.swing.JLabel jLabelStopRpm2;
    private javax.swing.JLabel jLabelTorque;
    private javax.swing.JPanel jPanAppearance;
    private javax.swing.JPanel jPanButtons;
    private javax.swing.JPanel jPanConfirm;
    private javax.swing.JPanel jPanCorr;
    private javax.swing.JPanel jPanDevice;
    private javax.swing.JPanel jPanEast;
    private javax.swing.JPanel jPanFilter;
    private javax.swing.JPanel jPanLoad;
    private javax.swing.JPanel jPanMain;
    private javax.swing.JPanel jPanPNG;
    private javax.swing.JPanel jPanPower;
    private javax.swing.JPanel jPanSerial;
    private javax.swing.JPanel jPanTemp;
    private javax.swing.JPanel jPanVelocity;
    private javax.swing.JPanel jPanWest;
    private javax.swing.ButtonGroup jbgAppearance;
    private javax.swing.ButtonGroup jbgFilter;
    private javax.swing.ButtonGroup jbgPower;
    private javax.swing.ButtonGroup jbgTemperature;
    private javax.swing.ButtonGroup jbgVelocity;
    private javax.swing.JButton jbutCancel;
    private javax.swing.JButton jbutLoad;
    private javax.swing.JButton jbutOK;
    private javax.swing.JButton jbutSave;
    private javax.swing.JCheckBox jcbEnableInertiaEdit;
    private javax.swing.JCheckBox jcbFilter;
    private javax.swing.JRadioButton jrbAverage;
    private javax.swing.JRadioButton jrbCelcius;
    private javax.swing.JRadioButton jrbDaymode;
    private javax.swing.JRadioButton jrbFahrenheit;
    private javax.swing.JRadioButton jrbKMH;
    private javax.swing.JRadioButton jrbKW;
    private javax.swing.JRadioButton jrbMIH;
    private javax.swing.JRadioButton jrbMPS;
    private javax.swing.JRadioButton jrbNightmode;
    private javax.swing.JRadioButton jrbPS;
    private javax.swing.JRadioButton jrbPoly;
    private javax.swing.JTextField jtfEngWarning;
    private javax.swing.JTextField jtfExhWarning;
    private javax.swing.JTextField jtfHysteresisKmh;
    private javax.swing.JTextField jtfHysteresisRpm;
    private javax.swing.JTextField jtfHysteresisTime;
    private javax.swing.JTextField jtfIdleKmh;
    private javax.swing.JTextField jtfIdleRpm;
    private javax.swing.JTextField jtfInertia;
    private javax.swing.JTextField jtfOrder;
    private javax.swing.JTextField jtfPeriod;
    private javax.swing.JTextField jtfPngX;
    private javax.swing.JTextField jtfPngY;
    private javax.swing.JTextField jtfPower;
    private javax.swing.JTextField jtfSmoothing;
    private javax.swing.JTextField jtfStartKmh;
    private javax.swing.JTextField jtfStartRpm;
    private javax.swing.JTextField jtfStopKmh;
    private javax.swing.JTextField jtfStopRpm;
    private javax.swing.JTextField jtfTorque;
    // End of variables declaration//GEN-END:variables
}
