package cn.edu.whut.sept.zuul.service;

import cn.edu.whut.sept.zuul.entity.Command;
import cn.edu.whut.sept.zuul.entity.Item;
import cn.edu.whut.sept.zuul.entity.Player;
import cn.edu.whut.sept.zuul.enums.CommandWord;
import cn.edu.whut.sept.zuul.moudle.Game;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.HashMap;

public class CommandTableDrivenTest extends TestCase {
    Game game = new Game();
    CommandTableDriven commandTableDriven = new CommandTableDriven(game,game.getPlayer());
    @Test
    public void testGo(){
        Command command = new Command(CommandWord.GO,"east");
        //正确
        commandTableDriven.getTable().get(CommandWord.GO).apply(command);
        //理论输出:
        //你发现自己在 in a lecture theater.
        //这个地方啥都没有
        //Exits: west up

    }
    @Test
    public void testBack(){
        Command command = new Command(CommandWord.GO,"east");
        Command command1 = new Command(CommandWord.BACK,null);
        //正确
        commandTableDriven.getTable().get(CommandWord.GO).apply(command);
        commandTableDriven.getTable().get(CommandWord.BACK).apply(command1);
        //输出如下语句：
//        你发现自己在 in a lecture theater.
//                这个地方有:
//        魔法饼干	有魔力一般，能让你更耐受	0.1kg
//        Exits: west up
//        你发现自己在 outside the main entrance of the university.
//        这个地方有:
//        画	一副奇怪的画，但看不懂画的是什么	0.2kg
//        大石头	一块奇怪的大石头	60.0kg
//        Exits: east south west
    }

    @Test
    public void testHelp(){
        Command command = new Command(CommandWord.HELP,null);
        //正确
        commandTableDriven.getTable().get(CommandWord.HELP).apply(command);
        //输出如下语句：
        //Your command words are:
        //drop 丢掉东西
        //help 获取帮助
        //take 拿起东西
        //go 前往一个地方
        //back 回到上一个地方
        //eat 吃
        //quit 退出游戏
        //look 仔细观察所处地方
        //items 展示所有物品
        //info 展示玩家信息

    }

    @Test
    public void testQuit(){
        Command command = new Command(CommandWord.QUIT,null);
        boolean flag = commandTableDriven.getTable().get(CommandWord.QUIT).apply(command);
        //返回true，正确退出
        assertEquals(true, flag);
    }

    @Test
    public void testTake(){
        Command command = new Command(CommandWord.TAKE,"画");
        //正确
        commandTableDriven.getTable().get(CommandWord.TAKE).apply(command);
        //输出如下语句：
        //你成功拿起了画,你背包剩余容量为:49.8kg
    }

    @Test
    public void testDrop(){
        Command command = new Command(CommandWord.TAKE,"画");
        commandTableDriven.getTable().get(CommandWord.TAKE).apply(command);
        Command command1 = new Command(CommandWord.DROP,"画");
        //正确
        commandTableDriven.getTable().get(CommandWord.DROP).apply(command1);
        //输出如下语句：
        //你成功拿起了画,你背包剩余容量为:49.8kg
        //你成功丢掉了画,你背包剩余容量为:50.0kg
        //你身上啥都没有
    }

    @Test
    public void testLook(){
        Command command = new Command(CommandWord.LOOK,null);
        commandTableDriven.getTable().get(CommandWord.LOOK).apply(command);
        //输出如下语句：
        //你发现自己在 outside the main entrance of the university.
        //这个地方有:
        //画	一副奇怪的画，但看不懂画的是什么	0.2kg
        //大石头	一块奇怪的大石头	60.0kg
        //Exits: east south west
    }

    @Test
    public void testItems(){
        Command command = new Command(CommandWord.ITEMS,null);
        commandTableDriven.getTable().get(CommandWord.ITEMS).apply(command);
        //输出如下语句：
        //这个地方有:
        //画	一副奇怪的画，但看不懂画的是什么	0.2kg
        //大石头	一块奇怪的大石头	60.0kg
        //
        //你身上啥都没有
    }
    @Test
    public void testInfo(){
        Command command = new Command(CommandWord.INFO,null);
        commandTableDriven.getTable().get(CommandWord.INFO).apply(command);
        //输出如下语句：
        //你仔细的审查者自己：
        //姓名：TXG
        //最大耐受量:50.0kg
        //目前耐受量:0.0kg
        //剩余耐受量:50.0kg
        //目前所在：outside the main entrance of the university
        //你身上啥都没有
    }
    @Test
    public void testEat(){
        Command command = new Command(CommandWord.EAT, "魔法饼干");
        //给房间添加魔法饼干
        game.getPlayer().getCurrentRoom().addItem(new Item("魔法饼干","1",1));
        commandTableDriven.getTable().get(CommandWord.EAT).apply(command);
        //正确输入如下:
        //你吃了这个魔法饼干，感觉力大无穷，神清气爽，耐受+ 20kg你现在的容量为：70.0kg
    }

}