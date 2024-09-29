package com.swpproject.koi_care_system.controllers;

import com.swpproject.koi_care_system.payload.request.LogCreateRequest;
import com.swpproject.koi_care_system.payload.request.LogUpdateRequest;
import com.swpproject.koi_care_system.payload.response.ApiResponse;
import com.swpproject.koi_care_system.service.imageBlobStorage.ImageStorage;
import com.swpproject.koi_care_system.service.log.ILogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestController
@RequestMapping("/log")
@RequiredArgsConstructor
public class LogController {
    private final ILogService logService;
    private final ImageStorage imageStorage;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createLog(@ModelAttribute LogCreateRequest logCreateRequest) {
        try{
            assert logCreateRequest.getFile() != null;
            logCreateRequest.setImage(!logCreateRequest.getFile().isEmpty()?imageStorage.uploadImage(logCreateRequest.getFile()):"");
            return ResponseEntity.ok(ApiResponse.builder()
                    .data(logService.createLog(logCreateRequest, logCreateRequest.getKoiPondId()))
                    .message("Log has been created")
                    .build());
        }catch (Exception e){
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PutMapping("/update/{logId}")
    public ResponseEntity<ApiResponse> updateLog(@PathVariable int logId, @ModelAttribute LogUpdateRequest logUpdateRequest) {
        try{
            assert logUpdateRequest.getFile() != null;
            logUpdateRequest.setImage(!logUpdateRequest.getFile().isEmpty()?imageStorage.uploadImage(logUpdateRequest.getFile()): logUpdateRequest.getImage());
            return ResponseEntity.ok(ApiResponse.builder()
                    .data(logService.updateLog(logId, logUpdateRequest))
                    .message("Log has been updated")
                    .build());
        }catch (Exception e){
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }
    @DeleteMapping("/delete/{logId}")
    public ResponseEntity<ApiResponse> deleteLog(@PathVariable int logId) {
        logService.deleteLog(logId);
        return ResponseEntity.ok(ApiResponse.builder()
                .message("Log has been deleted")
                .build());
    }

    @GetMapping("/getID/{logId}")
    public ResponseEntity<ApiResponse> getLog(@PathVariable int logId) {
        return ResponseEntity.ok(ApiResponse.builder()
                .message("Log found")
                .data(logService.getLogById(logId))
                .build());
    }
    @GetMapping
    public ResponseEntity<ApiResponse> getAllLogs(@RequestParam(defaultValue = "logDate") String sortBy,
                                                  @RequestParam(defaultValue = "Desc") String sortDir) {
        return ResponseEntity.ok(ApiResponse.builder()
                .message("List of logs")
                .data(logService.getAllLogs(sortBy, sortDir))
                .build());
    }
    @GetMapping("/koiPond/{pondId}")
    public ResponseEntity<ApiResponse> getLogsByPondId(@PathVariable long pondId) {
        return ResponseEntity.ok(ApiResponse.builder()
                .message("List of logs")
                .data(logService.getLogsByPondId(pondId))
                .build());
    }
    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse> getLogsByCategory(@PathVariable String category) {
        return ResponseEntity.ok(ApiResponse.builder()
                .message("List of logs")
                .data(logService.getLogsByCategory(category))
                .build());
    }

}



