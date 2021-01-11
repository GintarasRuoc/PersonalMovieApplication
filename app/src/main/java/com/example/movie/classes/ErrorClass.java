package com.example.movie.classes;

import com.google.gson.annotations.SerializedName;

public class ErrorClass {

    @SerializedName("code")
    private String codeError;
    @SerializedName("message")
    private String messageError;

    public ErrorClass(String codeError, String messageError) {
        this.codeError = codeError;
        this.messageError = messageError;
    }

    public String getCodeError() {
        return codeError;
    }

    public void setCodeError(String codeError) {
        this.codeError = codeError;
    }

    public String getMessageError() {
        return messageError;
    }

    public void setMessageError(String messageError) {
        this.messageError = messageError;
    }
}
