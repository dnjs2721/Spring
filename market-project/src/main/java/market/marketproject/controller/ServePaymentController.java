package market.marketproject.controller;

import market.marketproject.dto.JOIN_Cart_Product_User;
import market.marketproject.dto.PaymentDto;
import market.marketproject.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ServePaymentController {

    private PaymentService paymentService;

    @Autowired
    public ServePaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/buy/loadInfo")
    public ResponseEntity loadInfo(@RequestBody JOIN_Cart_Product_User joinCartProductUser){
        return ResponseEntity.status(HttpStatus.OK).body(paymentService.loadInfo(joinCartProductUser));
    }

    @PostMapping("/orderList")
    public ResponseEntity buyProcess(@RequestBody JOIN_Cart_Product_User joinCartProductUser){
        return ResponseEntity.status(HttpStatus.OK).body(paymentService.makeOrderList(joinCartProductUser));
    }

    @PostMapping("/payment")
    public ResponseEntity payment(@RequestBody JOIN_Cart_Product_User joinCartProductUser){
        return ResponseEntity.status(HttpStatus.OK).body(paymentService.payment(joinCartProductUser));
    }
}
