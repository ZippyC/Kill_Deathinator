package com.example.Kill_Deathinator;

/**
 * Created by 1568630 on 4/29/2016.
 */
public class MobileEnemy extends WorldObject{
    /*elements*/
    private int eX;//starting X position
    private int eY;//starting Y position
    private boolean vertical;//if the unit moves up/down, or left/right(true=up/down)
    private boolean leaving;//if the unit is traveling away from, or towards their home location(true=away)
    private boolean living;//if the unit is still alive
    private boolean moved;//if the unit has moved this turn
    /*constructor*/
    public MobileEnemy(int XPos, int YPos, int Type, boolean Enemy, boolean Walkable, boolean V, boolean Living, boolean Leaving, int EX, int EY){
        super(XPos, YPos, Type, Enemy, Walkable);
        eX=EX;
        eY=EY;
        vertical=V;
        living=Living;
        leaving=Leaving;
    }
    /*methods*/
    /*gets & sets*/

    public int getEX() {
        return eX;
    }
    public void setEX(int eX) {
        this.eX = eX;
    }
    public int getEY() {
        return eY;
    }
    public void setEY(int eY) {
        this.eY = eY;
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
