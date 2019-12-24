package cy.jsonview.listener;

import cy.jsonview.code.SelCompUtil;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Segment;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import org.netbeans.swing.tabcontrol.TabbedContainer;

/**
 *
 * @author cangyan
 */
public class FindActionListener implements ActionListener{
    private boolean isTxtFindDlgOpen = false;
    private boolean isTreeFinDlgdOpen = false;
    private final List<TreePath> treePathLst;
    private final JFrame frame;
    private final TabbedContainer  tabbedContainer;
    private final int type;
    private final String dlgTitle;
    private int curPos = 0;
    private JDialog openDlg;
    public FindActionListener(JFrame frame,TabbedContainer  tabbedContainer,int type,String dlgTitle){
        treePathLst = new ArrayList<TreePath>();
        this.frame = frame;
        this.tabbedContainer = tabbedContainer;
        this.type = type;
        this.dlgTitle = dlgTitle;
    }
    

    
    private JDialog createDialog(){
        JDialog dlg = new JDialog(frame);
        dlg.setTitle(dlgTitle);
        dlg.setModal(false);
        dlg.setSize(550,70);
        dlg.setResizable(false);
        dlg.setLocationRelativeTo(frame);
        return dlg;
    } 
    
    @Override
    public void actionPerformed(ActionEvent e) {
        openDlg = createDialog();
        java.awt.Container pane =  openDlg.getContentPane();
        FlowLayout layout = new FlowLayout(FlowLayout.LEFT);
        pane.setLayout(layout);
        JButton btnFind = new JButton("查找");
        JButton btnNext = new JButton("下一个");
        JButton btnPrev = new JButton("上一个");
        final JTextField textFieldFind = new JTextField(50);
        pane.add(textFieldFind);
        pane.add(btnFind);
        pane.add(btnPrev);
        pane.add(btnNext);
        //从头开始查找
        btnFind.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean flag = false;
                modifyDialgTitle(flag,-1);
                if(type==1){
                    flag =  startSegmentFindOrReplaceOperation(textFieldFind.getText(), true, true,true);
                }else{
                    findTreeChildValue(textFieldFind.getText(),treePathLst);
                    if(!treePathLst.isEmpty()){ 
                        flag = true;
                    }
                }
                modifyDialgTitle(flag,1);
            }
        });
        //向下查找
        btnNext.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean flag = false;
                modifyDialgTitle(flag,-1);
                //JTree tree = getTree();
                if(type==1){
                    flag =  startSegmentFindOrReplaceOperation(textFieldFind.getText(), true, true,false);
                }else{
                    curPos++;
                    if(curPos<treePathLst.size()){
                        JTree tree = SelCompUtil.getTree(tabbedContainer);
                        tree.setSelectionPath(treePathLst.get(curPos));
                        tree.scrollPathToVisible(treePathLst.get(curPos));
                        flag = true;
                    }else{
                        curPos = treePathLst.size() - 1;
                    }
                }
                modifyDialgTitle(flag,1);
            }
        });
        //向上查找
        btnPrev.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean flag = false;
                JTree tree = SelCompUtil.getTree(tabbedContainer);
                modifyDialgTitle(flag,-1);
                
                 if(type==1){
                    flag = startSegmentFindOrReplaceOperation(textFieldFind.getText(), true, false,false);
                }else{
                    curPos--;
                    if(curPos>=0){
                        tree.setSelectionPath(treePathLst.get(curPos));
                        tree.scrollPathToVisible(treePathLst.get(curPos));
                        flag = true;
                    }else{
                        curPos = 0;
                    }
                }
                modifyDialgTitle(flag,1);
            }
        });

        openDlg.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {
                treePathLst.clear();
                if(type == 1){
                    isTxtFindDlgOpen = false;
                }else{
                    isTreeFinDlgdOpen = false;
                }
                System.gc();
            }
            @Override
            public void windowClosed(WindowEvent e) { }
            @Override
            public void windowIconified(WindowEvent e) {}
            @Override
            public void windowDeiconified(WindowEvent e) {}
            @Override
            public void windowActivated(WindowEvent e) {}
            @Override
            public void windowDeactivated(WindowEvent e) {}
        });

        openDlg.setVisible(true);

        if(type == 1){
            isTxtFindDlgOpen = true;
        }else{
            isTreeFinDlgdOpen = true;
        }
    }
    
    private void modifyDialgTitle(boolean flag,int n){
        String[] tmp = openDlg.getTitle().split("-");
        if(n==-1){
            openDlg.setTitle(tmp[0] + "-" + "  ==");
            return;
        }
        if (flag) {
            openDlg.setTitle(tmp[0] + "-" + "  找到了^_^");
        } else {
            openDlg.setTitle(tmp[0] + "-" + "  没找到╮(╯_╰)╭");
        }
    }
    public boolean startSegmentFindOrReplaceOperation(String key, boolean ignoreCase, boolean down,boolean isFirst) {
        int length = key.length();
        JTextArea textArea =  SelCompUtil.getTextArea(tabbedContainer);
        Document doc = textArea.getDocument();
        int offset = textArea.getCaretPosition();
        int charsLeft = doc.getLength() - offset;
        if(charsLeft <=0 ){
            offset = 0;
            charsLeft = doc.getLength() - offset;
        }
        if (!down) {
            offset -= length;
            offset--;
            charsLeft = offset;
        }
        if(isFirst){
            offset = 0;
            charsLeft = doc.getLength() - offset;
        }
        Segment text = new Segment();
        text.setPartialReturn(true);
        try {
            while (charsLeft > 0) {
                doc.getText(offset, length, text);
                if ((ignoreCase == true && text.toString().equalsIgnoreCase(key))
                        || (ignoreCase == false && text.toString().equals(key))) {
                    textArea.requestFocus();////焦点,才能能看到效果
                    textArea.setSelectionStart(offset);
                    textArea.setSelectionEnd(offset + length);
                    return true;
                }
                charsLeft--;
                if (down) {
                    offset++;
                } else {
                    offset--;
                }

            }
        } catch (BadLocationException e) {
        }
        return false;
    }
    
    private void findTreeChildValue(String findText,List<TreePath> treePathLst) {
        JTree tree = SelCompUtil.getTree(tabbedContainer);
        DefaultMutableTreeNode root = (DefaultMutableTreeNode)tree.getModel().getRoot();
        Enumeration e = root.depthFirstEnumeration();
        treePathLst.clear();
        curPos = 0;
        while (e.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
            if (node.isLeaf()) {
                String str = node.toString();
                if (str.substring(2).contains(findText)) {
                    tree.expandPath(new TreePath(node.getPath()));
                    TreePath tp = expandTreeNode(tree,node.getPath(), true);
                    treePathLst.add(tp);
                }
            }
        }
        if(!treePathLst.isEmpty()){
            tree.setSelectionPath(treePathLst.get(0));
            tree.scrollPathToVisible(treePathLst.get(0));
        }
//        return treePathLst;
    }
    
    private TreePath expandTreeNode(JTree tree,TreeNode[] arr, Boolean expand) {
        TreePath[] tp = new TreePath[arr.length];
        tp[0] = new TreePath(arr[0]);
        int pos = 0;
        for (int i = 1; i < arr.length; i++) {
            tp[i] = tp[i - 1].pathByAddingChild(arr[i]);
        }
        for (int i = 0; i < arr.length; i++) {
            if (expand) {
                tree.expandPath(tp[i]);
            } else {
                tree.collapsePath(tp[i]);
            }
            pos = i;
        }
        return tp[pos];
    }
}
