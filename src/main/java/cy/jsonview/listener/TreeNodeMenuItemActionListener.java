/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cy.jsonview.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import cy.jsonview.code.Kit;
import cy.jsonview.code.MainApp;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import javax.swing.JTree;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author cangyan
 */
public class TreeNodeMenuItemActionListener implements ActionListener{
        private final int optType;
        private final Object obj;
        private final JTree tree;
        private final Map jsonEleTreeMap;
        /**
         * optType 1:key;2:value;3:key value
         * @param optType
         */
        public TreeNodeMenuItemActionListener(JTree tree,int optType,Object obj,Map jsonEleTreeMap){
            this.optType = optType;
            this.obj = obj;
            this.tree = tree;
            this.jsonEleTreeMap = jsonEleTreeMap;
        }
        /**
         * 复制节点路径.
         * @param treePath
         * @return 
         */
        public String copyTreeNodePath(TreePath treePath){
            String str = "";
            String s;
            int len =  treePath.getPathCount() -1;
            for(int i = 0; i <= len; i++){
                s = treePath.getPathComponent(i).toString();
                if(i>0) str += String.valueOf(MainApp.dot);
                if(i == len) {
                    str += Kit.pstr(s)[1];
                }else{
                    str += s.substring(2);
                }
            }
            str = StringUtils.replace(str, String.valueOf(MainApp.dot)+"[", "[");
            str = StringUtils.substring(str, 5);
            return str;
        }
        /**
         * 复制相似路径节点键值对.
         * @param treeNode
         * @return 
         */
        public String copySimilarPathKeyValue(TreeNode treeNode){
            String str = "";
            String key = Kit.pstr(treeNode.toString())[1];
            TreeNode node = treeNode.getParent();
            if(node==null) return "";
            node = node.getParent();
            if(node == null) return "";
            int count = node.getChildCount();
            int size = 0;
            for(int i = 0; i < count; i++){
                TreeNode child = node.getChildAt(i);
                if(child==null) continue;
                size = child.getChildCount();
                for(int i2 = 0; i2 < size; i2++){
                    TreeNode tmp = child.getChildAt(i2);
                    if(tmp==null)continue;
                    String arr[] = Kit.pstr(tmp.toString());
                    if(key!=null && key.equals(arr[1])){
                        str += arr[2] + "\n";
                    }
                }
            }
            return str;
        }
        /**
         * 复制节点内容.
         * @param path 节点路径
         * @param isFormat 是否带格式
         * @return 
         */
        private String copyNodeContent(String path,boolean isFormat){
            String str;
            String arr[] = StringUtils.split(path, String.valueOf(MainApp.dot));
            //System.out.println("Get HashCode : " + tree.hashCode() + " . TabTitle : " + getTabTitle());
            Object obj = jsonEleTreeMap.get(tree.hashCode());
            if(arr.length<=0){
                return "";
            }
            for (String arr1 : arr) {
                int index = Kit.getIndex(arr1);
                String key = Kit.getKey(arr1);
                if (index == -1) {
                    obj = ((JSONObject) obj).get(key);
                } else {
                    obj = ((JSONObject) obj).getJSONArray(key).get(index);
                }
            }
            if (obj == null) {
                return "";
            }
            if (isFormat) {
                str = JSON.toJSONString(obj, SerializerFeature.WriteMapNullValue, SerializerFeature.PrettyFormat);
            } else {
                str = JSON.toJSONString(obj, SerializerFeature.WriteMapNullValue);
            }
            return str;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            if(obj==null) return;
            StringSelection stringSelection = null;
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            if(optType == 4){
                 String path = copyTreeNodePath((TreePath)obj);
                 path = StringUtils.replace(path, String.valueOf(MainApp.dot), ".");
                 stringSelection = new StringSelection(path);
                 clipboard.setContents(stringSelection, null);
            }
            else if(optType == 5){
                 stringSelection = new StringSelection(copySimilarPathKeyValue((TreeNode)obj));
                 clipboard.setContents(stringSelection, null);
            }
            else if(optType == 6 || optType == 7){
                String path = copyTreeNodePath((TreePath)obj);
                boolean isForamt = false;
                if(optType == 7) isForamt = true;
                String str = copyNodeContent(path,isForamt);
                stringSelection = new StringSelection(str);
                clipboard.setContents(stringSelection, null);
            }
            else{
                String str = obj.toString();
                String[] arr = Kit.pstr(str);
                 if("<null>".equals(arr[2])){
                        arr[2] = "null";
                 }
                if (optType == 1 || optType == 2){
                    stringSelection = new StringSelection(arr[optType]);
                } else if (optType == 3) {
                    stringSelection = new StringSelection(str.substring(2));
                }else if(optType == 8){
                    String temp = "\"" + arr[1] + "\",\"" + arr[2] + "\"";
                    stringSelection = new StringSelection(temp);
                }
                clipboard.setContents(stringSelection, null);
            }
        }
    
}
