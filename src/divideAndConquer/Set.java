//TODO: issue with lower hull; need to look into
//TODO: split set into upper/lower sets
//TODO: combine upper/lower hull
//TODO: parallelize

package divideAndConquer;

import utils.*;

import java.util.ArrayList;

import static java.lang.Float.max;

public class Set {
    private ArrayList<Node> set;
    private ArrayList<Node> upperHull;
    private ArrayList<Node> lowerHull;

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

        stack.push(set.get(0));
        stack.push(set.get(1));

        // for every node, check if it is contained underneath the line segment created by connecting
        // previous node in hull and next potential node; if it is, remove from hull
        for(int i = 2; i < set.size(); i++) {
            while(stack.size() > 1 && stack.top().above(stack.second(), set.get(i))) {
                stack.pop();
            }
            stack.push(set.get(i));
        }

        // put in correct order in array list
        ArrayList<Node> hull = new ArrayList<>();
        while(stack.size() > 0) {
            hull.add(0, stack.pop());
        }

        lowerHull = hull;

        return hull;
    }

    public ArrayList<Node> findUpperTangent(Set s2) {
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

    public ArrayList<Node> findLowerTangent(Set s2) {
        boolean tangentFound = false;

        // start in middle
        int indS1 = set.size() / 2;
        int indS2 = s2.size() / 2;

        // keep track of movement size
        int layer1 = 4;
        int layer2 = 4;

        // essentially binary search on points to find tangents
        while(!tangentFound) {
            // if left point is below, first tangent point lies to the left
            if(!(set.get(indS1 - 1).above(set.get(indS1), s2.get(indS2)))) {
                System.out.println(layer1);
                indS1 = (int) (indS1 - max(1, set.size() / layer1));
                layer1 *= 2;
            } // if right point is below,  first tangent point lies to the right (assuming there is a point to right)
            else if((set.size() > indS1 + 2) && !(set.get(indS1 + 1).above(set.get(indS1), s2.get(indS2)))) {
                indS1 = (int) (indS1 + max(1, set.size() / layer1));
                layer1 *= 2;
            } // if left point is below, second tangent point lies to the left
            else if(!(s2.get(indS2 - 1).above(set.get(indS1), s2.get(indS2)))) {
                System.out.println(indS2);
                indS2 = (int) (indS2 - max(1, s2.size() / layer2));
                layer2 *= 2;
            } // if right point is below, second tangent point lies to the right (assuming there is a point to right)
            else if((s2.size() > indS2 + 2) && !(s2.get(indS1 + 1).above(set.get(indS1), s2.get(indS2)))) {
                indS2 = (int) (indS2 + max(1, s2.size() / layer2));
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

    public ArrayList<Node> combineUpperHull(Set s2) {

        ArrayList<Node> tangent = this.findUpperTangent(s2);
        int x1 = tangent.get(0).getX();
        int x2 = tangent.get(1).getX();

        // swap if for some reason flipped
        if(x1 > x2) {
            int temp = x1;
            x1 = x2;
            x2 = temp;
        }

        ArrayList<Node> hull = new ArrayList<>();

        for(Node n : upperHull) {
            if(n.getX() <= x1) {
                hull.add(n);
            }
        }

        for(Node n : s2.upperHull) {
            if(n.getX() >= x2) {
                hull.add(n);
            }
        }

        return hull;
    }

    public ArrayList<Node> combineLowerHull(Set s2) {

        ArrayList<Node> tangent = this.findLowerTangent(s2);
        int x1 = tangent.get(0).getX();
        int x2 = tangent.get(1).getX();

        // swap if for some reason flipped
        if(x1 > x2) {
            int temp = x1;
            x1 = x2;
            x2 = temp;
        }

        ArrayList<Node> hull = new ArrayList<>();

        for(Node n : lowerHull) {
            if(n.getX() <= x1) {
                hull.add(n);
            }
        }

        for(Node n : s2.lowerHull) {
            if(n.getX() >= x2) {
                hull.add(n);
            }
        }

        return hull;
    }

    public ArrayList<Node> getUpperHull() {
        return upperHull;
    }

    public ArrayList<Node> getLowerHull() {
        return lowerHull;
    }

    /*public static void main(String[] args) {
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

        System.out.println(testSet.combineUpperHull(testSet2));
        System.out.println(testSet.combineLowerHull(testSet2));
    }*/
}
