package com.example.library;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
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
    Button add_accession, Search;
    EditText accession_no, search_data;
    RecyclerView recyclerView;
    List<Data_Model_Search> list_data = new ArrayList<>();
    Adapter_list adapter_list;
    ProgressDialog dialog;


//    NukeSSLCerts certs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_form);
        recyclerView = findViewById(R.id.recyclerView_Accession);
        Search = findViewById(R.id.Search_b);
        search_data = findViewById(R.id.Search_Data);
        dialog = new ProgressDialog(this);

        Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Search_value = search_data.getText().toString();
//                filter(Search_value);
                adapter_list.getFilter(Search_value);
            }

        });


        add_accession = findViewById(R.id.Add_accession);
        accession_no = findViewById(R.id.Accession_no);
        add_accession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    FetchData();
                    dialog.show();
                    dialog.setMessage("Fetching...");
                    dialog.setCancelable(false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

//                list_data.add(new DataModel(accession));
//                Adapter_list adapter_list = new Adapter_list(list_data, getApplicationContext());
//                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
//                recyclerView.setAdapter(adapter_list);
                accession_no.setText("");
//                list_data = new ArrayList<>();
//                adapter_list = new Adapter_list(list_data,getApplicationContext());
//                recyclerView.setAdapter(adapter_list);

            }
        });


    }


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
                            String publisher=response.getString("Publisher");
                            String rfid=response.getString("RFIDNo");

                            list_data.add(new Data_Model_Search(publisher,rfid,access_details, Title));
                             adapter_list = new Adapter_list(list_data, getApplicationContext());
                            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                            recyclerView.setAdapter(adapter_list);
                           dialog.dismiss();
                            Log.e("response", response.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        hideProgressDialog();
                        dialog.dismiss();
                        System.out.println("Negative Response" + error.getMessage());
                    }
                });


        queue.add(jsObjRequest);

    }

//    private void filter(String s) {
//
//        ArrayList<Data_Model_Search> filteredlist = new ArrayList<>();
//
//        // running a for loop to compare elements.
//        for (Data_Model_Search item : list_data) {
//            // checking if the entered string matched with any item of our recycler view.
//            if (item.getAccessNo().toLowerCase().contains(s.toString().toLowerCase())) {
//                // if the item is matched we are
//                // adding it to our filtered list.
//                filteredlist.add(item);
//            }
//        }
//        if (filteredlist.isEmpty()) {
//            // if no item is added in filtered list we are
//            // displaying a toast message as no data found.
//            Toast.makeText(this, "No Data Found..", Toast.LENGTH_SHORT).show();
//        } else {
//            // at last we are passing that filtered
//            // list to our adapter class.
//            adapter_list.filterList(filteredlist);
//
//        }
//    }

}
