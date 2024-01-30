package de.tum.cit.ase.maze;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.ArrayList;

public class AStarPathfinding {
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

    //once the target node is reached, reconstruct the path from the target to the start
    //by following the parent references from the target node back to the start node
    private static List<Point> reconstructPath(Node endNode) {
        List<Point> path = new ArrayList<>();
        Node currentNode = endNode;
        while (currentNode != null) {
            path.add(0, currentNode.position); // Add to the front
            currentNode = currentNode.parent;
        }
        return path;
    }

    private static int manhattanDistance(Point a, Point b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }
}

