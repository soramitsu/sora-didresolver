package jp.co.soramitsu.sora.didresolver.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author rogachevsn
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class DIDDuplicateException extends RuntimeException {

    public DIDDuplicateException(String did) {
        super("DID " + did + " is already registered");
    }
}
