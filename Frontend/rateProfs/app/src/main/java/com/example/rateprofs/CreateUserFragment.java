package com.example.rateprofs;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.rateprofs.databinding.FragmentCreateUserBinding;
import com.example.rateprofs.databinding.FragmentUserLoginBinding;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.rateprofs.databinding.FragmentUserLoginBinding;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import android.os.Handler;
import android.os.Looper;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import org.json.JSONArray;

public class CreateUserFragment extends Fragment {

    FragmentCreateUserBinding binding;
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentCreateUserBinding.inflate(inflater, container, false);

        binding.button3.setOnClickListener(v -> {
            String username = binding.editTextText3.getText().toString();
            String major = binding.editTextText4.getText().toString();
            String courses = binding.editTextText5.getText().toString();
            List<String> courseList = Arrays.asList(courses.split(","));

            JSONObject studentJson = new JSONObject();
            try {
                studentJson.put("name", username);
                studentJson.put("major", major);
                studentJson.put("classes", new JSONArray(courseList));
                studentJson.put("reviewIds", new JSONArray());

                // Execute HTTP POST request
                executorService.execute(() -> {
                    try {
                        URL url = new URL("http://10.0.2.2:8080/api/students");
                        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                        httpURLConnection.setRequestMethod("POST");
                        httpURLConnection.setDoOutput(true);
                        httpURLConnection.setRequestProperty("Content-Type", "application/json");

                        try (OutputStream os = httpURLConnection.getOutputStream()) {
                            byte[] input = studentJson.toString().getBytes(StandardCharsets.UTF_8);
                            os.write(input, 0, input.length);
                        }

                        // Read the response
                        try (BufferedReader br = new BufferedReader(
                                new InputStreamReader(httpURLConnection.getInputStream(), StandardCharsets.UTF_8))) {
                            StringBuilder response = new StringBuilder();
                            String responseLine;
                            while ((responseLine = br.readLine()) != null) {
                                response.append(responseLine.trim());
                            }
                            handler.post(() -> {
                                NavController navController = Navigation.findNavController(getActivity(), R.id.fragmentContainerView);
                                navController.navigate(R.id.action_createUserFragment_to_userLoginFragment);
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        return binding.getRoot();
    }
}
