package market.marketproject.vo;

import lombok.Data;

@Data
public class ResponseMyCart {
    private String productUuid;
    private String productCount;
    private String productPrice;
    private Integer cartProductCount;

}
