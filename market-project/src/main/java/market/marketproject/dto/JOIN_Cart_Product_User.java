package market.marketproject.dto;

import lombok.Data;

import java.util.List;

@Data
public class JOIN_Cart_Product_User {
    // product
    // cart
    // user
    private String userUuid;
    private String userName;
    private String userTel;
    private String userEmail;
    private String sellerUuid;
    private String productUuid;
    private String productName;
    private Integer productCount;
    private Integer productPrice;
    private Integer cartProductCount;
    private Integer orderAmount;
    private List<String> productUuids;
    private String allProductUuids;
}
