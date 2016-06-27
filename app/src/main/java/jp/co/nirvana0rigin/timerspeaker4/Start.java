package jp.co.nirvana0rigin.timerspeaker4;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

//import android.graphics.Bitmap;

public class Start extends Fragment implements View.OnClickListener, P {

    private OnStartListener mListener;
    private ImageView carView;
    private Button start;
    private View v;
    private ObjectAnimator anim;
    private Context con;
    private Resources res;

    //__________________________________________________for life cycles

    /*
    public static Start newInstance(int[] p) {
        Start fragment = new Start();
        args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    */

    public Start() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        con = getActivity().getApplicationContext();
        res = getResources();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle outState) {
        v = inflater.inflate(R.layout.fragment_start, container, false);

        carImageReset();
        start = (Button) v.findViewById(R.id.start_b);
        start.setOnClickListener(this);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //NOTHING
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        startButtonStatus();
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        //carBitmap.recycle();
        //carBitmap = null;
    }

    /*
    public void onSaveInstanceState(Bundle outState) {
        //NOTHING
    */


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }







    //________________________________________________for connection on Activity

    public interface OnStartListener {
        public void onStartButton();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnStartListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnStartListener");
        }
    }

    public void onButtonPressed() {
        if (mListener != null) {
            mListener.onStartButton();
        }
    }








    //_________________________________________________for work on this Fragment

    public void startButtonStatus(){
        if(P.Param.isCounterRunning()) {
            start.setBackgroundResource(R.drawable.start_true);
            start.setText(R.string.stop);
            carAnimStart();
        }else{
            carAnimStop();
            start.setBackgroundResource(R.drawable.start_false);
            start.setText(R.string.start);
            if(anim.isRunning()) {
                carAnimStop();
            }
        }
    }

    private void setCarAnim(ImageView target, int car, long interval) {
        anim = ObjectAnimator.ofFloat(target, "rotation", 0f, 360f);
        int speedParMilliSeconds =1000 * car;
        int maxSeconds;
        if(P.Param.isEndS()) {
            maxSeconds = P.Param.END_S;
        }else {
            maxSeconds = P.Param.END_L;
        }
        anim.setDuration(speedParMilliSeconds);
        anim.setInterpolator(new LinearInterpolator());
        int repeatCount = maxSeconds / car;
        anim.setRepeatCount(repeatCount);
        anim.setRepeatMode(ObjectAnimator.RESTART);
    }

    public void carAnimStart() {
        if (android.os.Build.VERSION.SDK_INT >= 19) {
            if (anim.isPaused()) {
                anim.resume();
            } else {
                anim.start();
            }
        } else {
            anim.start();
        }
    }

    public void carAnimStop() {
        if (android.os.Build.VERSION.SDK_INT >= 19) {
            if (anim != null) {
                if (anim.isRunning()) {
                    anim.pause();
                }
            }
        } else {
            if (anim != null) {
                if (anim.isRunning()) {
                    anim.cancel();
                    carImageReset();
                }
            }
        }
    }

    private void carImageReset(){
        carView = null;
        carView = (ImageView) v.findViewById(R.id.car_img);
        int carNo = P.Param.getCarNo() ;
        String carStr = ("c"+ 1 ) + carNo;
        int ir = res.getIdentifier(carStr, "drawable", con.getPackageName());
        carView.setImageResource(ir);
        anim = null;
        setCarAnim(carView, carNo, P.Param.getInterval());
    }

    @Override
    public void onClick(View v) {
        //スレッド生→生、タイマー止→生、
        if (P.Param.isCounterRunning()) {   //要は動作→一時停止
            P.Param.setHalfwayStopped(true); //view
            P.Param.setReset(false); //thread
        //スレッド生→生、タイマー止→生
        //スレッド死→生、タイマー止→生
        } else {               //要はSTART又はRESTART
            P.Param.setHalfwayStopped(false); //view
            P.Param.setReset(false); //thread
        }
        startButtonStatus();
        onButtonPressed();
    }



}