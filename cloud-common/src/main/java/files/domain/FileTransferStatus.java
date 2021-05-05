package files.domain;

public enum FileTransferStatus {
    NOT_FOUND (-1),
    INTERRUPTED (-2),
    DONE (1);

    public final int value;

    FileTransferStatus(int value) {
        this.value = value;
    }
}
