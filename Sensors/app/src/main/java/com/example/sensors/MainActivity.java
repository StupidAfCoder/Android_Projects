package com.example.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor lightSensor;
    private Sensor proximitySensor;

    private TextView tvAccel, tvLight, tvProximity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvAccel     = findViewById(R.id.tvAccelerometer);
        tvLight     = findViewById(R.id.tvLight);
        tvProximity = findViewById(R.id.tvProximity);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer   = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        lightSensor     = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        if (accelerometer   == null) tvAccel.setText("Accelerometer: Not available");
        if (lightSensor     == null) tvLight.setText("Light Sensor: Not available");
        if (proximitySensor == null) tvProximity.setText("Proximity: Not available");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (accelerometer   != null) sensorManager.registerListener(this, accelerometer,   SensorManager.SENSOR_DELAY_NORMAL);
        if (lightSensor     != null) sensorManager.registerListener(this, lightSensor,     SensorManager.SENSOR_DELAY_NORMAL);
        if (proximitySensor != null) sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    // Called every time any registered sensor reports new data
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event == null) return;

        switch (event.sensor.getType()) {

            case Sensor.TYPE_ACCELEROMETER:
                float x = event.values[0], y = event.values[1], z = event.values[2];
                tvAccel.setText(String.format("Accelerometer\nX: %.2f m/s²\nY: %.2f m/s²\nZ: %.2f m/s²", x, y, z));
                break;

            case Sensor.TYPE_LIGHT:
                float lux = event.values[0];
                tvLight.setText(String.format("Light Sensor\nIlluminance: %.1f lux", lux));
                break;

            case Sensor.TYPE_PROXIMITY:
                float dist = event.values[0];
                String state = dist < proximitySensor.getMaximumRange() ? "NEAR" : "FAR";
                tvProximity.setText(String.format("Proximity Sensor\nDistance: %.1f cm\nState: %s", dist, state));
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }
}