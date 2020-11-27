package utils;

import java.util.ArrayList;

public class TransparentStack {
    private ArrayList<Node> stack;

    public TransparentStack() {
        stack = new ArrayList<Node>();
    }

    public void push(Node n) {
        stack.add(0, n);
        System.out.println(stack);
    }

    public Node top() {
        return stack.get(0);
    }

    public Node pop() {
        Node top = top();
        stack.remove(0);
        System.out.println(stack);
        return top;
    }

    public Node second() {
        return stack.get(1);
    }

    public ArrayList<Node> toArray() {
        return stack;
    }

    public int size() {
        return stack.size();
    }
}
