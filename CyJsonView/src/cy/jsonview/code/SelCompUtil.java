/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cy.jsonview.code;

import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.JViewport;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.netbeans.swing.tabcontrol.TabData;
import org.netbeans.swing.tabcontrol.TabDataModel;
import org.netbeans.swing.tabcontrol.TabbedContainer;

/**
 *
 * @author cangyan
 */
public class SelCompUtil {
    public static int getTabIndex(TabbedContainer  tabbedContainer){
        return tabbedContainer.getSelectionModel().getSelectedIndex();
    }
    
    public TabDataModel getTabDataModel(TabbedContainer  tabbedContainer){
        return tabbedContainer.getModel();
    }
    
    public static RSyntaxTextArea getTextArea(TabbedContainer  tabbedContainer){
        int selIndex = getTabIndex(tabbedContainer);
        TabDataModel tabDataModel = tabbedContainer.getModel();
        if(selIndex >= 0){
            TabData selTabData = tabDataModel.getTab(selIndex);
            JSplitPane selSplitPane = (JSplitPane)selTabData.getComponent();
            JScrollPane sp = (JScrollPane)selSplitPane.getLeftComponent();
            JViewport vp = (JViewport)sp.getComponent(0);
            RSyntaxTextArea ta = (RSyntaxTextArea)vp.getComponent(0);
            return ta;
        }
        return null;
    }
    
    
     public static JTree getTree(TabData tabData){
         if(tabData==null){
             return null;
         }
         JSplitPane selSplitPane = (JSplitPane)tabData.getComponent();
         JScrollPane sp = (JScrollPane)selSplitPane.getRightComponent();
         JViewport vp = (JViewport)sp.getComponent(0);
         JTree t = (JTree)vp.getComponent(0);
         return t;
     }
    public static JTree getTree(TabDataModel tabDataModel,int tabIndex){
        if(tabIndex >= 0){
            TabData selTabData = tabDataModel.getTab(tabIndex);
            return getTree(selTabData);
        }
        return null;
    }

    public static JTree getTree(TabbedContainer  tabbedContainer){
        return getTree(tabbedContainer.getModel(),getTabIndex(tabbedContainer));
    }
    
    public static JTable getTable(int tabIndex,TabbedContainer  tabbedContainer){
        if(tabIndex >= 0){
            TabData selTabData = tabbedContainer.getModel().getTab(tabIndex);
            JSplitPane selSplitPane = (JSplitPane)selTabData.getComponent();
            JSplitPane rightSplitPane = (JSplitPane)selSplitPane.getRightComponent();
            JScrollPane sp = (JScrollPane)rightSplitPane.getRightComponent();
            JViewport vp = (JViewport)sp.getComponent(0);
            JTable t = (JTable)vp.getComponent(0);
            return t;
        }
        return null;
    }
    
    public static JTable getTable(TabbedContainer  tabbedContainer){
        return getTable(getTabIndex(tabbedContainer),tabbedContainer);
    }
}
