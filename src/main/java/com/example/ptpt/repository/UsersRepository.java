package com.example.ptpt.repository;

import com.example.ptpt.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<UserEntity,Long> {
}
