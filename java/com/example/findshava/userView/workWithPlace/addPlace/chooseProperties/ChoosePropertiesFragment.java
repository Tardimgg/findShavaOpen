package com.example.findshava.userView.workWithPlace.addPlace.chooseProperties;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.findshava.R;
import com.example.findshava.customClass.SerializablePair;
import com.example.findshava.customView.FloatingTextActionButton;
import com.example.findshava.isReady.IsReady;
import com.example.findshava.viewPagerAdapter.ViewPager2FragmentStateAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChoosePropertiesFragment extends Fragment implements IsReady {

    private ChoosePropertiesResultListener resultListener;
    private IsReadyListener isReadyListener;

    private View view;

    private ChoosePropertiesItemFragment food;
    private ChoosePropertiesItemFragment water;
    private ChoosePropertiesItemFragment conveniences;

    private ViewPager2 pager2;

    private Set<String> infoProperties;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (this.isReadyListener != null) {
            this.isReadyListener.ready();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.choose_properties, null);

        FloatingTextActionButton food = this.view.findViewById(R.id.food);
        FloatingTextActionButton water = this.view.findViewById(R.id.water);
        FloatingTextActionButton conveniences = this.view.findViewById(R.id.conveniences);
        customizationPropertiesView(food, R.drawable.meal_icon, "Еда");
        customizationPropertiesView(water, R.drawable.water_icon, "Вода");
        customizationPropertiesView(conveniences, R.drawable.conveniences_icon, "Удобства");

        this.pager2 = this.view.findViewById(R.id.properties_pager);
        pager2.setUserInputEnabled(false);
        this.pager2.setOffscreenPageLimit(1);
        //pager2.setVisibility(View.INVISIBLE);
        ArrayList<SerializablePair<Integer, String>> imagesListFood = new ArrayList<>();
        imagesListFood.add(new SerializablePair<>(R.drawable.breads_icon, "Пироженные"));
        imagesListFood.add(new SerializablePair<>(R.drawable.shava_icon, "Шаверма"));
        imagesListFood.add(new SerializablePair<>(R.drawable.shava_cheese_icon, "Шаверма в сырном"));

        ArrayList<SerializablePair<Integer, String>> imagesListWater = new ArrayList<>();
        imagesListWater.add(new SerializablePair<>(R.drawable.alcohol_icon, "Алкоголь"));
        imagesListWater.add(new SerializablePair<>(R.drawable.coffee_icon, "Кофе"));
        imagesListWater.add(new SerializablePair<>(R.drawable.cola_icon, "Кола"));
        imagesListWater.add(new SerializablePair<>(R.drawable.kvass_icon, "Квас"));
        imagesListWater.add(new SerializablePair<>(R.drawable.tea_icon, "Чай"));

        ArrayList<SerializablePair<Integer, String>> imagesListConveniences = new ArrayList<>();
        imagesListConveniences.add(new SerializablePair<>(R.drawable.dining_icon, "Столовая"));
        imagesListConveniences.add(new SerializablePair<>(R.drawable.restaurant_icon, "Ресторан"));
        imagesListConveniences.add(new SerializablePair<>(R.drawable.sit_icon, "Скамейка"));
        imagesListConveniences.add(new SerializablePair<>(R.drawable.table_icon, "Стол"));

        this.food = ChoosePropertiesItemFragment.newInstance(imagesListFood);
        this.water = ChoosePropertiesItemFragment.newInstance(imagesListWater);
        this.conveniences = ChoosePropertiesItemFragment.newInstance(imagesListConveniences);

        Set<String> infoFoodProperties = new HashSet<>();
        Set<String> infoWaterProperties = new HashSet<>();
        Set<String> infoConveniencesProperties = new HashSet<>();

        if (this.infoProperties != null) {
            for (String value : this.infoProperties) {
                if (value.equals("Пироженные") || value.equals("Шаверма") || value.equals("Шаверма в сырном")) {
                    infoFoodProperties.add(value);
                } else if (value.equals("Алкоголь") || value.equals("Кофе") || value.equals("Кола") || value.equals("Квас") || value.equals("Чай")) {
                    infoWaterProperties.add(value);
                } else if (value.equals("Столовая") || value.equals("Ресторан") || value.equals("Скамейка") || value.equals("Стол")) {
                    infoConveniencesProperties.add(value);
                }
            }
        }

        this.food.setProperties(infoFoodProperties);
        this.water.setProperties(infoWaterProperties);
        this.conveniences.setProperties(infoConveniencesProperties);

        this.pager2.setAdapter(new ViewPager2FragmentStateAdapter(getFragmentManager(),
                this.food,
                this.water,
                this.conveniences));


        ((FloatingTextActionButton) this.view.findViewById(R.id.food)).getFloatingActionButton().setOnClickListener((view) -> {
            this.pager2.setCurrentItem(0);
            this.pager2.setVisibility(View.VISIBLE);
        });
        ((FloatingTextActionButton) this.view.findViewById(R.id.water)).getFloatingActionButton().setOnClickListener((view) -> {
            this.pager2.setCurrentItem(1);
            this.pager2.setVisibility(View.VISIBLE);
        });
        ((FloatingTextActionButton) this.view.findViewById(R.id.conveniences)).getFloatingActionButton().setOnClickListener((view) -> {
            this.pager2.setCurrentItem(2);
            this.pager2.setVisibility(View.VISIBLE);
        });


        this.view.findViewById(R.id.back_button).setOnClickListener((view) -> {
            List<String> answer = this.food.getProperties();
            answer.addAll(this.water.getProperties());
            answer.addAll(this.conveniences.getProperties());
            this.resultListener.chooseResult(answer);
        });

        return this.view;

    }

    private void customizationPropertiesView(FloatingTextActionButton floatingTextActionButton, int imageResource, String text) {
        floatingTextActionButton.getFloatingActionButton().setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.colorTransparent));
        floatingTextActionButton.getFloatingActionButton().setRippleColor(null);
        floatingTextActionButton.getFloatingActionButton().setImageResource(imageResource);
        floatingTextActionButton.getTextView().setText(text);

    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (this.pager2.getCurrentItem() != 0) {
            this.pager2.setCurrentItem(pager2.getCurrentItem() - 1, false);
            new Handler().postDelayed(() -> {
                pager2.setCurrentItem(pager2.getCurrentItem() + 1, false);
            }, 200);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.resultListener = (ChoosePropertiesResultListener) getArguments().getSerializable("resultListener");
        }
    }

    public void setInfoProperties(List<String> info) {
        if (info != null) {
            this.infoProperties = new HashSet<>(info);
        }
    }


    public static ChoosePropertiesFragment newInstance(ChoosePropertiesResultListener resultListener) {
        Bundle args = new Bundle();
        args.putSerializable("resultListener", resultListener);
        ChoosePropertiesFragment answer = new ChoosePropertiesFragment();
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

    public interface ChoosePropertiesResultListener extends Serializable {
        void chooseResult(List<String> result);
    }
}
