package pl.training.common.web;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

public class LocationUri {

    public static URI fromRequest(final String pathSegment) {
        var path = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .pathSegment(pathSegment)
                .build()
                .getPath();
        if (path == null) {
            throw new IllegalStateException();
        }
        return URI.create(path);
    }

}
