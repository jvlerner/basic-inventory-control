package com.unisul.basic_inventory_api.repository;

import com.unisul.basic_inventory_api.model.Product;
import com.unisul.basic_inventory_api.model.ProductDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    @Query("SELECT p, COUNT(p) OVER() AS totalItems " +
            "FROM Product p LEFT JOIN p.category c " +
            "WHERE (:search IS NULL OR p.name LIKE %:search% OR c.name LIKE %:search%) " +
            "AND p.deleted = false")
    List<ProductDTO> findProductsAndCount(@Param("search") String search,
                                          Pageable pageable);

    // Metodo para buscar produtos com quantidade menor que o valor fornecido
    @Query("SELECT p, COUNT(p) OVER() AS totalItems " +
            "FROM Product p LEFT JOIN p.category c " +
            "WHERE p.quantity < :quantity AND p.deleted = false " +
            "AND (c.deleted = false) " +
            "AND (:search IS NULL OR p.name LIKE %:search% OR c.name LIKE %:search%)")
    List<ProductDTO> findProductsWithCategoriesLowStock(@Param("search") String search,
                                                     @Param("quantity") int quantity,
                                                     Pageable pageable);

    // Metodo para encontrar um produto pelo nome
    @Query("SELECT p FROM Product p WHERE p.name = :name AND p.deleted = false")
    Optional<Product> findByName(@Param("name") String name);

}
