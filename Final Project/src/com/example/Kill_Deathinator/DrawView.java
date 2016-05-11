package com.example.Kill_Deathinator;
import android.content.Context;
import android.graphics.*;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by 1568630 on 4/27/2016.
 */
public class DrawView extends SurfaceView {
    private SparseMatrix<StaticObject> boardStatic = new SparseMatrix<StaticObject>(20, 20);
    private MediaPlayer bavaria;//media files of background music
    private LoopThread loopThread;
    private List<Sprite> sprites = new ArrayList<Sprite>();//enemies, angel, dead creatures
    private long lastClick;//keeping track of last click
    private Vibrator vibrator;//vibrator
    private Paint paint = new Paint();//paint used for text color
    private Bitmap background_1;//level 1 background image
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
    private int canvasWidth=0;
    private boolean levelStarted=false;

    //post: creates a the dialogue for a new level, death, vulnerability

    public DrawView(Context context) {
        super(context);
        loopThread = new LoopThread(this);
        paint.setTextSize(50);//text size
        paint.setColor(Color.rgb(0, 200, 160));//set colour to a blood red-ish type colour
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

        background_1 = BitmapFactory.decodeResource(getResources(), R.drawable.nab_bear_man);//instantiate the first backgound
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);//instantiate the vibrator
        bavaria = MediaPlayer.create(context, R.raw.meanwhile_in_bavaria);
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

    private void createLevel() {
        boardStatic.add(17, 17, (new StaticObject(0, false, false)));
        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board[0].length; c++) {
                board[r][c] = new RectF(canvasWidth / 8 * c, canvasWidth / 8 * r, canvasWidth / 8 * (c + 1), canvasWidth / 8 * (r + 1));
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(canvasWidth==0)
            canvasWidth=canvas.getWidth();
        if(!levelStarted){
            createLevel();
        }
        canvas.drawBitmap(background_1, 0, 0, paint);
        for(int r=0; r<board.length; r++){
            for(int c=0; c<board[0].length; c++){
                canvas.drawRect(board[r][c], paints[r+viewX][c+viewY]);
            }
        }
        canvas.drawRect(upButton, paint);
        canvas.drawRect(leftButton, paint);
        canvas.drawRect(downButton, paint);
        canvas.drawRect(rightButton, paint);
        canvas.drawText("x: "+viewX+" Y: "+viewY, (canvas.getWidth() / 2) - 100, 50, paint);//print screen location
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (System.currentTimeMillis() - lastClick > 150) {//prevents spam tapping and accidentally pressing multiple times based on dragging
            if(!bavariaOn) {
                bavaria.start();
                bavariaOn=true;
            }
            lastClick = System.currentTimeMillis();
            float x = event.getX();
            float y = event.getY();

            synchronized (getHolder()) {
                if (upButton.contains(x, y)) {
                    if(viewY>0) {
                        viewY -= 2;
                    }
                }
                else
                if(downButton.contains(x, y)){
                    if(viewY<12)
                        viewY+=2;
                }
                else
                if(rightButton.contains(x, y)){
                    if(viewX<12){
                        viewX+=2;
                    }
                }
                else
                if(leftButton.contains(x, y))
                    if(viewX>0)
                        viewX-=2;
            }
        }
        return true;
    }
}