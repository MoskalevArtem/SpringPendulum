package model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class Pendulum {
    private static final double SPRING_LENGTH = 300;// длина пружины без груза
    private static final double K = 1;//жёсткость пружины
    private static final double M = 1;//масса груза
    private static final double G = 9.8;//ускорение свободного падения
    private final AtomicInteger angle = new AtomicInteger();//угол отклонения груза
    private final AtomicInteger tension = new AtomicInteger();//удлинение пружины в %
    private ObjectProperty<PendulumParams> params = new SimpleObjectProperty<>();// объект для записи координат и угла
    private Thread processorThread;//поток
    private Cords areaSize;// размеры окна
    private boolean shutdown = false;//состоянение потока


    public Pendulum(int angle, int tension, double width, double height) {
        setAngle(angle);
        setTension(tension);
        areaSize = new Cords(width, height);
    }
    public void startThread()//запуск потока
    {
        processorThread = new Thread(this::startProcess);
        processorThread.start();
    }

    private void startProcess() {
        double fi0 = (double) angle.get() * Math.PI / 180;//начальный угол в радианах
        double r1 = (double) tension.get() * SPRING_LENGTH / 100 + SPRING_LENGTH;//начальная длина пружины
        double x = areaSize.x() / 2 + r1 * Math.sin(fi0);//координата х центра масс груза
        double y = areaSize.y() / 10 + r1 * Math.cos(fi0);//координата у центра масс груза
        double samplingTime = 0.05;//время между кадрами
        double Vx = 0;//х состовляющая скорости груза
        double Vy = 0;//у состовляющая скорости груза
        double Ax = 0;//х состовляющая ускорения груза
        double Ay = 0;//у состовляющая ускорения груза
        ArrayList x0 = new ArrayList<Double>();// список, в который будут записываться х координата груза
        ArrayList y0 = new ArrayList<Double>();// список, в который будут записываться у координата груза
        long startTime = System.currentTimeMillis();
        while (!shutdown) {
            long currentTime = System.currentTimeMillis()-startTime;
            if (currentTime % (samplingTime*1000) == 0) {
                x0.add(x);
                y0.add(y);
                Vx = getVx(x0, y0, samplingTime);
                Vy = getVy(x0, y0, samplingTime);
                Ax = getAx(x, y);
                Ay = getAy(x, y);
                x += (Vx * samplingTime + Ax * Math.pow(samplingTime,2) / 2);
                y += (Vy * samplingTime + Ay * Math.pow(samplingTime,2) / 2);
                double fi = getFi(x, y);
                params.set(new PendulumParams(new Cords(x, y), fi));
            }
        }
    }
    public double getLengthening(double x, double y) {//удлиннение пружины в точке (x,y)
        return Math.sqrt(Math.pow((x-areaSize.x()/2),2)+Math.pow(y-areaSize.y()/10,2))-SPRING_LENGTH;
    }
    public double getFi(double x, double y){//угол отконения груза в точке (x,y)
        return Math.atan((x-(areaSize.x()/2))/(y-(areaSize.y()/10)));
    }
    public double getAx(double x, double y){//х состовляющая ускорения в точке (x,y)
        double fi = getFi(x,y);
        return -K* getLengthening(x,y)*Math.sin(fi)/M;
    }
    public double getAy(double x, double y){//у состовляющая ускорения в точке (x,y)
        double fi = getFi(x,y);
        return G - K* getLengthening(x,y)*Math.cos(fi)/M;
    }
    public double getVx(ArrayList x, ArrayList y,double h) {//нахождение Vx в точке (x,y)
        //методом трапеций интегрируем ускорение по х
        double sum = 0;
        for (int i = 1; i < x.size(); i++)
            sum += (getAx((Double) x.get(i),(Double) y.get(i)) +
                    getAx((Double) x.get(i-1), (Double) y.get(i-1))) / 2 * h;
        return sum;
    }
    public double getVy(ArrayList x, ArrayList y,double h) {//нахождение Vу в точке (x,y)
        //методом трапеций интегрируем ускорение по у
        double sum = 0;
        for (int i = 1; i < x.size(); i++)
            sum += (getAy((Double) x.get(i), (Double) y.get(i)) +
                    getAy((Double) x.get(i-1), (Double) y.get(i-1))) / 2 * h;
        return sum;
    }

    public void setAngle(int angle) {//установка начального угла отклонения груза
        this.angle.set(angle);
    }

    public void setTension(int tension) {//установка начального растяжения пружины в %
        this.tension.set(tension);
    }
    public void shutdownThread()//метод для усыпления потока
    {
        shutdown = true;
    }

    public void addOnCordsListener(ChangeListener<? super PendulumParams> listener) {//добавление слушателя
        params.addListener(listener);
    }


}
