package com.example.messaginghub;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class PeopleAdapter extends RecyclerView.Adapter<PeopleAdapter.PeopleHolder>{
    public ArrayList<PeopleModel> data;

    public PeopleAdapter(ArrayList<PeopleModel> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public PeopleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.people,null);
          return new PeopleHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PeopleHolder holder, int position) {
        PeopleModel index=data.get(position);
        holder.user_name.setText(index.getUsername());
        Picasso.get()
                .load(Uri.parse(index.getImgid()))
                .into(holder.user_image);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class PeopleHolder extends RecyclerView.ViewHolder{
        CircleImageView user_image;
        TextView user_name;
        public PeopleHolder(@NonNull View itemView) {
            super(itemView);
            user_name=itemView.findViewById(R.id.user_name);
            user_image=itemView.findViewById(R.id.user_image);
        }
    }

}
