package market.marketproject.service;

import lombok.extern.slf4j.Slf4j;
import market.marketproject.dto.Product;
import market.marketproject.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class ProductService {
    private ProductMapper productMapper;

    @Autowired
    public ProductService(ProductMapper productMapper){
        this.productMapper = productMapper;
    }

    /* 판매 상품 등록 */
    public ResponseEntity registerProduct(Product product){
        try {
            product.setProductUuid(UUID.randomUUID().toString());
            product.setIdx(productMapper.count(product) + 1);
            productMapper.registerProduct(product);
            return ResponseEntity.status(HttpStatus.OK).body("상품 등록 완료");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("판매자 정보 없음");
        }
    }

    /* 판매자 판매 상품 조회*/
    public ResponseEntity productOfSeller(Product product){
        return ResponseEntity.status(HttpStatus.OK).body(productMapper.productOfSeller(product));
    }

    /* 카테고리를 통해 판매 상품 조회 */
    public List<Product> productOfCategory(Product product){
        return productMapper.productOfCategory(product);
    }

    /* 모든 상품 조회 */
    public List<Product> allProduct(Product product){
        product.setOffset((product.getPage() - 1) * product.getRecordSize());
        return productMapper.allProduct(product);
    }

    /* 판매 상품 삭제 */
    public ResponseEntity deleteProduct(Product product){
        productMapper.deleteProduct(product);
        return ResponseEntity.status(HttpStatus.OK).body("삭제 성공");
    }
}
