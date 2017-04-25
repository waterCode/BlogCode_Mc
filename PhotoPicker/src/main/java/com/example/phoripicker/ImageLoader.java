package com.example.phoripicker;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Path;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.LruCache;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.LinkedList;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by zmc on 2017/4/12.
 */
public class ImageLoader {
    private static ImageLoader mImageLoader;

    public LruCache<String,Bitmap> bitmapLruCache;//lru缓存对象,为什么不能用private
    public ExecutorService mThreadPool;//线程池，苦力人员
    private static final int DEFAULT_THREAD_COUNT=1;



    private static Type mType=Type.FIFO;//队列的调度方式

    public enum Type{
        FIFO,LIFO;
    }
    private LinkedList<Runnable> taskQueue;//任务列表

    private Thread mPoolThread;//后台轮询线程
    private Handler mPoolThreadHandler;

    private Handler uiHandler;//ui线程

    public ImageLoader(int threadCount,Type mType) {
        init(threadCount,mType);
    }

    private Runnable getTask(){
        if(mType==Type.FIFO){
            return taskQueue.getFirst();
        }else if (mType==Type.LIFO){
            return taskQueue.getLast();
        }else return null;
    }
    private void init(int threadCount, Type mType) {

        //后台轮询线程
        mPoolThread=new Thread(){
            @Override
            public void run() {
                Looper.prepare();
                mPoolThreadHandler = new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        //去任务队列拿出任务来执行
                        mThreadPool.execute(getTask());
                    }
                };
            }
        };

        mPoolThread.start();

        int maxMemory= (int) Runtime.getRuntime().maxMemory();
        int cacheMemory=maxMemory/8;

        bitmapLruCache = new LruCache<String,Bitmap>(cacheMemory){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount()*value.getHeight();//每行占据字节数成语他的高度
            }
        };

        mThreadPool=  Executors.newFixedThreadPool(threadCount);
        taskQueue=new LinkedList<>();

    }


    public static ImageLoader getInstance(){

        if (mImageLoader==null){
            synchronized (ImageLoader.class){
                if(mImageLoader==null){
                    mImageLoader=new ImageLoader(DEFAULT_THREAD_COUNT,mType);
                }
            }
        }
        return mImageLoader;
    }

    public void loadImage(final String path, final ImageView imageView){
        imageView.setTag(path);//防止混乱
        uiHandler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                //设置图片,等待回调
                ImageHolder holder= (ImageHolder) msg.obj;
                String path= holder.path;
                Bitmap bitmap=holder.bitmap;
                ImageView imageView=holder.imageView;
                if(imageView.getTag().toString().equals(path)){
                    imageView.setImageBitmap(bitmap);
                }
            }
        };

        Bitmap bm=getBitmapFromLruCache(path);
        if(bm!=null){
            refreshBitmap(path, imageView, bm);
        }else {
            addTask(new Runnable(){
                @Override
                public void run() {
                    //加载图片，图片压缩
                    //获得图片加载大小
                    ImageSize imageSize=getImageViewSize(imageView);
                    //压缩图片
                    Bitmap bm=decodeBimapFromPath(path,imageSize.width,imageSize.height);
                    //把图片加入内存
                    addBitmapToLru(path,bm);
                    refreshBitmap(path, imageView, bm);
                }
            });
        }

    }

    private void refreshBitmap(String path, ImageView imageView, Bitmap bm) {
        Message msg=Message.obtain();
        ImageHolder holder= new ImageHolder();
        holder.bitmap=bm;
        holder.path=path;
        holder.imageView=imageView;
        uiHandler.sendMessage(msg);
    }

    private void addBitmapToLru(String path,Bitmap bm) {
        if(getBitmapFromLruCache(path)!=null)
            bitmapLruCache.put(path,bm);
    }

    /*
    根据要求宽高进行压缩
     */
    private Bitmap decodeBimapFromPath(String path, int width, int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds=true;//不加载如内存
        BitmapFactory.decodeFile(path,options);
        
        options.inSampleSize=calculatorInSampleSize(options,width,height);
        //再次解析图片，要加载如内存
        options.inJustDecodeBounds=false;
        Bitmap bm=BitmapFactory.decodeFile(path,options);
        return bm;
    }

    /*
    根据需求的宽和高和实际的宽和高计算sampleSize
     */
    private int calculatorInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {

        int inSampleSize=1;//应该指的是比例
        int outWidth = options.outWidth;
        int outHeight = options.outHeight;
        if(outHeight>reqHeight||outWidth>reqWidth){
            int radioWidth=Math.round(outWidth*1f/reqWidth);
            int radioHeight=Math.round(outHeight*1f/reqHeight);
            inSampleSize=Math.max(radioWidth,radioHeight);
        }
        return inSampleSize;
    }

    private ImageSize getImageViewSize(ImageView imageView) {
        ImageSize size= new ImageSize();
        DisplayMetrics displayMetrics=imageView.getContext().getResources().getDisplayMetrics();//666
        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
        int width=(layoutParams.width== ViewGroup.LayoutParams.WRAP_CONTENT?0:imageView.getWidth());
        if(width<=0){
            width=imageView.getMaxWidth();
        }if (width<=0){
            width=displayMetrics.widthPixels;
        }

        int height=(layoutParams.height== ViewGroup.LayoutParams.WRAP_CONTENT?0:imageView.getHeight());
        if(height<=0){
            height=imageView.getMaxWidth();
        }if (height<=0){
            height=displayMetrics.heightPixels;
        }
        size.width=width;
        size.height=height;
        return size;
    }
    private class ImageSize{
        int width;
        int height;
    }

    private void addTask(Runnable runnable) {
        taskQueue.add(runnable);//放入任务队列中
        //发消息
        mPoolThreadHandler.sendEmptyMessage(0x110);
    }

    private class ImageHolder{
        Bitmap bitmap;
        ImageView imageView;
        String path;
    }
    private Bitmap getBitmapFromLruCache(String path) {
        return bitmapLruCache.get(path);
    }

}
