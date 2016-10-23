package com.example.mikah.multithreading;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button createButton = (Button) findViewById(R.id.createButton);
        final Button loadButton = (Button) findViewById(R.id.loadButton);
        final Button clearButton = (Button) findViewById(R.id.clearButton);
        final ListView list = (ListView) findViewById(R.id.theView);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        list.setAdapter(adapter);
        createButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FileWriter writerWorker = new FileWriter(getBaseContext());
                writerWorker.execute();
            }
        });
        loadButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FileReader readerWorker = new FileReader(getBaseContext());
                readerWorker.execute();
            }
        });
        clearButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
                progressBar.setProgress(0);
                final ListView list = (ListView) findViewById(R.id.theView);
                adapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_list_item_1);
                list.setAdapter(adapter);
                adapter.clear();
            }
        });
    }

    private class FileWriter extends AsyncTask<Void, Integer, Void> {

        private Context _appContext;

        FileWriter(Context appContext) {
            _appContext = appContext;
        }

        void doWork() {
            File file = new File(_appContext.getFilesDir(), "numbers.txt");
            try {
                if (file.exists())
                    file.delete();
                if (file.createNewFile()) {
                    OutputStream fileOut = new FileOutputStream(file);
                    for (int i = 1; i <= 10; i++) {
                        fileOut.write(Integer.toString(i).getBytes());
                        fileOut.write('\n');
                        Thread.sleep(250);
                        publishProgress(i);
                    }
                    fileOut.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            doWork();
            return null;
        }

        @Override
        protected void onPreExecute() {
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
            progressBar.setProgress(0);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
            progressBar.setProgress(values[0] * 10);
        }
    }

    private class FileReader extends AsyncTask<Void, String, Void> {

        private Context _appContext;
        private ArrayAdapter<String> adapter;

        FileReader(Context appContext) {
            _appContext = appContext;
        }

        void doWork() {
            System.out.println("Started working");
            File file = new File(getBaseContext().getFilesDir(), "numbers.txt");
            InputStream fin;
            int numLines = 0;
            try {
                fin = new FileInputStream(file);
                BufferedReader reader;
                String line;
                reader = new BufferedReader(new InputStreamReader(fin));
                System.out.println("Setup reader");
                while ((line = reader.readLine()) != null) {
                    System.out.println("Read line");
                    Thread.sleep(250);
                    publishProgress(Integer.toString(++numLines), line);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            doWork();
            return null;
        }

        @Override
        protected void onPreExecute() {
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
            progressBar.setProgress(0);
            final ListView list = (ListView) findViewById(R.id.theView);
            adapter = new ArrayAdapter<>(_appContext, android.R.layout.simple_list_item_1);
            list.setAdapter(adapter);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
            progressBar.setProgress(Integer.parseInt(values[0]) * 10);
            adapter.add(values[1]);
        }
    }
}


