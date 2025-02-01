public class Path {
    public int sum = 0;
    public int edgesCount = 0;
    public Edge[] edges;

    public Path(int edgesCount) {
        this.edges = new Edge[edgesCount];
    }

    public void calcSum() {
        for (int i = 0; i < edgesCount; i++) {
            sum += edges[i].distance;
        }
    }
}
