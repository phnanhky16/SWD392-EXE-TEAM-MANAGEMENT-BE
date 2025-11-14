package com.swd.exe.teammanagement.service;

import com.swd.exe.teammanagement.dto.request.IdeaRequest;
import com.swd.exe.teammanagement.dto.response.IdeaResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IdeaService {
    IdeaResponse createIdea(IdeaRequest request);
    IdeaResponse updateIdea(Long id, IdeaRequest request);
    Void deleteIdea(Long id);
    IdeaResponse getIdeaById(Long id);
    List<IdeaResponse> getAllIdeasByGroup(Long groupId);
    List<IdeaResponse> getAllIdeas();
    IdeaResponse submitIdea(Long id);
    IdeaResponse approveIdea(Long id);
    IdeaResponse rejectIdea(Long id, String reason);
    Page<IdeaResponse> getMyIdeasAsReviewer(int page, int size);
    IdeaResponse deactivateIdea(Long id);
}
