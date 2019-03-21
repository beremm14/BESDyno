package measure;

import data.Bike;
import data.Database;
import data.Datapoint;
import data.PreDatapoint;
import data.RawDatapoint;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import logging.Logger;

/**
 *
 * @author emil
 */
public class EWMA {

    private static final Logger LOG = Logger.getLogger(EWMA.class.getName());

    private final List<RawDatapoint> rawList;

    private final List<RawDatapoint> filteredRawList = new LinkedList<>();
    private final List<PreDatapoint> preList = new ArrayList<>();
    private final List<PreDatapoint> filteredPreList = new LinkedList<>();

    public EWMA(List<RawDatapoint> rawList) {
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
            PreDatapoint pdp = calcRpm(rdp);
            if (pdp.getTime() == 0 || pdp.getEngRpm() > 30000 || pdp.getWheelRpm() > 3300) {
                LOG.warning("Wrong data..." + pdp.toString());
            } else {
                filteredPreList.add(pdp);
            }
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
    
    public void filterAfterMeasurement(List<Datapoint> unFiltered, double smoothing) {
        Database.getInstance().killChart();
        List<Datapoint> filtered = new LinkedList<>();
        for (int i = 0; i < unFiltered.size() - 2; i++) {
            double power = (1 - smoothing) * unFiltered.get(i).getPower() + smoothing * unFiltered.get(i+1).getPower();
            double torque = (1 - smoothing) * unFiltered.get(i).getTorque()+ smoothing * unFiltered.get(i+1).getTorque();
            Datapoint dp = new Datapoint(power, torque, unFiltered.get(i).getOmega(), true);
            filtered.add(dp);
            Database.getInstance().addXYValuesAgain(dp, i);
            Database.getInstance().setDataList(filtered);
        }
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
