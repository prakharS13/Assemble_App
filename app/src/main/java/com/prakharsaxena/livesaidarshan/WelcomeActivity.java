package com.prakharsaxena.livesaidarshan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class WelcomeActivity extends AppCompatActivity {
    Button verify;
    TextView skip;
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        getSupportActionBar().hide();

        final LinearLayout linearLayout=(LinearLayout) findViewById(R.id.linearLayout);
        verify=(Button) findViewById(R.id.verifyEmail);
        skip=(TextView) findViewById(R.id.skipNow);
        mAuth=FirebaseAuth.getInstance();
        progressBar=(ProgressBar) findViewById(R.id.progress);
        final FirebaseUser firebaseUser=mAuth.getCurrentUser();

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verify.setVisibility(View.INVISIBLE);
                skip.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                assert firebaseUser != null;
                firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                             @Override
                             public void onComplete(@NonNull Task<Void> task) {
                                 if(!task.isSuccessful()){
                                     verify.setVisibility(View.VISIBLE);
                                     skip.setVisibility(View.VISIBLE);
                                     Snackbar.make(linearLayout, "Try again later!", Snackbar.LENGTH_LONG).show();
                                 }
                                 else{
                                     Toast.makeText(getApplicationContext(),"Email sent!Check your mail.",Toast.LENGTH_LONG).show();
                                     if(firebaseUser.isEmailVerified()) {
                                         progressBar.setVisibility(View.GONE);
                                         Intent i = new Intent(getApplicationContext(), UserActivity.class);
                                         startActivity(i);
                                         finish();
                                     }

                                 }
                             }
                         });
            }
        });
    }
}
