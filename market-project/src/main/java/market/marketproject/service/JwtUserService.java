package market.marketproject.service;

import market.marketproject.dto.JwtUser;
import market.marketproject.jwt.JwtTokenProvider;
import market.marketproject.mapper.JwtUserMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class JwtUserService{

    private final JwtUserMapper jwtUserMapper;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public JwtUserService(JwtUserMapper jwtUserMapper, BCryptPasswordEncoder bCryptPasswordEncoder, JwtTokenProvider jwtTokenProvider, JwtTokenProvider jwtTokenProvider1) {
        this.jwtUserMapper = jwtUserMapper;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtTokenProvider = jwtTokenProvider1;
    }

    /* 로그인 */
    public ResponseEntity<String> login(JwtUser jwtUser){
        JwtUser jwtUser1 = jwtUserMapper.validId(jwtUser.getId());

        /* 아이디 검증 */
        try {
            if (jwtUser1.getId().equals(jwtUser.getId())){
                /* 비밀번호 검증 */
                if(!bCryptPasswordEncoder.matches(jwtUser.getPassword(), jwtUser1.getPassword())){
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 비밀번호 입니다.");
                }
            }
        } catch (NullPointerException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("존재하지 않는 계정입니다.");
        }
        /* 토큰 발급 */
        String token = jwtTokenProvider.createToken(jwtUser.getId());
        return ResponseEntity.status(HttpStatus.OK).header("Authorization", token).body("로그인 성공");
    }

    /* 회원가입 */
    public ResponseEntity<String> register(JwtUser jwtUser){
        jwtUser.setPassword(bCryptPasswordEncoder.encode(jwtUser.getPassword()));
        jwtUserMapper.register(jwtUser);
        return ResponseEntity.status(HttpStatus.OK).body("OK");
    }

}
