package com.eztools.sl_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * @author <Rezven>
 * 短链接生成请求参数
 * @param url 需要缩短的原始URL
 */
public record ShortenRequest(
        @NotBlank(message = "URL不能为空")
        @Pattern(regexp = "^(https?|ftp)://[^\\s/$.?#].[^\\s]*$",
                message = "无效的URL格式")
        String url
) {}