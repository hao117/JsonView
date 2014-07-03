/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cy.jsonview.listener;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Map;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 *
 * @author cangyan
 */
public class TreeMouseListener implements MouseListener {
        private final JTree tree;
         private final Map jsonEleTreeMap;
        public TreeMouseListener(JTree tree,Map jsonEleTreeMap){
            this.tree = tree;
            this.jsonEleTreeMap = jsonEleTreeMap;
        }

        @Override
        public void mouseClicked(MouseEvent e) {}

        @Override
        public void mousePressed(MouseEvent e) {}

        @Override
        public void mouseReleased(MouseEvent e) {
            TreePath path = tree.getPathForLocation(e.getX(), e.getY());
            if (path == null)  return;
            tree.setSelectionPath(path);
            DefaultMutableTreeNode selNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (e.isPopupTrigger()) {
                JPopupMenu popMenu = new JPopupMenu();
                JMenuItem copyValue = new JMenuItem("复制 键值");
                JMenuItem copyKey = new JMenuItem("复制 键名");
                JMenuItem copyPath = new JMenuItem("复制 路径");
                JMenuItem copyKeyValue = new JMenuItem("复制 键名键值");
                JMenuItem copyNode = new JMenuItem("复制 节点内容");
                JMenuItem copyPathAllVal = new JMenuItem("复制 同路径键值");
                JMenuItem copySingleNodeString = new JMenuItem("复制 MAP式内容");
                JMenuItem copyNodeFormat = new JMenuItem("复制 节点内容带格式");
                
                popMenu.add(copyKey);
                popMenu.add(copyValue);
                popMenu.add(copyPath);
                popMenu.add(copyNode);
                popMenu.add(copyKeyValue);
                popMenu.add(copySingleNodeString);
                popMenu.add(copyPathAllVal);
                popMenu.add(copyNodeFormat);
                copyKey.addActionListener(new TreeNodeMenuItemActionListener(tree,1, selNode,jsonEleTreeMap));
                copyValue.addActionListener(new TreeNodeMenuItemActionListener(tree,2, selNode,jsonEleTreeMap));
                copyKeyValue.addActionListener(new TreeNodeMenuItemActionListener(tree,3, selNode,jsonEleTreeMap));
                copyPath.addActionListener(new TreeNodeMenuItemActionListener(tree,4, path,jsonEleTreeMap));
                copyPathAllVal.addActionListener(new TreeNodeMenuItemActionListener(tree,5,selNode,jsonEleTreeMap));
                copyNode.addActionListener(new TreeNodeMenuItemActionListener(tree,6,path,jsonEleTreeMap));
                copyNodeFormat.addActionListener(new TreeNodeMenuItemActionListener(tree,7,path,jsonEleTreeMap));
                copySingleNodeString.addActionListener(new TreeNodeMenuItemActionListener(tree,8,selNode,jsonEleTreeMap));
                popMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {}

        @Override
        public void mouseExited(MouseEvent e) {}
    
}
