package edutech.project.service;

import edutech.project.dto.request.AdminStaffRequestDTO;
import edutech.project.dto.response.AdminStaffResponseDTO;

import java.util.List;

public interface AdminStaffService {
    AdminStaffResponseDTO createAdminStaff(AdminStaffRequestDTO request);
    List<AdminStaffResponseDTO> getAllAdminStaff(String query);
    AdminStaffResponseDTO getAdminStaffById(Long adminId);
    AdminStaffResponseDTO updateAdminStaff(Long adminId, AdminStaffRequestDTO request);
    void deleteAdminStaff(Long adminId);
}
