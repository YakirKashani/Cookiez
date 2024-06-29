package com.example.cookiez.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cookiez.Interface.RecipeCallback;
import com.example.cookiez.Model.Recipe;
import com.example.cookiez.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {
    private Context context;
    private ArrayList<Recipe> recipes;
    private RecipeCallback recipeCallback;

    public RecipeAdapter(Context context, ArrayList<Recipe> recipes) {
        this.context = context;
        this.recipes = recipes;
    }

    public RecipeAdapter setRecipeCallback(RecipeCallback recipeCallback) {
        this.recipeCallback = recipeCallback;
        return this;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_recipe_item, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        holder.HRI_MTV_RecipeName.setText(recipes.get(position).getName());
        Glide.with(context).load(recipes.get(position).getRecipePicture()).into(holder.HRI_SIV_RecipePicture);
        holder.RecipeView_MTV_UploadTime.setText("Uploaded at " + recipes.get(position).getDate() + " " + recipes.get(position).getTime());

    }

    @Override
    public int getItemCount() {
        if (recipes == null)
            return 0;
        return recipes.size();
    }

    private Recipe getItem(int position) {
        return recipes.get(position);
    }


    public class RecipeViewHolder extends RecyclerView.ViewHolder {

        private MaterialTextView HRI_MTV_RecipeName;
        private MaterialTextView RecipeView_MTV_UploadTime;
        private ShapeableImageView HRI_SIV_RecipePicture;
        private CardView HRI_CV_Card;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            HRI_MTV_RecipeName = itemView.findViewById(R.id.HRI_MTV_RecipeName);
            RecipeView_MTV_UploadTime = itemView.findViewById(R.id.RecipeView_MTV_UploadTime);
            HRI_SIV_RecipePicture = itemView.findViewById(R.id.HRI_SIV_RecipePicture);
            HRI_CV_Card = itemView.findViewById(R.id.HRI_CV_Card);
            HRI_CV_Card.setOnClickListener(v -> {
                if (recipeCallback != null)
                    recipeCallback.RecipeClicked(getItem(getAdapterPosition()), getAdapterPosition());
            });

        }
    }
}
