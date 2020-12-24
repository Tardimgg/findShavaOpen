package com.example.findshava.userView.workWithPlace.infoPlace;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.findshava.FinishedWorkListener.FinishedWorkListener;
import com.example.findshava.R;
import com.example.findshava.customClass.Coordinates;
import com.example.findshava.customClass.internet.IsOnline;
import com.example.findshava.customSpannableSpan.RoundedBackgroundSpan;
import com.example.findshava.customView.FloatingTextActionButton;
import com.example.findshava.dataBase.PlaceLoader;
import com.example.findshava.feedbackPlace.FeedbacksPlace;
import com.example.findshava.isReady.IsReady;
import com.example.findshava.map.Map;

public class InfoPlaceFragment extends Fragment implements IsReady, PlaceLoader.GettingInfoPlaceListener {

    private View view;
    private Coordinates coordinates;
    private boolean isHavingTask = false;
    private IsReady.IsReadyListener isReadyListener;
    private RecyclerView recyclerView;
    private ProgressBar waitRecyclerView;
    private TextView propertiesPlace;
    private FunctionChangingLogo functionChangingLogo;
    private Map.ClickCreateRoute createRouteRunnable;
    private FinishedWorkListener finishedWorkListener;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (isHavingTask) {
            prepare(this.coordinates);
        }
        if (this.isReadyListener != null) {
            this.isReadyListener.ready();
        }
    }

    public void setCreateRouteRunnable(Map.ClickCreateRoute createRouteRunnable) {
        this.createRouteRunnable = createRouteRunnable;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.info_place, null);

        FloatingTextActionButton createRoute = this.view.findViewById(R.id.create_route);
        createRoute.getFloatingActionButton().setOnClickListener((view) -> {
            if (this.createRouteRunnable != null) {
                createRouteRunnable.createRoute(this.coordinates);
            }
        });
        createRoute.getFloatingActionButton().setImageResource(R.drawable.route_icon);
        createRoute.getTextView().setText("Маршрут");

        FloatingTextActionButton savePlace = this.view.findViewById(R.id.savePlace);
        savePlace.getFloatingActionButton().setOnClickListener((view) -> {
            if (this.coordinates != null && IsOnline.isOnline(getContext())) {
                PlaceLoader.getInstance().savePlace(this.coordinates);
            }
            this.finishedWorkListener.finished();
        });
        savePlace.getFloatingActionButton().setImageResource(R.drawable.save_icon);
        savePlace.getTextView().setText("Сохранить");
        return this.view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    @Override
    public void setIsReadyListener(IsReady.IsReadyListener isReadyListener) {
        if (getView() != null) {
            isReadyListener.ready();
        } else {
            this.isReadyListener = isReadyListener;
        }
    }

    public static InfoPlaceFragment newInstance() {
        return new InfoPlaceFragment();
    }

    public void setFinishedWorkListener(FinishedWorkListener finishedWorkListener) {
        this.finishedWorkListener = finishedWorkListener;
    }

    public void addTaskPrepare(Coordinates coordinates) {
        this.coordinates = coordinates;
        if (getView() != null) {
            prepare(this.coordinates);
        } else {
            isHavingTask = true;
        }
    }

    public void addFunctionChangingLogo(FunctionChangingLogo functionChangingLogo) {
        this.functionChangingLogo = functionChangingLogo;

    }

    private void prepare(Coordinates coordinates) {
        this.isHavingTask = false;
        if (getView() != null) {
            if (this.propertiesPlace == null) {
                this.propertiesPlace = view.findViewById(R.id.properties_place);
            }
            if (this.recyclerView == null) {
                this.recyclerView = view.findViewById(R.id.recycler_feedback);
                this.recyclerView.setNestedScrollingEnabled(false);
            }
            this.propertiesPlace.setVisibility(View.INVISIBLE);
            this.recyclerView.setVisibility(View.INVISIBLE);
            //this.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            //    @Override
            //    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            //        super.onScrollStateChanged(recyclerView, newState);
            //    }
            //});
            if (this.waitRecyclerView == null) {
                this.waitRecyclerView = view.findViewById(R.id.waitRecyclerView);
            } else {
                //this.waitRecyclerView.animate().translationY(Converter.convertingDpToPx(getResources(), 55)).setDuration(0);
                this.waitRecyclerView.setVisibility(View.VISIBLE);
            }
            new Handler().postDelayed(() -> PlaceLoader.getInstance().getInfo(coordinates, this), 200);

        }
    }

    @Override
    public void onCompleteListener(FeedbacksPlace info) {
        //this.waitRecyclerView.animate().translationY(-Converter.convertingDpToPx(getResources(), 55)).setDuration(200);

        if (info != null) {
            this.waitRecyclerView.setVisibility(View.INVISIBLE);

            FeedbackPlaceAdapter adapter = new FeedbackPlaceAdapter(getContext(), info);

            StringBuilder text = new StringBuilder();
            for (int i = 0; i < info.getProperties().size(); i++) {
                text.append(info.getProperties().get(i));
                if (i + 1 != info.getProperties().size()) {
                    text.append(", ");
                } else {
                    text.append(".");
                }
            }

            SpannableString spannableString = new SpannableString(text);
            int start = 0;
            for (String value : info.getProperties()) {
                selectedText(spannableString, start, start + value.length());
                start += value.length() + 2;
            }
            this.propertiesPlace.setText(spannableString);

            this.recyclerView.setAdapter(adapter);
            this.propertiesPlace.setVisibility(View.VISIBLE);
            this.recyclerView.setVisibility(View.VISIBLE);
            this.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            if (info.getProperties().contains("Ресторан")) {
                this.functionChangingLogo.changingLogo(R.drawable.restaurant);
            } else if (info.getProperties().contains("Шаверма в сырном")) {
                this.functionChangingLogo.changingLogo(R.drawable.shava);
            } else if (info.getProperties().contains("Шаверма")) {
                this.functionChangingLogo.changingLogo(R.drawable.shava);
            } else if (info.getProperties().contains("Столовая")) {
                this.functionChangingLogo.changingLogo(R.drawable.dining);
            } else if (info.getProperties().contains("Пироженные")) {
                this.functionChangingLogo.changingLogo(R.drawable.breads);
            } else if (info.getProperties().contains("Квас")) {
                this.functionChangingLogo.changingLogo(R.drawable.kvass);
            }
        } else {
            new Handler().postDelayed(() -> PlaceLoader.getInstance().getInfo(coordinates, this), 5000);
        }
    }

    private void selectedText(Spannable text, int start, int stop) {
        text.setSpan(new RoundedBackgroundSpan(0xFFBA6C1E, Color.WHITE, 15, -1), start, stop, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        text.setSpan(new RelativeSizeSpan(0.8f), start, stop, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    public interface FunctionChangingLogo {
        void changingLogo(int id);
    }
}
