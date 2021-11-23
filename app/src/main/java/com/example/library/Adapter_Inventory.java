package com.example.library;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class Adapter_Inventory extends RecyclerView.Adapter<Adapter_Inventory.MyViewholder> {
    List<DataModel_Inventory> list;
    Context c;

    public Adapter_Inventory(List<DataModel_Inventory> list, Context c) {
        this.list = list;
        this.c = c;
    }

    @NonNull
    @Override
    public MyViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(c).inflate(R.layout.list, parent, false);
        return new MyViewholder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewholder holder, int position) {
        DataModel_Inventory dataModel_inventory = list.get(position);
        holder.accession_num.setText(dataModel_inventory.getAccessNo());
        holder.subject_Tilte.setText(dataModel_inventory.getTitle());
        if (dataModel_inventory.getColor() == "Green") {
            holder.cardView.setCardBackgroundColor(Color.rgb(46, 139, 87));
        }
        else
        {
            holder.cardView.setCardBackgroundColor(Color.WHITE);

        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewholder extends RecyclerView.ViewHolder {

        TextView accession_num, subject_Tilte;
        LinearLayout list_layout;
        CardView cardView;
        public MyViewholder(@NonNull View itemView) {
            super(itemView);
            accession_num = itemView.findViewById(R.id.access_num);
            subject_Tilte = itemView.findViewById(R.id.subject_Tilte);
            list_layout = itemView.findViewById(R.id.list_layout);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }

    public int getFilter(String search_value) {
        String charString = search_value;
        int bookFooundCount = 0;
        if (!charString.isEmpty()) {

            for (DataModel_Inventory row : list) {
                if (row.getAccessNo().matches(charString) || row.getTitle().matches(charString)) {
                    row.setColor("Green");
                    bookFooundCount = bookFooundCount+1;
                    notifyDataSetChanged();
                }

            }
        }  else {
            Toast.makeText(c.getApplicationContext(), "Please Enter Keyword...", Toast.LENGTH_SHORT).show();
        }
        return  bookFooundCount;
    }
}
