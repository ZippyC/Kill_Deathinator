package com.example.Kill_Deathinator;

/**
 * Created by 1568630 on 5/4/2016.
 */
import java.io.*;
import java.util.*;
public class sMListNode<Object>{
    /*elements*/
    private int row;
    private int col;
    private Object node;//object stored
    /*constructor*/
    public sMListNode(int numRow, int numCol, Object Node){
        row=numRow;
        col=numCol;
        node=Node;
    }
    /*methods*/
    /*gets*/
    public int getRow(){
        return row;
    }
    public int getCol(){
        return col;
    }
    public Object getNode(){
        return node;
    }
    /*sets*/
    public void setNode(Object x){
        node=x;
    }
    /*others*/
   /*public int compareTo(Object a){
      return 0;//temp
   }*/
    //post: returns a copy of this
    public sMListNode clone(){
        return new sMListNode(row, col, node);
    }
}