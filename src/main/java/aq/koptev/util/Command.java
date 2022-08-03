package aq.koptev.util;

public enum Command {

    REGISTRATION("#registration"),
    AUTHENTICATION("#authentication");

    private String command;

    Command(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    public static Command getCommandByValue(String command) {
        for(Command c : Command.values()) {
            if(c.getCommand().equals(command)) {
                return c;
            }
        }
        return null;
    }
}
