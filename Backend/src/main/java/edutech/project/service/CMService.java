package edutech.project.service;

import edutech.project.dto.request.CMRequestDTO;
import edutech.project.dto.response.CMResponseDTO;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CMService {
    CMResponseDTO uploadMaterial(CMRequestDTO request, MultipartFile file);
    CMResponseDTO getMaterialById(Long materialId);
    void deleteMaterial(Long materialId);
    List<CMResponseDTO> getMaterialByCourse(Long courseId);
    Resource downloadMaterial(Long materialId);
    List<CMResponseDTO> getMaterialByProfessor(Long professorId, Long courseId, String query, Long academicPeriodId);
}
