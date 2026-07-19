package edutech.project.serviceImpl;

import edutech.project.dto.request.AdminStaffRequestDTO;
import edutech.project.dto.response.AdminStaffResponseDTO;
import edutech.project.exception.DuplicateResourceException;
import edutech.project.exception.ResourceNotFoundException;
import edutech.project.model.AdminStaff;
import edutech.project.model.User;
import edutech.project.repository.AdminStaffRepo;
import edutech.project.repository.UserRepo;
import edutech.project.service.AdminStaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdminStaffServiceImpl implements AdminStaffService {
    @Autowired
    private AdminStaffRepo adminStaffRepo;
    @Autowired
    private UserRepo userRepo;
    @Override
    public AdminStaffResponseDTO createAdminStaff(AdminStaffRequestDTO request){
        User user=userRepo.findById(request.getUserId()).orElseThrow(()->new ResourceNotFoundException("User not found"));
        if(adminStaffRepo.existsByEmployeeCode(request.getEmployeeCode())){
            throw new DuplicateResourceException("EmployeeCode already exist");
        }
        if (adminStaffRepo.existsByUser(user)) {
            throw new DuplicateResourceException("This user is already assigned to another admin staff");
        }
        AdminStaff adminStaff=new AdminStaff();
        adminStaff.setEmployeeCode(request.getEmployeeCode());
        adminStaff.setDepartment((request.getDepartment()));
        adminStaff.setDesignation(request.getDesignation());
        adminStaff.setContactNumber(request.getContactNumber());
        adminStaff.setFirstName(request.getFirstName());
        adminStaff.setLastName(request.getLastName());
        adminStaff.setUser(user);
        AdminStaff saved = adminStaffRepo.save(adminStaff);
        return mapToResponse(saved);
    }
    @Override
    public List<AdminStaffResponseDTO> getAllAdminStaff(String query) {
        List<AdminStaff> staffList = adminStaffRepo.findAll();
        
        if (query != null && !query.isBlank()) {
            String q = query.trim().toLowerCase();
            staffList = staffList.stream().filter(a -> 
                (a.getFirstName() != null && a.getFirstName().toLowerCase().contains(q)) || 
                (a.getLastName() != null && a.getLastName().toLowerCase().contains(q)) || 
                ((a.getFirstName() + " " + a.getLastName()).toLowerCase().contains(q)) || 
                (a.getEmployeeCode() != null && a.getEmployeeCode().toLowerCase().contains(q)) || 
                (a.getDepartment() != null && a.getDepartment().toLowerCase().contains(q)) || 
                (a.getDesignation() != null && a.getDesignation().toLowerCase().contains(q)) ||
                (a.getUser() != null && a.getUser().getEmail() != null && a.getUser().getEmail().toLowerCase().contains(q))
            ).toList();
        }

        List<AdminStaffResponseDTO> response = new ArrayList<>();
        for (AdminStaff admin : staffList) {
            response.add(mapToResponse(admin));
        }
        return response;
    }
    @Override
    public AdminStaffResponseDTO getAdminStaffById(Long adminId) {
        AdminStaff admin = adminStaffRepo.findById(adminId).orElseThrow(() -> new ResourceNotFoundException("Admin staff not found"));
        return mapToResponse(admin);
    }
    @Override
    public AdminStaffResponseDTO updateAdminStaff(Long adminId, AdminStaffRequestDTO request) {
        AdminStaff admin = adminStaffRepo.findById(adminId).orElseThrow(() -> new ResourceNotFoundException("Admin staff not found"));
        User user = userRepo.findById(request.getUserId()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (adminStaffRepo.existsByEmployeeCodeAndAdminIdNot(request.getEmployeeCode(), adminId)) {
            throw new DuplicateResourceException("EmployeeCode already exists");
        }
        if (adminStaffRepo.existsByUserAndAdminIdNot(user, adminId)) {
            throw new DuplicateResourceException("This user is already assigned to another admin staff");
        }
        admin.setEmployeeCode(request.getEmployeeCode());
        admin.setDepartment(request.getDepartment());
        admin.setDesignation(request.getDesignation());
        admin.setContactNumber(request.getContactNumber());
        admin.setFirstName(request.getFirstName());
        admin.setLastName(request.getLastName());
        admin.setUser(user);
        AdminStaff saved = adminStaffRepo.save(admin);
        return mapToResponse(saved);
    }
    @Override
    public void deleteAdminStaff(Long adminId) {
        AdminStaff admin = adminStaffRepo.findById(adminId).orElseThrow(() -> new ResourceNotFoundException("Admin staff not found"));
        adminStaffRepo.delete(admin);
    }

    private AdminStaffResponseDTO mapToResponse(AdminStaff request){
        return AdminStaffResponseDTO.builder()
                .adminId(request.getAdminId())
                .department(request.getDepartment())
                .employeeCode(request.getEmployeeCode())
                .designation(request.getDesignation())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .contactNumber(request.getContactNumber())
                .userId(request.getUser().getUserId())
                .username(request.getUser().getUsername()).build();
    }
}
