package com.sabanci.rateprofessors.service;

import com.sabanci.rateprofessors.model.Student;
import com.sabanci.rateprofessors.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {
    private final StudentRepository repository;

    public StudentService(StudentRepository repository) {
        this.repository = repository;
    }

    public Student createOrUpdateStudent(Student student) {
        return repository.save(student);
    }

    public Optional<Student> getStudentById(String id) {
        return repository.findById(id);
    }

    public List<Student> getAllStudents() {
        return repository.findAll();
    }

    public void deleteStudent(String id) {
        repository.deleteById(id);
    }

    public List<Student> findByName(String name) {
        return repository.findByName(name);
    }
}
