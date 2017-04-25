package com.example.download;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;


import com.example.download.api.ServiceGenerator;

import java.io.File;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by zmc on 2017/4/15.
 */
public class DownloadService extends Service{
    DownloadManager downloadManager;
    public static final String DOWNLOAD_PATH= Environment.getExternalStorageDirectory().getAbsolutePath()+"/TESTDownload/";
    public static final String ACTION_UPDATE="action_update";
    public Context mContext=DownloadService.this;
    public static final String TAG="DownloadService";
    private ThreadInfoDao threadInfoDao;//线程信息数据库类

    private ExecutorService threadPool;
    @Override
    public void onCreate() {
        super.onCreate();
        threadInfoDao = DownloadApplication.getInstance().getDaoSession().getThreadInfoDao();
        threadPool= Executors.newCachedThreadPool();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        downloadManager = new DownloadManager();
        return downloadManager;
    }


    class DownloadManager extends Binder{

        List<DownloadTask> downloadTaskList;

        public DownloadManager() {

            downloadTaskList=new ArrayList<>();
        }
        public void beginDownload(final FileInfo file){
            //开始下载
            //检查数据库并获得Threadinfo
            //从数据库中查询
            final List<ThreadInfo> threadInfoList = threadInfoDao.queryBuilder()
                    .where(ThreadInfoDao.Properties.Url.eq(file.getUrl()))
                    .list();
            if(threadInfoList.size()==0) {
                //表示第一次下载，获取文件长度分割多个线程并开始下载
                DownloadInterface service = ServiceGenerator.createService(DownloadInterface.class);
                Call<ResponseBody> call = service.getFileResult(file.getUrl());
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        //应该还是主线程
                        long length = response.body().contentLength();
                        Log.d(TAG,"文件长度"+length);
                        long per_length=length/DownloadTask.DOWNLOADTHEADNUM;
                        Log.d(TAG,"perLength"+per_length);
                        for (int i=0;i<DownloadTask.DOWNLOADTHEADNUM;i++){
                            //新建threadInfo
                            ThreadInfo f=new ThreadInfo(null,file.getUrl(),file.getFileName(),i*per_length,(i+1)*per_length-1,0,length);
                            threadInfoDao.insert(f);//第一次向数据库插入信息
                            if (i==DownloadTask.DOWNLOADTHEADNUM-1){
                                f.setEnd(length-1);//最后一段
                            }
                            Log.d(TAG,f.toString());
                            threadInfoList.add(f);//加入集合
                        }
                        //循环结束后直接开始任务
                        DownloadTask task=new DownloadTask(file.getFileName(),threadInfoList);
                        task.begin();
                        downloadTaskList.add(task);

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.d(TAG,"请求失败");
                    }
                });
            }else {
                DownloadTask task=new DownloadTask(file.getFileName(),threadInfoList);
                task.begin();//开始任务
                downloadTaskList.add(task);
            }


        }

        public void stopDownload(int i){
            downloadTaskList.get(i).stop();

        }



    }


    class DownloadTask{
        public static final int DOWNLOADTHEADNUM=3;
        List<ThreadInfo> threadInfoList;
        List<DownLoadThread> downloadThreadList;

        String fileName;
        private boolean stop=false;
        private long allFinished=0;

        public DownloadTask(String n,List<ThreadInfo> threadInfoList) {
            fileName=n;
            this.threadInfoList = threadInfoList;
            downloadThreadList=new ArrayList<>();

        }

        public void begin(){
            for(ThreadInfo f:threadInfoList){
                Log.d(TAG,"thread Id="+f.getId()+"begin down from range"+f.getStart()+"to"+f.getEnd()+"  and hasFinished="+f.getFinished());
                DownLoadThread t= new DownLoadThread(f);
                downloadThreadList.add(t);
                threadPool.execute(t);//不是sumit吧？和execute区别
            }
        }

        public void stop(){
            stop=true;
        }

        public  void updateProgress(){

            Intent intent =new Intent();
            intent.setAction(DownloadService.ACTION_UPDATE);
            Log.d(TAG,"allfinished="+allFinished);
            Log.d(TAG,"fileLength="+threadInfoList.get(0).getFileLength());
            intent.putExtra("downloadProgress",allFinished*100/threadInfoList.get(0).getFileLength());
            mContext.sendBroadcast(intent);
        }

        public boolean IsAllThreadFinished(){
            boolean is=true;//假设它是完成了的
            for (DownLoadThread d:downloadThreadList){
                if(!d.isThreadFinished) {
                    //一旦有一个false进入if
                    is = false;
                    break;
                }
            }
            return is;
        }

        /*
网络请求
参数：线程信息和文件对象
 */
        class DownLoadThread extends Thread{

            private ThreadInfo threadInfo;
            private File mFile;
            private long threadFinishedByte=0;
            private boolean isThreadFinished=false;




            public  DownLoadThread(ThreadInfo f) {
                threadInfo =f;
                mFile=checkFile(f.getFileName());
                threadFinishedByte=f.getFinished();

            }

            private File checkFile(String fileName){
                Log.d("DownloadService", "path is"+DOWNLOAD_PATH);
                File dir=new File(DOWNLOAD_PATH);
                if(!dir.exists())
                    dir.mkdir();
                File file=new File(DOWNLOAD_PATH,fileName);
                if(!file.exists())
                    try {
                        file.createNewFile();
                    } catch (IOException e) {

                        e.printStackTrace();
                        return null;
                    }
                return file;
            }



            @Override
            public void run() {
                super.run();

                DownloadInterface service = ServiceGenerator.createService(DownloadInterface.class);

                //传入线程信息是正确的，直接就可以开始
                Call<ResponseBody> call = service.getFileResult(threadInfo.getUrl(), "bytes=" + (threadInfo.getStart() + threadInfo.getFinished()) + "-" + threadInfo.getEnd());//断点部分开始请求

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                        if(response.isSuccess()){
                            Log.d("connect","successfully");
                            try {
                               //第一次的话向数据库插入信息
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            writeResponseBodytoDisk(response.body(),mFile);//写入储存
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.d(TAG,"fail");
                    }
                });
            }

            private void writeResponseBodytoDisk (ResponseBody body,File file)throws Exception {

                try (RandomAccessFile raf=new RandomAccessFile(file,"rwd");
                     InputStream inputStream=body.byteStream())
                {

                    raf.seek(threadInfo.getStart()+threadInfo.getFinished());
                    byte[] buf=new byte[1024];
                    int len=-1;
                    long time=System.currentTimeMillis();


                    while((len=inputStream.read(buf))!=-1){
                        raf.write(buf,0,len);//写入数据

                        threadFinishedByte+=len;//本身的线程进行++

                        allFinished+=len;
                        if(System.currentTimeMillis()-time>400){
                            time=System.currentTimeMillis();
                            updateProgress();
                        }

                        if (stop){
                            threadInfo.setFinished(threadFinishedByte);
                            threadInfoDao.update(threadInfo);//跟新数据库信息
                            updateProgress();
                            return ;
                        }
                    }
                    isThreadFinished=true;//当前线程结束
                    updateProgress();
                    threadInfoDao.deleteByKey(threadInfo.getId());//删除数据库的info信息
                    Log.d("DownloadService","ThreadInfoid="+threadInfo.getId()+" finished");

                } catch (Exception e) {
                    e.printStackTrace();

                }
            }



        }
    }





}


