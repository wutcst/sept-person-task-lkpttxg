package cn.edu.whut.sept.zuul;

import java.util.Set;
import java.util.HashMap;

/**
 * <code>Room</code>类的对象表示一个房间对象，是游戏活动的主要区域。<br>
 *  一个Room对象代表游戏中的一个位置。房间可以由出口通到其它房间。
 *@author txg
 *@version 创建时间：2021年12月31日 上午11:24:21
 */
public class Room
{
    private String description;
    /**
     * 用 HashMap 存放了一个房间的各种出口与其对应房间的 key-value 值
     */
    private HashMap<String, Room> exits;

    /**
     * 构造方法，初始化房间的描述和<code>HashMap</code>容器
     * @param description 对房间的描述
     */
    public Room(String description)
    {
        this.description = description;
        exits = new HashMap<>();
    }

    /**
     * set方法，为房间添加对应的出口与出口对应的其它房间
     * @param direction 出口方向
     * @param neighbor 出口对应的房间
     */
    public void setExit(String direction, Room neighbor)
    {
        exits.put(direction, neighbor);
    }

    /**
     * @return 对房间进行一个简短的描述
     */
    public String getShortDescription()
    {
        return description;
    }

    /**
     *
     * @return 对房间进行一个细致描述
     */
    public String getLongDescription()
    {
        return "You are " + description + ".\n" + getExitString();
    }

    /**
     * @return 返回房间的所有出口信息
     */
    private String getExitString()
    {
        String returnString = "Exits:";
        //获取map对应的set
        Set<String> keys = exits.keySet();
        //输出出口
        for(String exit : keys) {
            returnString += " " + exit;
        }
        return returnString;
    }

    /**
     * 通过方向指令，获得该方向对应的<code>Room</code>对象
     * @param direction 方向指令
     * @return 返回这个房间某个出口对应的<code>Room</code>对象
     */
    public Room getExit(String direction)
    {
        return exits.get(direction);
    }
}


