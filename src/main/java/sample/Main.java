package sample;

import controller.Controller;
import javafx.application.Application;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Pendulum;
import model.PendulumParams;

import java.util.concurrent.atomic.AtomicInteger;


public class Main extends Application {
    double width = Controller.getWidth();///ширина окна
    double height = Controller.getHeight();//высота окна
    Pendulum pendulum = new Pendulum(0,0,width,height);


    private Group addGroup() {

        //добавление в группу всех элементов GUI
        Group root = new Group();
        Canvas canvas = new Canvas(width, height);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Label angleSign = new Label("Angle(degree)");
        Label tensionSign = new Label("Tension(%)");
        TextField angle = new TextField("30");
        TextField tension = new TextField("10");
        Button start = new Button("Start");
        root.getChildren().addAll(canvas, start, tension, angle, angleSign, tensionSign);
        start.setLayoutX(600.0);
        start.setLayoutY(20.0);
        tension.setLayoutX(300.0);
        tension.setLayoutY(30.0);
        angle.setLayoutX(50.0);
        angle.setLayoutY(30.0);
        angleSign.setLayoutX(50.0);
        angleSign.setLayoutY(10.0);
        tensionSign.setLayoutX(300.0);
        tensionSign.setLayoutY(10.0);


        start.setOnAction(event -> {
            pendulum.setAngle(Integer.parseInt(angle.getText()));
            pendulum.setTension(Integer.parseInt(tension.getText()));
            pendulum.startThread();
            pendulum.addOnCordsListener(
                    (observableValue, pendulumParams, t1) ->
                            Platform.runLater(() -> {
                                gc.clearRect(0, 0, width, height);
                                Controller.pendulumDraw(pendulumParams.cords().x(), pendulumParams.cords().y(),
                                        pendulumParams.fi(), gc);
                            })
            );
        });
        return root;
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Spring pendulum");
        Group root = addGroup();
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        primaryStage.setOnCloseRequest(windowEvent -> pendulum.shutdownThread());
    }



    public static void main(String[] args) {
        launch(args);
    }


}