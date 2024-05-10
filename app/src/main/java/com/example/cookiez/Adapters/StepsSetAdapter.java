package com.example.cookiez.Adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cookiez.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.HashMap;

public class StepsSetAdapter extends RecyclerView.Adapter<StepsSetAdapter.StepViewHolder> {
    private Context context;
    private ArrayList<String> StepsList;
    private HashMap<Integer,String> StepsInputs = new HashMap<>();

    public StepsSetAdapter(Context context, ArrayList<String> StepsList){
        this.context = context;
        this.StepsList = StepsList;
    }


    @NonNull
    @Override
    public StepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_step_item,parent,false);
        return new StepViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StepViewHolder holder, int position) {
        holder.HSI_MTV_stepNo.setText(String.valueOf(position + 1));
        holder.HSI_TIET_input.setText(StepsInputs.get(position));
    }

    @Override
    public int getItemCount() {
        return StepsList == null ? 0 : StepsList.size();
    }

    public HashMap<Integer, String> getStepsInputs() {
        return StepsInputs;
    }
    public ArrayList<String> getStepsList(){
        ArrayList<String> Steps = new ArrayList<>();
        for(int i=0;i<StepsInputs.size();i++)
            Steps.add(StepsInputs.get(i));
        return Steps;
    }


    public class StepViewHolder extends RecyclerView.ViewHolder{
        private MaterialTextView HSI_MTV_stepNo;
        private TextInputEditText HSI_TIET_input;


        public StepViewHolder(@NonNull View itemView) {
            super(itemView);
            HSI_MTV_stepNo = itemView.findViewById(R.id.HSI_MTV_stepNo);
            HSI_TIET_input = itemView.findViewById(R.id.HSI_TIET_input);

            HSI_TIET_input.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    StepsInputs.put(getAdapterPosition(),s.toString());

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }
    }

}
