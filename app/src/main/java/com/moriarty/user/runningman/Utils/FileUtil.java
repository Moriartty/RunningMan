package com.moriarty.user.runningman.Utils;

import android.os.Environment;

import java.io.File;

/**
 * Created by user on 17-10-11.
 */
public class FileUtil {
    public static String dirPath= Environment.getExternalStorageDirectory()+"/RunningMan";

    public static void createDir(){
        File file=new File(dirPath);
        if(!file.exists())
            file.mkdir();
    }

    private boolean isExist(){
        File file = new File(dirPath);
        //判断文件夹是否存在,如果不存在则创建文件夹
        if (!file.exists()) {
            file.mkdir();
        }
        return true;
    }
}
