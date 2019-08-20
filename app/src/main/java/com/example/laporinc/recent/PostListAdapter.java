package com.example.laporinc.recent;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.laporinc.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class PostListAdapter extends RecyclerView.Adapter<PostListAdapter.RecentCardViewViewHolder> {

    private ArrayList<Post> dataList;

    // untuk on click item
    Context context;
    ItemClickListener itemClickListener;

    public PostListAdapter(ArrayList<Post> dataList, Context context, ItemClickListener itemClickListener) {
        this.dataList = dataList;
        this.context = context;
        this.itemClickListener = itemClickListener;
    }


    // DON'T PASS YOUR DATA HERE, just create a ViewHolder
    @Override
    public RecentCardViewViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from( viewGroup.getContext() );
        View view = layoutInflater.inflate( R.layout.post_item, viewGroup, false );

        RecentCardViewViewHolder holder = new RecentCardViewViewHolder( view, context );

        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull final PostListAdapter.RecentCardViewViewHolder holder, final int i) {

        final Post data = dataList.get( i );

        // set click listener
        holder.itemView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onItemClicked( holder, data, i );
            }
        } );

        holder.location.setText( dataList.get( i ).getLokasi() );
        holder.deskripsi.setText( dataList.get( i ).getDeskripsi() );
        holder.date.setText( dataList.get( i ).getDate() );
        holder.status.setText( dataList.get( i ).getStatus() );

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        final Bitmap[] bitmap = new Bitmap[1];

        final String[] thumbnailUri = new String[1];


        databaseReference.child( "images" ).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot snapshot) {

                if (i < dataList.size()) {

                    thumbnailUri[0] = (String) snapshot.child( dataList.get( i ).getImageKey() ).child( "0" ).getValue();

                    if (context != null) {

                        GlideApp.with( context )
                                .load( thumbnailUri[0] )
                                .centerCrop()
                                .into( holder.foto );
                    }
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        } );

    }


    @Override
    public int getItemCount() {
        return (dataList != null) ? dataList.size() : 0;
    }

    public class RecentCardViewViewHolder extends RecyclerView.ViewHolder {

        private TextView location;
        private TextView deskripsi;
        private TextView date;
        private ImageView foto;
        private TextView status;


        public RecentCardViewViewHolder(@NonNull View itemView, Context theContext) {
            super( itemView );
            context = theContext;

            location = (TextView) itemView.findViewById( R.id.tv_location );
            deskripsi = (TextView) itemView.findViewById( R.id.tv_description );
            date = (TextView) itemView.findViewById( R.id.tv_time );
            foto = (ImageView) itemView.findViewById( R.id.iv_foto );
            status = (TextView) itemView.findViewById( R.id.tv_status );
        }
    }

    public interface ItemClickListener {

        void onItemClicked(RecyclerView.ViewHolder vh, Object item, int pos);
    }

    public interface GenericItemClickListener<T, VH extends RecyclerView.ViewHolder> {

        void onItemClicked(VH vh, T item, int pos);
    }


    public class ImageDownloader extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {

            try {

                URL url = new URL( urls[0] );

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.connect();

                InputStream inputStream = connection.getInputStream();

                Bitmap myBitmap = BitmapFactory.decodeStream( inputStream );

                return myBitmap;


            } catch (MalformedURLException e) {

                e.printStackTrace();

            } catch (IOException e) {

                e.printStackTrace();

            }

            return null;

        }


    }


}
