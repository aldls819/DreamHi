package com.elephant.dreamhi.service;

import com.elephant.dreamhi.model.dto.UserDetailDto;
import com.elephant.dreamhi.model.entity.ActorProfile;
import com.elephant.dreamhi.model.entity.User;
import com.elephant.dreamhi.repository.ActorRepository;
import com.elephant.dreamhi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final ActorRepository actorRepository;

    /**
     * User 기본 정보 조회 메소드
     *
     * @param id
     * @return UserDetailDto
     * @throws UsernameNotFoundException : id 조회 결과 없을 경우 발생
     */
    @Override
    @Transactional
    public UserDetailDto findUserDetail(Long id) throws UsernameNotFoundException {
        User user = userRepository.findById(id).orElseThrow(() -> {
            return new UsernameNotFoundException(id + " 존재하지 않는 유저입니다.");
        });
        UserDetailDto userDetailDto = new UserDetailDto();
        userDetailDto.setUserDetailDto(user);

        ActorProfile actorProfile = actorRepository.findByUser_id(user.getId());
        log.info(actorProfile.getId().toString());

        userDetailDto.setActorProfileId(actorProfile.getId());

        return userDetailDto;
    }

}
