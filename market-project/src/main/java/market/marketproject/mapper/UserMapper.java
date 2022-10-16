package market.marketproject.mapper;

import market.marketproject.dto.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
    void registerUser(User user);
    Integer checkNameExists(User user);
    Integer checkEmailExists(User user);
    Integer checkEmailPasswordExists(User user);
    List<User> getUserByUserEmail(User user);
    String findEmail(User user);
    String findPassword(User user);
    void updateMailAuth(User user);
    Integer emailAuthFail(User user);
}
