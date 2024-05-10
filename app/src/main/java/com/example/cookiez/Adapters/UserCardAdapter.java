package com.example.cookiez.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cookiez.Interface.UserCardCallback;
import com.example.cookiez.Model.UserRecipesCount;
import com.example.cookiez.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

public class UserCardAdapter extends RecyclerView.Adapter<UserCardAdapter.UserCardViewHolder> {
    private Context context;
    private ArrayList<UserRecipesCount> userRecipesCounts;
    private UserCardCallback userCardCallback;

    public UserCardAdapter(Context context,ArrayList<UserRecipesCount> userRecipesCounts){
        this.context = context;
        this.userRecipesCounts = userRecipesCounts;
    }

    public UserCardAdapter setCallback(UserCardCallback userCardCallback){
        this.userCardCallback = userCardCallback;
        return this;
    }

    @NonNull
    @Override
    public UserCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_user_item,parent,false);
        return new UserCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserCardViewHolder holder, int position) {
        holder.HUI_MTV_Name.setText(userRecipesCounts.get(position).getUserName());
        holder.HUI_MTV_RecipesCount.setText("" + userRecipesCounts.get(position).getRecipesCount() + " Recipes");
        Glide.with(context).load(userRecipesCounts.get(position).getUserProfilePicture()).into(holder.HUI_SIV_ProfilePicture);
    }

    public UserRecipesCount getItem(int position){
        return userRecipesCounts.get(position);
    }

    @Override
    public int getItemCount() {
        if(userRecipesCounts == null)
            return 0;
        return userRecipesCounts.size();
    }

    public class UserCardViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout HUI_RL_UserCard;
        private MaterialTextView HUI_MTV_Name;
        private MaterialTextView HUI_MTV_RecipesCount;
        private ShapeableImageView HUI_SIV_ProfilePicture;

        public UserCardViewHolder(@NonNull View itemView) {
            super(itemView);
            HUI_MTV_Name = itemView.findViewById(R.id.HUI_MTV_Name);
            HUI_MTV_RecipesCount = itemView.findViewById(R.id.HUI_MTV_RecipesCount);
            HUI_RL_UserCard = itemView.findViewById(R.id.HUI_RL_UserCard);
            HUI_SIV_ProfilePicture = itemView.findViewById(R.id.HUI_SIV_ProfilePicture);

            HUI_RL_UserCard.setOnClickListener(v -> {
                if(userCardCallback != null){
                    userCardCallback.UserCardCliked(getItem(getAdapterPosition()),getAdapterPosition());
                }
            });


        }
    }

}
