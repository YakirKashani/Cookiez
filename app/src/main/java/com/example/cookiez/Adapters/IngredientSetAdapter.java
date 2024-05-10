package com.example.cookiez.Adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.MultiAutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cookiez.Model.Ingredient;
import com.example.cookiez.R;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.HashMap;

public class IngredientSetAdapter extends RecyclerView.Adapter<IngredientSetAdapter.IngredienViewtHolder> {
    private Context context;
    private ArrayList<Ingredient> ingredientList;
    private HashMap<Integer,String> nameInputs = new HashMap<>();
    private HashMap<Integer,String> amountInputs = new HashMap<>();
    private HashMap<Integer,String> unitsInputs = new HashMap<>();

    public IngredientSetAdapter(Context context, ArrayList<Ingredient> ingredientList){
        this.context = context;
        this.ingredientList = ingredientList;
    }

    @NonNull
    @Override
    public IngredienViewtHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_ingredient_item,parent,false);
        return new IngredienViewtHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredienViewtHolder holder, int position) {
        holder.HII_TIET_name.setText(nameInputs.get(position));
        holder.HII_TIET_amount.setText(amountInputs.get(position));
        holder.HII_MACTV_units.setText(unitsInputs.get(position));
    }

    @Override
    public int getItemCount() {
        return ingredientList == null ? 0 : ingredientList.size();
    }

    public ArrayList<Ingredient> getIngredientList() {
        ArrayList<Ingredient> ingredients = new ArrayList<>();
        for(int i=0;i<nameInputs.size();i++)
            ingredients.add(new Ingredient().setName(nameInputs.get(i)).setAmount(amountInputs.get(i)).setUnit(unitsInputs.get(i)));
        return ingredients;
    }

    public class IngredienViewtHolder extends RecyclerView.ViewHolder{

        private TextInputEditText HII_TIET_name;
        private  TextInputEditText HII_TIET_amount;
        private MultiAutoCompleteTextView HII_MACTV_units;
        private ImageButton HII_IB_add;
        public IngredienViewtHolder(@NonNull View itemview){
            super(itemview);
            HII_TIET_name = itemview.findViewById(R.id.HII_TIET_name);
            HII_TIET_amount = itemview.findViewById(R.id.HII_TIET_amount);
            HII_MACTV_units = itemview.findViewById(R.id.HII_MACTV_units);

            HII_TIET_name.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    nameInputs.put(getAdapterPosition(),s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            HII_TIET_amount.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    amountInputs.put(getAdapterPosition(),s.toString());

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            HII_MACTV_units.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    unitsInputs.put(getAdapterPosition(),s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

        }

        public String getName(){
            return HII_TIET_name.getText().toString();
        }

        public String getAmount(){
            return HII_TIET_amount.getText().toString();
        }

        public String getUnits(){
            return HII_MACTV_units.getText().toString();
        }

    }
}
