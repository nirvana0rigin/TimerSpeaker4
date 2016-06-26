package jp.co.nirvana0rigin.timerspeaker4;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;
import java.util.Locale;


public class Speak //extends Sync
        implements TextToSpeech.OnInitListener, P{

    //private static final int CHECK_TTS = 8;
    public TextToSpeech tts;
    private static int lang = 1;
    private Context con;
    private int interval;



    //___________________________________________for life cycles

    /*
    public Speak() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        checkTTS();
        tts = new TextToSpeech(con, this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        tts.shutdown();
    }
    */

    public Speak(Context con,int interval) {
        this.con = con;
        this.interval = interval;
        tts = new TextToSpeech(con, this);
    }







    //___________________________________________for connection on Activity










    //____________________________________________for work on Fragment

    /*
    private void checkTTS() {
        Intent intent = new Intent();
        intent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(intent, CHECK_TTS);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == CHECK_TTS) {
            if (resultCode != TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                Intent install = new Intent();
                install.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(install);
                String info = con.getString(R.string.info);
                Toast.makeText(con,info, Toast.LENGTH_LONG).show();
            }
        }
    }
    */

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

    public void speakMinute(String min) {
        Log.d("_______SPEAK_____","_________S/M________");
        String min2 ;
        if(min == "start"){
            if (lang == 0) {
                min2 = con.getString(R.string.start_jp);
            } else {
                min2 = min;    //NOTHING
            }
        }else {
            min2 = min;
            if (lang == 0) {
                if(interval <= 10){
                    min2 = min + con.getString(R.string.sec_lang_jp);
                }else {
                    min2 = min + con.getString(R.string.min_lang_jp);
                }
            } else {
                if(interval > 10){
                    min2 = min + con.getString(R.string.sec_lang_en);
                }else {
                    min2 = min + con.getString(R.string.min_lang_en);
                }
            }
        }

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            tts.speak(min2, TextToSpeech.QUEUE_FLUSH, null, "1");
        } else {
            tts.speak(min2, TextToSpeech.QUEUE_FLUSH, null);
        }
    }


}
