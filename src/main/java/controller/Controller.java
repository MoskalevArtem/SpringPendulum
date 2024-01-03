package controller;

import javafx.scene.canvas.GraphicsContext;

public class Controller {
    private static double width = 800;//ширина окна
    private static double height = 600;// высота окна
    public static void pendulumDraw(double x, double y, double fi, GraphicsContext gc){

        double x1 = x-50; double x2 = x+50;// х коорднинаты вершин груза
        double y1 = y-30; double y2 = y+30;// у координаты вершин груза

        //Координаты вершин груза после поворота на угол fi
        double x1Rot = xRotate(x1,y1,x,y,fi);
        double y1Rot = yRotate(x1,y1,x,y,fi);

        double x2Rot = xRotate(x2,y1,x,y,fi);
        double y2Rot = yRotate(x2,y1,x,y,fi);

        double x3Rot = xRotate(x2,y2,x,y,fi);
        double y3Rot = yRotate(x2,y2,x,y,fi);

        double x4Rot = xRotate(x1,y2,x,y,fi);
        double y4Rot = yRotate(x1,y2,x,y,fi);

        gc.strokeLine(x1Rot,y1Rot , x2Rot, y2Rot);//соединяем вершины груза
        gc.strokeLine(x2Rot,y2Rot , x3Rot, y3Rot);
        gc.strokeLine(x3Rot,y3Rot , x4Rot, y4Rot);
        gc.strokeLine(x4Rot,y4Rot , x1Rot, y1Rot);

        //Координаты точки крепления пружины и груза
        double centreX = (x1Rot+x2Rot)/2;
        double centreY = (y1Rot+y2Rot)/2;

        gc.strokeLine(width/2, height/10, centreX,centreY);//отрисовка пружины
        gc.strokeLine(0, height / 10, width, height / 10);//отрисовка опоры
    }


    public static double xRotate(double x, double y, double xc, double yc, double fi){
        //координата х точки (x1,y1) после поворота вокруг точки (xc,yc) на угол fi
        return (x-xc)*Math.cos(fi)+(y-yc)*Math.sin(fi) + xc;
    }
    public static double yRotate(double x, double y, double xc, double yc, double fi){
        //координата y точки (x1,y1) после поворота вокруг точки (xc,yc) на угол fi
        return -(x-xc)*Math.sin(fi) + (y-yc) * Math.cos(fi)+yc;
    }

    public static double getWidth() {
        return width;
    }

    public static double getHeight() {return height;}
}
