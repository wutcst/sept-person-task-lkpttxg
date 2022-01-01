package cn.edu.whut.sept.zuul.entity;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

/**
 * 玩家类，实例化游戏玩家，可为以后多玩家游戏搭建基础.
 *
 *@author txg
 * @version 2022.1.1
 */
public class Player {
    private String name;
    private float maxBearWeight;
    private float nowWeight;
    private Set<Item> items;
    private Room currentRoom;
    private Stack<Room> roomHistory;

    /**
     * 构造方法，实例化玩家类.
     *
     * @param name 玩家信息
     * @param maxBearWeight 玩家最大承受重量
     * @param nowWeight 玩家现在的承受重量
     */
    public Player(String name, float maxBearWeight, float nowWeight) {
        this.name = name;
        this.maxBearWeight = maxBearWeight;
        this.nowWeight = nowWeight;
        //初始化集合
        items = new HashSet<>();
        roomHistory = new Stack<>();
    }


    /**
     * 添加物品.
     *
     * @param item 物品对象
     * @return 可以添加为 true,不能添加为 flase
     */
    public boolean addItem(Item item) {
        if (!isOver(item)) {
            items.add(item);
            nowWeight += item.getWeight();
            return true;
        }
        return false;
    }

    /**
     * 判断加入某个物体后是否超重.
     *
     * @param item 物体对象
     * @return 超重为true，否则为flase
     */
    public boolean isOver(Item item) {
        return maxBearWeight < item.getWeight() + nowWeight;
    }

    /**
     * 从玩家的物品列表中获取物品.
     *
     * @param name 物品名
     * @return 如果物品存在，则返回该物品对象；否则返回 null
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
     * 丢弃player身上的物品.
     *
     * @param item 丢弃物品
     */
   public void dropItem(Item item) {
        items.remove(item);
        nowWeight -= item.getWeight();
   }

    /**
     * 展示Player身上所有的物品.
     *
     * @return 返回展示字符串
     */
   public String showItems() {
       if (items.isEmpty()) {
           return "你身上啥都没有" + '\n';
       }
       StringBuilder s = new StringBuilder("");
       for (Item item : items) {
           s.append(item.getName() + "\t" + item.getWeight() + "kg" + "\n");
       }
       return "你身上有:\n" + s.toString();
   }

    /**
     * 完整的描述自己.
     *
     * @return 返回描述字段
     */
   public String getSelfLongDescription() {
       return "姓名：" + name + "\n"
               + "最大耐受量:" + maxBearWeight + "kg" + "\n"
               + "目前耐受量:" + nowWeight + "kg" + "\n"
               + "剩余耐受量:" + (maxBearWeight - nowWeight) + "kg" + "\n"
               + "目前所在：" + getCurrentRoom().getShortDescription() + "\n"
               + showItems();
   }

    //以下都是对属性字段的 getter 和 setter 方法

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getMaxBearWeight() {
        return maxBearWeight;
    }

    public Set<Item> getItems() {
        return items;
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(Room currentRoom) {
        this.currentRoom = currentRoom;
    }

    public Stack<Room> getRoomHistory() {
        return roomHistory;
    }

    public float getNowWeight() {
        return nowWeight;
    }

    public void setMaxBearWeight(float maxBearWeight) {
        this.maxBearWeight = maxBearWeight;
    }

}
