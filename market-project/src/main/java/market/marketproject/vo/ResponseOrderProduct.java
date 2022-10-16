package market.marketproject.vo;

import lombok.Data;
import market.marketproject.dto.Cart;

@Data
public class ResponseOrderProduct {
    private String productUuid;
    private String sellerUuid;
    private String productName;
    private String productCount;
    private String productPrice;
    private Integer cartProductCount;


}
