package com.web.dto.request;

import com.web.entity.Authority;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class UserRequest {

    @NotNull(message = "Email không được bỏ trống")
    private String email;

    private String password;

    private String fullname;

    private String phone;

    private String tokenFcm;

    private Authority authorities;
}
