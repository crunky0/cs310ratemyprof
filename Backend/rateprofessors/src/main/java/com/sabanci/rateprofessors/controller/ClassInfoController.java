package com.sabanci.rateprofessors.controller;

import com.sabanci.rateprofessors.model.ClassInfo;
import com.sabanci.rateprofessors.service.ClassInfoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/classes")
public class ClassInfoController {
    private final ClassInfoService classInfoService;

    public ClassInfoController(ClassInfoService classInfoService) {
        this.classInfoService = classInfoService;
    }

    @PostMapping
    public ResponseEntity<ClassInfo> createClassInfo(@RequestBody ClassInfo classInfo) {
        ClassInfo createdClassInfo = classInfoService.createOrUpdateClassInfo(classInfo);
        return ResponseEntity.ok(createdClassInfo);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClassInfo> getClassInfoById(@PathVariable String id) {
        return classInfoService.getClassInfoById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<ClassInfo> getAllClassInfos() {
        return classInfoService.getAllClassInfos();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClassInfo> updateClassInfo(@PathVariable String id, @RequestBody ClassInfo classInfo) {
        return classInfoService.getClassInfoById(id)
                .map(existingClassInfo -> {
                    classInfo.setId(id); // Ensure the ID is set correctly
                    ClassInfo updatedClassInfo = classInfoService.createOrUpdateClassInfo(classInfo);
                    return ResponseEntity.ok(updatedClassInfo);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClassInfo(@PathVariable String id) {
        classInfoService.deleteClassInfo(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{classId}/addProfessor/{professorId}")
    public ResponseEntity<Void> addProfessorToClass(@PathVariable String classId, @PathVariable String professorId) {
        classInfoService.addProfessorToClass(classId, professorId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{classId}/removeProfessor/{professorId}")
    public ResponseEntity<Void> removeProfessorFromClass(@PathVariable String classId, @PathVariable String professorId) {
        classInfoService.removeProfessorFromClass(classId, professorId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/findByCode/{classCode}")
    public ResponseEntity<ClassInfo> getClassInfoByCode(@PathVariable String classCode) {
        Optional<ClassInfo> classInfo = classInfoService.findByClassCode(classCode);
        return classInfo
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
