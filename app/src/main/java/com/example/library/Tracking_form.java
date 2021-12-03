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
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Tracking_form extends AppCompatActivity {
    Spinner spinner_shelve_no, rack_number_Spinner;
    String[] value = new String[]{"Title", "Author", "Branch", "Selected Rack"};
    EditText rfid_number;
    Button Add_Racking, Submit_racking, Submit_Restart;
    List<String> rack_no, list_shelve;
    String rack_Number_Selected, shelve_number_selected, shelve;
    ProgressDialog dialog;
    List<DataModel_Racking> Item_Add_Item;
    RecyclerView recyclerView;
    Adapter_Racking adapter_racking;
    CoordinatorLayout constraintLayout;
    List<List_Data_Model> data_models;
    DataModel_Racking dataModel_racking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_form);
        //Component Initialization
        constraintLayout = findViewById(R.id.coordinator);
        spinner_shelve_no = findViewById(R.id.Shelve_number_racking);
        rack_number_Spinner = findViewById(R.id.Rack_number_Racking);
        rfid_number = findViewById(R.id.Rfid_Number_Racking);
        Add_Racking = findViewById(R.id.Add_Button_Racking);
        Submit_racking = findViewById(R.id.Submit_racking);
        recyclerView = findViewById(R.id.tracking_recyclerview);
        Submit_Restart = findViewById(R.id.Restart_racking);

//    Initialize of List and Model Class object
        Item_Add_Item = new ArrayList<>();
        data_models = new ArrayList<>();
        dataModel_racking = new DataModel_Racking();
        list_shelve = new ArrayList<>();


//     Disable Spinner
        spinner_shelve_no.setEnabled(false);


        //Listener on Add button
        Add_Racking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Item_Add_Item.add(new DataModel_Racking(rack_Number_Selected, shelve_number_selected, rfid_number.getText().toString()));
//                SetUPRecylerview();
                if (rfid_number.length() > 0) {
                    try {
                        FetchData();
                        rfid_number.setText("");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    rfid_number.setError("Please Enter Accession Number...");
                }
            }
        });


        //Dialog box initialize
        dialog = new ProgressDialog(this);
        dialog.show();
        dialog.setMessage(getString(R.string.Dialog_Text));
        dialog.setCancelable(false);

        Submit_racking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Update_shelve();
                    dialog.show();
                    dialog.setMessage(getString(R.string.Dialog_Text));
                    dialog.setCancelable(false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        //Method for swipe left to delete
        enableSwipeToDeleteAndUndo();


        //Method for Spinner value from Api
        FetchRack_NO();
        list_shelve.add("Choose");

        //Initialize List
        rack_no = new ArrayList<>();
        rack_no.add("Choose");


        // Set up Detail Spinner
        final ArrayAdapter<String> spinnerArrayAdapter1 = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, rack_no);
        spinnerArrayAdapter1.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        rack_number_Spinner.setAdapter(spinnerArrayAdapter1);
        rack_number_Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                rack_Number_Selected = parent.getItemAtPosition(position).toString();
                try {
                    dialog.show();
                    dialog.setMessage(getString(R.string.Dialog_Text));
                    dialog.setCancelable(false);
                    FetchShelve_No(rack_Number_Selected);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        Submit_Restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Clear();
            }
        });

        //Set up Spinner For Showing Shelve Number
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, list_shelve);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner_shelve_no.setAdapter(spinnerArrayAdapter);
        spinner_shelve_no.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                shelve_number_selected = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

//        data_models.add(new List_Data_Model(dataModel_racking.getRFIDNo(),shelve_number_selected,rack_Number_Selected));

    }

    //Method for Racks Number
    private void FetchRack_NO() {
        String url = "https://library.poxorfid.com/api/Inventory/FetchRacks";
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    dialog.dismiss();
                    JSONObject object = new JSONObject(response);

                    String one = object.getString("1");
                    String two = object.getString("2");
                    String three = object.getString("3");

                    rack_no.add(one);
                    rack_no.add(two);
                    rack_no.add(three);
                    System.out.println(one + "Value for spinner");

                } catch (JSONException jsonException) {
                    jsonException.printStackTrace();
                }
//                    final List<String> List = new ArrayList<>(Arrays.asList(value));


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Tracking_form.this, error.getMessage(), Toast.LENGTH_LONG).show();
                dialog.dismiss();

            }
        });

        queue.add(request);

    }

    //Method for Fetch Shelve Number
    private void FetchShelve_No(String rack_Number_Selected) throws JSONException {
        String url = "https://library.poxorfid.com/api/Inventory/FetchShelves";
        JSONObject object = new JSONObject();
        object.put("RackNo", rack_Number_Selected);
        final String requestBody = object.toString();
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                list_shelve.clear();
                list_shelve.add("Choose");
                spinner_shelve_no.setEnabled(true);
                try {
                    JSONArray array = new JSONArray(response);
                    for (int i = 0; i < array.length(); i++) {
                        shelve = array.getString(i);
                        list_shelve.add(shelve);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                Toast.makeText(Tracking_form.this, "Data Updated Successfully...." + response, Toast.LENGTH_SHORT).show();
//
                Log.i("VOLLEY", shelve);

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

    //Method for Submit the Report to Server
    private void Update_shelve() throws JSONException {

        String url = "https://library.poxorfid.com/api/Inventory/UpdateBookLocation";
        JSONArray array = new JSONArray();
        JSONObject obj = new JSONObject();
//
//        obj.put("RFIDNo", rfid_number.getText().toString());
        for (int i = 0; i < data_models.size(); i++) {
            obj.put("RFIDNo", data_models.get(i).getRfidNumber());
            obj.put("RackNo", data_models.get(i).getRackNumber());
            obj.put("ShelfNo", data_models.get(i).getShelveNumber());
        }
        array.put(obj);
        RequestQueue queue = Volley.newRequestQueue(this);


        final String requestBody = array.toString();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(Tracking_form.this, "Data Updated Successfully....", Toast.LENGTH_SHORT).show();

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
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                System.out.println("Response Code Racking" + response.statusCode);
                return super.parseNetworkResponse(response);
            }

        };

        queue.add(stringRequest);
    }

    //Method for Fetch data Books data by Accession Number
    private void FetchData() throws JSONException {

        String url = "https://library.poxorfid.com/api/BooksInfo/FetchBookByAccessNo";

        JSONObject obj = new JSONObject();
//
//        obj.put("AccessNo", "B1228");
        obj.put("AccessNo", rfid_number.getText().toString());
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            String access_details = response.getString("AccessNo");
                            String Title = response.getString("Title");
                            String publisher = response.getString("Publisher");
                            String rfid = response.getString("RFIDNo");
                            String subject = response.getString("SubjectTitle");
                            String language = response.getString("Language");
                            String edition = response.getString("Edition");

//                           Set up Recyclerview
                            Item_Add_Item.add(new DataModel_Racking(rfid, Title, publisher));
                            dataModel_racking = new DataModel_Racking(rfid, publisher, Title);
                            adapter_racking = new Adapter_Racking(Item_Add_Item, getApplicationContext());
                            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                            recyclerView.setAdapter(adapter_racking);


                            data_models.add(new List_Data_Model(dataModel_racking.getRfid(), shelve_number_selected, rack_Number_Selected));
                            System.out.println("DATA VALUE OF LIST" + data_models);
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

    //Method for Clearing Data
    public void Clear() {
        Item_Add_Item.clear();
        adapter_racking = new Adapter_Racking(Item_Add_Item, getApplicationContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter_racking);
        rfid_number.setText("");
        list_shelve.clear();
        spinner_shelve_no.setEnabled(false);
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, list_shelve);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner_shelve_no.setAdapter(spinnerArrayAdapter);


    }

    //Method for Swipe left to delete
    private void enableSwipeToDeleteAndUndo() {

        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {


                final int position = viewHolder.getAdapterPosition();
                final DataModel_Racking item = adapter_racking.getData().get(position);
                adapter_racking.removeItem(position);


                Snackbar snackbar = Snackbar
                        .make(constraintLayout, "Item was removed from the list.", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        adapter_racking.restoreItem(item, position);
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

}
