package org.nurma.aqyndar.service;

import lombok.RequiredArgsConstructor;
import org.nurma.aqyndar.entity.User;
import org.nurma.aqyndar.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public Optional<User> getByEmail(final String email) {
        return userRepository.findByEmail(email);
    }

    public void save(final User user) {
        userRepository.save(user);
    }

    public void delete(final User user) {
        userRepository.delete(user);
    }
}
