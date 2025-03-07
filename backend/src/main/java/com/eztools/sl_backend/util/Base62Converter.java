package com.eztools.sl_backend.util;

/**
 * Base62编码转换器
 * @author <Rezven>
 * 用于将Snowflake生成的long型ID转换为短字符串
 */
public class Base62Converter {
    private static final String BASE62_CHARS =
            "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final char[] CHAR_SET = BASE62_CHARS.toCharArray();
    
    /**
     * 将long型数字转换为Base62字符串
     * @param number 输入数字（需为正整数）
     * @return 6-8位长度的字符串
     */
    public static String encode(long number) {
        if (number < 0) throw new IllegalArgumentException("输入必须为正整数");
        
        StringBuilder code = new StringBuilder();
        while (number > 0) {
            int remainder = (int) (number % 62);
            code.append(CHAR_SET[remainder]);
            number /= 62;
        }
        return code.reverse().toString();
    }
}