package com.example.bluetooth;

import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder>
{
    private ArrayList<String> arrayListMAC;


    public MyRecyclerViewAdapter(ArrayList<String> arrayListMAC) {
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
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ModelMAC.saveMAC(((TextView)v.findViewById(android.R.id.text1)).getText().toString(), parent.getContext());
            }
        });
        v.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.setBackgroundColor(parent.getResources().getColor(android.R.color.darker_gray));
                return false;
            }
        });
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textViewMAC.setText(arrayListMAC.get(position));
    }

    @Override
    public int getItemCount() {
        if(arrayListMAC != null)
            return arrayListMAC.size();
        else return 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewMAC;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewMAC = itemView.findViewById(android.R.id.text1);
        }
    }
}
