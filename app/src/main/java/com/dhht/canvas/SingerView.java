package com.dhht.canvas;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 作者：chs on 2016/9/14 16:14
 * 邮箱：657083984@qq.com
 */
public class SingerView extends View {
    private Paint mPaint;
    private Path mPath;
    private float mPreX, mPreY, mScalePreX, mScalePreY;
    private String mFilePath;
    private boolean clear;
    private Path scalePath;
    private int scale = 3;


    private int top;
    private int bottom;
    private int left;
    private int right;

    private Rect mRect=new Rect(0,0,0,0);


    public Rect getRect() {
        return mRect;
    }

    public SingerView(Context context) {
        super(context);
        init(context);
    }

    public SingerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SingerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.BLACK);
        mPaint.setStrokeWidth(10);
        mPaint.setAntiAlias(true);
        mPath = new Path();
        scalePath = new Path();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (clear) {
            mPath.reset();
            scalePath.reset();
            clear = false;
        }
        canvas.drawColor(Color.TRANSPARENT);
        canvas.drawPath(mPath, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                mPreX = event.getX();
                mPreY = event.getY();

                mScalePreX = event.getX() / scale;
                mScalePreY = event.getY() / scale;
                mPath.moveTo(mPreX, mPreY);
                scalePath.moveTo(mScalePreX, mScalePreY);

                return true;
            case MotionEvent.ACTION_MOVE:
                float endX = (mPreX + event.getX()) / 2;
                float endY = (mPreY + event.getY()) / 2;
                mPath.quadTo(mPreX, mPreY, endX, endY);


                float endScaleX = (mScalePreX + event.getX()/scale) / 2;
                float endScaleY = (mScalePreY + event.getY()/scale) / 2;

                scalePath.quadTo(mScalePreX, mScalePreY,endScaleX,endScaleY);


                if(mScalePreX<mRect.left){
                    mRect.left= (int) mScalePreX;
                }


                if(mScalePreX>mRect.right){
                    mRect.right= (int) mScalePreX;
                }


                if(mScalePreY>mRect.bottom){
                    mRect.bottom= (int) mScalePreY;
                }


                if(mScalePreY<mRect.top){
                    mRect.top= (int) mScalePreY;
                }



                mPreX = event.getX();
                mPreY = event.getY();
                mScalePreX = event.getX() / scale;
                mScalePreY = event.getY() / scale;


                invalidate();
                break;
        }
        return super.onTouchEvent(event);
    }


    public void clearCanvas() {
        clear = true;
    }


    public boolean saveToGallery(String fileName, String subFolderPath, String fileDescription, Bitmap.CompressFormat
            format, int quality) {
        // 控制图片质量
        if (quality < 0 || quality > 100)
            quality = 50;

        long currentTime = System.currentTimeMillis();

        File extBaseDir = Environment.getExternalStorageDirectory();
        File file = new File(extBaseDir.getAbsolutePath() + "/DCIM/" + subFolderPath);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                return false;
            }
        }

        String mimeType = "";
        switch (format) {
            case PNG:
                mimeType = "image/png";
                if (!fileName.endsWith(".png"))
                    fileName += ".png";
                break;
            case WEBP:
                mimeType = "image/webp";
                if (!fileName.endsWith(".webp"))
                    fileName += ".webp";
                break;
            case JPEG:
            default:
                mimeType = "image/jpeg";
                if (!(fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")))
                    fileName += ".jpg";
                break;
        }

        mFilePath = file.getAbsolutePath() + "/" + fileName;
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(mFilePath);

            Bitmap b = getChartBitmap();
            b.compress(format, quality, out);

            out.flush();
            out.close();

        } catch (IOException e) {
            e.printStackTrace();

            return false;
        }

        long size = new File(mFilePath).length();

        ContentValues values = new ContentValues(8);

        // store the details
        values.put(MediaStore.Images.Media.TITLE, fileName);
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        values.put(MediaStore.Images.Media.DATE_ADDED, currentTime);
        values.put(MediaStore.Images.Media.MIME_TYPE, mimeType);
        values.put(MediaStore.Images.Media.DESCRIPTION, fileDescription);
        values.put(MediaStore.Images.Media.ORIENTATION, 0);
        values.put(MediaStore.Images.Media.DATA, mFilePath);
        values.put(MediaStore.Images.Media.SIZE, size);

        return getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values) != null;
    }

    public Bitmap getChartBitmap() {
        // 创建一个bitmap 根据我们自定义view的大小
        Bitmap returnedBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.RGB_565);
        // 绑定canvas
        Canvas canvas = new Canvas(returnedBitmap);
        // 获取视图的背景
        canvas.drawColor(Color.TRANSPARENT);
        // 绘制
        draw(canvas);
        return returnedBitmap;
    }

    public String getFilePath() {
        return mFilePath;
    }


    public Path getPath() {
        return scalePath;
    }
}