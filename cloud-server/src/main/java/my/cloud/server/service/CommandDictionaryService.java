package my.cloud.server.service;

import command.Command;
import io.netty.channel.ChannelHandlerContext;

public interface CommandDictionaryService {

    void processCommand(Command command, ChannelHandlerContext ctx);

}
