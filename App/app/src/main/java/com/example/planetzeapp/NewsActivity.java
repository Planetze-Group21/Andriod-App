package com.example.planetzeapp;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.Context;
import android.content.Intent;
import android.database.Observable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.planetzeapp.model.ArticleAdapter;
import com.example.planetzeapp.model.ArticleDetails;
import com.kwabenaberko.newsapilib.NewsApiClient;
import com.kwabenaberko.newsapilib.models.Article;
import com.kwabenaberko.newsapilib.models.request.EverythingRequest;
import com.kwabenaberko.newsapilib.models.response.ArticleResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class NewsActivity extends AppCompatActivity {


    private RecyclerView recyclerView;
    private Context context;
    private RequestQueue requestQueue;
    private ArrayList<ArticleDetails> articleDetailsArrayList = new ArrayList<>();
    private StringRequest stringRequest;

    private ArticleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_news);
        recyclerView = findViewById(R.id.article_ui);
        context = NewsActivity.this;
        adapter = new ArticleAdapter(context, articleDetailsArrayList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        //loadDummyData();
        requestJsonData();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.article_page), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void loadDummyData() {
        articleDetailsArrayList.add(new ArticleDetails("Title 1", "https://example.com", "Description 1", "https://via.placeholder.com/150"));
        articleDetailsArrayList.add(new ArticleDetails("Title 2", "https://example.com", "Description 2", "https://via.placeholder.com/150"));
        adapter.notifyDataSetChanged();
    }
    private void init(){
        recyclerView = findViewById(R.id.article_ui);
        context = NewsActivity.this;
    }
    private void requestJsonData() {
        requestQueue = Volley.newRequestQueue(context);
        stringRequest = new StringRequest(Request.Method.GET, "https://dummyjson.com/c/1bfe-6556-4307-9e9b",
                new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("JSONRequest", "Response received: " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("articles");
                    Log.d("JSONRequest", "OnResponse");
                    fetchTheData(jsonArray);

                } catch (JSONException e) {
                    Log.e("JSONRequest", "Error parsing JSON: " + e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showToast("API call error");
            }
        });
        requestQueue.add(stringRequest);

    }

    private void fetchTheData(JSONArray jsonArray) throws JSONException {
        for(int i = 0;i<jsonArray.length();i++){
            try {
                JSONObject article = jsonArray.getJSONObject(i);
                articleDetailsArrayList.add(new ArticleDetails(article.getString("title"),
                        article.getString("url"), article.getString("description"),
                        article.getString("image")));
            } catch (Exception e) {
                showToast("Mobile detail error.");
                throw new RuntimeException(e);
            }
        }
        Log.d("ArticleListSize", "Size: " + articleDetailsArrayList.size());
        adapter.notifyDataSetChanged();
    }

    public void showToast(String msg){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

}
