package com.example.findshava.shortUserView;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.findshava.R;
import com.example.findshava.converter.Converter;
import com.example.findshava.customClass.Coordinates;
import com.example.findshava.customClass.internet.Geocoder;
import com.example.findshava.isReady.IsReady;
import com.example.findshava.map.Map;

import java.io.Serializable;

public class ShortInfoPlace extends Fragment implements IsReady {

    private LinearLayout shortInfo;
    private ClickAddPlaceListener dialogWithAddPlace;
    private boolean isHavingTask = false;
    private Coordinates coordinates;
    private IsReadyListener isReadyListener;
    private Map.IsSuccessfullyAddPlaceListener isSuccessfullyAddPlaceListener;
    public static final String TAG = "ShortInfoPlace";

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (isHavingTask) {
            startWork(this.coordinates);
        }
        if (this.isReadyListener != null) {
            this.isReadyListener.ready();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.shortInfo = (LinearLayout) inflater.inflate(R.layout.short_info, null);
        return this.shortInfo;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.dialogWithAddPlace = (ClickAddPlaceListener) getArguments().getSerializable("dialogWithAddPlace");
        }
    }

    public void setIsReadyListener(IsReadyListener isReadyListener) {
        if (getView() != null) {
            isReadyListener.ready();
        } else {
            this.isReadyListener = isReadyListener;
        }
    }

    public static ShortInfoPlace newInstance(ClickAddPlaceListener dialogWithAddPlace) {
        Bundle args = new Bundle();
        args.putSerializable("dialogWithAddPlace", dialogWithAddPlace);
        ShortInfoPlace answer = new ShortInfoPlace();
        answer.setArguments(args);
        return answer;
    }

    public void addTaskStartWork(Coordinates coordinates, Map.IsSuccessfullyAddPlaceListener isSuccessfullyAddPlaceListener) {
        this.coordinates = coordinates;
        this.isSuccessfullyAddPlaceListener = isSuccessfullyAddPlaceListener;
        if (getView() != null) {
            startWork(coordinates);
        } else {
            isHavingTask = true;
        }
    }

    private void startWork(Coordinates coordinates) {
        Log.i("ShortInfoPlace", "start work");
        isHavingTask = false;
        prepareView();
        final TextView location = shortInfo.findViewById(R.id.location);
        String textLocation = new StringBuilder(Double.toString(coordinates.getLatitude()))
                .append("; ")
                .append((coordinates.getLongitude())).toString();
        location.setText(textLocation);
        Handler handler = new Handler();
        new Thread(new Runnable() {

            private String namePlace;

            @Override
            public void run() {
                this.namePlace = Geocoder.getPlace(coordinates);
                if (this.namePlace == null) {
                    this.namePlace = "\nПроверьте интернет-подключение";
                } else {
                    this.namePlace = Geocoder.parsePlace(this.namePlace);
                }
                handler.post(changeTheView);
            }

            Runnable changeTheView = () -> {
                location.setText(new StringBuilder(textLocation)
                        .append("\n")
                        .append(this.namePlace));
                if (shortInfo.getChildCount() > 1) {
                    shortInfo.removeViewAt(1);
                }
                Button portalToAddInfo = new Button(getContext());
                portalToAddInfo.setText("Добавить место");
                portalToAddInfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ShortInfoPlace.this.dialogWithAddPlace != null) {
                            ShortInfoPlace.this.dialogWithAddPlace.showAddPlace(coordinates, ShortInfoPlace.this.isSuccessfullyAddPlaceListener);
                        }
                    }
                });
                shortInfo.addView(portalToAddInfo);
            };

        }).start();

    }

    private void prepareView() {
        shortInfo.removeViewAt(1);
        ProgressBar progressBar = new ProgressBar(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int dp = Converter.convertingDpToPx(getResources(), 10);
        params.setMargins(dp, dp, dp, dp);
        progressBar.setLayoutParams(params);
        shortInfo.addView(progressBar);
    }

    public interface ClickAddPlaceListener extends Serializable {
        void showAddPlace(Coordinates coordinates, Map.IsSuccessfullyAddPlaceListener isSuccessfullyAddPlaceListener);

    }
}
