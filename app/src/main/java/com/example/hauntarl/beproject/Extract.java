package com.example.hauntarl.beproject;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.aakira.expandablelayout.ExpandableRelativeLayout;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Extract.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Extract#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Extract extends Fragment implements SensorEventListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    ExpandableRelativeLayout expandableLayout;
    private Button btnEx, btnEnd;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private Sensor senGyro;
    private TextView xVal;
    private TextView yVal;
    private TextView zVal;
    private TextView xGyroVal;
    private TextView yGyroVal;
    private TextView zGyroVal;
    private TextView xLoc;
    private TextView yLoc;
    private TextView speed;
    private LocationManager locationManager;
    private File folder;
    private String filename;
    private FileWriter fileWriter;

    private String[] accValues;
    private String[] gyroValues;
    private String[] locValues;
    private String velocity;
    private long  timestamp;
    private int startNew = 1;

    private int flag=1;


    private float[] gravity = new float[]
            {0, 0, 0};
    private float zval = 0f;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 222) {
            int i = 0;
            for (i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Please give the required permissions!!", Toast.LENGTH_LONG).show();
                    getAllPermissions();
                }
            }

        }
    }
    private void getAllPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(
                    new String[]{android.Manifest.permission
                            .READ_EXTERNAL_STORAGE, android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_CALENDAR},
                    222);
        }
    }

    public Extract() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Extract.
     */
    // TODO: Rename and change types and number of parameters
    public static Extract newInstance(String param1, String param2) {
        Extract fragment = new Extract();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_extract, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnEx = (Button) view.findViewById(R.id.expandableButton);
        btnEnd = (Button) view.findViewById(R.id.btn_end);
        expandableLayout = (ExpandableRelativeLayout) view.findViewById(R.id.expandableLayout);

        accValues = new String[3];
        gyroValues = new String[3];
        locValues = new String[3];

        getAllPermissions();



        checkGpsAvailability();

        xVal = view.findViewById(R.id.accX);
        yVal = view.findViewById(R.id.accY);
        zVal = view.findViewById(R.id.accZ);

        xGyroVal = view.findViewById(R.id.gyroX);
        yGyroVal = view.findViewById(R.id.gyroY);
        zGyroVal = view.findViewById(R.id.gyroZ);

        xLoc = view.findViewById(R.id.locX);
        yLoc = view.findViewById(R.id.locY);

        speed = view.findViewById(R.id.speed);

        MyLocationListener locationListener = new MyLocationListener();
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

        senSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        if(senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)!=null){
            senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            senSensorManager.registerListener( this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        }
        else{
            Toast.makeText(getActivity().getApplicationContext(),"This device doesn't have accelerometer support!!",Toast.LENGTH_LONG).show();
            getActivity().finish();
        }
        if(senSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)!= null){
            senGyro = senSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            SensorEventListener gyroSensorListener = new SensorEventListener() {

                @Override
                public void onSensorChanged(SensorEvent event) {
                    xGyroVal.setText("X-axis : "+String.valueOf(event.values[0]));
                    yGyroVal.setText("Y-axis : "+String.valueOf(event.values[1]));
                    zGyroVal.setText("Z-axis : "+String.valueOf(event.values[2]));

                    gyroValues[0] = String.valueOf(event.values[0]);
                    gyroValues[1] = String.valueOf(event.values[1]);
                    gyroValues[2] = String.valueOf(event.values[2]);

                    writeValues();
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {

                }
            };
            senSensorManager.registerListener(gyroSensorListener,senGyro,SensorManager.SENSOR_DELAY_NORMAL);
        }
        else{
            Toast.makeText(getActivity().getApplicationContext(),"The device doesn't have gyroscope support!!", Toast.LENGTH_LONG).show();

            getActivity().finish();

        }

        btnEx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag=0;
                expandableLayout.toggle();
                btnEx.setText("Collecting Data...");
                if(startNew==1) {
                    openAFile();
                    startNew=0;
                }
            }
        });
        btnEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag=1;
                expandableLayout.collapse();
                startNew=1;
                btnEx.setText("Start New Journey...");
            }
        });

    }
    private void writeValues() {
        try {

            if(flag==0) {
                fileWriter = new FileWriter(filename, true);
                timestamp = Calendar.getInstance().getTimeInMillis();
                fileWriter.append(locValues[0]);
                fileWriter.append(',');
                fileWriter.append(locValues[1]);
                fileWriter.append(',');
                fileWriter.append(accValues[0]);
                fileWriter.append(',');
                fileWriter.append(accValues[1]);
                fileWriter.append(',');
                fileWriter.append(accValues[2]);
                fileWriter.append(',');
                fileWriter.append(gyroValues[0]);
                fileWriter.append(',');
                fileWriter.append(gyroValues[1]);
                fileWriter.append(',');
                fileWriter.append(gyroValues[2]);
                fileWriter.append(',');
                fileWriter.append(velocity);
                fileWriter.append(',');
                fileWriter.append(String.valueOf(Calendar.getInstance().getTimeInMillis()));
                fileWriter.append(',');
                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                String formattedDate = df.format(Calendar.getInstance().getTime());
                fileWriter.append(formattedDate);
                fileWriter.append("\n");
                fileWriter.close();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkGpsAvailability() {
        LocationManager service = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        boolean enabled = service
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        Toast.makeText(getActivity().getApplicationContext(), "Please enable GPS!!",Toast.LENGTH_SHORT).show();

        if (!enabled) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
    }
    private void openAFile() {
        folder = new File(Environment.getExternalStorageDirectory()
                + "/Sensor Values");
        if (!folder.exists())
            folder.mkdir();
        filename = folder.toString() + "/" +String.valueOf(Calendar.getInstance().getTime()+" Values.csv");

        try {
            fileWriter  = new FileWriter(filename);
            fileWriter.append("LAT");
            fileWriter.append(',');
            fileWriter.append("LONG");
            fileWriter.append(',');
            fileWriter.append("accX");
            fileWriter.append(',');
            fileWriter.append("accY");
            fileWriter.append(',');
            fileWriter.append("accZ");
            fileWriter.append(',');
            fileWriter.append("gyroX");
            fileWriter.append(',');
            fileWriter.append("gyroY");
            fileWriter.append(',');
            fileWriter.append("gyroZ");
            fileWriter.append(',');
            fileWriter.append("Speed");
            fileWriter.append(',');
            fileWriter.append("Timestamp In mills");
            fileWriter.append(',');
            fileWriter.append("Timestamp In Date");
            fileWriter.append("\n");
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location loc) {
            xLoc.setText("Latitude: "+String.valueOf(loc.getLatitude()));
            yLoc.setText("Longitude: "+String.valueOf(loc.getLongitude()));
            speed.setText(String.valueOf(loc.getSpeed()));

            locValues[0] = String.valueOf(loc.getLatitude());
            locValues[1] = String.valueOf(loc.getLongitude());
            velocity = String.valueOf(loc.getSpeed());
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {
            //Toast.makeText(getApplicationContext(),"Make sure that GPS is ON",Toast.LENGTH_SHORT).show();
            Intent gpsOptionsIntent = new Intent(
                    android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(gpsOptionsIntent);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor mySensor = event.sensor;
        final float alpha = 0.8f;


        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];
        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            xVal.setText("X-axis : "+String.valueOf(event.values[0]-gravity[0]));
            yVal.setText("Y-axis : "+String.valueOf(event.values[1]-gravity[1]));
            zVal.setText("Z-axis : "+String.valueOf(event.values[1]-gravity[1]));

            accValues[0] = String.valueOf(event.values[0]-gravity[0]);
            accValues[1] = String.valueOf(event.values[1]-gravity[1]);
            accValues[2] = String.valueOf(event.values[2]-gravity[2]);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
