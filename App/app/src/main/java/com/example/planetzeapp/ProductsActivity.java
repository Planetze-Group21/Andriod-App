package com.example.planetzeapp;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.planetzeapp.model.ArticleAdapter;
import com.example.planetzeapp.model.ArticleDetails;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProductsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Context context;
    private RequestQueue requestQueue;
    private ArrayList<ArticleDetails> productDetailsArrayList = new ArrayList<>();
    private StringRequest stringRequest;

    private ArticleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  EdgeToEdge.enable(this);
        setContentView(R.layout.activity_products);
        recyclerView = findViewById(R.id.products_ui);
        context = ProductsActivity.this;
        adapter = new ArticleAdapter(context, productDetailsArrayList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        requestJsonData();
        if (savedInstanceState == null) {
            FooterFragment footerFragment = new FooterFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.footer_container, footerFragment); // You can use add() or replace()
            transaction.commit();
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.products_page), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void requestJsonData() {
        requestQueue = Volley.newRequestQueue(context);
        ProgressBar progressBar = findViewById(R.id.progress_bar_products);
        progressBar.setVisibility(View.VISIBLE);
        stringRequest = new StringRequest(Request.Method.GET, "https://dummyjson.com/c/18ec-538a-4dd9-8be7",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("JSONRequest", "Response received: " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("products");
                            Log.d("JSONRequest", "OnResponse");
                            progressBar.setVisibility(View.GONE);
                            fetchTheData(jsonArray);

                        } catch (JSONException e) {
                            Log.e("JSONRequest", "Error parsing JSON: " + e.getMessage());
                            throw new RuntimeException(e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                showToast("API call error");
            }
        });
        requestQueue.add(stringRequest);

    }

    private void fetchTheData(JSONArray jsonArray) throws JSONException {
        for(int i = 0;i<jsonArray.length();i++){
            try {
                JSONObject article = jsonArray.getJSONObject(i);
                productDetailsArrayList.add(new ArticleDetails(article.getString("title"),
                        article.getString("url"), article.getString("description"),
                        article.getString("image")));
            } catch (Exception e) {
                showToast("Product detail error.");
                throw new RuntimeException(e);
            }
        }
        Log.d("ProductListSize", "Size: " + productDetailsArrayList.size());
        adapter.notifyDataSetChanged();
    }

    public void showToast(String msg){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}