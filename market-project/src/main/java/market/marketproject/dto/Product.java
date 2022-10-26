package market.marketproject.dto;

import lombok.Data;

@Data
public class Product {
    private String productUuid;
    private String sellerUuid;
    private String productName;
    private Integer productCount;
    private Integer productPrice;
    private String bigCategory;
    private String middleCategory;
    private String smallCategory;
}
