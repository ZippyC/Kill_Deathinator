package com.example.Kill_Deathinator;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;

import java.util.Random;

/**
 * Created by 1568630 on 4/27/2016.
 */
public class Sprite {
    //direction=0up, 1 left, 2 down, 3 right
    //animation=3 back, 1 left, 0 front, 2 right
    int[] DIRECTION_TO_ANIMATION_MAP = {3, 1, 0, 2};
    private static final int BMP_ROWS = 4;
    private static final int BMP_COLUMNS = 3;
    private static final int MAX_SPEED = 5;
    private DrawView drawView;
    private Bitmap personBmp;
    private int x = 0;
    private int y = 0;
    private int xSpeed;
    private int ySpeed;
    private int currentFrame = 0;
    private int width;
    private int height;
    private RectF spriteRectF = new RectF();
    private int type;//0=player;1=enemy;2=angel;3=death angel;4=black angel
    private boolean canMove=false;

    public int getType() {
        return type;
    }

    public Sprite(DrawView drawView, Bitmap personBmp, int Type) {
        this.width = personBmp.getWidth() / BMP_COLUMNS;
        this.height = personBmp.getHeight() / BMP_ROWS;
        this.drawView = drawView;
        this.personBmp = personBmp;
        type = Type;

        Random rnd = new Random();
        if(type==0) {//if player then spawn in center
            x =drawView.getWidth()/2;
            y=drawView.getHeight()/2;

        }
        else {
            x = rnd.nextInt(drawView.getWidth() - width);
            y = rnd.nextInt(drawView.getHeight() - height);
        }
        xSpeed = rnd.nextInt(MAX_SPEED * 2) - MAX_SPEED;
        ySpeed = rnd.nextInt(MAX_SPEED * 2) - MAX_SPEED;
    }

    public boolean contains(Sprite enemy){
        if(enemy.getX()>=x&&(enemy.getX()+15)<=x)
            if(enemy.getY()>=y&&(enemy.getY()+15)<=y)
                return true;
        return false;
    }

    public int getX() {return x;}
    public int getY() {return y;}
    public void setX(int temp){x=temp;}
    public void setY(int temp){y=temp;}
    public boolean getCanMove(){return canMove;}
    public void setCanMove(boolean condition){canMove=condition;}


    private void update(){
        if(type==0) {
            //do nothing because I don't you to HAHAHA
        }
        else {
            if(canMove) {
                if (x >= drawView.getWidth() - width - xSpeed || x + xSpeed <= 0) {
                    xSpeed = -xSpeed;
                }
                x = x + xSpeed;
                if (y >= drawView.getHeight() - height - ySpeed || y + ySpeed <= 0) {
                    ySpeed = -ySpeed;
                }
                y = y + ySpeed;
                currentFrame = ++currentFrame % BMP_COLUMNS;
            }
        }
    }

    public void onDraw(Canvas canvas){//draw the sprite at its current location in its current frame/direction
        update();
        int srcX=currentFrame*width;
        int srcY=getAnimationRow()*height;
        Rect src=new Rect(srcX, srcY, srcX+width, srcY+height);
        spriteRectF.set(x, y, x+width, y+height);
        canvas.drawBitmap(personBmp, src, spriteRectF, null);
    }
    public boolean contains(float x, float y){
        return spriteRectF.contains(x, y);
    }
    // direction = 0 up, 1 left, 2 down, 3 right,
    // animation = 3 back, 1 left, 0 front, 2 right
    private int getAnimationRow(){//returns direction facing
        double dirDouble=(Math.atan2(xSpeed, ySpeed)/(Math.PI/2)+2);
        int direction = (int)Math.round(dirDouble)%BMP_ROWS;
        return DIRECTION_TO_ANIMATION_MAP[direction];
    }
}
