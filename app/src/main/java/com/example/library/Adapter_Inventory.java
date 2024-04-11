package com.example.library;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Adapter_Inventory extends RecyclerView.Adapter<Adapter_Inventory.MyViewholder> {
    List<DataModel_Inventory> list;
    Context c;
    SharedPreferences pref;






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
        //Initialize model
        DataModel_Inventory dataModel_inventory = list.get(position);

        //binding data with  components
        holder.Title.setText(dataModel_inventory.getTitle());
        holder.publisher.setText(dataModel_inventory.getPublisher());
        holder.Subject.setText(dataModel_inventory.getSubjectTitle());
        holder.author.setText(dataModel_inventory.getAuthor());
        holder.edition.setText(dataModel_inventory.getEdition());
        holder.language.setText(dataModel_inventory.getLanguage());
        holder.access_No.setText(dataModel_inventory.getAccessNo());
        holder.head_title.setText(dataModel_inventory.getSubjectTitle());
        holder.head_subject.setText(dataModel_inventory.getAccessNo());

        //listener
        holder.minimize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.card_details.setVisibility(View.GONE);
                holder.cardView.setVisibility(View.VISIBLE);
                holder.minimize.setVisibility(View.GONE);
                holder.expand.setVisibility(View.VISIBLE);
            }
        });

//       Change color if Search Found
        if (dataModel_inventory.getColor() == "Green") {
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
            //            holder.Title_Details.setTextColor(Color.parseColor("#FFFFFF"));
//            holder.Author_Details.setTextColor(Color.parseColor("#FFFFFF"));
//            holder.RFid_details.setTextColor(Color.parseColor("#FFFFFF"));
//            holder.Access_detail.setTextColor(Color.parseColor("#FFFFFF"));

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

    public class MyViewholder extends RecyclerView.ViewHolder {

        TextView Subject, Title, publisher, author, edition, language, access_No, head_subject, head_title,expand,minimize;
        LinearLayout list_layout;
        CardView cardView, card_details;


        public MyViewholder(@NonNull View itemView) {
            super(itemView);
            Subject = itemView.findViewById(R.id.Subject);
            Title = itemView.findViewById(R.id.Booktitle);
            expand=itemView.findViewById(R.id.expand);
            minimize=itemView.findViewById(R.id.minimize);
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

    public void restoreItem(DataModel_Inventory item, int position) {
        list.add(position, item);
        notifyItemInserted(position);
    }

    public List<DataModel_Inventory> getData() {
        return list;
    }

    //   method for search data
//    public int getFilter(String search_value) {
//        pref = c.getApplicationContext().getSharedPreferences("pref", Context.MODE_PRIVATE);
//        String charString = search_value;
//        int bookFooundCount = 0;
//        String rfidNo;
//        HashSet<String> matchedRfidNo = new HashSet<>();
//        SharedPreferences.Editor editor = pref.edit();
//        if (!charString.isEmpty()) {
//
//            for (DataModel_Inventory row : list) {
//               //if (!pref.getString("RFID NO",null).equals(row.getrFIDNo())) {
//
//                try {
//                    if ( row.getrFIDNo().matches(charString)) {
////                    if ( row.getrFIDNo().matches(charString) || row.getTitle().matches(charString)) {
//                       // if (!matchedRfidNo.contains(search_value)) {
//                           // matchedRfidNo.add(search_value);
//                            Log.d("SearchValie",search_value);
//                            row.setColor("Green");
//
//
//
////                    String accession=row.getAccessNo();
////                    Toast.makeText(c.getApplicationContext(), "accession"+row.getrFIDNo(), Toast.LENGTH_SHORT).show();
//                            editor.putString("RFID NO", row.getrFIDNo());
//                            editor.putString("Status", "True");
//                            editor.commit();
//                        bookFooundCount = bookFooundCount + 1;
//                        notifyDataSetChanged();
////                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
////                                List<String> matchedRfidNoDistinctList = matchedRfidNo.stream().distinct().collect(Collectors.toList());
////                                bookFooundCount = matchedRfidNo.size();
////                                notifyDataSetChanged();
////
////                            }
//
//                        //}
//                    }
//
////                    break;
//              //  }
//                else {
//                    editor.putString("RFID NO", row.getrFIDNo());
//                    editor.putString("Status", "False");
//
//
//                }
//                } catch (Exception e){
//                    Log.d("nullPointer",e.toString());
//                }
//            }
//        } else {
//            Toast.makeText(c.getApplicationContext(), "Please Enter Keyword...", Toast.LENGTH_SHORT).show();
//        }
//        return bookFooundCount;
//    }








    private Handler handler = new Handler(Looper.getMainLooper());
    private final Object countLock = new Object();
    final Set<String> uniqueRfidTags = new HashSet<>();
     int bookFoundCount = 0;

    public int getFilter(final String search_value) {
        final String charString = search_value;

        pref = c.getApplicationContext().getSharedPreferences("pref", Context.MODE_PRIVATE);

//        final Set<String> uniqueRfidTags = new HashSet<>(); // Store unique RFID tags

        if (!charString.isEmpty()) {
            // Offload the data processing to a background thread
            new Thread(new Runnable() {
                @Override
                public void run() {
                    List<DataModel_Inventory> matchingItems = new ArrayList<>();
                    SharedPreferences.Editor editor;

                    synchronized (pref) { // Synchronize SharedPreferences access
                        editor = pref.edit();
                        for (DataModel_Inventory row : list) {
                            if (row.getrFIDNo().matches(charString)) {
                                String rfidTag = row.getrFIDNo();
                                if (!uniqueRfidTags.contains(rfidTag)) {
                                    uniqueRfidTags.add(rfidTag); // Add to unique RFID tags set
                                    matchingItems.add(row);
                                    editor.putString("RFID NO", rfidTag);
                                    editor.putString("Status", "True");
                                }
                            } else {
                                editor.putString("RFID NO", row.getrFIDNo());
                                editor.putString("Status", "False");
                            }
                        }
                    }
                    editor.apply(); // Apply changes after processing all items

                    // Debug log to check the size of matchingItems
                    Log.d("MatchingItems", "Size: " + matchingItems.size());

                    // Update the UI with matching items on the main UI thread
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            updateColors(matchingItems);

                            // Synchronize access to bookFoundCount
                            synchronized (countLock) {
                               // bookFoundCount = uniqueRfidTags.size();
                                Log.d("nskvhkvijibvijb", String.valueOf(uniqueRfidTags.size()));
                                notifyDataSetChanged();
                            }

                            // Debug log to check if notifyDataSetChanged() is called
                            Log.d("UI Update", "NotifiedDataSetChanged");
                        }
                    });

                   // Log.d("unirfd", "Total Matching Items: " + uniqueRfidTags.size());
                }
            }).start();
        } else {
            Toast.makeText(c.getApplicationContext(), "Please Enter Keyword...", Toast.LENGTH_SHORT).show();
        }

        synchronized (countLock) {
            return bookFoundCount; // Return the count of unique RFID tags found
        }
    }




    private void updateColors(List<DataModel_Inventory> matchingItems) {
        for (DataModel_Inventory row : matchingItems) {
            row.setColor("Green");
            bookFoundCount = uniqueRfidTags.size();
            Log.d("unirfd", String.valueOf(uniqueRfidTags.size()));
        }
    }


}
