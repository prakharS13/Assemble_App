package com.prakharsaxena.livesaidarshan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class ViewProduct extends AppCompatActivity {
    ImageView imageView;
    EditText itemName, itemQty, itemTag, itemNote;
    private FirebaseStorage mStorage;
    private DatabaseReference mDatabaseRef;
    String user_id, product_id, pName, pQty, pTag, pURL, pNote;
    Button editBtn;
    RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_product);
        //getting product id key from intent
        Bundle product_key=getIntent().getExtras();
        assert product_key != null;
        product_id=product_key.getString("product_id");


        //setter of views
        imageView=(ImageView) findViewById(R.id.viewProductIV);
        itemName=(EditText) findViewById(R.id.viewProductName);
        itemQty=(EditText) findViewById(R.id.viewProductQuantity);
        itemTag=(EditText) findViewById(R.id.viewProductTag);
        itemNote=(EditText) findViewById(R.id.viewProductNote);
        editBtn=(Button) findViewById(R.id.saveEditProduct);
        relativeLayout=(RelativeLayout) findViewById(R.id.viewProductRelativeLayout);


        //Setting all views with data
        mStorage=FirebaseStorage.getInstance();

        final FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();

        user_id=user.getUid();
        mDatabaseRef= FirebaseDatabase.getInstance().getReference(user_id+"/Products").child(product_id);


       mDatabaseRef.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                   NewProduct read= dataSnapshot.getValue(NewProduct.class);
                   Picasso.get().load(read.getProductUrl()).placeholder(R.mipmap.ic_launcher).into(imageView);
                   pURL=read.getProductUrl();
                   itemName.setText(read.getProductName());
                   itemQty.setText(read.getProductQuantity());
                   itemTag.setText(read.getProductTag());
                   itemNote.setText(read.getProductNote());


                   itemName.setEnabled(false);


                   itemQty.setEnabled(false);


                   itemTag.setEnabled(false);


                   itemNote.setEnabled(false);

           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

               Toast.makeText(ViewProduct.this,"Product no longer exist!",Toast.LENGTH_SHORT).show();
           }
       });

       //Edit Button Onclick listener

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewProduct newProduct= new NewProduct(itemName.getText().toString(),
                        itemQty.getText().toString(), itemTag.getText().toString(), itemNote.getText().toString(), pURL);
                mDatabaseRef.setValue(newProduct);
                Snackbar.make(relativeLayout,"Editted Successfully",Snackbar.LENGTH_SHORT).show();
                editBtn.setEnabled(false);
                editBtn.setBackgroundColor(Color.GRAY);
            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu_bar, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_edit:

                itemName.setEnabled(true);


                itemQty.setEnabled(true);


                itemTag.setEnabled(true);


                itemNote.setEnabled(true);

                editBtn.setEnabled(true);
                editBtn.setBackgroundResource(R.drawable.button_design);
                Snackbar.make(relativeLayout,"Edit mode ON",Snackbar.LENGTH_SHORT).show();

                return true;


            case R.id.menu_share:
                mDatabaseRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        NewProduct read= dataSnapshot.getValue(NewProduct.class);
                        pName=read.getProductName();
                        pQty=read.getProductQuantity();
                        pTag=read.getProductTag();
                        pNote=read.getProductNote();
                        pURL=read.getProductUrl();
                        Intent shareIntent=new Intent();
                        shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE)
                                .setType("text/plain")
                                .putExtra(Intent.EXTRA_TEXT, "Hey check my this product from Assemble App.\nImage URL : "+pURL+"\n\nProduct Name: "+pName+"\nQuantity: "+pQty+"\nTags: "+pTag+"\nNotes: "+pNote+"\n\n\nNow managing and arranging things become easier with Assemble App\nDownload now from here: https://jsi.dx.am/download");
                        try{
                            startActivity(shareIntent);
                        }
                        catch(Exception e){
                            Toast.makeText(ViewProduct.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                return true;

            case R.id.menu_delete:
                mDatabaseRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        NewProduct read= dataSnapshot.getValue(NewProduct.class);
                        final StorageReference imageRef=mStorage.getReferenceFromUrl(read.getProductUrl());
                        AlertDialog.Builder builder=new AlertDialog.Builder(ViewProduct.this);
                        builder.setTitle("Delete")
                                .setMessage(read.getProductName()+" will be permanently deleted!")
                                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                mDatabaseRef.removeValue();
                                                Toast.makeText(ViewProduct.this, "Item Deleted! ", Toast.LENGTH_SHORT).show();
                                                finish();
                                            }
                                        });
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                })
                                .setCancelable(false);
                        AlertDialog alertDialog=builder.create();
                        alertDialog.show();
                        Button button=alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                        button.setBackgroundResource(R.drawable.button_design);
                        button.setPadding(20,0,20,0);
                        button.setTextColor(Color.WHITE);
                        Button buttonNegative=alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                        buttonNegative.setTextColor(Color.BLACK);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}