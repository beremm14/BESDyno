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
    private final List<Integer> engRpmList = new ArrayList<>();
    private final List<Integer> wheelRpmList = new ArrayList<>();
    
    //Torque (Nm)
    private final List<Double> engTorList = new LinkedList<>();
    private final List<Double> wheelTorList = new LinkedList<>();
    
    //Velocity (m/s or km/h or mi/h)
    private final List<Double> velList = new ArrayList<>();
    
    //Time (ms)
    private final List<Integer> timeList = new LinkedList<>();
    
    //Uncalculated List
    private final List<RawDatapoint> rawList = new ArrayList<>();

    //"Absolute" Power
    private double bikePower;
    
    //Sync-Object
    public final Object syncObj;

    private Database() {
        syncObj = new Object();
    }
    
    //Getter
    public List<Double> getEngPowerList() {
        return engPowerList;
    }

    public List<Double> getWheelPowerList() {
        return wheelPowerList;
    }

    public List<Integer> getEngRpmList() {
        return engRpmList;
    }

    public List<Integer> getWheelRpmList() {
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

    public double getBikePower() {
        return bikePower;
    }

    //Getter
    public Object getSyncObj() {    
        return syncObj;
    }

    //Setter
    public void setBikePower(double bikePower) {
        this.bikePower = bikePower;
    }

    public void setBikePower() {
        double power = engPowerList.get(0);
        for (int i = 0; i < engPowerList.size(); i++) {
            if (engPowerList.get(i) > power) {
                power = engPowerList.get(i);
            }
        }
        this.bikePower = power;
    }

    //LinkedList-Methods
    public boolean addEP(Double p) {
        return engPowerList.add(p);
    }

    public boolean addWP(Double p) {
        return wheelPowerList.add(p);
    }
    
    public boolean addER(Integer n) {
        return engRpmList.add(n);
    }
    
    public boolean addWR(Integer n) {
        return wheelRpmList.add(n);
    }
    
    public boolean addET(Double m) {
        return engTorList.add(m);
    }
    
    public boolean addWT(Double m) {
        return wheelTorList.add(m);
    }
    
    public boolean addRawDP(RawDatapoint dp) {
        return rawList.add(dp);
    }
    
    public boolean addVel(Double v) {
        return velList.add(v);
    }
    
    public boolean addTime(Integer t) {
        return timeList.add(t);
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
