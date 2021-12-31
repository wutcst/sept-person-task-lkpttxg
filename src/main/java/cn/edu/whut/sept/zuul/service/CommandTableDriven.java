package cn.edu.whut.sept.zuul.service;

import cn.edu.whut.sept.zuul.enums.CommandWord;
import cn.edu.whut.sept.zuul.moudle.Game;
import cn.edu.whut.sept.zuul.entity.Command;
import cn.edu.whut.sept.zuul.entity.Room;

import java.util.HashMap;
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

    /**
     * 当表驱动对象初始化后，将<code>Game</code>中的处理业务进行一一对应，其函数的注册是用的lambda表达式<br>
     * 值得提醒的是：lambda 表达式中 Function 的传入参数为 Command 对象 ,返回参数为 Boolean。这是因为，通过发现，
     * 无论是已有功能还是扩展功能，我们对其的执行的服务的输入最多只有一个 Command 类型，输出最多为 Boolean 类型判断是否
     * 退出游戏。
     * @param game 游戏主体
     */
    public CommandTableDriven(Game game){
        //初始化 game
        this.game=game;
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
            Room nextRoom = game.getCurrentRoom().getExit(direction);

            if (nextRoom == null) {
                System.out.println("There is no door!");
            }
            else {
                //在进去下一个房间前，将这个房间的信息给back,则可以通过back返回上一个房间
                nextRoom.setExit("back",game.getCurrentRoom());
                game.setCurrentRoom(nextRoom);
                System.out.println(game.getCurrentRoom().getLongDescription());
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
            System.out.println(game.getCurrentRoom().getLongDescription());return false;});

        // back 指令对应的功能，回退到上一个房间
        table.put(CommandWord.BACK,command -> {
            if(game.getCurrentRoom().hasExit("back")){
                command.setSecondWord("back");
                table.get(CommandWord.GO).apply(command);
            }else {
                System.out.println("这已经是起点...");
            }
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
