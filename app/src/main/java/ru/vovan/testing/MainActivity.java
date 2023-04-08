package ru.vovan.testing;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorAdditionalInfo;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    TextView accelerometer, gyroscope, rotation;
    SensorManager sensorManager;

    Sensor senAccelerometer, senGyroscope, senRotation;
    float accelerometerX, accelerometerY, accelerometerZ, gyroscopeX, gyroscopeY, gyroscopeZ,
            rotationX, rotationY, rotationZ;
    private SensorEventListener sensorEventListener;
    ImageView levelBuild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        accelerometer = findViewById(R.id.accelerometer);
        gyroscope = findViewById(R.id.gyroscope);
        rotation = findViewById(R.id.rotation);
        levelBuild = findViewById(R.id.levelBuild);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senGyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        senRotation = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        sensorEventListener = new SensorEventListener() {
            public void onSensorChanged(SensorEvent event) {
                Sensor multySensor=event.sensor;
                switch (multySensor.getType()){
                    case Sensor.TYPE_ACCELEROMETER:
                        accelerometerX = event.values[0];
                        accelerometerY = event.values[1];
                        accelerometerZ = event.values[2];
                        accelerometer.setText("данные акселерометра (м/с^2):\nx = " + accelerometerX +
                                "\ny = "+accelerometerY + "\nz = "+accelerometerZ);
                        break;
                    case Sensor.TYPE_GYROSCOPE:
                        gyroscopeX = event.values[0];
                        gyroscopeY = event.values[1];
                        gyroscopeZ = event.values[2];
                        gyroscope.setText("Данные гироскопа (рад/с):\nx = " + gyroscopeX +
                                "\ny = " + gyroscopeY + "\nz = "+gyroscopeZ);
                        break;
                    case Sensor.TYPE_ROTATION_VECTOR:
                        rotation.setText("Данные угла наклона уровня:\n"+
                                String.format("%.2f", vectorToDegree(event))+"градусов");
                        levelBuild.setRotation(-vectorToDegree(event));
                        break;
                }
            }
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };
    }
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(sensorEventListener,senAccelerometer,SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(sensorEventListener,senGyroscope,SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(sensorEventListener,senRotation,SensorManager.SENSOR_DELAY_NORMAL);
    }
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(sensorEventListener);
    }

    private float vectorToDegree(SensorEvent event) {
        float[] rotationMatrix = new float[16];
        SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
        float[] remappedRotationMatrix = new float[16];
        SensorManager.remapCoordinateSystem(rotationMatrix, SensorManager.AXIS_X,
                SensorManager.AXIS_Z, remappedRotationMatrix);
        float[] orientations = new float[3];
        SensorManager.getOrientation(remappedRotationMatrix, orientations);
        for (int i = 1; i < 3; i++) {
        orientations[i] = (float) (Math.toDegrees(orientations[i]));
        }
        return orientations[2];
    }
}