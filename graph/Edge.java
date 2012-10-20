package graph;

public class Edge {
    private int direction;
    private Vertex v;
    private Vertex u;
    
    public Edge(int dir, Vertex v, Vertex u) {
        this.direction = dir;
        this.v = v;
        this.u = u;
    }
    public Vertex opposite(Vertex i) {
        if (i == v) {
            return u;
        } else {
            return v;
        }
    }
    public int direction() {
        return direction;
    }
    public Vertex getV() {
        return v;
    }
}
