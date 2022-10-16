package market.marketproject.vo;

import lombok.Data;
import market.marketproject.dto.Cart;

import java.util.List;

@Data
public class ResponseOrderCart {
    private String userUuid;
    private List<Cart> cart;
}
