package com.example.messaginghub;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity {
    EditText message;
    ImageView sent_button,chat_image,backbutton,btn_chat_settings,Sl_backbutton,Sl_copy,Sl_delete,Sl_forward;
    TextView chat_username,user_status,Viewcontact,Clearchat,Sl_msgcount;
    LinearLayout Chatactivitylayout,Messagerylayout;
    View toolbar,selecttoolbar;
    public static String Receiverphoneno;
    String Myname,Name,Senderimgid,Receiverimgid,Senderphoneno,Roomid;
    int Senderid,Receiverid,backcolor;
    FirebaseAuth auth=FirebaseAuth.getInstance();
    FirebaseUser user=auth.getCurrentUser();
    FirebaseFirestore firestore=FirebaseFirestore.getInstance();
    FirebaseDatabase Realtimeref=FirebaseDatabase.getInstance();
    RecyclerView MessageRecyclerView;
    ArrayList<Messagesdata> data=new ArrayList<>();
    MessagesAdapter adapter;
    Boolean FromMain;
    DBhelper db;
    View view;
    Boolean Delete=false;
    int unreadcount;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getSupportActionBar().hide();
        //Finding id's
        message=findViewById(R.id.message);
        sent_button=findViewById(R.id.sent_button);
        chat_username=findViewById(R.id.chat_username);
        chat_image=findViewById(R.id.chat_image);
        toolbar=findViewById(R.id.toolbar);
        selecttoolbar=findViewById(R.id.selecttoolbar);
        user_status=findViewById(R.id.user_status);
        MessageRecyclerView=findViewById(R.id.MessageRecyclerView);
        backbutton=findViewById(R.id.backbutton);
        btn_chat_settings=findViewById(R.id.chat_settings);
        Chatactivitylayout=findViewById(R.id.Chatactivitylayout);
        Messagerylayout=findViewById(R.id.MessageRecyclerViewlayout);
        Viewcontact=findViewById(R.id.Viewcontact);
        Clearchat=findViewById(R.id.Clearchat);
        view=findViewById(R.id.line);
        Sl_backbutton=findViewById(R.id.Sl_backbutton);
        Sl_msgcount=findViewById(R.id.Sl_msgcount);
        Sl_delete=findViewById(R.id.Sl_delete);
        Sl_forward=findViewById(R.id.Sl_forward);
        Sl_copy=findViewById(R.id.Sl_copy);
        //Getting data
        db=DBhelper.getInstance(ChatActivity.this);
        Intent intent=getIntent();
        Myname=db.fetch_data().getUsername();
        Name=intent.getStringExtra("Name");
        Senderphoneno=db.fetch_data().getPhoneno();
        Roomid=getIntent().getStringExtra("Roomid");
        Senderimgid=intent.getStringExtra("Senderimgid");
        Receiverimgid=intent.getStringExtra("Receiverimgid");
        Receiverphoneno=intent.getStringExtra("Receiverphoneno");
        FromMain=getIntent().getBooleanExtra("FirstMessage",false);
        CollectionReference ref=firestore.collection("Chatrooms").document(Roomid)
                .collection("Messages");
        //Setting profile
        final DatabaseReference myConnectionsRef = Realtimeref.getReference("Users");
       myConnectionsRef.child(Receiverphoneno).addValueEventListener(new ValueEventListener() {
           @SuppressLint("SetTextI18n")
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
              if(Boolean.TRUE.equals(snapshot.child("Connected").getValue(Boolean.class))){
                  user_status.setText("Online");
              }
              else {
                  String Lastseen = snapshot.child("Lastseen").getValue(String.class);
                  if (Lastseen!=null) {
                      user_status.setText("Lastseen at " + Lastseen);
                  }
              }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });
        chat_username.setText(Name);
        Picasso.get()
                        .load(Receiverimgid)
                .into(chat_image);
        //Setting up buttons
        btn_chat_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Chatactivitylayout.getVisibility()==View.GONE){
                    Chatactivitylayout.setVisibility(View.VISIBLE);
                }
                else{
                    Chatactivitylayout.setVisibility(View.GONE);
                }
            }
        });
        Viewcontact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView showImage;
                TextView showName;
                Dialog dialog=new Dialog(ChatActivity.this);
                dialog.setContentView(R.layout.showprofile);
                showImage=dialog.findViewById(R.id.showImage);
                showName=dialog.findViewById(R.id.showName);
                Picasso.get().load(Receiverimgid).into(showImage);
                showName.setText(Name);
                dialog.show();
                Chatactivitylayout.setVisibility(View.GONE);
            }
        });
        Clearchat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog AD=new AlertDialog.Builder(ChatActivity.this)
                        .setTitle("Do you want to delete this chat?")
                                .setMessage("The messages will be only deleted from this device.")
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Delete=true;
                                                ref.addSnapshotListener(new EventListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                                        if (Delete) {
                                                            for (DocumentSnapshot sn : value.getDocuments()) {
                                                                Map<String, Object> update = new HashMap<>();
                                                                update.put("Deletedby" + Myname, true);
                                                                ref.document(sn.getId()).update(update);
                                                            }
                                                        }
                                                        Delete=false;
                                                    }
                                                });
                                                Clearchat.setVisibility(View.GONE);
                                                   }
                                        })
                                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                    }
                                                }).create();
                AD.show();
            }
        });
        Messagerylayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Chatactivitylayout.setVisibility(View.GONE);
            }
        });
        //Setting unseen messages to zero
        CollectionReference Chatids = (CollectionReference) firestore.collection("Chatids");
        HashMap up=new HashMap();
        up.put(Myname+"unreads",0);
        Chatids.document(Roomid).update(up);
        //Recyclerview
        ref.orderBy("Timestamp").addSnapshotListener(new EventListener<QuerySnapshot>() {
             @SuppressLint("NotifyDataSetChanged")
             @Override
             public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                 data.clear();
                 unreadcount=1;
                 for(DocumentSnapshot dc: Objects.requireNonNull(value).getDocuments()){
                     String Message= (String) dc.get("Message");
                     String Senderid= (String) dc.get("Senderid");
                     String Timestamp= (String) dc.get("Time");
                     String Date= (String) dc.get("Date");
                     Boolean Del1= (Boolean) dc.get("Deletedby"+Myname);
                     Boolean Isseen= (Boolean) dc.get("Isseen");
                     if(!Isseen&&Senderid.equals(Senderphoneno)){
                         unreadcount++;
                     }
                     if(!Del1){
                         data.add(new Messagesdata(Message,Senderid,Timestamp,Date,Isseen,false));
                     }

                     if(!Objects.equals(Senderid, Senderphoneno) && Boolean.FALSE.equals(Isseen)&&getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)){
                         Map<String,Object> update=new HashMap<>();
                         update.put("Isseen",true);
                         ref.document(dc.getId()).update(update).addOnSuccessListener(new OnSuccessListener<Void>() {
                             @Override
                             public void onSuccess(Void unused) {

                             }
                         });
                     }
                 }

                 adapter.notifyDataSetChanged();
                 MessageRecyclerView.setItemViewCacheSize(data.size());
                 MessageRecyclerView.scrollToPosition(data.size()-1);
                if(data.size()==0){
                    Clearchat.setVisibility(View.GONE);
                    view.setVisibility(View.GONE);
                }
             }
         });
            adapter = new MessagesAdapter(data);
            LinearLayoutManager manager = new LinearLayoutManager(ChatActivity.this);
            MessageRecyclerView.setLayoutManager(manager);
            MessageRecyclerView.setAdapter(adapter);
        MessageRecyclerView.addOnLayoutChangeListener((view, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            if (bottom < oldBottom) {
                MessageRecyclerView.scrollBy(0, oldBottom - bottom);
            }
            adapter.setItemclick(new MessagesAdapter.Onitemclick() {
                @Override
                public void onitemclick(View view, int position) {
                    Messagesdata index=data.get(position);
                    if (!index.getIsselected()) {
                        view.setBackgroundColor(getResources().getColor(R.color.transblack));
                        data.get(position).setIsselected(true);
                        if(selecttoolbar.getVisibility()==View.GONE){
                            selecttoolbar.setVisibility(View.VISIBLE);
                            toolbar.setVisibility(View.GONE);
                        }
                    } else {
                        int count=0;
                        for(int i=0; i<data.size(); i++){
                            if(data.get(i).getIsselected()){
                                count++;
                            }
                        }
                        view.setBackgroundColor(getResources().getColor(R.color.transparent));
                        data.get(position).setIsselected(false);
                        if(count==1){
                            selecttoolbar.setVisibility(View.GONE);
                            toolbar.setVisibility(View.VISIBLE);
                        }

                    }
                     }
            });
        });
        //Backbutton and Sentbutton
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatActivity.super.onBackPressed();
            }
        });




        sent_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Message = message.getText().toString();
                if (Message.length() > 0) {
                    //Adding last message
                    Map<String, Object> lastmessage = new HashMap<>();
                    lastmessage.put("Timestamp", System.currentTimeMillis());
                    lastmessage.put("Time",Currenttime());
                    lastmessage.put("Date",Currentdate());
                    lastmessage.put("LastMessage", Message);
                    lastmessage.put("Room", Roomid);
                    lastmessage.put("Sender", Senderphoneno);
                    lastmessage.put("Receiver", Receiverphoneno);
                    lastmessage.put("Sendername", Myname);
                    lastmessage.put("Isseen",false);
                    lastmessage.put("Receivername", Name);
                    lastmessage.put("Senderimgid", Senderimgid);
                    lastmessage.put("Receiverimgid", Receiverimgid);
                    lastmessage.put(Name+"unreads", unreadcount);
                    lastmessage.put(Myname+"unreads", 0);
                    firestore.collection("Chatids").document(Roomid).set(lastmessage);
                    //Recents in Mainactivity
                    firestore.collection("Users").document(Senderphoneno).collection("LastMessage").document("Message");
                    //Sending Message
                    Map<String, Object> data = new HashMap<>();
                    data.put("Timestamp",System.currentTimeMillis());
                    data.put("Time",Currenttime());
                    data.put("Date",Currentdate());
                    data.put("Message", Message);
                    data.put("Isseen",false);
                    data.put("Deletedby"+Name,false);
                    data.put("Deletedby"+Myname,false);
                    data.put("Senderid", Senderphoneno);
                    Intent intent1=new Intent(ChatActivity.this,MainActivity.class);

                    //sending message
                    firestore.collection("Chatrooms").document(Roomid).collection("Messages").add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {

                        }
                    });
                    message.setText("");
                    MessageRecyclerView.smoothScrollToPosition(Objects.requireNonNull(MessageRecyclerView.getAdapter()).getItemCount());

                }
            }

        });
        //Setting Select toolbar buttons
        Sl_backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ChatActivity.this, "hlo", Toast.LENGTH_SHORT).show();
                selecttoolbar.setVisibility(View.GONE);
                toolbar.setVisibility(View.VISIBLE);
                for(int i=0; i<data.size(); i++) {
                    data.get(i).setIsselected(false);
                    MessageRecyclerView.findViewHolderForAdapterPosition(i).itemView.setBackgroundColor(getResources().getColor(R.color.transparent));
                }
            }
        });
        Sl_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ChatActivity.this, "Hlo", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onBackPressed(){
        //Checking if items selected or not
        int count=0;
        for(int i=0; i<data.size(); i++){
            if(data.get(i).getIsselected()){
                count++;
            }
        }
        if(FromMain&&count==0) {
            super.onBackPressed();
        }
        else if(count>0){
            selecttoolbar.setVisibility(View.GONE);
            toolbar.setVisibility(View.VISIBLE);
            for(int i=1; i<data.size()-1; i++) {
                data.get(i).setIsselected(false);
                MessageRecyclerView.findViewHolderForItemId(i).itemView.setBackgroundColor(getResources().getColor(R.color.transparent));
                MessageRecyclerView.findViewHolderForAdapterPosition(i).itemView.setBackgroundColor(getResources().getColor(R.color.transparent));
            }
        }
        else{
            startActivity(new Intent(ChatActivity.this, MainActivity.class));
        }

    }


    @Override
    protected void onPause() {
        super.onPause();
        ChatActivity.Receiverphoneno=null;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Receiverphoneno=getIntent().getStringExtra("Receiverphoneno");

    }

    public String Currentdate(){
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        return formattedDate;
    }
    public String Currenttime(){
        Calendar calendar = Calendar.getInstance();
        int hour24hrs = calendar.get(Calendar.HOUR_OF_DAY);
        int hour12hrs = calendar.get(Calendar.HOUR);
        int minutes = calendar.get(Calendar.MINUTE);
        if(hour24hrs<=12){
            if(minutes<10){
                return hour24hrs + ":0" + minutes + " AM";
            }
            else {
                return hour24hrs + ":" + minutes + " AM";
            }
        }
        else{
            if(minutes<10){
                return hour12hrs + ":0" + minutes + " PM";
            }
            else {
                return hour12hrs + ":" + minutes + " PM";
            }
        }
    }
}