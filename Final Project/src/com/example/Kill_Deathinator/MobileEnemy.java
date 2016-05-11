package com.example.Kill_Deathinator;

/**
 * Created by 1568630 on 4/29/2016.
 */
public class MobileEnemy extends WorldObject{
    /*elements*/
    private int sX;//starting X position
    private int sY;//starting Y position
    private boolean vertical;//if the unit moves up/down, or left/right(true=up/down)
    private boolean leaving;//if the unit is traveling away from, or towards their home location(true=away)
    private boolean living;//if the unit is still alive
    private boolean moved;//if the unit has moved this turn
    /*constructor*/
    public MobileEnemy(int XPos, int YPos, int SX, int SY, boolean V, boolean Living, boolean Leaving){
        super(XPos, YPos);
        sX=SX;
        sY=SY;
        vertical=V;
        living=Living;
        leaving=Leaving;
    }
    /*methods*/
    /*gets & sets*/
    public int getSX() {
        return sX;
    }
    public void setSX(int sX) {
        this.sX = sX;
    }
    public int getSY() {
        return sY;
    }
    public void setSY(int sY) {
        this.sY = sY;
    }
    public boolean isVertical() {
        return vertical;
    }
    public void setVertical(boolean vertical) {
        this.vertical = vertical;
    }
    public boolean isLeaving() {
        return leaving;
    }
    public void setLeaving(boolean leaving) {
        this.leaving = leaving;
    }
    public boolean isLiving() {
        return living;
    }
    public void setLiving(boolean living) {
        this.living = living;
    }
    public boolean getMoved() {
        return moved;
    }
    public void setMoved(boolean moved) {
        this.moved = moved;
    }
}
