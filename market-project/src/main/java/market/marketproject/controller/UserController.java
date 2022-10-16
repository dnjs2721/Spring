package market.marketproject.controller;

import lombok.extern.slf4j.Slf4j;
import market.marketproject.dto.User;
import market.marketproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Member;
import java.util.List;

@Slf4j
@Controller
//@RequestMapping("/api")
public class UserController {
    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /* 메인 홈 */
    @GetMapping(value = "/home")
    public String home(HttpServletRequest request, HttpServletResponse response){
        HttpSession session = request.getSession();
        String user_uuid = (String) session.getAttribute("user_uuid");
        if(user_uuid != null) log.info("사용자 정보 : " + user_uuid);
        return "home";
    }
    @PostMapping("/home")
    public String home(HttpServletRequest request){
        HttpSession session = request.getSession();
        session.invalidate();
        return "redirect:/home";
    }

    /* 회원가입 */
    @RequestMapping("/register")
    public String register(){
        return "register";
    }
    @PostMapping("/register.do")
    public String registerUser(@ModelAttribute User user){
        userService.registerUser(user);
        return "redirect:/home";
    }

    /* 로그인 */
    @RequestMapping("/login")
    public String login() {
        return "login";
    }
    @PostMapping("/login.do")
    public String loginId(@ModelAttribute User user, HttpServletRequest request) {
        int state = userService.login(user, request);
        if (state == 0) {
            return "redirect:/home";
        } else if (state == 1) {
            return "/certificationEmail";
        }
        return "redirect:/login";
    }

    /* 로그아웃*/
    @PostMapping("/logout")
    public String logout(HttpServletRequest request){
        HttpSession session = request.getSession();
        session.invalidate();
        log.info("로그아웃 되었습니다.");
        return "redirect:/home";
    }

    /* 아이디 찾기*/
    @RequestMapping("/findEmail")
    public String findEmail(){
        return "findEmail";
    }
    @PostMapping("/findEmail.do")
    public String findEmailDo(@ModelAttribute User user, HttpServletRequest request){
        if(userService.findEmail(user, request)){
            return "redirect:/findEmail";
        }
        return "redirect:/home";
    }

    /* 비밀번호 찾기 */
    @RequestMapping("/findPassword")
    public String findPassword(HttpServletRequest request){
        HttpSession session = request.getSession();
        session.setAttribute("state", "findPassword");
        return "findPassword";
    }
    @PostMapping("/findPassword.do")
    public String findPasswordDo(@ModelAttribute User user, HttpServletRequest request){
        if(userService.findPassword(user, request)){
            return "redirect:/findPassword";
        }
        return "redirect:/home";
    }

    /* 이메일 인증*/
    @RequestMapping("/certificationEmail")
    public String certificationEmail(){
        return "certificationEmail";
    }
    @PostMapping("/sendEmail.do")
    public String sendEmail(@ModelAttribute User user, HttpServletRequest request) throws Exception{
        HttpSession session = request.getSession();
        userService.sendEmail(user, request);
        if("findPassword".equals(session.getAttribute("state"))){
            return "redirect:/findPassword";
        }
        return "redirect:/certificationEmail";
    }
    @PostMapping("/certificationEmail.do")
    public String certification(@ModelAttribute User user, HttpServletRequest request){
        if(!userService.certificationEmail(user,request)){
            return "redirect:/certificationEmail";
        }
        return "redirect:/home";
    }
}
