package com.moriarty.user.mapdemotest;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by user on 17-10-13.
 */
public class FileUtil {
    public static final String filePath = Environment.getExternalStorageDirectory().getPath()+"map.txt";

    public static String readFromFile(){
        try {
            StringBuffer text = new StringBuffer();
            InputStreamReader read = new InputStreamReader(
                    new FileInputStream(new File(filePath)));//考虑到编码格式
            BufferedReader bufferedReader = new BufferedReader(read);
            String lineTxt = null;
            while((lineTxt = bufferedReader.readLine()) != null){
                Log.d("Moriarty",lineTxt);
                text.append(lineTxt);
            }
            read.close();
            return text.toString();
        }catch (IOException e){
            Log.d("Moriarty","IOException_read");
            e.printStackTrace();
        }
        return null;
    }

    public static void writeInFile(String text){
        File file =new File(filePath);
        if(!file.exists()){
            try {
                file.createNewFile();
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
        try{
            FileWriter fileWritter = new FileWriter(file,false);
            BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
            Log.d("Moriarty",text);
            bufferWritter.write(text);
            bufferWritter.close();
        }catch(IOException e){
            Log.d("Moriarty","IOException_write");
            e.printStackTrace();
        }
    }

}
