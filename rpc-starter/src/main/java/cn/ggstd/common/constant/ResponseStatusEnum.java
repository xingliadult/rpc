package cn.ggstd.common.constant;

/**
 * Created by lixing on 2021-2-25 下午 6:01.
 */
public enum ResponseStatusEnum {

    SUCCESS(200,"success"),NOT_FOUND(404,"not found"),SYSTEM_ERROR(500,"system error");

    private Integer code;
    private String message;

    ResponseStatusEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
