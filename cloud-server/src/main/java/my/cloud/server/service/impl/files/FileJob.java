package my.cloud.server.service.impl.files;

import io.netty.channel.Channel;

import java.io.File;

/**
 * Holds client channel and file of interest (upload or download)
 */
public class FileJob {

    public final Channel userChannel;
    public final File file;

    public FileJob(Channel userChannel, File file) {
        this.userChannel = userChannel;
        this.file = file;
    }
}
