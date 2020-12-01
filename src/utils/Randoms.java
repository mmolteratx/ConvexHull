package utils;

public class Randoms {
    public static Node getRandom(int min, int max) {
        Node newNode = new Node();

        newNode.setX((int) ((Math.random() * (max - min)) + min));
        newNode.setY((int) ((Math.random() * (max - min)) + min));

        return newNode;
    }

    public static Node getRandomDisk(int radius) {
        Node newNode = new Node(radius, radius);

        while(!withinDisk(newNode, radius)) {
            newNode.setX((int) ((Math.random() * (radius)) - radius));
            newNode.setY((int) ((Math.random() * (radius)) - radius));
        }

        return newNode;
    }

    private static boolean withinDisk(Node n, int radius) {
        if(Math.sqrt(n.getX() ^ 2 + n.getY() ^ 2) > radius) {
            return false;
        } else {
            return true;
        }
    }
}
