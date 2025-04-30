package com.example.unifyx.adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unifyx.R;
import com.example.unifyx.model.ContractorProfile;
import com.example.unifyx.model.WorkerProfile;
import com.example.unifyx.owner.Profile;

import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder> {
    private List<Profile> profileList;

    public ProfileAdapter(List<Profile> profileList) {
        this.profileList = profileList;
    }

    @NonNull
    @Override
    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_profile, parent, false);
        return new ProfileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileViewHolder holder, int position) {
        Profile profile = profileList.get(position);
        holder.nameTextView.setText(profile.getName());
        holder.emailTextView.setText(profile.getEmail());
        holder.phoneTextView.setText(profile.getPhoneNo());
        holder.addressTextView.setText(profile.getAddress());
    }

    @Override
    public int getItemCount() {
        return profileList.size();
    }

    static class ProfileViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, emailTextView, phoneTextView, addressTextView;

        ProfileViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.tvName);
            emailTextView = itemView.findViewById(R.id.tvEmail);
            phoneTextView = itemView.findViewById(R.id.tvPhone);
            addressTextView = itemView.findViewById(R.id.tvAddress);
        }
    }
}

