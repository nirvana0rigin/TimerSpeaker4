package jp.co.nirvana0rigin.timerspeaker4;

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
    private boolean isBound;
    private static boolean isPause;

    private static Counter counter;
    private Info info;
    private Start start;
    private GoConfig goConfig;
    private Reset reset;
    private Config config;

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
        Log.d("_____main onC____", "_______  ______");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        res = getResources();
        con = getApplicationContext();
        b = savedInstanceState;
        fm = getSupportFragmentManager();

        int[] fragmentsID2 = {R.id.counter, R.id.info, R.id.start, R.id.reset, R.id.go_config};
        fragmentsID = fragmentsID2;

    }

    //描画生成
    @Override
    public void onStart() {
        super.onStart();
        Log.d("_____main onStart____", "_______  ______");

        startService(new Intent(con, Timer.class));
        doBindService();
    }

    @Override
    public void onResume() {
        super.onResume();
        isPause = false;
        if (P.Param.isConfigMode()) {
            Log.d("_____main onResume____", "_______config______");
            removeMainFragments();
        } else {
            Log.d("_____main onResume____", "_______NOT config______");
            createMainFragments();
            addMainFragments();
        }
        if (P.Param.isCounterRunning()) {
            startTimer();
        }
    }

    @Override
    public void onPause() {
        Log.d("_____main onPause____", "_______  ______");
        isPause = true;
        if (P.Param.isConfigMode()) {
            //NOTHING
        } else {
            start.carAnimStop();
            doUnbindService();
        }
        super.onPause();
    }

    @Override
    public void onStop() {
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
        Log.d("_______main onGoC_____","_______   _______");
        removeMainFragments();
        if(isAlive("config")) {
            Log.d("_____onGoC___","_______isA_____");
            config.createConfig();
        }
    }

    @Override
    public void onReset() {

        if (timer != null) {
            doUnbindService();
            stopService(new Intent(MainActivity.this, Timer.class));
            resetTimer();
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
        if (reset != null) {
            start.startButtonStatus();
        }
    }

    @Override
    public void onStartButton() {
        if (P.Param.isCounterRunning()) {
            if (P.Param.isTimeUp()) {
                P.Param.resetParam();
            }
            startService(new Intent(con, Timer.class));
            doBindService();
            goConfig.removeButton();
            reset.removeButton();
            startTimer();
        } else {
            reset.addButton();
            stopTimer();
        }
    }

    @Override
    public void onBackPressed() {
        Log.d("___main onB__", "_____  ______");
        if (0 == fm.getBackStackEntryCount()) {
            super.onBackPressed();
        } else {
            Log.d("___main onB__", "_____ !0 ______");
            if(P.Param.isConfigMode()) {
                P.Param.setConfigMode(false);
                super.onBackPressed();
                createMainFragments();
                addMainFragments();
            }else{
                Log.d("___main onB__", "_____ !0 finish______");
                finish();
            }
        }
    }







	//___________________________________________________for connection on Service
    private static Timer timer;
	private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            timer = ((Timer.TimerBinder) service).getService();
        }
        public void onServiceDisconnected(ComponentName className) {
            timer = null;
        }
    };

    private void doBindService() {
        bindService(new Intent(con, Timer.class), mConnection, Context.BIND_AUTO_CREATE);
        isBound = true;
    }

    private void doUnbindService() {
        if (isBound) {
            unbindService(mConnection);
            isBound = false;
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
        if(isAlive("config")){
            transaction.remove(config);
        }
        for (int i = 0; i < 5; i++) {
            if (!isAlive(fragmentsTag[i])) {
                transaction.add(fragmentsID[i], fragments[i], fragmentsTag[i]);
            }
        }
        transaction.commit();
    }

    private void removeMainFragments(){
        Log.d("_____removeMainFra___","_______   _______");
        P.Param.setConfigMode(true);
        FragmentTransaction transaction = fm.beginTransaction();
        for(int i=0; i<5; i++){
            if(isAlive(fragmentsTag[i]) ) {
                if(fragments[i] != null) {
                    transaction.remove(fragments[i]);
                }
            }
        }
        transaction.addToBackStack(null);

        if(!(isAlive("config")) ){
            Log.d("_____removeMainFra___","_______ !isA  _______");
            config = null;
            config = new Config();
            transaction.add(R.id.config, config, "config");
        }else{
            //onGoConfigにてconfig.createConfig();
        }
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
    private static final String START = "start";
	private void startTimer(){
        int delay = P.Param.getDelay();
        TimeReceiver timeReceiver = new TimeReceiver();
        timeReceiver.execute(delay);
        if(!P.Param.isTimerRunning()) {
            timer.speakMinute(START);
            timer.startTimer();
        }
    }

	private void stopTimer(){
		timer.stopTimer();
	}

	private void resetTimer(){
		timer.endTimer();
		P.Param.resetParam();
	}

	private void rewriteCounter(){
		counter.createCounterText();
	}






	//___________________________________________________________for work on Activity ____AsyncTask
    private class TimeReceiver extends AsyncTask<Integer, Integer, Integer> {

        @Override
        protected Integer doInBackground(Integer... value) {
        	int delay = value[0];
        	if(delay != 0){
                Log.d("_____async____","_______delay!=0______");
            	try {
                	publishProgress(delay);
                	Thread.sleep(delay);
            	} catch (InterruptedException e) {
                	//NOTHING
            	}
            }
            while (P.Param.isCounterRunning()) {
                try {
                    publishProgress(delay);
                    Thread.sleep(1000);

                    if (isPause) {
                        break;
                    }
                    if(P.Param.isTimeUp()){
                        break;
                    }
                } catch (InterruptedException e) {
                    //NOTHING
                }
            }
            return delay;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            rewriteCounter();
        }

    }





}
