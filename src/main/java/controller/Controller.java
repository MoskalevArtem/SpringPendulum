package controller;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;

public class Controller {
    private static double width = 800;//ширина окна
    private static double height = 600;// высота окна
    public static void pendulumDraw(double x, double y, GraphicsContext gc){//отрисовка системы
        gc.fillOval(x-10,y-10,20,20);//отрисовка груза
        gc.strokeLine(width/2, height/10, x,y);//отрисовка пружины
        gc.strokeLine(0, height / 10, width, height / 10);//отрисовка опоры
    }
    public static boolean checkParameters(int angel,int tension){//пр
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Wrong");
        try {
            if (Math.abs(angel)<=180 && tension >= -90 && tension <= 100) {
                return true;
            }else{
                throw new Exception();
            }
        }
        catch(Exception e) {
                alert.setContentText("!!!Wrong parameters!!!\n-180<=angel<=180\n-90<=tension<=100");
                alert.showAndWait();
                return false;
        }
    }
    public static double getWidth() {return width;}

    public static double getHeight() {return height;}
}
