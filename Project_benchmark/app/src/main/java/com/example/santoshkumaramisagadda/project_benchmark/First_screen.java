package com.example.santoshkumaramisagadda.project_benchmark;

import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import weka.core.Instances;
import weka.core.converters.ArffSaver;

public class First_screen extends AppCompatActivity {

    static SeekBar percent;
    static TextView percent_val;
    static Button btn_start;
    static int percent_value;
    static TextView result;
    static String val;
    static int flag=0;
    static int classifier=0;
    int step = 1;
    int max = 100;
    int min = 1;
    String arff_name="cpu.arff";
    String arff_location= Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Project" + File.separator +arff_name;

    String test_name="test.arff";
    String test_location= Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Project" + File.separator +test_name;

    String train_name="train.arff";
    String train_location= Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Project" + File.separator +train_name;



    Instances splitTrain,splitTest,data;

    private Spinner spinner;
    private static final String[] paths = {"Classifier 1", "Classifier 2", "Classifier 3", "Classifier4"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_screen);

        percent = (SeekBar) findViewById(R.id.seekBar);
        percent_val = (TextView) findViewById(R.id.textView2);
        percent.setMax(99);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            percent.setMin(1);
        }

        percent.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub
                if(progress==0)
                    progress=1;
                percent_val.setText(String.valueOf(progress) + "%");
                percent_value= progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }
        });

        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(First_screen.this,
                android.R.layout.simple_spinner_item, paths);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                result = (TextView) findViewById(R.id.textView6);
                String output = "";
                switch (position) {
                    case 0:
                        output += "Classifier selected = Classifier1";
                        classifier=1;
                        break;
                    case 1:
                        output += "Classifier selected = Classifier2";
                        classifier=2;
                        break;
                    case 2:
                        output += "Classifier selected = Classifier3";
                        classifier=3;
                        break;
                    case 3:
                        output += "Classifier selected = Classifier4";
                        classifier=4;
                        break;
                }
                result.setText(output);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });


        btn_start=(Button)findViewById(R.id.button);

        btn_start.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                //split the database
                try {
                    splitData(percent_value);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                UploadService upload=new UploadService(First_screen.this,getApplicationContext(),train_location,train_name,btn_start);
                upload.startUpload();

            }
        });
    }

    public void splitData(int x) throws IOException
    {
        BufferedReader inputReader=new BufferedReader(new FileReader(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Project" + File.separator +arff_name));
        data=new Instances(inputReader);
        data.setClassIndex(data.numAttributes() - 1);
        data.randomize(new java.util.Random());

        int dataSize=data.numInstances();

        splitTrain = new Instances(data,0);
        splitTrain.setClassIndex(splitTrain.numAttributes() - 1);

        splitTest = new Instances(data,0);
        splitTest.setClassIndex(splitTest.numAttributes() - 1);

        int trainingSize=x*dataSize/100;

        for(int i=0;i<trainingSize;i++)
        {
            splitTrain.add(data.instance(i));
        }

        for(int i=trainingSize;i<dataSize;i++)
        {
            splitTest.add(data.instance(i));
        }


        BufferedWriter writer1 = new BufferedWriter(new FileWriter(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Project" + File.separator +"train.arff")));
        writer1.write(splitTrain.toString());
        writer1.flush();
        writer1.close();

        BufferedWriter writer2 = new BufferedWriter(new FileWriter(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Project" + File.separator +"test.arff")));
        writer2.write(splitTest.toString());
        writer2.flush();
        writer2.close();
    }
}
