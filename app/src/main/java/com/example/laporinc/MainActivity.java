package com.example.laporinc;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.laporinc.akun.AkunFragment;
import com.example.laporinc.lapor.LaporActivity;
import com.example.laporinc.laporansaya.LaporanActivity;
import com.example.laporinc.recent.HomeFragment;

public class MainActivity extends AppCompatActivity {

    public static final String STATUS_PENDING = "pending";
    public static final String STATUS_PROCESSED = "processed";
    public static final String STATUS_DECLINED = "declined";

    private BottomNavigationView bottomNavigationView;

    private final Fragment homeFragment = new HomeFragment();
    private final Fragment akunFragment = new AkunFragment();
    private final FragmentManager fm = getSupportFragmentManager();
    private Fragment active = homeFragment;

    private Intent intent;


    // tes2
    private PassMethod listener ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        intent = new Intent();

        bottomNavigationView = (BottomNavigationView) findViewById( R.id.bottomNavBar );
        bottomNavigationView.setOnNavigationItemSelectedListener( mOnNavigationItemSelectedListener );

        // Mengatur ukuran icon bottom navigation bar
//        BottomNavigationMenuView menuView = (BottomNavigationMenuView) bottomNavigationView.getChildAt( 0 );
//        for (int i = 0; i < menuView.getChildCount(); i++) {
//            final View iconView = menuView.getChildAt( i ).findViewById( android.support.design.R.id.icon );
//            final ViewGroup.LayoutParams layoutParams = iconView.getLayoutParams();
//            final DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
//            layoutParams.height = (int) TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_DIP, 25, displayMetrics );
//            layoutParams.width = (int) TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_DIP, 25, displayMetrics );
//            iconView.setLayoutParams( layoutParams );
//        }
//
        fm.beginTransaction().add( R.id.main_container, akunFragment, "2" ).commit();
        fm.beginTransaction().add( R.id.main_container, homeFragment, "1" ).commit();


    }

    public void setListener(PassMethod listener)
    {
        this.listener = listener ;
    }




    // on click (bottom navigation bar)
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.item_recent:
                    if (active != homeFragment) {
                        fm.beginTransaction().hide( active ).show( homeFragment ).commit();
                        active = homeFragment;
                    } else {
                        // tes2
                        setListener( (PassMethod) homeFragment );
                        listener.goToTop();

                    }

                    return true;

                case R.id.item_lapor:
                    Intent intent = new Intent( getApplicationContext(), LaporActivity.class );

                    if (bottomNavigationView.getSelectedItemId() == R.id.item_recent) {
                        startActivityForResult( intent, 1 );
                    } else if (bottomNavigationView.getSelectedItemId() == R.id.item_akun) {
                        startActivityForResult( intent, 2 );
                    } else {
                        startActivityForResult( intent, 1 );
                    }

                    return true;

                case R.id.item_akun:
                    fm.beginTransaction().hide( homeFragment ).show( akunFragment ).commit();
                    active = akunFragment;
                    return true;
            }
            return false;
        }
    };


    // balik dari LaporActivity.java
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult( requestCode, resultCode, data );

        bottomNavigationView.setOnNavigationItemSelectedListener( mOnNavigationItemSelectedListener );

        switch (resultCode) {
            case 0:
                // batal
                if (requestCode == 1) {
                    bottomNavigationView.setSelectedItemId( R.id.item_recent );
                } else if (requestCode == 2) {
                    bottomNavigationView.setSelectedItemId( R.id.item_akun );
                } else {
                    bottomNavigationView.setSelectedItemId( R.id.item_recent );
                }
                break;

            case 1:
                //laporan baru
                Toast.makeText( this, "Laporan berhasil dibuat", Toast.LENGTH_SHORT ).show();

                intent = data;

                setListener( (PassMethod) homeFragment );
                listener.getPosts();


                // refresh fragment
                //getSupportFragmentManager()
                //        .beginTransaction()
                //        .detach( homeFragment )
                //        .commitNowAllowingStateLoss();
                //getSupportFragmentManager()
                //        .beginTransaction()
                //        .attach( homeFragment )
                //        .commitAllowingStateLoss();

                //Log.i( "lokasi", intent.getStringExtra( "lokasi" ) );

                if (requestCode == 1) {
                    bottomNavigationView.setSelectedItemId( R.id.item_recent );
                } else if (requestCode == 2) {
                    bottomNavigationView.setSelectedItemId( R.id.item_akun );
                } else {
                    bottomNavigationView.setSelectedItemId( R.id.item_recent );
                }


                break;
        }

    }


    // tes 2
    public interface PassMethod
    {
        void goToTop() ;
        void getPosts();
    }

    public  void showReports(View view){
        Intent intent = new Intent( MainActivity.this, LaporanActivity.class );
        startActivity( intent );
    }
}


