package com.example.library;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Inventory_form extends AppCompatActivity {
    Spinner Select_Inventory;
    Spinner Inventory_Details;
    String[] value = new String[]{"Choose", "Title", "Author", "Department", "Shelves"};
    List<String> data_value;
    String inventory_details_option, Select_option;
    ProgressDialog dialog;
    Button search_book, Search, Submit;
    List<DataModel_Inventory> List_Inventory;
    TextView total, found, not_found;
    Adapter_Inventory adapter_list;
    RecyclerView recyclerView;
    EditText Accession;
    DataModel_Inventory dataModel_inventory;
    String Submit_rfid;
    boolean submit_foundStatus;
    int counter = 0;
    int len;

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
        dataModel_inventory = new DataModel_Inventory();
        Submit = findViewById(R.id.Submit_Button);
        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    dialog.show();
                    dialog.setMessage("Fetching...");
                    dialog.setCancelable(false);
                    submit_Report();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


        if (dataModel_inventory.getColor() == "Green") {
            Submit_rfid = dataModel_inventory.getRFIDNo();
            submit_foundStatus = true;
        } else {
            submit_foundStatus = false;
        }

//        founded = dataModel_inventory.getColor();
//        Toast.makeText(Inventory_form.this, founded, Toast.LENGTH_SHORT).show();
        Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Search_value = Accession.getText().toString();
                int countResult = adapter_list.getFilter(Search_value);
//                found.setText(String.valueOf(countResult));count();
                Accession.setText("");
                count();
            }
        });


        search_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    List_Inventory.clear();
                    FetchData(inventory_details_option, Select_option);
                    dialog.show();
                    dialog.setMessage("Fetching...");
                    dialog.setCancelable(false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                Toast.makeText(Inventory_form.this, "Select value"+inventory_details_option+Select_option, Toast.LENGTH_SHORT).show();
            }
        });

        Inventory_Details.setEnabled(false);
        //Initialize Arrayo
        data_value = new ArrayList<String>();
        data_value.add("Choose");

        //Set up Local Data with Adapter
        final List<String> List = new ArrayList<>(Arrays.asList(value));
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, List);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        Select_Inventory.setAdapter(spinnerArrayAdapter);

//             Select_Inventory.setOnClickListener(new View.OnClickListener() {
//                 @Override
//                 public void onClick(View v) {
//                     List_Inventory.clear();
//                 }
//             });
        // Implemented onSelected Listener on Spinner
        Select_Inventory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 1) {
                    String url = "https://library.poxorfid.com/api/Inventory/FetchTitles";
                    SelectByTitle(url);
                    dialog.show();
                    dialog.setMessage("Fetching...");
                    dialog.setCancelable(false);

////                    Toast.makeText(Inventory_form.this,"Title",Toast.LENGTH_LONG).show();
                } else if (position == 2) {
                    String url = "https://library.poxorfid.com/api/Inventory/FetchAuthor";
                    SelectByTitle(url);
                    dialog.show();
                    dialog.setMessage("Fetching...");
                    dialog.setCancelable(false);

//                    Toast.makeText(Inventory_form.this,"Author",Toast.LENGTH_LONG).show();

                } else if (position == 3) {
                    String url = "https://library.poxorfid.com/api/Inventory/FetchDepartments";
                    SelectByTitle(url);
                    dialog.show();
                    dialog.setMessage("Fetching...");
                    dialog.setCancelable(false);

                } else if (position == 4) {
                    String url = "https://library.poxorfid.com/api/Inventory/FetchShelves";
                    SelectByTitle(url);
                    dialog.show();
                    dialog.setMessage("Fetching...");
                    dialog.setCancelable(false);

                }
                Select_option = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Set up Detail Spinner
        final ArrayAdapter<String> spinnerArrayAdapter1 = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, data_value);
        spinnerArrayAdapter1.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        Inventory_Details.setAdapter(spinnerArrayAdapter1);

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


    }

    private void submit_Report() throws JSONException {
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        String url = " https://library.poxorfid.com/api/Inventory/SubmitInventoyRecord";
        JSONArray array = new JSONArray();
        JSONObject object = new JSONObject();
        object.put("inventoryID", "");
        object.put("inventoryBasedOn", Select_option);
        object.put("doneBy", "Admin");
        object.put("total", total);
        object.put("found", found);
        object.put("notFound", not_found);
        object.put("date", currentDate);

        JSONObject obj = new JSONObject();
        obj.put("rfidNo", Submit_rfid);
        obj.put("foundStatus", submit_foundStatus);
        array.put(obj);
        object.put("inventoryList", obj.toString());
        RequestQueue queue = Volley.newRequestQueue(this);


        final String requestBody = obj.toString();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(Inventory_form.this, "Submitted....", Toast.LENGTH_SHORT).show();
                Log.i("VOLLEY Submit", response);
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


    public void count() {

        while (dataModel_inventory.getColor() == "Green") {

            System.out.println(counter + "Search DATA ");
        }
        counter++;

        System.out.println(counter + "Search DATA ");

        int not_founded = len - counter;

        total.setText(String.valueOf(len));
        found.setText(String.valueOf(counter));
        not_found.setText(String.valueOf(not_founded));

    }

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


                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        String RFIDNo = object.getString("RFIDNo");
                        String AccessNo = object.getString("AccessNo");
                        String Author = object.getString("Author");
                        String Title = object.getString("Title");
                        List_Inventory.add(new DataModel_Inventory(RFIDNo, AccessNo, Author, Title));


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

}