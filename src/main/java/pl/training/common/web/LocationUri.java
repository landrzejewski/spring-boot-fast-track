package pl.training.common.web;

import java.net.URI;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequestUri;

public final class LocationUri {

    public static URI fromRequest(final String pathSegment) {
        var path = fromCurrentRequestUri().pathSegment(pathSegment).build().getPath();
        if (path == null) {
            throw new IllegalStateException();
        }
        return URI.create(path);
    }

}
