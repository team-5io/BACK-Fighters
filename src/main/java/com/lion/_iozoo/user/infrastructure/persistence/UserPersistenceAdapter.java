package com.lion._iozoo.user.infrastructure.persistence;

import com.lion._iozoo.user.application.port.out.LoadUserPort;
import com.lion._iozoo.user.application.port.out.SaveUserPort;
import com.lion._iozoo.user.domain.User;
import com.lion._iozoo.user.infrastructure.persistence.entity.UserEntity;
import com.lion._iozoo.user.infrastructure.persistence.mapper.UserMapper;
import com.lion._iozoo.user.infrastructure.persistence.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserPersistenceAdapter implements SaveUserPort, LoadUserPort {

    private final UserJpaRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public User saveUser(User user) {
        // 1. 순수 도메인 객체를 DB 저장용 Entity로 변환합니다[cite: 2].
        UserEntity entity = userMapper.toEntity(user);

        // 2. Spring Data JPA를 통해 DB에 저장합니다[cite: 2].
        UserEntity savedEntity = userRepository.save(entity);

        // 3. 저장 완료된 Entity를 다시 순수 도메인 객체로 변환하여 Application 계층에 반환합니다[cite: 2].
        return userMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<User> loadUserByEmail(String email) {
        // DB에서 Entity를 찾아 Domain 객체로 매핑하여 반환
        return userRepository.findByEmail(email)
                .map(userMapper::toDomain);
    }

    @Override
    public Optional<User> loadUserById(Long id) {
        // JpaRepository는 findById를 기본으로 제공합니다.
        return userRepository.findById(id)
                .map(userMapper::toDomain);
    }
}