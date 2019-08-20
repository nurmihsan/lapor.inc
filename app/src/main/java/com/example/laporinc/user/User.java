package com.example.laporinc.user;

public class User {
    public String email;
    public String nama;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getPoin() {
        return poin;
    }

    public void setPoin(int poin) {
        this.poin = poin;
    }

    public int poin;

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }


    public User(){
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String email, String nama, int poin) {
        this.email = email;
        this.nama = nama;
        this.poin = poin;
    }
}
