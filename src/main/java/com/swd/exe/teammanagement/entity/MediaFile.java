package com.swd.exe.teammanagement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "media_files")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MediaFile {

    @Id
    UUID id;

    @Column(name = "bucket_key", nullable = false, length = 255)
    String bucketKey;

    @Column(name = "content_type", nullable = false, length = 100)
    String contentType;

    @Column(name = "size", nullable = false)
    long size;

    @Column(name = "created_at", nullable = false)
    LocalDateTime createdAt;
}
