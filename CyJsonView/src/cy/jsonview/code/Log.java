/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cy.jsonview.code;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
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
    
    public static void write(Exception e){
         try {
            StringWriter sw = new StringWriter();  
            PrintWriter pw = new PrintWriter(sw);  
            e.printStackTrace(pw);  
            write("异常:"+sw.toString());
        }catch (Exception ex) {
            write("write Exception异常");
            Exceptions.printStackTrace(ex);
        }
    }
}
