package cy.jsonview.code;

import static org.junit.Assert.*;
import org.junit.Test;
import java.util.Map;
import javax.swing.tree.DefaultMutableTreeNode;

public class KitTest {

    @Test
    public void testFkey() {
        assertEquals("[0]", Kit.fkey(0));
        assertEquals("[5]", Kit.fkey(5));
        assertEquals("[10]", Kit.fkey(10));
    }

    @Test
    public void testGetIndex() {
        assertEquals(5, Kit.getIndex("test[5]"));
        assertEquals(0, Kit.getIndex("[0]"));
        assertEquals(-1, Kit.getIndex("no-brackets"));
        assertEquals(-1, Kit.getIndex(null));
        assertEquals(-1, Kit.getIndex(""));
    }

    @Test
    public void testGetKey() {
        assertEquals("test", Kit.getKey("test[5]"));
        assertEquals("", Kit.getKey("[0]"));
        assertEquals("no-brackets", Kit.getKey("no-brackets"));
        assertNull(Kit.getKey(null));
        assertEquals("", Kit.getKey(""));
    }

    @Test
    public void testMapStringToJsonString() {
        String input = "{name=John,age=30}";
        String expected = "{\"name\":\"John\",\"age\":\"30\"}";
        assertEquals(expected, Kit.mapStringToJsonString(input));
    }

    @Test
    public void testMapStringToJsonStringWithNested() {
        String input = "{user={name=John},ids=[1,2,3]}";
        String result = Kit.mapStringToJsonString(input);
        assertTrue(result.contains("\"user\":{"));
        assertTrue(result.contains("\"ids\":["));
    }

    @Test
    public void testMapStringToJsonStringEmpty() {
        assertEquals("{}", Kit.mapStringToJsonString("{}"));
        assertEquals("[]", Kit.mapStringToJsonString("[]"));
    }

    @Test
    public void testPstr() {
        String[] result = Kit.pstr("v-key : \"value\"");
        assertEquals("v", result[0]);
        assertEquals("key", result[1]);
        assertEquals("value", result[2]);
    }

    @Test
    public void testPstrArray() {
        String[] result = Kit.pstr("a-[0]");
        assertEquals("a", result[0]);
        assertEquals("[0]", result[1]);
        assertEquals(Kit.array, result[2]);
    }

    @Test
    public void testPstrObject() {
        String[] result = Kit.pstr("o-user");
        assertEquals("o", result[0]);
        assertEquals("user", result[1]);
        assertEquals(Kit.object, result[2]);
    }

    @Test
    public void testNullNode() {
        DefaultMutableTreeNode node = Kit.nullNode("test");
        assertNotNull(node);
        assertTrue(node.toString().contains("test"));
    }

    @Test
    public void testNumNode() {
        DefaultMutableTreeNode node = Kit.numNode("age", "25");
        assertNotNull(node);
        assertTrue(node.toString().contains("age"));
        assertTrue(node.toString().contains("25"));
    }

    @Test
    public void testBoolNode() {
        DefaultMutableTreeNode node = Kit.boolNode("active", true);
        assertNotNull(node);
        assertTrue(node.toString().contains("active"));
        assertTrue(node.toString().contains("true"));
    }

    @Test
    public void testStrNode() {
        DefaultMutableTreeNode node = Kit.strNode("name", "John");
        assertNotNull(node);
        assertTrue(node.toString().contains("name"));
        assertTrue(node.toString().contains("John"));
    }

    @Test
    public void testObjNode() {
        DefaultMutableTreeNode node = Kit.objNode("user");
        assertNotNull(node);
        assertTrue(node.toString().contains("user"));
    }

    @Test
    public void testArrNode() {
        DefaultMutableTreeNode node = Kit.arrNode("items");
        assertNotNull(node);
        assertTrue(node.toString().contains("items"));
    }

    @Test
    public void testFArrKey() {
        assertEquals("a-[5]", Kit.fArrKey(5));
    }

    @Test
    public void testTreeNode() {
        DefaultMutableTreeNode node = Kit.treeNode("test");
        assertEquals("test", node.toString());
    }

    @Test
    public void testFormatXML() throws Exception {
        String xml = "<root><name>John</name></root>";
        String formatted = Kit.formatXML(xml, true);
        assertTrue(formatted.contains("root"));
        assertTrue(formatted.contains("name"));
        assertTrue(formatted.contains("John"));
    }

    @Test
    public void testFormatXMLCompact() throws Exception {
        String xml = "<root><name>John</name></root>";
        String formatted = Kit.formatXML(xml, false);
        assertTrue(formatted.contains("<root><name>John</name></root>"));
    }
}
