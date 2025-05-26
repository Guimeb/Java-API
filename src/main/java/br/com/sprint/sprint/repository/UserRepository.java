package br.com.sprint.sprint.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.sprint.sprint.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
