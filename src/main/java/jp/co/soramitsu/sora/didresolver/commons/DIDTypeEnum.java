package jp.co.soramitsu.sora.didresolver.commons;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
public enum DIDTypeEnum {
    IROHA("\\w*@\\w*\\..*"),
    UUID("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"),
    ED("\\w*");

    @Getter
    private String regexp;

}
