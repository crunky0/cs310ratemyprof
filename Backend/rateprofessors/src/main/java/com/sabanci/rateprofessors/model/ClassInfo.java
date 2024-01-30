package com.sabanci.rateprofessors.model;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "class_info")

public class ClassInfo {
    @Id
    private String id;
    private String classCode;
    private String className;
    private List<String> instructorIds; // List of teacher IDs

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List<String> getInstructorIds() {
        return instructorIds;
    }

    public void setInstructorIds(List<String> instructorIds) {
        this.instructorIds = instructorIds;
    }

}

