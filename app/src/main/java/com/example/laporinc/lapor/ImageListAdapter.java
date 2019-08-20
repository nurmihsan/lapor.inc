package com.example.laporinc.lapor;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.laporinc.R;

import java.util.ArrayList;

public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.ImageListViewHolder> {

    Context context;
    private ArrayList<Image> imageList;

    public ImageListAdapter(ArrayList<Image> imageList, Context context) {
        this.imageList = imageList;
        this.context = context;
    }

    @NonNull
    @Override
    public ImageListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from( parent.getContext() );

        View view = layoutInflater.inflate( R.layout.image_list_item, parent, false );

        return new ImageListViewHolder( view );
    }

    @Override
    public void onBindViewHolder(@NonNull final ImageListViewHolder holder, int i) {

        holder.imageId.setImageBitmap( imageList.get( i ).getImageBitmap() );

        holder.imageId.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        } );


    }

    @Override
    public int getItemCount() {
        return (imageList != null) ? (imageList.size()) : 0;
    }

    public class ImageListViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageId;

        public ImageListViewHolder(@NonNull View itemView) {
            super( itemView );
            imageId = (ImageView) itemView.findViewById( R.id.iv_image );

        }
    }


}
