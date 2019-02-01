package data;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.jfree.data.xy.XYSeries;

/**
 *
 * @author emil
 */
public class Database {

    private static Database instance = null;

    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    /*
    Informationen zur Auswahl der Collections:
    
    Quelle: https://stackoverflow.com/questions/322715/when-to-use-linkedlist-over-arraylist-in-java
    
    LinkedList: Für Lists, über die iteriert wird und die immer vollständig ausgelesen/gefüllt werden.
    ArrayList:  Für Lists, aus denen ein Wert auch "zwischendurch" ausgelesen wird.
     */
    //Chart-Values
    private final XYSeries seriesPower = new XYSeries("Power Final");
    private final XYSeries seriesTorque = new XYSeries("Torque Final");

    //Velocity (m/s or km/h or mi/h)
    private final List<Double> velList = new ArrayList<>();

    //Calculated List
    private final List<Datapoint> dataList = new LinkedList<>();

    //Schlepp-Lists for Drop-RPM
    private final List<PreDatapoint> schleppPreList = new LinkedList<>();
    private final List<Datapoint> schleppDataList = new LinkedList<>();

    //Pre-Calculated List
    private final List<PreDatapoint> preList = new ArrayList<>();

    //Uncalculated List
    private final List<RawDatapoint> rawList = new ArrayList<>();

    //MAX-Values
    private double bikePower;
    private double bikeVelo;
    private double bikeTorque;

    //Max-Values-Indices
    private int maxPowerIndex;
    private int maxVeloIndex;
    private int maxTorqueIndex;

    //Sync-Object
    public final Object syncObj = new Object();

    private Database() {
    }

    //Getter
    public List<Double> getVelList() {
        return velList;
    }

    public List<RawDatapoint> getRawList() {
        return rawList;
    }

    public List<PreDatapoint> getPreList() {
        return preList;
    }

    public List<Datapoint> getDataList() {
        return dataList;
    }

    public List<PreDatapoint> getSchleppPreList() {
        return schleppPreList;
    }

    public List<Datapoint> getSchleppDataList() {
        return schleppDataList;
    }

    public double getBikePower() {
        return bikePower;
    }

    public double getBikeVelo() {
        return bikeVelo;
    }

    public double getBikeTorque() {
        return bikeTorque;
    }

    public XYSeries getSeriesPower() {
        return seriesPower;
    }

    public XYSeries getSeriesTorque() {
        return seriesTorque;
    }

    public int getMaxPowerIndex() {
        return maxPowerIndex;
    }

    public int getMaxVeloIndex() {
        return maxVeloIndex;
    }

    public int getMaxTorqueIndex() {
        return maxTorqueIndex;
    }

    public Object getSyncObj() {
        return syncObj;
    }

    //Setter
    public void setBikePower(double bikePower) {
        this.bikePower = bikePower;
    }

    public void setBikeVelo(double bikeVelo) {
        this.bikeVelo = bikeVelo;
    }

    public void setBikeTorque(double bikeTorque) {
        this.bikeTorque = bikeTorque;
    }

    public void setMaxPowerIndex(int maxPowerIndex) {
        this.maxPowerIndex = maxPowerIndex;
    }

    public void setMaxVeloIndex(int maxVeloIndex) {
        this.maxVeloIndex = maxVeloIndex;
    }

    public void setMaxTorqueIndex(int maxTorqueIndex) {
        this.maxTorqueIndex = maxTorqueIndex;
    }

    //List-Methods
    public boolean addRawDP(RawDatapoint rdp) {
        return rawList.add(rdp);
    }

    public boolean addPreDP(PreDatapoint pdp) {
        return preList.add(pdp);
    }

    public boolean addPreSchlepp(PreDatapoint pdp) {
        return schleppPreList.add(pdp);
    }

    public boolean addSchleppDP(Datapoint dp) {
        return schleppDataList.add(dp);
    }

    public boolean addDP(Datapoint dp) {
        return dataList.add(dp);
    }

    public boolean addVel(Double v) {
        return velList.add(v);
    }

    public void addXYValues(Datapoint dp, PreDatapoint pdp) {
        if (Bike.getInstance().isMeasRpm()) {
            seriesPower.add(pdp.getEngRpm(), dp.getPower());
            seriesTorque.add(pdp.getEngRpm(), dp.getTorque());
        } else {
            seriesPower.add(pdp.getWheelRpm(), dp.getPower());
            seriesTorque.add(pdp.getWheelRpm(), dp.getTorque());
        }

    }

    public void rmFirstDP() {
        dataList.remove(0);
    }

    public void clearLists() {
        rawList.removeAll(rawList);
        preList.removeAll(preList);
        schleppPreList.removeAll(schleppPreList);
        schleppDataList.removeAll(schleppDataList);
        dataList.removeAll(dataList);
        velList.removeAll(velList);
    }

}
