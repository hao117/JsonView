package cy.jsonview.code;

import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * 关于.
 * @author cangyan
 */
public class AboutDialog {
    
    public static void show(JFrame frame){
        JDialog dlg = new JDialog(frame);
        dlg.setTitle("关于");
        dlg.setModal(false);
        dlg.setSize(260,160);
        dlg.setResizable(false);
        dlg.setLocationRelativeTo(frame);
        
        java.awt.Container pane =  dlg.getContentPane();
        BoxLayout layout = new BoxLayout(pane, BoxLayout.Y_AXIS);
        pane.setLayout(layout);
        pane.add(new JLabel(" "));
        pane.add(new JLabel("        CyJsonView 2.3.3"));
        pane.add(new JLabel(" "));
        pane.add(new JLabel("        作者：藏言"));
        pane.add(new JLabel(" "));
        pane.add(new JLabel("        邮箱：beetle082@163.com"));
        dlg.setVisible(true);
    }
}
