package lk.ijse.videostriming.conrtoller;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.nio.file.Files;


@CrossOrigin
@RestController
@RequestMapping("api/v1/vedioStrime")
public class VedioStrimeController {
    @GetMapping
    public String healthCheck() {
        return "Vedio is working Successfully";
    }

    @GetMapping("{filename}")
    public ResponseEntity<Resource> streamVideo(
            @PathVariable("filename") String filename,
            @RequestHeader(value = HttpHeaders.RANGE, required = false) String rangeHeader
    ) throws IOException {

        File videoFile = new File("videos/" + filename);
        if (!videoFile.exists()) {
            return ResponseEntity.notFound().build();
        }

        long fileLength = videoFile.length();
        long start = 0;
        long end = fileLength - 1;

        if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
            System.out.println(rangeHeader);
            String[] ranges = rangeHeader.substring("bytes=".length()).split("-");
            try {
                start = Long.parseLong(ranges[0]);
                if (ranges.length > 1 && !ranges[1].isEmpty()) {
                    end = Long.parseLong(ranges[1]);
                }
            } catch (NumberFormatException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }

        if (start > end || start >= fileLength) {
            return ResponseEntity.status(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE).build();
        }

        long contentLength = end - start + 1;

        InputStream inputStream;
        try {
            RandomAccessFile raf = new RandomAccessFile(videoFile, "r");
            raf.seek(start);
            byte[] buffer = new byte[(int) contentLength];
            raf.readFully(buffer);
            raf.close(); // Important: close the file resource!
            inputStream = new ByteArrayInputStream(buffer);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        String mimeType = Files.probeContentType(videoFile.toPath());

        if (mimeType == null) {
            String fileNameLower = filename.toLowerCase();
            if (fileNameLower.endsWith(".mkv")) {
                mimeType = "video/x-matroska";
            } else if (fileNameLower.endsWith(".mp4")) {
                mimeType = "video/mp4";
            } else if (fileNameLower.endsWith(".webm")) {
                mimeType = "video/webm";
            } else {
                mimeType = "application/octet-stream";
            }
        }

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(HttpHeaders.CONTENT_TYPE, mimeType);
        responseHeaders.set(HttpHeaders.ACCEPT_RANGES, "bytes");
        responseHeaders.set(HttpHeaders.CONTENT_LENGTH, String.valueOf(contentLength));
        responseHeaders.set(HttpHeaders.CONTENT_RANGE, String.format("bytes %d-%d/%d", start, end, fileLength));

        return new ResponseEntity<>(new InputStreamResource(inputStream), responseHeaders,
                (rangeHeader != null) ? HttpStatus.PARTIAL_CONTENT : HttpStatus.OK);
    }
}
