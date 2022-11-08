package com.example.messaginghub;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

public class NewContact extends AppCompatActivity {
    RecyclerView MessageRecyclerView;
    FirebaseFirestore firestore=FirebaseFirestore.getInstance();
    ArrayList<PeopleMsgModel> Arraydata=new ArrayList<>();
    String Roomid;
    DBhelper db=DBhelper.getInstance(NewContact.this);
    String Currentusername,Currentphoneno,Senderimgid,Receiverimgid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_contact);
        RecyclerView MessageRecyclerView=findViewById(R.id.MessageRecyclerView);
        //Getting data
        Currentusername=db.fetch_data().getUsername();
        Currentphoneno=db.fetch_data().getPhoneno();
        Senderimgid=db.fetch_data().getImgid();
        firestore.collection("Users").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                Arraydata.clear();
                 for(DocumentSnapshot data:value.getDocuments()){
                     String Name= (String) data.get("Name");
                     Receiverimgid= (String) data.get("Imageuri");
                     String Receiverphoneno= (String) data.get("Phoneno");
                     int Senderid= Integer.parseInt(Currentphoneno.substring(7,12));
                     int Receiverid= Integer.parseInt(Objects.requireNonNull(Receiverphoneno).substring(7,12));
                     Roomid=String.valueOf(Senderid*Receiverid);
                     if(!Receiverphoneno.equals(Currentphoneno)) {
                         Arraydata.add(new PeopleMsgModel(Name,Receiverimgid, Receiverphoneno, Roomid, false));
                     }
                 }
                PeopleMsgAdapter adapter=new PeopleMsgAdapter(Arraydata,NewContact.this);
                 MessageRecyclerView.setLayoutManager(new LinearLayoutManager(NewContact.this));
                MessageRecyclerView.setAdapter(adapter);
                adapter.setOnClick(new PeopleMsgAdapter.OnClick() {
                    @Override
                    public void OnClick(View view, int position) {
                        Intent intent = new Intent(NewContact.this, ChatActivity.class);
                        //Setting data for intent
                        PeopleMsgModel index=Arraydata.get(position);
                        intent.putExtra("Name",index.getMsg_username());
                        intent.putExtra("Senderimgid",Senderimgid);
                        intent.putExtra("Receiverimgid",index.getMsg_imgid());
                        intent.putExtra("Receiverphoneno",index.getReceiverphoneno());
                        intent.putExtra("Roomid",index.getRoomid());
                        intent.putExtra("FirstMessage", false);
                        startActivity(intent);
                    }
                });
                adapter.setImageOnClick(new PeopleMsgAdapter.ImageOnClick() {
                    @Override
                    public void imageOnClick(View view, int position) {
                        ImageView showImage;
                        TextView showName;
                        Dialog dialog=new Dialog(NewContact.this);
                        dialog.setContentView(R.layout.showprofile);
                        showImage=dialog.findViewById(R.id.showImage);
                        showName=dialog.findViewById(R.id.showName);
                        Picasso.get().load(Arraydata.get(position).getMsg_imgid()).into(showImage);
                        showName.setText(Arraydata.get(position).getMsg_username());
                        dialog.show();

                    }
                });

            }



        });


    }
}