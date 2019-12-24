/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cy.jsonview.code;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.XMLWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/**
 *
 * @author CangYan
 */
public class Kit {
    public final static String split = " : ";
    
    public final static String sign = "-" ;
    
    public final static String cNull = "k";
    public final static String cNum  = "n";
    public final static String cObj  = "o";
    public final static String cArr  = "a";
    public final static String cStr  = "v";
    public final static String cBool = "b";

    public final static String sNull = cNull + sign;
    public final static String sNum  = cNum + sign;
    public final static String sObj  = cObj + sign;
    public final static String sArr  = cArr + sign;
    public final static String sStr  = cStr + sign;
    public final static String sBool = cBool + sign;
   
    public final static String array   = "Array";
    public final static String object  = "Object";
    public static String baseResPath = "cy/jsonview/resources/";
    public static String  QUOT = "\"";

    public static DefaultMutableTreeNode nullNode(String key){
        return treeNode(sNull + key + split +  "<null>");
    }
    public static DefaultMutableTreeNode nullNode(int index){
        return nullNode(fkey(index));
    }

    public static DefaultMutableTreeNode numNode(String key,String val){
        return treeNode(sNum + key + split + val);
    }
    public static DefaultMutableTreeNode numNode(int index,String val){
        return numNode(fkey(index),val);
    }

    public static DefaultMutableTreeNode boolNode(String key,Boolean val){
        String sVal = "false";
         if (val){
             sVal = "true";
         }
        return  treeNode(sBool + key + split + sVal);
    }

    public static DefaultMutableTreeNode boolNode(int index,Boolean val){
       return  boolNode(fkey(index),val);
    }

    public static DefaultMutableTreeNode strNode(String key,String val){
        return  treeNode(sStr + key + split +"\"" + val + "\"");
    }
    public static DefaultMutableTreeNode strNode(int index,String val){
        return  strNode(fkey(index),val);
    }

    public static DefaultMutableTreeNode objNode(String key){
        return treeNode(sObj+key);
    }
    
    public static DefaultMutableTreeNode objNode(int index){
        return objNode(fkey(index));
    }

     public static DefaultMutableTreeNode arrNode(String key){
        return treeNode(sArr+key);
    }

    public static DefaultMutableTreeNode arrNode(int index){
        return arrNode(fkey(index));
    }


    public static DefaultMutableTreeNode treeNode(String str){
        return new DefaultMutableTreeNode(str);
    }

    public static DefaultMutableTreeNode treeNode(String type,int index,String val){
        return treeNode(type +"[" + index + "]");
    }

    public static String fkey(int index){
        return "[" + index + "]";
    }
    
    //"a-[" + i + "]"
    public static String fArrKey(int index){
        return sArr + fkey(index);
    }


    public static int getIndex(String str){
        int index = -1;
        if(str==null||str.length()==0) return index;
        index = str.lastIndexOf("[");
        if(index>=0){
            try{
                index = Integer.parseInt(str.substring(index+1,str.length()-1));
            }catch(Exception ex){
                index = -1;
            }
        }
        return index;
    }
    public static String getKey(String str){
        int index = -1;
        if(str==null||str.length()==0) return str;
        index = str.lastIndexOf("[");
        if(index>=0){
            return str.substring(0,index);
        }
        StringBuffer sb = null;
        return str;

    }

    public static String[] pstr(String str){
        String arr[] = new String[3];//类型,key,value
        arr[0] = str.substring(0,1);
        int i = str.indexOf(Kit.split);
//        if(i<0) return arr;
        if(Kit.cArr.equals(arr[0])){
            arr[1] = str.substring(2);
            arr[2] = Kit.array;
        }else if(Kit.cObj.equals(arr[0])){
            arr[1] = str.substring(2);
            arr[2] = Kit.object;
        }else if(Kit.cStr.equals(arr[0])){
            arr[1] = str.substring(2,i);
            arr[2] = str.substring(i+4,str.length()-1);
        }else{
            arr[1] = str.substring(2,i);
            arr[2] = str.substring(i+3,str.length());
        }
        return arr;
    }
    
    public static Map xmlToMap(String xml) throws SAXException, IOException, ParserConfigurationException {
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        docBuilderFactory.setNamespaceAware(true);
        Document doc = docBuilderFactory.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
        Map rootMap = new HashMap();
        Map nodeMap = new HashMap();
        Node node = doc.getDocumentElement();
        xmlConversion(node, nodeMap);
        rootMap.put(node.getLocalName(), nodeMap);
        return rootMap;
    }
    private static void xmlConversion(Node node, Map nodeMap) {
        if (node.hasChildNodes()) {
            //Map nodeMap = new HashMap();
            NodeList childList = node.getChildNodes();
            for (int i = 0; i < childList.getLength(); i++) {
                Node childNode = childList.item(i);
                if (childNode.hasChildNodes()) {
                    int len = childNode.getChildNodes().getLength();
                    short nodeType = childNode.getFirstChild().getNodeType();
                    if (len == 1 && nodeType == Node.TEXT_NODE) {
                        String keyName = childNode.getLocalName();
                        String value = childNode.getFirstChild().getNodeValue();
                        if (null == nodeMap) {
                            nodeMap = new HashMap();
                        }
                        if (nodeMap.get(keyName) == null) {
                            nodeMap.put(keyName, value);
                        } else {
                            //if (null != value) {
                                List list = new ArrayList();
                                if (nodeMap.get(keyName) instanceof List) {
                                    list = (List) nodeMap.get(keyName);
                                } else {
                                    list.add(nodeMap.get(keyName));
                                }
                                list.add(value);
                                nodeMap.put(keyName, list);
                            //}
                        }
                    } else {
                        HashMap map = new HashMap();
                        xmlConversion(childNode, map);
                        // String keyName = childNode.getNodeName();
                        String keyName = childNode.getLocalName();
                        
                        if (null == nodeMap) {
                            nodeMap = new HashMap();
                        }
                        if (null == nodeMap.get(keyName)) {
                            nodeMap.put(keyName, map);
                        } else {
                            List list = new ArrayList();
                            if (nodeMap.get(keyName) instanceof List) {
                                list = (List) nodeMap.get(keyName);
                            } else {
                                list.add(nodeMap.get(keyName));
                            }
                            list.add(map);
                            nodeMap.put(keyName, list);
                        }
                        if (childNode.getAttributes().getLength() > 0) {
                            String tmpName = childNode.getAttributes().item(0).getNodeName();
                            String tmpValue = childNode.getAttributes().item(0).getNodeValue();
                            nodeMap.put(tmpName, tmpValue);
                        }
                    }
                }else if(childNode.getNodeType() == Node.ELEMENT_NODE){
                    if(StringUtils.isNotBlank(childNode.getLocalName())){
                        nodeMap.put(childNode.getLocalName(), null);
                    }
                }
            }
            //rootMap.put(node.getLocalName(), nodeMap);
        }
        
    }
    
    public static String mapStringToJsonString(String mapstr){
        mapstr = mapstr.replace(" ", "");
        mapstr = mapstr.replace("\"", "");
        mapstr = mapstr.replace("\r", "");
        mapstr = mapstr.replace("\n", "");
        mapstr = mapstr.replace("\t", "");
        int len = mapstr.length() - 1;
        String json = "";
        for (int i = 0; i <= len; i++) {
            char c = mapstr.charAt(i);
            char cp = 0;
            if (i < len) {
                cp = mapstr.charAt(i + 1);
            }
            char cr = 0;
            if (i > 0) {
                cr = mapstr.charAt(i - 1);
            }

            //{[{aaa=
            if (c == '{' || c == '[') {
                if (cp == '}' || cp == ']') {
                    json = json + c + cp;
                    i++;
                    continue;
                }
                if (cp == '{' || cp == '[') {
                    json += c;
                    continue;
                }
                json = json + c + QUOT;
                continue;
            }
            //aa=aa ---> "aa":"aa"
            //aa={  ---> "aa":{
            //aa=[  ---> "aa":[
            //aa=,bb=bb ---> "aa":"","bb":"bb"
            if (c == '=') {
                if (cp == '{' || cp == '[') {
                    json = json + QUOT + ":";
                    continue;
                }
                if (cp == ',') {
                    json = json + QUOT + ":" + QUOT + QUOT + cp;
                    i++;
                    continue;
                }
                json = json + QUOT + ":" + QUOT;
                continue;
            }

            // ],[     ],{   ],b 
            // },{     },[   },b
            // a,{     a,[   a,b     
            // aaa=,bb=ccc
            if (c == ',') {
                if (cr == '}' || cr == ']') {
                    if (cp == '{' || cp == '[') {
                        json += c;
                        continue;
                    }
                    json += c + QUOT;
                    continue;
                }
                if (cp == '{' || cp == '[') {
                    json = json + QUOT + c;
                    continue;
                }
                json = json + QUOT + c + QUOT;
                continue;
            }

            // a]}]},ad=sd
            if (c == '}' || c == ']') {
                if (cr == '}' || cr == ']') {
                    json += c;
                    continue;
                }
                json = json + QUOT + c;
                continue;
            }
            json += c;
        }
        return json;
    }
   /* 
    public static String formatXML(String xml,boolean isIndenting) throws ParserConfigurationException, SAXException, IOException{
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        docBuilderFactory.setNamespaceAware(true);
        Document doc = docBuilderFactory.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
        OutputFormat format = new OutputFormat(doc);   
        if(isIndenting){
            format.setIndenting(true); 
        }else{
            System.out.println("##########################");
           // format.setIndenting(false);//设置是否缩进，默认为true  
            //format.setIndent(0);//设置缩进字符数  
            format.setPreserveSpace(true);
            format.setOmitXMLDeclaration(true);
            format.setLineSeparator( "" );

            //设置是否保持原来的格式,默认为 false
        }
        Writer out = new StringWriter();   
        XMLSerializer serializer = new XMLSerializer(out, format);   
        serializer.serialize(doc);   
        return out.toString();
    }
    
     public static String formatXML2(String xml,boolean isIndenting) throws TransformerConfigurationException, TransformerException, ParserConfigurationException, SAXException, IOException{
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        docBuilderFactory.setNamespaceAware(true);
        Document doc = docBuilderFactory.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
        System.out.println(doc.getTextContent());
       TransformerFactory transFactory = TransformerFactory.newInstance(); 
        Transformer transformer = transFactory.newTransformer(); 
        
        //transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no"); 
        //transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(); 
        DOMSource source = new DOMSource(); 
        source.setNode(doc); 
        StreamResult result = new StreamResult(); 
        result.setOutputStream(outputStream);
        transformer.transform(source, result); 
        return outputStream.toString();
    }
    */
      public static String formatXML(String xml,boolean isPrettyPrint) throws DocumentException, IOException{
            org.dom4j.Document doc =  DocumentHelper.parseText(xml);
            org.dom4j.io.OutputFormat format = null;
            if(isPrettyPrint){
                format = org.dom4j.io.OutputFormat.createPrettyPrint();
                format.setIndent(true);
                format.setIndentSize(4);
            }else{
                format = org.dom4j.io.OutputFormat.createCompactFormat();
                format.setSuppressDeclaration(true);
            }
            StringWriter sw = new StringWriter();   
            XMLWriter xw = new XMLWriter(sw, format);   
            xw.setEscapeText(false);  
            xw.write(doc);  
            String s = sw.toString();  
            xw.flush();   
            return  s;
      }
}
