package divideAndConquer;

import utils.*;

import java.util.ArrayList;

public class Set {
    private ArrayList<Node> set;

    public Set() {
        set = new ArrayList<Node>();
    }

    public Set(ArrayList<Node> set) {
        this.set = set;
    }

    public ArrayList<Node> upperHull() {
        TransparentStack stack = new TransparentStack();

        stack.push(set.get(0));
        stack.push(set.get(1));

        for(int i = 2; i < set.size(); i++) {
            while(stack.size() > 1 && !(stack.top().above(stack.second(), set.get(i)))) {
                stack.pop();
            }
            stack.push(set.get(i));
        }

        return stack.toArray();
    }

    public static void main(String[] args) {
        ArrayList<Node> field = new ArrayList<Node>();
        field.add(new Node(0, 0));
        field.add(new Node(1, 2));
        field.add(new Node(1, 4));
        field.add(new Node(4, 4));
        field.add(new Node(4, 2));
        field.add(new Node(5, 0));

        Set testSet = new Set(field);
        System.out.println(testSet.upperHull());
    }
}
