package com.sabanci.rateprofessors.repository;

import com.sabanci.rateprofessors.model.ClassInfo;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ClassInfoRepository extends MongoRepository<ClassInfo, String> {
    Optional<ClassInfo> findByClassCode(String classCode);

}
