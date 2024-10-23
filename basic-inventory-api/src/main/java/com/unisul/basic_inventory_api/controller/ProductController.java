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

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    @Autowired
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
            @RequestParam(defaultValue = "asc") String sortDirection) {

        ProductListDTO productList = productService.getPaginatedProducts(page, rowsPerPage, search, sortField, sortDirection);
        return ResponseEntity.ok(productList);
    }

    // Obter um produto específico
    @Operation(summary = "Obter produto por ID", description = "Retorna os detalhes de um produto específico pelo seu ID.")
    @ApiResponse(responseCode = "200", description = "Produto encontrado com sucesso.")
    @ApiResponse(responseCode = "404", description = "Produto não encontrado com o ID fornecido.")
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable int id) {
        return productService.getProductById(id) // Ensure this references the correct service
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build()); // Retorna 404 se não encontrado
    }

    // Cadastrar novo produto
    @Operation(summary = "Cadastrar um novo produto", description = "Cria um novo produto. Retorna 409 se o produto já existir.")
    @ApiResponse(responseCode = "201", description = "Produto criado com sucesso.")
    @ApiResponse(responseCode = "409", description = "Conflito: Produto já existe.")
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        // Verifica se o produto já existe
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
                .map(updatedProduct -> ResponseEntity.ok(updatedProduct)) // Retorna produto atualizado
                .orElse(ResponseEntity.notFound().build()); // Retorna 404 se não encontrado
    }

    // Deletar produto (exclusão lógica)
    @Operation(summary = "Deletar um produto", description = "Realiza a exclusão lógica de um produto pelo ID.")
    @ApiResponse(responseCode = "204", description = "Produto deletado com sucesso.")
    @ApiResponse(responseCode = "404", description = "Produto não encontrado.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable int id) {
        // Verifica se a exclusão foi bem-sucedida
        if (!productService.setProductDeleted(id)) {
            return ResponseEntity.notFound().build(); // Retorna 404 se não encontrado
        }
        return ResponseEntity.noContent().build(); // Retorna 204 No Content
    }
}
