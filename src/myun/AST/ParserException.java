package myun.AST;

import myun.MyunException;

/**
 * Thrown when the parser finds an error.
 */
class ParserException extends MyunException {
    ParserException(String msg) {
        super(msg);
    }
}
