package com.lanshifu.asm_plugin_library.imageview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.os.MessageQueue;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.Nullable;

/**
 * @author lanxiaobin
 * @date 2022/2/12
 */
public class MonitorImageView extends ImageView implements MessageQueue.IdleHandler {
    private String TAG = "MonitorImageView";
    private static int MAX_ALARM_IMAGE_SIZE = 500 * 1024; //500k
    private static int MAX_ALARM_MULTIPLE = 2;

    public MonitorImageView(Context context) {
        super(context);
    }

    public MonitorImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MonitorImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }



    // ImageView 部分
    @Override
    public void setImageDrawable(@Nullable Drawable drawable) {
        super.setImageDrawable(drawable);
        addImageLegalMonitor();
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        addImageLegalMonitor();
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        addImageLegalMonitor();
    }

    // View 的部分
    @Override
    public void setBackgroundDrawable(Drawable background) {
        super.setBackgroundDrawable(background);
        addImageLegalMonitor();
    }

    @Override
    public void setBackground(Drawable background) {
        super.setBackground(background);
        addImageLegalMonitor();
    }

    @Override
    public void setBackgroundResource(int resid) {
        super.setBackgroundResource(resid);
        addImageLegalMonitor();
    }

    /**
     * 添加图片合法监控
     */
    private void addImageLegalMonitor() {
        Log.w(TAG, "addImageLegalMonitor");
        Looper.myQueue().removeIdleHandler(this);
        Looper.myQueue().addIdleHandler(this);
    }

    @Override
    public boolean queueIdle() {
        post(() -> {
            try {
                Drawable drawable = getDrawable();
                Drawable background = getBackground();
                if (drawable != null) {
                    checkIsLegal(drawable, "图片");
                }
                if (background != null) {
                    checkIsLegal(background, "背景");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return false;
    }

    /**
     * 检查是否合法
     */
    private void checkIsLegal(Drawable drawable, String tag) {

        int viewWidth = getMeasuredWidth();
        int viewHeight = getMeasuredHeight();
        int drawableWidth = drawable.getIntrinsicWidth();
        int drawableHeight = drawable.getIntrinsicHeight();
        if (viewWidth == 0 || viewHeight ==0){
            return;
        }
        // 大小告警判断
        int imageSize = calculateImageSize(drawable);
        Log.w(TAG, "checkIsLegal -> viewWidth=" + viewWidth + " , " + tag + "宽度 -> " + drawableWidth + ",imageSize=" + imageSize);
        if (imageSize > MAX_ALARM_IMAGE_SIZE) {
            Log.e(TAG, "图片加载不合法，" + tag + "大小 -> " + imageSize);
            dealWarning(drawableWidth, drawableHeight, imageSize, drawable);
        }
        // 宽高告警判断
        if (MAX_ALARM_MULTIPLE * viewWidth < drawableWidth) {
            Log.e(TAG, "图片加载不合法, 控件宽度 -> " + viewWidth + " , " + tag + "宽度 -> " + drawableWidth);
            dealWarning(drawableWidth, drawableHeight, imageSize, drawable);
        }
        if (MAX_ALARM_MULTIPLE * viewHeight < drawableHeight) {
            Log.e(TAG, "图片加载不合法, 控件高度 -> " + viewHeight + " , " + tag + "高度 -> " + drawableHeight);
            dealWarning(drawableWidth, drawableHeight, imageSize, drawable);
        }
    }

    /**
     * 处理警告
     */
    private void dealWarning(int drawableWidth, int drawableHeight, int imageSize, Drawable drawable) {
        // 线上线下处理方式需要不一致，伪代码
        // 线上弹出提示窗口把信息输出，同时提供一个关闭打开开关
        // ......
        // 线下需要搜集代码信息，代码具体在哪里，把信息上报到服务器
        // ......
    }

    /**
     * 计算 drawable 的大小
     */
    private int calculateImageSize(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            return bitmap.getByteCount();
        }
        int pixelSize = drawable.getOpacity() != PixelFormat.OPAQUE ? 4 : 2;
        return pixelSize * drawable.getIntrinsicWidth() * drawable.getIntrinsicHeight();
    }

}
