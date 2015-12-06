package com.ybs.nutriapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class AboutTheGame extends Activity {

    private AboutTheGameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Get a Display object to access screen details
        Display display = getWindowManager().getDefaultDisplay();
        // Load the resolution into a Point object
        Point size = new Point();
        display.getSize(size);

        // Create an instance of our Tappy Defender View
        // Also passing in this.
        // Also passing in the screen resolution to the constructor
        gameView = new AboutTheGameView(this, size.x, size.y);

        // Make our gameView the view for the Activity
        setContentView(gameView);
    }

    // If the Activity is paused make sure to pause our thread
    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause();
    }

    // If the Activity is resumed make sure to resume our thread
    @Override
    protected void onResume() {
        super.onResume();
        gameView.resume();
    }
}

class AboutTheGameView extends SurfaceView implements Runnable {
    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder ourHolder;
    Thread gameThread = null;

    AboutTheGameView(Context context, int x, int y) {
        super(context);
        DataCache.init(context);
        FoodItem.maxX=x;

        //screenX = x;
        //screenY = y;

        // Initialize our drawing objects
        ourHolder = getHolder();
        paint = new Paint();
    }
    @Override
    public void run() {
        canvas = ourHolder.lockCanvas();
        //canvas.drawColor(Color.argb(255, 0, 0, 0));

        // For debugging
        // Switch to white pixels
        paint.setTextSize(25);
        if(MainActivity.language.equals("English")) {
            canvas.drawText("For Children ", 100, 50, paint);
            canvas.drawText("Food Battle shows children healthy and unhealthy food. This game is developed by Yayasan Balita Sehat ", 100, 100, paint);
            canvas.drawText("as a means to educate children to be able to choose healthy snacks and meals.", 100, 150, paint);
            canvas.drawText("For Parents ", 100, 250, paint);
            canvas.drawText("Food Battle could also be learned by parents to help their children and family members to choose", 100, 300, paint);
            canvas.drawText(" and consume healthy food. ", 100, 350, paint);
        } else {
            canvas.drawText("Untuk Anak-anak ", 100, 50, paint);
            canvas.drawText("Food Battle memperkenalkan anak dengan jenis makanan yang sehat dan tidak sehat. Permainan ini ", 100, 100, paint);
            canvas.drawText("dikembangkan oleh Yayasan Balita Sehat sebagai media untuk membekali anak agar dapat memilih makanan dan ", 100, 150, paint);
            canvas.drawText("jajanan sehat.", 100, 200, paint);
            canvas.drawText("Untuk Orang tua ", 100, 300, paint);
            canvas.drawText("Food Battle juga dapat dipelajari oleh orang tua untuk membantu anak dan anggota keluarga dalam ", 100, 350, paint);
            canvas.drawText("memilih dan mengkonsumsi makanan sehat. ", 100, 400, paint);
        }
        ourHolder.unlockCanvasAndPost(canvas);
    }

    // Clean up our thread if the game is interrupted or the player quits
    public void pause() {
        try {
            gameThread.join();
        } catch (InterruptedException e) {

        }
    }

    // Make a new thread and start it
    // Execution moves to our R
    public void resume() {
        gameThread = new Thread(this);
        gameThread.start();
    }
}