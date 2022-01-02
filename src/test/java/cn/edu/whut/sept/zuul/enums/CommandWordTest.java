package cn.edu.whut.sept.zuul.enums;

import junit.framework.TestCase;
import org.junit.Test;

public class CommandWordTest extends TestCase {
    CommandWord commandWord = CommandWord.GO;
    @Test
    public void testTestToString() {
        //应该为“go”，不为“go”则判断为false
        assertEquals( "go", commandWord.toString());

    }
    @Test
    public void testGetDescription() {
        //应该为“前往一个地方”，不为“前往一个地方”则判断false
        assertEquals("前往一个地方", commandWord.getDescription());
    }

}