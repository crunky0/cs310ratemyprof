package com.example.rateprofs;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.rateprofs.databinding.FragmentClassBinding;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.os.Handler;

public class ClassFragment extends Fragment {

    private FragmentClassBinding binding;
    private InstructorAdapter adapter;
    List<String> instructorList = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentClassBinding.inflate(inflater, container, false);
        String classDetailsJson = getArguments().getString("classDetails");

        if (classDetailsJson != null) {
            displayClassDetails(classDetailsJson);
        }

        // Initialize RecyclerView and Adapter
        adapter = new InstructorAdapter(getContext(), instructorList);
        binding.instructorsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.instructorsRecyclerView.setAdapter(adapter);

        return binding.getRoot();
    }

    private void fetchInstructorDetails(String instructorId) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executorService.execute(() -> {
            try {
                URL url = new URL("http://10.0.2.2:8080/api/teachers/" + instructorId);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                String responseBody = response.toString();

                handler.post(() -> {
                    updateRecyclerView(responseBody);
                });
            } catch (Exception e) {
                e.printStackTrace();
                handler.post(() -> {
                });
            }
        });
    }




    private void displayClassDetails(String classDetailsJson) {
        try {
            instructorList.clear();
            JSONObject jsonObject = new JSONObject(classDetailsJson);
            String classCode = jsonObject.getString("classCode");
            String className = jsonObject.getString("className");
            JSONArray instructorIdsJsonArray = jsonObject.getJSONArray("instructorIds");

            binding.classCodeTextView.setText("Code: " + classCode);
            binding.classNameTextView.setText("Name: " + className);

            for (int i = 0; i < instructorIdsJsonArray.length(); i++) {
                String instructorId = instructorIdsJsonArray.getString(i);
                fetchInstructorDetails(instructorId);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateRecyclerView(String professorDetails) {
        instructorList.add(professorDetails);
        adapter.notifyDataSetChanged();
    }

}
