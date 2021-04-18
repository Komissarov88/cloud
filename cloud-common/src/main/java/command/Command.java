package command;

import java.io.Serializable;

public class Command implements Serializable {

    private String msg;
    public Command(String m) {
        msg = m;
    }

    public String getMsg() {
        return msg;
    }

    public Command setMsg(String msg) {
        this.msg = msg;
        return this;
    }
}
