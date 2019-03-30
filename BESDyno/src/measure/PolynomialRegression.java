package measure;

import data.Database;
import data.Datapoint;
import data.PreDatapoint;
import data.RawDatapoint;
import java.util.LinkedList;
import java.util.List;
import logging.Logger;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;

/**
 *
 * @author emil
 */
public class PolynomialRegression {

    private static final Logger LOG = Logger.getLogger(PolynomialRegression.class.getName());

    private List<RawDatapoint> rawList;
    private List<PreDatapoint> preList;
    private List<Datapoint> dataList;
    private final List<RawDatapoint> rawFilteredList = new LinkedList<>();
    private final int order;

    public PolynomialRegression(List<RawDatapoint> rawList, int order) {
        this.rawList = rawList;
        this.order = order;
    }

    public PolynomialRegression(List<RawDatapoint> rawList, List<PreDatapoint> preList, List<Datapoint> dataList, int order) {
        this.rawList = rawList;
        this.preList = preList;
        this.dataList = dataList;
        this.order = order;
    }

    public List<RawDatapoint> filterRawData() {
        try {
            Database.getInstance().killChart();
            WeightedObservedPoints engObs = new WeightedObservedPoints();
            WeightedObservedPoints wheelObs = new WeightedObservedPoints();

            for (RawDatapoint rdp : rawList) {
                engObs.add(((double) rdp.getTime()), rdp.getEngTime());
                wheelObs.add(((double) rdp.getTime()), rdp.getWheelTime());
            }

            final PolynomialCurveFitter fitter = PolynomialCurveFitter.create(order);
            final double[] engCoef = fitter.fit(engObs.toList());
            final double[] wheelCoef = fitter.fit(wheelObs.toList());

            LOG.info("Koeffizienten Engine:");
            for (Double c : engCoef) {
                LOG.info(c + "");
            }
            LOG.info("Koeffizienten Wheel:");
            for (Double c : wheelCoef) {
                LOG.info(c + "");
            }

            PolynomialFunction engFunction = new PolynomialFunction(engCoef);
            PolynomialFunction wheelFunction = new PolynomialFunction(wheelCoef);

            for (RawDatapoint rdp : rawList) {
                double newEngTime = engFunction.value(rdp.getTime());
                double newWheelTime = wheelFunction.value(rdp.getTime());
                LOG.info("Engine: " + newEngTime + " Wheel: " + newWheelTime + " Time: " + rdp.getTime());
                rawFilteredList.add(new RawDatapoint((int) Math.round(newEngTime), (int) Math.round(newWheelTime), rdp.getTime()));
            }
            for (RawDatapoint rdp : rawFilteredList) {
                LOG.info("Engine: " + rdp.getEngTime() + " Wheel: " + rdp.getWheelTime() + " Time: " + rdp.getTime());
            }
            return rawFilteredList;

        } catch (Exception ex) {
            LOG.severe(ex);
            return rawList;
        }
    }

    public void filterPowerData() {
        try {
            Database.getInstance().clearLists();
            Database.getInstance().setRawList(rawList);
            Database.getInstance().setPreList(preList);

            WeightedObservedPoints powerObs = new WeightedObservedPoints();

            for (int i = 0; i < dataList.size() - 1; i++) {
                powerObs.add(i, dataList.get(i).getPower());
            }

            final PolynomialCurveFitter fitter = PolynomialCurveFitter.create(order);
            final double[] powerCoef = fitter.fit(powerObs.toList());

            PolynomialFunction powerFunction = new PolynomialFunction(powerCoef);

            for (int i = 0; i < dataList.size() - 1; i++) {
                double newPower = powerFunction.value(i);
                double omega = (2 * Math.PI / 60) * preList.get(i).getWheelRpm();
                Database.getInstance().addDP(new Datapoint(newPower, omega));
                Database.getInstance().addXYValues(new Datapoint(newPower, omega), preList.get(i));
            }
        } catch (Exception ex) {
            LOG.severe(ex);
        }
    }

    public void filterAfterMeasurement() {
        Calculate calc = new Calculate();
        List<RawDatapoint> filtered = filterRawData();
        List<PreDatapoint> pdpList = calc.calcPreList(filtered);
        Database.getInstance().clearLists();
        Database.getInstance().setRawList(filtered);
        Database.getInstance().setFilteredRawList(filtered);
        Database.getInstance().setFilteredPreList(pdpList);
        Database.getInstance().setPreList(pdpList);
        calc.calcPower(filtered, pdpList, false, true);
    }

}
