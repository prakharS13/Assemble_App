package com.prakharsaxena.livesaidarshan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class SecondActivity extends AppCompatActivity {
    TextView signIn;
    EditText email,password,username,confirmPassword;
    Button createNewAccount;
    ProgressBar progressBar;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        mAuth=FirebaseAuth.getInstance();

        getSupportActionBar().hide();
        //Declaring variables
        final LinearLayout linearLayout=(LinearLayout) findViewById(R.id.linearLayout);
        signIn=(TextView) findViewById(R.id.signIn);
        email=(EditText) findViewById(R.id.email);
        password=(EditText) findViewById(R.id.password);
        confirmPassword=(EditText) findViewById(R.id.confirmPassword);
        username=(EditText) findViewById(R.id.username);
        createNewAccount=(Button) findViewById(R.id.createAccount);
        progressBar=(ProgressBar) findViewById(R.id.progressUserAccount);

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(i);
                finish();
            }
        });
        createNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewAccount.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                String Email,Password,confirmPass;
                Email=email.getText().toString();
                Password=password.getText().toString();
                confirmPass=confirmPassword.getText().toString();
                if(!TextUtils.isEmpty(Email)&&!TextUtils.isEmpty(Password)) {
                    if(!Password.equals(confirmPass)){
                        progressBar.setVisibility(View.INVISIBLE);
                        createNewAccount.setVisibility(View.VISIBLE);
                        Toast.makeText(getApplicationContext(), "Password & Confirm password doesn't matches.",
                                Toast.LENGTH_LONG).show();
                    }
                    mAuth.createUserWithEmailAndPassword(Email, Password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(!task.isSuccessful()) {
                                        // If sign in fails, display a message to the user.
                                        createNewAccount.setVisibility(View.VISIBLE);
                                        progressBar.setVisibility(View.INVISIBLE);
                                        try{
                                            throw Objects.requireNonNull(task.getException());
                                        }catch(FirebaseAuthUserCollisionException e){
                                            Snackbar.make(linearLayout,"Email already in use.", Snackbar.LENGTH_LONG).show();
                                        }catch(FirebaseAuthWeakPasswordException e){
                                            Snackbar.make(linearLayout,"Password should be more than 6 characters.", Snackbar.LENGTH_LONG).show();
                                        }
                                        catch (Exception e) {
                                            Log.w("loginFailed", "createUserWithEmail:failure", task.getException());
                                        }

                                        // updateUI(user);
                                    }
                                    else{
                                        progressBar.setVisibility(View.GONE);
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d("isSuccessful", "createUserWithEmail:success");
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        Intent i=new Intent(getApplicationContext(), WelcomeActivity.class);
                                        startActivity(i);
                                        finish();
                                    }

                                }
                            });
                }else{
                    progressBar.setVisibility(View.INVISIBLE);
                    createNewAccount.setVisibility(View.VISIBLE);
                    Snackbar.make(v,"Fill up all fields",Snackbar.LENGTH_LONG).show();
                }
            }
        });



    }


}
