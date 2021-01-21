package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.*;

public class Main extends Application {

    public static Stage stage;
    public static Graph graph = new ListGraph();
    public static int count = 0;
    public static boolean edge = false;
    public static Integer neighbors = null;
    public static boolean prevEdge = false;
    public static Integer prevNeighbor = null;
    public static StackPane prev = null;
    public static Queue<Integer> removed = new LinkedList<>();
    public static List<Lines> lines = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        stage = primaryStage;
        primaryStage.setTitle("Graph");
        primaryStage.setScene(new Scene(root, 1000, 600));
        primaryStage.show();
    }


    public static void main(String[] args) throws Exception {
        launch(args);
    }
}
