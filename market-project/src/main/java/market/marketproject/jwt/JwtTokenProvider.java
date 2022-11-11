package market.marketproject.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import market.marketproject.service.JwtUserDetailService;
import market.marketproject.service.JwtUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;

/* 토큰을 생성하고 검증하는 클래스 */
/* 해당 컴포넌트는 필터클래스에서 사전 검증을 거친다. */
@RequiredArgsConstructor
@Component
public class JwtTokenProvider {

    private JwtUserDetailService jwtUserDetailService;
    private String secretKey = "myProjectSecret";
    /* 토큰 유효시간 30분 */
    private long tokenValidTime = 30 * 60 * 100L;

    @Autowired
    public JwtTokenProvider(JwtUserDetailService jwtUserDetailService) {
        this.jwtUserDetailService = jwtUserDetailService;
    }

    /* 객체 초기화, secretKey 를 Base64로 인코딩*/
    @PostConstruct
    protected void init(){
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    /* JWT 토큰 생성 */
    public String createToken(String id){
        Claims claims = Jwts.claims().setSubject(id); // JWT payload 에 저장되는 정보단위, 보통 여기서 user를 식별하는 값을 넣는다.
        Date now = new Date();
        return  Jwts.builder()
                .setClaims(claims) // 정보 저장
                .setIssuedAt(now) // 토큰 발행 시간 정보
                .setExpiration(new Date(now.getTime() + tokenValidTime)) // set Expire Time
                .signWith(SignatureAlgorithm.HS256, secretKey) // 사용할 암호화 알고리즘
                // signature 에 들어갈 secret 값 세팅
                .compact();
    }

    /* JWT 토큰에서 인증 정보 조히 */
    public Authentication getAuthentication(String token) {
        UserDetails user = jwtUserDetailService.loadUserByUsername(this.getUserIdToUserInfo(token));
        return new UsernamePasswordAuthenticationToken(user, "", null);
    }

    /* 토큰에서 회원 정보 추출 */
    public String getUserIdToUserInfo(String token){
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJwt(token).getBody().getSubject();
    }

    /* Request 의 Header 에서 token 값을 가져온다. "Authorization" : "TOKEN 값" */
    public String resolveToken(HttpServletRequest request){
        return request.getHeader("Authorization");
    }

    /* 토큰의 유효성 + 만료일자 확인 */
    public boolean validateToken(String jwtToken){
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e){
            return false;
        }
    }
}
