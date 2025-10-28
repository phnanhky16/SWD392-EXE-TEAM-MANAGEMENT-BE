package com.swd.exe.teammanagement.repository;

import com.swd.exe.teammanagement.entity.MediaFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MediaFileRepository extends JpaRepository<MediaFile, UUID> {
}
