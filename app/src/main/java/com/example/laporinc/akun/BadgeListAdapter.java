package com.example.laporinc.akun;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.laporinc.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class BadgeListAdapter extends RecyclerView.Adapter<BadgeListAdapter.BadgeListViewHolder> {

    private ArrayList<Badge> badgeList;
    private Context context;

    public BadgeListAdapter(ArrayList<Badge> badgeList, Context context) {
        this.badgeList = badgeList;
        this.context = context;
    }

    @NonNull
    @Override
    public BadgeListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from( viewGroup.getContext() );

        View view = layoutInflater.inflate( R.layout.badge_item, viewGroup, false );

        return new BadgeListAdapter.BadgeListViewHolder( view );
    }

    @Override
    public void onBindViewHolder(@NonNull BadgeListViewHolder holder, int i) {
        holder.circleImageView.setImageResource( badgeList.get( i ).getImageId() );
        holder.circleImageView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        } );
    }

    @Override
    public int getItemCount() {
        return (badgeList != null) ? (badgeList.size()) : 0;
    }

    public class BadgeListViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView circleImageView;

        public BadgeListViewHolder(@NonNull View itemView) {
            super( itemView );

            circleImageView = (CircleImageView) itemView.findViewById( R.id.civ_badge );
        }
    }
}






