package market.marketproject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Random;


@Service
public class MailService {
    private JavaMailSender mailSender;
    private RedisUtil redisUtil;

    @Autowired
    public MailService(JavaMailSender mailSender, RedisUtil redisUtil) {
        this.mailSender = mailSender;
        this.redisUtil = redisUtil;
    }

    private String authNum;

    // 인증번호 생성
    public void createKey(){
        Random ran = new Random();
        StringBuffer key = new StringBuffer();

        for(int i=0;i<8;i++) {
            int index = ran.nextInt(3);

            switch (index) {
                case 0 :
                    key.append((char) ((int)ran.nextInt(26) + 97));
                    break;
                case 1:
                    key.append((char) ((int)ran.nextInt(26) + 65));
                    break;
                case 2:
                    key.append(ran.nextInt(9));
                    break;
            }
        }
        authNum = key.toString();
    }

    // 메일 전송
    public String sendEmail(String userEmail) throws MessagingException{
        createKey();//인증 코드 생성
        String setFrom= "ska5du1@naver.com";
        String toEmail = userEmail;
        String title = "[Won's Shop 인증메일 입니다.]";

        MimeMessage message = mailSender.createMimeMessage();
        message.addRecipients(MimeMessage.RecipientType.TO, toEmail);
        message.setSubject(title);
        message.setFrom(setFrom);
        message.setText(authNum, "utf-8", "html");

        mailSender.send(message);

        // key: userEmail, value : 인증번호, 유효시간 5분
        redisUtil.setDataExpire(userEmail, authNum, 60 * 5L);
        return authNum;
    }
}

