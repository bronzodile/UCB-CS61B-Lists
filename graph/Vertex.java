package graph;

import list.DList;
import list.DListNode;
import list.InvalidNodeException;

public class Vertex {
    private int x;
    private int y;
    private DList edges;
    private boolean visited;
    
    public Vertex(int x, int y) {
        this.x = x;
        this.y = y;
        edges = new DList();
        visited = false;
    }
    public DList incidentEdges() {
        DList ret = new DList();
        try {
            DListNode node = (DListNode) edges.front();
            while (node.isValidNode()) {
                ret.insertBack(node.item());
                node = (DListNode) node.next();
            }
            return ret;
        } catch (InvalidNodeException e) {
            System.out.println(e);
            return ret;
        }
    }
    public boolean visited() {
        return visited;
    }
    public void visit() {
        visited = true;
    }
    public void reset() {
        visited = false;
    }
    public void addEdge(Edge e) {
        edges.insertBack(e);
    }
    public int x() {
        return x;
    }
    public int y() {
        return y;
    }
}
