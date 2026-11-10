package tcc.api.secureapi.exception;

public class EmailAlreadyUsedException extends RuntimeException {
    public EmailAlreadyUsedException() { super("e-mail já utilizado"); }
}
