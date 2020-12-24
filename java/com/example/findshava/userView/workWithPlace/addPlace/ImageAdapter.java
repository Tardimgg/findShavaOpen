package com.example.findshava.userView.workWithPlace.addPlace;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.findshava.R;
import com.example.findshava.customClass.SerializablePair;
import com.example.findshava.customView.SelectedImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private List<SerializablePair<Integer, String>> images;
    private View.OnTouchListener touchImageListener;
    private Set<String> startSelectedName;

    public ImageAdapter(ArrayList<SerializablePair<Integer, String>> images, Set<String> startSelectedName, View.OnTouchListener touchImageListener, Context context) {
        this.images = images;
        this.touchImageListener = touchImageListener;
        this.startSelectedName = startSelectedName;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.properties_image_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.image.setImageResource(images.get(position).getFirst(), images.get(position).getSecond());
        holder.image.setOnTouchListener(this.touchImageListener);

        if (this.startSelectedName != null && this.startSelectedName.contains(holder.image.getName())) {
            this.touchImageListener.onTouch(holder.image, null);
            holder.image.setSelected(true);
        } else {
            holder.image.setSelected(false);
        }

    }

    @Override
    public int getItemCount() {
        return this.images.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        SelectedImageView image;

        ViewHolder(View view) {
            super(view);
            this.image = view.findViewById(R.id.image);
        }
    }
}


