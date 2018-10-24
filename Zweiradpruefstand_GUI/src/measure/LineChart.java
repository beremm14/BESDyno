package measure;

import data.Bike;
import data.BikePower;
import data.Config;
import java.awt.Color;
import java.util.List;

/**
 *
 * @author emil
 */
public class LineChart {
    
    private Bike bike = Bike.getInstance();
    private BikePower power = BikePower.getInstance();
    private Config config = Config.getInstance();

    public LineChart() {
        
    }
    
//    private XYDataset createDataset (List<Integer> rpmList, List<Double> powerList) {
//        XYSeries series = new XYSeries("Position");
//        
//        for (int i=0; i<rpmList.size(); i++) {
//            series.add(rpmList.get(i), powerList.get(i));
//        }
//        XYSeriesCollection tmpDataset = new XYSeriesCollection();
//        //tmpDataset.addSeries(series);
//        
//        return tmpDataset;
//    }
    
//    public JFreeChart createEngineChart() {
//        XYDataset engineDataset = createDataset(power.getEngRpm(), power.getEngPower());
//        JFreeChart chart = ChartFactory.createXYAreaChart("Motorleistung " + bike.getVehicleName(),
//                                                          "Drehzahl (U/min)",
//                                                          "Motorleistung (" + config.getPowerUnit() + ")",
//                                                          engineDataset);
//        chart.setBackgroundPaint(Color.white);
//        
//        XYPlot plot = chart.getXYPlot();
//        plot.setBackgroundPaint(Color.darkGray);
//        plot.setDomainGridlinePaint(Color.white);
//        plot.setRangeGridlinePaint(Color.white);
//        
//        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
//        renderer.setSeriesLinesVisible(0, true);
//        renderer.setSeriesLinesVisible(1, true);
//        
//        return chart;
//    }
//    
//    public JFreeChart createWheelChart() {
//        XYDataset engineDataset = createDataset(power.getWheelRpm(), power.getWheelPower());
//        JFreeChart chart = ChartFactory.createXYAreaChart("Hinterradleistung " + bike.getVehicleName(),
//                                                          "Drehzahl (U/min)",
//                                                          "Hinterradleistung (" + config.getPowerUnit() + ")",
//                                                          engineDataset);
//        chart.setBackgroundPaint(Color.white);
//        
//        XYPlot plot = chart.getXYPlot();
//        plot.setBackgroundPaint(Color.darkGray);
//        plot.setDomainGridlinePaint(Color.white);
//        plot.setRangeGridlinePaint(Color.white);
//        
//        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
//        renderer.setSeriesLinesVisible(0, true);
//        renderer.setSeriesLinesVisible(1, true);
//        
//        return chart;
//    }
    
    
    
}
