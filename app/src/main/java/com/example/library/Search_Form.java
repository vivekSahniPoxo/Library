package com.example.library;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.ToneGenerator;
import android.net.SSLCertificateSocketFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.se.omapi.Session;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.speedata.libuhf.IUHFService;
import com.speedata.libuhf.UHFManager;
import com.speedata.libuhf.bean.SpdInventoryData;
import com.speedata.libuhf.interfaces.OnSpdInventoryListener;

import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;


public class Search_Form extends AppCompatActivity {
    Button add_accession_btn, Search_btn, retry_btn,NewBtn;
    EditText accession_no, search_data;
    RecyclerView recyclerView;
    List<Data_Model_Search> list_data_Recyclerview = new ArrayList<>();
    Adapter_list adapter_list;
    ProgressDialog dialog;
    CoordinatorLayout coordinate;
    IUHFService iuhfService;
    String RfidNo;
    String rfid;
    Button btnRfid;
    private ToneGenerator toneGenerator;
    TextView tvCount;
    TextView tvRfid;
    private boolean isSearchingStart = false;

    long lastTimeMillis;
    private int soundId;
    private SoundPool soundPool;
    boolean isInventoryRunning = false;

    int soundId1 = 0;

    private Handler handler;
    String rfidNo ;

    HashSet<String> scannedRfidList = new HashSet<>();
    @SuppressLint({"MissingInflatedId", "HandlerLeak"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_form);
        tvRfid = findViewById(R.id.tvRfid);

        //Binding Components
        coordinate = findViewById(R.id.coordinator);
        recyclerView = findViewById(R.id.recyclerView_Accession);
        Search_btn = findViewById(R.id.Search_b);
        search_data = findViewById(R.id.Search_Data);
        retry_btn = findViewById(R.id.Retry);
        add_accession_btn = findViewById(R.id.Add_accession);
        accession_no = findViewById(R.id.Accession_no);
        NewBtn = findViewById(R.id.New_accession);

        btnRfid = findViewById(R.id.btnTemp);
        tvCount = findViewById(R.id.tv_count);



        //Initialize of Progress Dialog
        dialog = new ProgressDialog(this);

        iuhfService = UHFManager.getUHFService(this);
        //iuhfService.openDev();
        //iuhfService.inventoryStart();
       // initSoundPool();

        //rfidNo = "E200470774F06026F2340111";


        handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                //rfidNo = "E200470774F06026F2340111";

                if (msg.what == 1) {
                    if (!TextUtils.isEmpty(rfidNo)) {
                        SpdInventoryData spdInventoryData = (SpdInventoryData) msg.obj;
                        String epc = spdInventoryData.getEpc();
                        Log.d("epc`1",epc);
                        if (epc.equals(rfidNo)) {
                            int rssi = Integer.parseInt(spdInventoryData.getRssi());
                            int i = -60;
                            int j = -40;
                            if (rssi > i) {
                                if (rssi > j) {
                                     Log.d("vvvvv",String.valueOf(rssi));
                                    soundPool.play(soundId1, 1f, 1f, 0, 0, 3f);
                                } else {
                                    Log.d("vvvvv",String.valueOf(rssi));
                                    soundPool.play(soundId1, 0.6f, 0.6f, 0, 0, 2f);
                                }
                            } else {
                                Log.d("vvvvv",String.valueOf(rssi));
                                soundPool.play(soundId1, 0.3f, 0.3f, 0, 0, 1f);
                            }

                        } else {
                            // Handle case when epc is not equal to rfidNo
                        }
                    }
                }
            }
        };

        //Method for Swipe to Delete
        enableSwipeToDeleteAndUndo();

        NewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search_data.setText("");
                accession_no.setText("");


            }
        });


        btnRfid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isInventoryRunning) {
                    // Start inventory service
                    iuhfService.setOnInventoryListener(new OnSpdInventoryListener() {
                        @Override
                        public void getInventoryData(SpdInventoryData var1) {
                            try {
                                if (!scannedRfidList.contains(var1.getEpc())) {
                                    scannedRfidList.add(var1.getEpc());
                                    tvCount.setText(String.valueOf("Tags Count "+scannedRfidList.size()));
                                }
                            } catch (Exception e){

                            }

                            rfid = var1.getEpc();
                            // Play sound and show toast

                            try {
                                long timeMillis = System.currentTimeMillis();
                                long l = timeMillis - lastTimeMillis;
                                if (l < 100) {
                                    return;
                                }
                                lastTimeMillis = System.currentTimeMillis();
                                soundPool.play(soundId, 1, 1, 0, 0, 1);
                                Log.d("RFFFF",rfid);
                                //Toast.makeText(Inventory_form.this,rfid,Toast.LENGTH_SHORT).show();
                                adapter_list.getFilter(var1.getEpc());
                                recyclerView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter_list.notifyDataSetChanged();
                                    }
                                });
                                //adapter_list.notifyDataSetChanged();
                                rfid ="";


                            } catch (Exception e){
                                Log.d("exception",e.toString());
                            }
                        }

                        @Override
                        public void onInventoryStatus(int i) {

                        }
                    });



                    try {

                        if (!scannedRfidList.contains(rfid)){
                            scannedRfidList.add(rfid);
                            tvCount.setText(scannedRfidList.size());
                        }

                    } catch (Exception e) {
                        Log.d("exception",e.toString());
                    }





                    // Start inventory service (you might need to call this method to start the service)
                    iuhfService.inventoryStart();

                    // Update button text and flag
                    btnRfid.setText("Stop");
                    isInventoryRunning = true;
                } else {
                    // Stop inventory service (you might need to call this method to stop the service)
                    // iuhfService.inventoryStop();

                    // Update button text and flag
                    btnRfid.setText("Start");
                    isInventoryRunning = false;
                    iuhfService.inventoryStop();
                }
            }
        });


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
                iuhfService.openDev();
                RfidNo = iuhfService.read_area(1, "2", "6", "00000000");
                search_data.setText(rfid);
                //if (search_data.length() > 0) {
                    String Search_value = search_data.getText().toString();
                    //search_data.setText("");
//                filter(Search_value);
                    adapter_list.getFilter(Search_value);
                    adapter_list.notifyDataSetChanged();
//                } else {
//                    search_data.setError("Enter Input...");
//                }
            }
        });

        add_accession_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (accession_no.length() > 0) {
                    hideKeyboard(v);
                    try {
                        FetchData();
                        dialog.show();
                        dialog.setMessage(getString(R.string.Dialog_Text));
                        dialog.setCancelable(false);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                   // accession_no.setError("Enter Input...");
                }
//                accession_no.setText("");


            }
        });


    }

    //Method for Search Data From Server using Accession Number
    private void FetchData() throws JSONException {
      // String url = "http://library.poxorfid.com/api/BooksInfo/FetchBookByAccessNo";
      //  String url = "http://192.168.0.101:4558/api/BooksInfo/FetchBookByAccessNo";
       //String url = "http://164.52.223.163:4558/api/BooksInfo/FetchBookByAccessNo";

        String url = "http://192.168.0.113:83/api/BooksInfo/FetchBookByAccessNo";
        //String url = "http://192.168.100.5:81/api/BooksInfo/FetchBookByAccessNo";
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
                            String rfid  =  response.getString("RFIDNo");

                            rfidNo = rfid;


                            list_data_Recyclerview.add(new Data_Model_Search(subject, language, edition, publisher, access_details, Author, Title,rfid));
                            adapter_list = new Adapter_list(list_data_Recyclerview, getApplicationContext());
                            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                            recyclerView.setAdapter(adapter_list);
                            dialog.dismiss();
//                           System.out.println("Search Response "+response.toString());
                            Log.e("response Search", response.toString());
                            accession_no.setText("");
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


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BUTTON_R2 || keyCode == KeyEvent.KEYCODE_F1 || keyCode == KeyEvent.KEYCODE_BUTTON_L1) {

                try {
                    if (rfidNo.isEmpty()){
                        Toast.makeText(getApplicationContext(),"No tag no found for search",Toast.LENGTH_SHORT).show();
                    } else {
                        if (!isSearchingStart) {
                            startSearching();

                            //isSearchingOn = true;
                            Toast.makeText(getApplicationContext(), rfidNo, Toast.LENGTH_SHORT).show();
                            iuhfService.setOnInventoryListener(new OnSpdInventoryListener() {
                                @Override
                                public void getInventoryData(SpdInventoryData var1) {
                                    handler.sendMessage(handler.obtainMessage(1, var1));
                                    Log.d("as3992_6C", "id is " + soundId);
                                }

                                @Override
                                public void onInventoryStatus(int status) {

                                }
                            });

                        } else {
                            stopSearching();
                            //isSearchingOn = false;
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }



            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            // startActivity(new Intent(this, MainActivity.class));
            finish();
            return true;
        }


        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.w("stop", "I'm stopping");
        try {
            if (soundPool != null) {
                soundPool.release();
            }

            if (iuhfService != null) {
                iuhfService.inventoryStop();
            }
        } catch (Exception e) {
            Log.d("eee", e.toString());
        }
    }


    private void startSearching() {



        try {
            initSoundPoolSearching();
            iuhfService = UHFManager.getUHFService(this);
            iuhfService.openDev();
            iuhfService.setAntennaPower(30);
            iuhfService.inventoryStart();
            //initSoundPool();
        } catch (Exception e) {
        }


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ImageView blinkingDot = findViewById(R.id.blinkingDot);
                blinkingDot.setVisibility(View.VISIBLE);
                AlphaAnimation blinkAnimation = new AlphaAnimation(1.0f, 0.0f);
                blinkAnimation.setDuration(500); // Adjust the duration as needed
                blinkAnimation.setRepeatMode(Animation.REVERSE);
                blinkAnimation.setRepeatCount(Animation.INFINITE);
                blinkingDot.startAnimation(blinkAnimation);

                isInventoryRunning = true;
                isSearchingStart = true;




            }
        });



    }



//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_F1) {
//            RfidNo = "";
//
//            try {
//                search_data.setVisibility(View.GONE);
//                toneGenerator = new ToneGenerator(AudioManager.STREAM_ALARM, ToneGenerator.MAX_VOLUME);
//                toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, 100);
//                vibratePhone(this);
//            } catch (Exception e){
//                Log.d("exception",e.toString());
//
//            }
//
//                try{
//                    iuhfService.openDev();
//                    RfidNo = iuhfService.read_area(1, "2", "6", "00000000");
//
//
//                    //search_data.setText(RfidNo);
//                    //String Search_value = search_data.getText().toString();
//
//                    adapter_list.getFilter(RfidNo);
//                    Log.d("ScannedRfid",RfidNo);
//                    Toast.makeText(this,RfidNo,Toast.LENGTH_LONG).show();
//                    RfidNo = "";
//
//
//
//
//
//            } catch (Exception e) {
//
//            }
//            return true;
//        } else {
//            if (keyCode == KeyEvent.KEYCODE_BACK){
//                startActivity(new Intent(this, MainActivity.class));
//                finish();
//            }
//        }
//        return super.onKeyUp(keyCode, event);
//    }

    public static void vibratePhone(Activity activity) {
        Vibrator vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(500);
        }
    }


    public void initSoundPool() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(1)
                    .setAudioAttributes(attributes)
                    .build();
        } else {
            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        }
        soundId = soundPool.load(this, R.raw.beep, 1);
    }

    private void initSoundPoolSearching() {
        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
        // Replace the path with the correct path for your audio file
        soundId = soundPool.load("/system/media/audio/ui/VideoRecord.ogg", 0);
        Log.w("as3992_6C", "id is " + soundId);
        // Use the resource ID to load the sound from resources
        soundId1 = soundPool.load(this, R.raw.scankey, 0);
    }






    private void stopInventoryService() {
        if (isInventoryRunning) {
            // Stop inventory service
            iuhfService.inventoryStop();
            isInventoryRunning = false;
        }
    }
    @Override
    public void onBackPressed() {
//        stopInventoryService();
        // Create an intent for the target activity
        Intent intent = new Intent(this, MainActivity.class);

        // Add flags to clear the activity stack and start a new task
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        // Start the target activity
        startActivity(intent);

        // Finish the current activity
        finish();
    }


    public void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }



    @SuppressLint("ResourceAsColor")
    private void stopSearching() {
        try {
            if (soundPool != null) {
                soundPool.release();
            }
        } catch (Exception e){
            Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
        }


        try {
            if (iuhfService != null) {
                iuhfService.inventoryStop();
                iuhfService.closeDev();
            }
        } catch (Exception e){
            Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
        }


        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                ImageView blinkingDot = findViewById(R.id.blinkingDot);
                blinkingDot.clearAnimation();
                blinkingDot.setVisibility(View.GONE);
                isInventoryRunning = false;
                isSearchingStart = false;
                adapter_list.notifyDataSetChanged();

            }
        });


    }




}