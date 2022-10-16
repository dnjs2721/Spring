package market.marketproject.mapper;

import market.marketproject.dto.Product;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper
public interface ProductMapper {
    void registerProduct(Product product);
    void deleteProduct(Product product);
    List<Product> productOfSeller(Product product);
}
