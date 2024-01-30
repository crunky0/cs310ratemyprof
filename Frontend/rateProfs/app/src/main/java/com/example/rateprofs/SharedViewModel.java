package com.example.rateprofs;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<Student> student = new MutableLiveData<>();

    public void setStudent(Student studentData) {
        student.setValue(studentData);
    }

    public LiveData<Student> getStudent() {
        return student;
    }
}

