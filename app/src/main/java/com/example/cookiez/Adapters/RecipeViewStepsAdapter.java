package com.example.cookiez.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cookiez.R;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

public class RecipeViewStepsAdapter extends RecyclerView.Adapter<RecipeViewStepsAdapter.RecipeViewStepsAdapterViewHolder> {
    private Context context;
    private ArrayList<String> steps;

    public RecipeViewStepsAdapter(Context context,ArrayList<String> steps) {
        this.context = context;
        this.steps = steps;
    }

    @NonNull
    @Override
    public RecipeViewStepsAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_step_view_item,parent,false);
        return new RecipeViewStepsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewStepsAdapterViewHolder holder, int position) {
        Log.d("Steps",steps.toString());
        Log.d("position",""+position);
        holder.HSVI_MTV_stepNo.setText("Step " + (position+1));
        holder.HSVI_MTV_StepDescription.setText(steps.get(position));
    }

    @Override
    public int getItemCount() {
        if(steps == null)
            return 0;
        return steps.size();
    }


    public class RecipeViewStepsAdapterViewHolder extends RecyclerView.ViewHolder{
        private MaterialTextView HSVI_MTV_stepNo;
        private MaterialTextView HSVI_MTV_StepDescription ;
        public RecipeViewStepsAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            HSVI_MTV_stepNo = itemView.findViewById(R.id.HSVI_MTV_stepNo);
            HSVI_MTV_StepDescription = itemView.findViewById(R.id.HSVI_MTV_StepDescription);

        }
    }
}
