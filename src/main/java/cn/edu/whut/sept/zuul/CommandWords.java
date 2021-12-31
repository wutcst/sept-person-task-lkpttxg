package cn.edu.whut.sept.zuul;

/**
 * 该类用于存储游戏中可用的行为指令（go\quit\help。。。）
 *@author txg
 *@version 创建时间：2021年12月31日 上午11:24:21
 */
public class CommandWords
{
    private static final String[] validCommands = {
            "go", "quit", "help"
    };

    /**
     * 构造方法，生成指令组对象
     */
    public CommandWords()
    {
        // nothing to do at the moment...
    }

    /**
     * 判断某个输入的指令是否为游戏中可用的行为指令
     * @param aString 传入的指令
     * @return 如果是游戏行为指令，返回<code>true</code>,否则返回<code>false</code>
     */
    public boolean isCommand(String aString)
    {
        //循环检测
        for(int i = 0; i < validCommands.length; i++) {
            if(validCommands[i].equals(aString))
                return true;
        }
        return false;
    }

    /**
     * 控制台打印所有的行为指令
     */
    public void showAll()
    {
        for(String command: validCommands) {
            System.out.print(command + "  ");
        }
        System.out.println();
    }
}
