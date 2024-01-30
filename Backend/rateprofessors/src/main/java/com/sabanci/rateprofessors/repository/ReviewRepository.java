package com.sabanci.rateprofessors.repository;

import com.sabanci.rateprofessors.model.Review;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ReviewRepository extends MongoRepository<Review, String> {
    List<Review> findByTeacherId(String teacherId);
    List<Review> findByStudentId(String studentId);
}
