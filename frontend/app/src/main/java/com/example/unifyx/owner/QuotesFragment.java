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
import com.example.unifyx.model.Hire;
import com.example.unifyx.model.Quote;
import com.example.unifyx.network.RetrofitClient;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Fragment to display quotes from workers
 */
public class QuotesFragment extends Fragment {
    
    private static final String ARG_QUOTES = "quotes";
    private static final String ARG_POST_ID = "postId";
    
    private List<Quote> quotes;
    private int postId;
    private RecyclerView recyclerView;
    private QuoteAdapter adapter;

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

        adapter = new QuoteAdapter(getContext(), quotes, postId);
        recyclerView.setAdapter(adapter);

        loadActiveHires();

        return rootView;
    }

    // Quote acceptance and Hire creation are separate entities (see QuoteAdapter);
    // this fetches the post's active/completed hires so accepted quote cards can
    // show the correct Mark Complete / Rate & Review state.
    private void loadActiveHires() {
        RetrofitClient.getInstance().getApiService()
            .getActiveHiresForPost(postId)
            .enqueue(new Callback<List<Hire>>() {
                @Override
                public void onResponse(Call<List<Hire>> call, Response<List<Hire>> response) {
                    if (response.isSuccessful() && response.body() != null && adapter != null) {
                        adapter.setActiveHires(response.body());
                    }
                }

                @Override
                public void onFailure(Call<List<Hire>> call, Throwable t) {
                    // Non-blocking; quote list still works without hire state.
                }
            });
    }
}

