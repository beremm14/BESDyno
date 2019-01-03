package development.gui.model;

import development.LoggedRequest;
import java.util.LinkedList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author emil
 */
public class RequestTableModel extends AbstractTableModel {
    
    private static final String [] colNames = { "Zeitpunkt", "Request" };
    
    private final List<LoggedRequest> reqList = new LinkedList<>();
    
    public void add(LoggedRequest lr) {
        reqList.add(lr);
        fireTableRowsInserted(reqList.size()-1, reqList.size()-1);
    }
    
    public void rmAll() {
        reqList.removeAll(reqList);
    }

    @Override
    public int getRowCount() {
       return reqList.size();
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
        final LoggedRequest lr = reqList.get(rowIndex);
        
        switch(columnIndex) {
            case 0: return lr.getReqTime();
            case 1: return lr.getReq();
            default: throw new RuntimeException("Wrong Column Index");
        }
    }
    
}
