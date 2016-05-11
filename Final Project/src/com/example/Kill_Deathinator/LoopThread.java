package com.example.Kill_Deathinator;

import android.graphics.Canvas;

/**
 * Created by 1568630 on 4/27/2016.
 */
public class LoopThread extends Thread {
    static long FPS=10;
    private DrawView view;
    private boolean running = false;
    public LoopThread(DrawView view){
        this.view=view;
    }
    public void setRunning(boolean run){
        running = run;
    }
    @Override
    public void run(){
        long ticksPS=1000/FPS;
        long startTime;
        long sleepTime;
        while(running){
            Canvas c=null;
            startTime=System.currentTimeMillis();
            try{
                c=view.getHolder().lockCanvas();
                synchronized(view.getHolder()){
                    view.onDraw(c);
                }
            }finally{
                if(c!=null){
                    view.getHolder().unlockCanvasAndPost(c);
                }
            }
            sleepTime=ticksPS-(System.currentTimeMillis()-startTime);
            try{
                if(sleepTime>0)
                    sleep(sleepTime);
                else
                    sleep(10);
            }catch(Exception e){}
        }
    }
}
