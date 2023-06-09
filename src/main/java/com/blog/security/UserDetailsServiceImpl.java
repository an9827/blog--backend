package com.blog.security;

import com.blog.adapter.user.repository.UserRepository;
import com.blog.infrastructure.user.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> userEntity = userRepository.findByUsername(username);
        if (userEntity.isPresent()) {
            return UserPrincipalInfo.builder().userEntity(userEntity.get()).build();
        } else {
            log.error(String.format("Access denied: Username: %s not found !", username));
            throw new UsernameNotFoundException(String.format("Username: %s not found !", username));
        }
    }
}
