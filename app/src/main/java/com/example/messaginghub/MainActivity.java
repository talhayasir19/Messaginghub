package com.example.messaginghub;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityManager;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bnview;
    RecyclerView peoplerecyclerview;
    TextView currentusername;
    ImageView settings, currentuserprofile,SelectContacts;
    PeopleAdapter adapter;
    String Currentusername;
    String Currentphoneno;
    String Currentimgid;
    RecyclerView ChatsRecyclerview;
    ArrayList<PeopleMsgModel> data = new ArrayList<>();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    DocumentReference ref;
    Long Timestamp;
    Boolean dataadding = false;
    ArrayList<PeopleModel> data1 = new ArrayList<>();
    DBhelper db;
    Long unreadcount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        db=DBhelper.getInstance(MainActivity.this);
        ProfileDatabaseModel data=db.fetch_data();
        ref = firestore.collection("Users").document(Objects.requireNonNull(auth.getCurrentUser().getPhoneNumber()));
        bnview = findViewById(R.id.bnview);
        currentusername = findViewById(R.id.currentusername);
        currentuserprofile = findViewById(R.id.currentuserprofile);
        SelectContacts=findViewById(R.id.SelectContacts);
        settings = findViewById(R.id.settings);
        peoplerecyclerview = findViewById(R.id.peoplerecyclerview);
        peoplerecyclerview = findViewById(R.id.peoplerecyclerview);
        //Setting up profile
        setprofile();
        loaddata();
        //Setting up online
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myConnectionsRef = database.getReference("Users/"+db.fetch_data().getPhoneno());
        final DatabaseReference connectedRef = database.getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                myConnectionsRef.child("Connected").onDisconnect().setValue(false);
                myConnectionsRef.child("Lastseen").onDisconnect().setValue(Currenttime());
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    myConnectionsRef.child("Connected").setValue(true);
                    myConnectionsRef.child("Lastseen").removeValue();
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("Error", "Listener was cancelled at .info/connected");
            }
        });
        //settings
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                FirebaseAuth.getInstance().signOut();
//                Toast.makeText(MainActivity.this, "Signed out", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(MainActivity.this, Registration.class);
//                startActivity(intent);
                Date c = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
                String formattedDate = df.format(c);
                showProfile(MainActivity.this);



            }
        });
        //Notifations
        CollectionReference ref = firestore.collection("Chatids");
        ref.whereEqualTo("Receiver",Currentphoneno).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (DocumentSnapshot sn : value.getDocuments()) {
                    Long time= (Long) sn.get("Timestamp");
                    Long currenttime=System.currentTimeMillis()-2000;
                    int x= Objects.requireNonNull(time).compareTo(currenttime);
                    if(x>0){
                        String Name = (String) sn.get("Sendername");
                        String Message = (String) Objects.requireNonNull(sn.get("LastMessage"));
                        String ReceiverImageid = (String) sn.get("Senderimgid");
                        String Room = (String) sn.get("Room");
                        String Receiverphoneno = (String) sn.get("Sender");
                        Intent intent = new Intent(new Intent(MainActivity.this, ListenOrder.class));
                        intent.putExtra("Name", Name);
                        intent.putExtra("Message", Message);
                        intent.putExtra("Roomid", Room);
                        intent.putExtra("Receiverimgid", ReceiverImageid);
                        intent.putExtra("Receiverphoneno", Receiverphoneno);
                        if (!Objects.equals(Receiverphoneno, ChatActivity.Receiverphoneno)) {
                            {
                                startService(intent);
                            }
                        }
                   }
                }
            }
        });
        //SelectContacts
        SelectContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,NewContact.class);
                startActivity(intent);
            }
        });
        bnview.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.Chats) {
                    loadfragment(new chats());
                } else if (id == R.id.story) {
                    loadfragment(new story());
                    dataadding = true;
                } else {
                    loadfragment(new calls());
                }
                return true;
            }
        });
        bnview.setSelectedItemId(R.id.Chats);
    }

    public void loadfragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.framelayout, fragment);
        ft.commit();
    }

    private void setprofile() {
        Currentusername=db.fetch_data().getUsername();
        currentusername.setText(Currentusername);
        Currentphoneno=db.fetch_data().getPhoneno();
        Currentimgid=db.fetch_data().getImgid();
        Uri uri= Uri.parse(Currentimgid);
        Picasso.get().load(uri).into(currentuserprofile);
    }

    public void loaddata() {
        ChatsRecyclerview = findViewById(R.id.chatsrecylerview);
        CollectionReference Chatids = (CollectionReference) firestore.collection("Chatids");
        //Adding through Recents
        Chatids.orderBy("Timestamp", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                data.clear();
             //   unreadcount=0;
                for (DocumentSnapshot snapshot : value.getDocuments()) {
                    String Name, Receiverphoneno,Imageuri;
                    if (Currentphoneno.equals(snapshot.get("Sender")) || Currentphoneno.equals(snapshot.get("Receiver"))) {
                        if (Currentphoneno.equals(snapshot.get("Receiver"))){
                            Name = (String) snapshot.get("Sendername");
                            Receiverphoneno = (String) snapshot.get("Sender");
                            Imageuri = (String) snapshot.get("Senderimgid");
                        } else {
                            Name = (String) snapshot.get("Receivername");
                           Receiverphoneno = (String) snapshot.get("Receiver");
                            Imageuri = (String) snapshot.get("Receiverimgid");
                        }
                        String Room = (String) snapshot.get("Room");
                        String Lastmessagetime = (String) snapshot.get("Time");
                        unreadcount= (Long) snapshot.get(Currentusername.concat("unreads"));
                        //Recyclerview
                        data.add(new PeopleMsgModel(Imageuri, Name, Receiverphoneno, Room, Lastmessagetime,true,unreadcount));
                        ChatsRecyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        PeopleMsgAdapter adapter = new PeopleMsgAdapter(data,MainActivity.this);
                        ChatsRecyclerview.setAdapter(adapter);
                        adapter.setOnClick(new PeopleMsgAdapter.OnClick() {
                            @Override
                            public void OnClick(View view, int position) {
                                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                                intent.putExtra("Name", data.get(position).getMsg_username());
                                intent.putExtra("Senderimgid",Currentimgid);
                                intent.putExtra("Receiverimgid", data.get(position).getMsg_imgid());
                                intent.putExtra("Receiverphoneno", data.get(position).getReceiverphoneno());
                                intent.putExtra("Roomid", data.get(position).getRoomid());
                                intent.putExtra("FirstMessage", true);
                                startActivity(intent);
                            }
                        });
                        adapter.setImageOnClick(new PeopleMsgAdapter.ImageOnClick() {
                            @Override
                            public void imageOnClick(View view, int position) {
                                ImageView showImage;
                                TextView showName;
                                Dialog dialog=new Dialog(MainActivity.this);
                                dialog.setContentView(R.layout.showprofile);
                                showImage=dialog.findViewById(R.id.showImage);
                                showName=dialog.findViewById(R.id.showName);
                                Picasso.get().load(data.get(position).getMsg_imgid()).into(showImage);
                                showName.setText(data.get(position).getMsg_username());
                                dialog.show();

                            }
                        });

                    }
                }
            }


        });

    }

    public String Currenttime() {
        Calendar calendar = Calendar.getInstance();
        int hour24hrs = calendar.get(Calendar.HOUR_OF_DAY);
        int hour12hrs = calendar.get(Calendar.HOUR);
        int minutes = calendar.get(Calendar.MINUTE);
        if (hour24hrs <= 12) {
            if (minutes < 10) {
                return hour24hrs + ":0" + minutes + " AM";
            } else {
                return hour24hrs + ":" + minutes + " AM";
            }
        } else {
            if (minutes < 10) {
                return hour12hrs + ":0" + minutes + " PM";
            } else {
                return hour12hrs + ":" + minutes + " PM";
            }
        }
    }








    @Override
    protected void onStart() {
        super.onStart();
        ChatActivity.Receiverphoneno=null;
        Currentphoneno=db.fetch_data().getPhoneno();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
  public static void showProfile(Context context){


  }

}