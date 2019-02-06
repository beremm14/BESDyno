package development.gui.model;

import java.util.LinkedList;
import java.util.List;
import javax.swing.AbstractListModel;

/**
 *
 * @author emil
 */
public class InfoPaneModel extends AbstractListModel {
    
    private final List<String> list = new LinkedList<>();
    
    public boolean add(String s) {
        boolean rv = list.add(s);
        fireContentsChanged(s, 0, list.size()-1);
        return rv;
    }
    
    public void rmAll() {
        list.removeAll(list);
    }

    @Override
    public int getSize() {
        return list.size();
    }

    @Override
    public Object getElementAt(int index) {
        return list.get(index);
    }

}
