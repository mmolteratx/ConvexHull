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

        // for every node, check if it is contained underneath the line segment created by connecting
        // previous node in hull and next potential node; if it is, remove from hull
        for(int i = 2; i < set.size(); i++) {
            while(stack.size() > 1 && !(stack.top().above(stack.second(), set.get(i)))) {
                stack.pop();
            }
            stack.push(set.get(i));
        }

        return stack.toArray();
    }

    public ArrayList<Node> findTangent(Set s2) {
        boolean tangentFound = false;

        // start in middle
        int indS1 = set.size() / 2;
        int indS2 = s2.size() / 2;

        // keep track of movement size
        int layer1 = 4;
        int layer2 = 4;

        // essentially binary search on points to find tangents
        while(!tangentFound) {
            // if left point is above, first tangent point lies to the left
            if(set.get(indS1 - 1).above(set.get(indS1), s2.get(indS2))) {
                indS1 = indS1 - (set.size() / layer1);
                layer1 *= 2;
            } // if right point is above, first tangent point lies to the right (assuming there is a point to right)
            else if((set.size() > indS1 + 2) && (set.get(indS1 + 1).above(set.get(indS1), s2.get(indS2)))) {
                indS1 = indS1 + set.size() / layer1;
                layer1 *= 2;
            } // if left point is above, second tangent point lies to the left
            else if(s2.get(indS2 - 1).above(set.get(indS1), s2.get(indS2))) {
                indS2 = indS2 - s2.size() / layer2;
                layer2 *= 2;
            } // if right point is above, second tangent point lies to the right (assuming there is a point to right)
            else if((s2.size() > indS2 + 2) && (s2.get(indS1 + 1).above(set.get(indS1), s2.get(indS2)))) {
                indS2 = indS2 + s2.size() / layer2;
                layer2 *= 2;
            } // if no neighbor points lie above previous computed tangent, tangent found
            else {
                tangentFound = true;
            }
        }

        // wrap nodes in list
        ArrayList<Node> tangentPoints = new ArrayList<Node>(2);
        tangentPoints.add(0, set.get(indS1));
        tangentPoints.add(1, s2.get(indS2));

        return tangentPoints;
    }

    public int size() {
        return set.size();
    }

    public Node get(int index) {
        return set.get(index);
    }

    /*
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

        ArrayList<Node> field2 = new ArrayList<Node>();
        field2.add(new Node(7, 0));
        field2.add(new Node(8, 9));
        field2.add(new Node(9, 0));

        Set testSet2 = new Set(field2);

        System.out.println(testSet2.upperHull());

        System.out.println(testSet.findTangent(testSet2));
    } */
}
