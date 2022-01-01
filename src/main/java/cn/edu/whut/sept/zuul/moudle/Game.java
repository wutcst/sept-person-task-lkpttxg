/**
 * 该类是“World-of-Zuul”应用程序的主类。
 * 《World of Zuul》是一款简单的文本冒险游戏。用户可以在一些房间组成的迷宫中探险。
 * 你们可以通过扩展该游戏的功能使它更有趣!.
 *
 * 如果想开始执行这个游戏，用户需要创建Game类的一个实例并调用“play”方法。
 *
 * Game类的实例将创建并初始化所有其他类:它创建所有房间，并将它们连接成迷宫；它创建解析器
 * 接收用户输入，并将用户输入转换成命令后开始运行游戏。
 *
 * @author  Michael Kölling and David J. Barnes
 * @version 1.0
 */
package cn.edu.whut.sept.zuul.moudle;

import cn.edu.whut.sept.zuul.entity.Item;
import cn.edu.whut.sept.zuul.entity.Player;
import cn.edu.whut.sept.zuul.service.CommandTableDriven;
import cn.edu.whut.sept.zuul.entity.Command;
import cn.edu.whut.sept.zuul.entity.Room;
import cn.edu.whut.sept.zuul.enums.CommandWord;
import cn.edu.whut.sept.zuul.controller.Parser;

import java.util.ArrayList;
import java.util.Stack;

/**
 * 该类是游戏的主体。它启动游戏，然后进入一个不断读取和执行输入的命令的循环.<br>
 * 它也包括执行每一个用户命令的代码。
 * @author txg
 * @version 创建时间：2021年12月31日 上午11:24:21
 */
public class Game
{
    /**
     * 游戏指令解析器，<code>Game</code>创建后直接生成。<br>
     * 通过指令解析器，我们获得用户输入的指令。
     * @see Parser#getCommand()
     */
    private Parser parser;

    /**
     * 目前只有一个玩家，所有并没有用集合存储
     */
    private Player player;

    /**
     * 表驱动类的实例对象，方便服务进行
     */
    private CommandTableDriven commandTableDriven;

    private ArrayList<Room> rooms;

    /**
     * 创建游戏并初始化内部数据和解析器
     */
    public Game() {
        //初始化对所有房间的存储数组列表
        rooms=new ArrayList<>();
        //创建玩家对象(目前只有一个)
        createPlayers();
        //创建所有房间
        createRooms();
        //初始化解析器
        parser = new Parser();
        //表驱动
        commandTableDriven=new CommandTableDriven(this,player);
    }

    /**
     * 创建所有房间对象并连接其出口用以构建迷宫.
     */
    private void createRooms()
    {
        Room outside, theater, pub, lab, office,cave;

        // create the rooms
        outside = new Room("outside the main entrance of the university");
        theater = new Room("in a lecture theater");
        pub = new Room("in the campus pub");
        lab = new Room("in a computing lab");
        office = new Room("in the computing admin office");
        //传输房间
        cave  = new Room("一个神秘的洞穴。你觉得很诡异，这时一股强光突然出现，你看不到任何东西，只知道自己好像身体不受支配，随意飘动...\n过来好一会，你才突然发现，你所处的早已不是洞穴，而是另一个地方");
        //将房间存入数组
        rooms.add(outside);
        rooms.add(theater);
        rooms.add(pub);
        rooms.add(lab);
        rooms.add(office);
        // initialise room exits
        outside.setExit("east", theater);
        outside.setExit("south", lab);
        outside.setExit("west", pub);

        theater.setExit("west", outside);

        pub.setExit("east", outside);

        lab.setExit("north", outside);
        lab.setExit("east", office);

        office.setExit("west", lab);
        office.setExit("south",cave);
        cave.setTransferPoint(true);
        //创建物品
        outside.addItem(new Item("大石头","一块奇怪的大石头",60));
        outside.addItem(new Item("画","一副奇怪的画，但看不懂画的是什么",0.2f));
        //随机放入一个魔法饼干到房间中
        addCookie();
        // start game outside
        player.setCurrentRoom(outside);
    }

    private void createPlayers(){
        this.player=new Player("TXG",50,0);
    }

    /**
     *  游戏主控循环，直到用户输入退出命令后结束整个程序.
     */
    public void play()
    {
        printWelcome();

        // Enter the main command loop.  Here we repeatedly read commands and
        // execute them until the game is over.

        boolean finished = false;
        while (! finished) {
            Command command = parser.getCommand();
            finished = processCommand(command);
        }
        System.out.println("Thank you for playing.  Good bye.");
    }

    /**
     * 向用户输出欢迎信息.
     */
    private void printWelcome()
    {
        System.out.println();
        System.out.println("Welcome to the World of Zuul!");
        System.out.println("World of Zuul is a new, incredibly boring adventure game.");
        System.out.println("Type 'help' if you need help.");
        System.out.println();
        System.out.println(player.getCurrentRoom().getLongDescription());
    }

    /**
     * 执行用户输入的游戏指令.
     * @param command 待处理的游戏指令，由解析器从用户输入内容生成.
     * @return 如果执行的是游戏结束指令，则返回true，否则返回false.
     */
    private boolean processCommand(Command command) {
        boolean wantToQuit = false;

       if(command.isUnknown()) {
            System.out.println("I don't know what you mean...");
            return false;
        }
        //获取命令枚举类型
        CommandWord commandWord = command.getCommandWord();
       /* if (commandWord==CommandWord.HELP) {
            printHelp();
        }
        else if (commandWord==CommandWord.GO) {
            goRoom(command);
        }
        else if (commandWord==CommandWord.QUIT) {
            wantToQuit = quit(command);
        }*/
        //获取驱动表，执行该命令对应的函数，返回值赋予 wantToQuit
        wantToQuit=commandTableDriven.getTable().get(commandWord).apply(command);
        // else command not recognised.
        return wantToQuit;

    }

    /**
     * 随机给一个非传送房间加入魔法饼干
     */
    private void addCookie(){
        //随机获得一个非传送房间的房间
        int i=(int)(Math.random()*rooms.size());
        //给这个房间添加魔法饼干
        rooms.get(i).addItem(new Item("魔法饼干","有魔力一般，能让你更耐受",0.1f));
    }

    public Parser getParser() {
        return parser;
    }


    /**
     * getter 方法，得到房间列表
     * @return 返回存储房间的数组列表
     */
    public ArrayList<Room> getRooms() {
        return rooms;
    }

    public Player getPlayer() {
        return player;
    }
}