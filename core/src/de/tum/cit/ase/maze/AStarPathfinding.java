package de.tum.cit.ase.maze;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Implements the A* pathfinding algorithm to find the shortest path between two points our a game map.
 * A* is a best-first search algorithm that finds the least-cost path from a given initial node to one goal node
 * (out of one or more possible goals). It uses a combination of the actual cost from the start node to a given node
 * and the estimated cost from that given node to the goal to determine the order in which nodes are explored.
 */
public class AStarPathfinding {

    /**
     * Finds the shortest path between two points on the game map using the A* pathfinding algorithm.
     *
     * @param gameMap The game map on which the pathfinding is to be performed.
     * @param start   The starting point of the path.
     * @param end     The end point of the path.
     * @return A list of {@link Point} objects representing the shortest path from the start to the end point,
     *         including both start and end points. Returns an empty list if no path is found.
     */
    public static List<Point> findPath(GameMap gameMap, Point start, Point end) {
        PriorityQueue<Node> openSet = new PriorityQueue<>();
        Map<Point, Node> allNodes = new HashMap<>();

        //starts with initial node, add to open set and map
        Node startNode = new Node(start, null, 0, manhattanDistance(start, end));
        openSet.add(startNode);
        allNodes.put(start, startNode);

        //loop that processes nodes from the open set until this set is empty (or the target is found)
        while (!openSet.isEmpty()) {
            Node currentNode = openSet.poll();

            //if we reached the end, reconstruct the path
            if (currentNode.position.equals(end)) {
                return reconstructPath(currentNode);
            }

            //check each neighbor
            for (Point neighborPos : gameMap.getNeighbors(currentNode.position)) {
                if (!gameMap.isCellfree(neighborPos.x, neighborPos.y)) {
                    continue; //skip blocked cells
                }

                int tentativeGCost = currentNode.gCost + 1; //cost of 1 for each step
                Node neighborNode = allNodes.getOrDefault(neighborPos, new Node(neighborPos, null, Integer.MAX_VALUE, manhattanDistance(neighborPos, end)));

                //checks if moving to that neighbor is a better path than any previously found
                //if so, it updates that neighbor's gCost, recalculates its fCost, sets the current node as its parent (for path reconstruction later)
                //and adds the neighbor to the open set if it's not already there
                if (tentativeGCost < neighborNode.gCost) {
                    neighborNode.parent = currentNode;
                    neighborNode.gCost = tentativeGCost;
                    neighborNode.fCost = tentativeGCost + neighborNode.hCost;

                    if (!openSet.contains(neighborNode)) {
                        openSet.add(neighborNode);
                        allNodes.put(neighborPos, neighborNode);
                    }
                }
            }
        }
        return Collections.emptyList(); //no path found
    }

    /**
     * Reconstructs the path from the end node to the start node by backtracking through the parent nodes.
     *
     * @param endNode The end node from which to start the path reconstruction.
     * @return A list of {@link Point} objects representing the path from the start node to the end node.
     */

    private static List<Point> reconstructPath(Node endNode) {
        List<Point> path = new ArrayList<>();
        Node currentNode = endNode;
        while (currentNode != null) {
            path.add(0, currentNode.position); // Add to the front
            //reconstruct path by following the parent references from the target node back to the start node
            currentNode = currentNode.parent;
        }
        return path;
    }

    /**
     * Calculates the Manhattan distance between two points. The Manhattan distance is the sum of the absolute
     * differences of their Cartesian coordinates. It is used as a heuristic in the A* pathfinding algorithm.
     *
     * @param a The first point.
     * @param b The second point.
     * @return The Manhattan distance between the two points.
     */
    private static int manhattanDistance(Point a, Point b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }

}

