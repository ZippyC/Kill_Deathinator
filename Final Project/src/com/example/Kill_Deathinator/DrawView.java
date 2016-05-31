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
    private Bitmap visionMarker;//shows user where the enemies can see
    private boolean bavariaOn = false;//keeping track of if the bavaria song is playing
    private RectF upScreenButton = new RectF(640, 640, 740, 740);
    private RectF leftScreenButton = new RectF(540, 740, 640, 840);
    private RectF downScreenButton = new RectF(640, 840, 740, 940);
    private RectF rightScreenButton = new RectF(740, 740, 840, 840);
    private RectF upButton = new RectF(640, 950, 740, 1050);
    private RectF leftButton = new RectF(540, 1050, 640, 1150);
    private RectF downButton = new RectF(640, 1150, 740, 1250);
    private RectF rightButton = new RectF(740, 1050, 840, 1150);
    private boolean moving=false;//control if the enemies are moving or not
    private int viewX;
    private int viewY;
    private Paint[][] paints=new Paint[20][20];
    private RectF[][] board=new RectF[8][8];
    private RectF musicButton=new RectF(200, 1200, 300, 1300);
    private int canvasWidth=0;
    private boolean levelStarted=false;
    private int level=1;
    private RectF moveButton=new RectF(200, 1300, 300, 1400);//button to move Objects'
    private int gameState=0;//0=start screen, 1=map, 2=fight
    private int[] playerHome = new int[2];
    private int[] playerLocation=new int[2];
    private boolean candy=false;
    private int playerHealth=20;
    private int[][] enemyVision = new int[20][20];

    public DrawView(Context context){
        super(context);
        loopThread = new LoopThread(this);
        paint.setTextSize(50);//text size
        paint.setColor(Color.rgb(0, 200, 160));
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
        visionMarker=BitmapFactory.decodeResource(getResources(), R.drawable.grass_danger);
    }

    //pre:  "fileName" is the name of a real file containing lines of text
    //post: returns the number of lines in fileName O(n)
    public static int getFileSize(String fileName)throws IOException {
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

    //post:returns a String array of all the elements in <filename>.txt, with index 0 unused (heap) O(n)
    public ArrayList<String> readFile(){
        ArrayList<String> temp= new ArrayList<String>();
        InputStream fis;
        final StringBuffer storedString = new StringBuffer();

        try {
            switch(level) {
                case 1:fis = getResources().openRawResource(R.raw.level_1);
                    break;
                case 2: fis = getResources().openRawResource(R.raw.level_2);
                    break;
                default : fis = getResources().openRawResource(R.raw.level_1);
                    break;
            }
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

    //pre: dir is greater than 0 and less than 4
    //post: moves the player in the direction specified by dir
    private void movePlayer(int dir){
        switch(dir){
            case 0://move up
                if(playerLocation[0]-1>=0)
                    if (boardStatic.get(playerLocation[0] - 1, playerLocation[1]) == null) {
                        playerLocation[0]=playerLocation[0]-1;
                    }
                    else
                    if(boardStatic.get(playerLocation[0] - 1, playerLocation[1]).getWalkable()){
                        if(boardStatic.get(playerLocation[0] - 1, playerLocation[1]).getType()==1){
                            candy=true;
                            boardStatic.remove(playerLocation[0] - 1, playerLocation[1]);
                        }
                        playerLocation[0]=playerLocation[0]-1;
                    }
                break;
            case 1://move left
                if(playerLocation[1]-1>=0)
                    if (boardStatic.get(playerLocation[0], playerLocation[1]-1) == null) {
                        playerLocation[1]=playerLocation[1]-1;
                    }
                    else
                    if(boardStatic.get(playerLocation[0], playerLocation[1]-1).getWalkable()){
                        if(boardStatic.get(playerLocation[0], playerLocation[1]-1).getType()==1){
                            candy=true;
                            boardStatic.remove(playerLocation[0], playerLocation[1]-1);
                        }
                        playerLocation[1]=playerLocation[1]-1;
                    }
                break;
            case 2://move right
                if(playerLocation[1]+1<=19)
                    if (boardStatic.get(playerLocation[0], playerLocation[1]+1) == null) {
                        playerLocation[1]=playerLocation[1]+1;
                    }
                    else
                    if(boardStatic.get(playerLocation[0], playerLocation[1]+1).getWalkable()){
                        if(boardStatic.get(playerLocation[0], playerLocation[1]+1).getType()==1){
                            candy=true;
                            boardStatic.remove(playerLocation[0], playerLocation[1]+1);
                        }
                        playerLocation[1]=playerLocation[1]+1;
                    }
                break;
            case 3://move down
                if(playerLocation[0]+1<=19)
                    if (boardStatic.get(playerLocation[0] + 1, playerLocation[1]) == null) {
                        playerLocation[0]=playerLocation[0]+1;
                    }
                    else
                    if(boardStatic.get(playerLocation[0] + 1, playerLocation[1]).getWalkable()){
                        if(boardStatic.get(playerLocation[0] + 1, playerLocation[1]).getType()==1){
                            candy=true;
                            boardStatic.remove(playerLocation[0] + 1, playerLocation[1]);
                        }
                        playerLocation[0]=playerLocation[0]+1;
                    }
                break;
            default://gets sent an invalid number
                break;
        }
        paint.setColor(Color.rgb(0, 200, 160));//after every move change paint to the default color
        checkVision();//update the current vision
        if(enemyVision[playerLocation[0]][playerLocation[1]]==1){//check if the player is within the vision of an enemy
            playerHealth--;
            paint.setColor(Color.rgb(256, 0, 0));//make text color red
            if(playerHealth<=0){
                gameState=2;//send to death screen
            }
        }
        if(candy){//if at home space with treasure, open next level
            int[] i=playerLocation;
            if(i[0]==playerHome[0]&&i[1]==playerHome[1]){
                paint.setColor(Color.GREEN);
                level++;
                createLevel();
            }
        }
    }

    //post: every enemy that can move moves 1 space forward form their current position
    private void move(){
        for(int r=0; r<boardStatic.numRows(); r++){
            for(int c=0; c<boardStatic.numColumns(); c++){
                if(boardStatic.get(r, c)!=null&&(boardStatic.get(r, c).getType()==6||boardStatic.get(r, c).getType()==7)) {//if Object is not null and is a soldier or a scout
                    if(!boardStatic.get(r, c).getMoved()) {
                        if (!boardStatic.get(r, c).getVertical()) {//if moving horizontally
                            if (boardStatic.get(r, c).getLeaving()) {//leaving
                                if (c < boardStatic.get(r, c).getEY()) {
                                    if (boardStatic.get(r, c+1) == null) {
                                        boardStatic.add(r, c + 1, new MobileEnemy(boardStatic.get(r, c).getXPos(), boardStatic.get(r, c).getYPos(), boardStatic.get(r, c).getType(), boardStatic.get(r, c).getVision(), true, false, false, true, true, boardStatic.get(r, c).getEX(), boardStatic.remove(r, c).getEY()));
                                        boardStatic.get(r, c+1).setMoved(true);
                                    }
                                }
                                else {
                                    boardStatic.get(r, c).setLeaving(false);
                                    if (c > boardStatic.get(r, c).getYPos()) {
                                        if (boardStatic.get(r, c-1) == null) {
                                            boardStatic.add(r, c - 1, new MobileEnemy(boardStatic.get(r, c).getXPos(), boardStatic.get(r, c).getYPos(), boardStatic.get(r, c).getType(), boardStatic.get(r, c).getVision(), true, false, false, true, false, boardStatic.get(r, c).getEX(), boardStatic.remove(r, c).getEY()));
                                            boardStatic.get(r, c-1).setMoved(true);
                                        }
                                    }
                                }
                            }
                            else{//not leaving
                                if (c > boardStatic.get(r, c).getYPos()) {
                                    if (boardStatic.get(r, c-1) == null) {
                                        boardStatic.add(r, c - 1, new MobileEnemy(boardStatic.get(r, c).getXPos(), boardStatic.get(r, c).getYPos(), boardStatic.get(r, c).getType(), boardStatic.get(r, c).getVision(), true, false, false, true, false, boardStatic.get(r, c).getEX(), boardStatic.remove(r, c).getEY()));
                                        boardStatic.get(r, c-1).setMoved(true);
                                    }
                                }
                                else {
                                    boardStatic.get(r, c).setLeaving(true);
                                    if (c < boardStatic.get(r, c).getEY()) {
                                        if (boardStatic.get(r, c+1) == null) {
                                            boardStatic.add(r, c + 1, new MobileEnemy(boardStatic.get(r, c).getXPos(), boardStatic.get(r, c).getYPos(), boardStatic.get(r, c).getType(), boardStatic.get(r, c).getVision(), true, false, false, true, true, boardStatic.get(r, c).getEX(), boardStatic.remove(r, c).getEY()));
                                            boardStatic.get(r, c+1).setMoved(true);
                                        }
                                    }
                                }
                            }
                        }
                        else{//if moving vertically
                            if (boardStatic.get(r, c).getLeaving()) {//leaving
                                if (r < boardStatic.get(r, c).getEX()) {
                                    if (boardStatic.get(r+1, c) == null) {
                                        boardStatic.add(r+1, c, new MobileEnemy(boardStatic.get(r, c).getXPos(), boardStatic.get(r, c).getYPos(), boardStatic.get(r, c).getType(), boardStatic.get(r, c).getVision(), true, false, true, true, true, boardStatic.get(r, c).getEX(), boardStatic.remove(r, c).getEY()));
                                        boardStatic.get(r+1, c).setMoved(true);
                                    }
                                }
                                else {
                                    boardStatic.get(r, c).setLeaving(false);
                                    if (r > boardStatic.get(r, c).getXPos()) {
                                        if (boardStatic.get(r-1, c) == null) {
                                            boardStatic.add(r-1, c, new MobileEnemy(boardStatic.get(r, c).getXPos(), boardStatic.get(r, c).getYPos(), boardStatic.get(r, c).getType(), boardStatic.get(r, c).getVision(), true, false, true, true, false, boardStatic.get(r, c).getEX(), boardStatic.remove(r, c).getEY()));
                                            boardStatic.get(r-1, c).setMoved(true);
                                        }
                                    }
                                }
                            }
                            else{//not leaving
                                if (r > boardStatic.get(r, c).getXPos()) {
                                    if (boardStatic.get(r-1, c) == null) {
                                        boardStatic.add(r-1, c, new MobileEnemy(boardStatic.get(r, c).getXPos(), boardStatic.get(r, c).getYPos(), boardStatic.get(r, c).getType(), boardStatic.get(r, c).getVision(), true, false, true, true, false, boardStatic.get(r, c).getEX(), boardStatic.remove(r, c).getEY()));
                                        boardStatic.get(r-1, c).setMoved(true);
                                    }
                                }
                                else {
                                    boardStatic.get(r, c).setLeaving(true);
                                    if (r < boardStatic.get(r, c).getEX()) {
                                        if (boardStatic.get(r+1, c) == null) {
                                            boardStatic.add(r+1, c, new MobileEnemy(boardStatic.get(r, c).getXPos(), boardStatic.get(r, c).getYPos(), boardStatic.get(r, c).getType(), boardStatic.get(r, c).getVision(), true, false, true, true, true, boardStatic.get(r, c).getEX(), boardStatic.remove(r, c).getEY()));
                                            boardStatic.get(r+1, c).setMoved(true);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        for(int r=0; r<boardStatic.numRows(); r++){//change all of the moved booleans to false to allow for future movement
            for(int c=0; c<boardStatic.numColumns(); c++) {
                if(boardStatic.get(r, c)!=null&&(boardStatic.get(r, c).getType()==6||boardStatic.get(r, c).getType()==7)){
                    boardStatic.get(r, c).setMoved(false);
                }
            }
        }
    }

    //post: fill the board with all of the WorldObjects for a given level
    private void createLevel() {
        int x, y, t, v, ex, ey, tX, tY;//x=X index, y= Y index, t=Type, v=Vision, ex=endingX position, ey=endingY Position
        boolean e, w, vert;//e=Enemy, w=Walkable, vert=vertical
        //boolean mobiles = true;//tells the loop if it should be reading in WorldObjects or MobileEnemys
        ArrayList<String> temp = readFile(/*"level_"+level+".txt"*/);
        boardStatic.clear();//clear the board so the board is ready for a new level
        if (temp != null) {
            playerLocation=new int[]{Integer.parseInt(temp.get(1).substring(0, 2)), Integer.parseInt(temp.get(1).substring(2))};
            for (int i = 2; i < temp.size(); i++) {//define all the variables then add the Object to the sparseMatrix
                x = Integer.parseInt(temp.get(i).substring(0, 2));
                y = Integer.parseInt(temp.get(i).substring(2, 4));
                t = Integer.parseInt(temp.get(i).substring(4, 5));
                v = Integer.parseInt(temp.get(i).substring(5, 6));
                e=false;
                if(temp.get(i).substring(6, 7).equals("T"))
                    e=true;
                w=false;
                if(temp.get(i).substring(7, 8).equals("T"))
                    w=true;
                vert=false;
                if(temp.get(i).substring(8, 9).equals("T"))
                    vert=true;
                ex = Integer.parseInt(temp.get(i).substring(9, 11));
                ey = Integer.parseInt(temp.get(i).substring(11));
                if(x>ex) {//if the final movement location is lower than the starting location, change the variables so movement works
                    tX=x;
                    tY=y;
                    x=ex;
                    y=ey;
                    ex=tX;
                    ey=tY;
                    boardStatic.add(tX, tY, (new MobileEnemy(x, y, t, v, e, w, vert, true, true, ex, ey)));//add the MobileEnemy to the board
                }
                else
                if(y>ey) {//if the final movement location is lower than the starting location, change the variables so movement works
                    tX=x;
                    tY=y;
                    x=ex;
                    y=ey;
                    ex=tX;
                    ey=tY;
                    boardStatic.add(tX, tY, (new MobileEnemy(x, y, t, v, e, w, vert, true, true, ex, ey)));//add the MobileEnemy to the board
                }
                else//normal condition
                    boardStatic.add(x, y, (new MobileEnemy(x, y, t, v, e, w, vert, true, true, ex, ey)));//add the MobileEnemy to the board
            }
        }
        candy=false;
        playerHome=playerLocation.clone();
        checkVision();
    }

    //post: if the player is found in the vision of any of the enemies, then changes the colour of the text to white
    private void checkVision(){
        enemyVision=new int[20][20];//refresh the enemyVision array
        for(int r=0; r<boardStatic.numRows(); r++) {
            for (int c = 0; c < boardStatic.numColumns(); c++) {
                if(boardStatic.get(r, c)!=null&&(boardStatic.get(r, c).getEnemy())){//if is an enemy
                    for(int R=r-boardStatic.get(r, c).getVision(); R<=r+boardStatic.get(r, c).getVision(); R++){//for every index of boardStatic
                        for(int C=c-boardStatic.get(r, c).getVision(); C<=c+boardStatic.get(r, c).getVision(); C++){
                            if(R>=0&&R<=19){//if R is a valid index
                                if(C>=0&&C<=19){//if C is a valid index
                                    enemyVision[R][C]=1;//mark this spot as within enemy vision
                                }
                            }
                        }
                    }
                }
            }
        }
    }

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
        } else {
            if (gameState == 1) {
                if (canvasWidth == 0) {
                    canvasWidth = canvas.getWidth();
                    createScreen();
                }
                if (!levelStarted) {
                    createLevel();
                    levelStarted = !levelStarted;
                }
                for (int r = 0; r < board.length; r++) {
                    for (int c = 0; c < board[0].length; c++) {
                        if (boardStatic.get(r + viewX, c + viewY) != null) {
                            switch (boardStatic.get(r + viewX, c + viewY).getType()) {
                                case 0:
                                    canvas.drawBitmap(grassPic, null, board[r][c], null);//draw grass
                                    if(enemyVision[r+viewX][c+viewY]==1){
                                        canvas.drawBitmap(visionMarker, null, board[r][c], null);//draw vision marker
                                    }
                                    canvas.drawBitmap(playerPic, null, board[r][c], null);//draw Player
                                    break;
                                case 1:
                                    canvas.drawBitmap(grassPic, null, board[r][c], null);//draw grass
                                    if(enemyVision[r+viewX][c+viewY]==1){
                                        canvas.drawBitmap(visionMarker, null, board[r][c], null);//draw vision marker
                                    }
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
                            if(enemyVision[r+viewX][c+viewY]==1){
                                canvas.drawBitmap(visionMarker, null, board[r][c], null);//draw vision marker
                            }
                        }
                    }
                }
                if((playerLocation[0]>=viewX&&playerLocation[0]<viewX+8)&&(playerLocation[1]>=viewY&&playerLocation[1]<viewY+8)) {//player is within bounds of the screen
                    if(playerLocation[0]<8&&playerLocation[1]<8){
                        canvas.drawBitmap(playerPic, null, board[playerLocation[0]][playerLocation[1] ], null);//draw Player
                    }
                    canvas.drawBitmap(playerPic, null, board[(playerLocation[0]-viewX)][(playerLocation[1]-viewY)], null);//draw Player
                }
                canvas.drawBitmap(upArrowPic, null, upButton, null);
                canvas.drawBitmap(downArrowPic, null, downButton, null);
                canvas.drawBitmap(leftArrowPic, null, leftButton, null);
                canvas.drawBitmap(rightArrowPic, null, rightButton, null);
                canvas.drawBitmap(upArrowPic, null, upScreenButton, null);
                canvas.drawBitmap(downArrowPic, null, downScreenButton, null);
                canvas.drawBitmap(leftArrowPic, null, leftScreenButton, null);
                canvas.drawBitmap(rightArrowPic, null, rightScreenButton, null);
                canvas.drawText("x: " + viewX + " Y: " + viewY + " Health: " + playerHealth, (canvas.getWidth() / 2) - 100, 50, paint);//print screen location
                canvas.drawBitmap(arinPic, null, moveButton, null);
                canvas.drawRect(musicButton, paint);
            } else {
                if (gameState == 2) {
                    canvas.drawRect(new RectF(0, 0, canvas.getWidth(), canvas.getHeight()), new Paint(Color.BLACK));
                    canvas.drawText("You have died", (canvas.getWidth() / 2) - 100, 300, paint);
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (System.currentTimeMillis() - lastClick > 150) {//prevents spam tapping and accidentally pressing multiple times based on dragging
            lastClick = System.currentTimeMillis();
            float x = event.getX();//x location of touch
            float y = event.getY();//y location of touch
            if (gameState == 0) {
                gameState=1;//"start" game
            }
            if (gameState == 1) {
                synchronized (getHolder()) {
                    if (upButton.contains(x, y)) {//move player up and move enemies 1
                        move();
                        movePlayer(0);
                    } else if (downButton.contains(x, y)) {//move player down and move enemies 1
                        move();
                        movePlayer(3);
                    } else if (rightButton.contains(x, y)) {//move player right and move enemies 1
                        move();
                        movePlayer(2);
                    } else if (leftButton.contains(x, y)) {//move player left and move enemies 1
                        move();
                        movePlayer(1);
                    }
                    if (upScreenButton.contains(x, y)) {//move screen up 2
                        if (viewX > 0) {
                            viewX -= 2;
                        }
                    } else if (downScreenButton.contains(x, y)) {//move screen down 2
                        if (viewX < 12)
                            viewX += 2;
                    } else if (rightScreenButton.contains(x, y)) {//move screen right 2
                        if (viewY < 12) {
                            viewY += 2;
                        }
                    } else if (leftScreenButton.contains(x, y))//move screen left 2
                        if (viewY > 0)
                            viewY -= 2;
                    if (moveButton.contains(x, y)) {
                        gameState=2;
                    }
                    if (musicButton.contains(x, y)) {//play/pause the music
                        if (bavariaOn) {//pause if on
                            bavariaOn = false;
                            fluteSong.pause();
                        } else {//play if off
                            bavariaOn = true;
                            fluteSong.start();
                        }
                    }
                }
            }
            if(gameState==2){
                if(musicButton.contains(x, y)){
                    gameState=1;
                }
            }
        }
        return true;
    }
}