package com.example.hauntarl.beproject;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

public class uploadFiles {
    private static File folder;
    private String fileName;
    private boolean isChecked;


    public uploadFiles(String fileName){
        this.fileName = fileName;
        this.isChecked = false;
    }
    public String getName(){
        return this.fileName;
    }
    public void  toggleCheck(){
        this.isChecked = !this.isChecked;
    }
    public String isChecked(){
        if(this.isChecked)
            return this.fileName;
        return null;
    }
    public static ArrayList<uploadFiles> intiFiles() {
        ArrayList<uploadFiles> files = new ArrayList<uploadFiles>();
        folder = new File(Environment.getExternalStorageDirectory()
                + "/Sensor Values");
        for (File f : folder.listFiles()) {
            Log.d("found","found");
            if (f.isFile()) {
                files.add(new uploadFiles(f.getName()));
            }
        }

        return files;
    }
}
