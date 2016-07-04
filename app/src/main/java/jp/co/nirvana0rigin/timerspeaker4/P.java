package jp.co.nirvana0rigin.timerspeaker4;

import android.content.Context;
import android.content.res.Resources;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;


public interface P {

    /*
    実装すべきメソッドはなし。
    共通パラメーターを持つためのインターフェイス。
     */

    static class Param implements Serializable {

        private static final long serialVersionUID = 10L;

        private static int carNo;  //アニメの車用。
        private static int interval;  //秒。音声間隔。
        public static final int END_S = 10 * 60 * 100;  //10minute*sec*100
        public static final int END_L = 60 * 60 * 100;  //60minute*sec*100
        private static boolean isEndS;  //SかLかのフラグ

        public static AtomicInteger sec = new AtomicInteger();        //センチ秒。現在進行時間。

        private static boolean halfwayStopped; //途中でストップ状態か否か
        private static boolean reset;  //リセット状態か否か。
        private static boolean counterRunning;   //現在「外面上で」カウントが走るべきか否か
        private static boolean timerRunning;  //現在「サービスで」カウントが走っているか否か
        private static boolean configMode;  //現在Config中か否か

        public static Context con;
        public static Resources res;
        public  static boolean isBound;

        public static final int BIND = 00;
        public static final int UNBIND = 01;
        public static final int START = 10;
        public static final int STOP = 11;
        public static final int RESET = 20;


        //___________________________________________________________for init
        static{
            carNo = 1;
            interval = 1;
            isEndS = true;

            sec.set(0);
        /* 加算処理はサービスのタイマーのみ
        	リセットはリセットボタン又はスタートのみ
        */

            halfwayStopped = false;
            reset = true;
            counterRunning = false;
            timerRunning = false;
            configMode = false;
        }

        public Param() {
            carNo = 1;
            interval = 1;
            isEndS = true;

            sec.set(0);
        /* 加算処理はサービスのタイマーのみ
        	リセットはリセットボタン又はスタートのみ
        */

            halfwayStopped = false;
            reset = true;
            counterRunning = false;
            timerRunning = false;
            configMode = false;
        }




        //___________________________________________________________for set&get
        public static int getCarNo() {
            return carNo;
        }

        public static void setCarNo(int x) {
            carNo = x;
        }

        public static int getInterval() {
            return interval;
        }

        public static void setInterval(int x) {
            interval = x;
            if (interval <= 10) {
                isEndS = true;
            } else {
                isEndS = false;
            }
        }

        public static boolean isEndS() {
            return isEndS;
        }
        //書き換えはintervalでするのみ

        public static boolean isHalfwayStopped() {
            return halfwayStopped;
        }

        public static void setHalfwayStopped(boolean x) {
            halfwayStopped = x;
            if (halfwayStopped) {
                setCounterRunning(false);
            }else if(!reset){
                counterRunning = true;
            }
        }

        public static boolean isReset() {
            return reset;
        }

        public static void setReset(boolean x) {
            reset = x;
            if (reset) {
                setHalfwayStopped(false);
                setCounterRunning(false);
            }else if (!halfwayStopped) {
                counterRunning = true;
            }
        }

        public static boolean isCounterRunning() {
            return counterRunning;
        }

        public static void setCounterRunning(boolean x){
            counterRunning = x;
        }

        public static boolean isTimerRunning() {
            return timerRunning;
        }

        public static void setTimerRunning(boolean x){
            timerRunning = x;
        }

        public static boolean isConfigMode() {
            return configMode;
        }

        public static void setConfigMode(boolean x){
            configMode = x;
        }

        //___________________________________________________________for other
        public static void resetParam() {
            setHalfwayStopped(false);
            setReset(true);
            sec.set(0);
        }

        public static int getTimeLeft() {
            int tl;
            if (isEndS) {
                tl = END_S - sec.get();
            } else {
                tl = END_L - sec.get();
            }
            return tl;
        }

        public static int getDelay() {
            int tl = getTimeLeft();
            int delay = (tl - ((tl / 100) * 100)) * 10;
            return delay;
        }

        public static boolean isTimeUp() {
            if (isEndS) {
                if (sec.get() >= END_S) {
                    return true;
                } else {
                    return false;
                }
            } else {
                if (sec.get() >= END_L) {
                    return true;
                } else {
                    return false;
                }
            }
        }

    }

}
