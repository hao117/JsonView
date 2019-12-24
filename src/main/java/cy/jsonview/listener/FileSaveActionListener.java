package cy.jsonview.listener;

import cy.jsonview.code.SelCompUtil;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import org.apache.commons.lang.StringUtils;
import org.netbeans.swing.tabcontrol.TabbedContainer;

/**
 *
 * @author cangyan
 */
public class FileSaveActionListener implements ActionListener{
    private final JFrame frame;
    private final TabbedContainer  tabbedContainer;
    private final String dlgTitle;
    public FileSaveActionListener(JFrame frame,TabbedContainer tabbedContainer,String dlgTitle){
        this.frame = frame;
        this.tabbedContainer = tabbedContainer;
        this.dlgTitle = dlgTitle;
    }
    
    
    @Override
    public void actionPerformed(ActionEvent e) {
        java.awt.FileDialog closeDlg = new java.awt.FileDialog(frame, dlgTitle, java.awt.FileDialog.SAVE);
        closeDlg.setVisible(true);
        File file = new File(closeDlg.getDirectory(),closeDlg.getFile());
        if(StringUtils.isBlank(closeDlg.getDirectory())||StringUtils.isBlank(closeDlg.getFile())){
            return;
        }
        BufferedWriter write= null;
        try {
            OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(file),"GBK");
            write = new BufferedWriter(osw);
            JTextArea textArea = SelCompUtil.getTextArea(tabbedContainer);
            String text = StringUtils.replace(textArea.getText(), "\n", "\r\n");
            write.write(text, 0, text.length());
            write.close();
        } catch (IOException ex) {

        } finally {
            if (write != null) {
                try {
                    write.close();
                } catch (IOException e1) {
                }
            }
        }
    }
    
}
