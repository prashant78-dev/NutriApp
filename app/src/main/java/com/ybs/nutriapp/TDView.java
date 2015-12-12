package com.ybs.nutriapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class TDView extends SurfaceView implements Runnable {
    private static Random randomGenerator;
    //For the FX
    private SoundPool soundPool;
    int start = -1;
    int bump = -1;
    int destroyed = -1;
    int win = -1;
    private boolean gameEnded;

    private Context context;

    private int screenX;
    private int screenY;

    private float distanceRemaining;
    private long timeTaken;
    private long timeStarted;
    private long fastestTime;

    volatile boolean playing;
    Thread gameThread = null;
    Object lock = new Object();


    // For drawing
    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder ourHolder;

    // For saving and loading the high score
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private List levelsDisplayed = new ArrayList();
    private int levelRunning = 0;
    private List<Bag> itemsToBeDisplayed = new ArrayList<>();
    private List<Bag> itemsDisplayed = new ArrayList<>();
    private List<FoodItem> selectedItems = new ArrayList<FoodItem>();
    private List unSelectedItems = new ArrayList();
    private String view="";
    private FoodItem itemForDetail;
    private Map<String,Button> backMap = new HashMap<>();
    private Button nextButton;
    private Button end;
    private Button replayLevelButton;
    private List<FoodItem> clickedItems = new ArrayList<>();
    LinearLayout mainLayout;
    ProgressBar pbr;
    TDView(Context context, int x, int y, ProgressBar pbr) {
        super(context);
        this.context  = context;
        DataCache.init(context);
        FoodItem.maxX=x;
        this.pbr=pbr;
        // This SoundPool is deprecated but don't worry
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC,0);
        try{
            //Create objects of the 2 required classes
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;

            //create our three fx in memory ready for use
            descriptor = assetManager.openFd("start.ogg");
            start = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("win.ogg");
            win = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("bump.ogg");
            bump = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("crash.ogg");
            destroyed = soundPool.load(descriptor, 0);


        }catch(IOException e){
            //Print an error message to the console
            Log.e("error", "failed to load sound files");
        }

        screenX = x;
        screenY = y;

        // Initialize our drawing objects
        ourHolder = getHolder();
        paint = new Paint();



        // Initialise our player ship
        //player = new PlayerShip(context, x, y);
        //enemy1 = new FoodItem(context, x, y);
        //enemy2 = new FoodItem(context, x, y);
        //enemy3 = new FoodItem(context, x, y);

        //int numSpecs = 40;

        //for (int i = 0; i < numSpecs; i++) {
        // Where will the dust spawn?
        //SpaceDust spec = new SpaceDust(x, y);
        //dustList.add(spec);
        //}

        // Load fastest time
        prefs = context.getSharedPreferences("HiScores", context.MODE_PRIVATE);
        // Initialize the editor ready
        editor = prefs.edit();
        // Load fastest time
        // if not available our highscore = 1000000
        fastestTime = prefs.getLong("fastestTime", 1000000);
        setOnGenericMotionListener(new OnGenericMotionListener() {
            @Override
            public boolean onGenericMotion(View v, MotionEvent event) {
                return false;
            }
        });
        setOnHoverListener(new OnHoverListener() {
            @Override
            public boolean onHover(View v, MotionEvent event) {
                return false;
            }
        });
        startGame();
    }

    private void startGame(){
        // Play the start sound

        try {
            soundPool.play(start, 1, 1, 0, 0, 1);
            List<Bag> bags = DataCache.getRandomBagList(1, 10);
            for (Bag bag : bags){
                GoodFoodItem gfi = bag.getGoodFoodItem();
                Class klass = R.drawable.class;
                Field fld = klass.getDeclaredField(gfi.getFileName());
                int resource_id = (Integer)fld.get(null);
                Bitmap bm = BitmapFactory.decodeResource(context.getResources(), resource_id);
                double htConversion = 1.0;
                double widthConversion = 1.0;
                if(bm.getHeight()>200) htConversion=bm.getHeight()/200.0;
                if(bm.getWidth()>200) widthConversion=bm.getWidth()/200.0;
                gfi.setHitbox(200, 200, Bitmap.createScaledBitmap(bm,
                        (int) (bm.getWidth() / Math.max(htConversion,widthConversion)),
                        (int) (bm.getHeight() / Math.max(htConversion,widthConversion)),
                        false));
                BadFoodItem bfi = bag.getBadFoodItem();
                fld = klass.getDeclaredField(bfi.getFileName());
                resource_id = (Integer)fld.get(null);
                bm = BitmapFactory.decodeResource(context.getResources(), resource_id);
                htConversion = 1.0;
                widthConversion = 1.0;
                if(bm.getHeight()>200) htConversion=bm.getHeight()/200.0;
                if(bm.getWidth()>200) widthConversion=bm.getWidth()/200.0;
                bfi.setHitbox(200, 200, Bitmap.createScaledBitmap(bm,
                        (int) (bm.getWidth() / Math.max(htConversion,widthConversion)),
                        (int) (bm.getHeight() / Math.max(htConversion,widthConversion)),
                        false));
            }
            itemsToBeDisplayed.addAll(bags);

            levelRunning=1;
            gameEnded = false;
            soundPool.play(start, 1, 1, 0, 0, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (playing) {
            //update();
            draw();
            control();
        }
    }

    private void draw() {
        if(gameEnded) return;
        if (ourHolder.getSurface().isValid()) {
            if(view.equals("Trophy")) {
                canvas = ourHolder.lockCanvas();
                canvas.drawColor(Color.argb(255, 0, 0, 0));
                canvas.drawBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.plainbackground), 0, 0, paint);
                Bitmap endBitMap = BitmapFactory.decodeResource(context.getResources(), R.drawable.trophy);
                randomGenerator = new Random();
                int randomNumber = randomGenerator.nextInt(11);

                switch (randomNumber)
                {
                    case 1:endBitMap = BitmapFactory.decodeResource(context.getResources(), R.drawable.trophy1);
                        break;
                    case 2:endBitMap = BitmapFactory.decodeResource(context.getResources(), R.drawable.trophy2);
                        break;
                    case 3:endBitMap = BitmapFactory.decodeResource(context.getResources(), R.drawable.trophy3);
                        break;
                    case 4:endBitMap = BitmapFactory.decodeResource(context.getResources(), R.drawable.trophy4);
                        break;
                    case 5:endBitMap = BitmapFactory.decodeResource(context.getResources(), R.drawable.trophy5);
                        break;
                    case 6:endBitMap = BitmapFactory.decodeResource(context.getResources(), R.drawable.trophy6);
                        break;
                    case 7:endBitMap = BitmapFactory.decodeResource(context.getResources(), R.drawable.trophy7);
                        break;
                    case 8:endBitMap = BitmapFactory.decodeResource(context.getResources(), R.drawable.trophy8);
                        break;
                    case 9:endBitMap = BitmapFactory.decodeResource(context.getResources(), R.drawable.trophy9);
                        break;
                    case 10:endBitMap = BitmapFactory.decodeResource(context.getResources(), R.drawable.trophy10);
                        break;
                    default:endBitMap = BitmapFactory.decodeResource(context.getResources(), R.drawable.trophy);
                        break;

                }

                canvas.drawBitmap(endBitMap, 400, 300, paint);
                paint.setColor(Color.argb(255, 255, 255, 255));
                paint.setTextSize(25);
                canvas.drawText("HOREEE! KAMU MENANG! TETAP PILIH MAKANAN SEHAT YAâ€¦!", 200, 50, paint);
                ourHolder.unlockCanvasAndPost(canvas);
                return;
            }
            if(view.equals("SelectedItemsView") && levelRunning>=4) {
                canvas = ourHolder.lockCanvas();
                canvas.drawBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.plainbackground), 0, 0, paint);
                canvas.drawColor(Color.argb(255, 0, 0, 0));
                Bitmap endBitMap = BitmapFactory.decodeResource(context.getResources(), R.drawable.trophy);
                randomGenerator = new Random();
                int randomNumber = randomGenerator.nextInt(11);

                switch (randomNumber)
                {
                    case 1:endBitMap = BitmapFactory.decodeResource(context.getResources(), R.drawable.trophy1);
                        break;
                    case 2:endBitMap = BitmapFactory.decodeResource(context.getResources(), R.drawable.trophy2);
                        break;
                    case 3:endBitMap = BitmapFactory.decodeResource(context.getResources(), R.drawable.trophy3);
                        break;
                    case 4:endBitMap = BitmapFactory.decodeResource(context.getResources(), R.drawable.trophy4);
                        break;
                    case 5:endBitMap = BitmapFactory.decodeResource(context.getResources(), R.drawable.trophy5);
                        break;
                    case 6:endBitMap = BitmapFactory.decodeResource(context.getResources(), R.drawable.trophy6);
                        break;
                    case 7:endBitMap = BitmapFactory.decodeResource(context.getResources(), R.drawable.trophy7);
                        break;
                    case 8:endBitMap = BitmapFactory.decodeResource(context.getResources(), R.drawable.trophy8);
                        break;
                    case 9:endBitMap = BitmapFactory.decodeResource(context.getResources(), R.drawable.trophy9);
                        break;
                    case 10:endBitMap = BitmapFactory.decodeResource(context.getResources(), R.drawable.trophy10);
                        break;
                    default:endBitMap = BitmapFactory.decodeResource(context.getResources(), R.drawable.trophy);
                        break;

                }
                end = new Button(550, 300, endBitMap);
                canvas.drawBitmap(endBitMap, end.x, end.y, paint);
                ourHolder.unlockCanvasAndPost(canvas);
                gameEnded=true;
                return;
            }
            if(view.equals("SelectedItemsView")) {
                return;
            }
            if( itemsToBeDisplayed.size()>0) {
                view="SelectionView";
            } else if(view.equals("SelectionView") || view.equals("")){
                int progress=0;
                for (FoodItem fItem : selectedItems) {
                    if(fItem.isGoodItem()) progress+=10;
                }
                pbr.setProgress(progress);
                if(view.equals("SelectionView") && pbr.getProgress()==100){
                    view="Trophy";
                    synchronized (lock) {
                        lock.notifyAll();
                    }
                    return;
                } else {
                    view = "SelectedItemsView";
                }
            }
            if(view.equals("SelectionView")) {
                canvas = ourHolder.lockCanvas();

                // Rub out the last frame
                canvas.drawColor(Color.argb(255, 0, 0, 0));

                // For debugging
                // Switch to white pixels
                paint.setColor(Color.argb(255, 255, 255, 255));
                canvas.drawBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.plainbackground), 0, 0, paint);
                // Draw Hit boxes
                //canvas.drawRect(player.getHitbox().left, player.getHitbox().top, player.getHitbox().right, player.getHitbox().bottom, paint);
                //canvas.drawRect(enemy1.getHitbox().left, enemy1.getHitbox().top, enemy1.getHitbox().right, enemy1.getHitbox().bottom, paint);
                //canvas.drawRect(enemy2.getHitbox().left, enemy2.getHitbox().top, enemy2.getHitbox().right, enemy2.getHitbox().bottom, paint);
                //canvas.drawRect(enemy3.getHitbox().left, enemy3.getHitbox().top, enemy3.getHitbox().right, enemy3.getHitbox().bottom, paint);


                // White specs of dust
                paint.setColor(Color.argb(255, 255, 255, 255));
                paint.setTextSize(25);
                // Draw the player
                Bag bag = itemsToBeDisplayed.remove(0);

                canvas.drawText("KAMU PILIH YANG MANA?", 200, 50, paint);
                if(Math.random()>0.5) {
                    bag.getGoodFoodItem().setX(300);bag.getGoodFoodItem().setY(200);
                    bag.getBadFoodItem().setX(700);bag.getBadFoodItem().setY(200);
                } else {
                    bag.getGoodFoodItem().setX(700);bag.getGoodFoodItem().setY(200);
                    bag.getBadFoodItem().setX(300);bag.getBadFoodItem().setY(200);
                }
                canvas.drawBitmap(bag.getGoodFoodItem().getBitmap(), bag.getGoodFoodItem().getX(),
                        bag.getGoodFoodItem().getY(), paint);
                String foodName = bag.getGoodFoodItem().getTransFoodName();
                if(foodName.length()>17) {
                    int breaks = foodName.length()/17;
                    int start=0;
                    int end=17;
                    int yCord=25;
                    for(int ii=0;ii<breaks;ii++) {
                        String text = foodName.substring(start, end);
                        int endPoint = Math.max(text.lastIndexOf(" "), text.lastIndexOf(","));
                        endPoint=Math.max(endPoint,text.lastIndexOf("."));
                        String displayString = foodName.substring(start,start+endPoint);
                        canvas.drawText(displayString, bag.getGoodFoodItem().getX(),
                                bag.getGoodFoodItem().getY() + bag.getGoodFoodItem().getBitmap().getHeight() + yCord, paint);
                        yCord+=25;
                        start+=endPoint;
                        end=Math.min(start+17,foodName.length());
                    }
                    canvas.drawText(foodName.substring(start, end), bag.getGoodFoodItem().getX(),
                            bag.getGoodFoodItem().getY() + bag.getGoodFoodItem().getBitmap().getHeight() + yCord, paint);
                }
                else {
                    canvas.drawText(foodName, bag.getGoodFoodItem().getX(),
                            bag.getGoodFoodItem().getY() + bag.getGoodFoodItem().getBitmap().getHeight() + 25, paint);
                }
                canvas.drawBitmap(bag.getBadFoodItem().getBitmap(), bag.getBadFoodItem().getX(),
                        bag.getBadFoodItem().getY(), paint);
                foodName = bag.getBadFoodItem().getTransFoodName();
                if(foodName.length()>17) {
                    int breaks = foodName.length()/17;
                    int start=0;
                    int end=17;
                    int yCord=25;
                    for(int ii=0;ii<breaks;ii++) {
                        String text = foodName.substring(start, end);
                        int endPoint = Math.max(text.lastIndexOf(" "), text.lastIndexOf(","));
                        endPoint=Math.max(endPoint,text.lastIndexOf("."));
                        String displayString = foodName.substring(start,start+endPoint);
                        canvas.drawText(displayString, bag.getBadFoodItem().getX(),
                                bag.getBadFoodItem().getY() + bag.getBadFoodItem().getBitmap().getHeight() + yCord, paint);
                        yCord+=25;
                        start+=endPoint;
                        end=Math.min(start+17,foodName.length());
                    }
                    canvas.drawText(foodName.substring(start, end), bag.getBadFoodItem().getX(),
                            bag.getBadFoodItem().getY() + bag.getBadFoodItem().getBitmap().getHeight() + yCord, paint);
                }
                else {
                    canvas.drawText(foodName, bag.getBadFoodItem().getX(),
                            bag.getBadFoodItem().getY() + bag.getBadFoodItem().getBitmap().getHeight() + 25, paint);
                }
                itemsDisplayed.add(bag);
                if(replayLevelButton==null){
                    Bitmap replayBitMap = BitmapFactory.decodeResource(context.getResources(), R.drawable.replaygame);
                    replayLevelButton = new Button(900, 640, Bitmap.createScaledBitmap(replayBitMap,
                            replayBitMap.getWidth(),
                            replayBitMap.getHeight(),
                            false));
                }
                /*if(!view.equals("ItemDetails") && !(levelRunning==1 && itemsDisplayed.size()==1)) {
                    canvas.drawBitmap(replayLevelButton.bitMap, replayLevelButton.x, replayLevelButton.y, paint);
                }*/

                if(!backMap.containsKey(view)) {
                    Bitmap backBitMap = BitmapFactory.decodeResource(context.getResources(), R.drawable.back_button);
                    Button backButton = new Button(800, 440, Bitmap.createScaledBitmap(backBitMap,
                            replayLevelButton.bitMap.getHeight(),
                            replayLevelButton.bitMap.getHeight(),
                            false));
                    backMap.put(view, backButton);
                }
                if(!(levelRunning==1 && itemsDisplayed.size()==1)) {
                    canvas.drawBitmap(backMap.get(view).bitMap, backMap.get(view).x, backMap.get(view).y, paint);
                }
                int progress=0;
                for (FoodItem fItem : selectedItems) {
                    if(fItem.isGoodItem()) progress+=10;
                }
                pbr.setProgress(progress);
                if (!gameEnded) {
                    // Draw the hud
                    paint.setTextAlign(Paint.Align.LEFT);
                    paint.setColor(Color.argb(255, 255, 255, 255));
                    paint.setTextSize(25);
                    //canvas.drawText("Fastest:" + fastestTime + "s", 10, 20, paint);
                } else {
                    // Show pause screen
                    paint.setTextSize(80);
                    paint.setTextAlign(Paint.Align.CENTER);
                    canvas.drawText("Game Over", screenX / 2, 100, paint);
                    paint.setTextSize(25);
                    //canvas.drawText("Fastest:"+ fastestTime + "s", screenX/2, 160, paint);
                }
                // Unlock and draw the scene
                ourHolder.unlockCanvasAndPost(canvas);

                if(pbr.getProgress()==100){
                    view="Trophy";
                    synchronized (lock) {
                        lock.notifyAll();
                    }
                    return;
                }
            }
            else if (view.equals("SelectedItemsView") || view.equals("SelectedItemsViewNew")) {
                view="SelectedItemsView";
                canvas = ourHolder.lockCanvas();
                canvas.drawColor(Color.argb(255, 0, 0, 0));
                canvas.drawBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.plainbackground), 0, 0, paint);
                int starterrX = 10;
                boolean allGood=true;

                canvas.drawText("INI ADALAH PILIHANMU. KLIK GAMBAR LAGI UNTUK MENGINGAT PILIHANMU SEHAT ATAU TIDAK.", 50, 50,
                        paint);
                int Y=100;
                for (FoodItem obj : selectedItems) {
                    if(!obj.isGoodItem()){
                        allGood=false;
                    }
                    if(obj==selectedItems.get(selectedItems.size()/2)) {
                        Y=400;starterrX=50;
                    }
                    obj.setX(starterrX);
                    obj.setY(Y);
                    canvas.drawBitmap(obj.getBitmap(), starterrX, Y, paint);
                    String foodName = obj.getTransFoodName();
                    if(foodName.length()>17) {
                        int breaks = foodName.length()/17;
                        int start=0;
                        int end=17;
                        int yCord=25;
                        for(int ii=0;ii<breaks;ii++) {
                            String text = foodName.substring(start, end);
                            int endPoint = Math.max(text.lastIndexOf(" "), text.lastIndexOf(","));
                            endPoint=Math.max(endPoint,text.lastIndexOf("."));
                            String displayString = foodName.substring(start,start+endPoint);
                            canvas.drawText(displayString, starterrX,
                                    Y + obj.getBitmap().getHeight() + yCord, paint);
                            yCord+=25;
                            start+=endPoint;
                            end=Math.min(start+17,foodName.length());
                        }
                        canvas.drawText(foodName.substring(start, end), starterrX,
                                Y + obj.getBitmap().getHeight() + yCord, paint);
                    }
                    else {
                        canvas.drawText(foodName, starterrX,
                                Y + obj.getBitmap().getHeight() + 25, paint);
                    }
                    starterrX+=200;
                }

                /*if(!backMap.containsKey(view)) {
                    Bitmap backBitMap = BitmapFactory.decodeResource(context.getResources(), R.drawable.back_button);
                    Button backButton = new Button(800, 550, Bitmap.createScaledBitmap(backBitMap,
                            replayLevelButton.bitMap.getHeight(),
                            replayLevelButton.bitMap.getHeight(),
                            false));
                    backMap.put(view, backButton);
                }*/
                //canvas.drawBitmap(backMap.get(view).bitMap, backMap.get(view).x, backMap.get(view).y, paint);
                int progress=0;
                for (FoodItem fItem : selectedItems) {
                    if(fItem.isGoodItem()) progress+=10;
                }
                pbr.setProgress(progress);
                canvas.drawBitmap(replayLevelButton.bitMap, replayLevelButton.x, replayLevelButton.y, paint);
                /*if(clickedItems.size()==5) {

                    if (allGood) {
                        Bitmap nextBitMap = BitmapFactory.decodeResource(context.getResources(), R.drawable.nextlevel);
                        nextButton = new Button(1050, 550, Bitmap.createScaledBitmap(nextBitMap,
                                nextBitMap.getWidth(),
                                nextBitMap.getHeight(),
                                false));
                        canvas.drawBitmap(nextButton.bitMap, nextButton.x, nextButton.y, paint);
                    }
                }*/
                ourHolder.unlockCanvasAndPost(canvas);
                if(view.equals("SelectedItemsView")) return;
                if(pbr.getProgress()==100){
                    view="Trophy";
                    synchronized (lock) {
                        lock.notifyAll();
                    }
                    return;
                }
            } else if(view.equals("ItemDetails")) {
                if(itemForDetail==null || itemForDetail.getBitmap()==null) return;
                canvas = ourHolder.lockCanvas();
                canvas.drawColor(Color.argb(255, 0, 0, 0));
                canvas.drawBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.plainbackground), 0, 0, paint);
                canvas.drawBitmap(itemForDetail.getBitmap(), 200, 400, paint);
                String foodName = itemForDetail.getTransFoodName();
                if(foodName.length()>17) {
                    int breaks = foodName.length()/17;
                    int start=0;
                    int end=17;
                    int yCord=25;
                    for(int ii=0;ii<breaks;ii++) {
                        String text = foodName.substring(start, end);
                        int endPoint = Math.max(text.lastIndexOf(" "), text.lastIndexOf(","));
                        endPoint=Math.max(endPoint,text.lastIndexOf("."));
                        String displayString = foodName.substring(start,start+endPoint);
                        canvas.drawText(displayString, 200, 400 + itemForDetail.getBitmap().getHeight() + yCord, paint);
                        yCord+=25;
                        start+=endPoint;
                        end=Math.min(start+17,foodName.length());
                    }
                    canvas.drawText(foodName.substring(start, end), 200, 400 + itemForDetail.getBitmap().getHeight() + yCord, paint);
                }
                else {
                    canvas.drawText(foodName, 200, 400 + itemForDetail.getBitmap().getHeight() + 25, paint);
                }
                String foodDesc = itemForDetail.getFoodDescription();
                if(foodDesc.length()>70) {
                    int breaks = foodDesc.length()/70;
                    int start=0;
                    int end=70;
                    int yCord=0;
                    for(int ii=0;ii<breaks;ii++) {
                        String text = foodDesc.substring(start, end);
                        int endPoint = Math.max(text.lastIndexOf(" "), text.lastIndexOf(","));
                        endPoint=Math.max(endPoint,text.lastIndexOf("."));
                        String displayString = foodDesc.substring(start,start+endPoint);
                        canvas.drawText(displayString, 200, 150+yCord, paint);
                        yCord+=25;
                        start+=endPoint;
                        end=Math.min(start+70,foodDesc.length());
                    }
                    canvas.drawText(foodDesc.substring(start, end), 200, 150 + yCord, paint);
                }
                else {
                    canvas.drawText(foodDesc, 200, 150, paint);
                }
                if(!backMap.containsKey(view)) {
                    Bitmap backBitMap = BitmapFactory.decodeResource(context.getResources(), R.drawable.back_button);
                    Button backButton = new Button(800, 640, Bitmap.createScaledBitmap(backBitMap,
                            replayLevelButton.bitMap.getHeight(),
                            replayLevelButton.bitMap.getHeight(),
                            false));
                    backMap.put(view,backButton);
                }
                canvas.drawBitmap(backMap.get(view).bitMap, backMap.get(view).x, backMap.get(view).y, paint);

                ourHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    private void control() {
        try {
            if((view.equals("SelectedItemsView") || view.equals("SelectionView"))) {
                synchronized (lock) {
                    lock.wait();
                }
            }
            if(gameEnded) {
                gameThread.sleep(100000);
            }
        } catch (InterruptedException e) {

        }
    }


    // SurfaceView allows us to handle the onTouchEvent
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        try {

            if(levelRunning>4) {
                synchronized (lock) {
                    lock.notifyAll();
                }
                return true;
            }
            // There are many different events in MotionEvent
            // We care about just 2 - for now.
            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                // Has the player lifted there finger up?
                // Has the player touched the screen?
                case MotionEvent.ACTION_UP:
                    if(gameEnded && end!=null && end.isTouched((int) motionEvent.getX(), (int) motionEvent.getY())) {
                        List<Bag> bags = DataCache.getRandomBagList(1, 10);
                        for (Bag bag : bags){
                            GoodFoodItem gfi = bag.getGoodFoodItem();
                            gfi.getFileName();
                            Class klass = R.drawable.class;
                            Field fld = klass.getDeclaredField(gfi.getFileName());
                            int resource_id = (Integer)fld.get(null);
                            Bitmap bm = BitmapFactory.decodeResource(context.getResources(), resource_id);
                            double htConversion = 1.0;
                            double widthConversion = 1.0;
                            if(bm.getHeight()>200) htConversion=bm.getHeight()/200.0;
                            if(bm.getWidth()>200) widthConversion=bm.getWidth()/200.0;
                            gfi.setHitbox(200, 200, Bitmap.createScaledBitmap(bm,
                                    (int) (bm.getWidth() / Math.max(htConversion,widthConversion)),
                                    (int) (bm.getHeight() / Math.max(htConversion,widthConversion)),
                                    false));
                            BadFoodItem bfi = bag.getBadFoodItem();
                            fld = klass.getDeclaredField(bfi.getFileName());
                            resource_id = (Integer)fld.get(null);
                            bm = BitmapFactory.decodeResource(context.getResources(), resource_id);
                            htConversion = 1.0;
                            widthConversion = 1.0;
                            if(bm.getHeight()>200) htConversion=bm.getHeight()/200.0;
                            if(bm.getWidth()>200) widthConversion=bm.getWidth()/200.0;
                            bfi.setHitbox(200, 200, Bitmap.createScaledBitmap(bm,
                                    (int) (bm.getWidth() / Math.max(htConversion,widthConversion)),
                                    (int) (bm.getHeight() / Math.max(htConversion,widthConversion)),
                                    false));
                        }
                        itemsToBeDisplayed.addAll(bags);
                        levelRunning=1;
                        gameEnded = false;
                        synchronized (lock) {
                            lock.notifyAll();
                        }
                    } else if(gameEnded) {
                        return true;
                    }
                    if(view.equals("Trophy")) {
                        view="SelectedItemsViewNew";
                        synchronized (lock) {
                            lock.notifyAll();
                        }
                        return true;
                        /*view="";
                        List<Bag> bags = DataCache.getRandomBagList(levelRunning, 10);
                        for (Bag bagg : bags){
                            GoodFoodItem gfi = bagg.getGoodFoodItem();
                            gfi.getFileName();
                            Class klass = R.drawable.class;
                            Field fld = klass.getDeclaredField(gfi.getFileName());
                            int resource_id = (Integer)fld.get(null);
                            Bitmap bm = BitmapFactory.decodeResource(context.getResources(), resource_id);
                            double htConversion = 1.0;
                            double widthConversion = 1.0;
                            if(bm.getHeight()>200) htConversion=bm.getHeight()/200.0;
                            if(bm.getWidth()>200) widthConversion=bm.getWidth()/200.0;
                            gfi.setHitbox(200, 200, Bitmap.createScaledBitmap(bm,
                                    (int) (bm.getWidth() / Math.max(htConversion,widthConversion)),
                                    (int) (bm.getHeight() / Math.max(htConversion,widthConversion)),
                                    false));
                            BadFoodItem bfi = bagg.getBadFoodItem();
                            fld = klass.getDeclaredField(bfi.getFileName());
                            resource_id = (Integer)fld.get(null);
                            bm = BitmapFactory.decodeResource(context.getResources(), resource_id);
                            htConversion = 1.0;
                            widthConversion = 1.0;
                            if(bm.getHeight()>200) htConversion=bm.getHeight()/200.0;
                            if(bm.getWidth()>200) widthConversion=bm.getWidth()/200.0;
                            bfi.setHitbox(200, 200, Bitmap.createScaledBitmap(bm,
                                    (int) (bm.getWidth() / Math.max(htConversion,widthConversion)),
                                    (int) (bm.getHeight() / Math.max(htConversion,widthConversion)),
                                    false));
                        }
                        itemsToBeDisplayed.addAll(bags);
                        itemsDisplayed.clear();
                        selectedItems.clear();
                        clickedItems.clear();
                        view="SelectionView";
                        synchronized (lock) {
                            lock.notifyAll();
                        }
                        return true;*/
                    }
                    if(view.equals("SelectionView")) {
                        if(pbr.getProgress()==100){
                            view="Trophy";
                            synchronized (lock) {
                                lock.notifyAll();
                            }
                            return true;
                        }
                        int index = itemsDisplayed.size() - 1;
                        if(index==-1) return true;
                        System.out.println("Innnnnnnnnn up " + index);
                        Bag bag = itemsDisplayed.get(index);
                        if (bag.getGoodFoodItem().isTouched((int) motionEvent.getX(), (int) motionEvent.getY())) {
                            selectedItems.add(bag.getGoodFoodItem());
                            // ----------START------
                            final PopupWindow popUp = new PopupWindow(context);
                            LinearLayout layout = new LinearLayout(context);

                            TextView tv = new TextView(context);
                            tv.setTextSize(20);
                            tv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

                            mainLayout = new LinearLayout(context);

                            RelativeLayout.LayoutParams params1 =
                                    new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                                            RelativeLayout.LayoutParams.WRAP_CONTENT);
                            params1.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                            params1.setMargins(0,0,0,0);
                            layout.setOrientation(LinearLayout.VERTICAL);
                            LinearLayout header = new LinearLayout(context);
                            header.setOrientation(LinearLayout.HORIZONTAL);
                            Bitmap thumbsUp = BitmapFactory.decodeResource(context.getResources(), R.drawable.thumbs_up);
                            double htConversion = 1.0;
                            double widthConversion = 1.0;
                            if(thumbsUp.getHeight()>50) htConversion=thumbsUp.getHeight()/50.0;
                            if(thumbsUp.getWidth()>50) widthConversion=thumbsUp.getWidth()/50.0;
                            android.widget.ImageView imv = new ImageView(context);
                            imv.setImageBitmap(Bitmap.createScaledBitmap(thumbsUp,
                                    (int) (thumbsUp.getWidth() / Math.max(htConversion,widthConversion)),
                                    (int) (thumbsUp.getHeight() / Math.max(htConversion,widthConversion)),
                                    false));

                            header.addView(imv, params1);
                            TextView greenText = new TextView(context);
                            greenText.setTextColor(Color.GREEN);
                            greenText.setTextSize(40);
                            greenText.setText("SEHAT");
                            header.addView(greenText, params1);
                            tv.append("\n");
                            tv.append("\n");
                            int lineCount=3;
                            String foodName = bag.getGoodFoodItem().getTransFoodName();
                            if(foodName.length()>60) {
                                int breaks = foodName.length()/60;
                                int start=0;
                                int end=60;
                                for(int ii=0;ii<breaks;ii++) {
                                    String text = foodName.substring(start, end);
                                    int endPoint = Math.max(text.lastIndexOf(" "), text.lastIndexOf(","));
                                    endPoint=Math.max(endPoint,text.lastIndexOf("."));
                                    String displayString = foodName.substring(start,start+endPoint);
                                    tv.append(displayString);
                                    lineCount++;
                                    start+=endPoint;
                                    end=Math.min(start+60,foodName.length());
                                }
                                tv.append(foodName.substring(start, end));
                                lineCount++;
                            }
                            else {
                                tv.append(foodName);
                                lineCount++;
                            }
                            tv.append("\n");
                            lineCount++;
                            tv.append("\n");
                            lineCount++;
                            String foodDesc = bag.getGoodFoodItem().getFoodDescription();
                            if(foodDesc.length()>60) {
                                int breaks = foodDesc.length()/60;
                                int start=0;
                                int end=60;
                                for(int ii=0;ii<breaks;ii++) {
                                    String text = foodDesc.substring(start, end);
                                    int endPoint = Math.max(text.lastIndexOf(" "), text.lastIndexOf(","));
                                    endPoint=Math.max(endPoint,text.lastIndexOf("."));
                                    String displayString = foodDesc.substring(start,start+endPoint);
                                    tv.append(displayString);
                                    lineCount++;
                                    start+=endPoint;
                                    end=Math.min(start+60,foodDesc.length());
                                }
                                tv.append("\n");
                                tv.append(foodDesc.substring(start, end));
                                lineCount++;
                            }
                            else {
                                tv.append(foodDesc);
                                lineCount++;
                            }
                            for (int i=lineCount;i<13;i++){
                                tv.append("\n");
                            }
                            header.setHorizontalGravity(Gravity.CENTER_HORIZONTAL);
                            layout.addView(header);
                            layout.addView(tv, params1);
                            android.widget.Button okButton = new android.widget.Button(context);
                            okButton.setX(375);
                            //okButton.setY(50+(10-lineCount)*30);
                            okButton.setText("OK");
                            okButton.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    popUp.dismiss();
                                    // -----END-------
                                    synchronized (lock) {
                                        lock.notifyAll();
                                    }
                                }
                            });
                            layout.addView(okButton, 50, 50);
                            GradientDrawable gd = new GradientDrawable();
                            gd.setColor(Color.BLACK); // Changes this drawbale to use a single color instead of a gradient
                            gd.setCornerRadius(5);
                            gd.setStroke(1, Color.WHITE);
                            layout.setBackgroundDrawable(gd);
                            layout.setBackgroundResource(R.drawable.plainbackground);
                            popUp.setContentView(layout);
                            popUp.showAtLocation(mainLayout, Gravity.CENTER, 10, 10);
                            popUp.update(50, 50, 800, 450);
                        }
                        if (bag.getBadFoodItem().isTouched((int) motionEvent.getX(), (int) motionEvent.getY())) {
                            selectedItems.add(bag.getBadFoodItem());

                            final PopupWindow popUp = new PopupWindow(context);
                            LinearLayout layout = new LinearLayout(context);
                            TextView tv = new TextView(context);
                            tv.setTextSize(20);
                            tv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

                            mainLayout = new LinearLayout(context);
                            ViewGroup.LayoutParams params= new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT);
                            layout.setOrientation(LinearLayout.VERTICAL);
                            RelativeLayout.LayoutParams params1 =
                                    new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                                            RelativeLayout.LayoutParams.WRAP_CONTENT);
                            params1.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                            params1.setMargins(0, 0, 0, 0);
                            Bitmap thumbsUp = BitmapFactory.decodeResource(context.getResources(), R.drawable.thumbs_down);
                            double htConversion = 1.0;
                            double widthConversion = 1.0;
                            if(thumbsUp.getHeight()>50) htConversion=thumbsUp.getHeight()/50.0;
                            if(thumbsUp.getWidth()>50) widthConversion=thumbsUp.getWidth()/50.0;
                            android.widget.ImageView imv = new ImageView(context);
                            imv.setImageBitmap(Bitmap.createScaledBitmap(thumbsUp,
                                    (int) (thumbsUp.getWidth() / Math.max(htConversion, widthConversion)),
                                    (int) (thumbsUp.getHeight() / Math.max(htConversion, widthConversion)),
                                    false));
                            LinearLayout header = new LinearLayout(context);
                            header.setOrientation(LinearLayout.HORIZONTAL);
                            header.addView(imv, params1);
                            TextView greenText = new TextView(context);
                            greenText.setTextColor(Color.RED);
                            greenText.setTextSize(40);
                            greenText.setText("KURANG SEHAT");
                            header.addView(greenText, params1);
                            tv.append("\n");
                            tv.append("\n");
                            int lineCount=3;
                            String foodName = bag.getBadFoodItem().getTransFoodName();
                            if(foodName.length()>60) {
                                int breaks = foodName.length()/60;
                                int start=0;
                                int end=60;
                                for(int ii=0;ii<breaks;ii++) {
                                    String text = foodName.substring(start, end);
                                    int endPoint = Math.max(text.lastIndexOf(" "), text.lastIndexOf(","));
                                    endPoint=Math.max(endPoint,text.lastIndexOf("."));
                                    String displayString = foodName.substring(start,start+endPoint);
                                    tv.append(displayString);
                                    lineCount++;
                                    start+=endPoint;
                                    end=Math.min(start+60,foodName.length());
                                }
                                tv.append(foodName.substring(start, end));
                                lineCount++;
                            }
                            else {
                                tv.append(foodName);
                                lineCount++;
                            }
                            tv.append("\n");
                            lineCount++;
                            tv.append("\n");
                            lineCount++;
                            String foodDesc = bag.getBadFoodItem().getFoodDescription();
                            if(foodDesc.length()>60) {
                                int breaks = foodDesc.length()/60;
                                int start=0;
                                int end=60;
                                for(int ii=0;ii<breaks;ii++) {
                                    String text = foodDesc.substring(start, end);
                                    int endPoint = Math.max(text.lastIndexOf(" "), text.lastIndexOf(","));
                                    endPoint=Math.max(endPoint,text.lastIndexOf("."));
                                    String displayString = foodDesc.substring(start,start+endPoint);
                                    tv.append(displayString);
                                    lineCount++;
                                    start+=endPoint;
                                    end=Math.min(start+60,foodDesc.length());
                                }
                                tv.append("\n");
                                tv.append(foodDesc.substring(start, end));
                                lineCount++;
                            }
                            else {
                                tv.append(foodDesc);
                                lineCount++;
                            }

                            for (int i=lineCount;i<13;i++){
                                tv.append("\n");
                            }
                            header.setHorizontalGravity(Gravity.CENTER_HORIZONTAL);
                            layout.addView(header);
                            layout.addView(tv, params);
                            android.widget.Button okButton = new android.widget.Button(context);

                            okButton.setX(375);
                            //okButton.setY(50+(10-lineCount)*30);
                            okButton.setText("OK");
                            okButton.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    popUp.dismiss();
                                    // -----END-------
                                    synchronized (lock) {
                                        lock.notifyAll();
                                    }
                                }
                            });
                            layout.addView(okButton, 50, 50);
                            GradientDrawable gd = new GradientDrawable();
                            gd.setColor(Color.BLACK); // Changes this drawbale to use a single color instead of a gradient
                            gd.setCornerRadius(5);
                            gd.setStroke(1, Color.WHITE);
                            layout.setBackgroundDrawable(gd);
                            layout.setBackgroundResource(R.drawable.plainbackground);
                            popUp.setContentView(layout);
                            popUp.showAtLocation(mainLayout, Gravity.CENTER, 10, 10);
                            popUp.update(50, 50, 800, 450);
                        }
                        if (backMap.get(view).isTouched((int) motionEvent.getX(), (int) motionEvent.getY())) {
                            if(index==0) return true;
                            Bag bagR = itemsDisplayed.remove(index);
                            itemsToBeDisplayed.add(0,bagR);
                            bagR = itemsDisplayed.remove(index-1);
                            itemsToBeDisplayed.add(0, bagR);
                            selectedItems.remove(selectedItems.size() - 1);
                            synchronized (lock) {
                                lock.notifyAll();
                            }
                        }
                        if (replayLevelButton.isTouched((int) motionEvent.getX(), (int) motionEvent.getY())) {
                            view="";
                            List<Bag> bags = DataCache.getRandomBagList(levelRunning, 10);
                            for (Bag bagg : bags){
                                GoodFoodItem gfi = bagg.getGoodFoodItem();
                                gfi.getFileName();
                                Class klass = R.drawable.class;
                                Field fld = klass.getDeclaredField(gfi.getFileName());
                                int resource_id = (Integer)fld.get(null);
                                Bitmap bm = BitmapFactory.decodeResource(context.getResources(), resource_id);
                                double htConversion = 1.0;
                                double widthConversion = 1.0;
                                if(bm.getHeight()>200) htConversion=bm.getHeight()/200.0;
                                if(bm.getWidth()>200) widthConversion=bm.getWidth()/200.0;
                                gfi.setHitbox(200, 200, Bitmap.createScaledBitmap(bm,
                                        (int) (bm.getWidth() / Math.max(htConversion,widthConversion)),
                                        (int) (bm.getHeight() / Math.max(htConversion,widthConversion)),
                                        false));
                                BadFoodItem bfi = bagg.getBadFoodItem();
                                fld = klass.getDeclaredField(bfi.getFileName());
                                resource_id = (Integer)fld.get(null);
                                bm = BitmapFactory.decodeResource(context.getResources(), resource_id);
                                htConversion = 1.0;
                                widthConversion = 1.0;
                                if(bm.getHeight()>200) htConversion=bm.getHeight()/200.0;
                                if(bm.getWidth()>200) widthConversion=bm.getWidth()/200.0;
                                bfi.setHitbox(200, 200, Bitmap.createScaledBitmap(bm,
                                        (int) (bm.getWidth() / Math.max(htConversion,widthConversion)),
                                        (int) (bm.getHeight() / Math.max(htConversion,widthConversion)),
                                        false));
                            }
                            itemsToBeDisplayed.addAll(bags);
                            itemsDisplayed.clear();
                            selectedItems.clear();
                            clickedItems.clear();
                            synchronized (lock) {
                                lock.notifyAll();
                            }
                        }
                    } else if(view.equals("SelectedItemsView")) {
                        for (FoodItem item : selectedItems) {
                            if(item.isTouched((int) motionEvent.getX(), (int) motionEvent.getY())) {
                                itemForDetail = item;
                                if(!clickedItems.contains(item)){
                                    clickedItems.add(item);
                                }
                                synchronized (lock) {
                                    lock.notifyAll();
                                }
                                if( itemsToBeDisplayed.size()>0) {
                                    view="SelectionView";
                                } else if (view.equals("SelectedItemsView")) {
                                    view="ItemDetails";
                                }
                                return true;
                            }
                        }
                        if(nextButton!=null && nextButton.isTouched((int) motionEvent.getX(), (int) motionEvent.getY())) {
                            if(levelRunning>=4) {
                                synchronized (lock) {
                                    lock.notifyAll();
                                }
                                return true;
                            }
                            view="";
                            List<Bag> bags = DataCache.getRandomBagList(++levelRunning, 10);
                            for (Bag bag : bags){
                                GoodFoodItem gfi = bag.getGoodFoodItem();
                                gfi.getFileName();
                                Class klass = R.drawable.class;
                                Field fld = klass.getDeclaredField(gfi.getFileName());
                                int resource_id = (Integer)fld.get(null);
                                Bitmap bm = BitmapFactory.decodeResource(context.getResources(), resource_id);
                                double htConversion = 1.0;
                                double widthConversion = 1.0;
                                if(bm.getHeight()>200) htConversion=bm.getHeight()/200.0;
                                if(bm.getWidth()>200) widthConversion=bm.getWidth()/200.0;
                                gfi.setHitbox(200, 200, Bitmap.createScaledBitmap(bm,
                                        (int) (bm.getWidth() / Math.max(htConversion,widthConversion)),
                                        (int) (bm.getHeight() / Math.max(htConversion,widthConversion)),
                                        false));
                                BadFoodItem bfi = bag.getBadFoodItem();
                                fld = klass.getDeclaredField(bfi.getFileName());
                                resource_id = (Integer)fld.get(null);
                                bm = BitmapFactory.decodeResource(context.getResources(), resource_id);
                                htConversion = 1.0;
                                widthConversion = 1.0;
                                if(bm.getHeight()>200) htConversion=bm.getHeight()/200.0;
                                if(bm.getWidth()>200) widthConversion=bm.getWidth()/200.0;
                                bfi.setHitbox(200, 200, Bitmap.createScaledBitmap(bm,
                                        (int) (bm.getWidth() / Math.max(htConversion,widthConversion)),
                                        (int) (bm.getHeight() / Math.max(htConversion,widthConversion)),
                                        false));
                            }
                            itemsToBeDisplayed.addAll(bags);
                            itemsDisplayed.clear();
                            selectedItems.clear();
                            clickedItems.clear();
                            synchronized (lock) {
                                lock.notifyAll();
                            }
                        } else if (backMap.get(view)!=null && backMap.get(view).isTouched((int) motionEvent.getX(), (int) motionEvent.getY())) {
                            if(selectedItems.size()==0) return true;
                            Bag bagR = itemsDisplayed.remove(itemsDisplayed.size()-1);
                            itemsToBeDisplayed.add(0,bagR);
                            selectedItems.remove(selectedItems.size() - 1);
                            view="SelectionView";
                            synchronized (lock) {
                                lock.notifyAll();
                            }
                        }else if (replayLevelButton.isTouched((int) motionEvent.getX(), (int) motionEvent.getY())) {

                            view="";
                            List<Bag> bags = DataCache.getRandomBagList(levelRunning, 10);
                            for (Bag bagg : bags){
                                GoodFoodItem gfi = bagg.getGoodFoodItem();
                                gfi.getFileName();
                                Class klass = R.drawable.class;
                                Field fld = klass.getDeclaredField(gfi.getFileName());
                                int resource_id = (Integer)fld.get(null);
                                Bitmap bm = BitmapFactory.decodeResource(context.getResources(), resource_id);
                                double htConversion = 1.0;
                                double widthConversion = 1.0;
                                if(bm.getHeight()>200) htConversion=bm.getHeight()/200.0;
                                if(bm.getWidth()>200) widthConversion=bm.getWidth()/200.0;
                                gfi.setHitbox(200, 200, Bitmap.createScaledBitmap(bm,
                                        (int) (bm.getWidth() / Math.max(htConversion,widthConversion)),
                                        (int) (bm.getHeight() / Math.max(htConversion,widthConversion)),
                                        false));
                                BadFoodItem bfi = bagg.getBadFoodItem();
                                fld = klass.getDeclaredField(bfi.getFileName());
                                resource_id = (Integer)fld.get(null);
                                bm = BitmapFactory.decodeResource(context.getResources(), resource_id);
                                htConversion = 1.0;
                                widthConversion = 1.0;
                                if(bm.getHeight()>200) htConversion=bm.getHeight()/200.0;
                                if(bm.getWidth()>200) widthConversion=bm.getWidth()/200.0;
                                bfi.setHitbox(200, 200, Bitmap.createScaledBitmap(bm,
                                        (int) (bm.getWidth() / Math.max(htConversion,widthConversion)),
                                        (int) (bm.getHeight() / Math.max(htConversion,widthConversion)),
                                        false));
                            }
                            itemsToBeDisplayed.addAll(bags);
                            itemsDisplayed.clear();
                            selectedItems.clear();
                            clickedItems.clear();
                            synchronized (lock) {
                                lock.notifyAll();
                            }
                        } else {
                            synchronized (lock) {
                                lock.notifyAll();
                            }
                            return true;
                        }
                    } else if(view.equals("ItemDetails")) {
                        if (backMap.get(view)!=null && backMap.get(view).isTouched((int) motionEvent.getX(), (int) motionEvent.getY())) {
                            view="";
                            synchronized (lock) {
                                lock.notifyAll();
                            }
                            return true;
                        } else if(nextButton!=null && nextButton.isTouched((int) motionEvent.getX(), (int) motionEvent.getY())) {
                            view="";
                            List<Bag> bags = DataCache.getRandomBagList(levelRunning, 10);
                            for (Bag bag : bags){
                                GoodFoodItem gfi = bag.getGoodFoodItem();
                                gfi.getFileName();
                                Class klass = R.drawable.class;
                                Field fld = klass.getDeclaredField(gfi.getFileName());
                                int resource_id = (Integer)fld.get(null);
                                Bitmap bm = BitmapFactory.decodeResource(context.getResources(), resource_id);
                                double htConversion = 1.0;
                                double widthConversion = 1.0;
                                if(bm.getHeight()>200) htConversion=bm.getHeight()/200.0;
                                if(bm.getWidth()>200) widthConversion=bm.getWidth()/200.0;
                                gfi.setHitbox(200, 200, Bitmap.createScaledBitmap(bm,
                                        (int) (bm.getWidth() / Math.max(htConversion,widthConversion)),
                                        (int) (bm.getHeight() / Math.max(htConversion,widthConversion)),
                                        false));
                                BadFoodItem bfi = bag.getBadFoodItem();
                                fld = klass.getDeclaredField(bfi.getFileName());
                                resource_id = (Integer)fld.get(null);
                                bm = BitmapFactory.decodeResource(context.getResources(), resource_id);
                                htConversion = 1.0;
                                widthConversion = 1.0;
                                if(bm.getHeight()>200) htConversion=bm.getHeight()/200.0;
                                if(bm.getWidth()>200) widthConversion=bm.getWidth()/200.0;
                                bfi.setHitbox(200, 200, Bitmap.createScaledBitmap(bm,
                                        (int) (bm.getWidth() / Math.max(htConversion,widthConversion)),
                                        (int) (bm.getHeight() / Math.max(htConversion,widthConversion)),
                                        false));
                            }
                            itemsToBeDisplayed.addAll(bags);
                            itemsDisplayed.clear();
                            selectedItems.clear();
                            synchronized (lock) {
                                lock.notifyAll();
                            }
                        }
                    }
                    if( itemsToBeDisplayed.size()>0) {
                        view="SelectionView";
                    } else if (view.equals("SelectedItemsView")) {
                        view="ItemDetails";
                    }
                    // If we are currently on the pause screen, start a new game
                    if(gameEnded){
                        startGame();
                    }
                    break;
            }


        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return true;
    }

    // Clean up our thread if the game is interrupted or the player quits
    public void pause() {
        playing = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {

        }
    }

    // Make a new thread and start it
    // Execution moves to our R
    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    private String formatTime(long time){
        long seconds = (time) / 1000;
        long thousandths = (time) - (seconds * 1000);
        String strThousandths = "" + thousandths;
        if (thousandths < 100){strThousandths = "0" + thousandths;}
        if (thousandths < 10){strThousandths = "0" + strThousandths;}
        String stringTime = "" + seconds + "." + strThousandths;
        return stringTime;
    }

    class Button {
        int x;
        int y;
        Bitmap bitMap;
        Button(int x, int y, Bitmap bitMap) {
            this.x=x;
            this.y=y;
            this.bitMap=bitMap;
        }

        public boolean isTouched(int xCor, int yCor) {
            if (xCor>x && xCor<x+bitMap.getWidth()) {
                if(yCor>y && yCor<y+bitMap.getHeight()) {
                    return true;
                }
            }
            return false;
        }
    }


}


