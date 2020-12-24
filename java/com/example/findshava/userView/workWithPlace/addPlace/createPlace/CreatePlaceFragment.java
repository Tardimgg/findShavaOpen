package com.example.findshava.userView.workWithPlace.addPlace.createPlace;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.findshava.FinishedWorkListener.FinishedWorkListener;
import com.example.findshava.R;
import com.example.findshava.customClass.Coordinates;
import com.example.findshava.customClass.internet.CurrentTime;
import com.example.findshava.customSpannableSpan.RoundedBackgroundSpan;
import com.example.findshava.dataBase.PlaceLoader;
import com.example.findshava.isReady.IsReady;
import com.example.findshava.map.Map;
import com.example.findshava.shortUserView.searchPlace.SearchAnswerAdapter;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class CreatePlaceFragment extends Fragment implements IsReady, View.OnClickListener {

    private View view;
    private IsReadyListener isReadyListener;
    private Coordinates coordinates;
    private Map.IsSuccessfullyAddPlaceListener dialogWithMap;
    private ShowChoosePropertiesListener showChoosePropertiesListener;

    private ArrayDeque<Runnable> tasks = new ArrayDeque<>();

    private List<String> infoProperties;

    private boolean newPlace;
    private ArrayList<String> listAnswer;
    private RatingBar ratingBar;
    private EditText editProperties;
    private EditText description;

    private FinishedWorkListener finishedWorkListener;

    public static final String TAG = "createPlaceFragment";

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        while (tasks.size() > 0) {
            tasks.pop().run();
        }
        if (this.isReadyListener != null) {
            this.isReadyListener.ready();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.create_place, null);

        this.ratingBar = this.view.findViewById(R.id.rating);
        this.ratingBar.setIsIndicator(false);
        this.ratingBar.setScaleX(1.5f);
        this.ratingBar.setScaleY(1.5f);
        this.ratingBar.setStepSize(1);

        this.view.findViewById(R.id.addMarkerButton).setOnClickListener(this);

        ((TextView) this.view.findViewById(R.id.date)).setText(CurrentTime.getDeviceTime("dd.MM.yyyy", Locale.ENGLISH));

        this.view.findViewById(R.id.date).animate().translationX(100).setDuration(0);

        this.description = this.view.findViewById(R.id.description);

        this.editProperties = this.view.findViewById(R.id.edit_properties);
        this.editProperties.setShowSoftInputOnFocus(false);
        this.editProperties.setOnFocusChangeListener((View v, boolean hasFocus) -> {
            if (hasFocus && getContext() != null) {
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(this.description.getWindowToken(), 0);
                }
                showChoosePropertiesListener.show(CreatePlaceFragment.this.infoProperties);
            }
        });

        this.listAnswer = new ArrayList<>(Arrays.asList("Шаверма", "Чай", "Пироженные", "Шаверма в сырном", "Чипсы", "Ресторан"));
        SearchAnswerAdapter adapter = new SearchAnswerAdapter(getContext(), this.listAnswer);
        adapter.filter("_");

        return this.view;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.dialogWithMap = (Map.IsSuccessfullyAddPlaceListener) getArguments().getSerializable("dialogWithMap");
            this.showChoosePropertiesListener = (ShowChoosePropertiesListener) getArguments().getSerializable("showChoosePropertiesListener");
        }
    }


    public static CreatePlaceFragment newInstance(Map.IsSuccessfullyAddPlaceListener dialogWithMap, ShowChoosePropertiesListener showChoosePropertiesListener) {
        Bundle args = new Bundle();
        args.putSerializable("dialogWithMap", dialogWithMap);
        args.putSerializable("showChoosePropertiesListener", showChoosePropertiesListener);
        CreatePlaceFragment answer = new CreatePlaceFragment();
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

    public void setInfoProperties(@Nullable List<String> properties) {
        this.infoProperties = properties;
        Runnable setInfo = () -> {
            if (this.infoProperties != null) {
                this.editProperties.setText(this.infoProperties.toString().replaceAll("\\[|\\]|", ""));
                int start = 0;
                for (String value : this.infoProperties) {
                    selectedText(this.editProperties.getText(), start, start + value.length());
                    start += value.length() + 2;
                }
            } else {
                this.editProperties.setText("");
            }
        };
        if (getView() != null) {
            setInfo.run();
        } else {
            this.tasks.add(setInfo);
        }

    }

    private void selectedText(Spannable text, int start, int stop) {
        text.setSpan(new RoundedBackgroundSpan(0xFFBA6C1E, Color.WHITE, 15, -5), start, stop, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        text.setSpan(new RelativeSizeSpan(0.8f), start, stop, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

    }

    public void addTaskPrepare(Coordinates coordinates, boolean newPlace) {
        this.coordinates = coordinates;
        this.newPlace = newPlace;
    }

    public void exit() {
        setInfoProperties(null);
        this.ratingBar.setRating(0);
        this.description.setText("");
        this.description.clearFocus();
        if (getContext() != null) {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(this.description.getWindowToken(), 0);
            }
        }
    }

    public void setFinishedWorkListener(FinishedWorkListener finishedWorkListener) {
        this.finishedWorkListener = finishedWorkListener;
    }

    @Override
    public void onClick(View v) {
        List<String> properties = new ArrayList<>(this.infoProperties);
        if (this.newPlace) {
            PlaceLoader.getInstance().addPlace(this.coordinates, properties, (int) this.ratingBar.getRating(), ((EditText) this.view.findViewById(R.id.description)).getText().toString());
        } else {
            PlaceLoader.getInstance().updatePlace(this.coordinates, properties, (int) this.ratingBar.getRating(), ((EditText) this.view.findViewById(R.id.description)).getText().toString());
        }
        if (dialogWithMap != null) {
            dialogWithMap.isSuccessfullyAddPlace(true);
        }
        if (this.finishedWorkListener != null) {
            this.finishedWorkListener.finished();
        }
    }


    public interface ShowChoosePropertiesListener extends Serializable {
        void show(List<String> info);
    }

}
