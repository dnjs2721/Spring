package market.marketproject.mapper;

import io.lettuce.core.dynamic.annotation.Param;
import market.marketproject.dto.JwtUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface JwtUserMapper {
    JwtUser findUser(@Param("id") String id);

    /* 아이디 검증 */
    /* 아이디가 존재 할때 아이디와 일치하는 모든 데이터를 가져온다. */
    JwtUser validId(@Param("id") String id);

    /* 회원가입 */
    void register(JwtUser jwtUser);
}
