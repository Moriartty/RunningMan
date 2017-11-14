package com.moriarty.user.runningman.Others;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.util.Log;


import com.moriarty.user.runningman.Activity.MainActivity;
import com.moriarty.user.runningman.R;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * Created by user on 16-10-17.
 */
public class ZoomBitmap {
    private static String currentTag="ZoomBitmap:";

    public static Bitmap getZoomBitmap(Context context, String uri, String flag){   //返回一个包含压缩bitmap的softReference
        int ruleWidth;
        if(flag.equals("small")){
            ruleWidth=100;
        }
        else if(flag.equals("medium")){
            ruleWidth=200;
        }
        else{
            ruleWidth=250;
        }
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inJustDecodeBounds=false;
        options.inPreferredConfig=Bitmap.Config.RGB_565;   //转换编码类型，放弃透明度来压缩图片
        Bitmap bmp=null;
        try {
            bmp = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(Uri.parse(uri)),null,options);
        }catch (Exception e){
            Resources res=context.getResources();
            bmp=BitmapFactory.decodeResource(res, R.drawable.contact_default2);    //这里是该用default还是default2
            e.printStackTrace();
        }
        // Bitmap bmp= BitmapFactory.decodeFile(getPath(Uri.parse(uri)));
        ByteArrayOutputStream bos=new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG,80,bos);
        byte[] bytes=bos.toByteArray();
        bmp=BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        // Log.d("Moriarty","QService"+bmp.getWidth()+"   "+bmp.getHeight());
        float width=bmp.getWidth();    //压缩图像的长度和宽度来压缩图像大小
        float height=bmp.getHeight();
        float temp=height/width;
        Matrix matrix=new Matrix();
        float scaleWidth=((float)ruleWidth)/width;
        float scaleHeight=((float)ruleWidth*temp)/height;
        matrix.postScale(scaleWidth,scaleHeight);
        Bitmap result=Bitmap.createBitmap(bmp,0,0,(int) width,(int)height,matrix,true);
        /*if(!bmp.isRecycled()){
            bmp.recycle();
            System.gc();
        }*/

        return result;
    }
    public static Bitmap getZoomBitmap2(InputStream inputStream,Context context){
        int ruleWidth=300;
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inJustDecodeBounds=false;
       // options.inPreferredConfig=Bitmap.Config.RGB_565;   //转换编码类型，放弃透明度来压缩图片
        Bitmap bmp=null;
        try {
            bmp = BitmapFactory.decodeStream(inputStream,null,options);
        }catch (Exception e){
            e.printStackTrace();
        }
       // Log.d("Moriarty","QService"+bmp.getWidth()+"   "+bmp.getHeight());
        if(bmp!=null){
            float width=bmp.getWidth();    //压缩图像的长度和宽度来压缩图像大小
            float height=bmp.getHeight();
            float temp=height/width;
            Matrix matrix=new Matrix();
            float scaleWidth=((float)ruleWidth)/width;
            float scaleHeight=((float)ruleWidth*temp)/height;
            matrix.postScale(scaleWidth,scaleHeight);
            Bitmap result=Bitmap.createBitmap(bmp,0,0,(int) width,(int)height,matrix,true);
            return result;
        }
        return null;
    }
    public static Bitmap ImageCrop(Bitmap bitmap, int num1, int num2,
                                   boolean isRecycled)
    {
        if (bitmap == null)
        {
            return null;
        }
        int w = bitmap.getWidth(); // 得到图片的宽，高
        int h = bitmap.getHeight();
        int retX, retY;
        int nw, nh;
        if (w > h)
        {
            if (h > w * num2 / num1)
            {
                nw = w;
                nh = w * num2 / num1;
                retX = 0;
                retY = (h - nh) / 2;
            } else
            {
                nw = h * num1 / num2;
                nh = h;
                retX = (w - nw) / 2;
                retY = 0;
            }
        } else
        {
            if (w > h * num2 / num1)
            {
                nh = h;
                nw = h * num2 / num1;
                retY = 0;
                retX = (w - nw) / 2;
            } else
            {
                nh = w * num1 / num2;
                nw = w;
                retY = (h - nh) / 2;
                retX = 0;
            }
        }
        Bitmap bmp = Bitmap.createBitmap(bitmap, retX, retY, nw, nh, null,
                false);
        if (isRecycled && bitmap != null && !bitmap.equals(bmp)
                && !bitmap.isRecycled())
        {
            bitmap.recycle();
            bitmap = null;
        }
        return bmp;// Bitmap.createBitmap(bitmap, retX, retY, nw, nh, null,
        // false);
    }
    public static Bitmap ImageCropWithRect(Bitmap bitmap)
    {
        if (bitmap == null)
        {
            return null;
        }

        int w = bitmap.getWidth(); // 得到图片的宽，高
        int h = bitmap.getHeight();

        int nw, nh, retX, retY;
        if (w > h)
        {
            nw = h / 2;
            nh = h;
            retX = (w - nw) / 2;
            retY = 0;
        } else
        {
            nw = w / 2;
            nh = w;
            retX = w / 4;
            retY = (h - w) / 2;
        }

        // 下面这句是关键
        Bitmap bmp = Bitmap.createBitmap(bitmap, retX, retY, nw, nh, null,
                false);
        if (bitmap != null && !bitmap.equals(bmp) && !bitmap.isRecycled())
        {
            bitmap.recycle();
            bitmap = null;
        }
        return bmp;// Bitmap.createBitmap(bitmap, retX, retY, nw, nh, null,
        // false);
    }
}


