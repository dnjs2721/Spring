package market.marketproject.controller;

import lombok.extern.slf4j.Slf4j;
import market.marketproject.dto.Product;
import market.marketproject.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
public class ProductController {

    private ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /* 판매 상품 등록 */
    @PostMapping("/register/item")
    public ResponseEntity registerItem(@RequestBody Product product){
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.registerProduct(product));
    }

    /* 판매자 상품 조회*/
    @PostMapping("/search/item")
    public ResponseEntity productOfSeller(@RequestBody Product product){
        return ResponseEntity.status(HttpStatus.OK).body(productService.productOfSeller(product));
    }

    /* 판매 상품 삭제 */
    @PostMapping("/delete/item")
    public ResponseEntity deleteProduct(@RequestBody Product product){
        return ResponseEntity.status(HttpStatus.OK).body(productService.deleteProduct(product));
    }
}
