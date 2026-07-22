package edutech.project.controller;

import edutech.project.dto.request.CMRequestDTO;
import edutech.project.dto.response.CMResponseDTO;
import edutech.project.service.CMService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/course-material")
public class CourseMaterialController {
    @Autowired
    private CMService cmService;
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')")
    @PostMapping
    public ResponseEntity<CMResponseDTO> uploadMaterial(@RequestPart("course-material") @Valid CMRequestDTO request, @RequestPart("file")MultipartFile file){
        CMResponseDTO response = cmService.uploadMaterial(request,file);
        return new ResponseEntity<>(response,HttpStatus.CREATED);
    }
    @GetMapping("/{materialId}/cm-details")
    public ResponseEntity<CMResponseDTO> getMaterialById(@PathVariable Long materialId) {
        return ResponseEntity.ok(cmService.getMaterialById(materialId));
    }
    @GetMapping("/{courseId}/cm-listbycourse")
    public ResponseEntity<List<CMResponseDTO>> getMaterialByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(cmService.getMaterialByCourse(courseId));
    }
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<CMResponseDTO>> getMaterialByCourseAlt(@PathVariable Long courseId) {
        return ResponseEntity.ok(cmService.getMaterialByCourse(courseId));
    }
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')")
    @DeleteMapping("/{materialId}/delete-cm")
    public ResponseEntity<String> deleteMaterial(@PathVariable Long materialId) {
        cmService.deleteMaterial(materialId);
        return ResponseEntity.ok("Course material deleted successfully");
    }
    @GetMapping("/{materialId}/download-cm")
    public ResponseEntity<Resource> downloadMaterial(@PathVariable Long materialId) {
        Resource resource = cmService.downloadMaterial(materialId);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\""
                )
                .body(resource);
    }
    @GetMapping("/{materialId}/download")
    public ResponseEntity<Resource> downloadMaterialAlt(@PathVariable Long materialId) {
        Resource resource = cmService.downloadMaterial(materialId);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\""
                )
                .body(resource);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')")
    @GetMapping("/professor/{professorId}/professor-materials")
    public ResponseEntity<List<CMResponseDTO>> getMaterialByProfessor(@PathVariable Long professorId,
                                                                                  @RequestParam(required = false) Long courseId,
                                                                                  @RequestParam(required = false) String query,
                                                                                  @RequestParam(required = false) Long academicPeriodId) {
        return ResponseEntity.ok(cmService.getMaterialByProfessor(professorId, courseId, query, academicPeriodId));
    }
}
