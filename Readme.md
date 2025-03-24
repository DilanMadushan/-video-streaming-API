# 📺 Video Streaming API

Welcome to the **Video Streaming API**! This API is designed to stream video files from your server to clients with support for **HTTP Range Requests**, enabling efficient video playback and seeking capabilities. Whether you’re building a media player, a video-on-demand service, or simply need a way to stream videos, this API has you covered.

## ✨ Key Features

- Stream video files directly from the backend server.
- Supports **HTTP Range Requests**, allowing users to seek through videos and stream parts of large video files.
- Handles popular video formats such as **MP4**, **MKV**, and **WEBM**.
- Supports **HTML5 Video Player** integration for seamless playback on modern web browsers.
- CORS-enabled for cross-origin requests, making it easy to integrate with various front-end applications.

---

## 🚀 Endpoint Overview

The API provides a single endpoint for streaming videos:

### `GET /api/v1/vedioStrime/{filename}`

This endpoint streams a video file based on the provided `filename`. It supports **partial content delivery** using HTTP Range requests, which allows clients to download and stream specific portions of a video file. This functionality is essential for video playback, as it lets users seek and resume videos without needing to download the entire file first.

### 📌 Method Signature (Java)
```java
@GetMapping("{filename}")
public ResponseEntity<Resource> streamVideo(
        @PathVariable("filename") String filename,
        @RequestHeader(value = HttpHeaders.RANGE, required = false) String rangeHeader
) throws IOException
```

---

## ⚙️ How It Works

When a request is made to stream a video, the following process occurs:

1. The `filename` is passed in the URL to identify the requested video file.
2. The server checks if the video exists in the `videos/` directory.
3. If the `Range` header is present, it parses the range (e.g., `bytes=0-5000`) to stream only the requested portion of the file.
4. If no range is specified, the full video file is streamed.
5. The server reads the file from disk using **RandomAccessFile**, allowing partial reads for efficient video delivery.
6. The appropriate **HTTP headers** are set, including:
    - `Content-Type`: The MIME type of the video (e.g., `video/mp4`).
    - `Accept-Ranges`: Indicating that range requests are supported.
    - `Content-Length`: The length of the requested video content.
    - `Content-Range`: Specifies the byte range and total file size.
7. The server responds with a `200 OK` for full video or `206 Partial Content` for partial video depending on the request.

---

## 📥 Example Requests

### 1. Health Check (Optional)

Check the status of the API:
```bash
curl http://localhost:8080/api/v1/vedioStrime
```

### 2. Stream Full Video (No Range Header)

Download or stream the entire video file:
```bash
curl http://localhost:8080/api/v1/vedioStrime/sample.mp4 --output sample.mp4
```

### 3. Stream Partial Video (With Range Header)

Stream a specific portion of the video file using the `Range` header:
```bash
curl -H "Range: bytes=0-" http://localhost:8080/api/v1/vedioStrime/sample.mp4 --output partial-sample.mp4
```

---

## 🎬 HTML5 Video Player Integration

You can easily integrate the video streaming API into an HTML5 video player for seamless playback:

```html
<video width="640" height="360" controls>
  <source src="http://localhost:8080/api/v1/vedioStrime/sample.mp4" type="video/mp4">
  Your browser does not support the video tag.
</video>
```

---

## 📨 Request Headers

| Header | Example        | Description                                               |
|--------|----------------|-----------------------------------------------------------|
| Range  | bytes=0-       | Specifies the byte range to request partial content.      |

---

## 📤 Response Headers

| Header         | Description                                                                |
|----------------|----------------------------------------------------------------------------|
| Content-Type   | MIME type of the video (e.g., video/mp4).                                  |
| Accept-Ranges  | Indicates that the server supports range requests (always bytes).          |
| Content-Length | Length of the returned content.                                            |
| Content-Range  | Specifies which bytes are returned and the total length (bytes start-end/total). |

---

## 🔢 Response Status Codes

| Status Code | Description                                               |
|-------------|-----------------------------------------------------------|
| 200 OK      | Full video stream (if no Range header is sent).           |
| 206 Partial Content | Partial video stream (if Range header is sent).   |
| 404 Not Found | The video file was not found on the server.             |
| 416 Requested Range Not Satisfiable | The requested byte range is invalid. |
| 500 Internal Server Error | An unexpected error occurred on the server. |

---

## 📂 Folder Structure

To run the API, make sure your project is structured as follows:

```
project-root/
├── videos/                # Store your video files here
├── src/
│   └── main/
│       └── java/
│           └── lk/ijse/videostriming/conrtoller/
│               └── VedioStrimeController.java
└── pom.xml                # Project configuration file
```

---

## ✅ Future Improvements

While the API is functional, we plan to enhance it with the following features:

- [ ] **Authentication & Authorization**: Secure access to video streams.
- [ ] **Adaptive Streaming**: Add support for protocols like HLS or DASH.
- [ ] **Video Upload Endpoint**: Allow users to upload their own videos.
- [ ] **Error Handling & Logging**: Improve error reporting and server-side logging.

---

## 📄 License

This project is licensed under the **MIT License**.

---

