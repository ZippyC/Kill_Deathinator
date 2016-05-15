package com.example.Kill_Deathinator;
import android.content.Context;
import android.graphics.*;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import java.io.*;
import java.util.*;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by 1568630 on 4/27/2016.
 */
public class DrawView extends SurfaceView {
    private SparseMatrix<MobileEnemy> boardStatic = new SparseMatrix<MobileEnemy>(20, 20);
    private MediaPlayer bavaria;//media files of background music
    private MediaPlayer fluteSong;//media file for the flute song
    private LoopThread loopThread;
    private long lastClick;//keeping track of last click
    private Vibrator vibrator;//vibrator
    private Paint paint = new Paint();//paint used for text color
    private Paint paint2 = new Paint();
    private Bitmap background_1;//level 1 background image
    private Bitmap grassPic;//grass
    private Bitmap archerPic;//archer
    private Bitmap playerPic;//player
    private Bitmap scoutPic;//scout
    private Bitmap soldierPic;//soldier
    private Bitmap treePic;//tree
    private Bitmap treasurePic;//treasure
    private Bitmap arinPic;//arin as Sonic
    private Bitmap rightArrowPic;
    private Bitmap leftArrowPic;
    private Bitmap upArrowPic;
    private Bitmap downArrowPic;
    private boolean bavariaOn = false;//keeping track of if the bavaria song is playing
    private RectF upButton = new RectF(640, 640, 740, 740);
    private RectF leftButton = new RectF(540, 740, 640, 840);
    private RectF downButton = new RectF(640, 840, 740, 940);
    private RectF rightButton = new RectF(740, 740, 840, 840);
    private boolean moving=false;//control if the enemies are moving or not
    private int viewX;
    private int viewY;
    private Paint[][] paints=new Paint[20][20];
    private RectF[][] board=new RectF[8][8];
    private RectF musicButton=new RectF(200, 1200, 300, 1300);
    private int canvasWidth=0;
    private boolean levelStarted=false;
    private int level=1;
    private RectF moveButton=new RectF(500, 1000, 900, 1600);//button to move Objects'
    private int gameState=0;//0=start screen, 1=map, 2=fight
    private int clicks=0;

    public DrawView(Context context){
        super(context);
        loopThread = new LoopThread(this);
        paint.setTextSize(50);//text size
        paint.setColor(Color.rgb(0, 200, 160));//set colour to a blood red-ish type colour
        paint2.setColor(Color.WHITE);
        viewX=12;
        viewY=12;
        getHolder().addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                boolean retry = true;
                loopThread.setRunning(false);
                while (retry) {
                    try {
                        loopThread.join();
                        retry = false;
                    } catch (InterruptedException e) {
                    }
                }
            }

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                loopThread.setRunning(true);
                loopThread.start();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }
        });

        background_1 = BitmapFactory.decodeResource(getResources(), R.drawable.dirt);//instantiate the first backgound
        playerPic=BitmapFactory.decodeResource(getResources(), R.drawable.player);
        archerPic=BitmapFactory.decodeResource(getResources(), R.drawable.archer);
        scoutPic=BitmapFactory.decodeResource(getResources(), R.drawable.scout);
        soldierPic=BitmapFactory.decodeResource(getResources(), R.drawable.soldier);
        treePic=BitmapFactory.decodeResource(getResources(), R.drawable.tree);
        treasurePic=BitmapFactory.decodeResource(getResources(), R.drawable.treasure);
        grassPic=BitmapFactory.decodeResource(getResources(), R.drawable.grass);
        arinPic=BitmapFactory.decodeResource(getResources(), R.drawable.im_sorry);
        rightArrowPic=BitmapFactory.decodeResource(getResources(), R.drawable.right_arrow);
        downArrowPic=BitmapFactory.decodeResource(getResources(), R.drawable.down_arrow);
        leftArrowPic=BitmapFactory.decodeResource(getResources(), R.drawable.left_arrow);
        upArrowPic=BitmapFactory.decodeResource(getResources(), R.drawable.up_arrow);
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);//instantiate the vibrator
        bavaria = MediaPlayer.create(context, R.raw.meanwhile_in_bavaria);
        fluteSong = MediaPlayer.create(context, R.raw.flute_song);
        for(int r=0; r<paints.length; r++){
            for(int c=0; c<paints[0].length; c++){
                paints[r][c] = new Paint();
                paints[r][c].setColor(Color.rgb(200, 200, 160));
                if(r%4==0) {
                    paints[r][c].setColor(Color.rgb(0, 200, 160));
                }
                if(r%4==1) {
                    paints[r][c].setColor(Color.rgb(200, 200, 160));
                }
                if(r%4==2) {
                    paints[r][c].setColor(Color.rgb(0, 7, 160));
                }
                if(r%4==3) {
                    paints[r][c].setColor(Color.rgb(50, 200, 8));
                }
            }
        }
        paints[4][2].setColor(Color.rgb(200, 0, 160));
        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board[0].length; c++) {
                board[r][c] = new RectF(canvasWidth / 8 * c, canvasWidth / 8 * r, canvasWidth / 8 * (c + 1), canvasWidth / 8 * (r + 1));
            }
        }

    }

    //post: returns the index of the player within the sparse matrix if the player is there
    private int[] getPlayerIndex() {
        for(int r=0; r<boardStatic.numRows(); r++){
            for(int c=0; c<boardStatic.numColumns(); c++){
                if(boardStatic.get(r, c)!=null&&(boardStatic.get(r, c).getType()==0||boardStatic.get(r, c).getType()==5)){
                    return new int[]{r, c};
                }
            }
        }
        return null;//should never be reached
    }

    //pre:  "fileName" is the name of a real file containing lines of text
    //post: returns the number of lines in fileName O(n)
    public static int getFileSize(String fileName)throws IOException
    {
        Scanner input = new Scanner(new FileReader(fileName));
        int size=0;
        while (input.hasNextLine())				//while there is another line in the file
        {
            size++;										//add to the size
            input.nextLine();							//go to the next line in the file
        }
        input.close();									//always close the files when you are done
        return size;
    }

    //pre:  "fileName" is the name of a real file containing lines of text - the first line intended to be unused
    //post:returns a String array of all the elements in <filename>.txt, with index 0 unused (heap) O(n)
    public ArrayList<String> readFile(String textFile){
        ArrayList<String> temp= new ArrayList<String>();
        InputStream fis;
        final StringBuffer storedString = new StringBuffer();

        try {
            fis = getResources().openRawResource(R.raw.level_1);
            DataInputStream dataIO = new DataInputStream(fis);
            String strLine = null;

            while ((strLine = dataIO.readLine()) != null) {
                temp.add(strLine);
            }

            dataIO.close();
            fis.close();
        }
        catch  (Exception e) {
        }
        return temp;
    }

    //post: moves every enemy that can move 1 space forward form their current position
    private void move(){
        int[] temp=getPlayerIndex();
        if(temp[0]>0){
            boardStatic.add(temp[0]-1, temp[1], boardStatic.get(temp[0], temp[1]).clone());
            boardStatic.remove(temp[0], temp[1]);
        }
        for(int r=0; r<boardStatic.numRows(); r++){
            for(int c=0; c<boardStatic.numColumns(); c++){
                if(boardStatic.get(r, c)!=null&&(boardStatic.get(r, c).getType()==6||boardStatic.get(r, c).getType()==7)) {
                    if (boardStatic.get(r, c).getVertical()) {//moving vertically
                        if (boardStatic.get(r, c).getLeaving()) {//moving away from starting location
                            if (r < boardStatic.get(r, c).getEX()) {
                                boardStatic.add(r + 1, c, new MobileEnemy(boardStatic.get(r, c).getXPos(), boardStatic.get(r, c).getYPos(), boardStatic.get(r, c).getType(), boardStatic.get(r, c).getVision(), true, false, true, true, true, boardStatic.get(r, c).getEX(), boardStatic.get(r, c).getEY()));
                                boardStatic.remove(r, c);
                            } else
                                boardStatic.add(r - 1, c, new MobileEnemy(boardStatic.get(r, c).getXPos(), boardStatic.get(r, c).getYPos(), boardStatic.get(r, c).getType(), boardStatic.get(r, c).getVision(), true, false, true, true, false, boardStatic.get(r, c).getEX(), boardStatic.remove(r, c).getEY()));
                        } else {//moving towards starting location
                            if (r > boardStatic.get(r, c).getXPos()) {
                                boardStatic.add(r - 1, c, new MobileEnemy(boardStatic.get(r, c).getXPos(), boardStatic.get(r, c).getYPos(), boardStatic.get(r, c).getType(), boardStatic.get(r, c).getVision(), true, false, true, true, false, boardStatic.get(r, c).getEX(), boardStatic.remove(r, c).getEY()));
                            } else
                                boardStatic.add(r + 1, c, new MobileEnemy(boardStatic.get(r, c).getXPos(), boardStatic.get(r, c).getYPos(), boardStatic.get(r, c).getType(), boardStatic.get(r, c).getVision(), true, false, true, true, true, boardStatic.get(r, c).getEX(), boardStatic.remove(r, c).getEY()));
                        }
                    } else {//moving horizontally                //need to change things for moving hor/vert
                        if (boardStatic.get(r, c).getLeaving()) {//moving away from starting position
                            if (c < boardStatic.get(r, c).getEY()) {
                                boardStatic.add(r, c + 1, new MobileEnemy(boardStatic.get(r, c).getXPos(), boardStatic.get(r, c).getYPos(), boardStatic.get(r, c).getType(), boardStatic.get(r, c).getVision(), true, false, true, true, true, boardStatic.get(r, c).getEX(), boardStatic.remove(r, c).getEY()));
                            } else
                                boardStatic.add(r, c - 1, new MobileEnemy(boardStatic.get(r, c).getXPos(), boardStatic.get(r, c).getYPos(), boardStatic.get(r, c).getType(), boardStatic.get(r, c).getVision(), true, false, true, true, false, boardStatic.get(r, c).getEX(), boardStatic.remove(r, c).getEY()));
                        } else {//moving away from starting location
                            if (c > boardStatic.get(r, c).getYPos()) {
                                boardStatic.add(r, c - 1, new MobileEnemy(boardStatic.get(r, c).getXPos(), boardStatic.get(r, c).getYPos(), boardStatic.get(r, c).getType(), boardStatic.get(r, c).getVision(), true, false, true, true, false, boardStatic.get(r, c).getEX(), boardStatic.remove(r, c).getEY()));
                            } else
                                boardStatic.add(r, c + 1, new MobileEnemy(boardStatic.get(r, c).getXPos(), boardStatic.get(r, c).getYPos(), boardStatic.get(r, c).getType(), boardStatic.get(r, c).getVision(), true, false, true, true, true, boardStatic.get(r, c).getEX(), boardStatic.remove(r, c).getEY()));
                        }
                    }
                }
            }
        }
        paint2.setColor(Color.GREEN);
    }                                               //ONLY A TEST SO FAR

    //pre: level>0 and the level being looked for exists
    //post: fill the board with all of the WorldObjects for a given level
    private void createLevel(int level) {
        int x, y, t, v, ex, ey;//x=X index, y= Y index, t=Type, v=Vision, ex=endingX position, ey=endingY Position
        boolean e, w, vert;//e=Enemy, w=Walkable, vert=vertical
        boolean mobiles = true;//tells the loop if it should be reading in WorldObjects or MobileEnemys
        ArrayList<String> temp = readFile("level_1.txt");
        if (temp != null) {
            paint.setColor(Color.LTGRAY);
            for (int i = 1; i < temp.size(); i++) {//define all the variables then add the Object to the sparseMatrix
                if(Integer.parseInt(temp.get(i).substring(0, 1))==1) {                                 //SOME ERROR IS OCCURING HERE AND IDK WHAT IS MAKING IT HAPPEN, reads in the first set of lines of the text file and uses the ELSE code block instead of IF
                    mobiles = false;
                }
                if(mobiles) {
                    x = Integer.parseInt(temp.get(i).substring(1, 3));
                    y = Integer.parseInt(temp.get(i).substring(3, 5));
                    t = Integer.parseInt(temp.get(i).substring(5, 6));
                    v = Integer.parseInt(temp.get(i).substring(6, 7));
                    e = Boolean.parseBoolean(temp.get(t).substring(7, 11));
                    w = Boolean.parseBoolean(temp.get(i).substring(11, 15));
                    boardStatic.add(x, y, (new MobileEnemy(x, y, t, v, e, w, false, false, false, 0, 0)));//add the Object to the sparseMatrix
                }
                else {
                    x = Integer.parseInt(temp.get(i).substring(1, 3));
                    y = Integer.parseInt(temp.get(i).substring(3, 5));
                    t = Integer.parseInt(temp.get(i).substring(5, 6));
                    v = Integer.parseInt(temp.get(i).substring(6, 7));
                    e = Boolean.parseBoolean(temp.get(t).substring(7, 11));
                    w = Boolean.parseBoolean(temp.get(i).substring(11, 15));
                    vert = Boolean.parseBoolean(temp.get(t).substring(15, 19));
                    ex = Integer.parseInt(temp.get(i).substring(19, 21));
                    ey = Integer.parseInt(temp.get(i).substring(21));
                    boardStatic.add(x, y, (new MobileEnemy(x, y, t, v, e, w, vert, true, true, ex, ey)));//add the MobileEnemy to the board
                }
            }
        }
    }                               //NEEDS TO BE FIXED

    //post: creates the 8x8 screen based on the size of the screen
    private void createScreen(){
        for (int r = 0; r < board.length; r++) {//create the 8x8 array of the screen
            for (int c = 0; c < board[0].length; c++) {
                board[r][c] = new RectF(canvasWidth / 8 * c, canvasWidth / 8 * r, canvasWidth / 8 * (c + 1), canvasWidth / 8 * (r + 1));
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(gameState==0) {
            canvas.drawText("This is the Start Screen", (canvas.getWidth() / 2) - 100, 300, paint);
        }
        if(gameState==1){
            if (canvasWidth == 0) {
                canvasWidth = canvas.getWidth();
                createScreen();
            }
            if (!levelStarted) {
                createLevel(1);
            }
            canvas.drawBitmap(background_1, 0, 0, paint);
            for (int r = 0; r < board.length; r++) {
                for (int c = 0; c < board[0].length; c++) {
                    if (boardStatic.get(r + viewX, c + viewY) != null) {
                        switch (boardStatic.get(r + viewX, c + viewY).getType()) {
                            case 0:
                                canvas.drawBitmap(grassPic, null, board[r][c], null);//draw grass
                                canvas.drawBitmap(playerPic, null, board[r][c], null);//draw Player
                                break;
                            case 1:
                                canvas.drawBitmap(grassPic, null, board[r][c], null);//draw grass
                                canvas.drawBitmap(treasurePic, null, board[r][c], null);//drawTreasure
                                break;
                            case 2:
                                canvas.drawBitmap(grassPic, null, board[r][c], null);//draw grass
                                canvas.drawBitmap(treePic, null, board[r][c], null);//draw Tree
                                break;
                            case 3:
                                canvas.drawBitmap(grassPic, null, board[r][c], null);//draw grass
                                canvas.drawBitmap(archerPic, null, board[r][c], null);//draw Archer
                                break;
                            case 5:
                                canvas.drawBitmap(grassPic, null, board[r][c], null);//draw grass
                                canvas.drawBitmap(treePic, null, board[r][c], null);//draw Tree
                                canvas.drawBitmap(playerPic, null, board[r][c], null);//draw Player
                                break;
                            case 6:
                                canvas.drawBitmap(grassPic, null, board[r][c], null);//draw grass
                                canvas.drawBitmap(soldierPic, null, board[r][c], null);//draw Soldier
                                break;
                            case 7:
                                canvas.drawBitmap(grassPic, null, board[r][c], null);//draw grass
                                canvas.drawBitmap(scoutPic, null, board[r][c], null);//draw Scout
                                break;
                            default:
                                canvas.drawBitmap(background_1, null, board[r][c], null);//draw MacNabb
                                break;
                        }
                    } else {
                        canvas.drawBitmap(grassPic, null, board[r][c], null);//draw grass
                    }
                }
            }
            canvas.drawBitmap(upArrowPic, null, upButton, null);
            canvas.drawBitmap(downArrowPic, null, downButton, null);
            canvas.drawBitmap(leftArrowPic, null, leftButton, null);
            canvas.drawBitmap(rightArrowPic, null, rightButton, null);
            canvas.drawText("x: " + viewX + " Y: " + viewY, (canvas.getWidth() / 2) - 100, 50, paint);//print screen location
            canvas.drawBitmap(arinPic, null, moveButton, null);
            canvas.drawRect(musicButton, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (System.currentTimeMillis() - lastClick > 150) {//prevents spam tapping and accidentally pressing multiple times based on dragging
            /*if(!bavariaOn) {
                bavaria.start();
                bavariaOn=true;
            }*/
            lastClick = System.currentTimeMillis();
            float x = event.getX();
            float y = event.getY();
            if (gameState == 0) {
                gameState=1;
            }
            if (gameState == 1) {
                synchronized (getHolder()) {
                    if (upButton.contains(x, y)) {
                        if (viewX > 0) {
                            viewX -= 2;
                        }
                    } else if (downButton.contains(x, y)) {
                        if (viewX < 12)
                            viewX += 2;
                    } else if (rightButton.contains(x, y)) {
                        if (viewY < 12) {
                            viewY += 2;
                        }
                    } else if (leftButton.contains(x, y))
                        if (viewY > 0)
                            viewY -= 2;
                    if (moveButton.contains(x, y)) {
                        move();
                        clicks++;
                    }
                    if (musicButton.contains(x, y)) {
                        if (bavariaOn) {
                            bavariaOn = false;
                            fluteSong.pause();
                        } else {
                            bavariaOn = true;
                            fluteSong.start();
                        }
                    }
                }
            }
        }
        if(gameState==2){
            gameState=1;
        }
        return true;
    }
}