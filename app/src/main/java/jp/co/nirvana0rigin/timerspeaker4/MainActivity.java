package jp.co.nirvana0rigin.timerspeaker4;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.LinearLayout;

import java.util.Calendar;

public class MainActivity
        extends AppCompatActivity
        implements GoConfig.OnGoConfigListener, Reset.OnResetListener, Start.OnStartListener, P {

    private Context con;
    private Resources res;
    private Bundle b;
    private FragmentManager fm;
    public static boolean isPause;

    private static Counter counter;
    private static Info info;
    private static Start start;
    private static GoConfig goConfig;
    private static Reset reset;
    private static Config config;
    private static BindTimer bt;

    private Fragment[] fragments;
    private int[] fragmentsID;
    private String[] fragmentsTag = {"counter", "info", "start", "reset", "go_config"};
    /*
        0: counter
        1: info
        2: start
        3: go_config
        4: reset
     */

    //________________________________________________________for life cycles

    //リソース生成のみ
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        res = getResources();
        con = getApplicationContext();
        P.Param.con = con;
        P.Param.res = getResources();
        b = savedInstanceState;
        fm = getSupportFragmentManager();
        P.Param.isBound = false;
        bt = new BindTimer(this);

        int[] fragmentsID2 = {R.id.counter, R.id.info, R.id.start, R.id.reset, R.id.go_config};
        fragmentsID = fragmentsID2;

    }

    //描画生成
    @Override
    public void onStart() {
        super.onStart();
        startService(new Intent(con, Timer.class));
        if(bt != null) {
            bt.bindTimer(P.Param.BIND);
        }else{
            stopService(new Intent(MainActivity.this, Timer.class));
            bt = new BindTimer(this);
            startService(new Intent(con, Timer.class));
            bt.bindTimer(P.Param.BIND);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        isPause = false;
        if (P.Param.isConfigMode()) {
            removeMainFragments();
        } else {
            createMainFragments();
            addMainFragments();
        }
        if (P.Param.isCounterRunning()) {
            bt.bindTimer(P.Param.START);
        }
    }

    @Override
    public void onPause() {
        isPause = true;
        super.onPause();
    }

    @Override
    public void onStop() {
        if (P.Param.isConfigMode()) {
            //NOTHING
        } else {
            start.carAnimStop();
            bt.bindTimer(P.Param.UNBIND);
        }
        if(P.Param.isReset()){
            stopService(new Intent(MainActivity.this, Timer.class));
        }
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        Log.d("_______main onD_____","_______   _______");
        super.onDestroy();
    }







    //___________________________________________________for connection on Fragments
    @Override
    public void onGoConfig() {
        removeMainFragments();
        if(isAlive("config")) {
            config.createConfig();
        }
    }

    @Override
    public void onReset() {
        if ( bt != null) {
            bt.bindTimer(P.Param.RESET);
        }
        if (counter != null) {
            counter.resetCounterText();
        }
        if (goConfig != null) {
            goConfig.addButton();
        }
        if (reset != null) {
            reset.removeButton();
        }
        if (start != null) {
            start.startButtonStatus();
        }
    }

    @Override
    public void onStartButton(Boolean isStop){
        if(isStop){
            reset.addButton();
            bt.bindTimer(P.Param.STOP);
        } else {
            if (P.Param.isTimeUp()) {
                P.Param.resetParam();
            }
            reset.removeButton();
            goConfig.removeButton();
            bt.bindTimer(P.Param.START);
        }
    }

    @Override
    public void onBackPressed() { //ハードのバックボタン
        if(P.Param.isConfigMode()) {
            P.Param.setConfigMode(false);
            //super.onBackPressed();
            createMainFragments();
            addMainFragments();
            onReset();
        }else{
            finish();
        }
    }






    //___________________________________________________________for work on Activity ____Fragments

    private void createMainFragments() {
        if(counter == null) {
            counter = new Counter();
        }
        if (info == null) {
            info = new Info();
        }
        if (start == null) {
            start = new Start();
        }
        if (goConfig == null) {
            goConfig = new GoConfig();
        }
        if (reset == null) {
            reset = new Reset();
        }
        Fragment[] fragments2 = {counter, info, start, reset,goConfig};
        fragments = fragments2;
    }

    private void addMainFragments(){
        P.Param.setConfigMode(false);
        FragmentTransaction transaction = fm.beginTransaction();

        for (int i = 0; i < 5; i++) {
            if (!isAlive(fragmentsTag[i])) {
                transaction.add(fragmentsID[i], fragments[i], fragmentsTag[i]);
            }else{
                transaction.show(fragments[i]);
            }
        }

        if(isAlive("config")){
            transaction.hide(config);
        }
        transaction.commit();
    }

    private void removeMainFragments(){
        P.Param.setConfigMode(true);
        FragmentTransaction transaction = fm.beginTransaction();

        if(!(isAlive("config")) ){
            config = null;
            config = new Config();
            transaction.add(R.id.config, config, "config");
        }else{
            transaction.show(config);
            //onGoConfigにてconfig.createConfig();
        }

        for(int i=0; i<5; i++){
            if(isAlive(fragmentsTag[i]) ) {
                if(fragments[i] != null) {
                    transaction.hide(fragments[i]);
                }
            }else{
                //NOTHING
            }
        }
        //transaction.addToBackStack(null);

        transaction.commit();
    }

    private boolean isAlive(String tag){
        if(fm.findFragmentByTag(tag) == null){
            return false;
        }else{
            return true;
        }
    }






	//___________________________________________________________for work on Activity ____AsyncTask&TIMAR
	public void rewriteCounter(){
		counter.createCounterText();
	}




}
