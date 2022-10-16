package market.marketproject.dto;

import lombok.Data;

@Data
public class Cart {
    private String userUuid;
    private String productUuid;
    private Integer cartProductCount;
}
