package com.example.rateprofs;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class InstructorAdapter extends RecyclerView.Adapter<InstructorAdapter.ViewHolder> {

    List<String> instructorList = new ArrayList<>();
    private final Context context;

    public InstructorAdapter(Context context, List<String> instructorList) {
        this.context = context;
        this.instructorList = instructorList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_instructor, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String instructor = instructorList.get(position);
        holder.bind(instructor);
    }

    @Override
    public int getItemCount() {
        return instructorList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView instructorNameTextView;

        ViewHolder(View itemView) {
            super(itemView);
            instructorNameTextView = itemView.findViewById(R.id.instructorNameTextView);
        }

        void bind(String professorDetails) {
            try {
                JSONObject jsonObject = new JSONObject(professorDetails);

                String primaryInstructor = jsonObject.getString("primaryInstructor");
                String instructorId = jsonObject.getString("id");

                instructorNameTextView.setText(primaryInstructor);

                itemView.setOnClickListener(v -> {
                    Bundle bundle = new Bundle();
                    bundle.putString("professorId", instructorId);
                    bundle.putString("professorDetails", professorDetails);
                    NavController navController = Navigation.findNavController(itemView);
                    navController.navigate(R.id.action_classFragment_to_profFragment, bundle);
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }
}

