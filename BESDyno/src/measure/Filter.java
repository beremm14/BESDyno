package measure;

import data.Bike;
import data.Database;
import data.PreDatapoint;
import data.RawDatapoint;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author emil
 */
public class Filter {

    private final List<RawDatapoint> rawList;

    private final List<RawDatapoint> filteredRawList = new LinkedList<>();
    private final List<PreDatapoint> preList = new ArrayList<>();
    private final List<PreDatapoint> filteredPreList = new LinkedList<>();

    public Filter(List<RawDatapoint> rawList) {
        this.rawList = rawList;
    }

    private void filterValues(double smoothing) {
        filteredRawList.add(rawList.get(0));
        for (int i = 0; i < rawList.size() - 1; i++) {
            double engTime = (1 - smoothing) * filteredRawList.get(i).getEngTime() + smoothing * rawList.get(i + 1).getEngTime();
            double wheelTime = (1 - smoothing) * filteredRawList.get(i).getWheelTime() + smoothing * rawList.get(i + 1).getWheelTime();
            filteredRawList.add(new RawDatapoint((int) Math.round(engTime), (int) Math.round(wheelTime), rawList.get(i).getTime()));
        }
    }
    
    private PreDatapoint calcRpm(RawDatapoint rdp) {
        double engTime = (double) rdp.getEngTime();
        double wheelTime = (double) rdp.getWheelTime();

        double totalImpulse = 26.0;
        double engRpm;
        double wheelRpm;

        wheelRpm = 60000000.0 / (totalImpulse * wheelTime);
        if (Double.isInfinite(wheelRpm)) {
            wheelRpm = 0;
        }

        if (Bike.getInstance().isTwoStroke()) {
            engRpm = 60000000.0 / engTime;
        } else {
            engRpm = 120000000.0 / engTime;
        }
        if (Double.isInfinite(engRpm)) {
            engRpm = 0;
        }

        return new PreDatapoint(engRpm, wheelRpm, (double) rdp.getTime());
    }

    private void filterValuesOrder(double smoothing, int order) {
        for (int i = 0; i < order; i++) {
            filterValues(smoothing);
        }
    }
    
    private void calcPreList() {
        for (RawDatapoint rdp : rawList) {
            filteredPreList.add(calcRpm(rdp));
        }
    }
    
    private void fillDatabase() {
        Database.getInstance().setFilteredRawList(filteredRawList);
        Database.getInstance().setFilteredPreList(filteredPreList);
    }
    
    public void compute(int order, double smoothing) {
        filterValuesOrder(smoothing, order);
        calcPreList();
        fillDatabase();
    }

    public List<RawDatapoint> getRawList() {
        return rawList;
    }

    public List<RawDatapoint> getFilteredRawList() {
        return filteredRawList;
    }

    public List<PreDatapoint> getPreList() {
        return preList;
    }

    public List<PreDatapoint> getFilteredPreList() {
        return filteredPreList;
    }

}
