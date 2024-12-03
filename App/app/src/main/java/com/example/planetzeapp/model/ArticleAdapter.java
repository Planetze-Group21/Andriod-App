
package com.example.planetzeapp.model;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.planetzeapp.R;

import com.kwabenaberko.newsapilib.models.Article;

import java.util.ArrayList;
import java.util.List;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {

    private ArrayList<ArticleDetails> articleList;
    private Context context;

    public ArticleAdapter(Context context, ArrayList<ArticleDetails> articleList) {
        this.context = context;
        this.articleList = articleList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.default_article, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ArticleDetails article = articleList.get(position);
        holder.title.setText(article.getTitle());
        holder.description.setText(article.getDescription());
        Glide.with(context)
                .load(article.getImageUrl())
                .placeholder(R.drawable.planetze_bg_no_logo)
                .into(holder.image);
        //Glide.with(context).load(article.getUrl()).into(holder.image);
        holder.url.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(article.getUrl()));
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, description;

        ImageView image;
        CardView url;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            url = itemView.findViewById(R.id.card_view_button);
            image = itemView.findViewById(R.id.image);
        }
    }
}

