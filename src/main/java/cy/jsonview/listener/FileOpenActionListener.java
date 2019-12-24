package cy.jsonview.listener;

import cy.jsonview.code.SelCompUtil;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.swing.JFrame;
import org.apache.commons.lang.StringUtils;
import org.netbeans.swing.tabcontrol.TabbedContainer;

/**
 *
 * @author cangyan
 */
public class FileOpenActionListener implements ActionListener{
    private final JFrame frame;
    private final TabbedContainer  tabbedContainer;
    private final String dlgTitle;
    public FileOpenActionListener(JFrame frame,TabbedContainer  tabbedContainer,String dlgTitle){
        this.frame = frame;
        this.tabbedContainer = tabbedContainer;
        this.dlgTitle = dlgTitle;
    }
    
    
    @Override
    public void actionPerformed(ActionEvent e) {
        java.awt.FileDialog openDlg = new java.awt.FileDialog(frame, dlgTitle, java.awt.FileDialog.LOAD);
        openDlg.setVisible(true);
        if(StringUtils.isBlank(openDlg.getDirectory())||StringUtils.isBlank(openDlg.getFile())){
            return;
        }
        File file = new File(openDlg.getDirectory(),openDlg.getFile()); //fc.getSelectedFile();
        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();
        try {
            InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "GBK");
            reader = new BufferedReader(isr);
            String temp;
            while ((temp = reader.readLine()) != null) {
                sb.append(temp);
            }
            reader.close();
        } catch (IOException ex) {

        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {
                }
            }
        }
        SelCompUtil.getTextArea(tabbedContainer).setText(sb.toString());
    }
    
}
