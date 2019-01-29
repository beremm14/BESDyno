package data;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
    

    //Power (ps or kw)
    private final List<Double> engPowerList = new LinkedList<>();
    private final List<Double> wheelPowerList = new LinkedList<>();
    
    //RPM (U/min)
    private final List<Double> engRpmList = new ArrayList<>();
    private final List<Double> wheelRpmList = new ArrayList<>();
    
    //Torque (Nm)
    private final List<Double> engTorList = new LinkedList<>();
    private final List<Double> wheelTorList = new LinkedList<>();
    
    //Velocity (m/s or km/h or mi/h)
    private final List<Double> velList = new ArrayList<>();
    
    //Time (µs)
    private final List<Integer> timeList = new LinkedList<>();
    
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
    
    //Sync-Object
    public final Object syncObj = new Object();

    private Database() {
    }
    
    //Getter
    public List<Double> getEngPowerList() {
        return engPowerList;
    }

    public List<Double> getWheelPowerList() {
        return wheelPowerList;
    }

    public List<Double> getEngRpmList() {
        return engRpmList;
    }

    public List<Double> getWheelRpmList() {
        return wheelRpmList;
    }

    public List<Double> getEngTorList() {
        return engTorList;
    }

    public List<Double> getWheelTorList() {
        return wheelTorList;
    }
    
    public List<Double> getVelList() {
        return velList;
    }

    public List<Integer> getTimeList() {
        return timeList;
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

    //Getter
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

    //LinkedList-Methods
    public boolean addEP(Double p) {
        return engPowerList.add(p);
    }

    public boolean addWP(Double p) {
        return wheelPowerList.add(p);
    }
    
    public boolean addER(Double n) {
        return engRpmList.add(n);
    }
    
    public boolean addWR(Double n) {
        return wheelRpmList.add(n);
    }
    
    public boolean addET(Double m) {
        return engTorList.add(m);
    }
    
    public boolean addWT(Double m) {
        return wheelTorList.add(m);
    }
    
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
    
    public boolean addTime(Integer t) {
        return timeList.add(t);
    }
    
    public void rmFirstDP() {
        dataList.remove(0);
    }
    
    public void clearLists() {
        engPowerList.removeAll(engPowerList);
        engRpmList.removeAll(engRpmList);
        engTorList.removeAll(engTorList);
        wheelPowerList.removeAll(wheelPowerList);
        wheelRpmList.removeAll(wheelRpmList);
        wheelTorList.removeAll(wheelTorList);
        rawList.removeAll(rawList);
        velList.removeAll(velList);
    }

}
