package market.marketproject.dto;

import lombok.Data;


@Data
public class PaymentDto {
    private String productName;
    private String buyerName;
    private String buyerEmail;
    private String buyerTel;
    private String buyerUuid;
    private int orderAmount;
    private String allProductUuids;
}
