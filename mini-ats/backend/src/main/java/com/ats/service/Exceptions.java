package com.ats.service;

public final class Exceptions {
    private Exceptions() {}
    public static class NotFoundException extends RuntimeException {
        public NotFoundException(String m) { super(m); }
    }
    public static class ConflictException extends RuntimeException {
        public ConflictException(String m) { super(m); }
    }
    public static class InvalidTransitionException extends RuntimeException {
        public InvalidTransitionException(String m) { super(m); }
    }
}
