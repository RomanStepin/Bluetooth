package com.example.bluetooth;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyRecyclerViewAdapterForMain extends RecyclerView.Adapter<MyRecyclerViewAdapterForMain.ViewHolderForMain>
{
    private ArrayList<String> arrayListMAC;

    public MyRecyclerViewAdapterForMain(ArrayList<String> arrayListMAC)
    {
        this.arrayListMAC = arrayListMAC;
    }

    public ArrayList<String> getData()
    {
        return arrayListMAC;
    }

    public void setData(ArrayList<String> arrayListMAC)
    {
        this.arrayListMAC = arrayListMAC;
    }

    @NonNull
    @Override
    public ViewHolderForMain onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.activity_list_item, parent, false);
        return new ViewHolderForMain(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderForMain holder, int position) {
        holder.textViewMAC.setText(arrayListMAC.get(position));
    }

    @Override
    public int getItemCount() {
        return arrayListMAC.size();
    }

    static class ViewHolderForMain extends RecyclerView.ViewHolder
    {
        private TextView textViewMAC;

        public ViewHolderForMain(View itemView) {
            super(itemView);
            textViewMAC = itemView.findViewById(android.R.id.text1);
        }
    }
}
