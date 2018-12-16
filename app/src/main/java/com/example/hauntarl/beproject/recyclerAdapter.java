package com.example.hauntarl.beproject;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class recyclerAdapter extends RecyclerView.Adapter<recyclerAdapter.ViewHolder> {
    private int filesDeleted;
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View fileView = inflater.inflate(R.layout.layout_file, parent, false);
        ViewHolder viewHolder = new ViewHolder(fileView);
        return viewHolder;
    }
    private List<uploadFiles> uploadFilesList;
    private ArrayList<String> finalListToBeUploaded = new ArrayList<String>();


    public recyclerAdapter(List<uploadFiles> uploadFiles) {
        uploadFilesList = uploadFiles;
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        uploadFiles file = uploadFilesList.get(position);
        // Set item views based on your views and data model
        TextView textView = holder.fileName;
        textView.setText(file.getName());

    }

    @Override
    public int getItemCount() {
        return uploadFilesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView fileName;
        public CheckBox checkBox;
        public ViewHolder(View itemView) {
            super(itemView);
            fileName = itemView.findViewById(R.id.fileName);
            checkBox = itemView.findViewById(R.id.fileChecked);
            checkBox.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(checkBox.isChecked()) {
                finalListToBeUploaded.add(fileName.getText().toString());
            }else
                finalListToBeUploaded.remove(fileName.getText().toString());
        }
    }
    public void uploadAllSelectedFiles(final Context passedContext){
        if(finalListToBeUploaded.isEmpty()){
            new SweetAlertDialog(passedContext, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Oops...")
                    .setContentText("No Files Selected!")
                    .show();
        }else{
            final SweetAlertDialog sDailog = new SweetAlertDialog(passedContext, SweetAlertDialog.WARNING_TYPE);
                    sDailog.setTitleText("Are you sure?");
                    sDailog.setContentText(finalListToBeUploaded.size()+" file(s) will be uploaded!");
                    sDailog.setConfirmText("Yes, upload!");
                    sDailog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            initiateFirebaseUpload(passedContext);
                            sDailog.cancel();
                        }
                    }).show();
        }
    }

    private void initiateFirebaseUpload(final Context passedContext) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        filesDeleted =0;
        final SweetAlertDialog pDialog = new SweetAlertDialog(passedContext, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
        pDialog.show();

        for (final String eachFile: finalListToBeUploaded) {
            final Uri file = Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/Sensor Values/" + eachFile));
            StorageReference riversRef = storageReference.child("CSVs/" + file.getLastPathSegment());
            UploadTask uploadTask = riversRef.putFile(file);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    pDialog.cancel();
                    new SweetAlertDialog(passedContext, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText("Something went wrong, try again?")
                            .show();

                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    deleteFile(passedContext,file,pDialog);
                }
            });
        }


    }
    public void deleteFile(Context passedContext,Uri file, SweetAlertDialog pDialog){
        filesDeleted++;
        new File(file.getPath()).delete();
        if(filesDeleted == finalListToBeUploaded.size()){
            pDialog.cancel();
            new SweetAlertDialog(passedContext, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Good job!")
                    .setContentText("All files pushed!")
                    .show();
            Upload upload = new Upload();
            upload.updateUiAfterDeletion();
        }

    }
}
