package br.com.sprint.sprint.service;

import java.util.List;

import br.com.sprint.sprint.dto.UserRequestCreate;
import br.com.sprint.sprint.dto.UserRequestUpdate;
import br.com.sprint.sprint.dto.UserResquestDelete;
import br.com.sprint.sprint.model.User;

public interface UserService {
    User create(UserRequestCreate dto);
    User update(UserRequestUpdate dto);
    void delete(UserResquestDelete dto);
    User findById(Long id);
    List<User> findAll();
}
