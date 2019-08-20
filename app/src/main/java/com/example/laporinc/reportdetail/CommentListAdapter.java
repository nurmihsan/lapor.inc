package com.example.laporinc.reportdetail;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.laporinc.R;

import java.util.ArrayList;

public class CommentListAdapter extends RecyclerView.Adapter<CommentListAdapter.CommentListViewHolder> {

    private ArrayList<Comment> commentList;
    private Context context;

    public CommentListAdapter(ArrayList<Comment> commentList, Context context) {
        this.commentList = commentList;
        this.context = context;
    }

    @NonNull
    @Override
    public CommentListAdapter.CommentListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from( viewGroup.getContext() );
        View view = layoutInflater.inflate( R.layout.comment_item , viewGroup, false);


        return new CommentListAdapter.CommentListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentListAdapter.CommentListViewHolder holder, int i) {
        holder.nama.setText( commentList.get( i ).getNama() );
        holder.waktu.setText( commentList.get( i ).getWaktu() );
        holder.isiKomentar.setText( commentList.get( i ).getIsiKomentar() );


        holder.nama.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        } );


    }

    @Override
    public int getItemCount() {
        return (commentList != null)? (commentList.size()) : 0;
    }

    public class CommentListViewHolder extends RecyclerView.ViewHolder {

        private TextView nama,waktu, isiKomentar;

        public CommentListViewHolder(@NonNull View itemView) {
            super( itemView );

            nama = (TextView) itemView.findViewById( R.id.tv_name );
            waktu = (TextView) itemView.findViewById( R.id.tv_time );
            isiKomentar = (TextView) itemView.findViewById( R.id.tv_comment );
        }
    }
}
