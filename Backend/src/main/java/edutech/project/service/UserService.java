package edutech.project.service;

import edutech.project.dto.request.ChangePasswordRequestDTO;
import edutech.project.dto.request.UserRequestDTO;
import edutech.project.dto.response.UserResponseDTO;
import edutech.project.model.User;

import edutech.project.dto.response.BulkImportSummaryDTO;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface UserService {
    UserResponseDTO createUser(UserRequestDTO request);
    UserResponseDTO getUserById(Long userId);
    List<UserResponseDTO> getAllUser(String query, String role);
    UserResponseDTO updateUser(Long userId,UserRequestDTO request);
    void deleteUser(Long userId);
    void changePassword(User currentUser, ChangePasswordRequestDTO request);
    BulkImportSummaryDTO bulkImportUsers(MultipartFile file);
}
