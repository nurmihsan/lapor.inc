package com.example.laporinc.lapor;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.laporinc.MainActivity;
import com.example.laporinc.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickCancel;
import com.vansuita.pickimage.listeners.IPickResult;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LaporActivity extends AppCompatActivity {

    private ArrayList<Image> imageList;
    private RecyclerView imageRecyclerView;
    private ImageListAdapter imageAdapter;
    private Bitmap bitmap;
    private ArrayList<Bitmap> bitmaps;
    private RelativeLayout add_image;
    private Button p1_parkir, p2_vandalisme, p3_fasilitas, p4_kebersihan, p5_gedung, button_lapor;
    private ImageButton add_button;
    private TextView tambah_jenis, ganti_jenis, jenis_pelanggaran;
    private EditText lokasi, deskripsi;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Intent intent;
    private Geocoder geocoder;
    private DatabaseReference databaseReference;
    private String address, lokasiKejadian, deskripsiKejadian, waktuKejadian, jenisPelanggaran, waktuKejadian_v2, imageKey, postKey, idUser;
    long order;
    private int imageCount;
    private boolean uploading = true;
    private ProgressBar progressBar;
//    private OnUploadFinishListener mListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_lapor );


        progressBar = (ProgressBar) findViewById( R.id.progressBar );
        databaseReference = FirebaseDatabase.getInstance().getReference();
        intent = new Intent();

        imageList = new ArrayList<>();
        bitmaps = new ArrayList<>();
        //URIs = new ArrayList<>(  );
        imageList.clear();
        bitmaps.clear();
        //URIs.clear();

        button_lapor = (Button) findViewById( R.id.button_lapor );
        jenis_pelanggaran = (TextView) findViewById( R.id.jenis_pelanggaran );
        lokasi = (EditText) findViewById( R.id.et_lokasi );
        deskripsi = (EditText) findViewById( R.id.et_detail_laporan );

        imageRecyclerView = (RecyclerView) findViewById( R.id.image_list );
        imageRecyclerView.addItemDecoration( new DividerItemDecoration( LaporActivity.this, LinearLayoutManager.HORIZONTAL ){
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                // Do not draw the divider
            }
        } );
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager( LaporActivity.this, LinearLayoutManager.HORIZONTAL, false );
        imageRecyclerView.setLayoutManager( horizontalLayoutManager );
        imageAdapter = new ImageListAdapter( imageList, getApplicationContext() );
        imageRecyclerView.setAdapter( imageAdapter );

        // "Lapor!" disabled jika lokasi tidak diisi
        lokasi.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (imageList.size() >= 1 && !TextUtils.isEmpty( lokasi.getText() ) && jenis_pelanggaran.getVisibility() == View.VISIBLE) {
                    button_lapor.setEnabled( true );
                    button_lapor.setBackgroundResource( R.drawable.lapor_button_border_enabled );
                } else {
                    button_lapor.setEnabled( false );
                    button_lapor.setBackgroundResource( R.drawable.lapor_button_border_disabled );
                }
            }
        } );


        // pick image + tambahkan ke recycler view ketika baru masuk ke LaporActivity pertama kali
        PickImageDialog.build( new PickSetup() )
                .setOnPickResult( new IPickResult() {
                    @Override
                    public void onPickResult(PickResult r) {
                        if (r.getError() == null) {
                            bitmap = r.getBitmap();
                            if (bitmap != null) {
                                imageList.add( new Image( bitmap ) );
                                bitmaps.add( bitmap );
                                imageAdapter.notifyDataSetChanged();

                                if (jenis_pelanggaran.getVisibility() == View.VISIBLE && !TextUtils.isEmpty( lokasi.getText() )) {
                                    button_lapor.setEnabled( true );
                                    button_lapor.setBackgroundResource( R.drawable.lapor_button_border_enabled );
                                }
                            }
                            bitmap = null;
                        } else {
                            //Handle possible errors
                            //TODO: do what you have to do with r.getError();

                        }
                    }
                } )
                .setOnPickCancel( new IPickCancel() {
                    @Override
                    public void onCancelClick() {
                        //TODO: do what you have to if user clicked cancel

                    }
                } ).show( getSupportFragmentManager() );


        // pick image + tambahkan gambar ke recycler view ketika tombol "tambah gambar" onclik
        add_image = (RelativeLayout) findViewById( R.id.rl_add_image );
        add_image.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pick image
                PickImageDialog.build( new PickSetup() )
                        .setOnPickResult( new IPickResult() {
                            @Override
                            public void onPickResult(PickResult r) {
                                if (r.getError() == null) {
                                    bitmap = r.getBitmap();
                                    if (bitmap != null) {
                                        bitmaps.add( bitmap );
                                        imageList.add( new Image( bitmap ) );
                                        imageAdapter.notifyDataSetChanged();

                                        if (jenis_pelanggaran.getVisibility() == View.VISIBLE && !TextUtils.isEmpty( lokasi.getText() )) {
                                            button_lapor.setEnabled( true );
                                            button_lapor.setBackgroundResource( R.drawable.lapor_button_border_enabled );
                                        }
                                    }
                                    bitmap = null;
                                } else {
                                    //Handle possible errors
                                    //TODO: do what you have to do with r.getError();
                                }
                            }
                        } )
                        .setOnPickCancel( new IPickCancel() {
                            @Override
                            public void onCancelClick() {
                                //TODO: do what you have to if user clicked cancel

                            }
                        } ).show( getSupportFragmentManager() );
            }
        } );


        // onclick imageList item ( untuk pop up image dialog + delete)
        imageRecyclerView.addOnItemTouchListener( new RecyclerTouchListener( this, imageRecyclerView, new ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                //Values are passing to activity & to fragment as well
                //Toast.makeText( LaporActivity.this, "Single Click on position :" + position, Toast.LENGTH_SHORT ).show();
                final ImageView image = (ImageView) view.findViewById( R.id.iv_image );

                image.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Bitmap bmp = ((BitmapDrawable) image.getDrawable()).getBitmap();

                        Dialog builder = new Dialog( LaporActivity.this, android.R.style.Theme_Light );
                        builder.requestWindowFeature( Window.FEATURE_NO_TITLE );
                        builder.getWindow().setBackgroundDrawable( new ColorDrawable( Color.WHITE ) );
                        builder.setOnDismissListener( new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface) {
                                //nothing;
                            }
                        } );

                        // show full image when clicked
                        ImageView imageView = new ImageView( LaporActivity.this );
                        imageView.setImageBitmap( bmp );
                        builder.addContentView( imageView, new RelativeLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT ) );
                        builder.show();

                    }
                } );


                ImageView cancel_button = (ImageView) view.findViewById( R.id.cancel_button );
                cancel_button.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        imageList.remove( position );
                        bitmaps.remove( position );
                        if (imageList.size() == 0) {
                            button_lapor.setEnabled( false );
                            button_lapor.setBackgroundResource( R.drawable.lapor_button_border_disabled );
                        }

                        imageAdapter.notifyDataSetChanged();
                    }
                } );

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        } ) );



        databaseReference.child( "posts" ).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                order = snapshot.getChildrenCount() * -1;



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );

    }

    public static interface ClickListener {
        public void onClick(View view, int position);

        public void onLongClick(View view, int position);
    }


    class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {
        private ClickListener clicklistener;
        private GestureDetector gestureDetector;

        public RecyclerTouchListener(Context context, final RecyclerView recycleView, final ClickListener clicklistener) {
            this.clicklistener = clicklistener;
            gestureDetector = new GestureDetector( context, new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recycleView.findChildViewUnder( e.getX(), e.getY() );
                    if (child != null && clicklistener != null) {
                        clicklistener.onLongClick( child, recycleView.getChildAdapterPosition( child ) );
                    }
                }
            } );
        }


        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder( e.getX(), e.getY() );

            if (child != null && clicklistener != null && gestureDetector.onTouchEvent( e )) {
                clicklistener.onClick( child, rv.getChildAdapterPosition( child ) );
            }
            return false;
        }


        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }


        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }


    // showing bottom sheet dialog
    //@OnClick(R.id.btn_bottom_sheet_dialog)
    public void showBottomSheetDialog(View v) {
        final View view = getLayoutInflater().inflate( R.layout.fragment_bottom_sheet_dialog, null );


        final BottomSheetDialog dialog = new BottomSheetDialog( this );
        dialog.setContentView( view );
        dialog.show();


        p1_parkir = (Button) view.findViewById( R.id.b_tertip_parkir );
        p2_vandalisme = (Button) view.findViewById( R.id.b_vandalisme );
        p3_fasilitas = (Button) view.findViewById( R.id.b_fasilitas_publik );
        p4_kebersihan = (Button) view.findViewById( R.id.b_kebersihan );
        p5_gedung = (Button) view.findViewById( R.id.b_gedung_ruangan );

        add_button = (ImageButton) findViewById( R.id.ib_add_button );
        tambah_jenis = (TextView) findViewById( R.id.tv_tambah_jenis );
        ganti_jenis = (TextView) findViewById( R.id.tv_ganti_jenis );


        p1_parkir.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_button.setVisibility( View.GONE );
                tambah_jenis.setVisibility( View.GONE );
                ganti_jenis.setVisibility( View.VISIBLE );
                jenis_pelanggaran.setVisibility( View.VISIBLE );
                jenis_pelanggaran.setText( p1_parkir.getText() );
                jenis_pelanggaran.setCompoundDrawablesWithIntrinsicBounds( R.drawable.tertip_parkir_icon, 0, 0, 0 );

                if (imageList.size() >= 1 && !TextUtils.isEmpty( lokasi.getText() ) && jenis_pelanggaran.getVisibility() == View.VISIBLE) {
                    button_lapor.setEnabled( true );
                    button_lapor.setBackgroundResource( R.drawable.lapor_button_border_enabled );
                }
                dialog.dismiss();
            }
        } );


        p2_vandalisme.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_button.setVisibility( View.GONE );
                tambah_jenis.setVisibility( View.GONE );
                ganti_jenis.setVisibility( View.VISIBLE );
                jenis_pelanggaran.setVisibility( View.VISIBLE );
                jenis_pelanggaran.setText( p2_vandalisme.getText() );
                jenis_pelanggaran.setCompoundDrawablesWithIntrinsicBounds( R.drawable.vandalisme_icon, 0, 0, 0 );

                if (imageList.size() >= 1 && !TextUtils.isEmpty( lokasi.getText() ) && jenis_pelanggaran.getVisibility() == View.VISIBLE) {
                    button_lapor.setEnabled( true );
                    button_lapor.setBackgroundResource( R.drawable.lapor_button_border_enabled );
                }

                dialog.dismiss();
            }
        } );

        p3_fasilitas.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_button.setVisibility( View.GONE );
                tambah_jenis.setVisibility( View.GONE );
                ganti_jenis.setVisibility( View.VISIBLE );
                jenis_pelanggaran.setVisibility( View.VISIBLE );
                jenis_pelanggaran.setText( p3_fasilitas.getText() );
                jenis_pelanggaran.setCompoundDrawablesWithIntrinsicBounds( R.drawable.fasilitas_publik_icon, 0, 0, 0 );

                if (imageList.size() >= 1 && !TextUtils.isEmpty( lokasi.getText() ) && jenis_pelanggaran.getVisibility() == View.VISIBLE) {
                    button_lapor.setEnabled( true );
                    button_lapor.setBackgroundResource( R.drawable.lapor_button_border_enabled );
                }

                dialog.dismiss();
            }
        } );

        p4_kebersihan.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_button.setVisibility( View.GONE );
                tambah_jenis.setVisibility( View.GONE );
                ganti_jenis.setVisibility( View.VISIBLE );
                jenis_pelanggaran.setVisibility( View.VISIBLE );
                jenis_pelanggaran.setText( p4_kebersihan.getText() );
                jenis_pelanggaran.setCompoundDrawablesWithIntrinsicBounds( R.drawable.kebersihan_icon, 0, 0, 0 );

                if (imageList.size() >= 1 && !TextUtils.isEmpty( lokasi.getText() ) && jenis_pelanggaran.getVisibility() == View.VISIBLE) {
                    button_lapor.setEnabled( true );
                    button_lapor.setBackgroundResource( R.drawable.lapor_button_border_enabled );
                }

                dialog.dismiss();
            }
        } );

        p5_gedung.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_button.setVisibility( View.GONE );
                tambah_jenis.setVisibility( View.GONE );
                ganti_jenis.setVisibility( View.VISIBLE );
                jenis_pelanggaran.setVisibility( View.VISIBLE );
                jenis_pelanggaran.setText( p5_gedung.getText() );
                jenis_pelanggaran.setCompoundDrawablesWithIntrinsicBounds( R.drawable.gedung_ruangan_icon, 0, 0, 0 );

                if (imageList.size() >= 1 && !TextUtils.isEmpty( lokasi.getText() ) && jenis_pelanggaran.getVisibility() == View.VISIBLE) {
                    button_lapor.setEnabled( true );
                    button_lapor.setBackgroundResource( R.drawable.lapor_button_border_enabled );
                }

                dialog.dismiss();
            }
        } );

    }


    // get current location on button click
    public void getLocation(View view) {


        locationManager = (LocationManager) this.getSystemService( Context.LOCATION_SERVICE );
        locationListener = new LocationListener() {

            // get location
            @Override
            public void onLocationChanged(Location location) {
                //LatLng userLocation = new LatLng( location.getLatitude(), location.getLongitude() );

                geocoder = new Geocoder( getApplicationContext(), Locale.getDefault() );

                try {
                    List<Address> listAddresses = geocoder.getFromLocation( location.getLatitude(), location.getLongitude(), 1 );

                    if (listAddresses != null && listAddresses.size() > 0) {
                        if (listAddresses.get( 0 ).getAddressLine( 0 ) != null) {
                            address = listAddresses.get( 0 ).getAddressLine( 0 );
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText( LaporActivity.this, "Error", Toast.LENGTH_SHORT ).show();
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };


        // request location access
        if (Build.VERSION.SDK_INT < 23) {
            if (ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, locationListener );
        } else {
            if (ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions( this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1 );
            } else {

                locationManager = (LocationManager) this.getSystemService( Context.LOCATION_SERVICE );

                locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, locationListener );

                Location lastKnownLocation = locationManager.getLastKnownLocation( LocationManager.GPS_PROVIDER );

                geocoder = new Geocoder( getApplicationContext(), Locale.getDefault() );

                try {
                    List<Address> listAddresses = geocoder.getFromLocation( lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(), 1 );

                    if (listAddresses != null && listAddresses.size() > 0) {
                       if (listAddresses.get( 0 ).getAddressLine( 0 ) != null) {
                            address = listAddresses.get( 0 ).getAddressLine( 0 );
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText( LaporActivity.this, "Error", Toast.LENGTH_SHORT ).show();
                }


            }
        }

        lokasi.setText( address );

    }

    // request permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult( requestCode, permissions, grantResults );

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, locationListener );
            }
        }
    }


    // on back pressed
    @Override
    public void onBackPressed() {

        new AlertDialog.Builder( LaporActivity.this )
                .setIcon( android.R.drawable.ic_dialog_alert )
                .setTitle( "Kembali" )
                .setMessage( "Apakah anda ingin membatalkan laporan?" )
                .setNegativeButton( "Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                } )
                .setPositiveButton( "Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        finish();
                    }
                } )
                .show();
    }


    //link with lapor_action_bar.xml (meng-attach icon close)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate( R.menu.lapor_action_bar, menu );

        return super.onCreateOptionsMenu( menu );
    }


    //respon onClick icon "close" di action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        onBackPressed();
        return super.onOptionsItemSelected( item );
    }


    // tombol "Lapor!" onclick
    public void lapor(View view) {

        intent.putExtra( "uploading", "true" );
        Log.i( "order", Long.toString( order ));

        //showProgressDialog();
        progressBar.setVisibility( View.VISIBLE );
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE );



        // jika deskripsi tidak diisi
        if (deskripsi == null) {
            deskripsi.setText( " " );
        }

        // menyimpan waktu saat ini
        DateFormat dateFormat = new SimpleDateFormat( "dd/MM/yyyy HH:mm" );
        DateFormat dateFormat_v2 = new SimpleDateFormat( "dd-MM-yyy_HH:mm" );
        Date date = new Date();

        lokasiKejadian = lokasi.getText().toString();
        deskripsiKejadian = deskripsi.getText().toString();
        waktuKejadian = dateFormat.format( date );
        waktuKejadian_v2 = dateFormat_v2.format( date );
        jenisPelanggaran = jenis_pelanggaran.getText().toString();
        //idPelapor = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //status = "unverified";
        //key = databaseReference.push().getKey();

       imageKey = databaseReference.push().getKey();

        LaporActivity activity = new LaporActivity();
//        OnUploadFinishListener mListener = new OnUploadFinishCallback();
//        activity.registerOnUploadFinishListener( mListener );

        postKey = databaseReference.push().getKey();
        idUser = FirebaseAuth.getInstance().getCurrentUser().getUid();


        UploadingInformationThread uploadingInformationThread = new UploadingInformationThread( lokasiKejadian, deskripsiKejadian, waktuKejadian, idUser, jenisPelanggaran, MainActivity.STATUS_PENDING, imageKey, postKey, order );
        uploadingInformationThread.start();


        UploadingImageThread uploadingImageThread = new UploadingImageThread( this, bitmaps, imageKey, jenisPelanggaran, waktuKejadian_v2, lokasiKejadian );
        uploadingImageThread.start();

    }

    public void finishActivity() {
        setResult( 1, intent );
        finish();
    }


    // ketika activity otw finished
    @Override
    protected void onStop() {
        super.onStop();
        if (intent.getExtras() == null) {
            setResult( 0, intent );
        } else {
            //udah diatur di public void lapor(View view)
            //setResult(1,intent);
        }
    }

    // Dismiss keyboard when click outside of EditText
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View v = getCurrentFocus();

        if (v != null &&
                (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) &&
                v instanceof EditText &&
                !v.getClass().getName().startsWith( "android.webkit." )) {
            int scrcoords[] = new int[2];
            v.getLocationOnScreen( scrcoords );
            float x = ev.getRawX() + v.getLeft() - scrcoords[0];
            float y = ev.getRawY() + v.getTop() - scrcoords[1];

            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom())
                hideKeyboard( this );
        }
        return super.dispatchTouchEvent( ev );
    }

    public static void hideKeyboard(LaporActivity activity) {
        if (activity != null && activity.getWindow() != null && activity.getWindow().getDecorView() != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService( Context.INPUT_METHOD_SERVICE );
            imm.hideSoftInputFromWindow( activity.getWindow().getDecorView().getWindowToken(), 0 );
        }
    }

}

