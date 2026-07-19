package edutech.project.storage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileStorageService {
    String uploadFile(MultipartFile file,String folderName) ;
    Resource downloadFile(String filePath);
    void deleteFile(String filePath);
}
