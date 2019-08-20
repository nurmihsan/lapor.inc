package com.example.laporinc.recent;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

public class Post {
    private String lokasi;
    private String deskripsi;
    private String date;
    private String idPelapor;
    private String jenisPelanggaran;
    private String status;
    private String imageKey;
    private long order;

    public Post() {
    }

    public Post(String lokasi, String deskripsi, String date, String idPelapor, String jenisPelanggaran, String status, String imageKey, long order) {
        this.lokasi = lokasi;
        this.deskripsi = deskripsi;
        this.date = date;
        this.idPelapor = idPelapor;
        this.jenisPelanggaran = jenisPelanggaran;
        this.status = status;
        this.imageKey = imageKey;
        this.order = order;
    }

    public long getOrder() {
        return order;
    }

    public void setOrder(long order) {
        this.order = order;
    }

    public String getImageKey() {
        return imageKey;
    }

    public void setImageKey(String imageKey) {
        this.imageKey = imageKey;
    }


    public String getJenisPelanggaran() {
        return jenisPelanggaran;
    }

    public void setJenisPelanggaran(String jenisPelanggaran) {
        this.jenisPelanggaran = jenisPelanggaran;
    }


    public String getLokasi() {
        return lokasi;
    }

    public void setLokasi(String lokasi) {
        this.lokasi = lokasi;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getIdPelapor() {
        return idPelapor;
    }

    public void setIdPelapor(String idPelapor) {
        this.idPelapor = idPelapor;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}