package com.example.juego;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements SensorEventListener, GestureDetector.OnGestureListener {

    private TextView instructionTextView;
    private TextView scoreTextView;
    private TextView livesTextView;

    private SensorManager sensorManager;
    private Sensor accelerometer;

    private int currentLevel = 1;
    private int score = 0;
    private int lives = 3;

    private Handler handler;
    private Runnable changeInstructionRunnable;

    private SharedPreferences sharedPreferences;

    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instructionTextView = findViewById(R.id.instructionTextView);
        scoreTextView = findViewById(R.id.scoreTextView);
        livesTextView = findViewById(R.id.livesTextView);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        handler = new Handler();

        sharedPreferences = getSharedPreferences("GamePreferences", Context.MODE_PRIVATE);

        gestureDetector = new GestureDetector(this, this);

        startGame();
    }

    private void startGame() {
        score = 0;
        lives = 3;
        updateScore();
        updateLives();
        showNewInstruction();
    }

    private void resetGame() {
        currentLevel = 1;
        score = 0;
        lives = 3;
        updateScore();
        updateLives();
    }

    private void updateScore() {
        scoreTextView.setText("Score: " + score);
    }

    private void updateLives() {
        livesTextView.setText("Lives: " + lives);
    }

    private void showNewInstruction() {
        String instruction = getRandomInstruction();
        instructionTextView.setText(instruction);
        startInstructionTimer();
    }

    private String getRandomInstruction() {
        // Generate a random instruction based on the current level
        switch (currentLevel) {
            case 1:
                return getRandomInstructionFromList("Tap the screen", "Shake the device", "Swipe left");
            case 2:
                return getRandomInstructionFromList("Double-tap the screen", "Rotate the device", "Swipe right");
            case 3:
                return getRandomInstructionFromList("Long-press the screen", "Tilt the device", "Swipe up");
            default:
                return "";
        }
    }

    private String getRandomInstructionFromList(String... instructions) {
        int randomIndex = new Random().nextInt(instructions.length);
        return instructions[randomIndex];
    }

    private void startInstructionTimer() {
        handler.postDelayed(changeInstructionRunnable, getInstructionTimeLimit());
    }

    private int getInstructionTimeLimit() {
        // Calculate the time limit for the current level
        switch (currentLevel) {
            case 1:
                return 10000; // 10 seconds
            case 2:
                return 7000; // 7 seconds
            case 3:
                return 5000; // 5 seconds
            default:
                return 10000;
        }
    }

    private void checkGesture(String expectedGesture, String gesture) {
        handler.removeCallbacks(changeInstructionRunnable);

        if (expectedGesture.equals(gesture)) {
            score++;
            updateScore();
            if (score % 3 == 0) {
                levelCompleted();
            } else {
                showNewInstruction();
            }
        } else {
            lives--;
            updateLives();
            if (lives > 0) {
                Toast.makeText(this, "Wrong gesture! Try again.", Toast.LENGTH_SHORT).show();
                showNewInstruction();
            } else {
                gameOver();
            }
        }
    }

    private void levelCompleted() {
        currentLevel++;
        Toast.makeText(this, "Level completed! Proceed to the next level.", Toast.LENGTH_SHORT).show();
        showNewInstruction();
    }

    private void gameOver() {
        Toast.makeText(this, "Game over!", Toast.LENGTH_SHORT).show();
        resetGame();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do nothing
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // TODO: Implement sensor event handling for shake gesture
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        // Do nothing
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        // Do nothing
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        checkGesture("Tap the screen", "TAP");
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        // Do nothing
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        // Do nothing
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        // Do nothing
        return true;
    }
}


