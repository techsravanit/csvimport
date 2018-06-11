package com.purpletalk.repositary;

import org.springframework.data.jpa.repository.JpaRepository;

import com.purpletalk.entities.TestEmp;

public interface TestEmpRepositary extends JpaRepository<TestEmp, String>{

}
