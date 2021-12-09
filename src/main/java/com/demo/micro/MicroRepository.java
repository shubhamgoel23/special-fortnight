package com.demo.micro;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MicroRepository extends JpaRepository<Micro, Integer> {
}