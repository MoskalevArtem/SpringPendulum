package sample;

import controller.Controller;
import javafx.application.Application;

import javafx.application.Platform;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Pendulum;

public class Main extends Application {
    private double width = Controller.getWidth();///ширина окна
    private double height = Controller.getHeight();//высота окна
    Pendulum pendulum = new Pendulum(0,0,width,height);//создаём маятник

    private Group addGroup() {
        //добавление в группу всех элементов GUI
        Group root = new Group();
        Canvas canvas = new Canvas(width, height);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Label angleSign = new Label("Angle(degree)");
        Label tensionSign = new Label("Tension(%)");
        TextField angle = new TextField("0");
        TextField tension = new TextField("0");
        Button start = new Button("Start");
        root.getChildren().addAll(canvas, start, tension, angle, angleSign, tensionSign);
        start.setLayoutX(600.0);start.setLayoutY(20.0);
        tension.setLayoutX(200.0);tension.setLayoutY(30.0);
        angle.setLayoutX(50.0);angle.setLayoutY(30.0);
        angleSign.setLayoutX(50.0);angleSign.setLayoutY(10.0);
        tensionSign.setLayoutX(200.0);tensionSign.setLayoutY(10.0);

        //события при нажатии на кнопку "Start"
        start.setOnAction(event -> {
            int ang = Integer.parseInt(angle.getText());
            int ten = Integer.parseInt(tension.getText());
            if(Controller.checkParameters(ang,ten)) {//проверка введёных параметров
                pendulum.setAngle(ang);//устанавливем угол отклонения
                pendulum.setTension(ten);//устанавлинаем удлинение пружины
                pendulum.startThread();//запускаем поток
                pendulum.addOnCordsListener(//добавляем слушателя
                        (observableValue, pendulumCords, t1) ->
                                Platform.runLater(() -> {
                                    gc.clearRect(0, 0, width, height);//очищаем окно
                                    Controller.pendulumDraw(t1.x(), t1.y(), gc);//отрисовка маятника
                                })
                );
            }
    });
        return root;
    }
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Spring pendulum");
        Group root = addGroup();
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        primaryStage.setOnCloseRequest(windowEvent ->
                pendulum.shutdownThread());// при закрыти окна усыпляем поток
    }

    public static void main(String[] args) {
        launch(args);
    }
}