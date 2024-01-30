package com.sabanci.rateprofessors.controller;

import com.sabanci.rateprofessors.model.TeacherInfo;
import com.sabanci.rateprofessors.service.TeacherInfoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/teachers")
public class TeacherInfoController {
    private final TeacherInfoService teacherInfoService;

    public TeacherInfoController(TeacherInfoService teacherInfoService) {
        this.teacherInfoService = teacherInfoService;
    }

    @PostMapping
    public ResponseEntity<TeacherInfo> createTeacher(@RequestBody TeacherInfo teacherInfo) {
        TeacherInfo createdTeacher = teacherInfoService.createOrUpdateTeacher(teacherInfo);
        return ResponseEntity.ok(createdTeacher);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeacherInfo> getTeacherById(@PathVariable String id) {
        return teacherInfoService.getTeacherById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<TeacherInfo> getAllTeachers() {
        return teacherInfoService.getAllTeachers();
    }

    @PutMapping("/{id}")
    public ResponseEntity<TeacherInfo> updateTeacher(@PathVariable String id, @RequestBody TeacherInfo teacherInfo) {
        return teacherInfoService.getTeacherById(id)
                .map(t -> {
                    teacherInfo.setId(id); // Ensure the ID is set correctly
                    TeacherInfo updatedTeacher = teacherInfoService.createOrUpdateTeacher(teacherInfo);
                    return ResponseEntity.ok(updatedTeacher);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeacher(@PathVariable String id) {
        teacherInfoService.deleteTeacher(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{teacherId}/addClass/{classCode}")
    public ResponseEntity<Void> addClassToTeacher(@PathVariable String teacherId, @PathVariable String classCode) {
        teacherInfoService.addClassToTeacher(teacherId, classCode);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{teacherId}/removeClass/{classCode}")
    public ResponseEntity<Void> removeClassFromTeacher(@PathVariable String teacherId, @PathVariable String classCode) {
        teacherInfoService.removeClassFromTeacher(teacherId, classCode);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/findByInstructor/{instructorName}")
    public ResponseEntity<TeacherInfo> getTeacherByInstructorName(@PathVariable String instructorName) {
        Optional<TeacherInfo> teacherInfo = teacherInfoService.findByPrimaryInstructor(instructorName);
        return teacherInfo
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}
