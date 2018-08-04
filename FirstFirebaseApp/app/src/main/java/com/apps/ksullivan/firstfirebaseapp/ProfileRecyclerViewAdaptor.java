package com.apps.ksullivan.firstfirebaseapp;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.apps.ksullivan.firstfirebaseapp.utils.FirebaseDao;
import com.apps.ksullivan.firstfirebaseapp.model.Gender;
import com.apps.ksullivan.firstfirebaseapp.model.Profile;
import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.common.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class ProfileRecyclerViewAdaptor extends RecyclerView.Adapter<ProfileRecyclerViewAdaptor.SingleProfileViewHolder> {

    private List<Profile> profiles;
    private ProfileOnItemClickListener listener;

    public interface ProfileOnItemClickListener {
        void onProfileItemClick(Profile profile);
    }

    public ProfileRecyclerViewAdaptor(ProfileOnItemClickListener listener) {
        this.profiles = new ArrayList<>();
        this.listener = listener;
    }

    public void setProfilesForRecView(List<Profile> profiles) {
        if (!CollectionUtils.isEmpty(profiles)) {
            this.profiles = profiles;
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SingleProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        return new SingleProfileViewHolder(inflater.inflate(R.layout.profile_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SingleProfileViewHolder holder, int position) {
        final Profile profile = profiles.get(position);
        Glide.with(holder.getProfileImageView().getContext())
                .using(new FirebaseImageLoader())
                .load(FirebaseDao.getImageFromStorageReference(profile.getImageId()))
                .into(holder.getProfileImageView());

        holder.getGender().setText(profile.getGender().getCode());
        if (profile.getGender().equals(Gender.Male)) {
            holder.getCardview().setCardBackgroundColor(Color.parseColor("#3F51B5"));
        } else {
            holder.getCardview().setCardBackgroundColor(Color.parseColor("#E040FB"));

        }
        holder.getAge().setText(String.valueOf(profile.getAge()));
        holder.getName().setText(profile.getName());
        StringBuilder sb = new StringBuilder();
        for (String hobby : profile.getHobbies()) {
            sb.append(hobby).append(',').append(' ');
        }
        sb.setLength(sb.length() - 2);
        holder.getHobbies().setText(sb.toString());

        holder.getItemView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //passing the profile to the main activity
                listener.onProfileItemClick(profile);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (CollectionUtils.isEmpty(profiles)) {
            return 0;
        }
        return profiles.size();
    }

    public class SingleProfileViewHolder extends RecyclerView.ViewHolder {
        private ImageView profileImageView;
        private TextView name;
        private TextView age;
        private TextView hobbies;
        private TextView gender;
        private CardView cardview;

        public SingleProfileViewHolder(View itemView) {
            super(itemView);
            profileImageView = itemView.findViewById(R.id.imageProfile);
            gender = itemView.findViewById(R.id.displayGender);
            name = itemView.findViewById(R.id.displayName);
            age = itemView.findViewById(R.id.displayAge);
            hobbies = itemView.findViewById(R.id.displayHobbies);
            cardview = itemView.findViewById(R.id.cardView);

        }
        public ImageView getProfileImageView() {
            return profileImageView;
        }

        public TextView getName() {
            return name;
        }

        public TextView getAge() {
            return age;
        }

        public TextView getHobbies() {
            return hobbies;
        }

        public TextView getGender() {
            return gender;
        }

        public  View getItemView(){
            return itemView;
        }
        public CardView getCardview() {
            return cardview;
        }
    }
}
