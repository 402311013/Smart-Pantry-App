package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
    }

    private final List<Recipe> recipeList;
    private final OnRecipeClickListener listener;

    public RecipeAdapter(List<Recipe> recipeList, OnRecipeClickListener listener) {
        this.recipeList = recipeList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recipe recipe = recipeList.get(position);
        holder.tvName.setText(recipe.getName());
        holder.tvPrepTime.setText(recipe.getPrepTime());

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < recipe.getIngredients().size(); i++) {
            RecipeIngredient ing = recipe.getIngredients().get(i);
            sb.append(ing.getQuantity()).append(" ").append(ing.getUnit()).append(" ").append(ing.getName());
            if (i < recipe.getIngredients().size() - 1) {
                sb.append(", ");
            }
        }
        holder.tvIngredients.setText(sb.toString());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRecipeClick(recipe);
            }
        });
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrepTime, tvIngredients;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvRecipeName);
            tvPrepTime = itemView.findViewById(R.id.tvRecipePrepTime);
            tvIngredients = itemView.findViewById(R.id.tvRecipeIngredients);
        }
    }
}
