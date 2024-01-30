package com.sabanci.rateprofessors.service;

import com.sabanci.rateprofessors.model.TeacherInfo;
import com.sabanci.rateprofessors.repository.TeacherInfoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TeacherInfoService {
    private final TeacherInfoRepository teacherInfoRepository;

    public TeacherInfoService(TeacherInfoRepository teacherInfoRepository) {
        this.teacherInfoRepository = teacherInfoRepository;
    }

    public TeacherInfo createOrUpdateTeacher(TeacherInfo teacherInfo) {
        return teacherInfoRepository.save(teacherInfo);
    }

    public Optional<TeacherInfo> getTeacherById(String id) {
        return teacherInfoRepository.findById(id);
    }

    public List<TeacherInfo> getAllTeachers() {
        return teacherInfoRepository.findAll();
    }

    public void deleteTeacher(String id) {
        teacherInfoRepository.deleteById(id);
    }

    @Transactional
    public void addClassToTeacher(String teacherId, String classCode) {
        Optional<TeacherInfo> teacherInfoOpt = teacherInfoRepository.findById(teacherId);
        if (teacherInfoOpt.isPresent()) {
            TeacherInfo teacherInfo = teacherInfoOpt.get();
            teacherInfo.getClassCodes().add(classCode);
            teacherInfoRepository.save(teacherInfo);
        }
    }

    @Transactional
    public void removeClassFromTeacher(String teacherId, String classCode) {
        Optional<TeacherInfo> teacherInfoOpt = teacherInfoRepository.findById(teacherId);
        if (teacherInfoOpt.isPresent()) {
            TeacherInfo teacherInfo = teacherInfoOpt.get();
            teacherInfo.getClassCodes().remove(classCode);
            teacherInfoRepository.save(teacherInfo);
        }
    }

    public Optional<TeacherInfo> findByPrimaryInstructor(String primaryInstructor) {
        return teacherInfoRepository.findByPrimaryInstructor(primaryInstructor);
    }




}
