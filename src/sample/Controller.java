package sample;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

public class Controller {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private AnchorPane mainAnchor;

    @FXML
    private Button buttonDoing;

    @FXML
    private Button buttonSave;

    @FXML
    private Button buttonLoad;

    @FXML
    public Pane pane;

    @FXML
    private Button buttonClear;

    @FXML
    void initialize() {

        buttonClear.setOnAction(e -> {
            clearAll();
            Main.graph = new ListGraph();
        });

        buttonLoad.setOnAction(e -> {
            clearAll();
            Main.graph = new ListGraph();
            final FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(Main.stage);
            try {
                Main.graph = Utils.fromNotation(file);
            } catch (Exception ex) {
                alert("Ошибка при создании графа");
                System.err.println(ex);
            }
            draw(Main.graph);
        });

        buttonSave.setOnAction(e -> {
            final FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showSaveDialog(Main.stage);
            try {
                Utils.makeNotation(file, Main.graph);
            } catch (Exception ex) {
                alert("Ошибка при записи в файл");
                System.err.println(ex);
            }
        });

        buttonDoing.setOnAction(e -> {
            if (Main.graph.checkFullBipartite()) {
                draw(Main.graph);
            } else {
                alert("Граф не является полным двудольным");
            }
        });

        EventHandler<MouseEvent> eventEventHandler = mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.PRIMARY && !mouseEvent.isShiftDown()) {
                if (mouseEvent.getX() > 20 && mouseEvent.getX() < 810 && mouseEvent.getY() > 20 && mouseEvent.getY() < 580) {
                    if (Utils.isClear(mouseEvent.getX(), mouseEvent.getY())) {
                        addVertex(mouseEvent.getX(), mouseEvent.getY(), Color.WHITE, false);
                    } else {
                        alert("Нельзя ставить вершину друг на друга!");
                    }
                } else {
                    alert("Нельзя ставить вершины, выходящие за поле!");
                }
            }
        };

        pane.addEventFilter(MouseEvent.MOUSE_CLICKED, eventEventHandler);
    }

    private void alert(String str) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Attention");
        alert.setHeaderText(null);
        alert.setContentText(str);
        alert.showAndWait();
    }

    private void addEdge(StackPane st) {
        if (!Main.edge) {
            Text txt = (Text) st.getChildren().get(1);
            Main.neighbors = Integer.parseInt(txt.getText());
            Main.edge = true;
            Main.prev = st;
        } else {
            int neighbor = Main.neighbors;
            Line ln = new Line(Main.graph.getVertex(neighbor).getX() + 20,
                    Main.graph.getVertex(neighbor).getY() + 20,
                    st.getLayoutX() + 20, st.getLayoutY() + 20);
            pane.getChildren().add(ln);
            Text text1 = (Text) st.getChildren().get(1);
            int next = Integer.parseInt(text1.getText());
            Main.edge = false;
            Main.lines.add(new Lines(neighbor, next, ln));
            Main.graph.addEdge(neighbor, next);
            pane.getChildren().remove(st);
            pane.getChildren().add(st);
            pane.getChildren().remove(Main.prev);
            pane.getChildren().add(Main.prev);
            Main.prev = null;
        }
    }

    private void addVertex(double x, double y, Color color, boolean fromFile) {
        Circle c = new Circle(x, y, 20, color);
        c.setStroke(Color.BLACK);
        c.setStrokeType(StrokeType.INSIDE);
        c.setStrokeWidth(2);
        Integer num = Main.removed.isEmpty() ? Main.count++ : Main.removed.poll();
        Text text = new Text(num.toString());
        text.setFont(new Font(15));
        StackPane st = new StackPane(c, text);
        st.setLayoutX(x - 20);
        st.setLayoutY(y - 20);
        st.setShape(c);
        st.setScaleShape(true);
        st.addEventFilter(MouseEvent.MOUSE_ENTERED, mouseEvent12 -> {
            Circle c1 = (Circle) st.getChildren().get(0);
            c1.setStroke(Color.GRAY);
        });
        st.addEventFilter(MouseEvent.MOUSE_EXITED, mouseEvent1 -> {
            Circle c1 = (Circle) st.getChildren().get(0);
            c1.setStroke(Color.BLACK);
        });
        st.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY && !e.isShiftDown()) {
                removeVertex(st);
            } else if (e.getButton() == MouseButton.PRIMARY) {
                addEdge(st);
            } else if (e.getButton() == MouseButton.SECONDARY) {
                removeEdge(st);
            }
        });
        if (!fromFile) {
            Main.graph.addVertex(num, x - 20, y - 20, color);
        }
        pane.getChildren().add(st);
    }

    private void removeEdge(StackPane st) {
        if (Main.prevEdge) {
            Text text1 = (Text) st.getChildren().get(1);
            int number = Integer.parseInt(text1.getText());
            int number2 = Main.prevNeighbor;
            Lines l1 = null;
            for (Lines l : Main.lines) {
                if (l.first == number && l.second == number2 ||
                        l.first == number2 && l.second == number) {
                    pane.getChildren().remove(l.line);
                    l1 = l;
                    break;
                }
            }
            Main.graph.removeEdge(number, number2);
            Main.lines.remove(l1);
            Main.prevEdge = false;
        } else {
            Text txt = (Text) st.getChildren().get(1);
            Main.prevNeighbor = Integer.parseInt(txt.getText());
            Main.prevEdge = true;
        }
    }

    private void removeVertex(StackPane st) {
        Text txt = (Text) st.getChildren().get(1);
        int num = Integer.parseInt(txt.getText());
        Main.graph.removeVertex(num);
        pane.getChildren().remove(st);
        for (ListIterator<Lines> it = Main.lines.listIterator(); it.hasNext(); ) {
            Lines l = it.next();
            if (l.second == num || l.first == num) {
                pane.getChildren().remove(l.line);
                it.remove();
            }
        }
        Main.removed.add(num);
    }

    private void clearAll() {
        pane.getChildren().clear();
        Main.lines.clear();
        Main.removed.clear();
        Main.prevNeighbor = null;
        Main.prev = null;
        Main.prevEdge = false;
        Main.edge = false;
        Main.count = 0;
    }

    private void draw(Graph g) {
        clearAll();

        double x, y;
        boolean line;
        for (Graph.Vertex v : g.fullSearch()) {
            x = v.getX() + 20;
            y = v.getY() + 20;
            for(Graph.Vertex v1 : v.ItrNeighbors()) {
                Lines ln = new Lines(v.getNumber(), v1.getNumber(), new Line(x, y, v1.getX() + 20, v1.getY() + 20));
                line = true;
                for(Lines l : Main.lines) {
                    if(l.second == v.getNumber() && l.first == v1.getNumber() ||
                            l.second == v1.getNumber() && l.first == v.getNumber()) {
                        line = false;
                        break;
                    }
                }
                if(line) {
                    Main.lines.add(ln);
                    pane.getChildren().add(ln.line);
                }
            }
            addVertex(x, y, v.getColor(), true);

        }
    }
}

