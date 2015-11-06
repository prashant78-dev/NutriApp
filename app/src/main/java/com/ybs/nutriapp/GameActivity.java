package com.ybs.nutriapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.ybs.nutriapp.R;

public class GameActivity extends Activity {

    // Our object to handle the View
    private TDView gameView;
    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder ourHolder;
    // This is where the "Play" button from HomeActivity sends us
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
        ProgressBar pbr = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        pbr.setIndeterminate(false);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(200, 50);
        params.setMargins(50, 650, 100, 100);
        pbr.setLayoutParams(params);
        pbr.setMax(100);
        ShapeDrawable pgDrawable = new ShapeDrawable(new RoundRectShape(null,     null, null));

        // Sets the progressBar color
        pgDrawable.getPaint().setColor(Color.GREEN);

        // Adds the drawable to your progressBar
        ClipDrawable progress = new ClipDrawable(pgDrawable, Gravity.LEFT, ClipDrawable.HORIZONTAL);
        pbr.setProgressDrawable(progress);pbr.setBackgroundColor(Color.RED);
        gameView = new TDView(this, size.x, size.y, pbr);
        // Make our gameView the view for the Activity
        //setContentView(gameView);
        setContentView(gameView);
        //addContentView(gameView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
        //        ViewGroup.LayoutParams.MATCH_PARENT));
        LinearLayout anotherLayout = new LinearLayout(this);
        LinearLayout.LayoutParams linearLayoutParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);

        //anotherLayout.removeView(MainActivity.pbr);
        anotherLayout.addView(pbr);anotherLayout.setMinimumWidth(250);anotherLayout.setMinimumHeight(75);
        addContentView(anotherLayout, linearLayoutParams);

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
        //bView.resume();
        gameView.resume();

    }

}
