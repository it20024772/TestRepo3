package com.example.datapirates;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datapirates.model.Book;
import java.util.ArrayList;


public class readingListAdapter extends RecyclerView.Adapter<readingListAdapter.readingListViewHolder>{

    Context context;
    ArrayList<Book> list;
    private OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onDelete(String bookKey,String title, int position);
        void onUpdate(String bookKey, String title, String author, String genre, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public readingListAdapter(Context context, ArrayList<Book> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public readingListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_book, parent, false);
        return new readingListViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull readingListViewHolder holder, int position) {
        Book book = list.get(position);
        holder.bookKey.setText(book.getBookId());
        holder.title.setText(book.getTitle());
        holder.author.setText(book.getAuthor());
        holder.genre.setText(book.getGenre());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class readingListViewHolder extends RecyclerView.ViewHolder{

        TextView bookKey, title, author, genre;
        Button btnEdit, btnDelete;

        public readingListViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);

            bookKey = itemView.findViewById(R.id.bookKey);
            title = itemView.findViewById(R.id.item_title);
            author = itemView.findViewById(R.id.item_author);
            genre = itemView.findViewById(R.id.item_genre);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onDelete(bookKey.getText().toString(), title.getText().toString(), position);
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
                            String bookKey_txt = bookKey.getText().toString();
                            String title_txt = title.getText().toString();
                            String author_txt = author.getText().toString();
                            String genre_txt = genre.getText().toString();

                            listener.onUpdate(bookKey_txt, title_txt, author_txt, genre_txt, position);
                        }

                    }

                }
            });

        }
    }
}

