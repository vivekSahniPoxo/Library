package com.example.library;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Adapter_list extends RecyclerView.Adapter<Adapter_list.myviewholder> {
    List<Data_Model_Search> list;
    List<Data_Model_Search> listfilter;
    Context context;

    public Adapter_list(List<Data_Model_Search> list, Context context) {
        this.list = list;
        this.context = context;
        listfilter = new ArrayList<>(list);
    }


    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.list, parent, false);
        return new myviewholder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull myviewholder holder, int position) {

        Data_Model_Search model_search = list.get(position);
        holder.accession_num.setText(model_search.getAccessNo());
        holder.subject_Tilte.setText(model_search.getTitle());
        if (model_search.getColor() == "Green") {
            holder.cardView.setCardBackgroundColor(Color.rgb(46, 139, 87));
        }else {
            holder.cardView.setCardBackgroundColor(Color.WHITE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class myviewholder extends RecyclerView.ViewHolder {
        TextView accession_num, subject_Tilte;
        LinearLayout list_layout;
        CardView cardView;

        public myviewholder(@NonNull View itemView) {
            super(itemView);
            accession_num = itemView.findViewById(R.id.access_num);
            subject_Tilte = itemView.findViewById(R.id.subject_Tilte);
            list_layout = itemView.findViewById(R.id.list_layout);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }


    public void getFilter(String search_value) {
        String charString = search_value;
        if (!charString.isEmpty()) {

            for (Data_Model_Search row : list) {


                if (row.getAccessNo().matches(charString)) {

                    row.setColor("Green");
                    notifyDataSetChanged();
                }

            }
        }  else {
            Toast.makeText(context.getApplicationContext(), "Please Enter Keyword...", Toast.LENGTH_SHORT).show();
        }
    }
}