package com.newuser.registration.service;

import com.newuser.registration.config.FileStorageProperties;
import com.newuser.registration.resource.errors.FileStorageException;
import com.newuser.registration.resource.errors.MyFileNotFoundException;
import com.newuser.registration.util.ApiConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private final Path fileStorageLocation;

    @Autowired
    public FileStorageServiceImpl(FileStorageProperties fileStorageProperties) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir()).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException(ApiConstants.FILE_STORAGE_EXCEPTION_PATH_NOT_FOUND, ex);
        }
    }

    @Override
    public String storeFile(MultipartFile file) throws IOException {

        if (!(file.getOriginalFilename().endsWith(ApiConstants.PNG_FILE_FORMAT) || file.getOriginalFilename()
                .endsWith(ApiConstants.JPEG_FILE_FORMAT) || file.getOriginalFilename().endsWith(ApiConstants.JPG_FILE_FORMAT)))
            throw new FileStorageException(ApiConstants.INVALID_FILE_FORMAT);

        File f = new File(ApiConstants.TEMP_DIR + file.getOriginalFilename());

        f.createNewFile();
        FileOutputStream fout = new FileOutputStream(f);
        fout.write(file.getBytes());
        fout.close();
        BufferedImage image = ImageIO.read(f);
        int height = image.getHeight();
        int width = image.getWidth();
        if (width > 300 || height > 300) {
            if (f.exists())
                f.delete();
            throw new FileStorageException(ApiConstants.INVALID_FILE_DIMENSIONS);
        }

        if (f.exists())
            f.delete();

        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            if (fileName.contains(ApiConstants.INVALID_FILE_DELIMITER)) {
                throw new FileStorageException(ApiConstants.INVALID_FILE_PATH_NAME + fileName);
            }
            String newFileName = System.currentTimeMillis() + ApiConstants.FILE_SEPERATOR + fileName;
            Path targetLocation = this.fileStorageLocation.resolve(newFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return newFileName;
        } catch (IOException ex) {
            throw new FileStorageException(String.format(ApiConstants.FILE_STORAGE_EXCEPTION, fileName), ex);
        }

    }

    @Override
    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new MyFileNotFoundException(ApiConstants.FILE_NOT_FOUND + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new MyFileNotFoundException(ApiConstants.FILE_NOT_FOUND + fileName, ex);
        }
    }
}

