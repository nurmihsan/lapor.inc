package com.example.laporinc.akun;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.laporinc.R;
import com.example.laporinc.laporansaya.LaporanActivity;
import com.example.laporinc.user.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class AkunFragment extends Fragment {

    private RecyclerView badgeRecyclerView;
    private ArrayList<Badge> badgeList;
    private BadgeListAdapter badgeListAdapter;
    private TextView userName;
    private TextView userPoin;
    private DatabaseReference databaseReference;
    private int poin;
    private String userId;
    private String namaUser;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private Toolbar actionBar;
    private Handler mainThreadHandler;


    public AkunFragment() {

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.fragment_akun, container,false);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        //set Recycler View
        badgeList = new ArrayList<>(  );
        badgeRecyclerView = (RecyclerView) view.findViewById( R.id.rv_badge_list );
        badgeRecyclerView.addItemDecoration( new DividerItemDecoration( getActivity(), LinearLayoutManager.HORIZONTAL ){
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                // Do not draw the divider
            }
        } );
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager( getActivity(), LinearLayoutManager.HORIZONTAL, false );
        badgeRecyclerView.setLayoutManager( horizontalLayoutManager );
        badgeListAdapter = new BadgeListAdapter( badgeList, getContext() );
        badgeRecyclerView.setAdapter( badgeListAdapter );
        addBadge();

        // mendapatkan user dari akun yang terautentikasi
        user = mAuth.getCurrentUser();
        userId = user.getUid();

        // Custom Action Bar
        userName = (TextView) view.findViewById( R.id.tv_nama_h );
        userPoin = (TextView) view.findViewById( R.id.tv_point_h );

        // ambil nama dan poin untuk actionbar
        getUserData();
        //set nama user dan poin user

//         This handler is used to handle child thread message from main thread message queue.
        mainThreadHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    // Update view component text, this is allowed.
                    userName.setText(namaUser);
                    userPoin.setText(Integer.toString(poin));
                }
            }
        };

        return view;
    }

    private void addBadge(){
        badgeList.add( new Badge( R.drawable.ellipse ) );
        badgeList.add( new Badge( R.drawable.ellipse ) );
        badgeList.add( new Badge( R.drawable.ellipse ) );
        badgeList.add( new Badge( R.drawable.ellipse ) );
        badgeList.add( new Badge( R.drawable.ellipse ) );
        badgeList.add( new Badge( R.drawable.ellipse ) );
        badgeList.add( new Badge( R.drawable.ellipse ) );
        badgeList.add( new Badge( R.drawable.ellipse ) );

        badgeListAdapter.notifyDataSetChanged();
    }

    private void getUserData() {

        databaseReference.child( "users" ).child( userId ).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {

                new Thread( new Runnable() {

                    @Override
                    public void run() {

                        User user = dataSnapshot.getValue( User.class );
                        //homeFragment.setNamaUser( user.getNama() );
                        namaUser = user.getNama();
                        poin = user.getPoin();



                        Message message = new Message();
                        message.what = 1;
                        //message.arg1 = i;
                        mainThreadHandler.sendMessage( message );

                    }
                } ).start();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        } );
    }
}
