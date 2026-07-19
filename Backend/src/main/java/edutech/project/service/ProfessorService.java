package edutech.project.service;

import edutech.project.dto.request.ProfessorRequestDTO;
import edutech.project.dto.response.ProfessorResponseDTO;

import java.util.List;

public interface ProfessorService {
    ProfessorResponseDTO createProfessor(ProfessorRequestDTO request);
    ProfessorResponseDTO getProfessorById(Long professorId);
    List<ProfessorResponseDTO> getAllProfessor(String query);
    void deleteProfessor(Long professorId);
    ProfessorResponseDTO updateProfessor(Long ProfessorId,ProfessorRequestDTO request);
    ProfessorResponseDTO getProfessorByUser(edutech.project.model.User user);
}
