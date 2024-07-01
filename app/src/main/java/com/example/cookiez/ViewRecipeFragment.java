package com.example.cookiez;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cookiez.Adapters.RecipeViewIngredientsAdapter;
import com.example.cookiez.Adapters.RecipeViewStepsAdapter;
import com.example.cookiez.Model.Ingredient;
import com.example.cookiez.Model.Recipe;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewRecipeFragment extends Fragment {
    private String UUID;
    private String RecipeName;
    private String RecipeAuthor;
    private MaterialTextView RecipeViewFragment_MTV_name;
    private MaterialTextView RecipeViewFragment_MTV_author;
    private RecyclerView RecipeViewFragment_RV_ingredients;
    private RecyclerView RecipeViewFragment_RV_steps;
    private FirebaseDatabase db;
    private DatabaseReference UsersRef;
    private RecipeViewIngredientsAdapter recipeViewIngredientsAdapter;
    private RecipeViewStepsAdapter recipeViewStepsAdapter;
    ArrayList<Ingredient> ingredients;
    ArrayList<String> steps;



    public ViewRecipeFragment() {
        // Required empty public constructor
    }

    private void initViews() {
        db = FirebaseDatabase.getInstance();
        UsersRef = db.getReference("users");
        UsersRef.child(UUID).child("Recipes").child(RecipeName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Recipe recipe = new Recipe();
                for(DataSnapshot ingredientSnapshot : snapshot.child("ingredients").getChildren()) {
                    String name = ingredientSnapshot.child("name").getValue(String.class);
                    String amount = ingredientSnapshot.child("amount").getValue(String.class);
                    String units = ingredientSnapshot.child("units").getValue(String.class);
                    Ingredient ingredient = new Ingredient();
                    ingredient.setName(name).setAmount(amount).setUnit(units);
                    ingredients.add(ingredient);
                }
                for(DataSnapshot stepSnapshot : snapshot.child("steps").getChildren()){
                    String step = stepSnapshot.getValue(String.class);
                    steps.add(step);
                }
                recipe.setAuthor(RecipeAuthor).setIngredients(ingredients).setName(RecipeName).setSteps(steps);
                useRecipe(recipe);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void findViews(View view) {
        RecipeViewFragment_MTV_name = view.findViewById(R.id.RecipeViewFragment_MTV_name);
        RecipeViewFragment_MTV_author = view.findViewById(R.id.RecipeViewFragment_MTV_author);
        RecipeViewFragment_RV_ingredients = view.findViewById(R.id.RecipeViewFragment_RV_ingredients);
        RecipeViewFragment_RV_steps = view.findViewById(R.id.RecipeViewFragment_RV_steps);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ingredients = new ArrayList<>();
        steps = new ArrayList<>();
        findViews(view);
        initViews();

    }

    public void useRecipe(Recipe recipe){
        recipeViewIngredientsAdapter = new RecipeViewIngredientsAdapter(getContext(),recipe.getIngredients());
        recipeViewStepsAdapter = new RecipeViewStepsAdapter(getContext(),recipe.getSteps());
        RecipeViewFragment_MTV_name.setText(RecipeName);
        RecipeViewFragment_MTV_author.setText(RecipeAuthor);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getContext());
        linearLayoutManager1.setOrientation(RecyclerView.VERTICAL);
        RecipeViewFragment_RV_ingredients.setLayoutManager(linearLayoutManager1);
        RecipeViewFragment_RV_ingredients.setAdapter(recipeViewIngredientsAdapter);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getContext());
        linearLayoutManager2.setOrientation(RecyclerView.VERTICAL);
        RecipeViewFragment_RV_steps.setLayoutManager(linearLayoutManager2);
        RecipeViewFragment_RV_steps.setAdapter(recipeViewStepsAdapter);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_recipe, container, false);
        Bundle bundle = getArguments();
        if(bundle!=null){
            UUID = bundle.getString("UUID");
            RecipeName = bundle.getString("RecipeName");
            RecipeAuthor = bundle.getString("Author");
        }

        return view;
    }


}

