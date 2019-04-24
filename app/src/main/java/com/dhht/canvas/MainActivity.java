package com.dhht.canvas;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author dhht
 */
public class MainActivity extends AppCompatActivity {

    ConstraintLayout clAll;
    TextView tvTxt;
    ObservableScrollView mScrollView;
    SingerView singerView;
    Button btn_cancle, btn_save;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        clAll = findViewById(R.id.cl_all);
        tvTxt = findViewById(R.id.tvTxt);
        mScrollView = findViewById(R.id.scView);
        singerView = findViewById(R.id.singerView);
        btn_cancle = findViewById(R.id.btn_cancle);
        btn_save = findViewById(R.id.btn_save);
        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);


        btn_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissSignerView();
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePhoto();
                dismissSignerView();
            }
        });

    }

    private void savePhoto() {
        Path path = singerView.getPath();
        Rect rect=singerView.getRect();

        Bitmap fileBitmap = shotScrollView(mScrollView);
        Canvas canvas = new Canvas(fileBitmap);

        path.offset(fileBitmap.getWidth()-rect.right-50,fileBitmap.getHeight()-(rect.bottom+50));


        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(10);
        paint.setAntiAlias(true);
        canvas.drawPath(path, paint);

        saveBitmapToSdCard(MainActivity.this, fileBitmap, "aaa");
    }


    @Override
    protected void onPause() {
        super.onPause();
        mScrollView.setOnScrolldListener(new ObservableScrollView.OnScrolldListener() {
            @Override
            public void onTopReached() {

            }

            @Override
            public void onBottomReached() {
                showSignerView();
            }
        });
    }

    void showSignerView() {
        singerView.setVisibility(View.VISIBLE);
        btn_cancle.setVisibility(View.VISIBLE);
        btn_save.setVisibility(View.VISIBLE);
    }

    void dismissSignerView() {
        singerView.clearCanvas();
        singerView.setVisibility(View.GONE);
        btn_cancle.setVisibility(View.GONE);
        btn_save.setVisibility(View.GONE);
    }

    /**
     * 将 Bitmap 保存到SD卡
     *
     * @param context
     * @param mybitmap
     * @param name
     * @return
     */
    public static boolean saveBitmapToSdCard(Context context, Bitmap mybitmap, String name) {
        boolean result = false;
        //创建位图保存目录
        String path = Environment.getExternalStorageDirectory() + "/AAA/";
        File sd = new File(path);
        if (!sd.exists()) {
            sd.mkdir();
        }

        File file = new File(path + name + ".png");
        if(file.exists()){
            file.delete();
        }


        FileOutputStream fileOutputStream = null;
        if (!file.exists()) {
            try {
                // 判断SD卡是否存在，并且是否具有读写权限
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    fileOutputStream = new FileOutputStream(file);
                    mybitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                    fileOutputStream.flush();
                    fileOutputStream.close();

                    //update gallery
                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    Uri uri = Uri.fromFile(file);
                    intent.setData(uri);
                    context.sendBroadcast(intent);
                    result = true;
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }


    /**
     * 根据指定的view截图
     *
     * @param v 要截图的view
     * @return Bitmap
     */
    public static Bitmap getViewBitmap(View v) {
        if (null == v) {
            return null;
        }
        v.setDrawingCacheEnabled(true);
        v.buildDrawingCache();
        if (Build.VERSION.SDK_INT >= 11) {
            v.measure(View.MeasureSpec.makeMeasureSpec(v.getWidth(), View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(v.getHeight(), View.MeasureSpec.EXACTLY));
            v.layout((int) v.getX(), (int) v.getY(), (int) v.getX() + v.getMeasuredWidth(), (int) v.getY() + v.getMeasuredHeight());
        } else {
            v.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
        }

        Bitmap bitmap = Bitmap.createBitmap(v.getDrawingCache(), 0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
        v.setDrawingCacheEnabled(false);
        v.destroyDrawingCache();
        return bitmap;
    }


    /**
     * Scrollview截屏
     *
     * @param scrollView 要截图的ScrollView
     * @return Bitmap
     */
    public static Bitmap shotScrollView(ScrollView scrollView) {
        int h = 0;
        Bitmap bitmap = null;
        for (int i = 0; i < scrollView.getChildCount(); i++) {
            h += scrollView.getChildAt(i).getHeight();
            scrollView.getChildAt(i).setBackgroundColor(Color.parseColor("#ffffff"));
        }
        bitmap = Bitmap.createBitmap(scrollView.getWidth(), h, Bitmap.Config.RGB_565);
        final Canvas canvas = new Canvas(bitmap);
        scrollView.draw(canvas);
        return bitmap;
    }

}
