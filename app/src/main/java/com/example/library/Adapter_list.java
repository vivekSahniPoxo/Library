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
    Context context;

    public Adapter_list(List<Data_Model_Search> list, Context context) {
        this.list = list;
        this.context = context;

    }


    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.list, parent, false);
        return new myviewholder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull myviewholder holder, int position) {
//Initial Data model
        Data_Model_Search model_search = list.get(position);

        //Binding Data with components
        holder.Title.setText(model_search.getTitle());
        holder.publisher.setText(model_search.getPublisher());
        holder.Subject.setText(model_search.getSubjectTitle());
        holder.author.setText(model_search.getAuthor());
        holder.edition.setText(model_search.getEdition());
        holder.language.setText(model_search.getLanguage());
        holder.access_No.setText(model_search.getAccessNo());
        holder.head_title.setText(model_search.getAccessNo());
        holder.head_subject.setText(model_search.getTitle());


        //Listener
        holder.minimize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.card_details.setVisibility(View.GONE);
                holder.cardView.setVisibility(View.VISIBLE);
                holder.minimize.setVisibility(View.GONE);
                holder.expand.setVisibility(View.VISIBLE);
            }
        });

//Change color if data found
        if (model_search.getColor() == "Green") {
            holder.cardView.setCardBackgroundColor(Color.rgb(46, 139, 87));
            holder.head_subject.setTextColor(Color.parseColor("#FFFFFF"));
            holder.head_title.setTextColor(Color.parseColor("#FFFFFF"));
            holder.expand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.card_details.setVisibility(View.VISIBLE);
                    holder.expand.setVisibility(View.GONE);
                    holder.minimize.setVisibility(View.VISIBLE);


//                holder.cardView.setVisibility(View.GONE);
                }
            });
        } else {
            holder.cardView.setCardBackgroundColor(Color.WHITE);
            holder.head_subject.setTextColor(Color.parseColor("#000000"));
            holder.head_title.setTextColor(Color.parseColor("#000000"));

        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class myviewholder extends RecyclerView.ViewHolder {
        TextView Subject, Title, publisher, author, edition, language, access_No, head_subject, head_title,expand,minimize;
        LinearLayout list_layout;
        CardView cardView, card_details;

        public myviewholder(@NonNull View itemView) {
            super(itemView);
            //Binding components
            Subject = itemView.findViewById(R.id.Subject);
            expand=itemView.findViewById(R.id.expand);
            minimize=itemView.findViewById(R.id.minimize);
            Title = itemView.findViewById(R.id.Booktitle);
            list_layout = itemView.findViewById(R.id.list_layout);
            cardView = itemView.findViewById(R.id.cardView);
            card_details = itemView.findViewById(R.id.cardView_Details);
            publisher = itemView.findViewById(R.id.Publisher);
            author = itemView.findViewById(R.id.Authorname);
            edition = itemView.findViewById(R.id.Edition);
            language = itemView.findViewById(R.id.Language);
            access_No = itemView.findViewById(R.id.Access_No);
            head_subject = itemView.findViewById(R.id.Head_subject);
            head_title = itemView.findViewById(R.id.Head_Tilte);

        }
    }


    public void removeItem(int position) {
        list.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(Data_Model_Search item, int position) {
        list.add(position, item);
        notifyItemInserted(position);
    }

    public List<Data_Model_Search> getData() {
        return list;
    }

    //Method for Search
    public void getFilter(String search_value) {
        String charString = search_value;
        if (!charString.isEmpty()) {

            for (Data_Model_Search row : list) {


                if (row.getAccessNo().matches(charString)) {

                    row.setColor("Green");
                    notifyDataSetChanged();
//                    break;
                }
//                else {
//                    Toast.makeText(context.getApplicationContext(), "Data Not Found", Toast.LENGTH_SHORT).show();
//                    break;
//                }
            }
        } else {
            Toast.makeText(context.getApplicationContext(), "Please Enter Keyword...", Toast.LENGTH_SHORT).show();
        }
    }
}