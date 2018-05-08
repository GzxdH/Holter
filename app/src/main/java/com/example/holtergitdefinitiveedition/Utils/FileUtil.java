package com.example.holtergitdefinitiveedition.Utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;

/**
 * Created by 王子龙 on 2018/4/24.
 */

public class FileUtil {

    private static String FileName;

    public static void setFileName(String fileName) {
        FileName = fileName;
    }

    public static String readData(String fileName) {
        StringBuilder sb = new StringBuilder("");
        try {
            FileInputStream fin = new FileInputStream(fileName);

            int length = fin.available();

            byte[] buffer = new byte[length];
            int len = 0;
            while ((len = fin.read(buffer)) > 0) {
                sb.append(new String(buffer, 0, len));
            }

            fin.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static void writeData(double[] doubles) {
        try {
            //内置sd卡路径
            String sdcardPath1 = System.getenv("EXTERNAL_STORAGE");

            //新建一个File，传入文件夹目录
            File file = new File(sdcardPath1 + "/Holter_Data");
            //判断文件夹是否存在，如果不存在就创建，否则不创建
            if (!file.exists()) {
                //通过file的mkdirs()方法创建<span style="color:#FF0000;">目录中包含却不存在</span>的文件夹
                file.mkdirs();
            }

            File txtFile = new File(sdcardPath1 + "/Holter_Data/" + FileName + ".txt");
            //判断文件是否存在，存在就删除
            if (txtFile.exists()) {
                txtFile.delete();
            } else {
                //创建文件
                txtFile.createNewFile();
            }

            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(txtFile));
            for (double d : doubles) {
                bufferedWriter.write(d + ",");
            }
            bufferedWriter.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeData(String data) {
        try {
            //内置sd卡路径
            String sdcardPath1 = System.getenv("EXTERNAL_STORAGE");

            //新建一个File，传入文件夹目录
            File file = new File(sdcardPath1 + "/Holter_Data");
            //判断文件夹是否存在，如果不存在就创建，否则不创建
            if (!file.exists()) {
                //通过file的mkdirs()方法创建<span style="color:#FF0000;">目录中包含却不存在</span>的文件夹
                file.mkdirs();
            }

            File txtFile = new File(sdcardPath1 + "/Holter_Data/" + FileName + ".txt");
            //判断文件是否存在，存在就删除
            if (txtFile.exists()) {
                txtFile.delete();
            } else {
                //创建文件
                txtFile.createNewFile();
            }

            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(txtFile));
            bufferedWriter.write(data);
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
