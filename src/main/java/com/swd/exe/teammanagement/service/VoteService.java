package com.swd.exe.teammanagement.service;

import com.swd.exe.teammanagement.entity.Vote;
import com.swd.exe.teammanagement.entity.VoteChoice;
import com.swd.exe.teammanagement.enums.vote.ChoiceValue;

import java.util.List;

public interface VoteService {
    Vote voteJoin(Long groupId, Long userId);
    VoteChoice voteChoice(Long voteId, ChoiceValue choiceValue);
    void voteDone(Long voteId);
    List<Vote> getOpenVotes();
    List<Vote> getVotesByGroup(Long groupId);
    Vote getVoteById(Long voteId);
    List<VoteChoice> getVoteChoices(Long voteId);
    Vote activateVote(Long voteId);
    Vote deactivateVote(Long voteId);
    Vote changeVoteActiveStatus(Long voteId);
}
