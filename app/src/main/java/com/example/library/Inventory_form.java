package com.example.library;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class Inventory_form extends AppCompatActivity {
    Spinner Select_Inventory, Inventory_Details;
    String[] value = new String[]{"Choose", "Title", "Author", "Department", "Racks"};
    List<String> data_value;
    String inventory_details_option, Select_option, Submit_rfid;
    ProgressDialog dialog;
    Button search_book, Search, Submit, NewBtn, Back_Btn;
    List<DataModel_Inventory> List_Inventory;
    TextView total, found, not_found;
    Adapter_Inventory adapter_list;
    RecyclerView recyclerView;
    EditText Accession;
    DataModel_Inventory dataModel_inventory;
    boolean submit_foundStatus;
    int counter = 0, len, not_founded;
    CoordinatorLayout coordinatorLayout;
    SharedPreferences sh;
    List<DataModel_Inventory_InventoryStatus> ListStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_form);

        //Initialize of Components
        Select_Inventory = findViewById(R.id.spinner);
        Inventory_Details = findViewById(R.id.spinner_details);
        dialog = new ProgressDialog(this);
        search_book = findViewById(R.id.Search_Books);
        total = findViewById(R.id.Total);
        found = findViewById(R.id.Found);
        not_found = findViewById(R.id.not_found);
        recyclerView = findViewById(R.id.recyclerView);
        Search = findViewById(R.id.Search_);
        Accession = findViewById(R.id.Search_Data);
        NewBtn = findViewById(R.id.New_Button);
        coordinatorLayout = findViewById(R.id.coordinator);
        Back_Btn = findViewById(R.id.Back_Button);
        dataModel_inventory = new DataModel_Inventory();
        Submit = findViewById(R.id.Submit_Button);

        //Method for Left Swipe to Delete
        enableSwipeToDeleteAndUndo();


        sh = getSharedPreferences("pref", 0);


        NewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Clear();
            }
        });
        Back_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Inventory_form.this, MainActivity.class));
                finish();
            }
        });

        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    dialog.show();
                    dialog.setMessage(getString(R.string.Dialog_Text));
                    dialog.setCancelable(false);
                    submit_Report();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


        Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Accession.length() > 0) {
                    String Search_value = Accession.getText().toString();
                    int fnd = adapter_list.getFilter(Search_value);
                    counter = counter + fnd;
//                    Toast.makeText(Inventory_form.this, "Counted "+counter, Toast.LENGTH_SHORT).show();
                    Accession.setText("");
                    not_founded = len - counter;
                    total.setText(String.valueOf(len));
                    found.setText(String.valueOf(counter));
                    not_found.setText(String.valueOf(not_founded));
//                    count();
                } else {
                    Accession.setError("Enter input please....");
                }
            }

        });

        search_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    List_Inventory.clear();
                    FetchData(inventory_details_option, Select_option);
                    dialog.show();
                    dialog.setMessage(getString(R.string.Dialog_Text));
                    dialog.setCancelable(false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                Toast.makeText(Inventory_form.this, "Select value"+inventory_details_option+Select_option, Toast.LENGTH_SHORT).show();
            }
        });

//     Enable DetailSpinner
        Inventory_Details.setEnabled(false);


        //Initialize Array
        data_value = new ArrayList<String>();
        ListStatus = new ArrayList<>();
        data_value.add("Choose");

        //Set up Local Data with Adapter
        final List<String> List = new ArrayList<>(Arrays.asList(value));
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, List);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        Select_Inventory.setAdapter(spinnerArrayAdapter);

        // Implemented onSelected Listener on Spinner
        Select_Inventory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 1) {
                    String url = "https://library.poxorfid.com/api/Inventory/FetchTitles";
                    SelectByTitle(url);
                    dialog.show();
                    dialog.setMessage(getString(R.string.Dialog_Text));
                    dialog.setCancelable(false);

////                    Toast.makeText(Inventory_form.this,"Title",Toast.LENGTH_LONG).show();
                } else if (position == 2) {
                    String url = "https://library.poxorfid.com/api/Inventory/FetchAuthor";
                    SelectByTitle(url);
                    dialog.show();
                    dialog.setMessage(getString(R.string.Dialog_Text));
                    dialog.setCancelable(false);

//                    Toast.makeText(Inventory_form.this,"Author",Toast.LENGTH_LONG).show();

                } else if (position == 3) {
                    String url = "https://library.poxorfid.com/api/Inventory/FetchDepartments";
                    SelectByTitle(url);
                    dialog.show();
                    dialog.setMessage(getString(R.string.Dialog_Text));
                    dialog.setCancelable(false);

                } else if (position == 4) {
                    String url = "https://library.poxorfid.com/api/Inventory/FetchShelves";
                    SelectByTitle(url);
                    dialog.show();
                    dialog.setMessage(getString(R.string.Dialog_Text));
                    dialog.setCancelable(false);

                }
                Select_option = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Set up Detail Spinner


        Inventory_Details.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                inventory_details_option = parent.getItemAtPosition(position).toString();
                System.out.println("Select Value " + inventory_details_option);
//                Toast.makeText(Inventory_form.this, "Select Value" + inventory_details_option, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Set The Value
        found.setText("0");
        not_found.setText("0");
        total.setText(String.valueOf(len));

    }

    //Submit Report to Server Method
    private void submit_Report() throws JSONException {

        //Getting Shared Data
        String rfid = sh.getString("RFID NO", null);
        String status = sh.getString("Status", null);
        ListStatus.add(new DataModel_Inventory_InventoryStatus(rfid, status));

        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        String url = " https://library.poxorfid.com/api/Inventory/SubmitInventoyRecord";

        JSONObject object = new JSONObject();
        object.put("inventoryID", "");
        object.put("inventoryBasedOn", Select_option);
        object.put("doneBy", "Admin");
        object.put("total", String.valueOf(len));
        object.put("found", String.valueOf(counter));
        object.put("notFound", String.valueOf(not_founded));
        object.put("date", currentDate);
        JSONArray array = new JSONArray();
        JSONObject obj = new JSONObject();

        for (int i = 0;i<ListStatus.size(); i++) {
            obj.put("rfidNo", ListStatus.get(i).getRFIDNUMBER());
            obj.put("foundStatus", ListStatus.get(i).getStatus());
            array.put(obj);
        }

        object.put("inventoryList", "" + array);
//
        RequestQueue queue = Volley.newRequestQueue(this);
        System.out.println("JSON DATA " + object);

        final String requestBody = object.toString();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(Inventory_form.this, response + "Submitted....", Toast.LENGTH_SHORT).show();
                Log.i("VOLLEY Submit", response);
                dialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VOLLEY Negative", error.toString() + "Submitted Response : " + error.networkResponse.data + "UTF-8");
                System.out.println("Error Submitting" + error.getCause());
                dialog.dismiss();
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                System.out.println("Response Code " + response.statusCode);
                return super.parseNetworkResponse(response);
            }
        };

        queue.add(stringRequest);
    }

    //Method for Count Number of Search
//    public void count() {
//        while (dataModel_inventory.getColor() == "Green") {
//
//            System.out.println(counter + "Search DATA ");
//        }
//        counter++;
//
//        System.out.println(counter + "Search DATA ");
//        not_founded = len - counter;
//        total.setText(String.valueOf(len));
//        found.setText(String.valueOf(counter));
//        not_found.setText(String.valueOf(not_founded));
//
//    }

    //Method Fetch Data for DetailsSpinner
    public void SelectByTitle(String url) {

        RequestQueue queue = Volley.newRequestQueue(this);


        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                try {
                    JSONArray jsonArray = new JSONArray(response);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        data_value.add(jsonArray.getString(i));

                    }
//
//                    System.out.println("Sorted List "+data_value);
                    RearrangeItems(data_value);
//                    final List<String> List = new ArrayList<>(Arrays.asList(value));
//
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                List_Inventory = new ArrayList<>();
                dialog.dismiss();
                Inventory_Details.setEnabled(true);

//                System.out.println("Array Value " + data_value);
//
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Inventory_form.this, error.getMessage(), Toast.LENGTH_LONG).show();
                dialog.dismiss();

            }
        });

        queue.add(request);

    }

    //Method for Fetch Books Details
    private void FetchData(String inventory_details_option, String select_option) throws JSONException {

        String url = "https://library.poxorfid.com/api/Inventory/InventoryRecords";
        JSONObject obj = new JSONObject();
//        obj.put("AccessNo", "B1228");
        obj.put("SearchParameter", select_option.trim());
        obj.put("SearchValue", inventory_details_option.trim());

        RequestQueue queue = Volley.newRequestQueue(this);


        final String requestBody = obj.toString();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONArray array = new JSONArray(response);

                    len = array.length();
                    total.setText(String.valueOf(len));


                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        String access_details = object.getString("AccessNo");
                        String Title = object.getString("Title");
                        String publisher = object.getString("Publisher");
                        String Author = object.getString("Author");
                        String subject = object.getString("SubjectTitle");
                        String language = object.getString("Language");
                        String edition = object.getString("Edition");
                        String RFIDNO = object.getString("RFIDNo");
                        List_Inventory.add(new DataModel_Inventory(Title, language, edition, publisher, access_details, Author, subject, RFIDNO));


                    }

                    adapter_list = new Adapter_Inventory(List_Inventory, getApplicationContext());
                    recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    recyclerView.setAdapter(adapter_list);
                    dialog.dismiss();
//                    Toast.makeText(Inventory_form.this, name, Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.i("VOLLEY", response);
                dialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VOLLEY Negative", error.toString());
                dialog.dismiss();
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }
        };

        queue.add(stringRequest);
    }

    //Method for Clear Data from components
    public void Clear() {
        List_Inventory.clear();

        adapter_list = new Adapter_Inventory(List_Inventory, getApplicationContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter_list);
        Accession.setText("");
        total.setText("");
        not_found.setText("");
        found.setText("");
        data_value.clear();
        data_value.add("Choose");
        final ArrayAdapter<String> spinnerArrayAdapter1 = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, data_value);
        spinnerArrayAdapter1.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        Inventory_Details.setAdapter(spinnerArrayAdapter1);

    }

    //Method For Left swipe for Delete
    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {


                final int position = viewHolder.getAdapterPosition();
                final DataModel_Inventory item = adapter_list.getData().get(position);
                adapter_list.removeItem(position);


                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, "Item was removed from the list.", Snackbar.LENGTH_LONG);
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

    private void RearrangeItems(List<String> data_value) {
        Collections.sort(this.data_value, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.compareToIgnoreCase(s2);
            }
        });
        final ArrayAdapter<String> spinnerArrayAdapter1 = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, this.data_value);
        spinnerArrayAdapter1.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        Inventory_Details.setAdapter(spinnerArrayAdapter1);

//        Adapter adapter = new Adapter(List_Inventory, Inventory_form.this);
//        recyclerView.setAdapter(adapter);
    }
}