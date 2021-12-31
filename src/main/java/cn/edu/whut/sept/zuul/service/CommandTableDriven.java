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
    private HashMap<CommandWord, Function<Command,Boolean>> table;
    private Game game;

    /**
     * 当表驱动对象初始化后，将<code>Game</code>中的处理业务进行一一对应，其函数的注册是用的lambda表达式<br>
     * 值得提醒的是：lambda 表达式中 Function 的传入参数为 Command 对象 ,返回参数为 Boolean。这是因为，通过发现，
     * 无论是已有功能还是扩展功能，我们对其的执行的服务的输入最多只有一个 Command 类型，输出最多为 Boolean 类型判断是否
     * 退出游戏。
     * @param game 游戏主体
     */
    public CommandTableDriven(Game game){
        this.game=game;
        table=new HashMap<>();
        //对每个命令，注册对应的函数
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
                game.setCurrentRoom(nextRoom);
                System.out.println(game.getCurrentRoom().getLongDescription());
            }
            return false;
        });
        table.put(CommandWord.HELP,(command)->{
            System.out.println("You are lost. You are alone. You wander");
            System.out.println("around at the university.");
            System.out.println();
            System.out.println("Your command words are:");
            game.getParser().showCommands();
            return false;});
        table.put(CommandWord.QUIT,command -> {
            if(command.hasSecondWord()) {
                System.out.println("Quit what?");
                return false;
            }
            else {
                return true;  // signal that we want to quit
            }
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
