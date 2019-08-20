package com.example.laporinc.lapor;

import android.graphics.Bitmap;

public class Image {
    private Bitmap imageBitmap;

    public Image(){

    }

    public Image(Bitmap imageBitmap) {

        this.imageBitmap = imageBitmap;
    }

    public Bitmap getImageBitmap() {

        return imageBitmap;
    }

    public void setImageBitmap(Bitmap imageBitmap) {

        this.imageBitmap = imageBitmap;
    }
}
