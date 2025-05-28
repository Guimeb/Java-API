package br.com.sprint.sprint.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.sprint.sprint.dto.UserRequestCreate;
import br.com.sprint.sprint.repository.UserRepository;
import br.com.sprint.sprint.model.User;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User createUser(UserRequestCreate dto) {
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        return userRepository.save(user);
    }
}

