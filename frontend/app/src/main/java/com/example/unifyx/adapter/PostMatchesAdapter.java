package com.example.unifyx.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.example.unifyx.model.Recommendation;
import com.example.unifyx.model.Quote;
import com.example.unifyx.owner.RecommendationsFragment;
import com.example.unifyx.owner.QuotesFragment;
import java.util.List;

/**
 * ViewPager2 Adapter for PostMatches activity (Recommendations + Quotes tabs)
 */
public class PostMatchesAdapter extends FragmentStateAdapter {
    
    private List<Recommendation> recommendations;
    private List<Quote> quotes;
    private int postId;
    
    public PostMatchesAdapter(@NonNull FragmentActivity fragmentActivity, 
                              List<Recommendation> recommendations,
                              List<Quote> quotes,
                              int postId) {
        super(fragmentActivity);
        this.recommendations = recommendations;
        this.quotes = quotes;
        this.postId = postId;
    }
    
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return RecommendationsFragment.newInstance(recommendations, postId);
        } else {
            return QuotesFragment.newInstance(quotes, postId);
        }
    }
    
    @Override
    public int getItemCount() {
        return 2; // Two tabs: Recommended and Quotes
    }
}

