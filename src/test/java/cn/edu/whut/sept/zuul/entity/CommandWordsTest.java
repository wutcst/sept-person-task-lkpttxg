package cn.edu.whut.sept.zuul.entity;

import cn.edu.whut.sept.zuul.enums.CommandWord;
import junit.framework.TestCase;
import org.junit.Test;

public class CommandWordsTest extends TestCase {
    CommandWords commandWords = new CommandWords();
    @Test
    public void testIsCommand() {
        //输入help,判断help是否为可用的行为指令,help是可用指令,结果为true
        assertEquals(true,commandWords.isCommand("help"));
        //输入goo,为无用的行为指令，结果为false
        assertEquals(false,commandWords.isCommand("goo"));
    }
    @Test
    public void testGetCommandWord() {
        //该方法的返回值应该是 CommandWord.BACK
        assertEquals(CommandWord.BACK, commandWords.getCommandWord("back"));
    }
    @Test
    public void testShowAll() {
        //控制台输出正确，方法正确
        commandWords.showAll();
    }
}