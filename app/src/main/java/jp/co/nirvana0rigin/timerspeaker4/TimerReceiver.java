package jp.co.nirvana0rigin.timerspeaker4;


import android.os.AsyncTask;

public class TimerReceiver extends AsyncTask<Integer, Integer, Integer>
    implements P{

    private MainActivity a;
    public TimerReceiver(MainActivity a){
        this.a = a;
    }

     @Override
    protected Integer doInBackground(Integer... value) {
        int delay = value[0];
        if(delay != 0){
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

                if (a.isPause) {
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
        a.rewriteCounter();
    }

}



