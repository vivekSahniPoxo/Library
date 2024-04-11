package com.example.library;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Adapter_Racking extends RecyclerView.Adapter<Adapter_Racking.myViewholder> {
    List<DataModel_Racking> list;
    Context c;
    Set<DataModel_Racking> uniqueItems; // Maintain a set to store unique items

    public Adapter_Racking(List<DataModel_Racking> list, Context c) {
        this.list = list;
        this.c = c;
        uniqueItems = new HashSet<>(list); // Initialize the set with existing items
    }

    @NonNull
    @Override
    public myViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(c).inflate(R.layout.racking_list, parent, false);
        return new myViewholder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewholder holder, int position) {
        // Initialize Model Class
        DataModel_Racking dataModel_racking = list.get(position);
        // Data Binding
        holder.rack_no.setText(dataModel_racking.getRfid());
        holder.shelve_no.setText(dataModel_racking.getPublisher());
        holder.rfid_no.setText(dataModel_racking.getTitle());

        // Add click listeners to handle removing and restoring items
        holder.itemView.setOnClickListener(v -> {
            if (uniqueItems.contains(dataModel_racking)) {
                removeItem(position);
            } else {
                restoreItem(dataModel_racking, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void removeItem(int position) {
        DataModel_Racking removedItem = list.remove(position);
        uniqueItems.remove(removedItem);
        notifyItemRemoved(position);
    }

    public void restoreItem(DataModel_Racking item, int position) {
        list.add(position, item);
        uniqueItems.add(item);
        notifyItemInserted(position);
    }

    public List<DataModel_Racking> getData() {
        return list;
    }

    public class myViewholder extends RecyclerView.ViewHolder {
        TextView rack_no, shelve_no, rfid_no;

        public myViewholder(@NonNull View itemView) {
            super(itemView);
            rack_no = itemView.findViewById(R.id.rack_number);
            shelve_no = itemView.findViewById(R.id.shelve_Number);
            rfid_no = itemView.findViewById(R.id.RFId_number);
        }
    }
}


//public class Adapter_Racking extends RecyclerView.Adapter<Adapter_Racking.myViewholder> {
//    List<DataModel_Racking> list;
//    Context c;
//
//    public Adapter_Racking(List<DataModel_Racking> list, Context c) {
//        this.list = list;
//        this.c = c;
//    }
//
//    @NonNull
//    @Override
//    public myViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View v = LayoutInflater.from(c).inflate(R.layout.racking_list, parent, false);
//        return new myViewholder(v);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull myViewholder holder, int position) {
//        //Initialize Model Class
//        DataModel_Racking dataModel_racking = list.get(position);
//        //Data Binding
//        holder.rack_no.setText(dataModel_racking.getRfid());
//        holder.shelve_no.setText(dataModel_racking.getPublisher());
//        holder.rfid_no.setText(dataModel_racking.getTitle());
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return list.size();
//    }
//
//    public void removeItem(int position) {
//        list.remove(position);
//        notifyItemRemoved(position);
//    }
//
//    public void restoreItem(DataModel_Racking item, int position) {
//        list.add(position, item);
//        notifyItemInserted(position);
//    }
//
//    public List<DataModel_Racking> getData() {
//        return list;
//    }
//
//    public class myViewholder extends RecyclerView.ViewHolder {
//        TextView rack_no, shelve_no, rfid_no;
//
//        public myViewholder(@NonNull View itemView) {
//            super(itemView);
//            rack_no = itemView.findViewById(R.id.rack_number);
//            shelve_no = itemView.findViewById(R.id.shelve_Number);
//            rfid_no = itemView.findViewById(R.id.RFId_number);
//
//
//        }
//    }
//
//}
