package cn.edu.whut.sept.zuul.entity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * <code>Room</code>类的对象表示一个房间对象，是游戏活动的主要区域。<br>
 *  一个Room对象代表游戏中的一个位置。房间可以由出口通到其它房间.
 *
 *@author txg
 *@version 创建时间：2021年12月31日 上午11:24:21
 */
public class Room {
    private String description;
    private boolean isTransferPoint;
    /**
     * 用 HashMap 存放了一个房间的各种出口与其对应房间的 key-value 值.
     */
    private HashMap<String, Room> exits;
    private HashSet<Item> items;

    /**
     * 构造方法，初始化房间的描述和<code>HashMap</code>容器.
     *
     * @param description 对房间的描述
     */
    public Room(String description) {
        this.description = description;
        exits = new HashMap<>();
        items = new HashSet<>();
    }

    /**
     * set方法，为房间添加对应的出口与出口对应的其它房间.
     *
     * @param direction 出口方向
     * @param neighbor 出口对应的房间
     */
    public void setExit(String direction, Room neighbor) {
        exits.put(direction, neighbor);
    }

    /**
     * 得到房间简单的描述.
     *
     *@return 对房间进行一个简短的描述
     */
    public String getShortDescription() {
        return description;
    }

    /**
     * 得到房间细致的描述.
     *
     *@return 对房间进行一个细致描述
     */
    public String getLongDescription() {
        return "你发现自己在 " + description + ".\n" + getItemsDescription() + getExitString();
    }

    /**
     * 获取所有物品的描述信息.
     *
     *@return 物品们的描述信息
     */
    public String getItemsDescription() {
        if (items.isEmpty()) {
            return "这个地方啥都没有" + '\n';
        }
        StringBuilder s = new StringBuilder("");
        for (Item item : items) {
            s.append(item.getName() + "\t" + item.getDescription() + "\t" + item.getWeight() + "kg" + "\n");
        }
        return "这个地方有:\n" + s.toString();
    }

    /**
     * 获得所有出口的信息.
     *
     * @return 返回房间的所有出口信息
     */
    private String getExitString() {
        String returnString = "Exits:";
        //获取map对应的set
        Set<String> keys = exits.keySet();
        //输出出口
        for (String exit : keys) {
            returnString += " " + exit;
        }
        return returnString;
    }

    /**
     * 通过方向指令，获得该方向对应的<code>Room</code>对象.
     *
     * @param direction 方向指令
     * @return 返回这个房间某个出口对应的<code>Room</code>对象
     */
    public Room getExit(String direction) {
        return exits.get(direction);
    }

    /**
     * 根据物品名，查看该房间的物品列表，看是否有该物品.
     *
     * @param name 物品名
     * @return 有该物品，返回该物品对象；无则返回 null
     */
    public Item getItem(String name) {
        for (Item item : items) {
            if (item.getName().equals(name)) {
                return item;
            }
        }
        return null;
    }
    /**
     * 向房间中增加一个物品。
     *
     * @param item 物品对象
     */
    public void addItem(Item item) {
        items.add(item);
    }

    /**
     * 判断是否存在某个出口.
     *
     * @param description 出口名
     * @return 存在则返回 true,否则返回 false
     */
    public boolean hasExit(String description) {
        if (exits.containsKey(description)) {
            return true;
        }
        return false;
    }

    public boolean isTransferPoint() {
        return isTransferPoint;
    }

    public void setTransferPoint(boolean transferPoint) {
        isTransferPoint = transferPoint;
    }

    public HashSet<Item> getItems() {
        return items;
    }
}


