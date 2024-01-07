package model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;

import java.io.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Pendulum {
    private static final double SPRING_LENGTH = 300;// длина пружины без груза
    private static final double K = 2;//жёсткость пружины
    private static final double M = 1;//масса груза
    private static final double G = 9.8;//ускорение свободного падения
    private final AtomicInteger angle = new AtomicInteger();//угол отклонения груза
    private final AtomicInteger tension = new AtomicInteger();//удлинение пружины в %
    private ObjectProperty<Cords> params = new SimpleObjectProperty<>();// объект для записи координат и угла
    private Thread processorThread;//поток
    private Cords areaSize;// размеры окна
    private boolean shutdown = false;//состоянение потока
    private static final double METER_PER_PIXELS = 0.1;

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

    private void startProcess(){

        double fi0 = (double) angle.get() * Math.PI / 180.0;//начальный угол в радианах
        double r1 = (tension.get()/100.0 + 1.0)* SPRING_LENGTH +M*G/K/METER_PER_PIXELS;//начальная длина пружины
        double x = areaSize.x() / 2 + r1 * Math.sin(fi0);//координата х центра масс груза
        double y = areaSize.y() / 10 + r1 * Math.cos(fi0);//координата у центра масс груза
        double delay = 0.02;//время между кадрами
        double Vx = 0;//х состовляющая скорости груза
        double Vy = 0;//у состовляющая скорости груза
        double Ax=0;//х состовляющая ускорения груза
        double Ay=0;//у состовляющая ускорения груза
        long lastUpdate = 0;
        long startTime = System.currentTimeMillis();
        try {
            beginWrite(angle.get(),tension.get());//очищаем файл изаписываем название передаваемых параметров
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        while (!shutdown) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastUpdate < delay*1000)
                continue;
            lastUpdate = System.currentTimeMillis();
            Ax = getAx(x, y);
            Ay = getAy(x, y);
            Vx += Ax;//интегрируем ускорение
            Vy += Ay;
            x += Vx;//интегрируем скорость
            y += Vy;
            params.set(new Cords(x, y));//записываем координаты груза

            double lengthening = getLengthening(x, y)/SPRING_LENGTH;//относительное удлинение пружины
            double time = ((currentTime - startTime)/1000.0);// время в секундах
            double amplitude = (y-(areaSize.y()/10 + SPRING_LENGTH + M*G/K/METER_PER_PIXELS))
                    /SPRING_LENGTH;//относительная амплитуда колебаний из положения равновесия
            try {
                writeToTxtFile(time + " " + lengthening + " " + amplitude);//запись параметров в txt файл
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public double getLengthening(double x, double y) {//удлиннение пружины в точке (x,y)
        return Math.sqrt(Math.pow((x-areaSize.x()/2),2)+Math.pow(y-areaSize.y()/10,2))-SPRING_LENGTH;
    }
    public double getFi(double x, double y){//угол отконения груза в точке (x,y)
        double fi = Math.atan((x-(areaSize.x()/2))/Math.abs(y-(areaSize.y()/10)));
        if (y<areaSize.y()/10 && x>areaSize.x()/2)
            fi = Math.PI - fi;
        if (y<areaSize.y()/10 && x<areaSize.x()/2)
            fi = - Math.PI - fi;
        return fi;
    }
    public double getAx(double x, double y){//х состовляющая ускорения в точке (x,y)
        double fi = getFi(x,y);
        return -K/M * getLengthening(x,y)*Math.sin(fi)/SPRING_LENGTH;
    }
    public double getAy(double x, double y){//у состовляющая ускорения в точке (x,y)
        double fi = getFi(x,y);
        double Ay = (G/METER_PER_PIXELS - K/M*getLengthening(x,y)*Math.cos(fi))/SPRING_LENGTH;
        return Ay;
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

    public void addOnCordsListener(ChangeListener<? super Cords> listener) {//добавление слушателя
        params.addListener(listener);
    }
    private static void writeToTxtFile(String data) throws IOException {
        //запись в файл значений
        try(FileWriter fw = new FileWriter("data.txt", true))
        {
            fw.write(data);
            fw.append('\n');
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void beginWrite(int angle, int tension) throws IOException {
        //перезапись файла
        try(FileWriter fw = new FileWriter("data.txt", false))
        {
            fw.write("Angle = "+angle+"; Tension = "+tension+'\n');
            fw.write("Time Tension Amplitude\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
