package ru.netology.exception;

public class UnsupportedMethodException extends Exception {
    public UnsupportedMethodException() {
        super("Method for this path not supported");
    }
}
