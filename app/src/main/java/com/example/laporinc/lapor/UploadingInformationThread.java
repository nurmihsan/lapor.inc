package com.example.laporinc.lapor;

import android.util.Log;

import com.example.laporinc.recent.Post;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UploadingInformationThread implements Runnable {

//    UploadingImageThread uploadingImageThread = new UploadingImageThread();
//    private Object lock = uploadingImageThread.getLOCK();

    private Thread uploadingInfo;
    private String lokasiKejadian, deskripsiKejadian, waktuKejadian, idUser, jenisPelanggaran, status, imageKey, postKey;
    long order;

    public UploadingInformationThread(String lokasiKejadian, String deskripsiKejadian, String waktuKejadian, String idUser, String jenisPelanggaran, String status, String imageKey, String postKey, long order) {
        this.lokasiKejadian = lokasiKejadian;
        this.deskripsiKejadian = deskripsiKejadian;
        this.waktuKejadian = waktuKejadian;
        this.idUser = idUser;
        this.jenisPelanggaran = jenisPelanggaran;
        this.status = status;
        this.imageKey = imageKey;
        this.postKey = postKey;
        this.order = order;
    }

    @Override
    public void run() {

//        synchronized (lock) {
//            try {
//                lock.wait();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Post post = new Post( lokasiKejadian, deskripsiKejadian, waktuKejadian, idUser, jenisPelanggaran, status, imageKey, order );
        databaseReference.child( "posts" ).child( postKey ).setValue( post );

//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

//        }
    }


        public void start () {
            if (uploadingInfo == null) {
                uploadingInfo = new Thread( this, "uploadingInfo" );
                uploadingInfo.start();
            }
        }

    }
