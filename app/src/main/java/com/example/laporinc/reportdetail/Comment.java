package com.example.laporinc.reportdetail;

public class Comment {

    private String nama, waktu, isiKomentar;

    public Comment(String nama, String waktu, String isiKomentar) {
        this.nama = nama;
        this.waktu = waktu;
        this.isiKomentar = isiKomentar;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getWaktu() {
        return waktu;
    }

    public void setWaktu(String waktu) {
        this.waktu = waktu;
    }

    public String getIsiKomentar() {
        return isiKomentar;
    }

    public void setIsiKomentar(String isiKomentar) {
        this.isiKomentar = isiKomentar;
    }
}

