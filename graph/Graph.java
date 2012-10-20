package graph;

import list.DList;
import list.DListNode;
import list.InvalidNodeException;

public class Graph {
    DList vertices;
    int edgeCount;
    int neutralEdges;
    
    public Graph() {
        vertices = new DList();
        edgeCount = 0;
        neutralEdges = 0;
    }
    public void insertVertex(Vertex v) {
        vertices.insertBack(v);
    }
    public void insertEdge(Edge e) {
        Vertex v = e.getV();
        Vertex u = e.opposite(v);
        v.addEdge(e);
        u.addEdge(e);
        edgeCount++;
        if (v.x() == 0 && u.x() == 0) neutralEdges++;
        if (v.x() == 0 && u.x() == 7) neutralEdges++;
        if (v.y() == 0 && u.y() == 0) neutralEdges++;
        if (v.y() == 0 && u.y() == 7) neutralEdges++;
    }
    public Vertex getVertex(int x, int y) {
        Vertex v = new Vertex(0,0);
        try {
            DListNode n = (DListNode) vertices.front();
            while (true) {
                v = (Vertex) n.item();
                if (v.x() == x && v.y() == y) {
                    return v;
                }
                n = (DListNode) n.next();
            }
        } catch (InvalidNodeException e) {
            System.out.println(e);
            return v;
        }
    }
    public void reset() {
        try {
            DListNode n = (DListNode) vertices.front();
            while(n.isValidNode()) {
                ((Vertex)n.item()).reset();
                n = (DListNode) n.next();
            }
        } catch (InvalidNodeException e) {
            System.out.println(e);
        }
    }
    public void debugPrint() {
        System.out.println("=== The graph state is... : ===");
        try {
            DListNode n = (DListNode) vertices.front();
            while (n.isValidNode()){
                Vertex vertex = (Vertex) n.item();
                System.out.print("Vertex at " + vertex.x() + " " + vertex.y());
                DList edges = vertex.incidentEdges();
                DListNode n2 = (DListNode) edges.front();
                while (n2.isValidNode()) {
                    Edge edge = (Edge) n2.item();
                    System.out.print(" edge " + edge.direction());
                    n2 = (DListNode) n2.next();
                }
                n = (DListNode) n.next();
                System.out.println();
            }
        } catch (InvalidNodeException e) {
            System.out.println(e);
        }
    }
    public int getEdgeCount() {
        return edgeCount - neutralEdges;
    }
}
