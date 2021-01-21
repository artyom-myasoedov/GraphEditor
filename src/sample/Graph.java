package sample;

import javafx.scene.paint.Color;

import java.util.*;
import java.util.Map.Entry;

public interface Graph {

    interface Vertex {

        int getNumber();

        double getY();

        double getX();

        Color getColor();

        void setColor(Color c);

        //добавляет смежный узел
        void addNeighbor(int neighborNum);

        //возвращает список всех смежных узлов
        ArrayList<ListGraph.Vertex> getNeighbors();

        //удаляет смежный узел
        void removeNeighbor(int num);

        // итератор для перебора всех смежных узлов для данного

        Iterable<Vertex> ItrNeighbors();
    }

    Map<Integer, Vertex> getvEdjMap();

    int vertexCount();

    int edgeCount();
    Vertex getVertex(int num);

    //добавляет узел
    void addVertex(int v, double x, double y, Color c);

    void addVertex(int v, double x, double y);

    void addVertex(int v);

    void addEdge(int v1, int v2);

    void removeEdge(int v1, int v2);

    void removeVertex(int v);

    void setNeighbors(Map<Integer, List<Integer>> lists);

    Iterable<Vertex> adjacencies(int v);

    default boolean isAdj(int v1, int v2) {
        for (Vertex adj : adjacencies(v1)) {
            if (adj.getNumber() == v2) {
                return true;
            }
        }
        return false;
    }

    default Iterable<Vertex> bfs(int v) {
        return new Iterable<>() {
            private Queue<Vertex> queue = null;
            private Map<Integer, Boolean> visited = null;

            @Override
            public Iterator<Vertex> iterator() {
                queue = new LinkedList<>();
                queue.add(getvEdjMap().get(v));
                visited = new HashMap<>();
                for (Vertex i : Graph.this.getvEdjMap().values()) {
                    visited.put(i.getNumber(), false);
                }
                visited.put(v, true);

                return new Iterator<>() {
                    @Override
                    public boolean hasNext() {
                        return !queue.isEmpty();
                    }

                    @Override
                    public Vertex next() {
                        Vertex result = queue.poll();
                        assert result != null;
                        for (Vertex adj : Graph.this.adjacencies(result.getNumber())) {
                            if (!visited.get(adj.getNumber())) {
                                visited.put(adj.getNumber(), true);
                                queue.add(adj);
                            }
                        }
                        return result;
                    }
                };
            }
        };
    }

    default Iterable<Vertex> bfs() {
        int first = 0;
        if (getvEdjMap().containsKey(0)) {
            return bfs(0);
        } else {
            for (Vertex i : getvEdjMap().values()) {
                if (i != null) {
                    first = i.getNumber();
                    break;
                }
            }
            return bfs(first);
        }
    }

    Iterable<Vertex> fullSearch();

    boolean checkEulerCicle(int v);

    boolean checkFullBipartite();
}

