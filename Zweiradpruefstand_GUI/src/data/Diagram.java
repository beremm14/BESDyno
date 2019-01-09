package data;

/**
 *
 * @author emil
 */
public class Diagram {
    
    private static Diagram instance;
    
    //Include Data:
    private boolean engPower;
    private boolean wheelPower;
    private boolean engTorque;
    private boolean wheelTorque;
    
    //Which Rpm:
    private boolean engRpm;
    
    //Environment-Data:
    private boolean envTemp;
    private boolean envPress;
    private boolean envAlt;
    private boolean engTemp;
    private boolean fumeTemp;
    
    //Max-Werte
    private boolean maxPower;
    private boolean maxVelocity;
    private boolean maxTorque;
    
    public static Diagram getInstance() {
        if (instance == null) {
            instance = new Diagram();
        }
        return instance;
    }

    private Diagram() {
    }

    //Getter
    public boolean isEngPower() {
        return engPower;
    }

    public boolean isWheelPower() {
        return wheelPower;
    }

    public boolean isEngTorque() {
        return engTorque;
    }

    public boolean isWheelTorque() {
        return wheelTorque;
    }

    public boolean isEngRpm() {
        return engRpm;
    }

    public boolean isEnvTemp() {
        return envTemp;
    }

    public boolean isEnvPress() {
        return envPress;
    }

    public boolean isEnvAlt() {
        return envAlt;
    }

    public boolean isEngTemp() {
        return engTemp;
    }

    public boolean isFumeTemp() {
        return fumeTemp;
    }

    public boolean isMaxPower() {
        return maxPower;
    }

    public boolean isMaxVelocity() {
        return maxVelocity;
    }

    public boolean isMaxTorque() {
        return maxTorque;
    }

    //Setter
    public void setEngPower(boolean engPower) {
        this.engPower = engPower;
    }

    public void setWheelPower(boolean wheelPower) {
        this.wheelPower = wheelPower;
    }

    public void setEngTorque(boolean engTorque) {
        this.engTorque = engTorque;
    }

    public void setWheelTorque(boolean wheelTorque) {
        this.wheelTorque = wheelTorque;
    }

    public void setEngRpm(boolean engRpm) {
        this.engRpm = engRpm;
    }

    public void setEnvTemp(boolean envTemp) {
        this.envTemp = envTemp;
    }

    public void setEnvPress(boolean envPress) {
        this.envPress = envPress;
    }

    public void setEnvAlt(boolean envAlt) {
        this.envAlt = envAlt;
    }

    public void setEngTemp(boolean engTemp) {
        this.engTemp = engTemp;
    }

    public void setFumeTemp(boolean fumeTemp) {
        this.fumeTemp = fumeTemp;
    }

    public void setMaxPower(boolean maxPower) {
        this.maxPower = maxPower;
    }

    public void setMaxVelocity(boolean maxVelocity) {
        this.maxVelocity = maxVelocity;
    }

    public void setMaxTorque(boolean maxTorque) {
        this.maxTorque = maxTorque;
    }

}
