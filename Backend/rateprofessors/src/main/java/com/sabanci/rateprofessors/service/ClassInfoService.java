package com.sabanci.rateprofessors.service;

import com.sabanci.rateprofessors.model.ClassInfo;
import com.sabanci.rateprofessors.model.TeacherInfo;
import com.sabanci.rateprofessors.repository.ClassInfoRepository;
import com.sabanci.rateprofessors.repository.TeacherInfoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.List;

@Service
public class ClassInfoService {
    private final ClassInfoRepository classInfoRepository;
    private final TeacherInfoRepository teacherInfoRepository;

    public ClassInfoService(ClassInfoRepository classInfoRepository, TeacherInfoRepository teacherInfoRepository) {
        this.classInfoRepository = classInfoRepository;
        this.teacherInfoRepository = teacherInfoRepository;
    }

    public ClassInfo createOrUpdateClassInfo(ClassInfo classInfo) {
        return classInfoRepository.save(classInfo);
    }

    public Optional<ClassInfo> getClassInfoById(String id) {
        return classInfoRepository.findById(id);
    }

    public List<ClassInfo> getAllClassInfos() {
        return classInfoRepository.findAll();
    }

    public void deleteClassInfo(String id) {
        classInfoRepository.deleteById(id);
    }

    @Transactional
    public void addProfessorToClass(String classId, String professorId) {
        Optional<ClassInfo> classInfoOpt = classInfoRepository.findById(classId);
        Optional<TeacherInfo> teacherInfoOpt = teacherInfoRepository.findById(professorId);

        if (classInfoOpt.isPresent() && teacherInfoOpt.isPresent()) {
            ClassInfo classInfo = classInfoOpt.get();
            TeacherInfo teacherInfo = teacherInfoOpt.get();

            classInfo.getInstructorIds().add(professorId);
            teacherInfo.getClassCodes().add(classInfo.getClassCode());

            classInfoRepository.save(classInfo);
            teacherInfoRepository.save(teacherInfo);
        }
    }

    @Transactional
    public void removeProfessorFromClass(String classId, String professorId) {
        Optional<ClassInfo> classInfoOpt = classInfoRepository.findById(classId);
        Optional<TeacherInfo> teacherInfoOpt = teacherInfoRepository.findById(professorId);

        if (classInfoOpt.isPresent() && teacherInfoOpt.isPresent()) {
            ClassInfo classInfo = classInfoOpt.get();
            TeacherInfo teacherInfo = teacherInfoOpt.get();

            classInfo.getInstructorIds().remove(professorId);
            teacherInfo.getClassCodes().remove(classInfo.getClassCode());

            classInfoRepository.save(classInfo);
            teacherInfoRepository.save(teacherInfo);
        }
    }

    public Optional<ClassInfo> findByClassCode(String classCode) {
        return classInfoRepository.findByClassCode(classCode);
    }


}
