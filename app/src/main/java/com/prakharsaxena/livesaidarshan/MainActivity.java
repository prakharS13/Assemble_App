package com.prakharsaxena.livesaidarshan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    TextView help;
    Button login,signUp;
    EditText email,password;
    ProgressBar progressBar;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth=FirebaseAuth.getInstance();

        getSupportActionBar().hide();
       final LinearLayout linearLayout=(LinearLayout) findViewById(R.id.linearLayout);
        signUp=(Button) findViewById(R.id.createAccount);
        login=(Button) findViewById(R.id.login);
        email=(EditText) findViewById(R.id.email);
        password=(EditText) findViewById(R.id.password);
        help=(TextView) findViewById(R.id.help);
        progressBar=(ProgressBar) findViewById(R.id.progressLoginAccount);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),SecondActivity.class);
                startActivity(i);
                finish();
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login.setVisibility(View.INVISIBLE);
                signUp.setVisibility(View.INVISIBLE);
                String Email,Password;
                progressBar.setVisibility(View.VISIBLE);
                Email=email.getText().toString();
                Password=password.getText().toString();
                if(!TextUtils.isEmpty(Email)&&!TextUtils.isEmpty(Password)) {
                    mAuth.signInWithEmailAndPassword(Email, Password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull final Task<AuthResult> task) {
                                    // If sign in fails, display a message to the user.
                                    if (!task.isSuccessful()) {
                                        login.setVisibility(View.VISIBLE);
                                        signUp.setVisibility(View.VISIBLE);
                                        progressBar.setVisibility(View.INVISIBLE);
                                        try {
                                            throw task.getException();
                                        } catch (FirebaseAuthInvalidCredentialsException e) {
                                            Snackbar.make(linearLayout, "No account with this email.", Snackbar.LENGTH_LONG).show();
                                        } catch (FirebaseAuthException e) {
                                            Snackbar.make(linearLayout, "Wrong email or password.", Snackbar.LENGTH_LONG).show();
                                        } catch (Exception e) {
                                            Log.w("LoginFailed", "signInWithEmail:failure", task.getException());
                                            Snackbar.make(linearLayout, "Seems problem from our end, try again later", Snackbar.LENGTH_LONG).show();
                                        }
                                    } else {
                                        progressBar.setVisibility(View.GONE);
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        if(!Objects.requireNonNull(user).isEmailVerified()){
                                            Intent i=new Intent(getApplicationContext(), WelcomeActivity.class);
                                            startActivity(i);
                                            finish();
                                        }else {
                                            // Sign in success, update UI with the signed-in user's information
                                            Log.d("LoginSuccess", "signInWithEmail:success");
                                            Snackbar.make(linearLayout, "Login Successfully", Snackbar.LENGTH_SHORT).show();
                                            Intent i = new Intent(getApplicationContext(), UserActivity.class);
                                            startActivity(i);
                                            finish();
                                        }
                                    }
                                }
                            });
                }else{
                    login.setVisibility(View.VISIBLE);
                    signUp.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                    Snackbar.make(v,"Fill all the fields to login",Snackbar.LENGTH_LONG).show();
                }
            }
        });

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(), HelpActivity.class);
                startActivity(i);

            }
        });

    }

    @Override
    protected void onStart() {
        FirebaseUser currentUser=mAuth.getCurrentUser();
        if(currentUser!=null){
            Intent i=new Intent(getApplicationContext(), UserActivity.class);
            startActivity(i);
            finish();
        }
        super.onStart();
    }

    /*
    @Override
    public void onBackPressed() {
        Toast.makeText(getApplicationContext(),"Press back again to exit",Toast.LENGTH_SHORT).show();
        MainActivity.super.onBackPressed();
        finish();

    }
    */

}
