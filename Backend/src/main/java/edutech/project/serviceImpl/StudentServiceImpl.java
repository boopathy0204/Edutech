package edutech.project.serviceImpl;

import edutech.project.dto.request.StudentRequestDTO;
import edutech.project.dto.response.BulkImportRowResultDTO;
import edutech.project.dto.response.BulkImportSummaryDTO;
import edutech.project.dto.response.StudentResponseDTO;
import edutech.project.exception.DuplicateResourceException;
import edutech.project.exception.ResourceNotFoundException;
import edutech.project.model.Student;
import edutech.project.model.User;
import edutech.project.repository.StudentRepo;
import edutech.project.repository.UserRepo;
import edutech.project.service.AcademicPeriodService;
import edutech.project.service.StudentService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {
    @Autowired
    private StudentRepo studentRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private AcademicPeriodService academicPeriodService;

    @Override
    public StudentResponseDTO createStudent(StudentRequestDTO request){
        User user = userRepo.findById(request.getUserId()).orElseThrow(()-> new ResourceNotFoundException("User not found"));
        if(studentRepo.existsByUser(user)){
            throw new DuplicateResourceException("This user is already assigned to another student");
        }
        if(studentRepo.existsByRegistrationNumber(request.getRegistrationNumber())){
            throw new DuplicateResourceException("RegistrationNumber already exist");
        }
        Student student=new Student();
        student.setRegistrationNumber(request.getRegistrationNumber());
        student.setFirstName(request.getFirstName());
        student.setLastName(request.getLastName());
        student.setPhone(request.getPhone());
        student.setProgram(request.getProgram());
        student.setDepartment(request.getDepartment());
        student.setUser(user);
        
        student.setAcademicPeriod(academicPeriodService.getActivePeriod());
        
        //save
        Student saved=studentRepo.save(student);
        return maptoResponse(saved);
    }

    @Override
    public StudentResponseDTO getStudentById(Long studentId){
         Student student =studentRepo.findById(studentId).orElseThrow(()-> new ResourceNotFoundException("student not found"));
         return maptoResponse(student);
    }
    @Override
    public List<StudentResponseDTO> getAllStudent (String query){
        List<Student> students = studentRepo.findAll();
        
        if (query != null && !query.isBlank()) {
            String q = query.trim().toLowerCase();
            students = students.stream().filter(s -> 
                (s.getFirstName() != null && s.getFirstName().toLowerCase().contains(q)) || 
                (s.getLastName() != null && s.getLastName().toLowerCase().contains(q)) || 
                ((s.getFirstName() + " " + s.getLastName()).toLowerCase().contains(q)) || 
                (s.getRegistrationNumber() != null && s.getRegistrationNumber().toLowerCase().contains(q)) || 
                (s.getDepartment() != null && s.getDepartment().toLowerCase().contains(q)) || 
                (s.getProgram() != null && s.getProgram().toLowerCase().contains(q)) ||
                (s.getUser() != null && s.getUser().getEmail() != null && s.getUser().getEmail().toLowerCase().contains(q))
            ).toList();
        }

        List<StudentResponseDTO> response=new ArrayList<>();
        for(Student std:students){
            response.add(maptoResponse(std));
        }
        return response;
    }

    @Override
    public void deleteStudent(Long studentId){
        Student student =studentRepo.findById(studentId).orElseThrow(()-> new ResourceNotFoundException("student not found"));
        studentRepo.delete(student);
    }
    @Override
    public StudentResponseDTO updateStudent(Long studentId,StudentRequestDTO request){
        Student student =studentRepo.findById(studentId).orElseThrow(()-> new ResourceNotFoundException("student not found"));
        if(studentRepo.existsByRegistrationNumberAndStudentIdNot(request.getRegistrationNumber(), studentId)){
            throw new DuplicateResourceException("RegistrationNumber already exists");
        }
        User user = userRepo.findById(request.getUserId()).orElseThrow(()-> new ResourceNotFoundException("User not found"));
        if(studentRepo.existsByUserAndStudentIdNot(user,studentId)){
            throw new DuplicateResourceException("This user is already assigned to another student");
        }
        student.setRegistrationNumber(request.getRegistrationNumber());
        student.setFirstName(request.getFirstName());
        student.setLastName(request.getLastName());
        student.setPhone(request.getPhone());
        student.setDepartment(request.getDepartment());
        student.setUser(user);
        //save
        Student saved=studentRepo.save(student);
        return maptoResponse(saved);
    }

    @Override
    public StudentResponseDTO getStudentByUser(User user) {
        Student student = studentRepo.findByUser(user).orElseThrow(() -> new ResourceNotFoundException("Student profile not found for this user"));
        return maptoResponse(student);
    }

    private StudentResponseDTO maptoResponse(Student student){
        return StudentResponseDTO.builder()
                .studentId(student.getStudentId())
                .registrationNumber(student.getRegistrationNumber())
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .phone(student.getPhone())
                .department(student.getDepartment())
                .program(student.getProgram())
                .created_at(student.getCreated_at())
                .userId(student.getUser().getUserId())
                .username(student.getUser().getUsername())
                .academicYear(student.getAcademicYear())
                .academicHalf(student.getAcademicHalf())
                .build();
    }
}
