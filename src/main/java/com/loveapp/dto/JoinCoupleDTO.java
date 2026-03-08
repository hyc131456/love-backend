package com.loveapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 加入空间请求DTO
 */
@Data
public class JoinCoupleDTO {
    
    /** 邀请码 */
    @NotBlank(message = "邀请码不能为空")
    @Size(min = 6, max = 6, message = "邀请码格式错误")
    private String inviteCode;
}
