package model;

import controller.Controller;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.canvas.GraphicsContext;

import java.util.concurrent.atomic.AtomicInteger;

public class Pendulum {
    private static final int SPRING_LENGTH = 400;// длина пружины без груза
    private final AtomicInteger angle = new AtomicInteger();
    private final AtomicInteger tension = new AtomicInteger();
    private ObjectProperty<PendulumParams> params = new SimpleObjectProperty<>();
    private Thread processorThread;
    private Cords areaSize;
    private boolean shutdown = false;


    public Pendulum(int angle, int tension, double width, double height) {
        setAngle(angle);
        setTension(tension);

        double startX = width/2 + (SPRING_LENGTH+tension) * Math.sin(angle);//координата х центра масс груза
        double startY = height/10+ (SPRING_LENGTH+tension)* Math.cos(angle);//координата у центра масс груза
        params.set(new PendulumParams(new Cords(startX, startY), angle));
        areaSize = new Cords(width, height);
    }
    public void startThread()
    {
        processorThread = new Thread(this::startProcess);
        processorThread.start();
    }

    private void startProcess() {

        double K = 1;//жёсткость пружины
        double M = 1;//масса груза
        double G = 9.8;//ускорение свободного падения
        long startTime = System.currentTimeMillis();
        long lastUpdate = -1;
        double fi0 = (double) angle.get() * Math.PI / 180;
        double r1 = (double) tension.get() * SPRING_LENGTH / 100;

        while (!shutdown) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastUpdate < 50)
                continue;
            long t = (currentTime - startTime) / 100;
            double r = SPRING_LENGTH + r1*Math.cos(Math.sqrt(M /K) * t/4)*Math.exp(-0.01*t/4);//зависимость длинны пружины от времени
            double fi = fi0*Math.cos(Math.sqrt(G/SPRING_LENGTH) * t/4)*Math.exp(-0.01*t/4);//зависимость угла от времени
            double x = areaSize.x() / 2 + r * Math.sin(fi);//координата х центра масс груза
            double y = areaSize.y() / 10 + r * Math.cos(fi);//координата у центра масс груза
            params.set(new PendulumParams(new Cords(x, y), fi));
        }
    }
    public void setAngle(int angle) {
        this.angle.set(angle);
    }

    public void setTension(int tension) {
        this.tension.set(tension);
    }
    public void shutdownThread()
    {
        shutdown = true;
    }

    public void addOnCordsListener(ChangeListener<? super PendulumParams> listener) {
        params.addListener(listener);
    }


}
