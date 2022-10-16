package market.marketproject.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Date;

@Data
public class User {

    private String userUuid;

    private String userName;

    private String userEmail;

    private String userPassword;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date userBirth;

    private String mailKey;

    private int mailAuth;

    private String userTel;
}
