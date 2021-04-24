package command.domain;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Main exchange dto between client and server
 */
public class Command implements Serializable {

    private CommandCode code;
    private String[] args;

    public Command(CommandCode code, String... args) {
        this.code = code;
        this.args = args;
    }

    public CommandCode getCode() {
        return code;
    }

    public Command setCode(CommandCode code) {
        this.code = code;
        return this;
    }

    public String[] getArgs() {
        return args;
    }

    public Command setArgs(String... args) {
        this.args = args;
        return this;
    }

    @Override
    public String toString() {
        return code.toString() + " : " + new String(Arrays.toString(args).getBytes(StandardCharsets.UTF_8));
    }
}
