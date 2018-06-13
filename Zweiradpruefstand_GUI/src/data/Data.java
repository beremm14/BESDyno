/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import java.util.ArrayList;

/**
 *
 * @author emil
 */
public class Data {

    public Data() {}
    

    private String filePath  = "Error im Benutzerverzeichnis beheben";
    private String vehicle = "Puch M50 SG";
    private String powerunit = "PS";

    private double inertia = 3.7017;
    private double vmax = 0;
    private double temperature = 20;
    private double pressure = 1013;
    private double correctionPower = 1.0;
    private double correctionTorque = 1.0;
    private double maxpower = 0.0;
    private double maxtorque = 0.0;

    private int startKMH = 10;
    private int startRPM = 3200;
    private int idleKMH = 4;
    private int idleRPM = 2500;
    private int pngWidth = 800;
    private int pngHeight = 600;
    private int windowWidth = 1200;
    private int windowHeight = 700;
    private int windowRelativeX = 0;
    private int windowRelativeY = 0;
    private int periodTimeMs = 20;
    private int humidity = 40;
    private int hysteresisRPM = 400;
    private int hysteresisKMH = 2;
    private int hysteresisTIME = 3000;

    private double filterTrqSmoothing = 0.3;//0.4;
    private int filterTrqOrder = 4;

    private double filterOmegaSmoothing = 0.6;//0.5;
    private int filterOmegaOrder = 4;//1;

    private double filterAlphaSmoothing = 0.4;
    private int filterAlphaOrder = 5;//4;

    private double filterRpmSmoothing = 0.6;//0.5;
    private int filterRpmOrder = 5;//2;

    //private ArrayList<RawDatapoint> rawDataList = new ArrayList<>();
    //private ArrayList<Datapoint> measureList = null;
    private boolean twoStroke = true;
    private boolean measRPM = true;
    private boolean automatic = false;
    private boolean schleppEnable = true;
    
    
    //Getter & Setter

    public String getFilePath() {
        return filePath;
    }

    public String getVehicle() {
        return vehicle;
    }

    public String getPowerunit() {
        return powerunit;
    }

    public double getInertia() {
        return inertia;
    }

    public double getVmax() {
        return vmax;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getPressure() {
        return pressure;
    }

    public double getCorrectionPower() {
        return correctionPower;
    }

    public double getCorrectionTorque() {
        return correctionTorque;
    }

    public double getMaxpower() {
        return maxpower;
    }

    public double getMaxtorque() {
        return maxtorque;
    }

    public int getStartKMH() {
        return startKMH;
    }

    public int getStartRPM() {
        return startRPM;
    }

    public int getIdleKMH() {
        return idleKMH;
    }

    public int getIdleRPM() {
        return idleRPM;
    }

    public int getPngWidth() {
        return pngWidth;
    }

    public int getPngHeight() {
        return pngHeight;
    }

    public int getWindowWidth() {
        return windowWidth;
    }

    public int getWindowHeight() {
        return windowHeight;
    }

    public int getWindowRelativeX() {
        return windowRelativeX;
    }

    public int getWindowRelativeY() {
        return windowRelativeY;
    }

    public int getPeriodTimeMs() {
        return periodTimeMs;
    }

    public int getHumidity() {
        return humidity;
    }

    public int getHysteresisRPM() {
        return hysteresisRPM;
    }

    public int getHysteresisKMH() {
        return hysteresisKMH;
    }

    public int getHysteresisTIME() {
        return hysteresisTIME;
    }

    public double getFilterTrqSmoothing() {
        return filterTrqSmoothing;
    }

    public int getFilterTrqOrder() {
        return filterTrqOrder;
    }

    public double getFilterOmegaSmoothing() {
        return filterOmegaSmoothing;
    }

    public int getFilterOmegaOrder() {
        return filterOmegaOrder;
    }

    public double getFilterAlphaSmoothing() {
        return filterAlphaSmoothing;
    }

    public int getFilterAlphaOrder() {
        return filterAlphaOrder;
    }

    public double getFilterRpmSmoothing() {
        return filterRpmSmoothing;
    }

    public int getFilterRpmOrder() {
        return filterRpmOrder;
    }

    public boolean isTwoStroke() {
        return twoStroke;
    }

    public boolean isMeasRPM() {
        return measRPM;
    }

    public boolean isAutomatic() {
        return automatic;
    }

    public boolean isSchleppEnable() {
        return schleppEnable;
    }
    
    
    //Setter
    
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setVehicle(String vehicle) {
        this.vehicle = vehicle;
    }

    public void setPowerunit(String powerunit) {
        this.powerunit = powerunit;
    }

    public void setInertia(double inertia) {
        this.inertia = inertia;
    }

    public void setVmax(double vmax) {
        this.vmax = vmax;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public void setPressure(double pressure) {
        this.pressure = pressure;
    }

    public void setCorrectionPower(double correctionPower) {
        this.correctionPower = correctionPower;
    }

    public void setCorrectionTorque(double correctionTorque) {
        this.correctionTorque = correctionTorque;
    }

    public void setMaxpower(double maxpower) {
        this.maxpower = maxpower;
    }

    public void setMaxtorque(double maxtorque) {
        this.maxtorque = maxtorque;
    }

    public void setStartKMH(int startKMH) {
        this.startKMH = startKMH;
    }

    public void setStartRPM(int startRPM) {
        this.startRPM = startRPM;
    }

    public void setIdleKMH(int idleKMH) {
        this.idleKMH = idleKMH;
    }

    public void setIdleRPM(int idleRPM) {
        this.idleRPM = idleRPM;
    }

    public void setPngWidth(int pngWidth) {
        this.pngWidth = pngWidth;
    }

    public void setPngHeight(int pngHeight) {
        this.pngHeight = pngHeight;
    }

    public void setWindowWidth(int windowWidth) {
        this.windowWidth = windowWidth;
    }

    public void setWindowHeight(int windowHeight) {
        this.windowHeight = windowHeight;
    }

    public void setWindowRelativeX(int windowRelativeX) {
        this.windowRelativeX = windowRelativeX;
    }

    public void setWindowRelativeY(int windowRelativeY) {
        this.windowRelativeY = windowRelativeY;
    }

    public void setPeriodTimeMs(int periodTimeMs) {
        this.periodTimeMs = periodTimeMs;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public void setHysteresisRPM(int hysteresisRPM) {
        this.hysteresisRPM = hysteresisRPM;
    }

    public void setHysteresisKMH(int hysteresisKMH) {
        this.hysteresisKMH = hysteresisKMH;
    }

    public void setHysteresisTIME(int hysteresisTIME) {
        this.hysteresisTIME = hysteresisTIME;
    }

    public void setFilterTrqSmoothing(double filterTrqSmoothing) {
        this.filterTrqSmoothing = filterTrqSmoothing;
    }

    public void setFilterTrqOrder(int filterTrqOrder) {
        this.filterTrqOrder = filterTrqOrder;
    }

    public void setFilterOmegaSmoothing(double filterOmegaSmoothing) {
        this.filterOmegaSmoothing = filterOmegaSmoothing;
    }

    public void setFilterOmegaOrder(int filterOmegaOrder) {
        this.filterOmegaOrder = filterOmegaOrder;
    }

    public void setFilterAlphaSmoothing(double filterAlphaSmoothing) {
        this.filterAlphaSmoothing = filterAlphaSmoothing;
    }

    public void setFilterAlphaOrder(int filterAlphaOrder) {
        this.filterAlphaOrder = filterAlphaOrder;
    }

    public void setFilterRpmSmoothing(double filterRpmSmoothing) {
        this.filterRpmSmoothing = filterRpmSmoothing;
    }

    public void setFilterRpmOrder(int filterRpmOrder) {
        this.filterRpmOrder = filterRpmOrder;
    }

    public void setTwoStroke(boolean twoStroke) {
        this.twoStroke = twoStroke;
    }

    public void setMeasRPM(boolean measRPM) {
        this.measRPM = measRPM;
    }

    public void setAutomatic(boolean automatic) {
        this.automatic = automatic;
    }

    public void setSchleppEnable(boolean schleppEnable) {
        this.schleppEnable = schleppEnable;
    }
    

}
