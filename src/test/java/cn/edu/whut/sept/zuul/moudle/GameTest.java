package cn.edu.whut.sept.zuul.moudle;

import cn.edu.whut.sept.zuul.entity.Command;
import cn.edu.whut.sept.zuul.entity.Room;
import cn.edu.whut.sept.zuul.enums.CommandWord;
import junit.framework.TestCase;
import org.junit.Test;
import java.lang.reflect.Method;

public class GameTest extends TestCase {
    //生成game对象
    Game game = new Game();
    //获得Game的Class类型
    Class c = Game.class;
    //待定Method类型
    Method method;

    /**
     * 测试 processCommand 方法.
     */
    @Test
    public void testProcessCommand() {
        //反射获取
        try {
            method = c.getDeclaredMethod("processCommand",new Class[]{Command.class});
            //使私有可执行
            method.setAccessible(true);
            //传入 new Command(CommandWord.GO,"east") 对象
            method.invoke(game,new Object[]{new Command(CommandWord.GO,"east")});
        } catch (Exception e) {
            e.printStackTrace();
        }

        //该测试结果应在控制台输出：
        //你发现自己在 in a lecture theater.
        //这个地方啥都没有
        //Exits: west up

        //具体测试结果一致 即可用执行 Command 指令的执行
    }

    /**
     * 增加Cookie.
     */
    @Test
    public void testAddCookie() {
        //反射获取
        try {
            method = c.getDeclaredMethod("addCookie");
            //使私有可执行
            method.setAccessible(true);
            //空参方法，执行完以后，房间中会多增加一块饼干
            method.invoke(game);
            //查询所有房间中cookie的数量，总cookie数应该是2块，因为初始化也调用了addCookie方法
            int count=0;
            for (Room room : game.getRooms()) {
                if (room.getItem("魔法饼干")!=null) {
                    count++;
                }
            }
            //count数量应该为2
            assertEquals(2,count);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}