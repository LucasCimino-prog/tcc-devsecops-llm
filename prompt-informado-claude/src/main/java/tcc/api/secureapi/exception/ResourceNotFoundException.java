package tcc.api.secureapi.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException() { super("recurso não encontrado"); }
}
