package measure;

import data.RawDatapoint;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 *
 * @author emil
 */
public class FilterRawData {
    
    private final Queue<Double> engTimeQ = new LinkedList<>();
    private final Queue<Double> wheelTimeQ = new LinkedList<>();
    
    private final int period;
    private double engSum;
    private double wheelSum;
    
    private final List<RawDatapoint> rawList;
    private final List<RawDatapoint> filteredList = new LinkedList<>();

    public FilterRawData(int period, List<RawDatapoint> rawList) {
        this.period = period;
        this.rawList = rawList;
    }
    
    private void addData(double eng, double wheel) {
        engSum += eng;
        wheelSum += wheel;
        
        engTimeQ.add(eng);
        wheelTimeQ.add(wheel);
        
        if (engTimeQ.size() > period) {
            engSum -= engTimeQ.remove();
        }
        
        if (wheelTimeQ.size() > period) {
            wheelSum -= wheelTimeQ.remove();
        }
    }
    
    private int getNextEngTime() {
        return (int) Math.round(engSum / period);
    }
    
    private int getNextWheelTime() {
        return (int) Math.round(wheelSum / period);
    }

    public List<RawDatapoint> compute() {
        for (RawDatapoint rdp : rawList) {
            double eng = (double) rdp.getEngTime();
            double wheel = (double) rdp.getWheelTime();
            addData(eng, wheel);
            filteredList.add(new RawDatapoint(getNextEngTime(), getNextWheelTime(), rdp.getTime()));
        }
        
        return filteredList;
    }
}
