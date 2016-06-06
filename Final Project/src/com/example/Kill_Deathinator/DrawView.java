package com.example.Kill_Deathinator;//add instructions
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
    private Bitmap rightArrowPic;//right arrow
    private Bitmap leftArrowPic;//left arrow
    private Bitmap upArrowPic;//up arrow
    private Bitmap downArrowPic;//down arrow
    private Bitmap moveIndicatorPic;//move indicator
    private Bitmap screenIndicatorPic;//screen movement indicator
    private Bitmap visionMarker;//shows user where the enemies can see
    private boolean bavariaOn = false;//keeping track of if the song is playing
    private RectF upScreenButton;//up button for the screen
    private RectF leftScreenButton;//left button for the screen
    private RectF downScreenButton;//down button for the screen
    private RectF rightScreenButton;//right button for the screen
    private RectF middleScreenButton;//button to go in the middle of the arrows
    private RectF upButton;//up button
    private RectF leftButton;//left button
    private RectF downButton;//down button
    private RectF rightButton;//right button
    private RectF middleButton;//button to go in the middle of the arrows
    private boolean moving=false;//control if the enemies are moving or not
    private int viewX;//lowest box shown on screen
    private int viewY;//lowest box shown on screen
    private RectF[][] board=new RectF[8][8];//array list holding the boxes to draw objects onto
    private RectF musicButton=new RectF(200, 1200, 300, 1300);//button to turn music on and off
    private int canvasWidth=0;//records width of canvas for scaling size of boxes
    private boolean levelStarted=false;//keeps track of if a level is currently going
    private int level=1;//level currently on
    private RectF moveButton=new RectF(200, 1300, 300, 1400);//button to move Objects
    private int gameState=0;//0=start screen, 1=map, 2=death, 3=fight, 4=victory
    private int[] playerHome = new int[2];//location of player's home
    private int[] playerLocation=new int[2];//location of player
    private boolean candy=false;//whether or not the player has obtained the treasure for current level
    private int playerHealth=20;//health
    private int[][] enemyVision = new int[20][20];//array to allow the enemy's vision to be displayed on the board for ease of use
    private RectF fullscreenBox;//box for use of drawing the entire screen
    private int[] enemyLocation=new int[2];//used to store the location of the enemy being fought

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
        visionMarker=BitmapFactory.decodeResource(getResources(), R.drawable.warning_tile);
        moveIndicatorPic=BitmapFactory.decodeResource(getResources(), R.drawable.move_button);
        screenIndicatorPic=BitmapFactory.decodeResource(getResources(), R.drawable.screen_button);
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
        ArrayList<String> temp= new ArrayList<String>();//will hold all of the found Strings
        InputStream fis;
        final StringBuffer storedString = new StringBuffer();

        try {//change which file based on current number of level
            switch(level) {
                case 1:fis = getResources().openRawResource(R.raw.level_1);//load level 1
                    break;
                case 2: fis = getResources().openRawResource(R.raw.level_2);//load level 2
                    break;
                case 3: fis = getResources().openRawResource(R.raw.level_3);//load level 3
                    break;
                default : fis = getResources().openRawResource(R.raw.level_1);//load level 1 if the player has beaten the game
                    break;
            }
            DataInputStream dataIO = new DataInputStream(fis);
            String strLine = null;

            while ((strLine = dataIO.readLine()) != null) {//add line to the arrayList
                temp.add(strLine);
            }

            dataIO.close();
            fis.close();//close file
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
                    if (boardStatic.get(playerLocation[0] - 1, playerLocation[1]) == null) {//if moving to an empty location
                        playerLocation[0]=playerLocation[0]-1;//update player location
                    }
                    else {//if moving to a location with an object
                        if (boardStatic.get(playerLocation[0] - 1, playerLocation[1]).getWalkable()) {
                            if (boardStatic.get(playerLocation[0] - 1, playerLocation[1]).getType() == 1) {
                                candy = true;//mark that treasure has been obtained
                                boardStatic.remove(playerLocation[0] - 1, playerLocation[1]);//remove treasure
                            }
                            playerLocation[0] = playerLocation[0] - 1;//update player location
                        } else {
                            enemyLocation[0]=playerLocation[0]-1;
                            enemyLocation[1]=playerLocation[1];
                            gameState=3;//start fight
                        }
                    }
                break;
            case 1://move left
                if(playerLocation[1]-1>=0)
                    if (boardStatic.get(playerLocation[0], playerLocation[1]-1) == null) {//if moving to an empty location
                        playerLocation[1]=playerLocation[1]-1;//update player location
                    }
                    else{//if moving to a location with an object
                        if(boardStatic.get(playerLocation[0], playerLocation[1]-1).getWalkable()){
                            if(boardStatic.get(playerLocation[0], playerLocation[1]-1).getType()==1){
                                candy=true;//mark that treasure has been obtained
                                boardStatic.remove(playerLocation[0], playerLocation[1]-1);//remove treasure
                            }
                            playerLocation[1]=playerLocation[1]-1;//update player location
                        } else {
                            enemyLocation[0] = playerLocation[0];
                            enemyLocation[1] = playerLocation[1] - 1;
                            gameState = 3;//start fight
                        }
                    }
                break;
            case 2://move right
                if(playerLocation[1]+1<=19)
                    if (boardStatic.get(playerLocation[0], playerLocation[1]+1) == null) {//if moving to an empty location
                        playerLocation[1]=playerLocation[1]+1;//update player location
                    }
                    else {//if moving to a location with an object
                        if (boardStatic.get(playerLocation[0], playerLocation[1] + 1).getWalkable()) {
                            if (boardStatic.get(playerLocation[0], playerLocation[1] + 1).getType() == 1) {
                                candy = true;//mark that treasure has been obtained
                                boardStatic.remove(playerLocation[0], playerLocation[1] + 1);//remove treasure
                            }
                            playerLocation[1] = playerLocation[1] + 1;//update player location
                        } else {
                            enemyLocation[0] = playerLocation[0];
                            enemyLocation[1] = playerLocation[1] + 1;
                            gameState = 3;//start fight
                        }
                    }
                break;
            case 3://move down
                if(playerLocation[0]+1<=19)
                    if (boardStatic.get(playerLocation[0] + 1, playerLocation[1]) == null) {//if moving to an empty location
                        playerLocation[0]=playerLocation[0]+1;//update player location
                    }
                    else {//if moving to a location with an object
                        if (boardStatic.get(playerLocation[0] + 1, playerLocation[1]).getWalkable()) {
                            if (boardStatic.get(playerLocation[0] + 1, playerLocation[1]).getType() == 1) {
                                candy = true;//mark that treasure has been obtained
                                boardStatic.remove(playerLocation[0] + 1, playerLocation[1]);//remove treasure
                            }
                            playerLocation[0] = playerLocation[0] + 1;//update player location
                        } else {
                            enemyLocation[0] = playerLocation[0] + 1;
                            enemyLocation[1] = playerLocation[1];
                            gameState = 3;//start fight
                        }
                    }

                break;
            default://gets sent an invalid number
                break;
        }
        paint.setColor(Color.rgb(0, 200, 160));//after every move change paint to the default color
        checkVision();//update the current vision
        if(enemyVision[playerLocation[0]][playerLocation[1]]==1&&(boardStatic.get(playerLocation[0], playerLocation[1])==null)){//check if the player is within the vision of an enemy
            playerHealth--;
            paint.setColor(Color.rgb(256, 0, 0));//make text color red
            if(playerHealth<=0){
                gameState=2;//send to death screen
            }
        }
        if(candy){//if at home space with treasure, open next level
            int[] i=playerLocation;//
            if(i[0]==playerHome[0]&&i[1]==playerHome[1]){//if player has beaten level
                if(level<4) {
                    paint.setColor(Color.GREEN);//change color to alert player of victory
                    level++;//up level
                    createLevel();//create the next level
                }
                else{
                    level=4;
                    gameState=10;//set to victory screen
                }
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
                x = Integer.parseInt(temp.get(i).substring(0, 2));//x cord
                y = Integer.parseInt(temp.get(i).substring(2, 4));//y cord
                t = Integer.parseInt(temp.get(i).substring(4, 5));//type of object
                v = Integer.parseInt(temp.get(i).substring(5, 6));//range of vision
                e=false;//set enemy to false by default
                if(temp.get(i).substring(6, 7).equals("T"))
                    e=true;//set enemy to true
                w=false;//set walkable to false by default
                if(temp.get(i).substring(7, 8).equals("T"))
                    w=true;//set walkable to true
                vert=false;//set vertical to false by default
                if(temp.get(i).substring(8, 9).equals("T"))
                    vert=true;//set vertical to true
                ex = Integer.parseInt(temp.get(i).substring(9, 11));//ending x location if they move
                ey = Integer.parseInt(temp.get(i).substring(11));//ending y location if they move
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
        candy=false;//has not obtained treasuse for new level
        playerHome=playerLocation.clone();//update player home
        checkVision();//reload the vision for the enemies to display on board
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

    //pre: canvas needs to be the valid canvas of the device
    //post: creates and recreates objects required for the game to be shown on screen
    private void createScreen(Canvas canvas){
        for (int r = 0; r < board.length; r++) {//create the 8x8 array of the screen
            for (int c = 0; c < board[0].length; c++) {
                board[r][c] = new RectF(canvasWidth / 8 * c, canvasWidth / 8 * r, canvasWidth / 8 * (c + 1), canvasWidth / 8 * (r + 1));//create board based on width
            }
        }
        int canvasWidth=canvas.getWidth();
        int canvasHeight=canvas.getHeight();
        fullscreenBox=new RectF(0, 0, canvas.getWidth(), canvas.getHeight());//create the box that is used to cover the whole screen

        //recreate all of the buttons to be scaled to the screen
        upButton=new RectF(canvasWidth/12+(canvasWidth/12*7), canvasHeight-(canvasWidth/12*5), canvasWidth/12+(canvasWidth/12*8), canvasHeight-(canvasWidth/12*4));
        downButton=new RectF(canvasWidth/12+(canvasWidth/12*7), canvasHeight-(canvasWidth/12*3), canvasWidth/12+(canvasWidth/12*8), canvasHeight-(canvasWidth/12*2));
        leftButton=new RectF(canvasWidth/12+(canvasWidth/12*6), canvasHeight-(canvasWidth/12*4), canvasWidth/12+(canvasWidth/12*7), canvasHeight-(canvasWidth/12*3));
        rightButton=new RectF(canvasWidth/12+(canvasWidth/12*8), canvasHeight-(canvasWidth/12*4), canvasWidth/12+(canvasWidth/12*9), canvasHeight-(canvasWidth/12*3));
        middleButton=new RectF(canvasWidth/12+(canvasWidth/12*7), canvasHeight-(canvasWidth/12*4), canvasWidth/12+(canvasWidth/12*8), canvasHeight-(canvasWidth/12*3));
        upScreenButton=new RectF(canvasWidth/12+(canvasWidth/12*2), canvasHeight-(canvasWidth/12*5), canvasWidth/12+(canvasWidth/12*3), canvasHeight-(canvasWidth/12*4));
        downScreenButton=new RectF(canvasWidth/12+(canvasWidth/12*2), canvasHeight-(canvasWidth/12*3), canvasWidth/12+(canvasWidth/12*3), canvasHeight-(canvasWidth/12*2));
        leftScreenButton=new RectF(canvasWidth/12+(canvasWidth/12), canvasHeight-(canvasWidth/12*4), canvasWidth/12+(canvasWidth/12*2), canvasHeight-(canvasWidth/12*3));
        rightScreenButton=new RectF(canvasWidth/12+(canvasWidth/12*3), canvasHeight-(canvasWidth/12*4), canvasWidth/12+(canvasWidth/12*4), canvasHeight-(canvasWidth/12*3));
        middleScreenButton=new RectF(canvasWidth/12+(canvasWidth/12*2), canvasHeight-(canvasWidth/12*4), canvasWidth/12+(canvasWidth/12*3), canvasHeight-(canvasWidth/12*3));
        musicButton=new RectF(canvasWidth/12+(canvasWidth/12), canvasHeight-(canvasWidth/12), canvasWidth/12+(canvasWidth/12*2), canvasHeight-(canvasWidth/12*2));
    }

    private String enemyTurn(MobileEnemy enemy){
        return "";//temp
    }

    private String playerTurn(MobileEnemy enemy){
        return "";//temp
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(gameState==0) {//start screen
            if (canvasWidth == 0) {//if the canvas' width has not been defined, set it
                canvasWidth = canvas.getWidth();
                createScreen(canvas);//make the screen
            }
            canvas.drawRect(fullscreenBox, paint2);//draw the box across whole screen covering whatever was there before
            canvas.drawText("This is the Start Screen", (canvas.getWidth() / 2) - 100, 300, paint);
        } else {
            if (gameState == 1) {//on map
                if (!levelStarted) {//if the level has not been created yet
                    createLevel();
                    levelStarted = !levelStarted;
                }
                canvas.drawRect(fullscreenBox, paint2);//draw the box across whole screen covering whatever was there before
                for (int r = 0; r < board.length; r++) {//draw all objects on map within the screen
                    for (int c = 0; c < board[0].length; c++) {
                        if (boardStatic.get(r + viewX, c + viewY) != null) {
                            switch (boardStatic.get(r + viewX, c + viewY).getType()) {
                                case 0://player (unused)
                                    canvas.drawBitmap(grassPic, null, board[r][c], null);//draw grass
                                    if(enemyVision[r+viewX][c+viewY]==1){
                                        canvas.drawBitmap(visionMarker, null, board[r][c], null);//draw vision marker
                                    }
                                    canvas.drawBitmap(playerPic, null, board[r][c], null);//draw Player
                                    break;
                                case 1://treasure
                                    canvas.drawBitmap(grassPic, null, board[r][c], null);//draw grass
                                    if(enemyVision[r+viewX][c+viewY]==1){
                                        canvas.drawBitmap(visionMarker, null, board[r][c], null);//draw vision marker
                                    }
                                    canvas.drawBitmap(treasurePic, null, board[r][c], null);//drawTreasure
                                    break;
                                case 2://tree
                                    canvas.drawBitmap(grassPic, null, board[r][c], null);//draw grass
                                    canvas.drawBitmap(treePic, null, board[r][c], null);//draw Tree
                                    break;
                                case 3://archer
                                    canvas.drawBitmap(grassPic, null, board[r][c], null);//draw grass
                                    canvas.drawBitmap(archerPic, null, board[r][c], null);//draw Archer
                                    break;
                                case 5://tree and player (unused)
                                    canvas.drawBitmap(grassPic, null, board[r][c], null);//draw grass
                                    canvas.drawBitmap(treePic, null, board[r][c], null);//draw Tree
                                    canvas.drawBitmap(playerPic, null, board[r][c], null);//draw Player
                                    break;
                                case 6://soldier
                                    canvas.drawBitmap(grassPic, null, board[r][c], null);//draw grass
                                    canvas.drawBitmap(soldierPic, null, board[r][c], null);//draw Soldier
                                    break;
                                case 7://scout
                                    canvas.drawBitmap(grassPic, null, board[r][c], null);//draw grass
                                    canvas.drawBitmap(scoutPic, null, board[r][c], null);//draw Scout
                                    break;
                                default://if there is a incorrect number(should never appear)
                                    canvas.drawBitmap(background_1, null, board[r][c], null);//draw MacNabb
                                    break;
                            }
                        } else {//draw grass on every space with no object
                            canvas.drawBitmap(grassPic, null, board[r][c], null);//draw grass
                            if(enemyVision[r+viewX][c+viewY]==1){
                                canvas.drawBitmap(visionMarker, null, board[r][c], null);//draw vision marker
                            }
                        }
                    }
                }
                if((playerLocation[0]>=viewX&&playerLocation[0]<viewX+8)&&(playerLocation[1]>=viewY&&playerLocation[1]<viewY+8)) {//player is within bounds of the screen
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
                canvas.drawBitmap(screenIndicatorPic, null, middleScreenButton, null);
                canvas.drawBitmap(moveIndicatorPic, null, middleButton, null);
                canvas.drawText("x: " + viewX + " Y: " + viewY + " Health: " + playerHealth, (canvas.getWidth() / 2) - 100, 50, paint);//print screen location
                //canvas.drawBitmap(arinPic, null, moveButton, null);
                canvas.drawRect(musicButton, paint);
            } else {
                if (gameState == 2) {//death screen
                    canvas.drawRect(fullscreenBox, paint2);//draw the box across whole screen covering whatever was there before
                    canvas.drawText("You have died", (canvas.getWidth() / 2) - 100, 300, paint);//text to let player know this is death screen
                    canvas.drawRect(musicButton, paint);
                }
                else {
                    if (gameState == 3) {//fight screen
                        canvas.drawRect(fullscreenBox, paint2);//draw the box across whole screen covering whatever was there before
                        if(boardStatic.get(enemyLocation[0], enemyLocation[1])!=null){
                            switch(boardStatic.get(enemyLocation[0], enemyLocation[1]).getType()){
                                case 3://archer
                                    canvas.drawBitmap(grassPic, null, board[4][2], null);//draw grass
                                    canvas.drawBitmap(archerPic, null, board[4][2], null);//draw Archer
                                    break;
                                case 6://soldier
                                    canvas.drawBitmap(grassPic, null, board[4][2], null);//draw grass
                                    canvas.drawBitmap(soldierPic, null, board[4][2], null);//draw Soldier
                                    break;
                                case 7://scout
                                    canvas.drawBitmap(grassPic, null, board[4][2], null);//draw grass
                                    canvas.drawBitmap(scoutPic, null, board[4][2], null);//draw Scout
                                    break;
                                default://if there is a incorrect number(should never appear)
                                    canvas.drawBitmap(background_1, null, board[4][2], null);//draw MacNabb
                                    break;
                            }
                        }
                        canvas.drawBitmap(grassPic, null, board[4][6], null);//draw grass
                        canvas.drawBitmap(playerPic, null, board[4][6], null);//draw Player
                    } else {
                        if (gameState == 4) {//victory screen
                            canvas.drawRect(fullscreenBox, paint2);//draw the box across whole screen covering whatever was there before
                            canvas.drawText("Victory, click to restart", (canvas.getWidth() / 4), (canvas.getHeight() / 2) - 100, paint);//text to say you won
                            canvas.drawRect(musicButton, paint);
                        }
                    }
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
                gameState = 1;//"start" game
            } else {
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
                        if (musicButton.contains(x, y)) {
                            gameState = 3;
                        }
                        /*if (musicButton.contains(x, y)) {//play/pause the music
                            if (bavariaOn) {//pause if on
                                bavariaOn = false;
                                fluteSong.pause();
                            } else {//play if off
                                bavariaOn = true;
                                fluteSong.start();
                            }
                        }*/
                    }
                } else {
                    if (gameState == 2) {//death screen
                        if (musicButton.contains(x, y)) {//restart game
                            gameState = 0;//start screen
                            playerHealth=20;//refresh health
                            level=1;
                            createLevel();//create level 1 again
                            viewX=12;//reset view
                            viewY=12;//reset view
                        }
                    }
                    else{
                        if(gameState==3){//fight screen
                            if(musicButton.contains(x, y)){
                                gameState=1;
                            }
                        }
                        else{
                            if(gameState==4) {//victory screen
                                if (musicButton.contains(x, y)) {//restart game
                                    gameState = 0;//start screen
                                    playerHealth = 20;//refresh health
                                    level = 1;
                                    createLevel();//create level 1 again
                                    viewX = 12;//reset view
                                    viewY = 12;//reset view
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }
}