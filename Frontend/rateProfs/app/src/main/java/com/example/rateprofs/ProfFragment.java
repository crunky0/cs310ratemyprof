package com.example.rateprofs;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import com.example.rateprofs.databinding.FragmentProfBinding;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;
import java.util.HashSet;
import java.util.Set;

public class ProfFragment extends Fragment {

    FragmentProfBinding binding;
    private String professorId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfBinding.inflate(inflater, container, false);

        if (getArguments() != null) {
            String professorDetailsJson = getArguments().getString("professorDetails");
            if (professorDetailsJson != null) {
                displayProfessorDetails(professorDetailsJson);
            }
        }

        binding.reviewButton.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("teacherId", professorId); // Replace with the actual variable holding the ID
            NavController navController = Navigation.findNavController(getActivity(), R.id.fragmentContainerView);
            navController.navigate(R.id.action_profFragment_to_reviewFragment, bundle);
        });




        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (professorId != null) {
            fetchProfessorDetails(professorId);
        }
    }

    private void fetchProfessorDetails(String professorId) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executorService.execute(() -> {
            try {
                URL url = new URL("http://10.0.2.2:8080/api/teachers/" + professorId);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    response.append(line);
                }
                bufferedReader.close();

                String professorDetailsJson = response.toString();

                handler.post(() -> displayProfessorDetails(professorDetailsJson));
            } catch (Exception e) {
                e.printStackTrace();
                handler.post(() -> {
                    Toast.makeText(getContext(), "Failed to fetch professor details", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
    private Set<String> processedReviewIds = ConcurrentHashMap.newKeySet();


    private void displayProfessorDetails(String professorDetailsJson) {
        try {
            JSONObject jsonObject = new JSONObject(professorDetailsJson);
            professorId = jsonObject.getString("id");

            String primaryInstructor = jsonObject.getString("primaryInstructor");
            JSONArray classCodesJsonArray = jsonObject.getJSONArray("classCodes");
            double averageRating = jsonObject.getDouble("averageRating");
            int numOfRatings = jsonObject.getInt("numOfRatings");

            binding.professorNameTextView.setText("Name: " + primaryInstructor);
            binding.professorClassesTextView.setText("Classes: " + idsToString(classCodesJsonArray));
            binding.professorRatingTextView.setText("Average Rating: " + String.format("%.2f", averageRating));
            binding.professorNumOfRatingsTextView.setText("Number of Ratings: " + numOfRatings);

            JSONArray reviewIdsJsonArray = jsonObject.getJSONArray("reviewIds");

            getActivity().runOnUiThread(() -> {
                binding.reviewsContainer.removeAllViews();
            });

            processedReviewIds.clear();


            for (int i = 0; i < reviewIdsJsonArray.length(); i++) {
                String reviewId = reviewIdsJsonArray.getString(i);
                if (processedReviewIds.add(reviewId)) {
                    fetchReviewDetails(reviewId);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void fetchReviewDetails(String reviewId) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executorService.execute(() -> {
            try {
                URL url = new URL("http://10.0.2.2:8080/api/reviews/" + reviewId);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    response.append(line);
                }
                bufferedReader.close();
                String reviewJson = response.toString();

                handler.post(() -> displayReview(reviewJson));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void displayReview(String reviewJson) {
        getActivity().runOnUiThread(() -> {
            try {
                // Parse JSON and create the review view
                JSONObject jsonObject = new JSONObject(reviewJson);
                double rating = jsonObject.getDouble("rating");
                String comment = jsonObject.getString("comment");

                TextView reviewView = new TextView(getContext());
                reviewView.setText("Rating: " + rating + "\nComment: " + comment);
                binding.reviewsContainer.addView(reviewView);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }



    private String idsToString(JSONArray jsonArray) throws JSONException {
        StringBuilder ids = new StringBuilder();
        for (int i = 0; i < jsonArray.length(); i++) {
            ids.append(jsonArray.getString(i));
            if (i < jsonArray.length() - 1) {
                ids.append(", ");
            }
        }
        return ids.toString();
    }
}
