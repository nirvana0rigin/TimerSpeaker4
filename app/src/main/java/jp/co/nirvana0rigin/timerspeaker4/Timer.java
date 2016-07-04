package jp.co.nirvana0rigin.timerspeaker4;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Timer extends Service implements TextToSpeech.OnInitListener, P {

    private Context con ;
    private Resources res ;
    private TextToSpeech tts ;
    private static Notification.Builder builder;
    PowerManager pm ;
    PowerManager.WakeLock wl;
	//________________________________________________________for life cycles
    @Override
    public void onCreate() {
        super.onCreate();
        con = getApplicationContext();
        res = getResources();
        tts = new TextToSpeech(con, this);
        builder = new Notification.Builder(this);
        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "timer");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d("_______Timer onD_____","_______   _______");
        serviceEnd();
        super.onDestroy();
    }

	public class TimerBinder extends Binder {
        Timer getService() {
            return Timer.this;
        }
    }

	private final IBinder timerBinder = new TimerBinder();


    @Override
    public IBinder onBind(Intent intent) {
        return timerBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return false;
    }






	//________________________________________________________on this service TIMAR
	private static ScheduledExecutorService scheduler;
    private static ScheduledFuture<?> future;





    public void startTimer() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        future = scheduler.scheduleAtFixedRate(new Task(), 0, 10, TimeUnit.MILLISECONDS);
        builderInit(builder);
        setNotification(true);
        long continueTime =  (long)P.Param.getTimeLeft() + 5*60*1000;
        wl.acquire(continueTime);
        P.Param.setTimerRunning(true);
    }

    public void stopTimer() {
        if (future != null) {
            future.cancel(true);
            wl.release();
            P.Param.setTimerRunning(false);
        }
    }

    public void endTimer() {
        if (scheduler != null) {
            scheduler.shutdownNow();
            setNotification(false);
            P.Param.setTimerRunning(false);
        }
    }
    
    public void serviceEnd(){
    	stopTimer();
        endTimer();
        P.Param.setHalfwayStopped(true) ; 
        tts.shutdown();
        stopSelf();
    }

    private class Task implements Runnable {
        public void run() {
            int sec2 = P.Param.sec.incrementAndGet();
            
			if( sec2 % (P.Param.getInterval()*100) == 0){
                speakMinute(sec2/100);
			}

            if( P.Param.isTimeUp() ) {
                serviceEnd();
            }
        }
    }






	//________________________________________________________on this service SPEAK
    private static int lang = 1;
    private int interval = P.Param.getInterval();

	@Override
    public void onInit(int status) {
        String info;
        if (TextToSpeech.SUCCESS == status) {
            Locale locale = Locale.getDefault();
            if (tts.isLanguageAvailable(locale) >= TextToSpeech.LANG_AVAILABLE) {
                tts.setLanguage(locale);
                lang = 0;
            } else {
                locale = Locale.ENGLISH;
                if (tts.isLanguageAvailable(locale) >= TextToSpeech.LANG_AVAILABLE) {
                    tts.setLanguage(locale);
                    lang = 1;
                    info = con.getString(R.string.info2);
                    Toast.makeText(con, info, Toast.LENGTH_SHORT).show();
                }else{
                    lang = 2;
                    info = con.getString(R.string.info3);
                    Toast.makeText(con, info, Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            lang =3;
            info = con.getString(R.string.info4);
            Toast.makeText(con, info, Toast.LENGTH_SHORT).show();
        }
    }
    
    private static int s;
    private static int m;
    private void setSM(int sec2){
    	if(P.Param.isEndS()){
    		s = sec2;
    		m = 1000;
    	}else{
    		s = sec2 % 60;
    		m = sec2/60; 
    	}
    }

    private static String time;
    public void speakMinute(int sec2) {
     	setSM(sec2);
        String time2;
        String time1;
        if (lang == 0) {
            if(m == 1000){
                if(P.Param.getInterval() != 1) {
                    time = s + con.getString(R.string.sec_lang_jp);
                }else{
                    time = s + "";
                }
            }else {
            	time1 = m + con.getString(R.string.min_lang_jp);
            	if(s != 0){
                    time2 = s + con.getString(R.string.sec_lang_jp);
                    if(m != 0) {
                        time = time1 + time2;
                    }else{
                        time = time2;
                    }
            	}else{
            		time = time1; 
            	}
            }
        } else {
            if(m == 1000){
                time = s + con.getString(R.string.sec_lang_en);
            }else {
                time1 = m + con.getString(R.string.min_lang_en);
            	if(s != 0){
            		time2 = s + con.getString(R.string.sec_lang_en);
                    if(m != 0) {
                        time = time1 + time2;
                    }else{
                        time = time2;
                    }
            	}else{
            		time = time1; 
            	}
            }
        }
        speak(time);
    }

	public void speakMinute(String info1) {
        String info ;
        if (lang == 0) {
            String s = info1 + "_jp";
            int i = res.getIdentifier(s, "string", con.getPackageName());
            info = con.getString(i);
        } else {
            String s = info1 + "_en";
            int i = res.getIdentifier(s, "string", con.getPackageName());
            info = con.getString(i);
        }
        speak(info);
    }

    private void speak(String info){
        if(P.Param.getInterval() != 1){
            tts.setSpeechRate(1.0f);
        }else{
            tts.setSpeechRate(1.5f);
        }
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            tts.speak(info, TextToSpeech.QUEUE_FLUSH, null, "1");
        } else {
            tts.speak(info, TextToSpeech.QUEUE_FLUSH, null);
        }
    }





	//________________________________________________________on this service FOR UNDEAD
    private void builderInit(Notification.Builder builder){
        builder.setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0));
        builder.setTicker(getText(R.string.app_name));
        builder.setSmallIcon(R.drawable.c01b);
        builder.setContentTitle(getText(R.string.app_name));
        builder.setContentText(getString(R.string.now_running));
        builder.setWhen(System.currentTimeMillis());
        builder.setAutoCancel(false);
    }

    private void setNotification(boolean isStart) {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if(isStart){
            manager.notify(1, builder.build());
        }else{
            manager.cancel(1);
        }

    }

	






}
