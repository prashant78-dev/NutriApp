package com.ybs.nutriapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;


public class MainActivity extends Activity
        implements View.OnClickListener{

    public static String language = "Bahasa";
    PopupWindow popUp;
    LinearLayout mainLayout;
    TextView tv;
    BackgroundSound mBackgroundSound = new BackgroundSound();
    // This is the entry point to our game
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        mBackgroundSound.execute();
        super.onCreate(savedInstanceState);

        //Here we set our UI layout as the view
        setContentView(R.layout.activity_main);

        // Get a reference to the button in our layout
        final ImageButton buttonPlay =
                (ImageButton)findViewById(R.id.buttonPlay);
        // Listen for clicks
        buttonPlay.setOnClickListener(this);
        // Get a reference to the button in our layout
        final ImageButton buttonAbout =
                (ImageButton)findViewById(R.id.buttonAbout);
        // Listen for clicks
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.about_the_game, null); //custom_layout is your xml file which contains popuplayout
        popUp = new PopupWindow(this);

        ScrollView layout = (ScrollView)view.findViewById(R.id.aboutthegamescrollview);
        layout.setScrollbarFadingEnabled(false);
        layout.setVerticalScrollBarEnabled(true);
        layout.setVerticalFadingEdgeEnabled(false);
        tv = (TextView)view.findViewById(R.id.aboutthegame);
        //canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.plainbackground), 0, 0, paint);
        //popUp.setBackgroundDrawable(getDrawable(R.drawable.plainbackground));
        layout.setBackgroundResource(R.drawable.plainbackground);
        //tv.setBackground(this.getDrawable(R.drawable.plainbackground));
        tv.setTextSize(25);
        tv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

        tv.setMovementMethod(ScrollingMovementMethod.getInstance());
        //tv.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        //tv.setVerticalScrollBarEnabled(true);
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
            tv.setText("Purpose of game:\n");
            tv.append("Untuk Anak-anak ");
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
            tv.append("\n");
            tv.append("For Children ");
            tv.append("\n");
            tv.append("Food Battle shows children healthy and unhealthy food. This game is developed by Yayasan Balita Sehat ");
            tv.append("as a means to educate children to be able to choose healthy snacks and meals.");
            tv.append("\n");
            tv.append("\n");
            tv.append("For Parents ");
            tv.append("\n");
            tv.append("Food Battle could also be learned by parents to help their children and family members to choose");
            tv.append(" and consume healthy food. ");
            tv.append("\n");
            tv.append("\n");
            tv.append("Cara Bermain:\n");
            tv.append("1. Ketika pemain mulai Food Battle, 2 gambar makanan acak akan ditampilkan, makanan yang sehat dan tidak sehat.\n");
            tv.append("2. Pilih makanan yang sehat untuk memenangkan Food Battle!\n");
            tv.append("3. Setiap kamu memilih makanan yang sehat, power akan meningkat!\n");
            tv.append("4. Kalau kamu memilih makanan yang tidak sehat, power akan tetap seperti itu!\n");
            tv.append("5. Setelah kamu selesai membaca pesan tentang makanan, sentuh layar lagi untuk memainkan set berikutnya.\n");
            tv.append("6. Ada 10 pasang makanan acak. Untuk memenangkan trofi permainan, kamu perlu memilih semua 10 makanan yang baik!\n");
            tv.append("7. Tapi, jangan khawatir jika kamu sudah memilih beberapa makanan yang buruk, setidaknya kamu bisa belajar lebih banyak tentang yang makanan yang sebaiknya dihindari.\n");
            tv.append("8. Kalau mau, kamu bisa menekan tombol panah ke belakang untuk kembali ke set makanan sebelumnya untuk dimainkan lagi\n");
            tv.append("9. Pada akhir 10 putaran, seorang dewasa sebaiknya menemani anak mereview kembali kandungan gizi dari 10 makanan yang dipilih di halaman ringkasan\n");
            tv.append("10. Orang dewasa dapat membaca rincian dengan menyentuh setiap makanan yang dipilih, yang akan menampilkan catatan singkat.\n");
            tv.append("11. Tombol Replay akan muncul pada halaman ringkasan untuk pemain mencoba lagi game yang telah dimainkan\n");
            tv.append("12. Tekan tombol Replay dan berusaha lebih keras babak berikutnya!\n");
            tv.append("13. Petunjuk # 1: makanan dan minuman baru dapat tampil di babak berikutnya!\n");
            tv.append("14. Petunjuk # 2: Ada piala yang berbeda untuk dimenangkan juga, ayo semangat memilih makanan sehat!\n");
            tv.append("\n");
            tv.append("Credits:\n");
            tv.append("Yayasan Balita Sehat acknowledges the usage of images from internet. For any complain of the image usage, please email to info@fmch-indonesia.org");
        }

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.BLACK); // Changes this drawbale to use a single color instead of a gradient
        gd.setCornerRadius(5);
        gd.setStroke(1, Color.WHITE);
        //tv.setBackgroundDrawable(gd);
        //layout.addView(tv, params);

        Button okButton = (Button)view.findViewById(R.id.buttonOK);
        okButton.setText("OK");
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUp.dismiss();
            }
        });
        //layout.addView(okButton, 50, 50);
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
                popUp.update(50, 50, 800, 400);
        }
    }
    public class BackgroundSound extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            MediaPlayer player = MediaPlayer.create(MainActivity.this, R.raw.background);
            player.setLooping(true); // Set looping
            player.setVolume(1.0f, 1.0f);
            player.start();

            return null;
        }

    }

}