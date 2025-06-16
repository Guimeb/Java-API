package br.com.sprint.sprint.service.impl;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.sprint.sprint.dto.UserRequestCreate;
import br.com.sprint.sprint.dto.UserRequestUpdate;
import br.com.sprint.sprint.dto.UserResquestDelete;
import br.com.sprint.sprint.exception.ResourceNotFoundException;
import br.com.sprint.sprint.model.User;
import br.com.sprint.sprint.repository.UserRepository;
import br.com.sprint.sprint.service.UserService;
import br.com.sprint.sprint.service.WalletService;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository repo;
    private final WalletService walletService;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository repo,
            WalletService walletService,
            PasswordEncoder passwordEncoder) {
        this.repo = repo;
        this.walletService = walletService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User create(UserRequestCreate dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        User saved = repo.save(user);

        walletService.createWalletForUser(saved.getId());

        return saved;
    }

    @Override
    public User update(UserRequestUpdate dto) {
        User user = repo.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + dto.getId()));
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        return repo.save(user);
    }

    @Override
    public void delete(UserResquestDelete dto) {
        User user = repo.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + dto.getId()));
        repo.delete(user);
    }

    @Override
    @Transactional(readOnly = true)
    public User findById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return repo.findAll();
    }
}
