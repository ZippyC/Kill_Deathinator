package com.example.Kill_Deathinator;

/**
 * Created by 1568630 on 5/6/2016.
 */
public class WorldObject{
    private int xPos;//original X position on the board
    private int yPos;//original Y position on the board
    private int type;//0=player, 1=treasure, 2=tree, 3=archer, 4=player home, 5=player on a tree, 6=soldier, 7=scout
    private int vision;//how far can the unit see
    private boolean enemy;//if this unit is an enemy
    private boolean walkable;//if the player can walk over this unit
    public WorldObject(int XPos, int YPos, int Type, int Vision, boolean Enemy, boolean Walkable) {
        xPos=XPos;
        yPos=YPos;
        type=Type;
        vision=Vision;
        enemy=Enemy;
        walkable=Walkable;
    }
    /*gets & sets*/
    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }
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
    public int getVision() {
        return vision;
    }
    public void setVision(int vision) {
        this.vision = vision;
    }
}

