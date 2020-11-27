// 2-d point represented as a node

public class Node implements Comparable {
    private int x;
    private int y;

    Node(int x, int y) {
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

    @Override
    public int compareTo(Object comp) {
        int compare = ((Node)comp).getX();

        return this.x - compare;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
