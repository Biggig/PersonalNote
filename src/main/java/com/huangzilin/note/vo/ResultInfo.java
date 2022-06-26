package com.huangzilin.note.vo;

/*
*封装返回结果的类
*   状态码 成功=1，失败=0
*   提示信息
*   返回的对象
 */
public class ResultInfo<T> {
    private Integer code;
    private String message;
    private T result;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}
