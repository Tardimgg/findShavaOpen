package com.example.findshava.shortUserView.searchPlace;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.findshava.R;

import java.util.ArrayList;
import java.util.List;

public class SearchAnswerAdapter extends RecyclerView.Adapter<SearchAnswerAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private List<String> allSearchAnswer;
    private List<String> searchAnswer;
    private View.OnClickListener onClickListener;

    public SearchAnswerAdapter(Context context, ArrayList<String> allSearchAnswer) {
        this.allSearchAnswer = allSearchAnswer;
        this.searchAnswer = (ArrayList<String>) allSearchAnswer.clone();
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public SearchAnswerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.search_answer_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.text.setText(searchAnswer.get(position));
        holder.itemView.setOnClickListener(onClickListener);

    }

    @Override
    public int getItemCount() {
        return this.searchAnswer.size();
    }


    public void filter(String query) {
        this.searchAnswer.clear();
        boolean all = false;
        if (query == null) {
            all = true;
            query = "_";
        }
        query = query.toLowerCase();
        for (String value : this.allSearchAnswer) {
            if (all || value.toLowerCase().contains(query)) {
                this.searchAnswer.add(value);
            }
        }
        this.notifyDataSetChanged();
    }

    void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    View.OnClickListener getOnClickListener() {
        return onClickListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView text;

        ViewHolder(View view) {
            super(view);
            this.text = view.findViewById(R.id.text_search_answer);
        }
    }
}
