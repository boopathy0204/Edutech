package edutech.project.storage;

import org.springframework.core.io.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;
    @Override
    public String uploadFile(MultipartFile file, String foldername)  {
        if (file.isEmpty()){
            throw new RuntimeException("File is empty");
        }
        try {
            Path uploadpath = Paths.get(uploadDir, foldername);
            if (!Files.exists(uploadpath)) {
                Files.createDirectories(uploadpath);
            }
            String originalName = file.getOriginalFilename();
            String fileName = UUID.randomUUID() + "_" + originalName;
            Path filePath = uploadpath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            return foldername + "/" + fileName;
        }catch(IOException ex){
            throw new RuntimeException("Failed to upload",ex);
        }
    }
    @Override
    public Resource downloadFile(String filepath){
        try{
            Path path= Paths.get(uploadDir).resolve(filepath);
            Resource resource=new UrlResource(path.toUri());
            if (resource.exists()){
                return resource;
            }
            throw new RuntimeException("File not found");
        }catch (MalformedURLException ex){
            throw new RuntimeException("Unable to download the file", ex);
        }
    }
    @Override
    public void deleteFile(String filePath){
        try{
            Path path=Paths.get(uploadDir).resolve(filePath);
            if(!Files.exists(path)){
                throw new RuntimeException("File not found");
            }
            Files.delete(path);
        }catch(IOException ex){
            throw new RuntimeException("Failed to delete",ex);
        }
    }
}











