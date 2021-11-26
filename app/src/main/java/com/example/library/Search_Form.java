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
    Button add_accession, Search, retry;
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
        retry = findViewById(R.id.Retry);
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Clear();
            }
        });
        dialog = new ProgressDialog(this);
        search_data.clearFocus();

        Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Search_value = search_data.getText().toString();
                search_data.setText("");
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
                    dialog.setMessage(getString(R.string.Dialog_Text));
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
                            String publisher = response.getString("Publisher");
                            String Author = response.getString("Author");
                            String subject = response.getString("SubjectTitle");
                            String language = response.getString("Language");
                            String edition = response.getString("Edition");


                            list_data.add(new Data_Model_Search(subject, language, edition, publisher, access_details, Author, Title));
                            adapter_list = new Adapter_list(list_data, getApplicationContext());
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

    public void Clear() {
        list_data.clear();
        adapter_list = new Adapter_list(list_data, getApplicationContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter_list);
        search_data.setText("");
        accession_no.setText("");
    }

}
