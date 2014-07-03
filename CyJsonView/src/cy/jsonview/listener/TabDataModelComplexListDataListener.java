package cy.jsonview.listener;

import cy.jsonview.code.SelCompUtil;
import java.util.Map;
import javax.swing.JTree;
import javax.swing.event.ListDataEvent;
import org.netbeans.swing.tabcontrol.TabData;
import org.netbeans.swing.tabcontrol.event.ComplexListDataEvent;
import org.netbeans.swing.tabcontrol.event.ComplexListDataListener;

/**
 * @author cangyan
 */
public class TabDataModelComplexListDataListener implements ComplexListDataListener {
    private final Map jsonEleTreeMap;
    public TabDataModelComplexListDataListener(Map jsonEleTreeMap){
        this.jsonEleTreeMap = jsonEleTreeMap;
    }
    @Override
    public void indicesAdded(ComplexListDataEvent clde) {}
    @Override
    public void indicesRemoved(ComplexListDataEvent clde) {}
    @Override
    public void indicesChanged(ComplexListDataEvent clde) {}
    @Override
    public void intervalAdded(ListDataEvent e) {}
    @Override
    public void intervalRemoved(ListDataEvent e) {
        ComplexListDataEvent ce = (ComplexListDataEvent)e;
        TabData[] tbArr = ce.getAffectedItems();
        if(tbArr!=null && tbArr.length>0){
             tbArr[0].getText();
             JTree tree = getTree(tbArr[0]);
             if(tree!=null){
                 jsonEleTreeMap.remove(tree.hashCode());
                 System.out.println("Remove HashCode: "+ tree.hashCode() + ". Close Tab: " + tbArr[0].getText() + " !");
             }
        }
    }
    @Override
    public void contentsChanged(ListDataEvent e) {}
    
    private JTree getTree(TabData tabData){
         return SelCompUtil.getTree(tabData);
     }
}
