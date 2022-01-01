package cn.edu.whut.sept.zuul.entity;

import junit.framework.TestCase;
import org.junit.Test;

public class ItemTest extends TestCase {
    Item item = new Item("西瓜", "一个大西瓜", 1);
    @Test
    public void testGetLongDescription() {
        String s = "西瓜" + "\t" + "一个大西瓜" + "\t" + "1.0";
        //测试应该正确
        assertEquals(s,item.getLongDescription());
    }
    @Test
    public void testGetShortDescription() {
        String s = "西瓜" + "\t" +"1.0";
        //测试应该正确
        assertEquals(s,item.getShortDescription());
    }
    @Test
    public void testGetWeight() {
        float f = 1.0f;
        //测试应该正确
        assertEquals(f,item.getWeight());

    }
    @Test
    public void testTestGetName() {
        String name = "西瓜";
        //测试应该正确
        assertEquals(name,item.getName());
    }
}