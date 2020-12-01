package divideAndConquer;

import utils.Node;
import utils.TransparentStack;
import utils.Randoms;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static java.lang.Float.max;

public class ParallelHull implements Callable {

    private List<Node> set = new ArrayList<Node>();
    private boolean UL = false;

    public ParallelHull(List<Node> set, boolean UL) {
        this.set = set;
        this.UL = UL;
    }

    private static ArrayList<Node> findUpperTangent(ArrayList<Node> s1, ArrayList<Node> s2) {
        boolean tangentFound = false;

        // start in middle
        int indS1 = s1.size() / 2;
        int indS2 = s2.size() / 2;

        // keep track of movement size
        int layer1 = 4;
        int layer2 = 4;

        // essentially binary search on points to find tangents
        while(!tangentFound) {
            // if left point is above, first tangent point lies to the left
            if((indS1 > 0) && s1.get(indS1 - 1).above(s1.get(indS1), s2.get(indS2))) {
                indS1 = (int) (indS1 - max(1, s1.size() / layer1));
                layer1 *= 2;
            } // if right point is above, first tangent point lies to the right (assuming there is a point to right)
            else if((s1.size() > indS1 + 2) && (s1.get(indS1 + 1).above(s1.get(indS1), s2.get(indS2)))) {
                indS1 = (int) (indS1 + max(1, s1.size() / layer1));
                layer1 *= 2;
            } // if left point is above, second tangent point lies to the left
            else if((indS2 > 0) && s2.get(indS2 - 1).above(s1.get(indS1), s2.get(indS2))) {
                indS2 = (int) (indS2 - max(1, s2.size() / layer2));
                layer2 *= 2;
            } // if right point is above, second tangent point lies to the right (assuming there is a point to right)
            else if((s2.size() > indS2 + 2) && (s2.get(indS2 + 1).above(s1.get(indS1), s2.get(indS2)))) {
                indS2 = (int) (indS2 + max(1, s2.size() / layer2));
                layer2 *= 2;
            } // if no neighbor points lie above previous computed tangent, tangent found
            else {
                tangentFound = true;
            }
        }

        // wrap nodes in list
        ArrayList<Node> tangentPoints = new ArrayList<Node>(2);
        tangentPoints.add(0, s1.get(indS1));
        tangentPoints.add(1, s2.get(indS2));

        return tangentPoints;
    }

    private static ArrayList<Node> findLowerTangent(ArrayList<Node> s1, ArrayList<Node> s2) {
        boolean tangentFound = false;

        // start in middle
        int indS1 = s1.size() / 2;
        int indS2 = s2.size() / 2;

        // keep track of movement size
        int layer1 = 4;
        int layer2 = 4;

        // essentially binary search on points to find tangents
        while(!tangentFound) {
            // if left point is below, first tangent point lies to the left
            if(!(s1.get(indS1 - 1).above(s1.get(indS1), s2.get(indS2)))) {
                indS1 = (int) (indS1 - max(1, s1.size() / layer1));
                layer1 *= 2;
            } // if right point is below,  first tangent point lies to the right (assuming there is a point to right)
            else if((s1.size() > indS1 + 2) && !(s1.get(indS1 + 1).above(s1.get(indS1), s2.get(indS2)))) {
                indS1 = (int) (indS1 + max(1, s1.size() / layer1));
                layer1 *= 2;
            } // if left point is below, second tangent point lies to the left
            else if(!(s2.get(indS2 - 1).above(s1.get(indS1), s2.get(indS2)))) {
                indS2 = (int) (indS2 - max(1, s2.size() / layer2));
                layer2 *= 2;
            } // if right point is below, second tangent point lies to the right (assuming there is a point to right)
            else if((s2.size() > indS2 + 2) && !(s2.get(indS2 + 1).above(s1.get(indS1), s2.get(indS2)))) {
                indS2 = (int) (indS2 + max(1, s2.size() / layer2));
                layer2 *= 2;
            } // if no neighbor points lie above previous computed tangent, tangent found
            else {
                tangentFound = true;
            }
        }

        // wrap nodes in list
        ArrayList<Node> tangentPoints = new ArrayList<Node>(2);
        tangentPoints.add(0, s1.get(indS1));
        tangentPoints.add(1, s2.get(indS2));

        return tangentPoints;
    }

    private static ArrayList<Node> combineUpperHull(ArrayList<Node> upperHull1, ArrayList<Node> upperHull2) {

        ArrayList<Node> tangent = findUpperTangent(upperHull1, upperHull2);
        int x1 = tangent.get(0).getX();
        int x2 = tangent.get(1).getX();

        // swap if for some reason flipped
        if(x1 > x2) {
            int temp = x1;
            x1 = x2;
            x2 = temp;
        }

        ArrayList<Node> hull = new ArrayList<>();

        for(Node n : upperHull1) {
            if(n.getX() <= x1) {
                hull.add(n);
            }
        }

        for(Node n : upperHull2) {
            if(n.getX() >= x2) {
                hull.add(n);
            }
        }

        return hull;
    }

    private static ArrayList<Node> combineLowerHull(ArrayList<Node> lowerHull1, ArrayList<Node> lowerHull2) {

        ArrayList<Node> tangent = findLowerTangent(lowerHull1, lowerHull2);
        int x1 = tangent.get(0).getX();
        int x2 = tangent.get(1).getX();

        // swap if for some reason flipped
        if(x1 > x2) {
            int temp = x1;
            x1 = x2;
            x2 = temp;
        }

        ArrayList<Node> hull = new ArrayList<>();

        for(Node n : lowerHull1) {
            if(n.getX() <= x1) {
                hull.add(n);
            }
        }

        for(Node n : lowerHull2) {
            if(n.getX() >= x2) {
                hull.add(n);
            }
        }

        return hull;
    }

    public ArrayList<Node> call() {

        ArrayList<Node> list = new ArrayList<>();

        if(UL) {
            list = lowerHull(set);
        } else {
            list = upperHull(set);
        }

        return list;
    }

    public ArrayList<Node> upperHull(List<Node> upperSet) {
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

        return hull;
    }

    public ArrayList<Node> lowerHull(List<Node> lowerSet) {
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

        return hull;
    }

    public static ArrayList<Node> combineHulls(ArrayList<Node> upper, ArrayList<Node> lower) {
        ArrayList<Node> totalHull = upper;

        for(int i = lower.size() - 2; i > 0; i--) {
            totalHull.add(lower.get(i));
        }

        return totalHull;
    }

    public static ArrayList<Node> parallelHull(List<Node> set, int numThreads) {

        Set s1 = new Set(set);
        s1.splitSet();

        ArrayList<Node> upper = (ArrayList<Node>) s1.getUpperSet();
        ArrayList<Node> lower = (ArrayList<Node>) s1.getLowerSet();

        ArrayList<Node> hull = new ArrayList<>();

        // Result list will be two lists of lists (hull -> subhulls -> upper/lower hulls)
        List<Future<ArrayList<Node>>> upperResultList = new ArrayList<>();
        List<Future<ArrayList<Node>>> lowerResultList = new ArrayList<>();

        ExecutorService threadPool = Executors.newFixedThreadPool(numThreads);

        int startU = 0;
        int startL = 0;
        int endU = s1.getUpperSet().size() / (numThreads / 2);
        int endL = s1.getLowerSet().size() / (numThreads / 2);

        for(int k = 0; k < (numThreads / 2) - 1; k++) {
            Future<ArrayList<Node>> upperHull = threadPool.submit(new ParallelHull(s1.getUpperSet().subList(startU, endU), false));
            upperResultList.add(upperHull);
            startU = endU;
            endU += s1.getUpperSet().size() / (numThreads / 2);

            Future<ArrayList<Node>> lowerHull = threadPool.submit(new ParallelHull(s1.getLowerSet().subList(startL, endL), true));
            lowerResultList.add(lowerHull);
            startL = endL;
            endL += s1.getLowerSet().size() / (numThreads / 2);
        }

        Future<ArrayList<Node>> upperHull = threadPool.submit(new ParallelHull(s1.getUpperSet().subList(startU, s1.getUpperSet().size()), false));
        upperResultList.add(upperHull);

        Future<ArrayList<Node>> lowerHull = threadPool.submit(new ParallelHull(s1.getLowerSet().subList(startL, s1.getLowerSet().size()), true));
        lowerResultList.add(lowerHull);


        ArrayList<ArrayList<Node>> upperResults = new ArrayList<>();
        ArrayList<ArrayList<Node>> lowerResults = new ArrayList<>();

        for(Future<ArrayList<Node>> future : upperResultList) {
            try {
                upperResults.add(future.get());
            } catch (Exception e) {
                System.err.println(e);
                return new ArrayList<>();
            }
        }

        for(Future<ArrayList<Node>> future : lowerResultList) {
            try {
                lowerResults.add(future.get());
            } catch (Exception e) {
                System.err.println(e);
                return new ArrayList<>();
            }
        }

        ArrayList<Node> upHull = upperResults.get(0);
        ArrayList<Node> lowHull = lowerResults.get(0);

        for(int i = 1; i < (numThreads / 2); i++) {
            upHull = combineUpperHull(upHull, upperResults.get(i));
            lowHull = combineLowerHull(lowHull, lowerResults.get(i));
        }

        hull = combineHulls(upHull, lowHull);

        threadPool.shutdown();

        return hull;
    }

    public static void main(String[] args) {
        ArrayList<Node> field = new ArrayList<Node>();

        for(int i = 0; i < 100000; i++) {
            field.add(Randoms.getRandom(-10000000, 10000000));
        }

        long startTime = System.nanoTime();
        System.out.println(parallelHull(field, 8));
        long endTime = System.nanoTime();
        long totalTime = endTime - startTime;
        System.out.println("Run time: " + totalTime / 1000);
    }
}
