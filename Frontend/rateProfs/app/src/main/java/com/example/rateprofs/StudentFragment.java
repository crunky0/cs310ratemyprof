package com.example.rateprofs;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StudentFragment extends Fragment {

    private TextView nameTextView, majorTextView, classesTextView;
    private LinearLayout reviewsContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student, container, false);

        nameTextView = view.findViewById(R.id.studentNameTextView);
        majorTextView = view.findViewById(R.id.studentMajorTextView);
        classesTextView = view.findViewById(R.id.studentClassesTextView);
        reviewsContainer = view.findViewById(R.id.reviewsContainer);

        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.getStudent().observe(getViewLifecycleOwner(), this::displayStudentInfo);

        return view;
    }

    private void displayStudentInfo(Student student) {
        nameTextView.setText(student.getName());
        majorTextView.setText(student.getMajor());

        StringBuilder classesBuilder = new StringBuilder();
        for (String course : student.getClasses()) {
            classesBuilder.append(course).append("\n");
        }
        classesTextView.setText(classesBuilder.toString());

        fetchAndDisplayStudentReviews(student.getId());
    }

    private void fetchAndDisplayStudentReviews(String studentId) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executorService.execute(() -> {
            try {
                URL url = new URL("http://10.0.2.2:8080/api/reviews/student/" + studentId);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONArray reviewsArray = new JSONArray(response.toString());
                for (int i = 0; i < reviewsArray.length(); i++) {
                    JSONObject review = reviewsArray.getJSONObject(i);
                    String comment = review.getString("comment");
                    double rating = review.getDouble("rating");
                    String teacherId = review.getString("teacherId");

                    fetchTeacherName(teacherId, comment, rating, handler);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void fetchTeacherName(String teacherId, String comment, double rating, Handler handler) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(() -> {
            try {
                URL url = new URL("http://10.0.2.2:8080/api/teachers/" + teacherId);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONObject teacher = new JSONObject(response.toString());
                String teacherName = teacher.getString("primaryInstructor");

                handler.post(() -> addReviewToContainer(teacherName, comment, rating));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void addReviewToContainer(String teacherName, String comment, double rating) {
        TextView reviewView = new TextView(getContext());
        String reviewText = "Teacher: " + teacherName + "\nRating: " + rating + "\nComment: " + comment + "\n\n";
        reviewView.setText(reviewText);
        reviewView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        reviewView.setPadding(10, 10, 10, 10); // Add padding for readability

        reviewsContainer.addView(reviewView);
    }
}
