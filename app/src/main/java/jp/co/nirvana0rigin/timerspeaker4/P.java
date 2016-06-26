package jp.co.nirvana0rigin.timerspeaker4;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;


public interface P {


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
        private static boolean running;   //現在カウントが走っているか否か

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
            running = false;
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
            running = false;
        }

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
                setRunning(false);
            }
            /*if (halfwayStopped == false && reset == false) {
                running = true;
            } else {
                running = false;
            }*/
        }

        public static boolean isReset() {
            return reset;
        }

        public static void setReset(boolean x) {
            reset = x;
            if (reset) {
                setHalfwayStopped(false);
                setRunning(false);
            }
            /*if (halfwayStopped == false && reset == false) {
                running = true;
            } else {
                running = false;
            }*/
        }

        public static boolean isRunning() {
            return running;
        }

        public static void setRunning(boolean x){
            running = x;
        }

        public static void resetParam() {
            setHalfwayStopped(false);
            setReset(true);
            sec.set(0);
        }

        public static int getDelay() {
            int spent;
            if (isEndS) {
                spent = END_S - sec.get();
            } else {
                spent = END_L - sec.get();
            }
            int delay = (spent - ((spent / 100) * 100)) * 10;
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
