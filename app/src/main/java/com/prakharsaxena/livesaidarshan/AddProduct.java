package com.prakharsaxena.livesaidarshan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class AddProduct extends AppCompatActivity {
    public static final int REQUEST_CAMERA=123;
    private final int code_img_gallery=1;
    private Uri uri;
    private String pathToFile;
    private FirebaseStorage storage=FirebaseStorage.getInstance();
    private StorageReference mStorageRef;
    private FirebaseDatabase database;
    FirebaseAuth firebaseAuth;
    ImageView uploadImage;
    EditText productName,productQuantity,productTag,productNote;
    Button uploadBtn,saveProduct;
    ProgressBar progressBar;
    private String user_id;
    Boolean CameraFlag=false,GalleryFlag=false;
    private StorageTask muploadTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        uploadImage=(ImageView) findViewById(R.id.uploadImage);
        uploadBtn=(Button) findViewById(R.id.uploadProductImage);
        saveProduct=(Button) findViewById(R.id.SaveProduct);
        progressBar=(ProgressBar) findViewById(R.id.progress_bar);
        productName=(EditText) findViewById(R.id.productName);
        productQuantity=(EditText) findViewById(R.id.productQuantity);
        productTag=(EditText) findViewById(R.id.productTag);
        productNote=(EditText) findViewById(R.id.productNote);

        mStorageRef = storage.getReference("uploads");
        database=FirebaseDatabase.getInstance();
        final FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        user_id=user.getUid();
        String user_email=user.getEmail();
        DatabaseReference myUser= database.getReference(user_id);

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              if(ContextCompat.checkSelfPermission(AddProduct.this, Manifest.permission.CAMERA )!= PackageManager.PERMISSION_GRANTED){
                    showAlert();
                }else {
                 selectImage();
              }
            }
        });
        saveProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              uploadProduct();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==0 && resultCode==RESULT_OK&& data!=null){
            CameraFlag=true;
            Bitmap bitmap=(Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
            uploadImage.setVisibility(View.VISIBLE);
            uploadImage.setImageBitmap(bitmap);
            uploadBtn.setVisibility(View.GONE);

        }
        else if(requestCode==1 && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            GalleryFlag=true;
            uri=data.getData();
            uploadImage.setVisibility(View.VISIBLE);
            uploadImage.setImageURI(uri);
            uploadBtn.setVisibility(View.GONE);
        }
    }
    private void showAlert() {
        AlertDialog alertDialog =new AlertDialog.Builder(AddProduct.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("App needs to access the camera");
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "DONT ALLOW", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "ALLOW", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                ActivityCompat.requestPermissions(AddProduct.this,new String[] {Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_CAMERA);
            }
        });
        alertDialog.show();
    }
    public void selectImage()
    {  final CharSequence[] items={"Camera","Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(AddProduct.this);
        builder.setTitle("Add Image");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if(items[i].equals("Camera"))
                {    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 0);
                    /*Intent intent =new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                         if(intent.resolveActivity(getPackageManager())!=null) {
                            File photoFile=null;
                            photoFile =createPhotoFile();
                            if(photoFile!=null){
                                pathToFile=photoFile.getAbsolutePath();
                                uri= FileProvider.getUriForFile(AddProduct.this,"com.prakharsaxena.livesaidarshan",photoFile);
                                // uri=Uri.fromFile(photoFile);
                                //  Log.w("myvalues", ""+uri);
                                intent.putExtra(MediaStore.EXTRA_OUTPUT,uri);
                                intent.putExtra("return-data",true);
                                startActivityForResult(intent, 123);
                            }
                        }*/
                }
                else if(items[i].equals("Gallery")){
                  startActivityForResult(new Intent().setAction(Intent.ACTION_GET_CONTENT).setType("image/*"),1);
                }
            }
        });
        AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }

    private String getFileExtension(Uri uri){
        ContentResolver cR=getContentResolver();
        MimeTypeMap mime=MimeTypeMap.getSingleton();
        return  mime.getExtensionFromMimeType(cR.getType(uri));

    }
    private void uploadProduct() {
        if (GalleryFlag == true) {
            if (uri != null) {
                final StorageReference productImage = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(uri));
                UploadTask uploadTask = productImage.putFile(uri);
                uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        saveProduct.setVisibility(View.INVISIBLE);
                        progressBar.setVisibility(View.VISIBLE);
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        progressBar.setProgress((int) progress);
                    }
                });
                uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();

                        }
                        return productImage.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setProgress(0);
                                }
                            }, 500);
                            Toast.makeText(AddProduct.this, "Upload Successful", Toast.LENGTH_SHORT).show();
                            NewProduct newProduct= new NewProduct(productName.getText().toString(),
                                    productQuantity.getText().toString(), productTag.getText().toString(), productNote.getText().toString(),downloadUri.toString());
                            DatabaseReference myProducts = database.getReference(user_id + "/Products").push();
                            myProducts.setValue(newProduct);
                            saveProduct.setVisibility(View.VISIBLE);
                            finish();
                        }
                    }
                });

            } else {
                Toast.makeText(this, "No File Selected", Toast.LENGTH_SHORT).show();
            }
        } else if (CameraFlag == true) {
            uploadImage.setDrawingCacheEnabled(true);
            uploadImage.buildDrawingCache();
            Bitmap uploadBitmap = ((BitmapDrawable) uploadImage.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            uploadBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            final StorageReference productImage = mStorageRef.child(System.currentTimeMillis() + ".jpg");
            UploadTask uploadTask = productImage.putBytes(data);
            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    saveProduct.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    progressBar.setProgress((int) progress);
                }
            });
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("UploadError", "" + e);
                }
            });
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();

                    }
                    return productImage.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setProgress(0);
                            }
                        }, 500);
                        Toast.makeText(AddProduct.this, "Upload Successful", Toast.LENGTH_SHORT).show();
                        NewProduct newProduct= new NewProduct(productName.getText().toString(),
                                productQuantity.getText().toString(), productTag.getText().toString(), productNote.getText().toString(),downloadUri.toString());

                        DatabaseReference myProducts = database.getReference(user_id + "/Products").push();
                        myProducts.setValue(newProduct);

                       /* myProducts.child("Product_Name").setValue();
                        myProducts.child("Quantity").setValue();
                        myProducts.child("Tag").setValue();
                        myProducts.child("Note").setValue();
                        myProducts.child("ImageURL").setValue(downloadUri.toString());
                        */

                        saveProduct.setVisibility(View.VISIBLE);
                        finish();
                    }
                }
            });
        } else {
            Toast.makeText(this, "No File Selected", Toast.LENGTH_SHORT).show();
        }

    }
}
 /*   private File createPhotoFile() {
        String name=new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File dir=getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image=null;
        try{
            image=File.createTempFile(name+"assemble",".jpg",dir);

        } catch (IOException e) {
            Log.d("mylog","Excep : "+ e.toString());
        }
        return image;

    }
*/
