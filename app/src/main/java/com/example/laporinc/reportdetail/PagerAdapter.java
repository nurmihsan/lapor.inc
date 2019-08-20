package com.example.laporinc.reportdetail;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.laporinc.R;

import java.util.ArrayList;

public class PagerAdapter extends android.support.v4.view.PagerAdapter {

    private Context context;
    private ArrayList<ImageModel> imageList;

    public PagerAdapter(ArrayList<ImageModel> imageList, Context context) {
        this.imageList = imageList;
        this.context = context;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        View view = LayoutInflater.from( context ).inflate( R.layout.image_collection_item, null );
        ImageView imageView = view.findViewById( R.id.iv_image_collection_item );
        imageView.setImageResource( imageList.get( position ).getImage() );

        container.addView( view );

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView( (View) object );
    }

    @Override
    public int getCount() {
        return (imageList != null)? imageList.size() : 0;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return o == view;
    }

}
