package my.cloud.server.service.command.commands;

import my.cloud.server.service.command.CommandService;

import java.io.File;

public class ViewFilesInDirCommand implements CommandService {

    @Override
    public String processCommand(String command) {
        final int requirementCountCommandParts = 2;

        String[] actualCommandParts = command.split("\\s");
        if (actualCommandParts.length != requirementCountCommandParts) {
            return "Command \"" + getCommand() + "\" is not correct";
        }

        return process(actualCommandParts[1]);
    }

    private String process(String dirPath) {
        File directory = new File(dirPath);

        if (!directory.exists()) {
            return "Directory is not exists";
        }

        if (directory.isFile()) {
            return dirPath;
        }

        StringBuilder builder = new StringBuilder();
        for (File childFile : directory.listFiles()) {
            String typeFile = getTypeFile(childFile);
            builder.append(childFile.getName()).append(" | ").append(typeFile).append(System.lineSeparator());
        }

        return builder.toString();
    }

    private String getTypeFile(File childFile) {
        return childFile.isDirectory() ? "DIR" : "FILE";
    }

    @Override
    public String getCommand() {
        return "ls";
    }

}
