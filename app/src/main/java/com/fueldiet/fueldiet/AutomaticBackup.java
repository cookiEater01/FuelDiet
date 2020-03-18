package com.fueldiet.fueldiet;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.preference.PreferenceManager;

import com.fueldiet.fueldiet.activity.BaseActivity;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AutomaticBackup {

    public File backupDir;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public AutomaticBackup() {
        File dir = Environment.getExternalStorageDirectory();

        String fueldietPath = dir.getAbsolutePath() + "/Fueldiet backups";
        backupDir = new File(fueldietPath);

        if (!backupDir.exists()) {
            backupDir.mkdir();
        }
    }

    public List<File> getAllBackups() {
        List<File> files = new ArrayList<>();
        if (!backupDir.exists()) {
            return files;
        }
        files = Arrays.asList(
                backupDir.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        if (name.contains(".csv"))
                            return true;
                        return false;
                    }
        }));
        return files;
    }

    private File[] getOldBackups() {
        if (!backupDir.exists()) {
            return new File[]{};
        }
        return backupDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if (name.contains("fueldiet_autobackup_") && name.contains(".csv"))
                    return true;
                return false;
            }
        });
    }

    public void createBackup(Context context) {

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean autoBackup = pref.getBoolean("auto_backup", false);

        if (!autoBackup)
            return;

        File[] files = getOldBackups();

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss");
        String formattedDate = df.format(c);

        if (files.length < 10) {
            //create backup
            File newBackup = new File(backupDir.getAbsolutePath() + "/fueldiet_autobackup_"+ formattedDate + ".csv");
            if (Utils.createCSVfile(Uri.fromFile(newBackup), context))
                Toast.makeText(context, context.getString(R.string.backup_created), Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(context, context.getString(R.string.backup_error), Toast.LENGTH_SHORT).show();
        } else {
            //remove oldest and create new
            long oldestDate = Long.MAX_VALUE;
            File oldestFile = null;
            for(File f : files){
                if(f.lastModified() < oldestDate){
                    oldestDate = f.lastModified();
                    oldestFile = f;
                }
            }
            if(oldestFile != null){
                oldestFile.delete();
            }
            File newBackup = new File(backupDir.getAbsolutePath() + "/fueldiet_autobackup_"+ formattedDate + ".csv");
            if (Utils.createCSVfile(Uri.fromFile(newBackup), context))
                Toast.makeText(context, context.getString(R.string.backup_created), Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(context, context.getString(R.string.backup_error), Toast.LENGTH_SHORT).show();
        }
    }
}