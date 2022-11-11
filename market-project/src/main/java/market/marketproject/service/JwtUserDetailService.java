package market.marketproject.service;

import market.marketproject.dto.JwtUser;
import market.marketproject.mapper.JwtUserMapper;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class JwtUserDetailService implements UserDetailsService {

    private final JwtUserMapper jwtUserMapper;

    public JwtUserDetailService(JwtUserMapper jwtUserMapper) {
        this.jwtUserMapper = jwtUserMapper;
    }

    public JwtUser loadUserByUsername(String id){
        return jwtUserMapper.findUser(id);
    }
}
