package com.prakharsaxena.livesaidarshan;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class ItemsFragment extends Fragment implements ProductAdapter.OnItemClickListener {
    private RecyclerView mRecyclerView;
    private ProductAdapter mAdapter;
    private FirebaseStorage mStorage;
    private DatabaseReference mDatabaseRef;
    private ValueEventListener mDBListener;
    private List<NewProduct> mNewProduct;
    private String user_id;
    String pName,pQty,pTag,pNote,pURL;
    ProgressBar progressBar;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_items, container, false);
        FloatingActionButton camera=(FloatingActionButton)v.findViewById(R.id.camera);
        progressBar=v.findViewById(R.id.progress);
        mRecyclerView=v.findViewById(R.id.mRecycler_View);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mNewProduct=new ArrayList<>();

        mAdapter= new ProductAdapter(getContext(),mNewProduct);

        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(ItemsFragment.this);

        mStorage=FirebaseStorage.getInstance();

        final FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();

        user_id=user.getUid();
        mDatabaseRef=FirebaseDatabase.getInstance().getReference(user_id+"/Products");
       mDBListener =  mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mNewProduct.clear();

                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    NewProduct upload=postSnapshot.getValue(NewProduct.class);
                    upload.setmKey(postSnapshot.getKey());
                    mNewProduct.add(upload);
                }

               mAdapter.notifyDataSetChanged();

                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(),databaseError.getMessage(),Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent=new Intent(getActivity(), AddProduct.class);
               startActivity(intent);

            }
        });

        return v;
    }


    @Override
    public void onItemClick(int position) {
        //Toast.makeText(getContext(), "Normal CLick at position"+position, Toast.LENGTH_SHORT).show();
        NewProduct selectedItem=mNewProduct.get(position);
        final String selectedKey=selectedItem.getmKey();
        Intent intent=new Intent(getContext(), ViewProduct.class);
        intent.putExtra("product_id",selectedKey);
        startActivity(intent);
    }

    @Override
    public void onEditClick(int position) {
       Toast.makeText(getContext(),"Edit Button",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onShareClick(int position) {

        NewProduct selectedItem=mNewProduct.get(position);
        final String selectedKey=selectedItem.getmKey();

        mDatabaseRef.child(selectedKey).addValueEventListener(new ValueEventListener() {
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
                    Toast.makeText(getContext(),""+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    public void onDeleteClick(int position) {
       NewProduct selectedItem=mNewProduct.get(position);
       final String selectedKey=selectedItem.getmKey();
       final StorageReference imageRef=mStorage.getReferenceFromUrl(selectedItem.getProductUrl());
       AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
       builder.setTitle("Delete")
               .setMessage(selectedItem.getProductName()+" will be permanently deleted!")
               .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                           @Override
                           public void onSuccess(Void aVoid) {
                               mDatabaseRef.child(selectedKey).removeValue();
                               Toast.makeText(getContext(), "Item Deleted! ", Toast.LENGTH_SHORT).show();
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

    public void showPopup(View v){
        PopupMenu popupMenu=new PopupMenu(getContext(),v);
        MenuInflater inflater=popupMenu.getMenuInflater();
        inflater.inflate(R.menu.popup_menu, popupMenu.getMenu());
        popupMenu.show();
    }




    @Override
    public void onDestroy() {
        super.onDestroy();
        mDatabaseRef.removeEventListener(mDBListener);
    }
}
