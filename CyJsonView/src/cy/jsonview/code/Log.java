/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cy.jsonview.code;

import java.io.FileWriter;
import java.io.IOException;
import org.openide.util.Exceptions;

/**
 *
 * @author cangyan
 */
public class Log {
    public static void write(String s){
        try {
            FileWriter fileWriter=new FileWriter("log.txt",true);
            fileWriter.write(s+"\r\n");
            fileWriter.flush();   
            fileWriter.close();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
}
