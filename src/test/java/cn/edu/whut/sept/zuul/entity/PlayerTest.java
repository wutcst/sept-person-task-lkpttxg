package cn.edu.whut.sept.zuul.entity;

import junit.framework.TestCase;
import org.junit.Test;

public class PlayerTest extends TestCase {
    @Test
    public void testAddItem() {
        Player player = new Player("TXG", 100, 0);
        //因为 player 初始化时自动初始化存放 Item 的集合，所有通过添加 item,看是否集合中元素+1
        int count1 = player.getItems().size();
        player.addItem(new Item("1","1",0));
        int count2 = player.getItems().size();
        //应该测试正确
        assertEquals(count2,count1+1);
    }
    @Test
    public void testIsOver() {
        Player player = new Player("TXG", 100, 0);
        Item item = new Item("石头","一块石头",50);
        Item item1 = new Item("大石头","一块大石头",120);
        //均测试成功
        assertEquals(false, player.isOver(item));
        assertEquals(true, player.isOver(item1));
    }
    @Test
    public void testGetItem() {
        Player player = new Player("TXG", 100, 0);
        Item item = new Item("石头","一块石头",50);
        player.addItem(item);
        //可以找到，测试成功
        assertEquals(item,player.getItem("石头"));
    }
    @Test
    public void testDropItem() {
        Player player = new Player("TXG", 100, 0);
        Item item = new Item("石头","一块石头",50);
        player.addItem(item);
        player.dropItem(item);
        //正确
        assertEquals(0, player.getItems().size());
    }
    @Test
    public void testShowItems() {
        Player player = new Player("TXG", 100, 0);
        Item item = new Item("大石头","一块大石头",50);
        Item item1 = new Item("石头","一块石头",10);
        player.addItem(item);
        player.addItem(item1);
        String s = "你身上有:\n石头\t10.0kg\n" +
                "大石头\t50.0kg\n";
        //正确
        assertEquals(s,player.showItems());
    }
    @Test
    public void testGetSelfLongDescription() {
        Player player = new Player("TXG", 100, 0);
        Item item = new Item("大石头","一块大石头",50);
        Item item1 = new Item("石头","一块石头",10);
        player.addItem(item);
        player.addItem(item1);
        player.setCurrentRoom(new Room("一个房间"));
        String s = "姓名：TXG\n"
                + "最大耐受量:100.0kg\n"
                + "目前耐受量:60.0kg\n"
                + "剩余耐受量:40.0kg\n"
                + "目前所在：一个房间\n"
                + "你身上有:\n"
                + "大石头\t50.0kg\n"
                + "石头\t10.0kg\n";
        //正确
        assertEquals(s,player.getSelfLongDescription());
    }
}