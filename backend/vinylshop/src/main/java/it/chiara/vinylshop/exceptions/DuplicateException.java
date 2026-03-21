package it.chiara.vinylshop.exceptions;

public class DuplicateException extends RuntimeException {

    private static final long serialVersionUID = 285864158499701450L;

    public DuplicateException() { super(); }

    public DuplicateException(String messaggio) { super(messaggio); }
}