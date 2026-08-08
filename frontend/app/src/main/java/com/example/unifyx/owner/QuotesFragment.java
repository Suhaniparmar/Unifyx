package com.example.unifyx.owner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.unifyx.R;
import com.example.unifyx.adapter.QuoteAdapter;
import com.example.unifyx.model.Quote;
import java.util.ArrayList;
import java.util.List;

/**
 * Fragment to display quotes from workers
 */
public class QuotesFragment extends Fragment {
    
    private static final String ARG_QUOTES = "quotes";
    private static final String ARG_POST_ID = "postId";
    
    private List<Quote> quotes;
    private int postId;
    private RecyclerView recyclerView;
    
    public static QuotesFragment newInstance(List<Quote> quotes, int postId) {
        QuotesFragment fragment = new QuotesFragment();
        Bundle args = new Bundle();
        fragment.quotes = quotes;
        args.putInt(ARG_POST_ID, postId);
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            postId = getArguments().getInt(ARG_POST_ID);
            if (quotes == null) {
                quotes = new ArrayList<>();
            }
        }
    }
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_quotes, container, false);
        
        recyclerView = rootView.findViewById(R.id.quotesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        QuoteAdapter adapter = new QuoteAdapter(getContext(), quotes, postId);
        recyclerView.setAdapter(adapter);
        
        return rootView;
    }
}

