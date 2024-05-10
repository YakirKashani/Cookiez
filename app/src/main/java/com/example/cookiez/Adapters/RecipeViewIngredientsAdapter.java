package com.example.cookiez.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cookiez.Model.Ingredient;
import com.example.cookiez.R;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

public class RecipeViewIngredientsAdapter extends RecyclerView.Adapter<RecipeViewIngredientsAdapter.RecipeViewIngredientsViewHolder> {
    private Context context;
    private ArrayList<Ingredient> ingredients;

    public RecipeViewIngredientsAdapter(Context context, ArrayList<Ingredient> ingredients)
    {
        this.context = context;
        this.ingredients = ingredients;
    }

    @NonNull
    @Override
    public RecipeViewIngredientsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_ingredient_view_item,parent,false);
        return new RecipeViewIngredientsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewIngredientsViewHolder holder, int position) {
        holder.HIVI_MTV_name.setText(ingredients.get(position).getName());
        holder.HIVI_MTV_amount.setText(ingredients.get(position).getAmount());
        holder.HIVI_MTV_units.setText(ingredients.get(position).getUnits());
    }

    @Override
    public int getItemCount() {
        if(ingredients == null)
            return 0;
        return ingredients.size();
    }

    public class RecipeViewIngredientsViewHolder extends RecyclerView.ViewHolder{

        private MaterialTextView HIVI_MTV_name;
        private MaterialTextView HIVI_MTV_amount;
        private MaterialTextView HIVI_MTV_units;
        public RecipeViewIngredientsViewHolder(@NonNull View itemView) {
            super(itemView);
            HIVI_MTV_name = itemView.findViewById(R.id.HIVI_MTV_name);
            HIVI_MTV_amount = itemView.findViewById(R.id.HIVI_MTV_amount);
            HIVI_MTV_units = itemView.findViewById(R.id.HIVI_MTV_units);
        }
    }




}


