package utils;// 2-d point represented as a node

public class Node implements Comparable {
    private int x;
    private int y;

    public Node() {}

    public Node(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean above(Node n1, Node n2) {
        float slope = (float)(n2.getY() - n1.getY()) / (n2.getX() - n1.getX());
        float intercept = n2.getY() - slope * n2.getX();

        float proj = slope * x + intercept;

        if(proj < y) {
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public int compareTo(Object comp) {
        int compare = ((Node)comp).getX();

        return this.x - compare;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    @Override
    public boolean equals(Object comp) {
        if(comp == null) {
            return false;
        }

        int x = ((Node)comp).getX();
        int y = ((Node)comp).getY();

        if(this.x == x && this.y == y) {
            return true;
        }
        else {
            return false;
        }
    }
}
