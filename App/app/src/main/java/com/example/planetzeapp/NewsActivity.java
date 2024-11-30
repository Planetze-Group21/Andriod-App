package com.example.planetzeapp;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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


public class NewsActivity extends AppCompatActivity {

    NewsApiClient newsApiClient = new NewsApiClient("67383d11a444403bb270e0b92d8db874");

    private RecyclerView recyclerView;
    private Context context;
    private RequestQueue requestQueue;
    private List<ArticleDetails> articleDetailsList = new ArrayList<>();
    private StringRequest stringRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_news);
        init();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.article_page), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        newsApiClient.getEverything(
                new EverythingRequest.Builder()
                        .q("environment OR \"climate change\" OR \"global warming\"") // OR "climate change" OR "global warming"
                        .language("en").build(),
                new NewsApiClient.ArticlesResponseCallback() {
                    @Override
                    public void onSuccess(ArticleResponse response) {
                        if (response.getArticles() == null || response.getArticles().isEmpty()) {
                            Log.e("API Response", "No articles found.");
                            return;
                        }
                        articleDetailsList.clear();
                        for (Article article : response.getArticles()) {
                            Log.d("API Response", "Fetched Article: " + article.getTitle());
                            articleDetailsList.add(new ArticleDetails(
                                    article.getTitle(), article.getUrl(),
                                    article.getDescription()
                            ));
                        }
                        Log.d("API Response", "Total Articles: " + articleDetailsList.size());
                        ArticleAdapter adapter = new ArticleAdapter(context, articleDetailsList);
                        recyclerView.setAdapter(adapter);
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        System.out.println(throwable.getMessage());
                    }
                }
        );
    }

    public void init(){
        recyclerView = findViewById(R.id.article_ui);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        context = NewsActivity.this;
    }
/*
    public void requestJsonData(){
        requestQueue = Volley.newRequestQueue(context);
        stringRequest = new StringRequest("https://newsapi.org/v2/everything?q=tesla&" +
                "from=2024-10-30&sortBy=publishedAt" +
                "&apiKey=67383d11a444403bb270e0b92d8db874", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.toString());
                    JSONArray jsonArray = jsonObject.getJSONArray()
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showToast("API call error");
            }
        });

    }

    public void showToast(String msg){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
    */
}