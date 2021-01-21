package sample;

import javafx.scene.paint.Color;

import java.util.*;

import static java.util.Map.*;

public class ListGraph implements Graph {

    //класс узла графа
    protected class Vertex implements Graph.Vertex {

        private final int number;
        private final double OX;
        private final double OY;
        private Color color;
        private final ArrayList<Vertex> neighbors;

        @Override
        public int getNumber() {
            return number;
        }

        @Override
        public double getY() {
            return OY;
        }

        @Override
        public double getX() {
            return OX;
        }

        @Override
        public Color getColor() {
            return color;
        }

        @Override
        public void setColor(Color c) {
            color = c;
        }

        //добавляет смежный узел
        @Override
        public void addNeighbor(int neighborNum) {
            Graph.Vertex v = vEdjMap.get(neighborNum);
            if(!neighbors.contains(v) && neighborNum != number)
                neighbors.add((Vertex) v);
        }

        //возвращает список всех смежных узлов
        @Override
        public ArrayList<Vertex> getNeighbors() {
            return neighbors;
        }

        //удаляет смежный узел
        @Override
        public void removeNeighbor(int num) {
            neighbors.remove(vEdjMap.get(num));
        }

        // итератор для перебора всех смежных узлов для данного

        @Override
        public Iterable<Graph.Vertex> ItrNeighbors() {
            return new Iterable<>() {
                Integer nextVertex;

                @Override
                public Iterator<Graph.Vertex> iterator() {
                    nextVertex = 0;
                    return new Iterator<>() {
                        @Override
                        public boolean hasNext() {
                            return nextVertex < neighbors.size();
                        }

                        @Override
                        public Graph.Vertex next() {
                            return neighbors.get(nextVertex++);
                        }
                    };
                }
            };
        }

        //конструкторы
        public Vertex(int num, double x, double y, ArrayList<Vertex> neighbors) {
            number = num;
            OX = x;
            OY = y;
            color = Color.WHITE;
            this.neighbors = neighbors;
        }

        public Vertex(int num, double x, double y, Color c) {
            number = num;
            OX = x;
            OY = y;
            color = c;
            neighbors = new ArrayList<>();
        }

        public Vertex(int num, double x, double y) {
            number = num;
            OX = x;
            OY = y;
            color = Color.WHITE;
            neighbors = new ArrayList<>();
        }

        public Vertex(int num) {
            number = num;
            OX = 0;
            OY = 0;
            color = Color.WHITE;
            neighbors = new ArrayList<>();
        }

        public Vertex() {
            number = 0;
            OX = 0;
            OY = 0;
            color = Color.WHITE;
            neighbors = new ArrayList<>();
        }
    }

    private final Map<Integer, Graph.Vertex> vEdjMap = new HashMap<>();
    private int vCount = 0;
    private int eCount = 0;

    private static final Iterable<Graph.Vertex> nullIterable = () -> new Iterator<>() {
        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public Vertex next() {
            return null;
        }
    };

    @Override
    public Map<Integer, Graph.Vertex> getvEdjMap() {
        return vEdjMap;
    }

    @Override
    public int vertexCount() {
        return vCount;
    }

    @Override
    public int edgeCount() {
        return eCount;
    }

    @Override
    public Graph.Vertex getVertex(int num) {
        return vEdjMap.get(num);
    }

    @Override
    //добавляет узел
    public void addVertex(int v, double x, double y, Color c) {
        if (!vEdjMap.containsKey(v)) {
            vEdjMap.put(v, new Vertex(v, x, y, c));
            vCount++;
        }
    }

    public void addVertex(int v, double x, double y) {
        addVertex(v, x, y, Color.WHITE);
    }

    @Override
    public void addVertex(int v) {
        addVertex(v, 0, 0);
    }

    @Override
    //добавляет ребро
    public void addEdge(int v1, int v2) {
        addVertex(v1);
        addVertex(v2);
        if (!isAdj(v1, v2)) {
            vEdjMap.get(v1).addNeighbor(v2);
            vEdjMap.get(v2).addNeighbor(v1);
            eCount++;
        }
    }

    @Override
    //удаляет узел
    public void removeVertex(int v) {
        for (Graph.Vertex i : vEdjMap.values()) {
            Vertex vertex = (Vertex) i;
            if (isAdj(vertex.getNumber(), v)) {
                vertex.removeNeighbor(v);
                eCount--;
            }
        }
        vEdjMap.remove(v);
        vCount--;
    }

    @Override
    //удаляет ребро
    public void removeEdge(int v1, int v2) {
        Graph.Vertex vr1 = vEdjMap.get(v1);
        Graph.Vertex vr2 = vEdjMap.get(v2);
        if (vr1 != null && vr2 != null && vr1.getNeighbors().contains(vr2) && vr2.getNeighbors().contains(vr1)) {
            vEdjMap.get(v1).removeNeighbor(v2);
            vEdjMap.get(v2).removeNeighbor(v1);
            eCount--;
        }
    }

    @Override
    public void setNeighbors(Map<Integer, List<Integer>> lists) {
        for (Integer i : lists.keySet()) {
            ArrayList<Integer> l = (ArrayList<Integer>) lists.get(i);
            if (l != null) {
                for (Integer j : l) {
                    addEdge(i, j);
                }
            }
        }
    }

    @Override
    //итератор для перебора соседей с учётом null элементов
    public Iterable<Graph.Vertex> adjacencies(int v) {
        return vEdjMap.get(v) == null ? nullIterable : vEdjMap.get(v).ItrNeighbors();
    }

    @Override
    // итератор для перебора всех вершин
    public Iterable<Graph.Vertex> fullSearch() {
        return () -> (Iterator<Graph.Vertex>) vEdjMap.values().iterator();
    }

    @Override
    public boolean checkEulerCicle(int v) {
        System.err.println("Not supported yet");
        return false;
    }

    //меняет цвет на "противоположный"
    private static Color changeColor(Color c) {
        return c == Color.SKYBLUE ? Color.CORAL : Color.SKYBLUE;
    }

    @Override
    //проверка на полный двудольный граф
    public boolean checkFullBipartite() {
        if (vCount == 0) {
            return true;
        }
        boolean first = true;
        Vertex vertex = null;
        Queue<Vertex> queue = new LinkedList<>();
        Color c = Color.SKYBLUE;
        int blueCount = 1;
        Map<Integer, Boolean> visited = new HashMap<>();
        for (Graph.Vertex i : vEdjMap.values()) {
            if (i != null && first) {
                vertex = (Vertex) i;
                first = false;
            }
            assert i != null;
            visited.put(i.getNumber(), false);
        }
        assert vertex != null;
        vertex.setColor(c);
        if(vertex.getColor() ==Color.SKYBLUE) {
            blueCount++;
        }
        queue.add(vertex);
        visited.put(vertex.getNumber(), true);
        while (!queue.isEmpty()) {
            Vertex result = queue.poll();
            c = changeColor(result.getColor());
            for (Graph.Vertex adj : ListGraph.this.adjacencies(result.getNumber())) {
                Graph.Vertex vertex1 = vEdjMap.get(adj.getNumber());
                if (!visited.get(adj.getNumber())) {
                    visited.put(adj.getNumber(), true);
                    queue.add((Vertex) vertex1);
                    vertex1.setColor(c);
                } else if (result.getColor() == vertex1.getColor()) {
                    return false;
                }
            }
        }
        return eCount >= blueCount * (vCount - blueCount);
    }
}
