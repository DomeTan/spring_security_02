package cn.gotham.spring_security_02.common.bean;

/**
 *  SpringSecurity服务执行异常返会model
 * @author tanchong
 * Create Date: 2020/3/15
 */
public class ErrorResponse {

    private String error;

    public ErrorResponse(String message) {

    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "ErrorResponse{" +
                "error='" + error + '\'' +
                '}';
    }
}
