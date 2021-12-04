package ru.netology.mycloud.controller;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.mycloud.dto.RenameFileDTO;
import ru.netology.mycloud.security.jwt.JwtTokenProvider;
import ru.netology.mycloud.service.FileService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class FileController {
    FileService fileService;
    JwtTokenProvider jwtTokenProvider;

    public FileController(FileService fileService, JwtTokenProvider jwtTokenProvider) {
        this.fileService = fileService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/file")
    public ResponseEntity uploadFile(@RequestHeader("auth-token") String token, @RequestBody MultipartFile file) throws IOException {
        String tokenWithoutBearer = token.substring(7);
        String username = jwtTokenProvider.getUsername(tokenWithoutBearer);
        if (jwtTokenProvider.validateToken(tokenWithoutBearer)) {
            if (fileService.saveFile(file, username)) {
                return ResponseEntity.ok("Success upload");
            } else {
                return ResponseEntity.status(400).body("Error input data");
            }
        } else {
            return ResponseEntity.status(401).body("Unauthorized error");
        }
    }

    @PutMapping("/file")
    public ResponseEntity getFile(@RequestHeader("auth-token") String token,
                                  @RequestParam("filename") String fileName,
                                  @RequestBody RenameFileDTO renameFile) throws FileNotFoundException {
        fileService.renameFile(token, fileName, renameFile.getNewName());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/file")
    public ResponseEntity getFile(@RequestHeader("auth-token") String token, @RequestParam("filename") String fileName) {
        String tokenWithoutBearer = token.substring(7);
        if (jwtTokenProvider.validateToken(tokenWithoutBearer)) {
            File file = null;
            try {
                file = fileService.getFile(fileName);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Path path = Paths.get(file.getAbsolutePath());
            byte[] bytes = new byte[0];
            String probeContentType = null;
            try {
                bytes = Files.readAllBytes(path);
                probeContentType = Files.probeContentType(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            ContentDisposition.attachment().filename(file.getName()).build().toString())
                    .contentType(probeContentType != null ? MediaType.valueOf(probeContentType) : MediaType.APPLICATION_OCTET_STREAM)
                    .body(bytes);
        } else {
            return ResponseEntity.status(401).body("Unauthorized error");
        }
    }

    @DeleteMapping("/file")
    public ResponseEntity deleteFile(@RequestHeader("auth-token") String token, @RequestParam("filename") String fileName) {
        String tokenWithoutBearer = token.substring(7);
        if (jwtTokenProvider.validateToken(tokenWithoutBearer)) {
            if (fileService.deleteFile(fileName)) {
                return ResponseEntity.ok("Success deleted");
            } else {
                return ResponseEntity.status(400).body("Error input data");
            }
        } else {
            return ResponseEntity.status(401).body("Unauthorized error");
        }
    }

    @GetMapping("/list")
    public ResponseEntity getFilesList(@RequestHeader("auth-token") String token) {
        String tokenWithoutBearer = token.substring(7);
        String username = jwtTokenProvider.getUsername(tokenWithoutBearer);
        if (jwtTokenProvider.validateToken(tokenWithoutBearer)) {
            return ResponseEntity.status(200).body(fileService.findFilesByUsername(username));
        } else {
            return ResponseEntity.status(401).body("Unauthorized error");
        }
    }
}
