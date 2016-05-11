package com.example.Kill_Deathinator;

/**
 * Created by 1568630 on 5/6/2016.
 */
public class StaticObject{
    /*elements*/
    private int type;//0=player, 1=treasure, 2=tree, 3=archer, 4=player home, 5=player on a tree
    private boolean enemy;
    private boolean walkable;
    /*constructor*/
    public StaticObject(/*int XPos, int YPos, */int Type, boolean Enemy, boolean Walkable){
        //super(XPos, YPos);
        type=Type;
        enemy=Enemy;
        walkable=Walkable;
    }

    /*gets & sets*/
    public boolean isEnemy() {
        return enemy;
    }
    public void setEnemy(boolean enemy) {
        this.enemy = enemy;
    }
    public boolean isWalkable() {
        return walkable;
    }
    public void setWalkable(boolean walkable) {
        this.walkable = walkable;
    }
    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }
}
