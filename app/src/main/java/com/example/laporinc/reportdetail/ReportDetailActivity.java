package com.example.laporinc.reportdetail;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.laporinc.R;

import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator;

public class ReportDetailActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private CircleIndicator circleIndicator;
    private PagerAdapter pagerAdapter;
    private ArrayList<ImageModel> imageList;

    private RecyclerView commentsRecyclerView;
    private ArrayList<Comment> commentList;
    private CommentListAdapter commentListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_report_detail );


        // set view pager (untuk image collection)
        imageList = new ArrayList<>(  );
        imageList.add( new ImageModel( R.drawable.sample_pic ) );
        imageList.add( new ImageModel( R.drawable.sample_pic ) );
        imageList.add( new ImageModel( R.drawable.sample_pic ) );
        imageList.add( new ImageModel( R.drawable.sample_pic ) );

        pagerAdapter = new PagerAdapter(imageList, this );
        viewPager = findViewById( R.id.view_pager );
        viewPager.setAdapter( pagerAdapter );
        circleIndicator = findViewById( R.id.circle );
        circleIndicator.setViewPager( viewPager );


        //set recycler view (untuk komentar)
        commentList = new ArrayList<>(  );
        commentsRecyclerView = (RecyclerView) findViewById( R.id.rv_comments );
        commentListAdapter = new CommentListAdapter( commentList, this );
        LinearLayoutManager layoutManager = new LinearLayoutManager( this );
        layoutManager.setOrientation( LinearLayoutManager.VERTICAL );
        commentsRecyclerView.setLayoutManager( layoutManager );
        commentsRecyclerView.setAdapter( commentListAdapter );

        addCommment();
    }

    private void addCommment(){

        commentList.add( new Comment( "Anonim", "1 hour ago", "accumsan nisl vitae, ornare purus. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean sed feugiat nisl. " ) );
        commentList.add( new Comment( "Budi Rahasa", "1 hour ago", "accumsan nisl vitae, ornare purus. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean sed feugiat nisl. " ) );
        commentList.add( new Comment( "Deti Dwi", "1 hour ago", "accumsan nisl vitae, ornare purus. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean sed feugiat nisl. " ) );
        commentList.add( new Comment( "Anonim", "1 hour ago", "accumsan nisl vitae, ornare purus. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean sed feugiat nisl. " ) );

        commentListAdapter.notifyDataSetChanged();
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
