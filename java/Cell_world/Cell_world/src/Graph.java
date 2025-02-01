import java.awt.*;

public class Graph extends Canvas implements Runnable {
    // Входные параметры алгоритма
    public int graphWidth = 5;
    public int graphHeight = 5;
    final int antNum = 7;
    public int circlesNum = 2;
    public int randomIndex = 4;
    final int repaintDelay = 300;
    // -----------
    public Node[] nodeArr;
    public Edge[] edgeArr;
    public int edgeCount = 0;
    public int nodeCount = 0;
    public Node homeNode = null;
    public Node foodNode = null;
    final int windowWidth;
    final int windowHeight;
    public Ant[] antArr;
    public Path[] pathArr;
    public int pathCount;
    public Path bestPath = null;
    private boolean gameOver = false;

    public Graph(int windowWidth, int windowHeight) {
        nodeArr = new Node[graphWidth * graphHeight];
        edgeArr = new Edge[graphWidth * graphHeight * 2];
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
    }
    public void createGraph() {
        for (int x = 50, i = 0; i < graphHeight; x += 50, i++) {
            for (int y = 50, j = 0; j < graphWidth; y += 50, j++) {
                Node currentNode = new Node(i, j, x, y);
                if (i == 0 && j == 0) homeNode = currentNode;
                if (i == graphHeight - 1 && j == graphWidth - 1) foodNode = currentNode;
                nodeArr[nodeCount] = currentNode;
                // грань к предыдущему узлу по вертикали
                if (i > 0) {
                    Edge edgeFromUpper = new Edge(nodeArr[nodeCount - graphHeight], currentNode, "vertical");
                    edgeArr[edgeCount++] = edgeFromUpper;
                }
                // грань к предыдущему узлу по горизонтали
                if (j > 0) {
                    Edge edgeFromLeft = new Edge(nodeArr[nodeCount - 1], currentNode, "horizontal");
                    edgeArr[edgeCount++] = edgeFromLeft;
                }
                nodeCount++;
            }
        }
    }
    public void createAnts() {
        antArr = new Ant[antNum];
        pathArr = new Path[10000];
        for (int i = 0; i < antNum; i++) {
            Path path = new Path(this.edgeCount);
            pathArr[pathCount++] = path;
            Ant ant = new Ant(this.homeNode, path);
            antArr[i] = ant;
        }
    }
    public void drawGraph(Graphics g) {
        g.setColor(Color.white);
        g.fillRect(0, 0, windowWidth, windowHeight);
        g.setColor(Color.pink);
        for (int i = 0; i < edgeArr.length; i++) {
            if (edgeArr[i] == null) break;
            edgeArr[i].drawEdge(g);
        }
        for (int i = 0; i < nodeArr.length; i++) {
            nodeArr[i].drawNode(g, homeNode == nodeArr[i], foodNode == nodeArr[i]);
        }
    }

    public void paint(Graphics g) {
        drawGraph(g);
        // draw ants
        g.setColor(Color.black);
        for (int i = 0; i < antArr.length; i++) {
            g.fillRect(antArr[i].location.x + 20, antArr[i].location.y + 20, 20, 10);
        }
        if (bestPath != null) {
            g.setFont(new Font("Arial", Font.PLAIN, 16));
            g.drawString("Кратчайший путь: " + bestPath.sum, 100, 20);
        }
    }

    @Override
    public void run() {
        // Цикл отрисовки кадров
        while (!gameOver) {
            Edge[] chosenEdges = new Edge[antNum];
            int finishedAntCount = 0;
            int pausedAntCount = 0;
            // Передвигаем каждого муравья
            for (int i = 0; i < antNum; i++) {
                if (antArr[i].status != "paused") {
                    chosenEdges[i] = antArr[i].moveAnt(this);
                } else {
                    pausedAntCount++;
                }
            }
            // Оставляем феромоны
            for (int i = 0; i < antNum; i++) {
                if (antArr[i].status == "finished") {
                    finishedAntCount++;
                } else if (antArr[i].status != "paused") {
                    antArr[i].leaveFeromon(chosenEdges[i]);
                }
            }
            if (pausedAntCount == antNum) {
                // Элитный муравей
                for (int i = 0; i < bestPath.edgesCount; i++) {
                    bestPath.edges[i].feromon++;
                }
                // Запускаем новый цикл поиска пищи
                for (int i = 0; i < antNum; i++) {
                    antArr[i].status = "search-food";
                }
            }
            // Проверяем нужно ли завершить алгоритм
            if (finishedAntCount == antNum) {
                gameOver = true;
                System.out.println("Кратчайший путь: " + bestPath.sum);
            }
            try{
                Thread.currentThread().sleep(repaintDelay);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            repaint();
        }
    }
}
