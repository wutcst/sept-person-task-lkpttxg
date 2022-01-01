package cn.edu.whut.sept.zuul.service;

import cn.edu.whut.sept.zuul.entity.Item;
import cn.edu.whut.sept.zuul.entity.Player;
import cn.edu.whut.sept.zuul.enums.CommandWord;
import cn.edu.whut.sept.zuul.moudle.Game;
import cn.edu.whut.sept.zuul.entity.Command;
import cn.edu.whut.sept.zuul.entity.Room;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Stack;
import java.util.function.Function;

/**
 * 该类执行表驱动的作用，从而优化if-else结构<br>
 *
 * @author txg
 * @version 2021.12.31
 */
public class CommandTableDriven {
    /**
     * 每个指令对应的函数存储到 map 中,形成表驱动结构
     */
    private final HashMap<CommandWord, Function<Command,Boolean>> table;
    private final Game game;
    private final Player player;

    /**
     * 当表驱动对象初始化后，将<code>Game</code>中的处理业务进行一一对应，其函数的注册是用的lambda表达式<br>
     * 值得提醒的是：lambda 表达式中 Function 的传入参数为 Command 对象 ,返回参数为 Boolean。这是因为，通过发现，
     * 无论是已有功能还是扩展功能，我们对其的执行的服务的输入最多只有一个 Command 类型，输出最多为 Boolean 类型判断是否
     * 退出游戏。
     * @param game 游戏主体
     */
    public CommandTableDriven(Game game,Player player){
        //初始化 game
        this.game=game;
        //初始化 player
        this.player=player;
        //初始化驱动表
        table=new HashMap<>();

        // go 指令所对应的功能，进入下一个房间
        table.put(CommandWord.GO,(command)->{
            if(!command.hasSecondWord()) {
                // if there is no second word, we don't know where to go...
                System.out.println("Go where?");
                return false;
            }

            String direction = command.getSecondWord();

            // Try to leave current room.
            Room nextRoom = player.getCurrentRoom().getExit(direction);

            if (nextRoom == null) {
                System.out.println("There is no door!");
            }
            else {
                /*//在进去下一个房间前，将这个房间的信息给back,则可以通过back返回上一个房间
                nextRoom.setExit("back",game.getCurrentRoom());
                 */
                //检验下一个房间是否有秘密传送点
                if(nextRoom.isTransferPoint()){
                    //输出nextRoom的描述
                    System.out.println(nextRoom.getShortDescription());
                    ArrayList<Room> rooms = game.getRooms();
                    //通过随机数实现随机传输到另一个房间
                    int i =(int)(Math.random()*rooms.size());
                    nextRoom = rooms.get(i);
                    //保证房间不相同
                    while(nextRoom == player.getCurrentRoom()){
                        i =(int)(Math.random()*rooms.size());
                        nextRoom = rooms.get(i);
                    }

                }
                //进入了另一个房间，将上一个房间入栈
                player.getRoom_history().push(player.getCurrentRoom());
                player.setCurrentRoom(nextRoom);
                System.out.println(player.getCurrentRoom().getLongDescription());
            }
            return false;
        });

        // help 指令所对应的功能，打印所有的指令信息
        table.put(CommandWord.HELP,(command)->{
            System.out.println("You are lost. You are alone. You wander");
            System.out.println("around at the university.");
            System.out.println();
            System.out.println("Your command words are:");
            game.getParser().showCommands();
            return false;});

        // quit 指令所对应的功能，退出游戏
        table.put(CommandWord.QUIT,command -> {
            if(command.hasSecondWord()) {
                System.out.println("Quit what?");
                return false;
            }
            else {
                return true;  // signal that we want to quit
            }
        });

        // look 指令对应的功能，查看房间信息
        table.put(CommandWord.LOOK,command -> {
            System.out.println(player.getCurrentRoom().getLongDescription());return false;});

        /* back 指令对应的功能，回退到上一个房间
        如果栈顶为空，则表示已经在起点；
        否则，pop 出栈顶作为当前房间，输出描述信息
         */
        table.put(CommandWord.BACK,command -> {
            Stack<Room> stack = player.getRoom_history();
            //对栈顶元素进行判断
            if(stack.size()!=0){
                player.setCurrentRoom(stack.pop());
                System.out.println(player.getCurrentRoom().getLongDescription());
            }else{
                System.out.println("这已经是起点了...");
            }
            return false;
        });

        //玩家的拿东西指令
        table.put(CommandWord.TAKE,command -> {
            if(!command.hasSecondWord()) {
                // if there is no second word, we don't know take what...
                System.out.println("Take what?");
                return false;
            }
            //获取物品名称
            String name = command.getSecondWord();
            //查询是否有这个物品在房间中
            Item item = player.getCurrentRoom().getItem(name);
            if(item==null){
                System.out.println("这个房间没有这个东西");
            }else {
                //player拿东西
                //先判断是否拿的动
                if(player.addItem(item)){
                    player.getCurrentRoom().getItems().remove(item);
                    System.out.println("你成功拿起了"+item.getName()+",你背包剩余容量为:"+(player.getMaxBearWeight()-player.getNowWeight())+"kg");
                }else {
                    System.out.println("你拿不动:"+item.getName()+" "+item.getWeight()+"kg"+" ，"+"而你容量只有:"+(player.getMaxBearWeight()-player.getNowWeight())+"kg");
                }
            }
            return false;
        });

        //玩家的丢东西指令
        table.put(CommandWord.DROP,command -> {
            if(!command.hasSecondWord()) {
                // if there is no second word, we don't know take what...
                System.out.println("Drop what?");
                return false;
            }
            //获取物品名称
            String name = command.getSecondWord();
            //查询这个物品是否在玩家身上
            Item item = player.getItem(name);
            if(item==null){
                System.out.println("你身上没那个东西！！！");
            }else {
                player.dropItem(item);
                System.out.println("你成功丢掉了"+item.getName()+",你背包剩余容量为:"+(player.getMaxBearWeight()-player.getNowWeight())+"kg");
                System.out.println(player.showItems());
                //丢掉的物品放入当前房间中
                player.getCurrentRoom().addItem(item);
            }
            return false;
        });

        //展示所有物品信息
        table.put(CommandWord.ITEMS,command -> {
            System.out.println(player.getCurrentRoom().getItemsDescription());
            System.out.println(player.showItems());
            return false;
        });

        //玩家执行吃magic cookie
        table.put(CommandWord.EAT,command -> {
            if(!command.hasSecondWord()) {
                // if there is no second word, we don't know eat what...
                System.out.println("EAT what?");
                return false;
            }
            //获取物品名称
            String name = command.getSecondWord();
            //查询是否有这个物品在房间中
            Item item = player.getCurrentRoom().getItem(name);
            if(item==null){
                System.out.println("这个房间没有这个东西");
            }else {
                if(item.getName().equals("魔法饼干")){
                    //玩家增加耐受容量+20kg
                    player.setMaxBearWeight(player.getMaxBearWeight()+20);
                    System.out.println("你吃了这个魔法饼干，感觉力大无穷，神清气爽，耐受+20kg"+"你现在的容量为："+player.getMaxBearWeight()+"kg");
                    //把魔法饼干从房间移除
                    player.getCurrentRoom().getItems().remove(item);
                }else {
                    System.out.println("这个东西不能吃！！！");
                }
            }
            return false;
        });
        //展示玩家具体信息
        table.put(CommandWord.INFO,command -> {
            System.out.println("你仔细的审查者自己：");
            System.out.println(player.getSelfLongDescription());
           return false;
        });
    }

    /**
     * 向<code>Game</code>对象中返回 map 的表驱动集合
     * @return 返回表驱动集合
     */
    public HashMap<CommandWord, Function<Command,Boolean>> getTable() {
        return table;
    }

}
