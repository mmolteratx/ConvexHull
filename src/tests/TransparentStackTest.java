package tests;

import utils.Node;
import utils.TransparentStack;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.Assert.*;

public class TransparentStackTest {
    @Test
    public void testPushTopSecond() {
        TransparentStack stack = new TransparentStack();
        stack.push(new Node(0, 0));
        stack.push(new Node(1, 1));

        assertEquals(stack.top(), new Node(1, 1));
        assertEquals(stack.second(), new Node(0, 0));
    }

    @Test
    public void testPushPop() {
        TransparentStack stack = new TransparentStack();
        stack.push(new Node(0, 0));
        stack.push(new Node(1, 1));

        assertEquals(stack.top(), new Node(1, 1));
        assertEquals(stack.pop(), new Node(1, 1));
        assertEquals(stack.top(), new Node(0, 0));
    }
}
