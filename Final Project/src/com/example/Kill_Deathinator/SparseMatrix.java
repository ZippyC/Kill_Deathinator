package com.example.Kill_Deathinator;

/**
 * Created by 1568630 on 5/4/2016.
 */
import java.io.*;
import java.util.*;
public class SparseMatrix<anyType> implements Matrixable<anyType>{
    /*elements*/
    private int rows;
    private int cols;
    private ArrayList<sMListNode> sparse;//container
    /*constructor*/
    public SparseMatrix(int numRows, int numCols){
        rows=numRows;
        cols=numCols;
        sparse= new ArrayList<sMListNode>();
    }
    /*interface methods*/
    //pre: r and c are >=0
    //post: returns the element at row r, col c
    public anyType get(int r, int c){
        if(isEmpty())
            return null;//if array is empty
        for(sMListNode el: sparse){//could be better
            if(el.getRow()==r)
                if(el.getCol()==c)
                    return (anyType)el.getNode();//return object if found
        }
        return null;//if item is not in array
    }
    //pre: r and c are >=0
    //post: changes element at (r,c), returns old value
    public anyType set(int r, int c, anyType x){
        if(isEmpty())
            return null;//null if array is empty
        for(sMListNode el: sparse){//could be better
            if(el.getRow()==r&&el.getCol()==c){
                sMListNode temp=el.clone();
                el.setNode(x);
                return (anyType)temp.getNode();//return old element
            }
        }
        return null;//if location is not used
    }
    //pre: r and c are >=0
    //post: adds obj at row r, col c
    public void add(int r, int c, anyType x){
        if(sparse.isEmpty())
            sparse.add(new sMListNode(r, c, x));
        else{
            boolean flag=true;//used to know if the location is already used
            for(int i=0; i<sparse.size(); i++){
                if(sparse.get(i).getRow()==r&&sparse.get(i).getCol()==c){
                    this.set(r, c, x);//if location is used replace it
                    flag=false;//location was found used
                    break;
                }
            }
            if(flag)//if the location was not already used
                sparse.add(new sMListNode(r, c, x));//temp needs to check if the index is already filled
        }
    }
    //Pre: r and c are >=0
    //post: removes and returns the Object at the given row/col if the Object exists, otherwise return null
    public anyType remove(int r, int c){
        for(int i=0; i<sparse.size(); i++){
            if(sparse.get(i).getRow()==r&&sparse.get(i).getCol()==c){
                sMListNode temp=sparse.get(i).clone();//create temporary copy
                sparse.remove(i);//remove object
                return (anyType)temp.getNode();//return removed object
            }
        }
        return null;//if the object is not there
    }
    //post: returns true if there are no actual elements stored
    public boolean isEmpty(){
        if(sparse.size()==0)
            return true;
        return false;
    }
    //post: clears all elements out of the list
    public void clear(){
        sparse=new ArrayList<sMListNode>();//replaces ArrayList with a new empty one
    }
    //post: returns equivalent structure in 2-D array form
    public Object[][] toArray(){
        Object[][] temp=new Object[rows][cols];//make the array
        for(sMListNode el: sparse){
            temp[el.getRow()][el.getCol()]=el.getNode();//set only values that are in SparseMatrix to be not null
        }
        return temp;
    }
    //post: returns a String representing the entire Matrix in a 2D array format
    public String toString(){
        String sparsey="";
        Object[][] temp=this.toArray();//temporary array
        for(int r=0; r<temp.length; r++){
            if(r>0)
                sparsey+="\n";//go to next line
            for(int c=0; c<temp[0].length; c++){
                if(temp[r][c]==null)
                    sparsey+="- ";//make all nulls appear as -
                else
                    sparsey+=temp[r][c].toString()+" ";
            }
        }
        return sparsey;
    }
    /*gets*/
    public int size(){			//returns # actual elements stored
        return sparse.size();
    }
    public int numRows(){		//returns # rows set in constructor
        return rows;
    }
    public int numColumns(){	//returns # cols set in constructor
        return cols;
    }
}