package market.marketproject.controller;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.response.Payment;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import lombok.extern.slf4j.Slf4j;
import market.marketproject.dto.JOIN_Cart_Product_User;
import market.marketproject.dto.PaymentDto;
import market.marketproject.mapper.PaymentMapper;
import market.marketproject.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Locale;

@Slf4j
@Controller
public class PaymentController {

    private PaymentService paymentService;
    private IamportClient api;
    private PaymentMapper paymentMapper;

    public PaymentController(PaymentService paymentService, PaymentMapper paymentMapper) {
        this.paymentService = paymentService;
        this.api = new IamportClient("apiKey",
                "apiSecret");
        this.paymentMapper = paymentMapper;
    }

    /* 결제 페이지 불러오기*/
    @PostMapping(value = "/payment")
    public String iamport(Model model, PaymentDto paymentDto, HttpServletRequest request){
        try {
            paymentService.loadPaymentParam(paymentDto, request, model);
            return "payment";
        }catch (Exception e){
            return "404";
        }
    }

    /* 결제 검증 */
    @ResponseBody
    @RequestMapping(value = "/verifyIamport/{imp_uid}")
    public IamportResponse<Payment> paymentByImUid(
            Model model, Locale locale, HttpSession session,
            @PathVariable(value = "imp_uid") String imp_uid) throws IamportResponseException, IOException
    {
        return api.paymentByImpUid(imp_uid);
    }

    /* 결제 후 동작 */
    @RequestMapping(value = "paymentStat.do", method = {RequestMethod.POST})
    public ResponseEntity paymentStat(@RequestParam("Status") String Status,
                            @RequestParam("buyer_uuid") String buyer_uuid,
                            @RequestParam("product_uuids") String product_uuids, JOIN_Cart_Product_User joinCartProductUser){
        if(Status.equals("true")){
            joinCartProductUser.setUserUuid(buyer_uuid);
            joinCartProductUser.setProductUuid(product_uuids);
            return ResponseEntity.status(HttpStatus.OK).body(paymentService.payment(joinCartProductUser));
        }
        else {
            log.info("결제 실패");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("결제 실패");
        }
    }
}