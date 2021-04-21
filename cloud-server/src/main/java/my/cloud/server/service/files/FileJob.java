package my.cloud.server.service.files;

import io.netty.channel.Channel;

import java.io.File;

public class FileJob {

    public final Channel userChannel;
    public final File file;

    public FileJob(Channel userChannel, File file) {
        this.userChannel = userChannel;
        this.file = file;
    }
}
