package market.marketproject.config;

import lombok.AllArgsConstructor;
import market.marketproject.jwt.JwtAuthenticationFilter;
import market.marketproject.jwt.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@AllArgsConstructor
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final JwtTokenProvider jwtTokenProvider;

    /* authenticationMapper 를 Bean 등록한다. */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception{
        http.csrf().disable();
        // http.httpBasic().disable();
        // 일반적인 루트가 아닌 다른 방식으로 요청시 거절, header 에 id, pw 가 아닌 token(jwt)를 달고 간다.
        // 그래서 basic 이 아닌 bearer 를 사용한다.
        http.httpBasic().disable()
                .authorizeRequests() // 요청에 대한 사용권한 체크
                .antMatchers("/api/jwtLogin").permitAll()
                .antMatchers("/**").authenticated()
                .and()
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);
                // JwtAuthenticationFilter 를 UsernamePasswordAuthenticationFilter 전에 넣는다.
        // + 토큰에 저장된 유저정보를 활용하여야 하기 때문에 CustomUserDetailService 클래스르르 생성한다.
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }
}
