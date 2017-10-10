package incorporation.app.primera.mi.proyectofinal;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

import java.util.Timer;
import java.util.TimerTask;

public class Splash extends AppCompatActivity {


    private static final long DELAY = 1500;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//solo funciona en portrait
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);//quita la barra de arriba

        setContentView(R.layout.activity_splash);//se baja para que no salga error, porque sno abre primero est actividad

        TimerTask task = new TimerTask() {
            @Override
            public void run() {

                    intent = new Intent(Splash.this, Login.class);

                startActivity(intent);
                finish();
            }
        };
        Timer timer = new Timer();
        timer.schedule(task,DELAY);//que se cunpla esta tarea(task)cuando se cumpla este tiempo (SPLASH_DELAY)
    }
}
