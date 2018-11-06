package com.deverdie.checknetworkstatus;

import android.Manifest;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class RxJavaExtrasActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "dlg-" + RxJavaExtrasActivity.class.getSimpleName();

    TextView tvStatus;
    Button btUpload;
    private ProgressDialog mProgressDialog;

    private enum Module {
        PRODUCT("Product: "),
        LOCATION("Location: "),
        UNIT("Unit: "),
        COUNTING("Counting: ");

        private final String text;

        Module(final String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_upload:
                String host = GetExtStoreagePath();
                String product = "Product999.txt";
                String location = "Location999.txt";
                String unit = "Unit999.txt";
                String counting = "Counting999.txt";

//                new ReadTask().execute(
//                        new Task("Product", readTextFilePath(host.concat(product))),
//                        new Task("Location", readTextFilePath(host.concat(location))),
//                        new Task("Unit", readTextFilePath(host.concat(unit))),
//                        new Task("Counting", readTextFilePath(host.concat(counting)))
//                );

                List<Task> taskList = new ArrayList<>();
                taskList.add(new Task(Module.PRODUCT.toString(), readTextFilePath(host.concat(product))));
                taskList.add(new Task(Module.LOCATION.toString(), readTextFilePath(host.concat(location))));
                taskList.add(new Task(Module.UNIT.toString(), readTextFilePath(host.concat(unit))));
                taskList.add(new Task(Module.COUNTING.toString(), readTextFilePath(host.concat(counting))));

                Task[] tasks = taskList.toArray(new Task[taskList.size()]);

                new ReadTask().execute(tasks);

                break;
        }
    }

    private class Task {
        String name;
        FileInputStream bufferedReader;

        public Task(String name, FileInputStream bufferedReader) {
            this.name = name;
            this.bufferedReader = bufferedReader;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public FileInputStream getBufferedReader() {
            return bufferedReader;
        }

        public void setBufferedReader(FileInputStream bufferedReader) {
            this.bufferedReader = bufferedReader;
        }
    }

    private class ReadTask extends AsyncTask<Task, Integer, String> {

        long started;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, "onPreExecute: Loding...");
            started = System.currentTimeMillis();
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        @Override
        protected String doInBackground(Task... tasks) {
            StringBuilder ret = new StringBuilder();
            try {
                String line;
                int taskid = 0;
                for (Task task : tasks) {
                    int c = 0;
                    BufferedReader bufferedReader = resetEncoding(task.getBufferedReader());
                    int cc = bufferedReaderSize(bufferedReader);
                    task.getBufferedReader().getChannel().position(0);
                    bufferedReader = resetEncoding(task.getBufferedReader());
                    Log.d(TAG, "bufferedReaderSize: " + cc);
                    while ((line = bufferedReader.readLine()) != null) {
//                        Thread.sleep(1);
                        String[] lines = line.split("\t");
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < lines.length; i++) {
                            if (i != 0) {
                                sb.append(", ");
                            }
                            sb.append(lines[i]);
                        }
                        ++c;
                        int p = Math.round((((c / (float) cc) * 100) / tasks.length) + ((100 / (float) tasks.length) * taskid));//(int) ((((float) ((c) / cc) * 100) / tasks.length) + ((float) (100 / tasks.length) * taskid));
                        Log.d(TAG, String.format("c=%d, cc=%d,c/cc=%d, task.lenght=%d, progress=%d", c, cc, (c / cc), tasks.length, p));
                        // Publish the async task progress
                        // Added 1, because index start from 0
//                        publishProgress((int) ((++c) / (float) tasks.length) * 100);
                        publishProgress(p);

//                        Log.d(TAG, String.format("doInBackground, row: %s, data: %s", c, sb.toString()));

                    }


                    ++taskid;
                    ret.append(String.format("%s uploaded %d row(s)\n", task.getName(), c));


                    // If the AsyncTask cancelled
                    if (isCancelled()) {
                        break;
                    }
                }
            } catch (Exception e) {
                ret.append(e.getMessage());
            }
            return ret.toString();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
//            Log.d(TAG, "onProgressUpdate: " + values[0]);
            mProgressDialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            long t = (System.currentTimeMillis() - started);
            Log.d(TAG, "onPostExecute: Time use :" + t + " ms");
            mProgressDialog.dismiss();

            tvStatus.setText(String.format("%s Time use : %d ms", s, t));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_java_extras);

        checkPermission();

        tvStatus = findViewById(R.id.tv_status);
        btUpload = findViewById(R.id.bt_upload);

        btUpload.setOnClickListener(this);

        // Initialize the progress dialog
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(false);
        // Progress dialog horizontal style
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        // Progress dialog title
        mProgressDialog.setTitle("Importing");
        // Progress dialog message
        mProgressDialog.setMessage("Please wait, we are importing your files...");

    }

    private int bufferedReaderSize(BufferedReader bufferedReader) throws IOException {
        String line;
        int c = 0;
        while ((line = bufferedReader.readLine()) != null) {
            ++c;
        }
        return c;
    }

    public static String GetExtStoreagePath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "WIv3" + File.separator;
    }

    public static BufferedReader readTextFilePath55(String path) {
        try {

            File myFile = new File(path);
            FileInputStream fIn = new FileInputStream(myFile);
            return new BufferedReader(new InputStreamReader(fIn));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static FileInputStream readTextFilePath(String path) {
        try {

            File myFile = new File(path);
            return new FileInputStream(myFile);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void checkPermission() {

        PermissionListener dialogPermissionListener =
                DialogOnDeniedPermissionListener.Builder
                        .withContext(getApplicationContext())
                        .withTitle("WRITE_EXTERNAL_STORAGE permission")
                        .withMessage("WRITE_EXTERNAL_STORAGE permission is needed to read to file.")
                        .withButtonText(android.R.string.ok)
                        .withIcon(R.mipmap.ic_launcher)
                        .build();

        Dexter.withActivity(this)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(dialogPermissionListener).check();
    }

    private BufferedReader resetEncoding(FileInputStream fileInputStream) {
        try {
            UnicodeBOMInputStream ubis = null;
            ubis = new UnicodeBOMInputStream(fileInputStream);
            UnicodeBOMInputStream.BOM cs = ubis.getBOM();
            InputStreamReader isr = new InputStreamReader(ubis, ubis.getBOM().toString());
            return new BufferedReader(isr);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
