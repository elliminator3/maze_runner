package de.tum.cit.ase.maze;

import java.awt.*;

public class Node implements Comparable<Node>{
    public Point position;
    public Node parent;
    public int gCost;
    public int hCost;
    public int fCost;

    public Node(Point position, Node parent, int gCost, int hCost) {
        this.position = position;
        this.parent = parent;
        this.gCost = gCost;
        this.hCost = hCost;
        this.fCost = gCost + hCost;
    }

    @Override
    public int compareTo(Node other) {
        return Integer.compare(this.fCost, other.fCost);
    }
}
