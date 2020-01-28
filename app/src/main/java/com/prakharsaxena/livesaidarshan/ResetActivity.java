package com.prakharsaxena.livesaidarshan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import java.util.Objects;

public class ResetActivity extends AppCompatActivity {
    EditText resetEmail;
    Button reset;
    String email;
    ProgressBar progressBar;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);
        getSupportActionBar().hide();

        progressBar=(ProgressBar) findViewById(R.id.progressResetAccount);
        final LinearLayout linearLayout=(LinearLayout) findViewById(R.id.linearLayout);
        resetEmail=(EditText) findViewById(R.id.resetEmail);
        reset=(Button) findViewById(R.id.resetPassword);
        mAuth=FirebaseAuth.getInstance();

        reset.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                reset.setVisibility(View.INVISIBLE);
                if(!TextUtils.isEmpty(resetEmail.getText().toString())) {
                    mAuth.sendPasswordResetEmail(resetEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (!task.isSuccessful()) {
                                reset.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.INVISIBLE);
                                try{throw Objects.requireNonNull(task.getException());
                                }catch (FirebaseAuthInvalidUserException e){
                                    Snackbar.make(linearLayout, "Account doesn't exist", Snackbar.LENGTH_LONG).show();
                                }catch( Exception e){
                                    Log.d("Error",""+task.getException());
                                }

                            } else {
                                progressBar.setVisibility(View.INVISIBLE);
                                Snackbar.make(linearLayout, "Reset email sent!", Snackbar.LENGTH_LONG).show();
                                Log.d("EmailSuccess", "EMail sent" + task.getException());
                            }
                        }
                    });
                }
                else{
                    reset.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                    Snackbar.make(v,"Fill email field",Snackbar.LENGTH_LONG).show();

                }

    }
    });
    }
}