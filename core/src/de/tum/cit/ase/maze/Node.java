package de.tum.cit.ase.maze;

import java.awt.*;

/**
 * Represents a node in a pathfinding context, used in our A* pathfinding algorithm.
 * Each node corresponds to a specific position in a grid or map and contains information
 * about the cost of reaching this node (gCost), the estimated cost to reach the goal from
 * this node (hCost), and the parent node from which this node was reached.
 */
public class Node implements Comparable<Node>{

    public Point position; // The position of this node in the grid or map
    public Node parent; // The parent node from which this node was reached
    public int gCost; // The cost of reaching this node from the start node
    public int hCost; // The heuristic estimated cost from this node to the goal node
    public int fCost; // The total cost of this node, calculated as gCost + hCost

    /**
     * Constructs a new Node with the specified position, parent node, and costs.
     *
     * @param position The position of this node in the grid or map.
     * @param parent   The parent node from which this node was reached. Can be null if this node is the start node.
     * @param gCost    The cost of reaching this node from the start node.
     * @param hCost    The heuristic estimated cost from this node to the goal node.
     */
    public Node(Point position, Node parent, int gCost, int hCost) {
        this.position = position;
        this.parent = parent;
        this.gCost = gCost;
        this.hCost = hCost;
        this.fCost = gCost + hCost;
    }

    /**
     * Compares this node with another node based on their total costs (fCost).
     * This method is used to order nodes, typically in a priority queue, from lowest
     * total cost to highest to determine the next node to process in pathfinding algorithms.
     *
     * @param other The other node to compare against.
     * @return A negative integer, zero, or a positive integer as this node's total cost
     *         is less than, equal to, or greater than the specified node's total cost, respectively.
     */
    @Override
    public int compareTo(Node other) {
        return Integer.compare(this.fCost, other.fCost);
    }
}
