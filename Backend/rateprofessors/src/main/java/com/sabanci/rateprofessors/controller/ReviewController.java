package com.sabanci.rateprofessors.controller;

import com.sabanci.rateprofessors.model.Review;
import com.sabanci.rateprofessors.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<Review> createReview(@RequestBody Review review) {
        Review createdReview = reviewService.createReview(review);
        return ResponseEntity.ok(createdReview);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Review> getReviewById(@PathVariable String id) {
        return reviewService.getReviewById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<Review>> getAllReviewsForTeacher(@PathVariable String teacherId) {
        List<Review> reviews = reviewService.findAllReviewsByTeacherId(teacherId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<Review>> getAllReviewsForStudent(@PathVariable String studentId) {
        List<Review> reviews = reviewService.findAllReviewsByStudentId(studentId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping
    public List<Review> getAllReviews() {
        return reviewService.getAllReviews();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateReview(@PathVariable String id,
                                          @RequestParam double newRating,
                                          @RequestParam String newComment) {
        boolean updateSuccessful = reviewService.updateReview(id, newRating, newComment);
        if (updateSuccessful) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable String id) {
        reviewService.deleteReview(id);
        return ResponseEntity.ok().build();
    }


}
