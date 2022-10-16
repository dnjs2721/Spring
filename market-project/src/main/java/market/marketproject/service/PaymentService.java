package market.marketproject.service;

import lombok.extern.slf4j.Slf4j;
import market.marketproject.dto.JOIN_Cart_Product_User;
import market.marketproject.dto.PaymentDto;
import market.marketproject.mapper.PaymentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

@Service
@Slf4j
public class PaymentService {

    private PaymentMapper paymentMapper;

    @Autowired
    public PaymentService(PaymentMapper paymentMapper) {
        this.paymentMapper = paymentMapper;
    }

    /* cart, product 정보 조합 */
    public ArrayList<HashMap<String, String>> loadInfo(JOIN_Cart_Product_User joinCartProductUser){
        List<String> productUuids = joinCartProductUser.getProductUuids();
        int selectedNum = productUuids.size();
        ArrayList<HashMap<String, String>> selectedProduct = new ArrayList<>();

        if(selectedNum > 0){
            for(int i = 0; i < selectedNum; i++){
                LinkedHashMap<String, String> selectedHashMap = new LinkedHashMap<>();
                joinCartProductUser.setProductUuid(productUuids.get(i));
                List<JOIN_Cart_Product_User> selectedList = paymentMapper.loadInfo(joinCartProductUser);

                selectedHashMap.put("user_name", selectedList.get(0).getUserName());
                selectedHashMap.put("user_tel", selectedList.get(0).getUserTel());
                selectedHashMap.put("user_email", selectedList.get(0).getUserEmail());
                selectedHashMap.put("product_uuid", selectedList.get(0).getProductUuid());
                selectedHashMap.put("product_name", selectedList.get(0).getProductName());
                selectedHashMap.put("product_price", selectedList.get(0).getProductPrice().toString());
                selectedHashMap.put("product_count", selectedList.get(0).getProductCount().toString());
                selectedHashMap.put("cart_product_count", selectedList.get(0).getCartProductCount().toString());
                selectedProduct.add(selectedHashMap);
            }
            LinkedHashMap<String, String> allProductUuids = new LinkedHashMap<>();
            allProductUuids.put("all_product_uuids", productUuids.toString());
            selectedProduct.add(allProductUuids);
        }
        return selectedProduct;
    }

    /* 주문 목록 만들기 */
    public LinkedHashMap<String, String> makeOrderList (JOIN_Cart_Product_User joinCartProductUser){
        if(paymentMapper.checkOrderExists(joinCartProductUser) != 1){
            LinkedHashMap<String, String> orderList = new LinkedHashMap<>();
            ArrayList<HashMap<String, String>> loadInfo = loadInfo(joinCartProductUser);
            int orderCount = loadInfo.size();
            int totalPrice = 0;

            String allProductUuids = loadInfo.get(orderCount - 1).get("all_product_uuids");
            allProductUuids = allProductUuids.replace("[", "");
            allProductUuids = allProductUuids.replace("]", "");
            allProductUuids = allProductUuids.replace(", ", ",");

            String buyerEmail = loadInfo.get(0).get("user_email");
            String buyerName = loadInfo.get(0).get("user_name");
            String buyerTel = loadInfo.get(0).get("user_tel");
            String userUuid = joinCartProductUser.getUserUuid();
            String allProduct = loadInfo.get(0).get("product_name") + "외 " + (orderCount - 1) + "건";

            if (orderCount > 0) {
                for (int i = 0; i < orderCount - 1; i++) {
                    int productCount = Integer.parseInt(loadInfo.get(i).get("product_count"));
                    int cartProductCount = Integer.parseInt(loadInfo.get(i).get("cart_product_count"));
                    int productPrice = Integer.parseInt(loadInfo.get(i).get("product_price"));

                    if (productCount >= cartProductCount) {
                        totalPrice = totalPrice + (productPrice * cartProductCount);

                    } else {
                        log.info("상품명 : " + loadInfo.get(i).get("product_name"));
                        log.info("상태 : " + "상품 수량 부족");
                    }
                }
                orderList.put("product", allProduct);
                orderList.put("buyer_name", buyerName);
                orderList.put("buyer_email", buyerEmail);
                orderList.put("buyer_tel", buyerTel);
                orderList.put("buyer_uuid", userUuid);
                orderList.put("totalPrice", String.valueOf(totalPrice));
                orderList.put("ProductUuids", allProductUuids);

                joinCartProductUser.setProductName(allProduct);
                joinCartProductUser.setUserName(buyerName);
                joinCartProductUser.setUserEmail(buyerEmail);
                joinCartProductUser.setUserTel(buyerTel);
                joinCartProductUser.setUserUuid(userUuid);
                joinCartProductUser.setOrderAmount(totalPrice);
                joinCartProductUser.setAllProductUuids(allProductUuids);
                paymentMapper.paymentParam(joinCartProductUser);
            } else {
                log.info("선택된 상품 없음");
                return null;
            }
            return orderList;
        }else{
            log.info("사용자에 대한 주문서가 이미 있습니다.");
            return null;
        }
    }

    /* 결제 페이지 불러오기*/
    public void loadPaymentParam(PaymentDto paymentDto, HttpServletRequest request, Model model){
        HttpSession session = request.getSession();
        String buyer_uuid = (String) session.getAttribute("user_uuid");
        paymentDto.setBuyerUuid(buyer_uuid); // 로그인 쿠키, 세션으로 교체 되어야 함
        List<PaymentDto> param = paymentMapper.loadPaymentParam(paymentDto);
        log.info(param.toString());
        model.addAttribute("pro_name", param.get(0).getProductName());
        model.addAttribute("buyer_name", param.get(0).getBuyerName());
        model.addAttribute("buyer_email", param.get(0).getBuyerEmail());
        model.addAttribute("buyer_tel", param.get(0).getBuyerTel());
        model.addAttribute("buyer_uuid", param.get(0).getBuyerUuid());
        model.addAttribute("amount", param.get(0).getOrderAmount());
        model.addAttribute("ProductUuids", param.get(0).getAllProductUuids());
    }

    /* 결제 */
    public ResponseEntity payment (JOIN_Cart_Product_User joinCartProductUser){
        try{
            String[] splitStr = joinCartProductUser.getProductUuid().split(",");
            for(int i = 0; i < splitStr.length; i++){
                joinCartProductUser.setProductUuid(splitStr[i]);
                paymentMapper.updateProductCount(joinCartProductUser);
                paymentMapper.deleteOrderList(joinCartProductUser);
                paymentMapper.deleteCartList(joinCartProductUser);
            }
            log.info("결제 성공");
            return ResponseEntity.status(HttpStatus.OK).body("결제 성공");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("장바구니 비어있음");
        }
    }
}
