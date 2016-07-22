package com.sjtu.asynctaskdemo;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    private ProgressDialog mDialog;
    private AsyncTask mAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDialog = new ProgressDialog(this);
        mDialog.setMax(100);
        mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mDialog.setCancelable(false);
        mAsyncTask = new MyAsyncTask();

        findViewById(R.id.tv).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        mAsyncTask.execute();
    }

    private class MyAsyncTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            mDialog.show();
            Log.e(TAG, Thread.currentThread().getName() + " onPreExecute ");
        }

        @Override
        protected Void doInBackground(Void... params) {

            // 模拟数据的加载,耗时的任务
            for (int i = 0; i < 100; i++) {
                try {
                    Thread.sleep(80);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                publishProgress(i);
            }

            Log.e(TAG, Thread.currentThread().getName() + " doInBackground ");
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            mDialog.setProgress(values[0]);
            Log.e(TAG, Thread.currentThread().getName() + " onProgressUpdate ");
        }

        @Override
        protected void onPostExecute(Void result) {
            // 进行数据加载完成后的UI操作
            mDialog.dismiss();
            Log.e(TAG, Thread.currentThread().getName() + " onPostExecute ");
        }
    }
}
