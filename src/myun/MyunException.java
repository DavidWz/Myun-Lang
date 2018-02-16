package myun;

/**
 * Abstract class for Myun exceptions.
 */
public abstract class MyunException extends RuntimeException {
    public MyunException() {
        super();
    }

    public MyunException(String msg) {
        super(msg);
    }
}
