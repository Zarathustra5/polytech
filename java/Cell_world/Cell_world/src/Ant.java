import java.awt.*;
import java.util.Random;

public class Ant extends Canvas {
    public Node location;
    public Path path;
    public String status = "search-food";
    public int currentCircle = 1;


    public Ant(Node homeNode, Path path) {
        this.location = homeNode;
        this.path = path;
    }

    public Edge moveAnt(Graph graph) {
        Edge[] availableEdges = new Edge[3];
        int availableEdgesCount = 0;
        int maxFeromon = 0;
        Edge[] bestFeromonEdges = new Edge[3];
        int bestFeromonEdgesCount = 0;
        Edge chosenEdge = null;
        // Ищем возможные пути движения
        for (int i = 0; i < graph.edgeCount; i++) {
            boolean isEdgeAvailable;
            if (status == "search-food") {
                isEdgeAvailable = graph.edgeArr[i].nodeA == this.location;
            } else {
                isEdgeAvailable = this.location == graph.edgeArr[i].nodeB;
            }
            if (isEdgeAvailable) availableEdges[availableEdgesCount++] = graph.edgeArr[i];
        }
        if (currentCircle > 1) {
            // Проверяем есть ли путь с максимальным феромоном
            for (int j = 0; j < availableEdgesCount; j++) {
                Edge edge = availableEdges[j];
                if (edge.feromon > maxFeromon) {
                    maxFeromon = edge.feromon;
                }
            }
        }
        // Если не было найдено путя с наибольшим феромоном, то выбираем путь далее
        if (maxFeromon == 0) {
            boolean isChoiceRandom;
            if (status == "search-food") {
                isChoiceRandom = location.xIndex < graph.randomIndex && location.yIndex < graph.randomIndex;
            } else {
                isChoiceRandom = location.xIndex > graph.graphWidth - graph.randomIndex - 1 && location.yIndex > graph.graphHeight - graph.randomIndex - 1;
            }
            if (isChoiceRandom) {
                // Выбираем случайный путь
                int rnd = new Random().nextInt(availableEdgesCount);
                chosenEdge = availableEdges[rnd];
            } else {
                // Выбираем самый короткий путь
                Edge shortestEdge = availableEdges[0];
                for (int j = 1; j < availableEdgesCount; j++) {
                    Edge edge = availableEdges[j];
                    if (shortestEdge.distance < edge.distance) {
                        shortestEdge = edge;
                    }
                }
                chosenEdge = shortestEdge;
            }
        } else {
            // На случай, если путей с наибольшим феромоном несколько
            for (int j = 0; j < availableEdgesCount; j++) {
                Edge edge = availableEdges[j];
                if (edge.feromon == maxFeromon) {
                    bestFeromonEdges[bestFeromonEdgesCount++] = edge;
                }
            }
            int rnd = new Random().nextInt(bestFeromonEdgesCount);
            chosenEdge = bestFeromonEdges[rnd];
        }

        // Обновляем текущие данные о местоположении муравья и его дороги
        path.sum += chosenEdge.distance;
        this.location = chosenEdge.nodeA == this.location ? chosenEdge.nodeB : chosenEdge.nodeA;
        path.edges[path.edgesCount++] = chosenEdge;

        // Проверяем не достиг ли муравей конечных точек - дома или еды
        if (location == graph.foodNode && status == "search-food") {
            status = "search-home";
            System.out.println("search home");
        } else if (location == graph.homeNode && status == "search-home") {
            path.calcSum();
            if (graph.bestPath == null) graph.bestPath = path;
            if (graph.bestPath.sum > path.sum) graph.bestPath = path;
            if (currentCircle >= graph.circlesNum) {
                status = "finished";
            } else {
                status = "paused";
            }
            path = new Path(10000);
            graph.pathArr[graph.pathCount++] = path;
            currentCircle++;
        }
        return chosenEdge;
    }
    public void leaveFeromon(Edge edge) {
        edge.feromon++;
    }
}
