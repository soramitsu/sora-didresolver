package jp.co.soramitsu.sora.crypto.hash;

public interface RawDigestStrategy {

    /**
     * Calculate digest (raw bytes)
     *
     * @param input raw bytes of payload, to be hashed
     * @return raw bytes of hash. Length depends on the specific implementation.
     */
    byte[] digest(byte[] input);
}
