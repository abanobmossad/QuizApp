package com.example.abano.quizyourbrain;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardedVideoAd;

public class QuizMainActivity extends AppCompatActivity {
    private static RewardedVideoAd RewardedVideoAd;
    private static InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        String user_id = intent.getStringExtra("user_id");
        LoadData.getQuestions().clear();
        // do the retrieving  data in background from firebase
        new LoadData(this, user_id).execute();
        // get in full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_main);

        // initialize ads
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");
        RewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        loadRewardedVideoAd();
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");

    }


    public void startQuestions(View view) {
        try {
            if (isOnline()) {
                FragmentManager manager = getSupportFragmentManager();
                Fragment fragment = manager.findFragmentById(R.id.fragmentContainer);
                if (fragment == null) {
                    fragment = QuestionFragment.newInstance(LoadData.getQuestions().get(0), 0, "0");
                    manager.beginTransaction().add(R.id.fragmentContainer, fragment).commit();
                }
            } else {
                noInternetDialog(this);
            }
        } catch (IndexOutOfBoundsException e) {
            Log.d("IndexOutOfException", e.getMessage());
        }
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public static void noInternetDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.checkconnection)
                .setTitle(R.string.noconnection);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            System.exit(0);
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 3000);
    }

    // loading video ads
    public static void loadRewardedVideoAd() {
        if (!RewardedVideoAd.isLoaded()) {
            RewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917", new AdRequest.Builder().build());
        }
    }

    public static void loadPopupAd() {
        if (!mInterstitialAd.isLoaded()) {
            mInterstitialAd.loadAd(new AdRequest.Builder().build());
        }
    }

    public static InterstitialAd getInterstitialAd() {
        return mInterstitialAd;
    }

    public static RewardedVideoAd getRewardedVideoAd() {
        return RewardedVideoAd;
    }


//     Ad preserve state
//    @Override
//    protected void onPause() {
//        super.onPause();
//        RewardedVideoAd.pause(this);
//        //stop service and stop music
//        stopService(new Intent(QuizMainActivity.this, SoundService.class));
//    }
////
//    @Override
//    protected void onResume() {
//        super.onResume();
//        RewardedVideoAd.resume(this);
//        startService(new Intent(QuizMainActivity.this, SoundService.class));
//    }
////
////    @Override
////    protected void onDestroy() {
////        RewardedVideoAd.destroy(this);
////        //stop service and stop music
////        stopService(new Intent(QuizMainActivity.this, SoundService.class));
////        super.onDestroy();
////    }
}
