package com.example.cookiez.ui.notifications;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cookiez.Adapters.RecipeAdapter;
import com.example.cookiez.Interface.RecipeCallback;
import com.example.cookiez.Model.Recipe;
import com.example.cookiez.NewRecipeActivity;
import com.example.cookiez.R;
import com.example.cookiez.ViewRecipeActivity;

import com.example.cookiez.databinding.FragmentNotificationsBinding;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    private MaterialButton User_MB_NewRecipe;
    private MaterialTextView account_MTV_UserName;
    private MaterialTextView account_MTV_RecipesCount;
    private MaterialTextView account_MTV_FollowingCount;
    private MaterialTextView account_MTV_FollowersCount;
    private ShapeableImageView account_SIV_ProfilePicture;
    private FirebaseDatabase db;
    private DatabaseReference UsersRef;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private ArrayList<Recipe> recipes;
    private RecyclerView User_RV_MyRecipes;
    private RecipeAdapter recipeAdapter;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        recipes = new ArrayList<>();


        final TextView textView = binding.textNotifications;
        notificationsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = FirebaseDatabase.getInstance();
        UsersRef = db.getReference("users");
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        recipeAdapter = new RecipeAdapter(getContext(), recipes);
        findViews(view);
        initViews(view);
        DatabaseReference SpecificUserRef = UsersRef.child(user.getUid()); // Users -> specific user
//        SpecificUserRef.child("userName").addValueEventListener(
//                new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        String username = snapshot.getValue(String.class);
//                        account_MTV_UserName.setText(username);
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                }
//        );

    }

    private void initViews(View view) {

        DatabaseReference SpecificUserRef = UsersRef.child(user.getUid()); // Users -> specific user
        DatabaseReference RecipesRef = SpecificUserRef.child("Recipes");
        RecipesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot recipeSnapshot : snapshot.getChildren()) {
                    String name = recipeSnapshot.getKey();
                    String author = recipeSnapshot.child("Author").getValue(String.class);
                    String dateUploaded = recipeSnapshot.child("Upload date").getValue(String.class);
                    String timeUploaded = recipeSnapshot.child("Upload time").getValue(String.class);
                    String RecipePicture = recipeSnapshot.child("Recipe Picture").getValue(String.class);
                    Recipe recipe = new Recipe();
                    recipe.setAuthor(author).setName(name).setIngredients(null).setSteps(null).setDate(dateUploaded).setTime(timeUploaded).setRecipePicture(RecipePicture);
                    recipes.add(recipe);

                  /*  ArrayList<Ingredient> ingredients = new ArrayList<>();
                    for(DataSnapshot ingredientSnapshot : recipeSnapshot.child("ingredients").getChildren()){
                        String name = ingredientSnapshot.child("name").getValue(String.class);
                        String amount = ingredientSnapshot.child("amount").getValue(String.class);
                        String units = ingredientSnapshot.child("units").getValue(String.class);
                        Ingredient ingredient = new Ingredient();
                        ingredient.setName(name).setAmount(amount).setUnit(units);
                        ingredients.add(ingredient);
                    }

                    ArrayList<String> steps = new ArrayList<>();
                    for(DataSnapshot stepSnapshot : recipeSnapshot.child("steps").getChildren()){
                        String step = stepSnapshot.getValue(String.class);
                        steps.add(step);
                    }

                    Recipe recipe = new Recipe();
                    recipe.setAuthor(author).setIngredients(ingredients).setName(name).setSteps(steps);

                   */

                }
                recipes.sort(new Recipe.RecipeDateComparator());
                useRecipes(view, recipes);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        User_MB_NewRecipe.setOnClickListener(v -> {
            Intent newRecipeIntent = new Intent(getContext(), NewRecipeActivity.class);
            newRecipeIntent.putExtra(NewRecipeActivity.UserNameStatus, account_MTV_UserName.getText().toString());
            startActivity(newRecipeIntent);
        });

        recipeAdapter.setRecipeCallback(new RecipeCallback() {
            @Override
            public void RecipeClicked(Recipe recipe, int position) {
                Intent RecipeIntent = new Intent(getContext(), ViewRecipeActivity.class);
                RecipeIntent.putExtra(ViewRecipeActivity.RecipeNameStatus, recipe.getName());
                RecipeIntent.putExtra(ViewRecipeActivity.RecipeAuthorStatus, recipe.getAuthor());
                RecipeIntent.putExtra(ViewRecipeActivity.UUIDstatus, user.getUid());
                startActivity(RecipeIntent);

     /*           FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Bundle bundle = new Bundle();
                bundle.putString("RecipeName",recipe.getName());
                bundle.putString("Author",recipe.getAuthor());
                bundle.putString("UUID",user.getUid());
                ViewRecipeFragment fragment = new ViewRecipeFragment();
                fragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.nav_host_fragment_activity_main,fragment);
            //    fragmentTransaction.addToBackStack(null);
               fragmentTransaction.commit();*/
            }
        });

        SpecificUserRef.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String username = snapshot.child("userName").getValue(String.class);
                        account_MTV_UserName.setText(username);
                        account_MTV_RecipesCount.setText("" + snapshot.child("Recipes").getChildrenCount());
                        account_MTV_FollowingCount.setText("" + snapshot.child("Following").getChildrenCount());
                        account_MTV_FollowersCount.setText("" + snapshot.child("Followers").getChildrenCount());
                        String profilePic = snapshot.child("Profile Picture").getValue(String.class);
                        loadProfilePicture(profilePic);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                }
        );
    }

    public void loadProfilePicture(String profilePic) {
        Context context = getContext();
        if (context != null)
            Glide.with(context).load(profilePic).into(account_SIV_ProfilePicture);
    }

    private void useRecipes(View view, ArrayList<Recipe> recipes) {
        recipes.sort(new Recipe.RecipeDateComparator());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        User_RV_MyRecipes.setLayoutManager(linearLayoutManager);
        User_RV_MyRecipes.setAdapter(recipeAdapter);
    }

    private void findViews(View view) {
        User_MB_NewRecipe = view.findViewById(R.id.User_MB_NewRecipe);
        account_MTV_UserName = view.findViewById(R.id.account_MTV_UserName);
        User_RV_MyRecipes = view.findViewById(R.id.User_RV_MyRecipes);
        account_MTV_RecipesCount = view.findViewById(R.id.account_MTV_RecipesCount);
        account_MTV_FollowingCount = view.findViewById(R.id.account_MTV_FollowingCount);
        account_MTV_FollowersCount = view.findViewById(R.id.account_MTV_FollowersCount);
        account_SIV_ProfilePicture = view.findViewById(R.id.account_SIV_ProfilePicture);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}