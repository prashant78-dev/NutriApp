package com.ybs.nutriapp;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class GameActivity extends Activity {

    // Our object to handle the View
    private TDView gameView;

    // This is where the "Play" button from HomeActivity sends us
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get a Display object to access screen details
        Display display = getWindowManager().getDefaultDisplay();
        // Load the resolution into a Point object
        Point size = new Point();
        display.getSize(size);
        ProgressBar pbr = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        pbr.setIndeterminate(false);
        pbr.setMinimumHeight(50);
        pbr.setMinimumWidth(150);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(200, 50);
        params.setMargins(50, 650, 100, 100);
        //pbr.setLayoutParams(params);
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
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayoutParams.gravity= Gravity.BOTTOM;
        linearLayoutParams.setMargins(20, 550, 0, 0);
        anotherLayout.setOrientation(LinearLayout.VERTICAL);
        //anotherLayout.removeView(MainActivity.pbr);
        TextView power = new TextView(this);
        power.setTextColor(Color.GREEN);
        power.setTextSize(25);
        power.setText("     POWER");
        LinearLayout.LayoutParams params1 =
                new LinearLayout.LayoutParams(200, 50);
        //params1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        params1.setMargins(50, 550, 100, 200);
        //power.setLayoutParams(params1);
        LinearLayout lv = new LinearLayout(this);
        lv.setOrientation(LinearLayout.VERTICAL);
        lv.setGravity(Gravity.BOTTOM);
        lv.setVerticalGravity(Gravity.BOTTOM);
        lv.setMinimumHeight(150);
        lv.setMinimumWidth(150);
        lv.addView(power);
        lv.addView(pbr);
        anotherLayout.setGravity(Gravity.BOTTOM);
        anotherLayout.setVerticalGravity(Gravity.BOTTOM);
        anotherLayout.addView(lv, linearLayoutParams);
        anotherLayout.setMinimumWidth(200);anotherLayout.setMinimumHeight(150);
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
        gameView.resume();
    }


}
