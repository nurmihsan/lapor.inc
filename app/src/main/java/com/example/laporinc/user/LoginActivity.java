package com.example.laporinc.user;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.laporinc.MainActivity;
import com.example.laporinc.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPassword;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private ProgressBar progressBar;


    public void daftarSekarang(View view) {
        Intent intent = new Intent( getApplicationContext(), DaftarActivity.class );
        startActivity( intent );
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_login );

        etEmail = (EditText) findViewById( R.id.et_email );
        etPassword = (EditText) findViewById( R.id.et_password );
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        progressBar = (ProgressBar) findViewById( R.id.progressBar );

    }

    //fungsi signin untuk mengkonfirmasi data pengguna yang sudah mendaftar sebelumnya
    public void signIn(View view) {

        Log.d( "TAG", "signIn" );
        if (!validateForm()) {
            return;
        }

        //showProgressDialog();
        progressBar.setVisibility( View.VISIBLE );
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);



        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        mAuth.signInWithEmailAndPassword( email, password ).addOnCompleteListener( this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d( "TAG", "signIn:onComplete:" + task.isSuccessful() );
                //hideProgressDialog();
                progressBar.setVisibility( View.GONE );
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);


                if (task.isSuccessful()) {
                    onAuthSuccess( task.getResult().getUser() );
                } else {
                    Toast.makeText( LoginActivity.this, "Gagal masuk", Toast.LENGTH_SHORT ).show();
                }
            }
        } );
    }

    //fungsi dipanggil ketika proses Authentikasi berhasil
    private void onAuthSuccess(FirebaseUser user) {

        //String username = usernameFromEmail(user.getEmail());

        // membuat User baru
        //writeNewUser(user.getUid(), username, user.getEmail());


        // Go to MainActivity
        startActivity( new Intent( getApplicationContext(), MainActivity.class));
        finish();
    }

    /*
        ini fungsi buat bikin username dari email
            contoh email: abcdefg@mail.com
            maka username nya: abcdefg
     */
    private String usernameFromEmail(String email) {
        if (email.contains( "@" )) {
            return email.split( "@" )[0];
        } else {
            return email;
        }
    }

    //fungsi untuk memvalidasi EditText email dan password agar tak kosong dan sesuai format
    private boolean validateForm() {
        boolean result = true;
        if (TextUtils.isEmpty( etEmail.getText().toString() )) {
            etEmail.setError( "Harus diisi" );
            result = false;
        } else {
            etEmail.setError( null );
        }

        if (TextUtils.isEmpty( etPassword.getText().toString() )) {
            etPassword.setError( "Harus diisi" );
            result = false;
        } else {
            etPassword.setError( null );
        }

        return result;
    }


    // Dismiss keyboard when click outside of EditText
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View v = getCurrentFocus();

        if (v != null &&
                (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) &&
                v instanceof EditText &&
                !v.getClass().getName().startsWith("android.webkit.")) {
            int scrcoords[] = new int[2];
            v.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + v.getLeft() - scrcoords[0];
            float y = ev.getRawY() + v.getTop() - scrcoords[1];

            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom())
                hideKeyboard(this);
        }
        return super.dispatchTouchEvent(ev);
    }
    public static void hideKeyboard(LoginActivity activity) {
        if (activity != null && activity.getWindow() != null && activity.getWindow().getDecorView() != null) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService( Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }


}
