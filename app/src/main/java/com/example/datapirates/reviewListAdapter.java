package com.example.datapirates;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datapirates.model.Review;

import java.util.ArrayList;

public class reviewListAdapter extends RecyclerView.Adapter<reviewListAdapter.reviewListViewHolder>{

    Context context;
    ArrayList<Review> list;
    private OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onDelete(String reviewKey,String bookName, int position);
        void onUpdate(String reviewKey, String bookName, String description, String rating, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public reviewListAdapter(Context context, ArrayList<Review> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public reviewListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_review, parent, false);
        return new reviewListViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull reviewListViewHolder holder, int position) {
        Review review = list.get(position);
        holder.reviewKey.setText(review.getReviewId());
        holder.bookName.setText(review.getBookName());
        holder.description.setText(review.getDescription());
        holder.rating.setRating(Float.parseFloat(review.getRating()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class reviewListViewHolder extends RecyclerView.ViewHolder{

        TextView reviewKey, bookName, description;
        RatingBar rating;
        Button btnEdit, btnDelete;

        public reviewListViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);

            reviewKey = itemView.findViewById(R.id.reviewKey);
            bookName = itemView.findViewById(R.id.review_bookname);
            description = itemView.findViewById(R.id.review_description);
            rating = itemView.findViewById(R.id.reviewRatingBar);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onDelete(reviewKey.getText().toString(), bookName.getText().toString(), position);
                        }

                    }

                }
            });

            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            String reviewKey_txt = reviewKey.getText().toString();
                            String bookName_txt = bookName.getText().toString();
                            String description_txt = description.getText().toString();
                            String rating_txt = String.valueOf(rating.getRating());

                            listener.onUpdate(reviewKey_txt, bookName_txt, description_txt, rating_txt, position);
                        }

                    }

                }
            });

        }
    }
}


