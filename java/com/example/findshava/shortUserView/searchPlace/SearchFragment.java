package com.example.findshava.shortUserView.searchPlace;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.findshava.R;
import com.example.findshava.isReady.IsReady;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class SearchFragment extends Fragment implements IsReady {

    private View search;
    private IsReadyListener isReadyListener;
    private SearchRequiredFilterListener dialogWithFilter;
    public static final String TAG = "SearchFragment";

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (this.isReadyListener != null) {
            this.isReadyListener.ready();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.search = inflater.inflate(R.layout.search, null);
        RecyclerView answerSearchList = this.search.findViewById(R.id.search_answer_list);
        ArrayList<String> listAnswer = new ArrayList<>(Arrays.asList("Пироженные", "Шаверма",
                "Шаверма в сырном", "Алкоголь", "Кофе", "Кола", "Квас",
                "Чай", "Столовая", "Ресторан", "Скамейка", "Стол"));
        SearchAnswerAdapter adapter = new SearchAnswerAdapter(getContext(), listAnswer);
        answerSearchList.setAdapter(adapter);
        answerSearchList.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter.filter("_");
        answerSearchList.setVisibility(View.INVISIBLE);

        SearchView searchView = this.search.findViewById(R.id.search);

        answerSearchList.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        UpdateSearchView updateSearchView = new UpdateSearchView(adapter, answerSearchList);

        searchView.setOnCloseListener(updateSearchView);
        searchView.setOnQueryTextListener(updateSearchView);
        searchView.setOnClickListener(updateSearchView);
        searchView.setOnSearchClickListener(updateSearchView);
        return this.search;
    }

    private class UpdateSearchView implements SearchView.OnCloseListener, SearchView.OnQueryTextListener, View.OnClickListener {


        private SearchAnswerAdapter adapter;
        private View answerSearchList;

        UpdateSearchView(SearchAnswerAdapter adapter, View answerSearchList) {
            this.adapter = adapter;
            this.answerSearchList = answerSearchList;
        }

        @Override
        public boolean onClose() {
            adapter.filter("_");
            answerSearchList.setVisibility(View.INVISIBLE);
            answerSearchList.animate().alpha(0).setDuration(150);
            if (SearchFragment.this.dialogWithFilter != null) {
                SearchFragment.this.dialogWithFilter.requiredFilter(null);
            }
            return false;
        }

        @Override
        public boolean onQueryTextSubmit(String query) {
            this.answerSearchList.setVisibility(View.VISIBLE);
            this.adapter.filter(query);
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            this.answerSearchList.setVisibility(View.VISIBLE);
            this.adapter.filter(newText);
            return false;
        }

        @Override
        public void onClick(View v) {
            answerSearchList.setVisibility(View.VISIBLE);
            answerSearchList.animate().alpha(1).setDuration(150);
            SearchFragment.this.search.bringToFront();
            SearchFragment.this.search.bringToFront();
            if (this.adapter.getOnClickListener() == null) {
                this.adapter.setOnClickListener((View answerItem) -> {
                    SearchFragment.this.dialogWithFilter.requiredFilter((((Button) answerItem).getText().toString()));
                    ((SearchView.SearchAutoComplete) v.findViewById(R.id.search_src_text)).setText(((Button) answerItem).getText());
                    v.clearFocus();
                    this.answerSearchList.setVisibility(View.INVISIBLE);
                });
            }
            SearchView searchView = (SearchView) v;
            if (searchView.isIconified()) {
                searchView.setIconified(false);
            }
            this.adapter.filter(null);

        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.dialogWithFilter = (SearchRequiredFilterListener) getArguments().getSerializable("dialogWithFilter");
        }
    }

    public void setIsReadyListener(IsReadyListener isReadyListener) {
        if (getView() != null) {
            isReadyListener.ready();
        } else {
            this.isReadyListener = isReadyListener;
        }
    }

    public static SearchFragment newInstance(SearchRequiredFilterListener dialogWithFilter) {
        Bundle args = new Bundle();
        args.putSerializable("dialogWithFilter", dialogWithFilter);
        SearchFragment answer = new SearchFragment();
        answer.setArguments(args);
        return answer;
    }


    public interface SearchRequiredFilterListener extends Serializable {
        void requiredFilter(CharSequence query);

    }
}
