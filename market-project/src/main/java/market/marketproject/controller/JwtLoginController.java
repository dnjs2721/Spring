package market.marketproject.controller;

import market.marketproject.dto.JwtUser;
import market.marketproject.service.JwtUserDetailService;
import market.marketproject.service.JwtUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class JwtLoginController {

    private final JwtUserService jwtUserService;

    @Autowired
    public JwtLoginController(JwtUserService jwtUserService, JwtUserDetailService jwtUserDetailService) {
        this.jwtUserService = jwtUserService;
    }

    @GetMapping("/hello")
    public ResponseEntity<String> hello(){
        return ResponseEntity.status(HttpStatus.OK).body("hello");
    }

    @PostMapping("/jwtLogin")
    public ResponseEntity<String> jwtLogin(@RequestBody JwtUser jwtUser){
        return jwtUserService.login(jwtUser);
    }

    @PostMapping("/jwtRegister")
    public ResponseEntity<String> jwtRegister(@RequestBody JwtUser jwtUser){
        ResponseEntity<String> r = jwtUserService.register(jwtUser);
        return ResponseEntity.status(r.getStatusCode()).body(r.getBody());
    }
}
