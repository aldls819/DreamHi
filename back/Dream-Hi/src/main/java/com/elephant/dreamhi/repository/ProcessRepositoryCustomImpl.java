package com.elephant.dreamhi.repository;

import static com.elephant.dreamhi.model.entity.QAnnouncement.announcement;
import static com.elephant.dreamhi.model.entity.QProcess.process;
import static com.querydsl.core.group.GroupBy.groupBy;

import com.elephant.dreamhi.model.entity.Process;
import com.elephant.dreamhi.model.statics.ProcessState;
import com.elephant.dreamhi.model.statics.StageName;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProcessRepositoryCustomImpl implements ProcessRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Process> findLastProcessByAnnouncementId(Long announcementId) {
        return Optional.ofNullable(
                queryFactory.selectFrom(process)
                            .where(process.announcement.id.eq(announcementId))
                            .orderBy(process.id.desc())
                            .limit(1)
                            .fetchOne()
        );
    }

    @Override
    public Optional<Process> findByIdAndStageAndState(Long processId, StageName stageName, ProcessState processState) {
        return Optional.ofNullable(
                queryFactory.selectFrom(process)
                            .where(process.id.eq(processId),
                                   process.stage.eq(stageName),
                                   process.state.eq(processState))
                            .fetchOne()
        );
    }

    @Override
    public Map<Long, Process> findLastProcessesByAnnouncementIds(List<Long> announcementIds) {
        return queryFactory.selectFrom(process)
                           .join(process.announcement, announcement)
                           .where(announcement.id.in(announcementIds))
                           .transform(groupBy(announcement.id).as(process));
    }

}
