package com.example.laporinc.lapor;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.UUID;

public class UploadingImageThread implements Runnable {

//    private static final Object LOCK = new Object();
//    private Object lock = LOCK;

    private Context context;
    private Thread uploadingImage;
    private ArrayList<Bitmap> bitmaps;
    private String imageKey, jenisPelanggaran, waktuKejadian, lokasiKejadian;

//    public static Object getLOCK() {
//        return LOCK;
//    }


    public UploadingImageThread() {

    }

    public UploadingImageThread(Context context, ArrayList<Bitmap> bitmaps, String imageKey, String jenisPelanggaran, String waktuKejadian, String lokasiKejadian) {
        this.context = context;
        this.bitmaps = bitmaps;
        this.imageKey = imageKey;
        this.jenisPelanggaran = jenisPelanggaran;
        this.waktuKejadian = waktuKejadian;
        this.lokasiKejadian = lokasiKejadian;
    }

    @Override
    public void run() {

//        synchronized (lock) {

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final int[] imageCount = {0};

        String folderName = jenisPelanggaran + "/" + waktuKejadian + "_" + lokasiKejadian;


        for (Bitmap bitmap : bitmaps) {
            final StorageReference imageRef = storage
                    .getReferenceFromUrl( "gs://laporinc.appspot.com/" )
                    .child( folderName + "/" + UUID.randomUUID() + ".jpeg" );

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress( Bitmap.CompressFormat.JPEG, 20, baos );
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = imageRef.putBytes( data );


            uploadTask.addOnFailureListener( new OnFailureListener() {

                @Override
                public void onFailure(@NonNull Exception exception) {

                    //Toast.makeText( LaporActivity.this, "Gagal mengunggah foto", Toast.LENGTH_SHORT ).show();

                }

            } ).addOnSuccessListener( new OnSuccessListener<UploadTask.TaskSnapshot>() {

                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    imageRef.getDownloadUrl().addOnSuccessListener( new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {


                            databaseReference.child( "images" ).child( imageKey ).child( Integer.toString( imageCount[0] ) ).setValue( uri.toString() );

                            imageCount[0]++;

                            if (imageCount[0] == bitmaps.size()) {
                                 ((Activity)context).setResult( 1, null );
                                ((Activity)context).finish();

                            }
                        }
                    } );
                }
            } );


//            }

//            lock.notifyAll();

        }




    }

    public void start() {
        if (uploadingImage == null) {
            uploadingImage = new Thread( this, "uploadingImage" );
            uploadingImage.start();
        }
    }
}
