package com.example.rateprofs;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import org.json.JSONObject;
import org.json.JSONException;
import com.example.rateprofs.databinding.FragmentReviewBinding;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import android.os.Handler;
import android.os.Looper;

public class ReviewFragment extends Fragment {

    private FragmentReviewBinding binding;
    private String professorId;
    private String studentId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentReviewBinding.inflate(inflater, container, false);

        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.getStudent().observe(getViewLifecycleOwner(), student -> {
            if (student != null) {
                studentId = student.getId();
            }
        });

        if (getArguments() != null) {
            professorId = getArguments().getString("teacherId");
        }

        binding.submitReviewButton.setOnClickListener(v -> {
            if (studentId != null && professorId != null) {
                float rating = binding.ratingBar.getRating();
                String comment = binding.commentEditText.getText().toString();
                submitReview(studentId, professorId, rating, comment);
            } else {
                Toast.makeText(getContext(), "Missing information", Toast.LENGTH_SHORT).show();
            }
        });

        return binding.getRoot();
    }

    private void submitReview(String studentId, String teacherId, float rating, String comment) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executorService.execute(() -> {
            try {
                JSONObject reviewJson = new JSONObject();
                reviewJson.put("studentId", studentId);
                reviewJson.put("teacherId", teacherId);
                reviewJson.put("rating", rating);
                reviewJson.put("comment", comment);

                URL url = new URL("http://10.0.2.2:8080/api/reviews");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestProperty("Content-Type", "application/json");
                try (OutputStream os = httpURLConnection.getOutputStream()) {
                    byte[] input = reviewJson.toString().getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                int responseCode = httpURLConnection.getResponseCode();
                handler.post(() -> {
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        Toast.makeText(getContext(), "Review submitted successfully", Toast.LENGTH_SHORT).show();
                        NavController navController = Navigation.findNavController(getActivity(), R.id.fragmentContainerView);
                        navController.popBackStack();
                    } else {
                        Toast.makeText(getContext(), "Error submitting review", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
                handler.post(() -> Toast.makeText(getContext(), "JSON error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            } catch (Exception e) {
                e.printStackTrace();
                handler.post(() -> Toast.makeText(getContext(), "Network error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }
}
