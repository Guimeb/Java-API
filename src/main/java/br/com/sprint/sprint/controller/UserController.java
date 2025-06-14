package br.com.sprint.sprint.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import br.com.sprint.sprint.dto.UserRequestCreate;
import br.com.sprint.sprint.dto.UserRequestUpdate;
import br.com.sprint.sprint.dto.UserResquestDelete;
import br.com.sprint.sprint.model.User;
import br.com.sprint.sprint.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping
    public List<User> listAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public User getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public User create(@RequestBody UserRequestCreate dto) {
        return service.create(dto);
    }

    @PutMapping
    public User update(@RequestBody UserRequestUpdate dto) {
        return service.update(dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        UserResquestDelete dto = new UserResquestDelete();
        dto.setId(id);
        service.delete(dto);
    }
}
