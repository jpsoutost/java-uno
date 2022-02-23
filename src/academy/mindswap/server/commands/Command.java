package academy.mindswap.server.commands;

import java.util.concurrent.Executor;

/**
 * A ENUM that sets the game commands.
 */
public enum Command {
    LIST("/list", new ListHandler()),
    HELP("/help", new HelpHandler()),
    WHISPER("/whisper", new WhisperHandler()),
    QUIT("/quit", new QuitHandler());
    //SHOUT("/shout", new ShoutHandler());

    private String description;
    private CommandHandler handler;

    /**
     * Method that initializes the ENUM with two parameters.
     * @param description The name of the command.
     * @param handler The class executed by the respective command.
     */
    Command(String description, CommandHandler handler) {
        this.description = description;
        this.handler = handler;
    }

    /**
     * Method that get command from description.
     * @param description The name of the command.
     * @return Respective command.
     */
    public static Command getCommandFromDescription(String description) {
        for (Command command : values()) {
            if (description.equals(command.description)) {
                return command;
            }
        }
        return null;
    }

    public CommandHandler getHandler() {
        return handler;
    }
}
