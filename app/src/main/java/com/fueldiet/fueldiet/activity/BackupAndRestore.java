package com.fueldiet.fueldiet.activity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fueldiet.fueldiet.AutomaticBackup;
import com.fueldiet.fueldiet.BuildConfig;
import com.fueldiet.fueldiet.R;
import com.fueldiet.fueldiet.Utils;
import com.fueldiet.fueldiet.adapter.FoldersAdapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BackupAndRestore extends BaseActivity {
    private static final String TAG = "BackupAndRestore";

    Button backup, restore;
    TextView defaultDir;
    RecyclerView mRecyclerView;
    LinearLayoutManager mLayoutManager;
    FoldersAdapter mAdapter;
    AutomaticBackup automaticBackup;
    List<File> data;
    Locale locale;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_backup_and_restore);
        automaticBackup = new AutomaticBackup(this);

        Configuration configuration = getResources().getConfiguration();
        locale = configuration.getLocales().get(0);

        backup = findViewById(R.id.activity_backup_button_backup);
        restore = findViewById(R.id.activity_backup_button_restore);
        defaultDir = findViewById(R.id.activity_backup_textview_folder);
        mRecyclerView = findViewById(R.id.activity_backup_recyclerview_restore);
        data = new ArrayList<>();
        fillData();
        mLayoutManager= new LinearLayoutManager(this);
        mAdapter = new FoldersAdapter(this, data);
        mAdapter.setOnItemClickListener(new FoldersAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                selectFolder(position);
            }
        });

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);

        Context context = this;

        backup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Date c = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss", locale);
                String formattedDate = df.format(c);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("text/*");
                    intent.putExtra(Intent.EXTRA_TITLE, "fueldiet_" + formattedDate + ".csv");

                    startActivityForResult(intent, 1);
                } else {

                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    final EditText edittext = new EditText(context);
                    alert.setTitle(R.string.enter_backup_file_name);
                    alert.setMessage("");

                    alert.setView(edittext);
                    edittext.setText("fueldiet_" + formattedDate + ".csv");

                    alert.setPositiveButton(context.getString(R.string.confirm), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            String fileName = edittext.getText().toString();
                            File newBackup = new File(automaticBackup.backupDir.getAbsolutePath() + "/" + fileName);
                            dialog.dismiss();
                            Intent passData = new Intent();
                            //---set the data to pass back---
                            passData.setData(Uri.fromFile(newBackup));
                            setResult(MainActivity.RESULT_BACKUP, passData);
                            //---close the activity---
                            finish();
                        }
                    });

                    alert.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.cancel();
                        }
                    });

                    alert.show();
                }
            }
        });

        restore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fileDest = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                fileDest.addCategory(Intent.CATEGORY_OPENABLE);
                fileDest.setType("text/*");

                //Uri uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", automaticBackup.backupDir);
                //fileDest.setDataAndType(uri, "text/*");
                try {
                    startActivityForResult(fileDest, 0);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                defaultDir.setText(getString(R.string.android_10_saving));
            else
                defaultDir.setText(automaticBackup.backupDir.getCanonicalPath());
        } catch (IOException e) {
            defaultDir.setText("Error");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                if (data != null && data.getData() != null) {
                    Uri uri = data.getData();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(uri);
                            String msg = Utils.readCSVfile(inputStream, getApplicationContext());
                            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                        } catch (FileNotFoundException e) {
                            Log.e(TAG, "onActivityResult: file was not found on restore backup", e.fillInStackTrace());
                            e.printStackTrace();
                        }
                    } else {
                        Intent passData = new Intent();
                        //---set the data to pass back---
                        passData.setData(uri);
                        setResult(MainActivity.RESULT_RESTORE, passData);
                        //---close the activity---
                        finish();
                    }
                }
            }
        } else if (requestCode == 1) {
            //only for android 10 and up
            if (resultCode == RESULT_OK) {
                if (data != null && data.getData() != null) {
                    Uri uri = data.getData();
                    OutputStream outputStream = null;
                    try {
                        outputStream = getContentResolver().openOutputStream(uri);
                        String msg = Utils.createCSVfile(outputStream, getApplicationContext());
                        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                    } catch (FileNotFoundException e) {
                        Log.e(TAG, "onActivityResult: file was not found on create backup", e.fillInStackTrace());
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    private void fillData() {
        data.clear();
        data.addAll(automaticBackup.getAllBackups());

        /*
        Comparator<File> fileComparator = (o1, o2) ->
                Long.compare(o2.lastModified(), o1.lastModified());

        Collections.sort(data, fileComparator);*/

        data.sort((o1, o2) -> Long.compare(o2.lastModified(), o1.lastModified()));
    }

    private void selectFolder(int position) {
        File selected = data.get(position);
        Date d = new Date(selected.lastModified());
        SimpleDateFormat sdf = new SimpleDateFormat("dd. MM. yyyy, HH:mm", locale);

        LayoutInflater factory = LayoutInflater.from(this);
        final View confirmDialogView = factory.inflate(R.layout.dialog_backup_and_restore, null);
        final AlertDialog confirmDialog = new AlertDialog.Builder(this).create();
        confirmDialog.setView(confirmDialogView);
        confirmDialogView.findViewById(R.id.dialog_backup_restore_button_restore).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Utils.readCSVfile(Uri.fromFile(selected), getApplicationContext());
                confirmDialog.dismiss();
                Intent passData = new Intent();
                //---set the data to pass back---
                passData.setData(Uri.fromFile(selected));
                setResult(MainActivity.RESULT_RESTORE, passData);
                //---close the activity---
                finish();
            }
        });
        confirmDialogView.findViewById(R.id.dialog_backup_restore_button_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDialog.cancel();
            }
        });
        TextView when = confirmDialogView.findViewById(R.id.dialog_backup_restore_created);
        when.setText(sdf.format(d));
        TextView size = confirmDialogView.findViewById(R.id.dialog_backup_restore_size);
        double kb = selected.length() / 1024.0;
        if (kb < 1025)
            size.setText(String.format("%.0f KB", kb));
        else
            size.setText(String.format("%.2f MB", kb / 1024.0));
        confirmDialog.show();
    }
}
