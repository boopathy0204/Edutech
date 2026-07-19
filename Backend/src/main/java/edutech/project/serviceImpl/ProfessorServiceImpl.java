package edutech.project.serviceImpl;

import edutech.project.dto.request.ProfessorRequestDTO;
import edutech.project.dto.response.ProfessorResponseDTO;
import edutech.project.exception.DuplicateResourceException;
import edutech.project.exception.ResourceNotFoundException;
import edutech.project.model.Professor;
import edutech.project.model.User;
import edutech.project.repository.ProfessorRepo;
import edutech.project.repository.UserRepo;
import edutech.project.service.ProfessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProfessorServiceImpl implements ProfessorService {
    @Autowired
    private ProfessorRepo professorRepo;
    @Autowired
    private UserRepo userRepo;
    @Override
    public ProfessorResponseDTO createProfessor(ProfessorRequestDTO request){
        User user = userRepo.findById(request.getUserId()).orElseThrow(()-> new ResourceNotFoundException("User not found"));
        if (professorRepo.existsByUser(user)) {
            throw new DuplicateResourceException("This user is already assigned to another professor");
        }
       if(professorRepo.existsByEmployeeCode(request.getEmployeeCode())){
           throw new DuplicateResourceException("EmployeeCode already exists");
       }
       Professor professor=new Professor();
       professor.setName(request.getName());
       professor.setEmployeeCode(request.getEmployeeCode());
       professor.setDepartment(request.getDepartment());
       professor.setDesignation(request.getDesignation());
       professor.setContactNumber(request.getContactNumber());
       professor.setUser(user);

       Professor saved=professorRepo.save(professor);
       return maptoResponse(saved);
    }

    @Override
    public List<ProfessorResponseDTO> getAllProfessor(String query){
        List<Professor> professors = professorRepo.findAll();
        
        if (query != null && !query.isBlank()) {
            String q = query.trim().toLowerCase();
            professors = professors.stream().filter(p -> 
                (p.getName() != null && p.getName().toLowerCase().contains(q)) || 
                (p.getEmployeeCode() != null && p.getEmployeeCode().toLowerCase().contains(q)) || 
                (p.getDepartment() != null && p.getDepartment().toLowerCase().contains(q)) || 
                (p.getUser() != null && p.getUser().getEmail() != null && p.getUser().getEmail().toLowerCase().contains(q))
            ).toList();
        }

        List<ProfessorResponseDTO> response = new ArrayList<>();
        for( Professor pro : professors){
            response.add(maptoResponse(pro));
        }
        return response;
    }
    @Override
    public ProfessorResponseDTO getProfessorById(Long professorId){
        Professor professor = professorRepo.findById(professorId).orElseThrow(()->new ResourceNotFoundException("User account has been created successfully, but the professor has not completed their profile yet. Please ask the professor to complete their registration."));
        return maptoResponse(professor);
    }

    @Override
    public void deleteProfessor(Long professorId){
        Professor professor = professorRepo.findById(professorId).orElseThrow(()->new ResourceNotFoundException("Professor not found"));
        professorRepo.delete(professor);
    }
    @Override
    public ProfessorResponseDTO updateProfessor(Long professorId,ProfessorRequestDTO request){
        Professor professor = professorRepo.findById(professorId).orElseThrow(()->new ResourceNotFoundException("Professor not found"));
        User user = userRepo.findById(request.getUserId()).orElseThrow(()-> new ResourceNotFoundException("User not found"));
        if(professorRepo.existsByEmployeeCodeAndProfessorIdNot(request.getEmployeeCode(),professorId)){
            throw new DuplicateResourceException("Employeecode already exist");
        }
        if (professorRepo.existsByUserAndProfessorIdNot(user, professorId)) {
            throw new DuplicateResourceException("This user is already assigned to another professor");
        }
        professor.setName(request.getName());
        professor.setEmployeeCode(request.getEmployeeCode());
        professor.setDepartment(request.getDepartment());
        professor.setDesignation(request.getDesignation());
        professor.setContactNumber(request.getContactNumber());
        professor.setUser(user);
        // save
        Professor saved = professorRepo.save(professor);
        return maptoResponse(saved);
    }

    @Override
    public ProfessorResponseDTO getProfessorByUser(User user) {
        Professor professor = professorRepo.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Professor profile not found for this user"));
        return maptoResponse(professor);
    }

    private ProfessorResponseDTO maptoResponse(Professor professor){
        return ProfessorResponseDTO.builder()
                .professorId(professor.getProfessorId())
                .name(professor.getName())
                .employeeCode(professor.getEmployeeCode())
                .department(professor.getDepartment())
                .designation(professor.getDesignation())
                .userId(professor.getUser().getUserId())
                .contactNumber(professor.getContactNumber())
                .username(professor.getUser().getUsername()).build();
    }
}
