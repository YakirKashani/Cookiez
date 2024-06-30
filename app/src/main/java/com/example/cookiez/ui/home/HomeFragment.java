package com.example.cookiez.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cookiez.Adapters.PostAdapter;
import com.example.cookiez.Interface.PostCallback;
import com.example.cookiez.Interface.RecipeCallback;
import com.example.cookiez.Model.Recipe;
import com.example.cookiez.R;
import com.example.cookiez.ViewRecipeActivity;
import com.example.cookiez.databinding.FragmentHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private RecyclerView Home_RV_Posts;
    private PostAdapter postAdapter;
    private ArrayList<Recipe> posts;
    private FirebaseDatabase db;
    private DatabaseReference UsersRef;
    private FirebaseAuth auth;
    private FirebaseUser user;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = FirebaseDatabase.getInstance();
        UsersRef = db.getReference("users");
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        posts = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), posts);
        findViews(view);
        initViews(view);
    }
    private void findViews(View view) {
        Home_RV_Posts = view.findViewById(R.id.Home_RV_Posts);
    }
    private void initViews(View view) { //TODO - Improve reading methods
        DatabaseReference CurrentUserRef = UsersRef.child(user.getUid()).child("Following");
        CurrentUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot Following : snapshot.getChildren()) {
                    String uid = Following.getKey();
                    DatabaseReference FollowingUserRef = UsersRef.child(uid);
                    FollowingUserRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String AuthorPicture = snapshot.child("Profile Picture").getValue(String.class); //BUG
                            for (DataSnapshot recipeSanpshot : snapshot.child("Recipes").getChildren()) {
                                String name = recipeSanpshot.getKey();
                                String author = recipeSanpshot.child("author").getValue(String.class);
                                String dateUploaded = recipeSanpshot.child("date").getValue(String.class);
                                String timeUploaded = recipeSanpshot.child("time").getValue(String.class);
                                String RecipePicture = recipeSanpshot.child("Recipe Picture").getValue(String.class);
                                long likes = recipeSanpshot.child("Likes").getChildrenCount();
                                ArrayList<String> UsersLiked = new ArrayList<>();
                                for (DataSnapshot likesSnapshot : recipeSanpshot.child("Likes").getChildren())
                                    UsersLiked.add(likesSnapshot.getKey());
                                Recipe recipe = new Recipe();
                                recipe.setName(name).setAuthorUid(uid).setAuthor(author).setDate(dateUploaded).setTime(timeUploaded).setLikes(likes).setIngredients(null).setSteps(null).setAuthorPicture(AuthorPicture).setRecipePicture(RecipePicture).setUsersLiked(UsersLiked);
                                posts.add(recipe);
                            }
                            usePosts(view, posts);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        postAdapter.setRecipeCallback(new RecipeCallback() {
            @Override
            public void RecipeClicked(Recipe recipe, int position) {
                Intent RecipeIntent = new Intent(getContext(), ViewRecipeActivity.class);
                RecipeIntent.putExtra(ViewRecipeActivity.RecipeNameStatus, recipe.getName());
                RecipeIntent.putExtra(ViewRecipeActivity.RecipeAuthorStatus, recipe.getAuthor());
                RecipeIntent.putExtra(ViewRecipeActivity.UUIDstatus, recipe.getAuthorUid());
                startActivity(RecipeIntent);
                getActivity().finish();
            }
        });

        postAdapter.setPostCallback(new PostCallback() {
            @Override
            public void likeButtonClicked(Recipe recipe, int position) {
                updateLikes(recipe,position);
            }
        });
    }

    public void updateLikes(Recipe recipe,int position)
    {
        DatabaseReference recipeRef = UsersRef.child(recipe.getAuthorUid()).child("Recipes").child(recipe.getName()).child("Likes").child(user.getUid());
        if (recipe.getUsersLiked().contains(user.getUid())) {
            recipeRef.removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    recipe.getUsersLiked().remove(user.getUid());
                    recipe.setLikes(recipe.getLikes() - 1);
                    postAdapter.notifyItemChanged(position);
                }
            });
        } else {
            recipeRef.setValue(user.getUid()).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    recipe.getUsersLiked().add(user.getUid());
                    recipe.setLikes(recipe.getLikes() + 1);
                    postAdapter.notifyItemChanged(position);
                }
            });
        }
    }

    private void usePosts(View view, ArrayList<Recipe> posts) {
        posts.sort(new Recipe.RecipeDateComparator());
        for(int i=0;i<posts.size();i++){
            Recipe recipe = posts.get(i);
            for(int k=i+1;k<posts.size();k++){
                if(Objects.equals(recipe.getName(), posts.get(k).getName()) &&
                        Objects.equals(recipe.getAuthorUid(), posts.get(k).getAuthorUid()) &&
                        Objects.equals(recipe.getDate(), posts.get(k).getDate()) &&
                        Objects.equals(recipe.getTime(), posts.get(k).getTime())) {
                    posts.remove(k);
                }
            }
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        Home_RV_Posts.setLayoutManager(linearLayoutManager);
        Home_RV_Posts.setAdapter(postAdapter);
        Log.d("posts" , posts.toString());
    }
}