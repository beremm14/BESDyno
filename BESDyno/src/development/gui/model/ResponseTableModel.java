package development.gui.model;

import development.LoggedRequest;
import development.LoggedResponse;
import java.util.LinkedList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author emil
 */
public class ResponseTableModel extends AbstractTableModel {
    
    private static final String [] colNames = { "Zeitpunkt", "Response", "Empfangener CRC", "Berechneter CRC"};
    
    private final List<LoggedResponse> resList = new LinkedList<>();
    
    public void add(LoggedResponse lr) {
        resList.add(lr);
        fireTableRowsInserted(resList.size()-1, resList.size()-1);
    }
    
    public void rmAll() {
        resList.removeAll(resList);
    }

    @Override
    public int getRowCount() {
       return resList.size();
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
        final LoggedResponse lr = resList.get(rowIndex);
        
        switch(columnIndex) {
            case 0: return lr.getResTime();
            case 1: return lr.getRes();
            case 2: return lr.getResCRC();
            case 3: return lr.getCalcedCRC();
            default: throw new RuntimeException("Wrong Column Index");
        }
    }
    
}
