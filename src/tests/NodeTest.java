package tests;

import utils.Node;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.Assert.*;

public class NodeTest {
    @Test
    public void testConstruct() {
        Node n = new Node(0, 1);
        assertEquals(n.getX(), 0);
        assertEquals(n.getY(), 1);
    }

    @Test
    public void testSet() {
        Node n = new Node();
        n.setX(0);
        n.setY(1);
        assertEquals(n.getX(), 0);
        assertEquals(n.getY(), 1);
    }

    @Test
    public void testCompare() {
        Node m = new Node(0, 3);
        Node n = new Node(3, 1);
        assertEquals(m.compareTo(n), -3);
        assertEquals(n.compareTo(m), 3);
    }

    @Test
    public void testSort() {
        ArrayList<Node> list = new ArrayList<Node>(10);
        for(int i = 0; i < 10; i++) {
            list.add(new Node(10 - i, 3));
        }

        Collections.sort(list);

        for(int i = 0; i < 9; i++) {
            assertTrue(list.get(i).getX() < list.get(i + 1).getX());
        }

    }

    @Test
    public void testAbove() {
        Node n1 = new Node(1, 1);
        Node n2 = new Node(3, 4);
        Node n3 = new Node(3, 7);
        Node n4 = new Node(6, 0);

        assertTrue(n3.above(n1, n2));
        assertFalse(n4.above(n1, n2));
        assertFalse(n1.above(n1, n2));
    }
}
