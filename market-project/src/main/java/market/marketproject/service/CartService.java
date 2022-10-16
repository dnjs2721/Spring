package market.marketproject.service;

import market.marketproject.dto.Cart;
import market.marketproject.mapper.CartMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {
    private CartMapper cartMapper;

    @Autowired
    public CartService(CartMapper cartMapper){
        this.cartMapper = cartMapper;
    }

    /* 장바구니 상품 등록 */
    public ResponseEntity addCart(Cart cart){
        cartMapper.addCart(cart);
        return ResponseEntity.status(HttpStatus.OK).body("장바구니 추가");
    }

    /* 장바구니 조회 */
    public ResponseEntity loadCart(Cart cart){
        List<Cart> loadCart = cartMapper.loadCart(cart);
        if(loadCart.size() == 0){
            return ResponseEntity.status(HttpStatus.OK).body("장바구니 비어있음");
        }
        return ResponseEntity.status(HttpStatus.OK).body(cartMapper.loadCart(cart));
    }
}
