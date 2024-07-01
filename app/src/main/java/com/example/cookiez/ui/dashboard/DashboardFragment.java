package com.example.cookiez.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cookiez.Adapters.UserCardAdapter;
import com.example.cookiez.Interface.UserCardCallback;
import com.example.cookiez.Model.UserRecipesCount;
import com.example.cookiez.R;
import com.example.cookiez.UserProfileActivity;
import com.example.cookiez.databinding.FragmentDashboardBinding;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DashboardFragment extends Fragment {
    private TextInputEditText Search_TIET_SearchBar;
    private RecyclerView Search_RV_Results;
    private FragmentDashboardBinding binding;
    private FirebaseDatabase db;
    private DatabaseReference UsersRef;
    private UserCardAdapter userCardAdapter;
    private ArrayList<UserRecipesCount> userRecipesCountsList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textDashboard;
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
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
        userRecipesCountsList = new ArrayList<>();
        userCardAdapter = new UserCardAdapter(getContext(),userRecipesCountsList);
        findViews(view);
        initViews(view);
    }
    private void findViews(View view) {
        Search_TIET_SearchBar = view.findViewById(R.id.Search_TIET_SearchBar);
        Search_RV_Results = view.findViewById(R.id.Search_RV_Results);
    }
    private void initViews(View view) {
        userCardAdapter.setCallback(new UserCardCallback() {
            @Override
            public void UserCardCliked(UserRecipesCount userRecipesCount, int position) {
                Intent UserProfileIntent = new Intent(getContext(), UserProfileActivity.class);
                UserProfileIntent.putExtra(UserProfileActivity.UserIdStatus,userRecipesCountsList.get(position).getUserId());
                startActivity(UserProfileIntent);
                getActivity().finish();
            }
        });

        Search_TIET_SearchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                String search = s.toString();

                    UsersRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            userRecipesCountsList.clear();
                            if (search.length() == 0) {
                                userRecipesCountsList.clear();
                            }
                            else {
                                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                    String name = userSnapshot.child("userName").getValue(String.class);
                                    String ProfilePicture = userSnapshot.child("Profile Picture").getValue(String.class);
                                    if (name.startsWith(search)) {
                                        long recipesCount = userSnapshot.child("Recipes").getChildrenCount();
                                        UserRecipesCount userRecipesCount = new UserRecipesCount();
                                        userRecipesCount.setUserName(name).setRecipesCount(recipesCount).setUserId(userSnapshot.getKey()).setUserProfilePicture(ProfilePicture);
                                        Log.d("User",userRecipesCount.toString());
                                        userRecipesCountsList.add(userRecipesCount);
                                    }
                                }
                            }
                                useList(view, userRecipesCountsList);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void useList(View view,ArrayList<UserRecipesCount>userRecipesCounts){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        Search_RV_Results.setLayoutManager(linearLayoutManager);
        Search_RV_Results.setAdapter(userCardAdapter);

    }


}