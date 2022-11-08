package com.example.messaginghub;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfile extends AppCompatActivity {
    AppCompatButton verifybtn;
    EditText nameprofile;
    FirebaseFirestore firestore;
    CircleImageView profile_image;
    int RESULT_LOAD_IMG=1;
    ProgressDialog progressDialog;
    FirebaseUser user;
    Uri imageuri;
    String Name;
    int Phoneno;
    DatabaseReference Realtimeref=FirebaseDatabase.getInstance().getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        verifybtn=findViewById(R.id.verifybtn);
        nameprofile=findViewById(R.id.nameprfile);
        profile_image=findViewById(R.id.profile_image);
        firestore=FirebaseFirestore.getInstance();
        user=FirebaseAuth.getInstance().getCurrentUser();
        getSupportActionBar().hide();


        verifybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                if(nameprofile.getText().toString().length()>0){
                    Name=nameprofile.getText().toString();
                    setupprofile();
                  progressDialog=new ProgressDialog(EditProfile.this);
                    progressDialog.setMessage("Setting up profile...");
                    progressDialog.show();


                }
                else {
                    Toast.makeText(EditProfile.this, "Please enter your name", Toast.LENGTH_SHORT).show();

                }
            }
        });
        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RESULT_LOAD_IMG){
          Uri imageuri=data.getData();
            final InputStream imageStream;
            try {
                imageStream=getContentResolver().openInputStream(imageuri);
                Bitmap image= BitmapFactory.decodeStream(imageStream);
                profile_image.setImageBitmap(image);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
    }
    public void setupprofile(){
        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference storageRef = storage.getReference();

        StorageReference mountainsRef = storageRef.child(user.getPhoneNumber()+".jpg");

        StorageReference mountainImagesRef = storageRef.child("images/mountains.jpg");
        profile_image.setDrawingCacheEnabled(true);
        profile_image.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable)profile_image.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //compress
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            float bitmapRatio = (float)width / (float) height;
            if (bitmapRatio > 1) {
                width = 1000;
                height = (int) (width / bitmapRatio);
            } else {
                height = 1000;
                width = (int) (height * bitmapRatio);
            }
            bitmap=Bitmap.createScaledBitmap(bitmap, width, height, true);


        bitmap.compress(Bitmap.CompressFormat.JPEG, 10, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mountainsRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(EditProfile.this, "Failed "+exception, Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                generatinglink();

            }
        });

    }
    void generatinglink(){
        //Storing data in local database
        DBhelper db=DBhelper.getInstance(EditProfile.this);
        FirebaseStorage storage=FirebaseStorage.getInstance();
        StorageReference ref=storage.getReference();
        ref.child(user.getPhoneNumber()+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                db.add_data(new ProfileDatabaseModel(Name,Registration.Phoneno,uri.toString()));
                 HashMap<String,Object> data=new HashMap<>();
                data.put("Name",Name);
                data.put("Imageuri",uri);
                data.put("Phoneno",user.getPhoneNumber());
                firestore.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().toString()).set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(EditProfile.this, "Profile created successfully", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            Intent intent=new Intent(EditProfile.this,MainActivity.class);
                            intent.putExtra("username",Name);
                            startActivity(intent);

                        }
                        else{
                            Toast.makeText(EditProfile.this, "error", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                });
            }
        });
    }
    @Override
    public void onBackPressed() {
        backdialog();
    }
    void backdialog(){
        AlertDialog.Builder AD=new AlertDialog.Builder(this)
                .setTitle("Choose the following action")
                .setMessage("Do you want to cancel account creation")
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        AD.show();
    }

}
