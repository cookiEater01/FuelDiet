package com.fueldiet.fueldiet.activity;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
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
import com.fueldiet.fueldiet.adapter.ConsumptionAdapter;
import com.fueldiet.fueldiet.adapter.FoldersAdapter;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class BackupAndRestore extends BaseActivity {

    Button backup, restore;
    TextView defaultDir;
    RecyclerView mRecyclerView;
    LinearLayoutManager mLayoutManager;
    FoldersAdapter mAdapter;
    AutomaticBackup automaticBackup;
    List<File> data;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_backup_and_restore);
        automaticBackup = new AutomaticBackup();

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
                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss");
                String formattedDate = df.format(c);

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
                        if (Utils.createCSVfile(Uri.fromFile(newBackup), getApplicationContext())) {
                            Toast.makeText(getApplicationContext(), getString(R.string.backup_created), Toast.LENGTH_SHORT).show();
                            fillData();
                            mAdapter.notifyItemChanged(0);
                        }else
                            Toast.makeText(getApplicationContext(), getString(R.string.backup_error), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });

                alert.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });

                alert.show();
            }
        });

        restore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fileDest = new Intent(Intent.ACTION_GET_CONTENT);
                Uri uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", automaticBackup.backupDir);
                fileDest.setDataAndType(uri, "text/*");
                try {
                    startActivityForResult(fileDest, 0);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        try {
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
                    Utils.readCSVfile(data.getData(), this);
                }
            }
        }
    }

    private void fillData() {
        data.clear();
        data.addAll(automaticBackup.getAllBackups());

        data.sort(new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return (int)(o2.lastModified() - o1.lastModified());
            }
        });
    }

    private void selectFolder(int position) {
        File selected = data.get(position);
        Date d = new Date(selected.lastModified());
        SimpleDateFormat sdf = new SimpleDateFormat("dd. MM. yyyy, HH:mm");

        LayoutInflater factory = LayoutInflater.from(this);
        final View confirmDialogView = factory.inflate(R.layout.dialog_backup_and_restore, null);
        final AlertDialog confirmDialog = new AlertDialog.Builder(this).create();
        confirmDialog.setView(confirmDialogView);
        confirmDialogView.findViewById(R.id.dialog_backup_restore_button_restore).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.readCSVfile(Uri.fromFile(selected), getApplicationContext());
                confirmDialog.dismiss();
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
        size.setText(String.format("%.2g MB", selected.length() / 1024.0));
        confirmDialog.show();
    }
}