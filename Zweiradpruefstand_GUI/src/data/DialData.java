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
    private int engRpm;
    private Velocity unit;
    
    private Status status;

    public DialData(Status status, double wheelVelo, int engRpm) {
        this.wheelVelo = wheelVelo;
        this.engRpm = engRpm;
        this.unit = Config.getInstance().getVelocity();
        this.status = status;
    }

    public DialData(Status status, Datapoint dp) {
        Calculate calc = new Calculate();
        this.engRpm = dp.getEngRpm();
        this.unit = Config.getInstance().getVelocity();
        switch(this.unit) {
            case MPS:
                this.wheelVelo = calc.calcMps(dp);
                break;
            case KMH:
                this.wheelVelo = calc.calcKmh(dp);
                break;
            case MIH:
                this.wheelVelo = calc.calcMph(dp);
                break;
        }
        this.status = status;
    }
    
    public DialData(Status status, double wheelVelo) {
        this.engRpm = 0;
        this.wheelVelo = wheelVelo;
        this.unit = Config.getInstance().getVelocity();
        this.status = status;
    }

    public double getWheelVelo() {
        return wheelVelo;
    }

    public int getEngRpm() {
        return engRpm;
    }

    public Velocity getUnit() {
        return unit;
    }

}
