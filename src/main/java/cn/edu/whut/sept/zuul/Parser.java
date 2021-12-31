package cn.edu.whut.sept.zuul;

import java.util.Scanner;
/**
 * 该类为语法分析器，从终端读入一行输入，将其解析为命令，并据此创建 Command 类对象。
 * @author txg
 * @version 创建时间：2021年12月31日 上午11:24:21
 */
public class Parser
{
    private CommandWords commands;
    private Scanner reader;

    /**
     * 构造方法，同时初始化指令组对象和Scanner输入对象
     */
    public Parser()
    {
        commands = new CommandWords();
        reader = new Scanner(System.in);
    }

    /**
     * 获取控制台用户的输入命令
     * @return 通过判断用户命令的正确性，返回生成的<code>Command</code>对象
     */
    public Command getCommand()
    {
        String inputLine;
        String word1 = null;
        String word2 = null;

        System.out.print("> ");
        //获取用户输入
        inputLine = reader.nextLine();
        //解析用户输入
        Scanner tokenizer = new Scanner(inputLine);
        if(tokenizer.hasNext()) {
            word1 = tokenizer.next();
            if(tokenizer.hasNext()) {
                word2 = tokenizer.next();
            }
        }
        //判断是否有行为控制指令，执行不同的返回操作
        if(commands.isCommand(word1)) {
            return new Command(word1, word2);
        }
        else {
            return new Command(null, word2);
        }
    }

    /**
     * 调用<code>Command</code>的<code>showAll()</code>方法，展示所有的行为控制指令
     */
    public void showCommands()
    {
        commands.showAll();
    }
}
