package edutech.project.serviceImpl;

import edutech.project.dto.request.CMRequestDTO;
import edutech.project.dto.response.CMResponseDTO;
import edutech.project.exception.ResourceNotFoundException;
import edutech.project.model.Course;
import edutech.project.model.CourseMaterial;
import edutech.project.model.Enrollment;
import edutech.project.repository.CourseMaterialRepo;
import edutech.project.repository.CourseRepo;
import edutech.project.repository.EnrollmentRepo;
import edutech.project.service.CMService;
import edutech.project.service.NotificationService;
import edutech.project.storage.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class CMServiceImpl implements CMService {
    @Autowired
    private CourseMaterialRepo cmRepo;
    @Autowired
    private CourseRepo courseRepo;
    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private EnrollmentRepo enrollmentRepo;

    @Override
    public CMResponseDTO uploadMaterial(CMRequestDTO request, MultipartFile file) {
        Course course = courseRepo.findById(request.getCourseId()).orElseThrow(() -> new ResourceNotFoundException("Count not found"));
        String path = fileStorageService.uploadFile(file, "Course-material");
        CourseMaterial cm = new CourseMaterial();
        cm.setTitle(request.getTitle());
        cm.setFileUrl(path);
        cm.setCourse(course);
        cm.setFileName(file.getOriginalFilename());
        CourseMaterial saved = cmRepo.save(cm);

        //Notification

        List<Enrollment> enrollments = enrollmentRepo.findByCourse(course);
        for (Enrollment enrollment : enrollments) {
            notificationService.createNotification(enrollment.getStudent(), "New Course Material", saved.getTitle() + "\" has been uploaded.");
        }
        return mapToResponse(saved);
    }

    @Override
    public CMResponseDTO getMaterialById(Long materialId) {
        CourseMaterial courseMaterial = cmRepo.findById(materialId).orElseThrow(() -> new ResourceNotFoundException("Material not found"));
        return mapToResponse(courseMaterial);
    }

    @Override
    public List<CMResponseDTO> getMaterialByCourse(Long courseId) {
        Course course = courseRepo.findById(courseId).orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        List<CourseMaterial> materials = cmRepo.findByCourse(course);
        List<CMResponseDTO> response = new ArrayList<>();
        for (CourseMaterial material : materials) {
            response.add(mapToResponse(material));
        }
        return response;
    }

    @Override
    public void deleteMaterial(Long materialId) {
        CourseMaterial courseMaterial = cmRepo.findById(materialId).orElseThrow(() -> new ResourceNotFoundException("Material not found"));
        cmRepo.delete(courseMaterial);
    }
    @Override
    public Resource downloadMaterial(Long materialId){
        CourseMaterial material = cmRepo.findById(materialId).orElseThrow(() -> new ResourceNotFoundException("Material not found"));
        return fileStorageService.downloadFile(material.getFileUrl());
    }

    @Override
    public List<CMResponseDTO> getMaterialByProfessor(Long professorId, Long courseId, String query, Long academicPeriodId) {
        List<CourseMaterial> materials = cmRepo.findMaterialByProfessor(professorId, courseId, query, academicPeriodId);
        List<CMResponseDTO> response = new ArrayList<>();
        for (CourseMaterial material : materials) {
            response.add(mapToResponse(material));
        }
        return response;
    }

     private CMResponseDTO mapToResponse(CourseMaterial request){
         return CMResponseDTO.builder()
                 .materialId(request.getMaterialId())
                 .title(request.getTitle())
                 .fileUrl(request.getFileUrl())
                 .uploadedAt(request.getUploadedAt())
                 .courseId(request.getCourse().getCourseId())
                 .courseName(request.getCourse().getCourseName())
                 .courseCode(request.getCourse().getCourseCode())
                 .filename(request.getFileName())
                 .build();
     }

}
