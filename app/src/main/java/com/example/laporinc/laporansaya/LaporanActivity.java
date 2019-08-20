package com.example.laporinc.laporansaya;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.laporinc.MainActivity;
import com.example.laporinc.R;
import com.example.laporinc.recent.Post;
import com.example.laporinc.recent.PostListAdapter;
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

public class LaporanActivity extends AppCompatActivity {

    private static final Object LOCK = new Object();
    private Object lock = LOCK;

    private PostListAdapter adapter;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private ArrayList<String> keys;
    private ArrayList<Post> posts;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private Long oldestPost;//4th data
    private LinearLayoutManager layoutManager;
    private FirebaseUser user;
    private String userId;
    private PostListAdapter.ItemClickListener rv_listener;
    private SwipeRefreshLayout swipeRefreshLayout;
    //private View actionBarLayout;

    private Handler mainThreadHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_laporan );

        databaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        progressBar = (ProgressBar) findViewById( R.id.progressBar2 );
        keys = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById( R.id.rv_laporan );
        posts = new ArrayList<>();
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById( R.id.swipeRefreshLayout2 );

        // on Recycler View item click
        rv_listener = new PostListAdapter.ItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView.ViewHolder vh, Object item, int pos) {
                Log.i( Integer.toString( pos ), keys.get( pos ) );

                Intent intent = new Intent( LaporanActivity.this, ReportDetailActivity.class );
                startActivity( intent );
            }
        };

        // adapter for recycler view
        adapter = new PostListAdapter( posts, this, rv_listener );
        recyclerView.setAdapter( adapter );


        // Layout Manager for recycler view
        layoutManager = new LinearLayoutManager( this );
        layoutManager.setOrientation( LinearLayoutManager.VERTICAL );
        // reverse order ==================================================
        //((LinearLayoutManager) layoutManager).setReverseLayout( true );
        //((LinearLayoutManager) layoutManager).setStackFromEnd( true );
        //=================================================================
        recyclerView.setLayoutManager( layoutManager );


        // mendapatkan user dari akun yang terautentikasi
        user = mAuth.getCurrentUser();
        userId = user.getUid();

        // mengambil data postingan
        getPosts();


        // This handler is used to handle child thread message from main thread message queue.
        mainThreadHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 2) {
                    progressBar.setVisibility( View.GONE );
                    adapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing( false );
                } else if (msg.what == 3) {
                    Toast.makeText( LaporanActivity.this, "Sudah di akhir halaman", Toast.LENGTH_SHORT ).show();
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

                                        postItem = new Post( lokasi, deskripsi, date, idPelapor, jenisPelanggaran, status, imageKey, order );
                                        posts.add( postItem );
                                        keys.add( key );

                                    }

                                    if (oldestPost == 0) {
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
                            //Log.e( "Get Data", post.getLokasi());


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

                            Log.i( "outside if", status );
                            Log.i( "STATUS_PROCESSED", MainActivity.STATUS_PROCESSED );


                            Log.i( "inside if", status );

                            post = new Post( lokasi, deskripsi, date, idPelapor, jenisPelanggaran, status, imageKey, order );
                            posts.add( post );
                            keys.add( key );

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


    // mengatur action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate( R.menu.close_icon, menu );

        return super.onCreateOptionsMenu( menu );
    }

    // respon onclick icon "close" di action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();

        return super.onOptionsItemSelected( item );
    }
}
