package cn.edu.whut.sept.zuul.enums;

/**
 * 包含了游戏中所有的命令关键字
 * @author txg
 * @version 2021.12.21
 */
public enum CommandWord {
    //将命令字符串与枚举类型关联起来
    GO("go"),QUIT("quit"),HELP("help"),UNKNOWN("?");

    private String commandString;

    /**
     * 拥有命令字符串属性的的枚举类型
     * @param commandString 命令字符串
     */
    CommandWord(String commandString){
        this.commandString=commandString;
    }

    /**
     * 重写 toString() 方法，返回枚举类型代表的命令字符串
     * @return 返回对应的命令字符串
     */
    public String toString(){
        return commandString;
    }
}
