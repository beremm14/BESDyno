package measure;

import data.RawDatapoint;
import java.util.List;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;

/**
 *
 * @author emil
 */
public class PolynomialRegression {
    
    private final List<RawDatapoint> rawList;
    private List<RawDatapoint> rawFilteredList;

    public PolynomialRegression(List<RawDatapoint> rawList) {
        this.rawList = rawList;
    }

    public List<RawDatapoint> filterRawData() {
        WeightedObservedPoints engObs = new WeightedObservedPoints();
        WeightedObservedPoints wheelObs = new WeightedObservedPoints();
        
        for (RawDatapoint rdp : rawList) {
            engObs.add(rdp.getTime(), rdp.getEngTime());
            wheelObs.add(rdp.getTime(), rdp.getWheelTime());
        }
        
        final PolynomialCurveFitter fitter = PolynomialCurveFitter.create(2);
        final double[] engCoef = fitter.fit(engObs.toList());
        final double[] wheelCoef = fitter.fit(wheelObs.toList());
        
        for (RawDatapoint rdp : rawList) {
            double newEngTime = engCoef[0] * Math.pow(rdp.getTime(), 2) + engCoef[1] * rdp.getTime() + engCoef[2];
            double newWheelTime = wheelCoef[0] * Math.pow(rdp.getTime(), 2) + wheelCoef[1] * rdp.getTime() + wheelCoef[2];
            
            rawFilteredList.add(new RawDatapoint((int)Math.round(newEngTime), (int)Math.round(newWheelTime), rdp.getTime()));
        }
        
        return rawFilteredList;
    }
    
}
