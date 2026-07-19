package edutech.project.controller;

import edutech.project.dto.request.ProfessorRequestDTO;
import edutech.project.dto.response.ProfessorResponseDTO;
import edutech.project.service.ProfessorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/professor")
public class ProfessorController {
    @Autowired
    private ProfessorService professorService;

    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')")
    @PostMapping
    public ResponseEntity<ProfessorResponseDTO> createprofessor(@Valid @RequestBody ProfessorRequestDTO request) {
        ProfessorResponseDTO response = professorService.createProfessor(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<ProfessorResponseDTO>> getAllProfessor(
            @RequestParam(required = false) String query) {
        return ResponseEntity.ok(professorService.getAllProfessor(query));
    }

    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR','STUDENT')")
    @GetMapping("/{professorId}/professor-details")
    public ResponseEntity<ProfessorResponseDTO> getProfessorById(@PathVariable Long professorId) {
        return ResponseEntity.ok(professorService.getProfessorById(professorId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{professorId}/delete-professor")
    public ResponseEntity<String> deleteprofessor(@PathVariable Long professorId) {
        professorService.deleteProfessor(professorId);
        return ResponseEntity.ok("Deleted successfully");
    }

    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR')")
    @PutMapping("/{professorId}/update-professor")
    public ResponseEntity<ProfessorResponseDTO> updateProfessor(@PathVariable Long professorId,
                                                                @Valid @RequestBody ProfessorRequestDTO request) {
        return ResponseEntity.ok(professorService.updateProfessor(professorId, request));
    }

    @PreAuthorize("hasRole('PROFESSOR')")
    @GetMapping("/professorprofile")
    public ResponseEntity<ProfessorResponseDTO> getCurrentProfessor(@org.springframework.security.core.annotation.AuthenticationPrincipal edutech.project.model.User currentUser) {
        return ResponseEntity.ok(professorService.getProfessorByUser(currentUser));
    }
}
