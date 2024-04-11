package com.example.library;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.speedata.libuhf.IUHFService;
import com.speedata.libuhf.UHFManager;
import com.speedata.libuhf.bean.SpdReadData;
import com.speedata.libuhf.interfaces.OnSpdReadListener;
import com.speedata.libuhf.utils.ErrorStatus;
import com.speedata.libuhf.utils.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class Identify_Form extends AppCompatActivity {
    Button Search, NEW_data, Retry;
    EditText search_key;
    TextView LibraryItemType, BookAddedIn, BookCategory, ItemStatus, SubjectTitle, Language, Edition, Publisher, RFIDNo, AccessNo, Author, Title, YearOfPublication, EntryDate,rfidTagByTriggerButton;
    ProgressDialog dialog;
    IUHFService iuhfService;
    String tagno;
    CardView TapToScanButton;
    ImageView backButton;
    boolean isInventoryRunning = false;
    private ToneGenerator toneGenerator;
    Handler handlerr;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identify_form);
        //stopInventoryService();
        handlerr = new Handler();
        //Binding Components
        search_key = findViewById(R.id.Search_key);
        Retry = findViewById(R.id.Retry);
        Search = findViewById(R.id.Search_rfid_button);
        LibraryItemType = findViewById(R.id.Library_item);
        NEW_data = findViewById(R.id.New_accession);
        BookAddedIn = findViewById(R.id.Book_Add);
        BookCategory = findViewById(R.id.BookCategory);
        ItemStatus = findViewById(R.id.Item_status);
        SubjectTitle = findViewById(R.id.Subject_t);
        Language = findViewById(R.id.Language);
        Edition = findViewById(R.id.Edition);
        Publisher = findViewById(R.id.Publisher);
        RFIDNo = findViewById(R.id.RFID_NO);
        AccessNo = findViewById(R.id.Access_No);
        Author = findViewById(R.id.Authorname);
        Title = findViewById(R.id.Booktitle);
        YearOfPublication = findViewById(R.id.YearOfPublication);
        EntryDate = findViewById(R.id.EntryDate);
        TapToScanButton = findViewById(R.id.button);
        rfidTagByTriggerButton = findViewById(R.id.tv_rfid_no_trigger);
        backButton = findViewById(R.id.im_back_button);

        //Initialize of Dialog Box
        dialog = new ProgressDialog(this);

//        iuhfService = UHFManager.getUHFService(this);
//        iuhfService.openDev();
//        iuhfService.inventoryStart();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent = new Intent(Identify_Form.this, MainActivity.class);
                startActivity(intent);
            }
        });

        //Listeners
        Retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClearData();
            }
        });

        NEW_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClearData();
            }
        });
        Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (search_key.length() > 0) {
                    try {
                        FetchData(tagno);
                        dialog.show();
                        dialog.setMessage(getString(R.string.Dialog_Text));
                        dialog.setCancelable(false);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
//                } else {
//                    search_key.setError("Please Enter Input....");
//                }
            }
        });



        TapToScanButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {



                iuhfService.setOnReadListener(new OnSpdReadListener() {
                    @Override
                    public void getReadData(SpdReadData var1) {
                        StringBuilder stringBuilder = new StringBuilder();
                        byte[] epcData = var1.getEPCData();
                        String hexString = StringUtils.byteToHexString(epcData, var1.getEPCLen());
                        if (!TextUtils.isEmpty(hexString)) {
                            stringBuilder.append("EPC：").append(hexString).append("\n");
                        } else if (var1.getStatus() == 0) {
                            byte[] readData = var1.getReadData();
                            String readHexString = StringUtils.byteToHexString(readData, var1.getDataLen());
                            stringBuilder.append("ReadData:").append(readHexString).append("\n");
                            Toast.makeText(Identify_Form.this, readHexString, Toast.LENGTH_SHORT).show();

                            tagno = readHexString;
                            search_key.setText(tagno);
                            rfidTagByTriggerButton.setText(search_key.getText().toString());
                            try {
                                dialog.show();
                                dialog.setMessage(getString(R.string.Dialog_Text));
                                dialog.setCancelable(false);
                                FetchData(tagno);
                            } catch (JSONException e) {
                                Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            stringBuilder.append(getResources().getString(R.string.read_fail))
                                    .append(":").append(ErrorStatus.getErrorStatus(Identify_Form.this, var1.getStatus()))
                                    .append("\n");
                            handlerr.sendMessage(handlerr.obtainMessage(1, stringBuilder.toString()));
                        }
                    }
                });

                Integer readArea = iuhfService.readArea(1, 2, 6, "00000000");
                if (readArea != null && readArea != 0) {
                    String err = getResources().getString(R.string.read_fail) + ":" +
                            ErrorStatus.getErrorStatus(getApplicationContext(), readArea) + "\n";
                    handlerr.sendMessage(handlerr.obtainMessage(1, err));
                }







//                iuhfService.setOnReadListener(new OnSpdReadListener() {
//
//                    @Override
//                    public void getReadData(SpdReadData var1) {
//
//                        StringBuilder stringBuilder = new StringBuilder();
//                        byte[] epcData = var1.getReadData();
//                        String hexString = StringUtils.byteToHexString(epcData, var1.getDataLen());
//                        if (!TextUtils.isEmpty(hexString)) {
//                            stringBuilder.append("EPC：").append(hexString).append("\n");
//                        } else if (var1.getStatus() == 0) {
//                            byte[] readData = var1.getReadData();
//                            String readHexString = StringUtils.byteToHexString(readData, var1.getDataLen());
//                            stringBuilder.append("ReadData:").append(readHexString).append("\n");
//                            Toast.makeText(Identify_Form.this, readHexString, Toast.LENGTH_SHORT).show();
//                            tagno = readHexString;
//                            search_key.setText(tagno);
//                            rfidTagByTriggerButton.setText(tagno);
//                            rfidTagByTriggerButton.setText(search_key.getText().toString());
//                            try {
//                                FetchData(tagno);
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                            dialog.show();
//                            dialog.setMessage(getString(R.string.Dialog_Text));
//                            dialog.setCancelable(false);
//                        } else {
//                            int readArea = iuhfService.readArea(1, 2, 6, "00000000");
//                            if (readArea != 0) {
//                                stringBuilder.append(getResources().getString(R.string.read_fail)).append(":").append(ErrorStatus.getErrorStatus(Identify_Form.this, var1.getStatus())).append("\n");
//                                handlerr.sendMessage(handlerr.obtainMessage(1, stringBuilder.toString()));
//                            }
//                        }
//                    }
//
//
//                });


                //iuhfService.openDev();
//                tagno = iuhfService.read_area(1, "2", "6", "00000000");
                //rfidTagByTriggerButton.setText(tagno);
                 // Log.d("TagNo",tagno);
//                try {
//                    FetchData();
//                    dialog.show();
//                    dialog.setMessage(getString(R.string.Dialog_Text));
//                    dialog.setCancelable(false);
//                   // iuhfService.inventoryStop();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }



            }
        });



    }



    //Method For Clear Components
    private void ClearData() {

        LibraryItemType.setText("");
        BookAddedIn.setText("");
        BookCategory.setText("");
        ItemStatus.setText("");
        SubjectTitle.setText("");
        Language.setText("");
        Edition.setText("");
        Publisher.setText("");
        RFIDNo.setText("");
        AccessNo.setText("");
        Author.setText("");
        Title.setText("");
        YearOfPublication.setText("");
        EntryDate.setText("");
        search_key.setText("");


    }

    //Method For Fetching Data from Server
    private void FetchData(String rfidNo) throws JSONException {
      // String url =  "https://library.poxorfid.com/api/BooksInfo/FetchBookByRFIDNo";
        String url =  "http://164.52.223.163:4558/api/BooksInfo/FetchBookByRFIDNo";

       // String url = "http://192.168.0.113:83/api/BooksInfo/FetchBookByRFIDNo";

        //String url = "http://192.168.100.5:81/api/BooksInfo/FetchBookByRFIDNo";

        JSONObject obj = new JSONObject();
//
        //obj.put("AccessNo", search_key.getText().toString());
//         obj.put("RFIDNo", search_key.getText().toString());
        obj.put("RFIDNo", rfidNo);
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            String LibraryItemType1 = response.getString("LibraryItemType");
                            String BookAddedIn1 = response.getString("BookAddedIn");
                            String BookCategory1 = response.getString("BookCategory");
                            String ItemStatus1 = response.getString("ItemStatus");
                            String SubjectTitle1 = response.getString("SubjectTitle");
                            String Language1 = response.getString("Language");
                            String Edition1 = response.getString("Edition");
                            String Publisher1 = response.getString("Publisher");
                            String RFIDNo1 = response.getString("RFIDNo");
                            String AccessNo1 = response.getString("AccessNo");
                            String Author1 = response.getString("Author");
                            String Title1 = response.getString("Title");
                            String YearOfPublication1 = response.getString("YearOfPublication");
                            String EntryDate1 = response.getString("EntryDate");
                            LibraryItemType.setText(LibraryItemType1);
                            BookAddedIn.setText(BookAddedIn1);
                            BookCategory.setText(BookCategory1);
                            ItemStatus.setText(ItemStatus1);
                            SubjectTitle.setText(SubjectTitle1);
                            Language.setText(Language1);
                            Edition.setText(Edition1);
                            Publisher.setText(Publisher1);
                            RFIDNo.setText(RFIDNo1);
                            AccessNo.setText(AccessNo1);
                            Author.setText(Author1);
                            Title.setText(Title1);
                            YearOfPublication.setText(YearOfPublication1);
                            EntryDate.setText(EntryDate1);
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




//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (event.getAction() == KeyEvent.KEYCODE_F1) {
//            search_key.setFocusable(true);
//            search_key.setFocusableInTouchMode(true);
//            iuhfService = UHFManager.getUHFService(this);
//            iuhfService.openDev();
//            iuhfService.inventoryStart();
//            tagno = iuhfService.read_area(1, "2", "6", "00000000").toString();
//            rfidTagByTriggerButton.setText(tagno);
//            Log.d("tag",tagno);
//
//            try {
//                FetchData();
//                dialog.show();
//                dialog.setMessage(getString(R.string.Dialog_Text));
//                dialog.setCancelable(false);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//            return true;
//        } else {
//            if (keyCode == KeyEvent.KEYCODE_BACK){
//                startActivity(new Intent(Identify_Form.this, MainActivity.class));
//                finish();
//            }
//        }
//        return super.onKeyUp(keyCode, event);
//    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(Identify_Form.this, MainActivity.class));
        finish();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BUTTON_R2 || keyCode==131) {
            //stopInventoryService();
//
//            try {
//                toneGenerator = new ToneGenerator(AudioManager.STREAM_ALARM, ToneGenerator.MAX_VOLUME);
//                toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, 100);
//                VibrationUtil.vibratePhone(this);
//            } catch (Exception e){
//                Log.d("exception",e.toString());
//
//            }

//            search_key.setFocusable(true);
//            search_key.setFocusableInTouchMode(true);
//            iuhfService = UHFManager.getUHFService(this);
//            iuhfService.openDev();
//            iuhfService.inventoryStart();







            iuhfService.setOnReadListener(new OnSpdReadListener() {
                @Override
                public void getReadData(SpdReadData var1) {
                    try {
                        if (var1 != null) {
                            StringBuilder stringBuilder = new StringBuilder();
                            byte[] epcData = var1.getEPCData();
                            String hexString = StringUtils.byteToHexString(epcData, var1.getEPCLen());
                            if (!TextUtils.isEmpty(hexString)) {
                                stringBuilder.append("EPC：").append(hexString).append("\n");
                            } else if (var1.getStatus() == 0) {
                                byte[] readData = var1.getReadData();
                                String readHexString = StringUtils.byteToHexString(readData, var1.getDataLen());
                                stringBuilder.append("ReadData:").append(readHexString).append("\n");
                                Toast.makeText(Identify_Form.this, readHexString, Toast.LENGTH_SHORT).show();
                                tagno = readHexString;
                                search_key.setText(tagno);
                                rfidTagByTriggerButton.setText(search_key.getText().toString());
                                dialog.show();
                                dialog.setMessage(getString(R.string.Dialog_Text));
                                dialog.setCancelable(false);
                                FetchData(tagno);
                            } else {
                                stringBuilder.append(getResources().getString(R.string.read_fail))
                                        .append(":").append(ErrorStatus.getErrorStatus(Identify_Form.this, var1.getStatus()))
                                        .append("\n");
                                handlerr.sendMessage(handlerr.obtainMessage(1, stringBuilder.toString()));
                            }
                        } else {
                            Log.d("OnReadListener", "Received null SpdReadData");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Received null SpdReadData", Toast.LENGTH_SHORT).show();

                                }});
                                }
                    } catch (Exception e) {
                        Log.d("OnReadListener", "Error in getReadData: " + e.getMessage());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                }
            });

            Integer readArea = iuhfService.readArea(1, 2, 6, "00000000");
            if (readArea != null && readArea != 0) {
                String err = getResources().getString(R.string.read_fail) + ":" +
                        ErrorStatus.getErrorStatus(this, readArea) + "\n";
                handlerr.sendMessage(handlerr.obtainMessage(1, err));
            }




            ///tagno = iuhfService.read_area(1, "2", "6", "00000000").toString();
           // search_key.setText(tagno);
//            rfidTagByTriggerButton.setText(search_key.getText().toString());




//            Toast.makeText(this,tagno,Toast.LENGTH_SHORT).show();
//            Log.d("tag",tagno);

//            try {
//                FetchData();
//                dialog.show();
//                dialog.setMessage(getString(R.string.Dialog_Text));
//                dialog.setCancelable(false);
//               // iuhfService.inventoryStop();
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
            return true;
        } else {
            if (keyCode == KeyEvent.KEYCODE_BACK){
                startActivity(new Intent(Identify_Form.this, MainActivity.class));
                finish();
            }
        }
        return super.onKeyUp(keyCode, event);
    }


//    @Override
//    protected void onPause() {
//        super.onPause();
//        stopInventoryService();
//    }

    @Override
    protected void onStop() {
        super.onStop();
        stopInventoryService();
    }

    private void stopInventoryService() {
//        if (isInventoryRunning) {
            // Stop inventory service
           // iuhfService.inventoryStop();
            iuhfService.closeDev();
           // isInventoryRunning = false;
       // }
    }


    @Override
    protected void onStart() {
        super.onStart();
        initializeUHF();
    }

    @SuppressLint("ResourceAsColor")
    private void initializeUHF() {
        try {
            iuhfService = UHFManager.getUHFService(this);
            iuhfService.openDev();
            iuhfService.setAntennaPower(30);

            // iuhfService.setFrequency(2);

        } catch (Exception e) {
        Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
        }
    }












}