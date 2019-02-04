package data;

import data.Config.Velocity;
import measure.Calculate;
import measure.MeasurementWorker.Status;

/**
 *
 * @author emil
 */
public class DialData {
    
    private double wheelVelo;
    private double engRpm;
    private Velocity unit;
    private String unitToString;
    private int wheelRef;
    private int engRef;
    private double engTemp;
    private double fumeTemp;
    
    private Status status;

    public DialData(Status status, double wheelVelo, double engRpm, int wheelRef, int engRef) {
        this.wheelVelo = wheelVelo;
        this.engRpm = engRpm;
        this.unit = Config.getInstance().getVelocity();
        this.status = status;
        this.wheelRef = wheelRef;
        this.engRef = engRef;
        
        switch(this.unit) {
            case MPS: this.unitToString = "m/s"; break;
            case MIH: this.unitToString = "mi/h"; break;
            case KMH: this.unitToString = "km/h"; break;
        }
    }

    public DialData(Status status, PreDatapoint pdp, int wheelRef, int engRef) {
        Calculate calc = new Calculate();
        this.engRpm = pdp.getEngRpm();
        this.unit = Config.getInstance().getVelocity();
        switch(this.unit) {
            case MPS:
                this.wheelVelo = calc.calcMps(pdp);
                break;
            case KMH:
                this.wheelVelo = calc.calcKmh(pdp);
                break;
            case MIH:
                this.wheelVelo = calc.calcMih(pdp);
                break;
        }
        
        this.status = status;
        
        switch(this.unit) {
            case MPS: this.unitToString = "m/s"; break;
            case MIH: this.unitToString = "mi/h"; break;
            case KMH: this.unitToString = "km/h"; break;
        }
        
        this.wheelRef = wheelRef;
        this.engRef = engRef;
    }
    
    public DialData(Status status, PreDatapoint pdp, int wheelRef, int engRef, double engTemp, double fumeTemp) {
        Calculate calc = new Calculate();
        this.engRpm = pdp.getEngRpm();
        this.unit = Config.getInstance().getVelocity();
        switch(this.unit) {
            case MPS:
                this.wheelVelo = calc.calcMps(pdp);
                break;
            case KMH:
                this.wheelVelo = calc.calcKmh(pdp);
                break;
            case MIH:
                this.wheelVelo = calc.calcMih(pdp);
                break;
        }
        
        this.status = status;
        
        switch(this.unit) {
            case MPS: this.unitToString = "m/s"; break;
            case MIH: this.unitToString = "mi/h"; break;
            case KMH: this.unitToString = "km/h"; break;
        }
        
        this.wheelRef = wheelRef;
        this.engRef = engRef;
        this.engTemp = engTemp;
        this.fumeTemp = fumeTemp;
    }
    
    public DialData(Status status, double wheelVelo, int wheelRef) {
        this.engRpm = 0;
        this.wheelVelo = wheelVelo;
        this.unit = Config.getInstance().getVelocity();
        this.status = status;
        switch(this.unit) {
            case MPS: this.unitToString = "m/s"; break;
            case MIH: this.unitToString = "mi/h"; break;
            case KMH: this.unitToString = "km/h"; break;
        }
        this.wheelRef = wheelRef;
    }

    public double getWheelVelo() {
        return wheelVelo;
    }

    public double getEngRpm() {
        return engRpm;
    }

    public Velocity getUnit() {
        return unit;
    }
    
    public String getStatusText() throws Exception {
        switch(status) {
            case SHIFT_UP: return "HOCHSCHALTEN";
            case WAIT: return "WARTEN...";
            case READY: return "BEREIT";
            case MEASURE: return "MESSUNG GESTARTET";
            case FINISH : return "FERTIG";
            default: throw new Exception("Wrong Status");
        }
    }

    public Status getStatus() {
        return status;
    }

    public String getUnitToString() {
        return unitToString;
    }

    public int getWheelRef() {
        return wheelRef;
    }

    public int getEngRef() {
        return engRef;
    }

    public double getEngTemp() {
        return engTemp;
    }

    public double getFumeTemp() {
        return fumeTemp;
    }

}
