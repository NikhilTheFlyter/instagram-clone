package com.instagram.follow.exception;

public class SelfFollowException extends RuntimeException {

    public SelfFollowException(String message) {
        super(message);
    }
}
