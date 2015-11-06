package com.ybs.nutriapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;


public class MainActivity extends Activity
        implements View.OnClickListener{

    public static String language = "Bahasa";
    PopupWindow popUp;
    LinearLayout mainLayout;
    TextView tv;
    // This is the entry point to our game
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Here we set our UI layout as the view
        setContentView(R.layout.activity_main);

        // Get a reference to the button in our layout
        final Button buttonPlay =
                (Button)findViewById(R.id.buttonPlay);
        // Listen for clicks
        buttonPlay.setOnClickListener(this);
        // Get a reference to the button in our layout
        final Button buttonAbout =
                (Button)findViewById(R.id.buttonAbout);
        // Listen for clicks
        popUp = new PopupWindow(this);
        LinearLayout layout = new LinearLayout(this);
        tv = new TextView(this);

        tv.setTextSize(25);
        tv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        mainLayout = new LinearLayout(this);

        final boolean click = true;
        buttonAbout.setOnClickListener(new ClickListener());
        /*buttonAbout.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if (click) {
                    popUp.showAtLocation(mainLayout, Gravity.CENTER, 10, 10);
                    popUp.update(50, 50, 700, 400);
                    //click = false;
                } else {
                    popUp.dismiss();
                    //click = true;
                }
            }

        });*/
        ViewGroup.LayoutParams params= new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layout.setOrientation(LinearLayout.VERTICAL);
        if(language.equals("English")) {
            tv.setText("For Children ");
            tv.append("\n");
            tv.append("Food Battle shows children healthy and unhealthy food. This game is developed by Yayasan Balita Sehat ");
            tv.append("as a means to educate children to be able to choose healthy snacks and meals.");
            tv.append("\n");
            tv.append("\n");
            tv.append("For Parents ");
            tv.append("\n");
            tv.append("Food Battle could also be learned by parents to help their children and family members to choose");
            tv.append(" and consume healthy food. ");
        } else {
            tv.setText("Untuk Anak-anak ");
            tv.append("\n");
            tv.append("Food Battle memperkenalkan anak dengan jenis makanan yang sehat dan tidak sehat. Permainan ini dikembangkan ");
            tv.append("oleh Yayasan Balita Sehat sebagai media untuk membekali anak agar dapat memilih makanan dan jajanan sehat.");
            tv.append("\n");
            tv.append("\n");
            tv.append("Untuk Orang tua ");
            tv.append("\n");
            tv.append("Food Battle juga dapat dipelajari oleh orang tua untuk membantu anak dan anggota keluarga dalam ");
            tv.append("memilih dan mengkonsumsi makanan sehat. ");
            tv.append("\n");
            tv.append("Cara Bermain");
            tv.append("1. Ketika pemain mulai Food Battle, 2 gambar makanan acak akan ditampilkan, makanan yang sehat dan tidak sehat.\n");
            tv.append("Pilih makanan yang sehat untuk memenangkan Food Battle!");
            tv.append("Setiap kamu memilih makanan yang sehat, power akan meningkat!");
            tv.append("Kalau kamu memilih makanan yang tidak sehat, power akan tetap seperti itu!");
            tv.append("Setelah kamu selesai membaca pesan tentang makanan, sentuh layar lagi untuk memainkan set berikutnya.");
            tv.append("Ada 10 pasang makanan acak. Untuk memenangkan trofi permainan, kamu perlu memilih semua 10 makanan yang baik!");
            tv.append("Tapi, jangan khawatir jika kamu sudah memilih beberapa makanan yang buruk, setidaknya kamu bisa belajar lebih banyak tentang yang makanan yang sebaiknya dihindari.");
            tv.append("Pada akhir 10 putaran, seorang dewasa sebaiknya menemani anak mereview kembali kandungan gizi dari 10 makanan yang dipilih di halaman ringkasan");
            tv.append("Orang dewasa dapat membaca rincian dengan menyentuh setiap makanan yang dipilih, yang akan menampilkan catatan singkat.");
            tv.append("Ketika semua catatan telah dibuka, tombol Replay akan muncul.");
            tv.append("Replay Food Battle dan berusaha lebih keras babak berikutnya!");
            tv.append("Petunjuk # 1: makanan dan minuman baru dapat tampil di babak berikutnya!");
            tv.append("Petunjuk # 2: Ada piala yang berbeda untuk dimenangkan juga, ayo semangat memilih makanan sehat!");

        }
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.BLACK); // Changes this drawbale to use a single color instead of a gradient
        gd.setCornerRadius(5);
        gd.setStroke(1, Color.WHITE);
        //tv.setBackgroundDrawable(gd);
        layout.addView(tv, params);

        Button okButton = new Button(this);
        okButton.setText("OK");
        okButton.setX(325);
        okButton.setY(50);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUp.dismiss();
            }
        });
        layout.addView(okButton, 50, 50);
        layout.setBackgroundDrawable(gd);
        popUp.setContentView(layout);

        final Button buttonEnglish =
                (Button)findViewById(R.id.buttonEnglish);
        // Listen for clicks
        buttonEnglish.setOnClickListener(this);

        final Button buttonBahasa =
                (Button)findViewById(R.id.buttonBahasa);
        // Listen for clicks
        buttonBahasa.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        // must be the Play button.
// Create a new Intent object
        if(v instanceof Button && ((Button)v).getText().toString().equals("Bahasa")) {
            language="Bahasa";
                tv.setText("Untuk Anak-anak ");
                tv.append("\n");
                tv.append("Food Battle memperkenalkan anak dengan jenis makanan yang sehat dan tidak sehat. Permainan ini dikembangkan ");
                tv.append("oleh Yayasan Balita Sehat sebagai media untuk membekali anak agar dapat memilih makanan dan jajanan sehat.");
                tv.append("\n");
                tv.append("\n");
                tv.append("Untuk Orang tua ");
                tv.append("\n");
                tv.append("Food Battle juga dapat dipelajari oleh orang tua untuk membantu anak dan anggota keluarga dalam ");
                tv.append("memilih dan mengkonsumsi makanan sehat. ");

            return;
        }
        if(v instanceof Button && ((Button)v).getText().toString().equals("English")) {
            language="English";

                tv.setText("For Children ");
                tv.append("\n");
                tv.append("Food Battle shows children healthy and unhealthy food. This game is developed by Yayasan Balita Sehat ");
                tv.append("as a means to educate children to be able to choose healthy snacks and meals.");
                tv.append("\n");
                tv.append("\n");
                tv.append("For Parents ");
                tv.append("\n");
                tv.append("Food Battle could also be learned by parents to help their children and family members to choose");
                tv.append(" and consume healthy food. ");
            return;
        }
        if(v instanceof Button && ((Button)v).getText().toString().equals("About the game")) {
            Intent i = new Intent(this, AboutTheGame.class);
            startActivity(i);
            return;
        }
        Intent i = new Intent(this, GameActivity.class);
// Start our GameActivity class via the Intent
        startActivity(i);
// Now shut this activity down
        finish();
    }

    class ClickListener implements View.OnClickListener {

        ClickListener() {

        }
        @Override
        public void onClick(View v) {
                popUp.showAtLocation(mainLayout, Gravity.CENTER, 10, 10);
                popUp.update(50, 50, 700, 400);
        }
    }
}