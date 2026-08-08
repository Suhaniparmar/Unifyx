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
import com.example.unifyx.adapter.RecommendationAdapter;
import com.example.unifyx.model.Recommendation;
import java.util.ArrayList;
import java.util.List;

/**
 * Fragment to display recommended workers
 */
public class RecommendationsFragment extends Fragment {
    
    private static final String ARG_RECOMMENDATIONS = "recommendations";
    private static final String ARG_POST_ID = "postId";
    
    private List<Recommendation> recommendations;
    private int postId;
    private RecyclerView recyclerView;
    
    public static RecommendationsFragment newInstance(List<Recommendation> recommendations, int postId) {
        RecommendationsFragment fragment = new RecommendationsFragment();
        Bundle args = new Bundle();
        fragment.recommendations = recommendations;
        args.putInt(ARG_POST_ID, postId);
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            postId = getArguments().getInt(ARG_POST_ID);
            if (recommendations == null) {
                recommendations = new ArrayList<>();
            }
        }
    }
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recommendations, container, false);
        
        recyclerView = rootView.findViewById(R.id.recommendationsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        RecommendationAdapter adapter = new RecommendationAdapter(getContext(), recommendations, postId);
        recyclerView.setAdapter(adapter);
        
        return rootView;
    }
}

