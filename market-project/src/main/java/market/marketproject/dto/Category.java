package market.marketproject.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Category {
    private String groupId;
    private Integer categoryLev;
    private String nKey;
    private String categoryNm;
    private Integer categoryDetailLev;
    private String categoryDetailNm;
    private Integer categoryParentLev;
    private Integer categoryDetailParentLev;
    private String categoryName;
    private String navigator;
}
