package development.gui.model;

import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author emil
 */
public class MeasurementValuesModel extends AbstractTableModel {
    
    private static final String [] colNames = {"Geschwindigkeit", "Motorleistung",
                                               "Motordrehzahl", "Motormoment",
                                               "Hinterradleistung", "Walzendrehzahl",
                                               "Walzenmoment"};
    
    private final List<Double> engPowerList;
    private final List<Double> wheelPowerList;
    
    private final List<Integer> engRpmList;
    private final List<Integer> wheelRpmList;
    
    private final List<Double> engTorList;
    private final List<Double> wheelTorList;
    
    private final List<Double> velList;

    
    public MeasurementValuesModel(List<Double> engPowerList, List<Double> wheelPowerList,
                                  List<Integer> engRpmList, List<Integer> wheelRpmList,
                                  List<Double> engTorList, List<Double> wheelTorList,
                                  List<Double> velList) {
        this.engPowerList = engPowerList;
        this.wheelPowerList = wheelPowerList;
        this.engRpmList = engRpmList;
        this.wheelRpmList = wheelRpmList;
        this.engTorList = engTorList;
        this.wheelTorList = wheelTorList;
        this.velList = velList;
    }
    
    
    @Override
    public int getRowCount() {
        return engPowerList.size();
    }

    @Override
    public int getColumnCount() {
        return colNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return colNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        final double engPower = engPowerList.get(rowIndex);
        final double wheelPower = wheelPowerList.get(rowIndex);
        final double engRpm = engRpmList.get(rowIndex);
        final double wheelRpm = wheelRpmList.get(rowIndex);
        final double engTor = engTorList.get(rowIndex);
        final double wheelTor = wheelTorList.get(rowIndex);
        final double velocity = velList.get(rowIndex);
        
        switch (columnIndex) {
            case 0: return velocity;
            case 1: return engPower;
            case 2: return engRpm;
            case 3: return engTor;
            case 4: return wheelPower;
            case 5: return wheelRpm;
            case 6: return wheelTor;
            default: throw new RuntimeException("Wrong column index...");
        }
    }
    
}
