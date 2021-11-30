package com.example.library;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.net.SSLCertificateSocketFactory;
import android.os.Build;
import android.os.Bundle;
import android.se.omapi.Session;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BaseHttpStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;


public class Search_Form extends AppCompatActivity {
    Button add_accession_btn, Search_btn, retry_btn;
    EditText accession_no, search_data;
    RecyclerView recyclerView;
    List<Data_Model_Search> list_data_Recyclerview = new ArrayList<>();
    Adapter_list adapter_list;
    ProgressDialog dialog;
    CoordinatorLayout coordinate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_form);

        //Binding Components
        coordinate = findViewById(R.id.coordinator);
        recyclerView = findViewById(R.id.recyclerView_Accession);
        Search_btn = findViewById(R.id.Search_b);
        search_data = findViewById(R.id.Search_Data);
        retry_btn = findViewById(R.id.Retry);
        add_accession_btn = findViewById(R.id.Add_accession);
        accession_no = findViewById(R.id.Accession_no);

        //Initialize of Progress Dialog
        dialog = new ProgressDialog(this);

        //Method for Swipe to Delete
        enableSwipeToDeleteAndUndo();

        //Listeners
        retry_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Clear();
            }
        });

        Search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (search_data.length() > 0) {

                    String Search_value = search_data.getText().toString();
                    search_data.setText("");
//                filter(Search_value);
                    adapter_list.getFilter(Search_value);
                } else {
                    search_data.setError("Enter Input...");
                }
            }
        });

        add_accession_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (accession_no.length() > 0) {

                    try {
                        FetchData();
                        dialog.show();
                        dialog.setMessage(getString(R.string.Dialog_Text));
                        dialog.setCancelable(false);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    accession_no.setError("Enter Input...");
                }
                accession_no.setText("");
            }
        });


    }

    //Method for Search Data From Server using Accession Number
    private void FetchData() throws JSONException {
        String url = "https://library.poxorfid.com/api/BooksInfo/FetchBookByAccessNo";
        JSONObject obj = new JSONObject();
//
//        obj.put("AccessNo", "B1228");
        obj.put("AccessNo", accession_no.getText().toString());
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String access_details = response.getString("AccessNo");
                            String Title = response.getString("Title");
                            String publisher = response.getString("Publisher");
                            String Author = response.getString("Author");
                            String subject = response.getString("SubjectTitle");
                            String language = response.getString("Language");
                            String edition = response.getString("Edition");


                            list_data_Recyclerview.add(new Data_Model_Search(subject, language, edition, publisher, access_details, Author, Title));
                            adapter_list = new Adapter_list(list_data_Recyclerview, getApplicationContext());
                            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                            recyclerView.setAdapter(adapter_list);
                            dialog.dismiss();
//                           System.out.println("Search Response "+response.toString());
                            Log.e("response Search", response.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog.dismiss();
                        System.out.println("Negative Response" + error.getMessage());
                    }
                });


        queue.add(jsObjRequest);

    }

    private void enableSwipeToDeleteAndUndo() {

        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {


                final int position = viewHolder.getAdapterPosition();
                final Data_Model_Search item = adapter_list.getData().get(position);
                adapter_list.removeItem(position);


                Snackbar snackbar = Snackbar
                        .make(coordinate, "Item was removed from the list.", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        adapter_list.restoreItem(item, position);
                        recyclerView.scrollToPosition(position);
                    }
                });
//
                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();

            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(recyclerView);
    }

    //Method for Clear Data from Components
    public void Clear() {
        list_data_Recyclerview.clear();
        adapter_list = new Adapter_list(list_data_Recyclerview, getApplicationContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter_list);
        search_data.setText("");
        accession_no.setText("");
    }

}