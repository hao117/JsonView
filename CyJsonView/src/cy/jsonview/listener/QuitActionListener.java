package cy.jsonview.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 * @author cangyan
 */
public class QuitActionListener implements ActionListener{

    @Override
    public void actionPerformed(ActionEvent e) {
        System.exit(0);
    }
    
}
