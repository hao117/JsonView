package cy.jsonview.code;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import cy.jsonview.listener.FileOpenActionListener;
import cy.jsonview.listener.FileSaveActionListener;
import cy.jsonview.listener.FindActionListener;
import cy.jsonview.listener.QuitActionListener;
import cy.jsonview.listener.TabDataModelComplexListDataListener;
import cy.jsonview.listener.TreeMouseListener;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.DocumentException;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.SyntaxScheme;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rtextarea.Gutter;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.netbeans.swing.tabcontrol.DefaultTabDataModel;
import org.netbeans.swing.tabcontrol.TabData;
import org.netbeans.swing.tabcontrol.TabDataModel;
import org.netbeans.swing.tabcontrol.TabbedContainer;
import org.openide.util.Exceptions;
import org.xml.sax.SAXException;

public class MainApp extends javax.swing.JFrame {
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JFrame frame;
    private TabDataModel tabDataModel;
    private TabbedContainer tabbedContainer;
    private ResourceBundle bundle;
    private final Map jsonEleTreeMap = new HashMap();
    public static final char dot = 30;
    public String baseResPath = "cy/jsonview/resources/";

    public MainApp() {
        initComponents();
        this.setSize(1000, 600);
        setLocationRelativeTo(getOwner());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("CyJsonView 2.3.2 @藏言");
    }

    private int getTabIndex() {
        return tabbedContainer.getSelectionModel().getSelectedIndex();
    }

    private RSyntaxTextArea getTextArea() {
        return SelCompUtil.getTextArea(tabbedContainer);
    }

    private JMenu createFileMenu() {
        JMenu menu = new javax.swing.JMenu();
        menu.setText("文件");
        JMenuItem menuItemOpenFile = createMenuItem("打开文件", KeyEvent.VK_O);
        menuItemOpenFile.addActionListener(new FileOpenActionListener(frame, tabbedContainer, "打开文件对话框"));
        menu.add(menuItemOpenFile);

        JMenuItem menuItemSaveFile = createMenuItem("保存文件", KeyEvent.VK_S);
        menuItemSaveFile.addActionListener(new FileSaveActionListener(frame, tabbedContainer, "保存文件对话框"));

        menu.add(menuItemSaveFile);

        JMenuItem exitMenuItem = new JMenuItem();
        exitMenuItem.addActionListener(new QuitActionListener());
        exitMenuItem.setText("退出");
        menu.add(exitMenuItem);
        return menu;
    }


    private JMenu createAboutMenu() {
        JMenu menuAbout = new javax.swing.JMenu();
        menuAbout.setText("关于"); // NOI18N
        JMenuItem menuItemLayout = createMenuItem("关于", KeyEvent.VK_A);
        menuItemLayout.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AboutDialog.show(frame);
            }
        });
        menuAbout.add(menuItemLayout);
        return menuAbout;
    }


    private JMenu createToolMenu() {
        JMenu toolMenu = new javax.swing.JMenu();
        toolMenu.setText("工具");

        JMenuItem menuItemLayout = createMenuItem("布局", KeyEvent.VK_L);
        menuItemLayout.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeLayout();
            }
        });
        toolMenu.add(menuItemLayout);

        JMenuItem menuItemNew = createMenuItem("新标签", KeyEvent.VK_N);
        menuItemNew.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addTab("NewTab", true);
            }
        });
        toolMenu.add(menuItemNew);

        return toolMenu;
    }

    private JMenuItem createMenuItem(String name, int keyCode) {
        JMenuItem menuItem = new JMenuItem();
        menuItem.setAccelerator(KeyStroke.getKeyStroke(keyCode, InputEvent.CTRL_MASK));
        menuItem.setText(name);
        return menuItem;
    }

    private void initComponents() {
        frame = this;
        jMenuBar1 = new javax.swing.JMenuBar();

        initTabbedContainer();
        jMenuBar1.add(createFileMenu());
        jMenuBar1.add(createEditMenu());
        jMenuBar1.add(createToolMenu());
        jMenuBar1.add(createAboutMenu());

        setJMenuBar(jMenuBar1);

        this.add(createToolBar(), BorderLayout.PAGE_START);
        this.add(tabbedContainer, BorderLayout.CENTER);
        pack();

    }

    private JMenu createEditMenu() {
        JMenu editMenu = new javax.swing.JMenu();
        editMenu.setText("编辑");
        JMenuItem menuItemClean = createMenuItem("清除", KeyEvent.VK_D);
        menuItemClean.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                getTextArea().setText("");
            }
        });
        editMenu.add(menuItemClean);

        JMenuItem menuItemFormat = createMenuItem("格式化", KeyEvent.VK_F);
        menuItemFormat.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                formatJson();
            }
        });
        editMenu.add(menuItemFormat);
        return editMenu;
    }

    public void setFrameTitle(String title) {
        this.setTitle(title);
    }

    private void initTabbedContainer() {
        TabData tabData = newTabData("Welcome!", "This is a Tab!", null);
        tabDataModel = new DefaultTabDataModel(new TabData[]{tabData});
        tabbedContainer = new TabbedContainer(tabDataModel, TabbedContainer.TYPE_EDITOR);
        tabbedContainer.getSelectionModel().setSelectedIndex(0);
        tabbedContainer.setShowCloseButton(true);
        tabDataModel.addComplexListDataListener(new TabDataModelComplexListDataListener(jsonEleTreeMap));
        /*
        tabbedContainer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //System.out.println("@@@:TabbedContainerActionCommand = "+e.getActionCommand());
                if("select".equalsIgnoreCase(e.getActionCommand())){
                    treePathLst.clear();
                }
            }
        });
        */

    }

    private TabData newTabData(String tabName, String tabTip, Icon icon) {
        final JSplitPane splitPane = new JSplitPane();
        splitPane.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                splitPane.setDividerLocation(0.6);
            }

            @Override
            public void componentMoved(ComponentEvent e) {
            }

            @Override
            public void componentShown(ComponentEvent e) {
            }

            @Override
            public void componentHidden(ComponentEvent e) {
            }
        });

        RSyntaxTextArea textArea = newTextArea();
        RTextScrollPane sp = new RTextScrollPane(textArea);
        sp.setFoldIndicatorEnabled(true);
        sp.setIconRowHeaderEnabled(true);
        Gutter gutter = sp.getGutter();
        gutter.setBookmarkingEnabled(true);
        URL url = getClass().getClassLoader().getResource("cy/jsonview/resources/bookmark.png");
        gutter.setBookmarkIcon(new ImageIcon(url));
        splitPane.setLeftComponent(sp);
        splitPane.setRightComponent(new JScrollPane(newTree()));
        TabData tabData = new TabData(splitPane, icon, tabName, tabTip);
        return tabData;
    }

    private void addPopupMenuItemCopyXml(RSyntaxTextArea textArea) {
        JPopupMenu popMenu = textArea.getPopupMenu();
        popMenu.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                String text = getTextArea().getText();
                JPopupMenu popMenu = getTextArea().getPopupMenu();
                JMenuItem mtCopyXml = (JMenuItem) popMenu.getComponent(popMenu.getComponentCount() - 1);
                int num = text.indexOf("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                if (num >= 0 && num < 2) {
                    mtCopyXml.setEnabled(true);
                } else {
                    mtCopyXml.setEnabled(false);
                }
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
            }
        });

        JMenuItem mtCopyXml = new JMenuItem("复制XML");
        mtCopyXml.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String xml = getTextArea().getText();
                StringSelection stringSelection;
                try {
                    xml = Kit.formatXML(xml, false);
                    xml = StringUtils.remove(xml, "<?xml versio<?xmln=\"1.0\" encoding=\"UTF-8\"?>");
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    stringSelection = new StringSelection(xml);
                    clipboard.setContents(stringSelection, null);
                } catch (HeadlessException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (DocumentException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        });
        popMenu.add(mtCopyXml);
    }

    private RSyntaxTextArea newTextArea() {
        RSyntaxTextArea textArea = new RSyntaxTextArea();
        textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT);
        textArea.setCodeFoldingEnabled(true);
        textArea.setAntiAliasingEnabled(true);
        textArea.setAutoscrolls(true);
        textArea.setMarkOccurrences(true);
        textArea.setTabSize(4);
        textArea.setAnimateBracketMatching(true);
        textArea.setPaintTabLines(true);
        addPopupMenuItemCopyXml(textArea);

        //textArea.setLineWrap(true);

        SyntaxScheme scheme = textArea.getSyntaxScheme();
        scheme.getStyle(Token.LITERAL_STRING_DOUBLE_QUOTE).foreground = Color.BLUE;
        scheme.getStyle(Token.LITERAL_NUMBER_DECIMAL_INT).foreground = new Color(164, 0, 0);
        scheme.getStyle(Token.LITERAL_NUMBER_FLOAT).foreground = new Color(164, 0, 0);
        scheme.getStyle(Token.LITERAL_BOOLEAN).foreground = Color.RED;
        scheme.getStyle(Token.OPERATOR).foreground = Color.BLACK;
        textArea.revalidate();
        //textArea.addMouseListener(new MainView.TextAreaMouseListener());
        return textArea;
    }

    private JTree newTree() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("o-JSON");
        DefaultTreeModel model = new DefaultTreeModel(root);
        JTree tree = new JTree(model);
        setNodeIcon(tree);
        tree.addMouseListener(new TreeMouseListener(tree, jsonEleTreeMap));
        return tree;
    }

    private void formatJson() {
        //格式化字符串
        JTextArea ta = getTextArea();
        String text = ta.getText();
        char c = text.trim().charAt(0);
        if (c == '{' || c == '[') {
            buildJson(text);
        }
    }

    private void buildJson(String text) {
        try {
            Object jsonobj = JSON.parse(text, Feature.AllowSingleQuotes);
            String jsonText = JSON.toJSONString(jsonobj, SerializerFeature.WriteMapNullValue, SerializerFeature.PrettyFormat);
            if (jsonText != null) {
                jsonText = StringEscapeUtils.unescapeJava(jsonText);
                //getTextArea().setAutoIndentEnabled(true);
                getTextArea().setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT);
                getTextArea().setText(jsonText);
            }
            createTree(jsonobj);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
            showMessageDialog("非法JSON字符串！", ex.getMessage());
        }
        System.gc();
    }

    private JTree getTree() {
        return SelCompUtil.getTree(tabbedContainer);
    }

    private String getTabTitle() {
        return tabDataModel.getTab(getTabIndex()).getText();
    }

    private void analyzeJsonObject(JSONObject jobj, DefaultMutableTreeNode treeNode) {
        if (jobj.isEmpty()) {
            return;
        }
        List<String> keyList = new ArrayList<String>();
        Iterator<String> itkey = jobj.keySet().iterator();
        while (itkey.hasNext()) {
            keyList.add(itkey.next());
        }
        Collections.sort(keyList);
        for (String key : keyList) {
            Object obj = jobj.get(key);
            if (obj instanceof JSONObject) {
                DefaultMutableTreeNode node = Kit.objNode(key);
                analyzeJsonObject((JSONObject) obj, node);
                treeNode.add(node);
            } else if (obj instanceof JSONArray) {
                analyzeJsonArray((JSONArray) obj, treeNode, key);
            } else if (obj instanceof String) {
                treeNode.add(Kit.strNode(key, (String) obj));
            } else if (obj instanceof Number) {
                treeNode.add(Kit.numNode(key, obj.toString()));
            } else if (obj instanceof Boolean) {
                treeNode.add(Kit.boolNode(key, (Boolean) obj));
            } else if (obj == null) {
                treeNode.add(Kit.nullNode(key));
            }
        }
    }

    private void analyzeJsonArray(JSONArray jarr, DefaultMutableTreeNode rootNode, String key) {
        int index = 0;
        DefaultMutableTreeNode topNode = Kit.arrNode(key);
        for (Iterator it = jarr.iterator(); it.hasNext(); ) {
            Object el = it.next();
            if (el instanceof JSONObject) {
                DefaultMutableTreeNode childNode = Kit.objNode(index);
                analyzeJsonObject((JSONObject) el, childNode);
                topNode.add(childNode);
            } else if (el instanceof JSONArray) {
                analyzeJsonArray((JSONArray) el, topNode, Kit.fkey(index));
            } else if (el instanceof String) {
                topNode.add(Kit.strNode(Kit.fkey(index), el.toString()));
            } else if (el instanceof Number) {
                topNode.add(Kit.numNode(Kit.fkey(index), el.toString()));
            } else if (el instanceof Boolean) {
                topNode.add(Kit.boolNode(Kit.fkey(index), (Boolean) el));
            } else if (el == null) {
                topNode.add(Kit.nullNode(Kit.fkey(index)));
            }
            ++index;
        }
        rootNode.add(topNode);
    }

    private void setNodeIcon(JTree tree) {
        tree.setCellRenderer(new DefaultTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                String tmp = node.toString();
                ClassLoader classLoader = getClass().getClassLoader();
                ImageIcon icon;
                if (tmp.startsWith(Kit.sArr)) {
                    icon = new ImageIcon(classLoader.getResource(baseResPath + "a.gif"));
                } else if (tmp.startsWith(Kit.sStr)) {
                    icon = new ImageIcon(classLoader.getResource(baseResPath + "v.gif"));
                } else if (tmp.startsWith(Kit.sObj)) {
                    icon = new ImageIcon(classLoader.getResource(baseResPath + "o.gif"));
                } else if (tmp.startsWith(Kit.sNum)) {
                    icon = new ImageIcon(classLoader.getResource(baseResPath + "n.gif"));
                } else if (tmp.startsWith(Kit.sNull)) {
                    icon = new ImageIcon(classLoader.getResource(baseResPath + "k.gif"));
                } else if (tmp.startsWith(Kit.sBool)) {
                    icon = new ImageIcon(classLoader.getResource(baseResPath + "v.gif"));
                } else {
                    icon = new ImageIcon(classLoader.getResource(baseResPath + "v.gif"));
                }
                this.setIcon(icon);
                this.setText(tmp.substring(2));
                return this;
            }

        });
    }

    private void createTree(Object jsonobj) {
        //创建树节点
        DefaultMutableTreeNode root = null;
        DefaultTreeModel model = null;
        try {
            JTree tree = getTree();
            System.out.println("Put HashCode : " + tree.hashCode() + " . TabTitle : " + getTabTitle() + " !");
            jsonEleTreeMap.put(tree.hashCode(), jsonobj);
            root = Kit.objNode("JSON");
            model = (DefaultTreeModel) tree.getModel();
//          root = (DefaultMutableTreeNode) model.getRoot();
            if (jsonobj instanceof JSONObject) {
                analyzeJsonObject((JSONObject) jsonobj, root);
            } else if (jsonobj instanceof JSONArray) {
                analyzeJsonArray((JSONArray) jsonobj, root, "");
            }
            tree.setVisible(true);
            model.setRoot(root);
            setNodeIcon(tree);
        } catch (Exception ex) {
            Log.write(ex);
            Exceptions.printStackTrace(ex);
            showMessageDialog("创建json树失败！", ex.getMessage());
            if (root != null) {
                root.removeAllChildren();
            }
            if (null != model) {
                model.setRoot(root);
            }
        }
        System.gc();
    }

    private JButton createBtnAppTitle(final JTextField textField) {
        JButton btnAppTitle = new JButton("标题修改");
        btnAppTitle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setTitle(textField.getText());
            }
        });
        return btnAppTitle;
    }

    private JButton createBtnFormat() {
        JButton btnFormat = new JButton("格式化JSON(F)");
        btnFormat.setBackground(new Color(255, 184, 72));
        btnFormat.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                formatJson();
            }
        });
        return btnFormat;
    }

    private JButton createBtnFormatXml() {
        JButton btnFormatXml = new JButton("格式化XML");
        btnFormatXml.setBackground(new Color(3, 98, 253));
        btnFormatXml.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                formatXml();
            }
        });
        return btnFormatXml;
    }

    private JButton createBtnConvertXml() {
        JButton btnConvertXml = new JButton("XML->JSON ");
        btnConvertXml.setBackground(Color.YELLOW);
        btnConvertXml.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                convertXml();
            }
        });
        return btnConvertXml;
    }

    private JButton createBtnConvertMap() {
        JButton btnConvertMap = new JButton("MAP->JSON ");
        btnConvertMap.setBackground(Color.PINK);
        btnConvertMap.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                convertMap();
            }
        });
        return btnConvertMap;
    }

    private JButton createBtnClean() {
        JButton btnClean = new JButton("清空(D)");
        btnClean.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextArea ta = getTextArea();
                if (ta != null) {
                    ta.setText("");
                }

            }
        });
        return btnClean;
    }

    private JButton createBtnParse() {
        JButton btnParse = new JButton("粘帖(V)");
        btnParse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextArea ta = getTextArea();
                if (ta != null) {
                    ta.paste();
                    //formatJson();
                }
            }
        });
        return btnParse;
    }

    private JButton createBtnNewLine() {
        JButton btnNewLine = new JButton("清除(\\n)");
        btnNewLine.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextArea ta = getTextArea();
                if (ta != null) {
                    ta.setText(ta.getText().replaceAll("\n", ""));
                }
            }
        });
        return btnNewLine;
    }

    private JButton createBtnXG() {
        JButton btnXG = new JButton("清除(\\)");
        btnXG.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextArea ta = getTextArea();
                if (ta != null) {
                    ta.setText(ta.getText().replaceAll("\\\\", ""));
                }
            }
        });
        return btnXG;
    }

    private JButton createBtnTxtFind() {
        JButton btnTxtFind = new JButton("文本查找");
        btnTxtFind.addActionListener(new FindActionListener(frame, tabbedContainer, 1, "文本查找对话框"));
        return btnTxtFind;
    }

    private JButton createBtnNodeFind() {
        JButton btnNodeFind = new JButton("节点查找");
        btnNodeFind.addActionListener(new FindActionListener(frame, tabbedContainer, 2, "树节点查找对话框"));
        return btnNodeFind;
    }

    private JButton createBtnSelTabName(final JTextField textField) {
        JButton btnSelTabName = new JButton("标签名修改");
        btnSelTabName.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selIndex = getTabIndex();
                if (selIndex >= 0) {
                    tabDataModel.setText(selIndex, textField.getText());
                    //System.out.println("Modify HashCode : " + getTree(selIndex).hashCode() + " . TabTitle : " + textField.getText() + " !");
                }
            }
        });
        return btnSelTabName;
    }

    private JButton createBtnNewTab() {
        JButton btnNewTab = new JButton("新标签(N)");
        btnNewTab.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addTab("NewTab", true);
            }
        });
        return btnNewTab;
    }

    private JButton createBtnCloseTab() {
        JButton btnCloseTab = new JButton("关闭标签");
        btnCloseTab.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selIndex = getTabIndex();
                if (selIndex >= 0) {
                    tabDataModel.removeTab(selIndex);
                }
            }
        });
        return btnCloseTab;
    }

    private JToolBar createToolBar() {
        JToolBar toolbar = new JToolBar();
        final JTextField textField = new JTextField();
        textField.setMaximumSize(new Dimension(180, 25));
        toolbar.setMargin(new Insets(0, 13, 0, 13));
        toolbar.add(createBtnNewTab());
        toolbar.add(createBtnCloseTab());
        toolbar.add(createBtnFormat());
        toolbar.add(createBtnConvertXml());
        toolbar.add(createBtnConvertMap());
        toolbar.add(createBtnFormatXml());
        toolbar.add(createBtnClean());
        toolbar.add(createBtnParse());
        toolbar.add(createBtnNewLine());
        toolbar.add(createBtnXG());
        toolbar.add(createBtnNodeFind());
        toolbar.add(createBtnTxtFind());
        toolbar.addSeparator(new Dimension(30, 20));
        toolbar.add(textField);
        toolbar.add(createBtnAppTitle(textField));
        toolbar.add(createBtnSelTabName(textField));
        return toolbar;
    }

    private void convertXml() {
        JTextArea ta = getTextArea();
        String text = ta.getText();
        try {
            Map map = Kit.xmlToMap(text);
            String str = JSON.toJSONString(map, SerializerFeature.WriteMapNullValue);
            buildJson(str);
        } catch (IOException ex) {
            showMessageDialog("转换异常，非法XML字符串！", ex.getMessage());
        } catch (ParserConfigurationException ex) {
            showMessageDialog("转换异常，非法XML字符串！", ex.getMessage());
        } catch (SAXException ex) {
            showMessageDialog("转换异常，非法XML字符串！", ex.getMessage());
        }

    }

    private void convertMap() {
        JTextArea ta = getTextArea();
        String text = ta.getText();
        text = Kit.mapStringToJsonString(text);
        buildJson(text);
    }

    private void formatXml() {
        RSyntaxTextArea ta = getTextArea();
        String xml = ta.getText();
        ta.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_XML);
        try {
            xml = Kit.formatXML(xml, true);
            ta.setText(xml);
            Map json = Kit.xmlToMap(xml);
            Object jsonobj = JSON.toJSON(json);
            createTree(jsonobj);
        } catch (ParserConfigurationException ex) {
            showMessageDialog("xml格式化异常", "ParserConfigurationException:\n" + ex.getMessage());
        } catch (SAXException ex) {
            showMessageDialog("xml格式化异常", "SAXException:\n" + ex.getMessage());
        } catch (IOException ex) {
            showMessageDialog("xml格式化异常IOException", "IOException:\n" + ex.getMessage());
        } catch (DocumentException ex) {
            showMessageDialog("xml转换异常Exception", "Exception:\n" + ex.getMessage());
        }
        try {

        } catch (Exception e) {

        }
    }

    private int addTab(String tabName, boolean isSel) {
        TabData tabData = newTabData(tabName, tabName, null);
        int newIndex = tabbedContainer.getTabCount();
        tabDataModel.addTab(newIndex, tabData);
        if (isSel) {
            tabbedContainer.getSelectionModel().setSelectedIndex(newIndex);
        }
        return newIndex;
    }

    public static void main(String args[]) {
        try {
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainApp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainApp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainApp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainApp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainApp().setVisible(true);
            }
        });
    }

    private void showMessageDialog(String title, String msg) {
        if (msg == null) {
            msg = "";
        }
        JDialog dlg = new JDialog(frame);
        dlg.setTitle(title);
        dlg.setMinimumSize(new Dimension(350, 160));
        BorderLayout layout = new BorderLayout();
        dlg.getContentPane().setLayout(layout);
        dlg.getContentPane().add(new JLabel("异常信息："), BorderLayout.NORTH);
        JTextArea ta = new JTextArea();
        ta.setLineWrap(true);
        ta.setText(msg);
        ta.setWrapStyleWord(true);
        dlg.setLocationRelativeTo(frame);
        dlg.getContentPane().add(new JScrollPane(ta), BorderLayout.CENTER);
        dlg.setVisible(true);
    }

    private void changeLayout() {
        int selIndex = getTabIndex();
        if (selIndex < 0) {
            return;
        }
        TabData selTabData = tabDataModel.getTab(selIndex);
        JSplitPane splitPane = (JSplitPane) selTabData.getComponent();
        double dividerLocation = 0.6;
        if (splitPane.getOrientation() == JSplitPane.VERTICAL_SPLIT) {
            splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
            splitPane.setDividerLocation(dividerLocation);
        } else {
            splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
            splitPane.setDividerLocation(dividerLocation);
        }
    }

}
