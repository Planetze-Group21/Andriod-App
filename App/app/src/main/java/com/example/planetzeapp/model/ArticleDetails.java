package com.example.planetzeapp.model;

import androidx.cardview.widget.CardView;

import com.kwabenaberko.newsapilib.NewsApiClient;
import com.kwabenaberko.newsapilib.models.Article;
import com.kwabenaberko.newsapilib.models.request.EverythingRequest;
import com.kwabenaberko.newsapilib.models.response.ArticleResponse;
public class ArticleDetails {
    private String title;
    private String url;

    private CardView cardView;
    private String description;

    private String imageUrl;

    public ArticleDetails(String title, String url, String description, String imageUrl){
        this.title = title;
        this.url = url;
        this.description = description;
        this.imageUrl = imageUrl;

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}
