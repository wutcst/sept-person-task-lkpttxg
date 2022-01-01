package cn.edu.whut.sept.zuul.enums;

/**
 * 包含了游戏中所有的命令关键字.
 *
 * @author txg
 * @version 2021.12.21
 */
public enum CommandWord {
    //将命令字符串与枚举类型关联起来
    GO("go", "前往一个地方"), QUIT("quit", "退出游戏"),
    HELP("help", "获取帮助"), LOOK("look", "仔细观察所处地方"),
    BACK("back", "回到上一个地方"), UNKNOWN("?", ""),
    TAKE("take", "拿起东西"), DROP("drop", "丢掉东西"),
    ITEMS("items", "展示所有物品"), EAT("eat", "吃"),
    INFO("info", "展示玩家信息");

    private String commandString;
    private String description;

    /**
     * 拥有命令字符串属性的的枚举类型.
     *
     * @param commandString 命令字符串
     */
    CommandWord(String commandString, String description) {
        this.commandString = commandString;
        this.description = description;
    }

    /**
     * 重写 toString() 方法，返回枚举类型代表的命令字符串.
     *
     *@return 返回对应的命令字符串
     */
    @Override
    public String toString() {
        return commandString;
    }

    public String getDescription() {
        return description;
    }
}
