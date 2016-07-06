package jp.co.nirvana0rigin.timerspeaker4;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

public class BindTimer
        implements P {

    MainActivity a;
    TimerReceiver tr;
    public static Timer timer;
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            timer = ((Timer.TimerBinder) service).getService();
        }

        public void onServiceDisconnected(ComponentName className) {
            timer = null;
        }
    };
    private static final String START = "start";

    public BindTimer(MainActivity a) {
        this.a = a;
    }

    public void bindTimer(int r) {
        switch (r) {
            case P.Param.BIND:
                if (!P.Param.isBound) {
                    a.bindService(new Intent(P.Param.con, Timer.class), mConnection, Context.BIND_AUTO_CREATE);
                    P.Param.isBound = true;
                }
                break;
            case P.Param.UNBIND:
                if (P.Param.isBound) {
                    a.unbindService(mConnection);
                    P.Param.isBound = false;
                }
                break;
            case P.Param.START:
                int delay = P.Param.getDelay();
                if (timer != null) {
                    tr = new TimerReceiver(a);
                    tr.execute(delay);
                    if(!P.Param.isTimerRunning()) {
                        timer.speakMinute(START);
                        timer.startTimer();
                    }
                }
                break;
            case P.Param.STOP:
                if (timer != null) {
                    timer.stopTimer();
                    tr = null;
                }
                break;
            case P.Param.RESET:
                timer.endTimer();
                P.Param.resetParam();
                break;

            default:
                Log.d("___BindTimer___","______CASE   NO THING____");
        }
    }

}