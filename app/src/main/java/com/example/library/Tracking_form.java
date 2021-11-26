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
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

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
    List<String> rack_no;
    String rack_Number_Selected, shelve_number_selected;
    ProgressDialog dialog;
    String shelve;
    List<String> list_shelve;
    List<DataModel_Racking> Item_Add_Item;
    RecyclerView recyclerView;
    Adapter_Racking adapter_racking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_form);
        //Component Initialization
        spinner_shelve_no = findViewById(R.id.Shelve_number_racking);
        rack_number_Spinner = findViewById(R.id.Rack_number_Racking);
        rfid_number = findViewById(R.id.Rfid_Number_Racking);
        Add_Racking = findViewById(R.id.Add_Button_Racking);
        Submit_racking = findViewById(R.id.Submit_racking);
        recyclerView = findViewById(R.id.tracking_recyclerview);
        Item_Add_Item = new ArrayList<>();

        //Listener on Add button
        Add_Racking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Item_Add_Item.add(new DataModel_Racking(rack_Number_Selected, shelve_number_selected, rfid_number.getText().toString()));
                SetUPRecylerview();
            }
        });


        list_shelve = new ArrayList<>();
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
        Submit_Restart = findViewById(R.id.Restart_racking);
        Submit_Restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Clear();
            }
        });


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


    }

    private void SetUPRecylerview() {
        adapter_racking = new Adapter_Racking(Item_Add_Item, getApplicationContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter_racking);
    }

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

    private void FetchShelve_No(String rack_Number_Selected) throws JSONException {
        String url = "https://library.poxorfid.com/api/Inventory/FetchShelves";
        JSONObject object = new JSONObject();
        object.put("RackNo", rack_Number_Selected);
        final String requestBody = object.toString();
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray array = new JSONArray(response);
                    for (int i = 0; i < array.length(); i++) {
                        shelve = array.getString(i);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Toast.makeText(Tracking_form.this, "Data Updated Successfully...." + response, Toast.LENGTH_SHORT).show();

                Log.i("VOLLEY", shelve);
                list_shelve.add(shelve);
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

    private void Update_shelve() throws JSONException {

        String url = "https://library.poxorfid.com/api/Inventory/UpdateBookLocation";
        JSONArray array = new JSONArray();
        JSONObject obj = new JSONObject();
//
//        obj.put("RFIDNo", rfid_number.getText().toString());
        obj.put("RFIDNo", rfid_number.getText().toString());
        obj.put("RackNo", rack_Number_Selected);
        obj.put("ShelfNo", shelve_number_selected);
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
        };

        queue.add(stringRequest);
    }

    public void Clear() {
        Item_Add_Item.clear();
        adapter_racking = new Adapter_Racking(Item_Add_Item, getApplicationContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter_racking);
        rfid_number.setText("");


    }
}
