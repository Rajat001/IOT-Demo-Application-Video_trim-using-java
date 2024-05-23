package com.example.demo.DemoApplication.controller;

//package com.example.demo.controller;
//import com.example.demo.service.FfmpegService;

import com.example.demo.DemoApplication.service.FfmpegService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class VideoController {

    private final FfmpegService ffmpegService;

    @Autowired
    public VideoController(FfmpegService ffmpegService) {
        this.ffmpegService = ffmpegService;
    }

    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                   @RequestParam("startTime") String startTime,
                                   @RequestParam("duration") String duration) {
        String ffmpegPath = "ffmpeg"; // Adjust if ffmpeg is not in your PATH
        String uploadDir = "uploads/";
        Path uploadPath = Paths.get(uploadDir);

        // Ensure the upload directory exists
        if (!Files.exists(uploadPath)) {
            try {
                Files.createDirectories(uploadPath);
            } catch (IOException e) {
                e.printStackTrace();
                return "Error: Could not create upload directory";
            }
        }

        try {
            // Save the uploaded file
            String fileName = file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, fileName);
            Files.write(filePath, file.getBytes());

            // Generate output file path
            // String outputFileName = "trimmed_" + fileName;
            String outputFileName = "trimmed_" + System.currentTimeMillis() + "_" + fileName;

            String outputFilePath = uploadDir + outputFileName;

            // Trim the video
            String ffmpegOutput = ffmpegService.trimVideo(ffmpegPath, filePath.toFile(), startTime, duration, outputFilePath);

            // Generate download URL for the trimmed video
            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/download/")
                    .path(outputFileName)
                    .toUriString();

            return "Video trimmed successfully. <a href=\"" + fileDownloadUri + "\">Download the trimmed video</a>";

        } catch (IOException e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    @GetMapping("/download/{filename:.+}")
    public @ResponseBody byte[] downloadFile(@PathVariable String filename) throws IOException {
        Path filePath = Paths.get("uploads").resolve(filename);
        return Files.readAllBytes(filePath);
    }

    @GetMapping("/sum")
    public String sum() {
        int a = 5;
        int b = 10;
        int result = a + b;
        return "The sum of " + a + " and " + b + " is: " + result;
    }
}
