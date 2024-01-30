package com.sabanci.rateprofessors.repository;

import com.sabanci.rateprofessors.model.TeacherInfo;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface TeacherInfoRepository extends MongoRepository<TeacherInfo, String> {
    Optional<TeacherInfo> findByPrimaryInstructor(String primaryInstructor);
}
