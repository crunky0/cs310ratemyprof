package com.example.rateprofs;

// Import statements
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
import org.json.JSONObject;
import java.util.ArrayList;
import org.json.JSONArray;


public class userLoginFragment extends Fragment {

    FragmentUserLoginBinding binding;
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUserLoginBinding.inflate(inflater, container, false);

        binding.button.setOnClickListener(v -> {
            String studentName = binding.editTextText2.getText().toString();
            String urlString = "http://10.0.2.2:8080/api/students/findByName/" + studentName;

            executorService.execute(() -> {
                try {
                    URL url = new URL(urlString);
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
                        try {
                            JSONArray jsonResponseArray = new JSONArray(response.toString());

                            // Assuming you want the first student object in the array
                            if (jsonResponseArray.length() > 0) {
                                JSONObject studentJsonObject = jsonResponseArray.getJSONObject(0);

                                Student student = new Student();
                                student.setId(studentJsonObject.optString("id"));
                                student.setName(studentJsonObject.getString("name"));
                                student.setMajor(studentJsonObject.getString("major"));

                                // Parsing classes
                                JSONArray classesJsonArray = studentJsonObject.optJSONArray("classes");
                                List<String> classesList = new ArrayList<>();
                                if (classesJsonArray != null) {
                                    for (int i = 0; i < classesJsonArray.length(); i++) {
                                        classesList.add(classesJsonArray.getString(i));
                                    }
                                }
                                student.setClasses(classesList);

                                // Parsing reviewIds
                                JSONArray reviewIdsJsonArray = studentJsonObject.optJSONArray("reviewIds");
                                List<String> reviewIdsList = new ArrayList<>();
                                if (reviewIdsJsonArray != null) {
                                    for (int i = 0; i < reviewIdsJsonArray.length(); i++) {
                                        reviewIdsList.add(reviewIdsJsonArray.getString(i));
                                    }
                                }
                                student.setReviewIds(reviewIdsList);

                                SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
                                sharedViewModel.setStudent(student);

                                NavController navController = Navigation.findNavController(getActivity(), R.id.fragmentContainerView);
                                navController.navigate(R.id.action_userLoginFragment_to_mainFragment);


                            } else {
                                NavController navController = Navigation.findNavController(getActivity(), R.id.fragmentContainerView);
                                navController.navigate(R.id.action_userLoginFragment_to_createUserFragment);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });




        binding.button2.setOnClickListener(v -> {

            NavController navController = Navigation.findNavController(getActivity(), R.id.fragmentContainerView);
            navController.navigate(R.id.action_userLoginFragment_to_createUserFragment);


        });



        return binding.getRoot();
    }
}