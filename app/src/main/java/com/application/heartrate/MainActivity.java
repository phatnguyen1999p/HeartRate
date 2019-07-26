package com.application.heartrate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.io.File;
import java.util.ArrayList;

import static java.lang.Math.cos;
import static java.lang.Math.sin;


public class MainActivity extends AppCompatActivity {
    private LineChart chart;
    private ArrayList<Entry> values = new ArrayList<>();
    Data DataSource = new Data();
    PopupWindow popupWindow;
    ViewPortHandler viewPortHandler;

    loadDataTask loadDataTask = new loadDataTask();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_NO);
        isReadStoragePermissionGranted();
        isWriteStoragePermissionGranted();

        //DataSource = FileIO.readData("data.txt");
        //setDataAndTime();

        //loadDataTask.execute();

        chart = findViewById(R.id.LineChart);
        viewPortHandler = chart.getViewPortHandler();
        viewPortHandler.setMaximumScaleX(30f);
        viewPortHandler.setMaximumScaleY(1f);

        chart.setTouchEnabled(true);
        chart.setPinchZoom(true);
        chart.setDragEnabled(true);
        chart.setGridBackgroundColor(Color.BLUE);
        chart.setHardwareAccelerationEnabled(true);
        chart.setAutoScaleMinMaxEnabled(true);

        chart.setOnChartGestureListener(new OnChartGestureListener() {
            @Override
            public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

            }

            @Override
            public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

            }

            @Override
            public void onChartLongPressed(MotionEvent me) {

            }

            @Override
            public void onChartDoubleTapped(MotionEvent me) {

            }

            @Override
            public void onChartSingleTapped(MotionEvent me) {

            }

            @Override
            public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
            }

            @Override
            public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

                chart.resetTracking();
                drawChart(setData());
            }

            @Override
            public void onChartTranslate(MotionEvent me, float dX, float dY) {

            }
        });


        Button toBLScreen = findViewById(R.id.BL_Connect_btn);
        toBLScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, BLConnecting_Activity.class);
                startActivity(intent);
            }
        });

        final Button selectData_btn = findViewById(R.id.getData_btn);
        selectData_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.showAsDropDown(selectData_btn, 20, 20);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        popupWindow = popupWindow();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //loadDataTask.cancel(true);
    }


    public boolean isReadStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
                return false;
            }
        } else {
            return true;
        }
    }

    public boolean isWriteStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                //Log.v("TAG","Permission is granted2");
                return true;
            } else {

                //Log.v(TAG,"Permission is revoked2");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            //Log.v("TAG","Permission is granted2");
            return true;
        }
    }

    Double[] Time;
    Double[] Data;

    private void setDataAndTime() {
        Data = DataSource.getData();
        Time = DataSource.getThoiGian();
    }

    public ArrayList setData() {
        float X, Y;
        values.clear();
        int size = Time.length;

        float scaleX = viewPortHandler.getScaleX();
        if (size > 20000) {

            int numberForScaling = size / 5000;
            for (int i = 0; i < size; i += numberForScaling / scaleX) {
                X = Time[i].floatValue() - Time[1].floatValue();
                Y = Data[i].floatValue();
                values.add(new Entry(X, Y));
            }
        } else {
            for (int i = 0; i < Time.length; i++) {
                X = Time[i].floatValue() - Time[1].floatValue();
                Y = Data[i].floatValue();
                values.add(new Entry(X, Y));
            }
        }
        return values;
    }

    public void drawChart(ArrayList values) {

        LineDataSet dataSet = new LineDataSet(values, "HÀM THEO THỜI GIAN");
        LineData lineData = new LineData(dataSet);

        dataSet.setColor(Color.RED);
        dataSet.setDrawValues(false);
        dataSet.setDrawCircles(false);

        chart.setData(lineData);
        dataSet.setLineWidth(2f);
        chart.invalidate();
    }

    /*public void writeData() {
        ArrayList<Double> tg = new ArrayList<>();
        ArrayList<Double> dt = new ArrayList<>();
        for (double i = 0; i <= 100000; i += 0.1) {
            tg.add(i);
            dt.add(3 * cos(0.01 * i) + 2 * sin(0.001 * i));
        }
        //Data Sample_1 = new Data(tg.toArray(new Double[tg.size()]), dt.toArray(new Double[dt.size()]));
        FileIO.saveData(tg.toArray(new Double[tg.size()]), dt.toArray(new Double[dt.size()]));
    }*/

    private PopupWindow popupWindow() {
        final PopupWindow popupWindow = new PopupWindow(this);

        popupWindow.setFocusable(true);
        popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);

        ListView listViewData = new ListView(this);

        ArrayList<String> temp = new ArrayList<>();
        ArrayList<File> DataFiles = new ArrayList<>( FileIO.findTxtFiles());

        for (File i : DataFiles) {
            temp.add(i.getName());
        }

        ArrayAdapter<String> ListContent = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, temp);

        listViewData.setAdapter(ListContent);
        popupWindow.setContentView(listViewData);

        listViewData.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String value = (String) adapterView.getItemAtPosition(i);
                popupWindow.dismiss();

                if (loadDataTask.getStatus() != AsyncTask.Status.RUNNING) {   // check if asyncTasks is running
                    loadDataTask.cancel(true); // asyncTasks not running => cancel it
                    loadDataTask = new loadDataTask(); // reset task
                    loadDataTask.execute(value); // execute new task (the same task)

                    Toast.makeText(getApplicationContext(), "Selected Data : " + value, Toast.LENGTH_SHORT).show();
                    TextView dataName = findViewById(R.id.dataName);
                    dataName.setText(value);
                } else {
                    Toast.makeText(MainActivity.this, "AsyncTask in progress, please wait", Toast.LENGTH_LONG).show();
                }

            }
        });
        return popupWindow;
    }

    public final ArrayList<Data> DataBase = new ArrayList<>();

    private class loadDataTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            publishProgress();

            for (String fileName : strings) {
                DataSource = null;
                if (!DataBase.isEmpty()) {
                    for (Data i : DataBase) {
                        if (i.getFileName().equals(fileName)) {
                            DataSource = i;
                        }
                    }
                    if (DataSource == null) {
                        DataSource = FileIO.readData(fileName);
                        DataBase.add(DataSource);
                    }
                } else {
                    DataSource = FileIO.readData(fileName);
                    DataBase.add(DataSource);
                }
            }
        return null;
        }
        protected void onProgressUpdate(Void avoid){
            Toast.makeText(MainActivity.this, "Loading", Toast.LENGTH_LONG).show();
        }
        protected void onPostExecute(Void avoid){
            super.onPostExecute(avoid);
            //viewPortHandler.setZoom(1f,1f);
            setDataAndTime();
            drawChart(setData());
            Toast.makeText(MainActivity.this, "Completed", Toast.LENGTH_LONG).show();
        }
    }
}