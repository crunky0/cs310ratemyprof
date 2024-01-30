package com.sabanci.rateprofessors.service;

import com.sabanci.rateprofessors.model.Review;
import com.sabanci.rateprofessors.model.Student;
import com.sabanci.rateprofessors.model.TeacherInfo;
import com.sabanci.rateprofessors.repository.ReviewRepository;
import com.sabanci.rateprofessors.repository.StudentRepository;
import com.sabanci.rateprofessors.repository.TeacherInfoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.List;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final TeacherInfoRepository teacherInfoRepository;
    private final StudentRepository studentRepository;

    public ReviewService(ReviewRepository reviewRepository, TeacherInfoRepository teacherInfoRepository, StudentRepository studentRepository) {
        this.reviewRepository = reviewRepository;
        this.teacherInfoRepository = teacherInfoRepository;
        this.studentRepository = studentRepository;
    }

    public Optional<Review> getReviewById(String id) {
        return reviewRepository.findById(id);
    }

    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }



    @Transactional
    public Review createReview(Review review) {

        Review savedReview = reviewRepository.save(review);

        updateTeacherRatingAndLinkReview(savedReview);

        linkReviewToStudent(savedReview);

        return savedReview;
    }

    @Transactional
    public void deleteReview(String reviewId) {
        Optional<Review> reviewOpt = reviewRepository.findById(reviewId);
        if (reviewOpt.isPresent()) {
            Review review = reviewOpt.get();

            removeReviewFromTeacher(review.getTeacherId(), reviewId, review.getRating());
            removeReviewFromStudent(review.getStudentId(), reviewId);

            reviewRepository.deleteById(reviewId);
        }

    }

    private void removeReviewFromTeacher(String teacherId, String reviewId, double rating) {
        Optional<TeacherInfo> teacherOpt = teacherInfoRepository.findById(teacherId);
        if (teacherOpt.isPresent()) {
            TeacherInfo teacher = teacherOpt.get();
            teacher.getReviewIds().remove(reviewId);
            recalculateTeacherRatingAfterDeletion(teacher, rating);
            teacherInfoRepository.save(teacher);
        }

    }

    private void recalculateTeacherRatingAfterDeletion(TeacherInfo teacher, double rating) {
        double totalRating = teacher.getAverageRating() * teacher.getNumOfRatings();
        totalRating -= rating;
        int newNumberOfRatings = teacher.getNumOfRatings() - 1;
        teacher.setNumOfRatings(newNumberOfRatings);
        if (newNumberOfRatings > 0) {
            teacher.setAverageRating(totalRating / newNumberOfRatings);
        } else {
            teacher.setAverageRating(0);
        }
    }

    private void removeReviewFromStudent(String studentId, String reviewId) {
        Optional<Student> studentOpt = studentRepository.findById(studentId);
        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            student.getReviewIds().remove(reviewId);
            studentRepository.save(student);
        }

    }

    private void updateTeacherRatingAndLinkReview(Review review) {
        Optional<TeacherInfo> teacherOpt = teacherInfoRepository.findById(review.getTeacherId());
        if (teacherOpt.isPresent()) {
            TeacherInfo teacher = teacherOpt.get();
            double newAverage = calculateNewAverage(teacher, review.getRating());
            teacher.setAverageRating(newAverage);
            teacher.setNumOfRatings(teacher.getNumOfRatings() + 1);
            teacher.getReviewIds().add(review.getId());
            teacherInfoRepository.save(teacher);
        }

    }

    private double calculateNewAverage(TeacherInfo teacher, double newRating) {
        double totalRating = teacher.getAverageRating() * teacher.getNumOfRatings();
        totalRating += newRating;
        return totalRating / (teacher.getNumOfRatings() + 1);
    }
    @Transactional
    public boolean updateReview(String reviewId, Double newRating, String newComment) {
        Optional<Review> reviewOpt = reviewRepository.findById(reviewId);
        if (reviewOpt.isPresent()) {
            Review review = reviewOpt.get();


            double oldRating = review.getRating();
            double ratingDifference = newRating - oldRating;


            review.setRating(newRating);
            review.setComment(newComment);
            reviewRepository.save(review);


            updateTeacherRating(review.getTeacherId(), ratingDifference);

            return true;
        } else {
            // Review not found
            return false;
        }
    }

    private void updateTeacherRating(String teacherId, double ratingDifference) {
        Optional<TeacherInfo> teacherOpt = teacherInfoRepository.findById(teacherId);
        if (teacherOpt.isPresent()) {
            TeacherInfo teacher = teacherOpt.get();
            double totalRating = teacher.getAverageRating() * teacher.getNumOfRatings();
            totalRating += ratingDifference;
            teacher.setAverageRating(totalRating / teacher.getNumOfRatings());
            teacherInfoRepository.save(teacher);
        }

    }


    private void linkReviewToStudent(Review review) {
        Optional<Student> studentOpt = studentRepository.findById(review.getStudentId());
        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            student.getReviewIds().add(review.getId());
            studentRepository.save(student);
        }

    }



    public List<Review> findAllReviewsByTeacherId(String teacherId) {

        return reviewRepository.findByTeacherId(teacherId);
    }

    public List<Review> findAllReviewsByStudentId(String studentId) {

        return reviewRepository.findByStudentId(studentId);
    }





}
