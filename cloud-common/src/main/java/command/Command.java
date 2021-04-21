package command;

import java.io.Serializable;
import java.util.Arrays;

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
        StringBuilder sb = new StringBuilder();
        sb.append(code.toString()).append(" : ");
        sb.append(Arrays.toString(args)).append(System.lineSeparator());
        return sb.toString();
    }
}
