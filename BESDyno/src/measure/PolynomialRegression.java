package measure;

import data.RawDatapoint;
import java.util.LinkedList;
import java.util.List;
import logging.Logger;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;

/**
 *
 * @author emil
 */
public class PolynomialRegression {

    private static final Logger LOG = Logger.getLogger(PolynomialRegression.class.getName());

    private final List<RawDatapoint> rawList;
    private final List<RawDatapoint> rawFilteredList = new LinkedList<>();

    public PolynomialRegression(List<RawDatapoint> rawList) {
        this.rawList = rawList;
    }

    public List<RawDatapoint> filterRawData() {
        try {
            WeightedObservedPoints engObs = new WeightedObservedPoints();
            WeightedObservedPoints wheelObs = new WeightedObservedPoints();

            for (RawDatapoint rdp : rawList) {
                engObs.add(((double)rdp.getTime())/1000000.0, rdp.getEngTime());
                wheelObs.add(((double)rdp.getTime())/1000000.0, rdp.getWheelTime());
            }

            final PolynomialCurveFitter fitter = PolynomialCurveFitter.create(2);
            final double[] engCoef = fitter.fit(engObs.toList());
            final double[] wheelCoef = fitter.fit(wheelObs.toList());

            LOG.fine(String.format("Regression Engine: a = %.2f, b = %.2f, c = %.2f", engCoef[0], engCoef[1], engCoef[2]));
            LOG.fine(String.format("Regression  Wheel: a = %.2f, b = %.2f, c = %.2f", wheelCoef[0], wheelCoef[1], wheelCoef[2]));

            for (RawDatapoint rdp : rawList) {
                double newEngTime = engCoef[0] * Math.pow(((double)rdp.getTime())/1000000.0, 2) + engCoef[1] * ((double)rdp.getTime())/1000000.0 + engCoef[2];
                double newWheelTime = wheelCoef[0] * Math.pow(((double)rdp.getTime())/1000000.0, 2) + wheelCoef[1] * ((double)rdp.getTime())/1000000.0 + wheelCoef[2];

                rawFilteredList.add(new RawDatapoint((int) Math.round(newEngTime), (int) Math.round(newWheelTime), rdp.getTime()));
            }

            return rawFilteredList;

        } catch (Exception ex) {
            LOG.severe(ex);
            return rawList;
        }
    }

}
