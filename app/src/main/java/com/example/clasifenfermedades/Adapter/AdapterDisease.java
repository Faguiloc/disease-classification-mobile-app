package com.example.clasifenfermedades.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clasifenfermedades.Classes.Diseases;
import com.example.clasifenfermedades.Helper.DownloadImageTask;
import com.example.clasifenfermedades.R;

import java.util.ArrayList;

public class AdapterDisease extends RecyclerView.Adapter<AdapterDisease.ViewHolderDatos> implements View.OnClickListener {
    ArrayList<Diseases> listDis;
    int resource;


    public AdapterDisease(ArrayList<Diseases> listDis,int resource){
        if(listDis==null){
            this.listDis = new ArrayList<Diseases>();
        }
        else{
            this.listDis = listDis;
        }
        this.resource = resource;

    }


    @NonNull
    @Override
    public ViewHolderDatos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(resource,parent,false);
        view.setOnClickListener(this);
        return new ViewHolderDatos(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderDatos holder, int position) {
        holder.title.setText(listDis.get(position).getTitle());
        holder.desc.setText(listDis.get(position).getDesc());
        new DownloadImageTask(holder.image)
                .execute(listDis.get(position).getImg());


    }

    @Override
    public int getItemCount() {
        return listDis.size();
    }

    @Override
    public void onClick(View view) {

    }


    public class ViewHolderDatos extends RecyclerView.ViewHolder {

        TextView title,desc;
        ImageView image;

        public ViewHolderDatos(@NonNull View itemView) {
            super(itemView);
            title  = (TextView) itemView.findViewById(R.id.cat_title);
            desc  = (TextView) itemView.findViewById(R.id.cat_desc);
            image      = (ImageView) itemView.findViewById(R.id.img_cat);
        }
    }
}
