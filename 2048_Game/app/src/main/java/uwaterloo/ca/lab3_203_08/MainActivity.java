package uwaterloo.ca.lab3_203_08;


import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Timer;


public class MainActivity extends AppCompatActivity {

    RelativeLayout rl;
    // dynamically set up the gameboard size based on the image in drawable folder
    public static final int GAMEBOARD_DIMENSION = Resources.getSystem().getDisplayMetrics().widthPixels - 80;
    final int GAMELOOPRATE = 25;

    GameLoopTask myGameLoop;
    Timer myGameLoopTimer;

    TextView tvAccelerometer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // layout
        rl = (RelativeLayout) findViewById(R.id.relative_layout1);
        rl.getLayoutParams().width = GAMEBOARD_DIMENSION;
        rl.getLayoutParams().height = GAMEBOARD_DIMENSION;

        //set up the gameboard
        ImageView gameBoard = new ImageView(getApplicationContext());
        gameBoard.setImageResource(R.drawable.gameboard);
        gameBoard.setPivotX(0);
        gameBoard.setPivotY(0);
        rl.addView(gameBoard);


        tvAccelerometer = new TextView(getApplicationContext());
        tvAccelerometer.setTextSize(40.0f);

        // set up the loop timer
        myGameLoop = new GameLoopTask(this, rl, getApplicationContext());
        myGameLoopTimer = new Timer();
        myGameLoopTimer.schedule(myGameLoop, GAMELOOPRATE, GAMELOOPRATE);


        // sensor manager and register
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        Sensor accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        AccelerometerEventListener accelListener = new AccelerometerEventListener(tvAccelerometer, myGameLoop);
        sensorManager.registerListener(accelListener, accelSensor, SensorManager.SENSOR_DELAY_GAME);

    }
}
