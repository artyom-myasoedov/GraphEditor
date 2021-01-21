package sample;

import javafx.scene.paint.Color;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Utils {

    public static void makeNotation(File file, Graph g1) throws IOException {
        StringBuilder str = new StringBuilder();
        for (Graph.Vertex v : g1.fullSearch()) {
            str.append("vertex: ")
                    .append(v.getNumber())
                    .append(" x: ")
                    .append(v.getX())
                    .append(" y: ")
                    .append(v.getY())
                    .append(" color: ")
                    .append(v.getColor())
                    .append(" (");
            boolean first = true;
            for (Graph.Vertex i : v.ItrNeighbors()) {
                if (first) {
                    str.append(i.getNumber());
                    first = false;
                } else {
                    str.append(", ")
                            .append(i.getNumber());
                }
            }
            str.append(")\n");
        }
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(str.toString());
        fileWriter.close();
    }

    public static Graph fromNotation(File file) throws Exception {
        Graph g = new ListGraph();
        int num;
        double x, y;
        Map<Integer, List<Integer>> edges = new HashMap<>();
        String[] neighbors;
        List<Integer> neighbors2 = new ArrayList<>();
        Color c;
        List<String> lines = new ArrayList<>();
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNext()) {
                lines.add(scanner.nextLine());
            }
        }
        for (String str : lines) {
            str = str.substring(str.indexOf(' ') + 1);
            num = Integer.parseInt(str.substring(0, str.indexOf(' ')));
            str = str.substring(str.indexOf(':') + 2);
            x = Double.parseDouble(str.substring(0, str.indexOf(' ')));
            str = str.substring(str.indexOf(':') + 2);
            y = Double.parseDouble(str.substring(0, str.indexOf(' ')));
            c = toColor(str.substring(str.lastIndexOf(':') + 2, str.indexOf('(') - 1));
            str = str.substring(str.indexOf('(') + 1, str.length() - 1);
            g.addVertex(num, x, y, c);
            if (!str.isEmpty()) {
                neighbors = str.split(", ");
                for (String i : neighbors) {
                    neighbors2.add(Integer.parseInt(i));
                }
            }
            edges.put(num, neighbors2);
            neighbors2 = new ArrayList<>();
        }
        g.setNeighbors(edges);
        return g;
    }

    private static Color toColor(String str) {
        if (str.equals("0xffffffff")) {
            return Color.WHITE;
        } else if (str.equals("0xff7f50ff")) {
            return Color.CORAL;
        }
        return Color.SKYBLUE;
    }

    public static boolean isClear(double x, double y) {
        for (Graph.Vertex v : Main.graph.fullSearch()) {
            double t = (x - v.getX()) * (x - v.getX()) + (y - v.getY()) * (y - v.getY());
            if (t < 1600) {
                return false;
            }
        }
        return true;
    }

}

