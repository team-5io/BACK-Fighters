package com.lion._iozoo.docpr.application.service;

import com.lion._iozoo.docpr.application.port.out.LoadDocPrPort;
import com.lion._iozoo.docpr.application.usecase.ListDocPrsUseCase;
import com.lion._iozoo.docpr.domain.DocPr;
import com.lion._iozoo.team.application.TeamPermissionChecker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ListDocPrsService implements ListDocPrsUseCase {

    private final LoadDocPrPort loadDocPrPort;
    private final TeamPermissionChecker teamPermissionChecker;

    @Override
    @Transactional(readOnly = true)
    public Page<DocPr> list(Long userId, Long teamId, Pageable pageable) {
        log.info("event=docpr_list_시작 userId={}, teamId={}", userId, teamId);

        try {
            teamPermissionChecker.requireMember(teamId, userId);

            Page<DocPr> result = loadDocPrPort.loadByTeamId(teamId, userId, pageable);

            log.info("event=docpr_list_완료 userId={}, teamId={}, count={}",
                    userId, teamId, result.getNumberOfElements());
            return result;
        } catch (RuntimeException e) {
            log.warn("event=docpr_list_실패 userId={}, teamId={}, reason={}", userId, teamId, e.getMessage(), e);
            throw e;
        }
    }
}
