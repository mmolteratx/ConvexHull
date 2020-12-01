package divideAndConquer;

import utils.Node;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Float.max;

public class ConvexHull {


    private static List<Node> findConvexHull(ArrayList<Node> list) {
        Set set = new Set(list);

        set.splitSet();

        set.upperHull();
        set.lowerHull();

        return set.combineHulls();
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
        field.add(new Node(7, 0));
        field.add(new Node(8, 9));
        field.add(new Node(8, -2));
        field.add(new Node(9, 0));

        System.out.println(findConvexHull(field));
    }
}
