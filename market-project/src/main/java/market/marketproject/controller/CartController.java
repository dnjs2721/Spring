package market.marketproject.controller;

import market.marketproject.dto.Cart;
import market.marketproject.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CartController {
    private CartService cartService;

    @Autowired
    public  CartController(CartService cartService){
        this.cartService = cartService;
    }

    /* 장바구니 추가 */
    @PostMapping("/cart/add")
    public ResponseEntity addCart(@RequestBody Cart cart){
        return ResponseEntity.status(HttpStatus.OK).body(cartService.addCart(cart));
    }

    /* 장바구니 조회 */
    @PostMapping("/cart/load")
    public ResponseEntity loadCart(@RequestBody Cart cart){
        return ResponseEntity.status(HttpStatus.OK).body(cartService.loadCart(cart));
    }
}
