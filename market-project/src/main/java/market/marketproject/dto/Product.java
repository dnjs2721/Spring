package market.marketproject.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Product {
    private String productUuid;
    private String sellerUuid;
    private String productName;
    private Integer productCount;
    private Integer productPrice;
    private String bigCategory;
    private String middleCategory;
    private String smallCategory;
    private Integer idx;

    private Integer page;
    private Integer recordSize;
    private Integer pageSize;
    private String keyword;
    private String searchType;
    private Integer offset;

//    public  Product() {
//        this.page = 1;
//        this.recordSize = 10;
//        this.pageSize = 10;
//    }

//    public int getOffset(){
//        return (page - 1) * recordSize;
//    }
}
