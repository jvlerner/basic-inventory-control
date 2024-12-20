package com.unisul.basic_inventory_api.controller;

import com.unisul.basic_inventory_api.model.Product;
import com.unisul.basic_inventory_api.model.ProductListDTO;
import com.unisul.basic_inventory_api.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Arrays;

@RestController
@RequestMapping("/products")
public class ProductController {
    @Autowired
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // Listar produtos com paginação e busca
    @Operation(summary = "Listar produtos", description = "Obtém uma lista de produtos com opções de busca, paginação e ordenação.")
    @ApiResponse(responseCode = "200", description = "Lista de produtos retornada com sucesso.")
    @GetMapping
    public ResponseEntity<ProductListDTO> getAllProducts(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int rowsPerPage,
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "asc") String sortDirection,
            @RequestParam(required = false, defaultValue = "20") int quantity,
            @RequestParam(required = false, defaultValue = "false") boolean lowStock) {

        if (page < 1) {
            return ResponseEntity.badRequest().build();
        }

        if (5 > rowsPerPage || rowsPerPage > 100) {
            return ResponseEntity.badRequest().build();
        }

        if (!Arrays.asList("name", "id", "price", "quantity").contains(sortField.toLowerCase())) {
            return ResponseEntity.badRequest().build();
        }
        if (!Arrays.asList("asc", "desc").contains(sortDirection.toLowerCase())) {
            return ResponseEntity.badRequest().build();
        }
        if (lowStock) {
            ProductListDTO productList = productService.getPaginatedProductsLowStock(page, rowsPerPage,
                    search.toLowerCase(),
                    quantity,
                    sortField.toLowerCase(),
                    sortDirection.toLowerCase());
            return ResponseEntity.ok(productList);
        }

        ProductListDTO productList = productService.getPaginatedProducts(page, rowsPerPage, search.toLowerCase(),
                sortField.toLowerCase(),
                sortDirection.toLowerCase());
        return ResponseEntity.ok(productList);
    }

    // Obter um produto específico
    @Operation(summary = "Obter produto por ID", description = "Retorna os detalhes de um produto específico pelo seu ID.")
    @ApiResponse(responseCode = "200", description = "Produto encontrado com sucesso.")
    @ApiResponse(responseCode = "404", description = "Produto não encontrado com o ID fornecido.")
    @GetMapping("/{id}")

    public ResponseEntity<Product> getProduct(@PathVariable int id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok) // Retorna 200 se encontrado
                .orElse(ResponseEntity.notFound().build()); // Retorna 404 se não encontrado
    }

    // Cadastrar novo produto
    @Operation(summary = "Cadastrar um novo produto", description = "Cria um novo produto. Retorna 409 se o produto já existir.")
    @ApiResponse(responseCode = "201", description = "Produto criado com sucesso.")
    @ApiResponse(responseCode = "409", description = "Conflito: Produto já existe.")
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        if (productService.productExists(product.getName())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build(); // Retorna 409 Conflict
        }

        Product savedProduct = productService.saveProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct); // Retorna 201 Created
    }

    // Atualizar produto existente
    @Operation(summary = "Atualizar um produto", description = "Atualiza um produto existente pelo ID.")
    @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso.")
    @ApiResponse(responseCode = "404", description = "Produto não encontrado.")
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable int id, @RequestBody Product product) {
        return productService.updateProduct(id, product)
                .map(ResponseEntity::ok) // Retorna produto atualizado
                .orElse(ResponseEntity.notFound().build()); // Retorna 404 se não encontrado); // Retorna produto
                                                            // atualizado
    }

    // Deletar produto (exclusão lógica)
    @Operation(summary = "Deletar um produto", description = "Realiza a exclusão lógica de um produto pelo ID.")
    @ApiResponse(responseCode = "204", description = "Produto deletado com sucesso.")
    @ApiResponse(responseCode = "404", description = "Produto não encontrado.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable int id) {
        if (productService.setProductDeleted(id)) {
            return ResponseEntity.noContent().build(); // Retorna 204 No Content
        }
        return ResponseEntity.notFound().build(); // Retorna 404 se não encontrado
    }
}
