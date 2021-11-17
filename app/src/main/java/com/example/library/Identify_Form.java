package com.example.library;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class Identify_Form extends AppCompatActivity {
    Button Search;
    EditText search_key;
    TextView LibraryItemType, BookAddedIn, BookCategory, ItemStatus, SubjectTitle, Language, Edition, Publisher, RFIDNo, AccessNo, Author, Title, YearOfPublication, EntryDate;
    //   ProgressDialog dialog = new ProgressDialog(this);
    ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identify_form);
        Search = findViewById(R.id.Search_rfid_button);
        LibraryItemType = findViewById(R.id.Library_item);
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
        dialog = new ProgressDialog(this);


        search_key = findViewById(R.id.Search_key);
        Search.setOnClickListener(new View.OnClickListener() {
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
            }
        });
    }

    private void FetchData() throws JSONException {

        String url = " https://library.poxorfid.com/api/BooksInfo/FetchBookByRFIDNo";

        JSONObject obj = new JSONObject();
//
        obj.put("RFIDNo", "E2801170000002142498B9A2");
//        obj.put("RFIDNo", search_key.getText().toString());
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
}