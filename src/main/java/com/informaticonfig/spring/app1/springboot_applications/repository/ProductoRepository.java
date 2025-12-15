package com.informaticonfig.spring.app1.springboot_applications.repository;

import com.informaticonfig.spring.app1.springboot_applications.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
}
