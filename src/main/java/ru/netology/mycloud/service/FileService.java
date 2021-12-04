package ru.netology.mycloud.service;


import lombok.Cleanup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.mycloud.model.FileModel;
import ru.netology.mycloud.repository.FileRepository;
import ru.netology.mycloud.security.jwt.JwtTokenProvider;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;


@Service
public class FileService {
    FileRepository fileRepository;
    JwtTokenProvider jwtTokenProvider;

    public FileService(FileRepository fileRepository, JwtTokenProvider jwtTokenProvider) {
        this.fileRepository = fileRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Value("${upload.path}")
    private String uploadPath;

    public List<FileModel> findFilesByUsername(String username) {
        return fileRepository.findFilesByUsername(username);
    }


    public boolean saveFile(MultipartFile multipartFile, String username) throws IOException {
        String uuidFile = UUID.randomUUID().toString();
        String resultFilename = uuidFile + "." + multipartFile.getOriginalFilename();
        var file = new File(uploadPath + resultFilename);
        if (file.exists() || multipartFile.isEmpty()) return false;

        var checkPath = Paths.get(uploadPath);
        if (!Files.exists(checkPath)) {
            var dir = new java.io.File(uploadPath);
            dir.mkdir();
        }

        byte[] bytes = multipartFile.getBytes();
        @Cleanup BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(file));
        stream.write(bytes);
        FileModel uploadedFile = new FileModel()
                .setFilename(resultFilename)
                .setSize(multipartFile.getSize())
                .setUsername(username);
        fileRepository.save(uploadedFile);
        return true;
    }

    public boolean deleteFile(String fileName) {
        var file = new File(uploadPath + fileName);
        if (file.exists()) {
            file.delete();
            fileRepository.deleteFileModelByFilename(fileName);
            return true;
        }
        return false;
    }

    public File getFile(String fileName) throws FileNotFoundException {
        System.out.println(fileName);
        var file = new File(uploadPath + fileName);
        if (file.exists()) {
            return file;
        } else {
            throw new FileNotFoundException("File not found");
        }
    }

    public boolean renameFile(String token, String fileName, String newName) {
        var tokenWithoutBearer = token.substring(7);
        var username = jwtTokenProvider.getUsername(tokenWithoutBearer);
        var file = new File(uploadPath + fileName);
        if (!file.exists()) {
            return false;
        } else {
            FileModel renamedFile = new FileModel()
                    .setFilename(newName)
                    .setSize(file.length())
                    .setUsername(username);
            fileRepository.deleteFileModelByFilename(fileName);
            fileRepository.save(renamedFile);
            return file.renameTo(new File(uploadPath + newName));
        }
    }
}
