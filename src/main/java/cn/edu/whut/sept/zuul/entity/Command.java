package cn.edu.whut.sept.zuul.entity;

import cn.edu.whut.sept.zuul.enums.CommandWord;

/**
 * 一个 Commmad 对象代表了用户输入的命令，它有一些方法可以很容易地判断是否是游戏的命令，还可以将命令中的第一个和第二个单词分离开来.
 *
 *@author txg
 *@version 创建时间：2021年12月31日 上午11:24:21
 */
public class Command {
    private CommandWord commandWord;
    private String secondWord;
    /*private String commandWord;
    private String secondWord;*/

    /**
     * Command的构造方法，用于生成一个游戏指令对象，由解析器<code>Parser</code>间接调用.
     *
     * @param firstWord 指令的第一个单词字段
     * @param secondWord 指令的第二个单词字段
     */
    public Command(CommandWord firstWord, String secondWord) {
        commandWord = firstWord;
        this.secondWord = secondWord;
    }

    /**
     * getter方法，用于获得第一个字段的内容，在游戏中用为行为控制.
     *
     *@return 返回第一个字段的内容
     */
    public CommandWord getCommandWord() {
        return commandWord;
    }

    /**
     * getter方法，用于获取第二个字段的内容，在游戏中用于控制方向.
     *
     *@return 返回第二个字段的内容
     */
    public String getSecondWord() {
        return secondWord;
    }

    /**
     * 判断行为控制字段<code>commandWord</code>是否存在.
     *
     *@return 行为控制字段不存在返回<code>true</code>,存在返回为<code>false</code>
     */
    public boolean isUnknown() {
        return (commandWord == null);
    }

    /**
     * 判断方向控制字段<code>secondWord</code>是否存在.
     *
     *@return 方向控制字段存在返回<code>true</code>,不存在返回<code>false</code>
     */
    public boolean hasSecondWord() {
        return (secondWord != null);
    }
}
