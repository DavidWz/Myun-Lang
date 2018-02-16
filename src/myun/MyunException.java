package myun;

/**
 * Abstract class for Myun exceptions.
 */
public abstract class MyunException extends RuntimeException {
    protected MyunException() {
    }

    protected MyunException(String msg) {
        super(msg);
    }
}
