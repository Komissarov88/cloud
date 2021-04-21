package my.cloud.server.service.files;

import utils.Hash;

import java.nio.file.Path;

public class FileJob {

    private final String hash;
    private final Path path;
    private final String login;

    public FileJob(Path path, String login) {
        if (path == null || login == null) {
            throw new IllegalArgumentException("fields must be not null");
        }
        this.path = path;
        this.login = login;
        hash = Hash.get(login + path);
    }

    public String getAuthenticateKey() {
        return hash;
    }

    public Path getPath() {
        return path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FileJob fileJob = (FileJob) o;

        if (!path.equals(fileJob.path)) return false;
        return login.equals(fileJob.login);
    }

    public boolean equals(Path path, String login) {
        return path.equals(this.path) && login.equals(this.login);
    }

    @Override
    public int hashCode() {
        int result = path.hashCode();
        result = 31 * result + login.hashCode();
        return result;
    }
}
