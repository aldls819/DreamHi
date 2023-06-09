package com.elephant.dreamhi.service;

import com.elephant.dreamhi.exception.NotFoundException;
import com.elephant.dreamhi.model.dto.BookPeriodDto;
import com.elephant.dreamhi.model.dto.BookPeriodSaveDto;
import com.elephant.dreamhi.model.dto.BookProducerDto;
import com.elephant.dreamhi.model.dto.BookResponseDto;
import com.elephant.dreamhi.model.dto.BookedVolunteerDto;
import com.elephant.dreamhi.model.dto.FileDto;
import com.elephant.dreamhi.model.entity.Book;
import com.elephant.dreamhi.model.entity.NoticeFile;
import com.elephant.dreamhi.model.entity.Process;
import com.elephant.dreamhi.model.entity.Volunteer;
import com.elephant.dreamhi.model.statics.ProcessState;
import com.elephant.dreamhi.model.statics.StageName;
import com.elephant.dreamhi.repository.BookRepository;
import com.elephant.dreamhi.repository.NoticeFileRepository;
import com.elephant.dreamhi.repository.ProcessRepository;
import com.elephant.dreamhi.repository.VolunteerRepository;
import com.elephant.dreamhi.security.PrincipalDetails;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class AuditionServiceImpl implements AuditionService {

    @Value("${app.schedule-period}")

    private Long VIDEO_TIME_TAKE;
    private final ProcessRepository processRepository;
    private final VolunteerRepository volunteerRepository;
    private final BookRepository bookRepository;
    private final NoticeFileRepository noticeFileRepository;

    @Override
    public BookResponseDto findBookOfVolunteer(Long processId, PrincipalDetails user) throws NotFoundException {
        return bookRepository.findByUserIdAndProcessId(user.getId(), processId)
                             .map(BookResponseDto::toDto)
                             .orElseGet(BookResponseDto::new);
    }

    @Override
    public BookPeriodDto findBookPeriod(Long processId) throws NotFoundException {
        return bookRepository.findBookPeriodByProcessId(processId)
                             .orElseThrow(() -> new NotFoundException("현재 절차에서 예약 가능한 기간을 찾을 수 없습니다. 제작사는 예약 기간을 등록해주세요."));
    }

    @Override
    public List<BookResponseDto> findAllBookForVolunteer(Long processId, LocalDate date) {
        return bookRepository.findAllForVolunteerByProcessIdAndDate(processId, date);
    }

    @Override
    public List<BookProducerDto> findAllBookForProducer(Long processId, LocalDate date) {
        return bookRepository.findAllForProducerByProducerIdAndDate(processId, date);
    }

    @Override
    public List<NoticeFile> findFileUrl(Long processId) {
        return noticeFileRepository.findByProcessId(processId);
    }

    @Override
    public String findSessionId(Long processId) throws NotFoundException {
        return findVideoProcess(processId).getSessionId();
    }

    @Override
    public List<BookedVolunteerDto> findBookedVolunteersOnToday(Long processId) {
        return bookRepository.findByProcessIdAndBookDate(processId, LocalDate.now(ZoneId.of("Asia/Seoul")));
    }

    @Override
    @Transactional
    public void updateReserved(Long bookId, Long userId) throws NotFoundException, IllegalStateException {
        Optional<Book> book = bookRepository.findByIdAndVolunteerIdIsNull(bookId);
        if(book.isEmpty()) throw new NotFoundException("예약 가능한 시간대가 아닙니다.");
        Optional<Volunteer> volunteer = volunteerRepository.findByUserId(userId);
        if(volunteer.isEmpty()) throw new IllegalArgumentException("예약할 수 없습니다.");
        book.get().reverse(volunteer.get());
    }

    @Override
    @Transactional
    public void createAuditionSchedule(Long processId, BookPeriodSaveDto bookPeriodSaveDto) throws NotFoundException {
        log.info("{} {}",processId, bookPeriodSaveDto);
        Process process = findVideoProcess(processId).setSessionId(UUID.randomUUID().toString());
        log.info("{} {} {} {}", process.getId(), process.getState(), process.getStage(), process.getSessionId());
        log.info("{}", VIDEO_TIME_TAKE);
        List<Book> books = Book.toEntityList(process, bookPeriodSaveDto, VIDEO_TIME_TAKE);
        books.forEach(b -> {
            log.info("{} {} {}", b.getId(), b.getStartTime(), b.getEndTime());
        });
        Long totalVolunteerCount = volunteerRepository.countByCurrentProcessId(processId);

        if (books.size() < totalVolunteerCount) {
            throw new IllegalArgumentException("화상 오디션 예약 가능 일자가 너무 적습니다.");
        }

        bookRepository.saveAll(books);
    }

    @Override
    @Transactional
    public void saveAllNoticeFiles(Long processId, List<FileDto> fileDtos) throws NotFoundException {
        Process process = findVideoProcess(processId);
        List<NoticeFile> noticeFiles = fileDtos.stream()
                                               .map(fileDto -> NoticeFile.toEntity(process, fileDto))
                                               .collect(Collectors.toList());
        noticeFileRepository.saveAll(noticeFiles);
    }

    private Process findVideoProcess(Long processId) throws NotFoundException {
        return processRepository.findByIdAndStageAndState(processId, StageName.VIDEO, ProcessState.IN_PROGRESS)
                                .orElseThrow(() -> new NotFoundException("화상 오디션을 진행 중인 공고를 찾을 수 없습니다."));
    }

}
