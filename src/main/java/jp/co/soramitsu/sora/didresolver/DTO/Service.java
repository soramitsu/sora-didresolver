package jp.co.soramitsu.sora.didresolver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.net.URI;

@Data
@AllArgsConstructor
@NoArgsConstructor
class Service {

    @NotBlank
    private String type;

    @NotNull
    private URI serviceEndpoint;
}
