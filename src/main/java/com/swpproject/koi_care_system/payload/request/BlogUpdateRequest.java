package com.swpproject.koi_care_system.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;
import reactor.util.annotation.Nullable;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BlogUpdateRequest {
    @NotBlank
    String blogTitle;
    @NotBlank
    String blogContent;
    String blogImage;
    String blogDate;
    Set<Integer> tagIds;
    @Nullable
    MultipartFile file;
}