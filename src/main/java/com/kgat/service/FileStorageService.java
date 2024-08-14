package com.kgat.service;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileStorageService {
    private static final String PATH = "D:\\DEV\\resource\\profileImage";

    public String saveProfileImage(InputStream fileInputStream, String fileName) throws IOException {
        Path directoryPath = Paths.get(PATH);

        if(!Files.exists(directoryPath)) {
            Files.createDirectories(directoryPath);
        }

        File file = new File(PATH + File.separator + fileName);

        try(FileOutputStream fos = new FileOutputStream(file)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while((bytesRead = fileInputStream.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
        }

        return file.getAbsolutePath();
    }
}
