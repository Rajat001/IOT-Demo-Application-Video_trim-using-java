package com.example.demo.DemoApplication.service;

//package com.example.demo.service;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

@Service
public class FfmpegService {

    public String trimVideo(String ffmpegPath, File inputFile, String startTime, String duration, String outputFilePath) {
        StringBuilder output = new StringBuilder();
        try {
            ProcessBuilder builder = new ProcessBuilder(
                    ffmpegPath, "-i", inputFile.getAbsolutePath(), "-ss", startTime, "-t", duration, "-c", "copy", outputFilePath);
            builder.redirectErrorStream(true);
            Process process = builder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
        return output.toString();
    }
}
