package com.unisul.basic_inventory_api.repository;

import com.unisul.basic_inventory_api.model.Category;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    @Query("SELECT c FROM Category c WHERE (:search IS NULL OR c.name LIKE %:search%) AND c.deleted = false")
    List<Category> findCategoriesWithSearch(@Param("search") String search, Pageable pageable);

    @Query("SELECT COUNT(c) FROM Category c WHERE (:search IS NULL OR c.name LIKE %:search%) AND c.deleted = false")
    long countCategoriesBySearch(@Param("search") String search);

    // Metodo para encontrar uma categoria pelo nome
    @Query("SELECT c FROM Category c WHERE c.name = :name")
    Optional<Category> findByName(@Param("name") String name);

}
