package jp.co.nirvana0rigin.timerspeaker4;


import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;



public class Config extends Fragment implements View.OnClickListener, P {

    private Button[] cFlag = new Button[6];
    private Button[] iFlag = new Button[6];
    private Button[][] flag = {cFlag, iFlag};

    private Button car1;
    private Button car2;
    private Button car3;
    private Button car4;
    private Button intervalA;
    private Button intervalB;
    private Button intervalC;
    private Button intervalD;

    private View v;

    private Context con;
    private Resources res;






    //__________________________________________________for life cycles

    /*
    //初回は必ずここから起動
    public static Config newInstance(int[] param) {
        Config fragment = new Config();
        args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    */

    public Config() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        con = getActivity().getApplicationContext();
        res = getResources();
    }

    //Viewの生成のみ、表示はonStart
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_config, container, false);

        car1 = (Button) v.findViewById(R.id.car1);
        car1.setOnClickListener(this);
        cFlag[1] = car1;
        car2 = (Button) v.findViewById(R.id.car2);
        car2.setOnClickListener(this);
        cFlag[2] = car2;
        car3 = (Button) v.findViewById(R.id.car3);
        car3.setOnClickListener(this);
        cFlag[3] = car3;
        car4 = (Button) v.findViewById(R.id.car4);
        car4.setOnClickListener(this);
        cFlag[4] = car4;
        intervalA = (Button) v.findViewById(R.id.interval_a);
        intervalA.setOnClickListener(this);
        iFlag[1] = intervalA;
        intervalB = (Button) v.findViewById(R.id.interval_b);
        intervalB.setOnClickListener(this);
        iFlag[2] = intervalB;
        intervalC = (Button) v.findViewById(R.id.interval_c);
        intervalC.setOnClickListener(this);
        iFlag[3] = intervalC;
        intervalD = (Button) v.findViewById(R.id.interval_d);
        intervalD.setOnClickListener(this);
        iFlag[4] = intervalD;

        Button[][] flag2 = {cFlag, iFlag};
        flag = flag2;

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //NOTHING
    }

    //選択状況を表示
    @Override
    public void onStart() {
        super.onStart();
        setSelectColor();
    }

    /*
    public void onResume() {
    public void onStop() {
    public void onSaveInstanceState(Bundle outState) {
        //NOTHING
    */

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }








    //________________________________________________for connection on Activity








    //_________________________________________________for work on this Fragment

    @Override
    public void onClick(View v) {
        int id = v.getId();
        int choice = 0;
        int target = 0;
        switch (id) {
            case R.id.car1:     choice = 1;target = 0;break;
            case R.id.car2:     choice = 2;target = 0;break;
            case R.id.car3:     choice = 3;target = 0;break;
            case R.id.car4:     choice = 4;target = 0;break;
            case R.id.interval_a:        choice = 1;target = 1;break;
            case R.id.interval_b:        choice = 10;target = 1;break;
            case R.id.interval_c:        choice = 30;target = 1;break;
            case R.id.interval_d:        choice = 60;target = 1;break;
        }

        if(target==0){
            P.Param.setCarNo(choice);

        }else{
            P.Param.setInterval(choice);
        }
        setSelectColor();
    }

    private void setSelectColor(){
        setNotSelectColor();
        int carNo = P.Param.getCarNo();
        for(int i=0; i<2; i++) {
            if (i == 0) {
                String cs = "choice_car0" + carNo + "t";
                int ci = res.getIdentifier(cs, "drawable", con.getPackageName());
                ( flag[i][carNo]).setBackgroundResource(ci);
            }else{
                int x = P.Param.getInterval();
                if(x == 1){}  //NOTHING
                else if(x == 10){x = 2;}
                else if(x == 30){x = 3;}
                else{x = 4;}
                ( flag[i][x]).setBackgroundResource(R.drawable.choice_true);
            }
        }
    }

    private void setNotSelectColor(){
        for(int i=0; i<2; i++) {
            if (i == 0) {
                for (int j = 1; j < 5; j++) {
                    if (flag[i][j] == null) {
                        //NOTHING
                    } else {
                        String cs = "choice_car0"+j+"f";
                        int ci = res.getIdentifier(cs, "drawable", con.getPackageName());
                        ( flag[i][j]).setBackgroundResource(ci);
                    }
                }
            } else {
                for (int j = 1; j < 5; j++) {
                    if (flag[i][j] == null) {
                        //NOTHING
                    } else {
                        ( flag[i][j]).setBackgroundResource(R.drawable.choice_false);
                    }
                }
            }
        }
    }

    public void createConfig(){
        setSelectColor();
    }


}
