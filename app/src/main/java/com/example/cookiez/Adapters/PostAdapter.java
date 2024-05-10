package com.example.cookiez.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cookiez.Interface.PostCallback;
import com.example.cookiez.Interface.RecipeCallback;
import com.example.cookiez.Model.Recipe;
import com.example.cookiez.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private Context context;
    private ArrayList<Recipe> posts;
    private RecipeCallback recipeCallback;
    private PostCallback postCallback;
    private FirebaseAuth auth;
    private FirebaseUser CurrentUser;

    public PostAdapter(Context context,ArrayList<Recipe> posts){
        this.context = context;
        this.posts = posts;
        auth = FirebaseAuth.getInstance();
        CurrentUser = auth.getCurrentUser();
    }

    public PostAdapter setRecipeCallback(RecipeCallback recipeCallback){
        this.recipeCallback = recipeCallback;
        return this;
    }

    public PostAdapter setPostCallback(PostCallback postCallback){
        this.postCallback = postCallback;
        return this;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_post_item,parent,false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        holder.HPI_MTV_Name.setText(posts.get(position).getAuthor() + " shared a new recipe");
        holder.HPI_MTV_RecipeName.setText(posts.get(position).getName());
        holder.HPI_MTV_Likes.setText(posts.get(position).getLikes() + " Likes");
        holder.RecipeView_MTV_UploadTime.setText("Uploaded at " + posts.get(position).getDate() + " " + posts.get(position).getTime());
        Glide.with(context).load(posts.get(position).getAuthorPicture()).into(holder.HPI_SIV_ProfilePicture);
        Glide.with(context).load(posts.get(position).getRecipePicture()).into(holder.HPI_SIV_RecipePicture);


        if(posts.get(position).getUsersLiked().contains(CurrentUser.getUid()))
            holder.HPI_SIV_Like.setImageResource(R.drawable.full_heart);
        else
            holder.HPI_SIV_Like.setImageResource(R.drawable.empty_heart);

    }

    @Override
    public int getItemCount() {
        if(posts == null)
            return 0;
        return posts.size();
    }

    private Recipe getItem(int position){
        return posts.get(position);

    }

    public class PostViewHolder extends RecyclerView.ViewHolder{
        private CardView HPI_CV_Posts;
        private MaterialTextView HPI_MTV_Name;
        private MaterialTextView HPI_MTV_RecipeName;
        private MaterialTextView HPI_MTV_Likes;
        private MaterialTextView RecipeView_MTV_UploadTime;
        private ShapeableImageView HPI_SIV_Like;
        private ShapeableImageView HPI_SIV_RecipePicture;
        private ShapeableImageView HPI_SIV_ProfilePicture;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            HPI_CV_Posts = itemView.findViewById(R.id.HPI_CV_Posts);
            HPI_MTV_Name = itemView.findViewById(R.id.HPI_MTV_Name);
            HPI_MTV_RecipeName = itemView.findViewById(R.id.HPI_MTV_RecipeName);
            HPI_MTV_Likes = itemView.findViewById(R.id.HPI_MTV_Likes);
            RecipeView_MTV_UploadTime = itemView.findViewById(R.id.RecipeView_MTV_UploadTime);
            HPI_SIV_Like = itemView.findViewById(R.id.HPI_SIV_Like);
            HPI_SIV_RecipePicture = itemView.findViewById(R.id.HPI_SIV_RecipePicture);
            HPI_SIV_ProfilePicture = itemView.findViewById(R.id.HPI_SIV_ProfilePicture);

            HPI_CV_Posts.setOnClickListener(v -> {
                if(recipeCallback != null)
                    recipeCallback.RecipeClicked(getItem(getAdapterPosition()),getAdapterPosition());
            });
            HPI_SIV_Like.setOnClickListener(v -> {
                if(postCallback != null)
                    postCallback.likeButtonClicked(getItem(getAdapterPosition()),getAdapterPosition());
            });
        }
    }

}
