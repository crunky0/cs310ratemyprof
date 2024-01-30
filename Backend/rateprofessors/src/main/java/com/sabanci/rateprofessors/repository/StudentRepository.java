package com.sabanci.rateprofessors.repository;

import com.sabanci.rateprofessors.model.Student;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends MongoRepository<Student, String> {
    List<Student> findByName(String name);
}
