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
        holder.Title.setText(model_search.getTitle());
        holder.publisher.setText(model_search.getPublisher());
        holder.Subject.setText(model_search.getSubjectTitle());
        holder.author.setText(model_search.getAuthor());
        holder.edition.setText(model_search.getEdition());
        holder.language.setText(model_search.getLanguage());
        holder.access_No.setText(model_search.getAccessNo());
        holder.head_title.setText(model_search.getAccessNo());
        holder.head_subject.setText(model_search.getTitle());
        holder.access_No.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.card_details.setVisibility(View.GONE);
                holder.cardView.setVisibility(View.VISIBLE);
            }
        });


        if (model_search.getColor() == "Green") {
            holder.cardView.setCardBackgroundColor(Color.rgb(46, 139, 87));
            holder.head_subject.setTextColor(Color.parseColor("#FFFFFF"));
            holder.head_title.setTextColor(Color.parseColor("#FFFFFF"));
            holder.head_title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.card_details.setVisibility(View.VISIBLE);


//                holder.cardView.setVisibility(View.GONE);
                }
            });
//            holder.Title_Details.setTextColor(Color.parseColor("#FFFFFF"));
//            holder.Author_Details.setTextColor(Color.parseColor("#FFFFFF"));
//            holder.RFid_details.setTextColor(Color.parseColor("#FFFFFF"));
//            holder.Access_detail.setTextColor(Color.parseColor("#FFFFFF"));

        } else {
            holder.cardView.setCardBackgroundColor(Color.WHITE);

        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class myviewholder extends RecyclerView.ViewHolder {
        TextView Subject, Title, publisher, author, edition, language, access_No, head_subject, head_title;
        LinearLayout list_layout;
        CardView cardView, card_details;

        public myviewholder(@NonNull View itemView) {
            super(itemView);
            Subject = itemView.findViewById(R.id.Subject);
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


    public void getFilter(String search_value) {
        String charString = search_value;
        if (!charString.isEmpty()) {

            for (Data_Model_Search row : list) {


                if (row.getAccessNo().matches(charString)) {

                    row.setColor("Green");
                    notifyDataSetChanged();
                }

            }
        } else {
            Toast.makeText(context.getApplicationContext(), "Please Enter Keyword...", Toast.LENGTH_SHORT).show();
        }
    }
}