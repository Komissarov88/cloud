package files.domain;

import io.netty.channel.Channel;

import java.nio.file.Path;

public class Transfer {

    public final Channel channel;
    public final Path destination;
    public final Path origin;

    public Transfer(Path origin, Path destination, Channel channel) {
        this.channel = channel;
        this.destination = destination;
        this.origin = origin;
    }
}
