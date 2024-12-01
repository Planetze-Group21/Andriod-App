/*
package com.example.planetzeapp;

import android.content.Context;
import android.database.DataSetObserver;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Toast;

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

public class NewsFragment extends Fragment {

    NewsApiClient newsApiClient = new NewsApiClient("67383d11a444403bb270e0b92d8db874");
    RecyclerView recyclerView;
    ArticleAdapter adapter;
    private RequestQueue requestQueue;
    NewsFragment context;

    private StringRequest stringRequest;
    ArrayList<ArticleDetails> articleDetailsArrayList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_news, null);
        //recyclerView = v.findViewById(R.id.article_ui);
        //adapter = new ArticleAdapter(requireContext(), articleDetailsArrayList);
        //recyclerView.setAdapter(adapter);
        //recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        //requestJsonData();
        //getNews();
        return v;
    }



    private void init(){
        recyclerView.findViewById(R.id.article_ui);
        context = NewsFragment.this;
    }

    private void requestJsonData() {
        requestQueue = Volley.newRequestQueue(context);
        stringRequest = new StringRequest("https://dummyjson.com/c/905d-9606-4d84-89a4", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("articles");
                    fetchTheData(jsonArray);

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
        //recyclerView.s

    }

    public void showToast(String msg){
        //Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    void getNews() {
        Log.d("NewsFragment", "getNews() called");

        articleDetailsArrayList.add(new ArticleDetails(
                "Dummy Title 1",
                "https://example.com/1",
                "This is a dummy description",
                "https://via.placeholder.com/150"
        ));
        articleDetailsArrayList.add(new ArticleDetails(
                "Dummy Title 2",
                "https://example.com/2",
                "This is another dummy description",
                "https://via.placeholder.com/150"
        ));

        adapter.notifyDataSetChanged();

        Log.d("NewsFragment", "before");
        try {
            newsApiClient.getEverything(new EverythingRequest.Builder().q("environment") // OR "climate change" OR "global warming"
                            .pageSize(10).language("en").build(),
                    new NewsApiClient.ArticlesResponseCallback() {
                        @Override
                        public void onSuccess(ArticleResponse response) {
                            Log.d("API Response", "Success.");
                            if (response.getArticles() == null || response.getArticles().isEmpty()) {
                                Log.e("API Response", "No articles found.");
                                return;
                            }
                            for (Article article : response.getArticles()) {
                                Log.d("API Response", "Fetched Article: " + article.getTitle());
                                articleDetailsArrayList.add(new ArticleDetails(
                                        article.getTitle(), article.getUrl(),
                                        article.getDescription(), article.getUrlToImage()
                                ));
                            }
                            Log.d("API Response", "Number of Articles: " + response.getArticles().size());
                            adapter.notifyDataSetChanged();

                        }

                        @Override
                        public void onFailure(Throwable throwable) {
                            Log.d("NewsFragment", "Failure called");
                            System.out.println(throwable.getMessage());
                        }
                    }
            );
        } catch (Exception e) {
            Log.e("API Call", "Exception occurred: " + e.getMessage(), e);
        }
        Log.d("NewsFragment", "Reached end");
    }
}
*/