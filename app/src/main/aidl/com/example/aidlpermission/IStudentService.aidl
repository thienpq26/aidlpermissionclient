// IStudentService.aidl
package com.example.aidlpermission;
import com.example.aidlpermission.Student;
import java.util.List;
// Declare any non-default types here with import statements

interface IStudentService {
    void addStudent(int id, String name, String phone, String address, String email);
    List<Student> getStudents();
    Student getStudent(int id);
}
