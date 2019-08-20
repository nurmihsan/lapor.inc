package com.example.laporinc.recent;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.laporinc.MainActivity;
import com.example.laporinc.R;
import com.example.laporinc.reportdetail.ReportDetailActivity;
import com.example.laporinc.user.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;

public class HomeFragment extends Fragment implements PostListAdapter.ItemClickListener, PostListAdapter.GenericItemClickListener, MainActivity.PassMethod {

    private static final Object LOCK = new Object();
    private Object lock = LOCK;

    private PostListAdapter adapter;
    private Intent intent;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private ArrayList<String> keys;
    private String namaUser;
    private int poin;
    private ArrayList<Post> posts;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private Long oldestPost;//4th data
    private Toolbar actionBar;
    private TextView userName;
    private TextView userPoin;
    private LinearLayoutManager layoutManager;
    private FirebaseUser user;
    private String userId;
    private PostListAdapter.ItemClickListener rv_listener;
    private RecyclerView.SmoothScroller smoothScroller;
    private SwipeRefreshLayout swipeRefreshLayout;
    //private View actionBarLayout;

    private Handler mainThreadHandler;
    private Thread setActionBarThread;
    private Thread loadDataThread;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.fragment_home, container, false );
        //View actionBarLayout = inflater.inflate( R.layout.home_action_bar, container, false );

        databaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        progressBar = (ProgressBar) view.findViewById( R.id.progressBar );
        keys = new ArrayList<>();
        recyclerView = (RecyclerView) view.findViewById( R.id.recyclerView );
        posts = new ArrayList<>();
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById( R.id.swipeRefreshLayout );

        // on Recycler View item click
        rv_listener = new PostListAdapter.ItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView.ViewHolder viewHolder, Object item, int position) {
                //Toast.makeText(getActivity(), "Item clicked: " + position, Toast.LENGTH_SHORT).show();
                Log.i( Integer.toString( position ), keys.get( position ) );

                Intent intent = new Intent( getActivity(), ReportDetailActivity.class );
                startActivity( intent );
            }
        };

        // adapter for recycler view
        adapter = new PostListAdapter( posts, getContext(), rv_listener );
        recyclerView.setAdapter( adapter );

        // smooth scrolling to top
        smoothScroller = new LinearSmoothScroller( getActivity() ) {
            @Override
            protected int getVerticalSnapPreference() {
                return LinearSmoothScroller.SNAP_TO_START;
            }
        };

        // Layout Manager for recycler view
        layoutManager = new LinearLayoutManager( getActivity() );
        layoutManager.setOrientation( LinearLayoutManager.VERTICAL );
        // reverse order ==================================================
        //((LinearLayoutManager) layoutManager).setReverseLayout( true );
        //((LinearLayoutManager) layoutManager).setStackFromEnd( true );
        //=================================================================
        recyclerView.setLayoutManager( layoutManager );


        // mendapatkan user dari akun yang terautentikasi
        user = mAuth.getCurrentUser();
        userId = user.getUid();


        // Custom Action Bar
        actionBar = (Toolbar) view.findViewById( R.id.actionBar );
        userName = (TextView) view.findViewById( R.id.tv_username );
        userPoin = (TextView) view.findViewById( R.id.tv_point );

        // ambil nama dan poin untuk actionbar
        getUserData();

        // mengambil data postingan
        getPosts();


        // This handler is used to handle child thread message from main thread message queue.
        mainThreadHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    // Update view component text, this is allowed.
                    ((AppCompatActivity) getActivity()).setSupportActionBar( actionBar );
                    userName.setText( namaUser );
                    userPoin.setText( Integer.toString( poin ) );
                } else if (msg.what == 2) {
                    progressBar.setVisibility( View.GONE );
                    adapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                } else if (msg.what == 3) {
                    Toast.makeText( getActivity(), "Sudah di akhir halaman", Toast.LENGTH_SHORT ).show();
                }

            }
        };


        // load more data when scrolling
        recyclerView.addOnScrollListener( new RecyclerView.OnScrollListener() {

            private int currentVisibleItemCount;
            private int currentScrollState;
            private int currentFirstVisibleItem;
            private int totalItem;
            //private LinearLayout lBelow;

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged( recyclerView, newState );

                // TODO Auto-generated method stub
                this.currentScrollState = newState;
                this.isScrollCompleted();
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled( recyclerView, dx, dy );

                this.currentVisibleItemCount = recyclerView.getChildCount();
                this.totalItem = layoutManager.getItemCount();
                this.currentFirstVisibleItem = layoutManager.findFirstVisibleItemPosition();
            }

            private void isScrollCompleted() {

                if (totalItem - currentFirstVisibleItem == currentVisibleItemCount && this.currentScrollState == SCROLL_STATE_IDLE) {

                    //Log.i("totalItem", Integer.toString( totalItem ));
                    //Log.i("currentFirstVisibleItem", Integer.toString( currentFirstVisibleItem ));
                    //Log.i("currentVisibleItemCount", Integer.toString( currentVisibleItemCount ));


                    progressBar.setVisibility( View.VISIBLE );


                    databaseReference.child( "posts" ).orderByChild( "order" ).startAt( oldestPost ).limitToFirst( 5 ).addListenerForSingleValueEvent( new ValueEventListener() {

                        @Override
                        public void onDataChange(final DataSnapshot dataSnapshot) {

                            new Thread( new Runnable() {
                                @Override
                                public void run() {

                                    Post postItem;

                                    for (DataSnapshot child : dataSnapshot.getChildren()) {

                                        Post post = child.getValue( Post.class );

                                        if (oldestPost == post.getOrder()) {
                                            continue;
                                        }

                                        String key = child.getKey();
                                        String idPelapor = post.getIdPelapor();
                                        String lokasi = post.getLokasi();
                                        String deskripsi = post.getDeskripsi();
                                        String date = post.getDate();
                                        String jenisPelanggaran = post.getJenisPelanggaran();
                                        String status = post.getStatus();
                                        String imageKey = post.getImageKey();
                                        Long order = post.getOrder();

                                        oldestPost = post.getOrder();

                                        if (status.equals(MainActivity.STATUS_PROCESSED  ) ) {
                                            postItem = new Post( lokasi, deskripsi, date, idPelapor, jenisPelanggaran, status, imageKey, order );
                                            posts.add( postItem );
                                            keys.add( key );
                                        }
                                    }

                                    if (oldestPost == 0){
                                        Message message = new Message();
                                        message.what = 3;
                                        //message.arg1 = i;
                                        mainThreadHandler.sendMessage( message );
                                    }

                                    Message message = new Message();
                                    message.what = 2;
                                    //message.arg1 = i;
                                    mainThreadHandler.sendMessage( message );

                                }
                            } ).start();

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    } );

                }

            }


        } );

        swipeRefreshLayout.setOnRefreshListener( new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPosts();
            }


        } );


        return view;
    }


    @Override
    public void onItemClicked(RecyclerView.ViewHolder vh, Object item, int pos) {

    }

    // tes
    public void goToTop() {
        //LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        //layoutManager.scrollToPosition( posts.size() - 1 );
        //layoutManager.scrollToPosition( 0 );
        smoothScroller.setTargetPosition( 0 );
        layoutManager.startSmoothScroll( smoothScroller );
    }


    public void getPosts() {

        progressBar.setVisibility( View.VISIBLE );


        databaseReference.child( "posts" ).orderByChild( "order" ).limitToFirst( 5 ).addValueEventListener( new ValueEventListener() {

            Post post;

            @Override
            public void onDataChange(final DataSnapshot snapshot) {

                new Thread( new Runnable() {
                    @Override
                    public void run() {
                        keys.clear();
                        posts.clear();

                        Log.e( "Count ", "" + snapshot.getChildrenCount() );

                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {

                            Post post = postSnapshot.getValue( Post.class );

                            String key = postSnapshot.getKey();
                            String idPelapor = post.getIdPelapor();
                            String lokasi = post.getLokasi();
                            String deskripsi = post.getDeskripsi();
                            String date = post.getDate();
                            String jenisPelanggaran = post.getJenisPelanggaran();
                            String status = post.getStatus();
                            String imageKey = post.getImageKey();
                            Long order = post.getOrder();
                            //String thumbnailUri = post.getThumbnail();

                            oldestPost = post.getOrder();

                            if (status.equals(MainActivity.STATUS_PROCESSED  ) ) {

                                post = new Post( lokasi, deskripsi, date, idPelapor, jenisPelanggaran, status, imageKey, order );
                                posts.add( post );
                                keys.add( key );
                            }
                        }

                        Message message = new Message();
                        message.what = 2;
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