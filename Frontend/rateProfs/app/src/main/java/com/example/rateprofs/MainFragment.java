package com.example.rateprofs;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.rateprofs.databinding.FragmentMainBinding;
import com.example.rateprofs.databinding.FragmentUserLoginBinding;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.rateprofs.databinding.FragmentMainBinding;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;



public class MainFragment extends Fragment {

    FragmentMainBinding binding;
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    Handler handler = new Handler(Looper.getMainLooper());


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentMainBinding.inflate(inflater, container, false);



        binding.button5.setOnClickListener(v -> {
            String instructorName = binding.editTextText.getText().toString();
            if (!instructorName.isEmpty()) {
                searchProfessor(instructorName);
            } else {
                Toast.makeText(getContext(), "Please enter an instructor name", Toast.LENGTH_SHORT).show();
            }
        });

        binding.button6.setOnClickListener(v -> {
            String courseCode = binding.editTextText.getText().toString();
            if (!courseCode.isEmpty()) {
                searchCourse(courseCode);
            } else {
                Toast.makeText(getContext(), "Please enter a course code", Toast.LENGTH_SHORT).show();
            }
        });


        binding.button9.setOnClickListener(v -> {

            NavController navController = Navigation.findNavController(getActivity(), R.id.fragmentContainerView);
            navController.navigate(R.id.action_mainFragment_to_studentFragment);
        });



        return binding.getRoot();


    }
    private void searchCourse(String courseCode) {
        executorService.execute(() -> {
            try {
                URL url = new URL("http://10.0.2.2:8080/api/classes/findByCode/" + courseCode);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    response.append(line);
                }
                bufferedReader.close();

                String classDetails = response.toString();

                handler.post(() -> {
                    Bundle bundle = new Bundle();
                    bundle.putString("classDetails", classDetails);

                    NavController navController = Navigation.findNavController(getActivity(), R.id.fragmentContainerView);
                    navController.navigate(R.id.action_mainFragment_to_classFragment, bundle);
                });
            } catch (Exception e) {
                e.printStackTrace();
                handler.post(() -> Toast.makeText(getContext(), "Error fetching course data", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void searchProfessor(String instructorName) {
        executorService.execute(() -> {
            try {
                URL url = new URL("http://10.0.2.2:8080/api/teachers/findByInstructor/" + instructorName);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    response.append(line);
                }
                bufferedReader.close();

                handler.post(() -> {
                    Bundle bundle = new Bundle();
                    bundle.putString("professorDetails", response.toString());

                    NavController navController = Navigation.findNavController(getActivity(), R.id.fragmentContainerView);
                    navController.navigate(R.id.action_mainFragment_to_profFragment, bundle);
                });
            } catch (Exception e) {
                e.printStackTrace();
                handler.post(() -> Toast.makeText(getContext(), "Error fetching professor data", Toast.LENGTH_SHORT).show());
            }
        });
    }

}