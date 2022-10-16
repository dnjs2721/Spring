package market.marketproject.service;

import lombok.extern.slf4j.Slf4j;
import market.marketproject.dto.User;
import market.marketproject.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class UserService {

    private UserMapper userMapper;
    private MailService mailService;
    private RedisUtil redisUtil;

    @Autowired
    public UserService(UserMapper userMapper, MailService mailService, RedisUtil redisUtil) {
        this.userMapper = userMapper;
        this.mailService = mailService;
        this.redisUtil = redisUtil;
    }

    // 회원가입
    public int registerUser(User user){
        // 이미 등록된 이메일
        if(userMapper.checkEmailExists(user) == 1){
            log.info("이미 가입된 이메일 입니다.");
            return 0;
        } // 이미 등록된 이름과 생일
        else if(userMapper.checkNameExists(user) == 1){
            log.info("이미 가입된 사용자 입니다.");
            return 1;
        }
        user.setUserUuid(UUID.randomUUID().toString());
        userMapper.registerUser(user);
        log.info("회원가입 되었습니다. (이메일 인증 필요)");
        return 2;
    }

    // 인증메일 발송
    public void sendEmail(User user, HttpServletRequest request) throws Exception{
        HttpSession session = request.getSession();
        session.setAttribute("user_email", user.getUserEmail());
        String mail_Key = mailService.sendEmail(user.getUserEmail());
        user.setMailKey(mail_Key);
        log.info("인증번호 발송");
    }

    // 회원가입 후 인증
    public boolean certificationEmail(User user, HttpServletRequest request){
        HttpSession session = request.getSession();
        user.setUserEmail((String) session.getAttribute("user_email"));
        if(user.getMailKey().equals(redisUtil.getData(user.getUserEmail()))){
            userMapper.updateMailAuth(user);
            redisUtil.deleteData(user.getUserEmail());
            log.info("이메일 인증 성공");
            log.info("다시 로그인 해주세요");
            session.invalidate();
            return true;
        }else {
            log.info("이메일 인증 실패");
            return false;
        }
    }

     // 이메일을 통해 사용자 정보 조회
    public List<User> getUserByUserEmail(User user){
        return userMapper.getUserByUserEmail(user);
    }

    // 이름과 생일을 통해 이메일 찾기
    public Boolean findEmail(User user, HttpServletRequest request){
        if(userMapper.checkNameExists(user) == 1){
            HttpSession session = request.getSession();
            session.setAttribute("user_name", user.getUserName());
            session.setAttribute("user_email", userMapper.findEmail(user));
            return true;
        }
        log.info("등록된 회원이 아닙니다.");
        return false;
    }

    // 이메일 인증을 통해 비밀번호 찾기
    public Boolean findPassword(User user, HttpServletRequest request){
        HttpSession session = request.getSession();
        user.setUserEmail((String) session.getAttribute("user_email"));
        if(user.getMailKey().equals(redisUtil.getData(user.getUserEmail()))){
            redisUtil.deleteData(user.getUserEmail());
            session.setAttribute("user_password", userMapper.findPassword(user));
            return true;
        }else{
            log.info("이메일 인증 실패");
            return false;
        }
    }

    // 로그인
    public int login(User user, HttpServletRequest request) {
        /* 이메일, 패스워드 체크 */
        if (userMapper.checkEmailPasswordExists(user) == 1 && userMapper.emailAuthFail(user) == 1) {
            log.info("======= 로그인 성공 =======");
            String userEmail = user.getUserEmail();
            String userUuid = getUserByUserEmail(user).get(0).getUserUuid();
            String userName = getUserByUserEmail(user).get(0).getUserName();
            HttpSession session = request.getSession();
            session.setAttribute("user_uuid", userUuid);
            session.setAttribute("user_name", userName);
            session.setAttribute("user_Email", userEmail);

            return 0;
        } else if (userMapper.checkEmailPasswordExists(user) == 1 && userMapper.emailAuthFail(user) != 1) {
            log.info("이메일 인증 필요.");
            return 1;
        } else{
            log.info("이메일 혹은 비밀번호가 틀렸습니다.");
            return 2;
        }
    }
}
