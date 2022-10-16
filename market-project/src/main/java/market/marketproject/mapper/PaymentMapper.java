package market.marketproject.mapper;

import market.marketproject.dto.JOIN_Cart_Product_User;
import market.marketproject.dto.PaymentDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PaymentMapper {
    List<JOIN_Cart_Product_User> loadInfo(JOIN_Cart_Product_User joinCartProductUser);
    void updateProductCount(JOIN_Cart_Product_User joinCartProductUser);
    void deleteCartList(JOIN_Cart_Product_User joinCartProductUser);
    void paymentParam(JOIN_Cart_Product_User joinCartProductUser);
    List<PaymentDto> loadPaymentParam(PaymentDto paymentDto);
    void deleteOrderList(JOIN_Cart_Product_User joinCartProductUser);
    Integer checkOrderExists(JOIN_Cart_Product_User joinCartProductUser);

}
