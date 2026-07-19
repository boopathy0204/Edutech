package edutech.project.controller;

import edutech.project.dto.request.ChangePasswordRequestDTO;
import edutech.project.dto.request.UserRequestDTO;
import edutech.project.dto.response.UserResponseDTO;
import edutech.project.model.User;
import edutech.project.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import edutech.project.dto.response.BulkImportSummaryDTO;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO request) {
        UserResponseDTO response = userService.createUser(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<UserResponseDTO> getAllUsers(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String role) {
        return userService.getAllUser(query, role);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{userId}/user-details")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{userId}/update-user")
    public ResponseEntity<String> updateUser(@PathVariable Long userId, @Valid @RequestBody UserRequestDTO request) {
        userService.updateUser(userId, request);
        return ResponseEntity.ok("User updated successfully");
    }
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{userId}/delete-delete")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok("User deleted successfully");
    }
    @PatchMapping("/change-password")
    public ResponseEntity<String> changePassword(@AuthenticationPrincipal User currentUser, @Valid @RequestBody ChangePasswordRequestDTO request) {
        userService.changePassword(currentUser, request);
        return ResponseEntity.ok("Password changed successfully");
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getCurrentUser(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(userService.getUserById(currentUser.getUserId()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value="/bulk-import", consumes = "multipart/form-data")
    public ResponseEntity<BulkImportSummaryDTO> bulkImportUsers(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(BulkImportSummaryDTO.builder()
                    .totalRows(0)
                    .successCount(0)
                    .failureCount(0).build());
        }
        return ResponseEntity.ok(userService.bulkImportUsers(file));
    }
}