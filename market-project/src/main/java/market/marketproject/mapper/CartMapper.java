package market.marketproject.mapper;

import market.marketproject.dto.Cart;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CartMapper {
    void addCart(Cart cart);
    List<Cart> loadCart(Cart cart);
}
