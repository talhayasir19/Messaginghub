package com.example.messaginghub;

import static com.example.messaginghub.R.drawable.selectbackground;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class MessagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<Messagesdata> data;
    String currentuser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getPhoneNumber();
    Onitemclick itemclick;
    Context context;

    public void setItemclick(Onitemclick itemclick) {
        this.itemclick = itemclick;
    }


    public MessagesAdapter(ArrayList<Messagesdata> data) {
        this.data = data;
    }

    @Override
    public int getItemViewType(int position) {
        Messagesdata index = data.get(position);
        if (Objects.equals(currentuser, index.getSenderid())) {
            return 0;
        } else {
            return 1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == 0) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.send, null);
            return new SendMessagesHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.receive, null);
            return new ReceiveMessagesHolder(view);
        }

    }

    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Messagesdata index = data.get(position);

            if (holder instanceof SendMessagesHolder) {
                ((SendMessagesHolder) holder).Timegroup.setVisibility(View.GONE);
                ((SendMessagesHolder) holder).send.setText(index.getMessage());
                ((SendMessagesHolder) holder).Timestamp.setText(index.getTimestamp());

                if(index.getIsseen()){
                    ((SendMessagesHolder) holder).Isseen.setImageResource(R.drawable.seen);
                }
                else {
                    ((SendMessagesHolder) holder).Isseen.setImageResource(R.drawable.sent);
                }
                if (position == 0) {
                    if (index.getDate().equals(Currentdate())) {
                        ((SendMessagesHolder) holder).Timegroup.setText("Today");
                    } else {
                        ((SendMessagesHolder) holder).Timegroup.setText(index.getDate());
                    }
                    ((SendMessagesHolder) holder).Timegroup.setVisibility(View.VISIBLE);
                } else {
                    if (!index.getDate().equals(data.get(position - 1).getDate())) {
                        if (index.getDate().equals(Currentdate())) {
                            ((SendMessagesHolder) holder).Timegroup.setText("Today");
                        } else {
                            ((SendMessagesHolder) holder).Timegroup.setText(index.getDate());
                        }
                        ((SendMessagesHolder) holder).Timegroup.setVisibility(View.VISIBLE);

                    }
                }
            } else if (holder instanceof ReceiveMessagesHolder) {
                ((ReceiveMessagesHolder) holder).Timegroup.setVisibility(View.GONE);
                ((ReceiveMessagesHolder) holder).receive.setText(index.getMessage());
                ((ReceiveMessagesHolder) holder).Timestamp.setText(index.getTimestamp());

                if (position == 0) {
                    if (index.getDate().equals(Currentdate())) {
                        ((ReceiveMessagesHolder) holder).Timegroup.setText("Today");
                    } else {
                        ((ReceiveMessagesHolder) holder).Timegroup.setText(index.getDate());
                    }
                    ((ReceiveMessagesHolder) holder).Timegroup.setVisibility(View.VISIBLE);
                } else {
                    if (!index.getDate().equals(data.get(position - 1).getDate())) {
                        if (index.getDate().equals(Currentdate())) {
                            ((ReceiveMessagesHolder) holder).Timegroup.setText("Today");
                        } else {
                            ((ReceiveMessagesHolder) holder).Timegroup.setText(index.getDate());
                        }
                        ((ReceiveMessagesHolder) holder).Timegroup.setVisibility(View.VISIBLE);

                    }
                }

        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v){
                int count=0;
                for(int i=0; i<data.size(); i++){
                    if(data.get(i).getIsselected()){
                        count++;
                    }
                }
                if (count>0) {
                    itemclick.onitemclick(v,holder.getAdapterPosition());
                }
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @SuppressLint({"ResourceAsColor", "UseCompatLoadingForDrawables"})
            @Override
            public boolean onLongClick(View v) {
                int count=0;
                for(int i=0; i<data.size(); i++){
                    if(data.get(i).getIsselected()){
                        count++;
                    }
                }
                if (count==0) {
                    itemclick.onitemclick(holder.itemView,holder.getAdapterPosition());
                }
                return true;

            }
        });

    }
    @Override
    public int getItemCount() {
        return data.size();
    }

    class SendMessagesHolder extends RecyclerView.ViewHolder {
        TextView send, Timestamp, Timegroup;
        LinearLayout sendmessagelayout;
        ImageView Isseen;

        public SendMessagesHolder(@NonNull View itemView) {
            super(itemView);
            send = itemView.findViewById(R.id.send);
            Timestamp = itemView.findViewById(R.id.timestamp);
            sendmessagelayout = itemView.findViewById(R.id.sendmessagelayout);
            Timegroup = itemView.findViewById(R.id.Timegroup);
            Isseen=itemView.findViewById(R.id.Isseen);

        }
    }

    class ReceiveMessagesHolder extends RecyclerView.ViewHolder {
        LinearLayout receivemessagelayout;
        TextView receive, Timestamp, Timegroup;

        public ReceiveMessagesHolder(@NonNull View itemView) {
            super(itemView);
            receive = itemView.findViewById(R.id.Receive);
            Timestamp = itemView.findViewById(R.id.timestamp);
            receivemessagelayout = itemView.findViewById(R.id.receivemessagelayout);
            Timegroup = itemView.findViewById(R.id.Timegroup);
        }
    }

    interface Onitemclick {
        public void onitemclick(View view, int position);
    }



    public String Currentdate(){
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        return formattedDate;
    }
}
