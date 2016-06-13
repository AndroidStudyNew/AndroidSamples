package com.sjtu.charles.multithreaddownload;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * android开发笔记之多线程下载及断点续传
 * http://blog.csdn.net/qq_22063697/article/details/51568066
 */
public class MainActivity extends AppCompatActivity {

    //进度条
    private ProgressBar pb;
    //显示进度(百分比)
    private TextView tv;
    //记录当前进度条的下载进度
    private int currentProgress;
    //进行下载的线程数量
    public static final int THREADCOUNT = 3;
    //下载完成的线程数量
    public int finishedThread = 0;
    //下载完成生成的文件名
    public String fileName = "navicat.pdf";
    //请求的文件下载地址(本地文件)
    public String path = "http://192.168.1.100:8089/" + fileName;
    //请求的文件下载地址(网络文件)
//	 public String path =
//	 "thunder://QUFodHRwOi8vZGw0NS44MHMuaW06OTIwLzE2MDUv6LaF6ISRNDjlsI/ml7Zb5Zu96K+tRFZE54mIXS/otoXohJE0OOWwj+aXtlvlm73or61EVkTniYhdX2JkLm1wNFpa";
    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 0x1) {
                tv.setText(pb.getProgress()*100/pb.getMax() + "%");
            }
        };
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }
    /**
     * 初始化组件
     */
    private void initView() {
        pb = (ProgressBar) findViewById(R.id.pb);
        tv = (TextView) findViewById(R.id.tv);
    }
    /**
     * 点击下载的事件
     * @param view
     */
    public void download(View view) {
        new Thread() {
            public void run() {
                try {
                    URL url = new URL(path);
                    HttpURLConnection conn = (HttpURLConnection) url
                            .openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(3000);
                    conn.setReadTimeout(8000);
                    //请求成功时的响应码为200(注意响应码为200)
                    if (conn.getResponseCode() == 200) {
                        // 拿到需要下载的文件的大小
                        int length = conn.getContentLength();
                        // 先占个位置，生成临时文件
                        File file = new File(Environment.getExternalStorageDirectory(),fileName);
                        RandomAccessFile raf = new RandomAccessFile(file, "rwd");
                        raf.setLength(length);
                        //设置进度条的最大进度为文件的长度
                        pb.setMax(length);
                        raf.close();
                        //每个线程应该下载的长度(最后一个线程除外，因为不一定能够平分)
                        int size = length / THREADCOUNT;
                        for (int i = 0; i < THREADCOUNT; i++) {
                            // 1.确定每个线程的下载区间
                            // 2.开启对应的子线程
                            int startIndex = i * size;	//开始位置
                            int endIndex = (i + 1) * size - 1;	//结束位置
                            // 最后一个线程
                            if (i == THREADCOUNT - 1) {
                                endIndex = length - 1;
                            }
                            System.out.println("第" + (i + 1) + "个线程的下载区间为:"
                                    + startIndex + "-" + endIndex);
                            new DownloadThread(startIndex, endIndex, path, i)
                                    .start();
                        }
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            };
        }.start();
    }
    class DownloadThread extends Thread{
        private int lastProgress;
        private int startIndex,endIndex,threadId;
        private String path;
        public DownloadThread(int startIndex,int endIndex,String path,int threadId) {
            this.startIndex = startIndex;
            this.endIndex = endIndex;
            this.path = path;
            this.threadId = threadId;
        }
        @Override
        public void run() {
            try {
                //建立进度临时文件，其实这时还没有创建。当往文件里写东西的时候才创建。
                File progressFile = new File(Environment.getExternalStorageDirectory(), threadId+".txt");
                //判断临时文件是否存在，存在表示已下载过，没下完而已
                if (progressFile.exists()) {
                    FileInputStream fis = new FileInputStream(progressFile);
                    BufferedReader br = new BufferedReader(new InputStreamReader(fis));
                    //从进度临时文件中读取出上一次下载的总进度，然后与原本的开始位置相加，得到新的开始位置
                    lastProgress = Integer.parseInt(br.readLine());
                    startIndex += lastProgress;

                    //断点续传，更新上次下载的进度条
                    currentProgress += lastProgress;
                    pb.setProgress(currentProgress);
                    Message msg = Message.obtain();
                    msg.what = 0x1;
                    mHandler.sendMessage(msg);

                    br.close();
                    fis.close();
                }
                //真正请求数据
                URL url = new URL(path);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(3000);
                conn.setReadTimeout(8000);

                //设置本次http请求所请求的数据的区间(这是需要服务器那边支持断点)，格式需要这样写，不能写错
                conn.setRequestProperty("Range", "bytes=" + startIndex + "-" + endIndex);
                //请求部分数据，响应码是206(注意响应码是206)
                if (conn.getResponseCode() == 206) {
                    //此时流中只有1/3原数据
                    InputStream is = conn.getInputStream();
                    File file = new File(Environment.getExternalStorageDirectory(),fileName);
                    RandomAccessFile raf = new RandomAccessFile(file, "rwd");
                    //把文件的写入位置移动至startIndex
                    raf.seek(startIndex);

                    byte[] b = new byte[1024];
                    int len = 0;
                    int total = lastProgress;
                    while ((len = is.read(b)) != -1) {
                        raf.write(b, 0, len);
                        total += len;
                        System.out.println("线程" + threadId + "下载了" + total);
                        //生成一个专门用来记录下载进度的临时文件
                        RandomAccessFile progressRaf = new RandomAccessFile(progressFile, "rwd");
                        //每次读取流里数据之后，同步把当前线程下载的总进度写入进度临时文件中
                        progressRaf.write((total + "").getBytes());
                        progressRaf.close();

                        //下载时更新进度条
                        currentProgress += len;
                        pb.setProgress(currentProgress);
                        Message msg = Message.obtain();
                        msg.what = 0x1;
                        mHandler.sendMessage(msg);
                    }
                    System.out.println("线程" + threadId + "下载完成");
                    raf.close();

                    //每完成一个线程就+1
                    finishedThread ++;
                    //等标志位等于线程数的时候就说明线程全部完成了
                    if (finishedThread == THREADCOUNT) {
                        for (int i = 0; i < finishedThread; i++) {
                            //将生成的进度临时文件删除
                            File f = new File(Environment.getExternalStorageDirectory(),i + ".txt");
                            f.delete();
                        }
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
