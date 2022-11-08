package com.example.messaginghub;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PeopleMsgAdapter extends RecyclerView.Adapter<PeopleMsgAdapter.MsgHolder>{

    ArrayList<PeopleMsgModel> data=new ArrayList<>();
    OnClick onclick;
    ImageOnClick imageOnClick;
    Context context;
    CollectionReference ref= FirebaseFirestore.getInstance().collection("Chatids");
    int unreadmessages;
    public void setOnClick(OnClick onClick) {
        this.onclick = onClick;
    }
    public void setImageOnClick(ImageOnClick imageOnClick) {
        this.imageOnClick = imageOnClick;
    }

    public PeopleMsgAdapter(ArrayList<PeopleMsgModel> data, Context context) {
        this.data = data;
        this.context=context;
    }

    @NonNull
    @Override
    public MsgHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.peoplemessages,null);
        return new MsgHolder(view);

    }
    @Override
    public void onBindViewHolder(@NonNull MsgHolder holder, int position){
         PeopleMsgModel index=data.get(position);
        Picasso.get()
                        .load(Uri.parse(index.getMsg_imgid()))
                                .into(holder.msg_image);
         holder.msg_username.setText(index.getMsg_username());
         //Setting last message and time
        if(index.getFrommain()){
            DocumentReference Chatroomsref= FirebaseFirestore.getInstance().collection("Chatrooms").document(index.getRoomid());
            ref.document(index.getRoomid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @SuppressLint("ResourceAsColor")
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (value.exists()) {
                        holder.user_msg.setText(value.get("LastMessage").toString());
                        String time = (String) value.get("Time");
                        holder.msg_time.setText(time);
                        if(index.getUnreadcount().compareTo(0L)==1){
                            holder.unread_count.setVisibility(View.VISIBLE);
                            holder.unread_count.setText(String.valueOf(index.getUnreadcount()));
                            holder.msg_time.setTextColor(ContextCompat.getColor(context, R.color.lightgreen));
                        }
                        else{
                            holder.unread_count.setVisibility(View.INVISIBLE);
                          //  holder.msg_time.setTextColor(R.color.textgrey);
                        }
                    }

                }
            });


        }




    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MsgHolder extends RecyclerView.ViewHolder{
        ImageView msg_image;
        TextView msg_username,user_msg,msg_time,unread_count;
        public MsgHolder(@NonNull View itemView) {
            super(itemView);
            msg_image=itemView.findViewById(R.id.msg_image);
            msg_username=itemView.findViewById(R.id.msg_username);
            user_msg=itemView.findViewById(R.id.user_msg);
            msg_time=itemView.findViewById(R.id.msg_time);
            unread_count=itemView.findViewById(R.id.unread_count);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onclick.OnClick(view,getAdapterPosition());
                }
            });
            msg_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imageOnClick.imageOnClick(v,getAdapterPosition());
                }
            });


        }
    }
    interface OnClick{
        public void OnClick(View view,int position);
    }
    interface ImageOnClick{
        public void imageOnClick(View view,int position);
    }


}
