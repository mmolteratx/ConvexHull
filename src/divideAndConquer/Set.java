package divideAndConquer;

import utils.*;

import java.util.ArrayList;
import java.util.List;

public class Set {
    private List<Node> set;
    private List<Node> upperSet;
    private List<Node> lowerSet;
    private ArrayList<Node> upperHull;
    private ArrayList<Node> lowerHull;
    private List<Node> totalHull;

    public Set() {
        set = new ArrayList<Node>();
        splitSet();
    }

    public Set(List<Node> set) {
        this.set = set;
        splitSet();
    }

    public void splitSet() {
        Node end1 = set.get(0);
        Node end2 = set.get(set.size() - 1);

        upperSet = new ArrayList<Node>();
        lowerSet = new ArrayList<Node>();

        upperSet.add(end1);

        for(Node n : set) {
            if(n.above(end1, end2)) {
                upperSet.add(n);
            } else {
                lowerSet.add(n);
            }
        }

        upperSet.add(end2);
    }

    public ArrayList<Node> upperHull() {
        TransparentStack stack = new TransparentStack();

        stack.push(upperSet.get(0));
        stack.push(upperSet.get(1));

        // for every node, check if it is contained underneath the line segment created by connecting
        // previous node in hull and next potential node; if it is, remove from hull
        for(int i = 2; i < upperSet.size(); i++) {
            while(stack.size() > 1 && !(stack.top().above(stack.second(), upperSet.get(i)))) {
                stack.pop();
            }
            stack.push(upperSet.get(i));
        }

        // put in correct order in array list
        ArrayList<Node> hull = new ArrayList<>();
        while(stack.size() > 0) {
            hull.add(0, stack.pop());
        }

        upperHull = hull;

        return hull;
    }

    public ArrayList<Node> lowerHull() {
        TransparentStack stack = new TransparentStack();

        stack.push(lowerSet.get(0));
        stack.push(lowerSet.get(1));

        // for every node, check if it is contained underneath the line segment created by connecting
        // previous node in hull and next potential node; if it is, remove from hull
        for(int i = 2; i < lowerSet.size(); i++) {
            while(stack.size() > 1 && stack.top().above(stack.second(), lowerSet.get(i))) {
                stack.pop();
            }
            stack.push(lowerSet.get(i));
        }

        // put in correct order in array list
        ArrayList<Node> hull = new ArrayList<>();
        while(stack.size() > 0) {
            hull.add(0, stack.pop());
        }

        lowerHull = hull;

        return hull;
    }

    public List<Node> combineHulls() {
        totalHull = upperHull();

        for(int i = lowerHull.size() - 2; i > 0; i--) {
            totalHull.add(lowerHull.get(i));
        }

        return totalHull;
    }

    public ArrayList<Node> getUpperHull() {
        return upperHull;
    }

    public ArrayList<Node> getLowerHull() {
        return lowerHull;
    }

    public List<Node> getUpperSet() {
        return upperSet;
    }

    public List<Node> getLowerSet() {
        return lowerSet;
    }

    public static void main(String[] args) {
        ArrayList<Node> field = new ArrayList<Node>();
        field.add(new Node(0, 0));
        field.add(new Node(1, 2));
        field.add(new Node(1, 4));
        field.add(new Node(1, -4));
        field.add(new Node(4, 4));
        field.add(new Node(4, 2));
        field.add(new Node(5, 0));

        Set testSet = new Set(field);
        System.out.println(testSet.upperHull());
        System.out.println(testSet.lowerHull());

        ArrayList<Node> field2 = new ArrayList<Node>();
        field2.add(new Node(7, 0));
        field2.add(new Node(8, 9));
        field2.add(new Node(8, -2));
        field2.add(new Node(9, 0));

        Set testSet2 = new Set(field2);

        System.out.println(testSet2.upperHull());
        System.out.println(testSet2.lowerHull());

        System.out.println(testSet.combineHulls());
        System.out.println(testSet2.combineHulls());
    }
}
