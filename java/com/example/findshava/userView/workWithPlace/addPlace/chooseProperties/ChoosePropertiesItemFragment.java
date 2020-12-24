package com.example.findshava.userView.workWithPlace.addPlace.chooseProperties;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.findshava.R;
import com.example.findshava.converter.Converter;
import com.example.findshava.customClass.SerializablePair;
import com.example.findshava.customView.SelectedImageView;
import com.example.findshava.isReady.IsReady;
import com.example.findshava.userView.workWithPlace.addPlace.ImageAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ChoosePropertiesItemFragment extends Fragment implements IsReady, View.OnTouchListener {

    private IsReadyListener isReadyListener;

    private Map<String, Boolean> selectedImages;

    private ArrayList<SerializablePair<Integer, String>> images;

    private Set<String> startProperties;

    private View view;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (this.isReadyListener != null) {
            this.isReadyListener.ready();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);
        this.view = inflater.inflate(R.layout.choose_properties_item, null);
        this.selectedImages = new HashMap<>();
        RecyclerView gridView = this.view.findViewById(R.id.properties_images);
        gridView.setAdapter(new ImageAdapter(this.images, this.startProperties, this, getContext()));
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        Runnable setSpanCount = () -> {
            int width = gridView.getWidth();
            int result = Converter.convertingPxToDp(getResources(), width) / 120;
            if (result > 0) {
                gridLayoutManager.setSpanCount(result);
            }
        };
        setSpanCount.run();
        gridView.setLayoutManager(gridLayoutManager);
        gridView.getViewTreeObserver().addOnGlobalLayoutListener(setSpanCount::run);
        return this.view;
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (this.selectedImages == null) {
            this.selectedImages = new HashMap<>();
        }
        SelectedImageView selectedImageView = (SelectedImageView) v;
        this.selectedImages.put(selectedImageView.getName(), !selectedImageView.isSelected());
        return false;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.images = (ArrayList<SerializablePair<Integer, String>>) getArguments().getSerializable("images");
        }
    }

    void setProperties(Set<String> properties) {
        this.startProperties = properties;
    }

    List<String> getProperties() {
        List<String> answer = new ArrayList<>();
        if (this.selectedImages != null) {
            for (Map.Entry<String, Boolean> entry : this.selectedImages.entrySet()) {
                if (entry.getValue()) {
                    answer.add(entry.getKey());
                } else {
                    this.startProperties.remove(entry.getKey());
                }
            }
        }
        if (this.startProperties != null) {
            for (String value : this.startProperties) {
                if (!answer.contains(value)) {
                    answer.add(value);
                }
            }
        }
        return answer;
    }


    static ChoosePropertiesItemFragment newInstance(ArrayList<SerializablePair<Integer, String>> images) {
        Bundle args = new Bundle();
        args.putSerializable("images", images);
        ChoosePropertiesItemFragment answer = new ChoosePropertiesItemFragment();
        answer.setArguments(args);
        return answer;
    }

    @Override
    public void setIsReadyListener(IsReadyListener isReadyListener) {
        if (getView() != null) {
            isReadyListener.ready();
        } else {
            this.isReadyListener = isReadyListener;
        }
    }


}
