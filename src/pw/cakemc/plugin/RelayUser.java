package pw.cakemc.plugin;

/**
 * A throw away class used to refer either to a Discord user or a Minecraft user.
 * ID is always unique to the platform the user belongs to.
 */
public class RelayUser {

    private String id;
    private String username;
    private String display;
    private UserType type;

    public enum UserType {
        DISCORD, MINECRAFT
    }

    public RelayUser(String id, String username, String display, UserType type) {
        this.id = id;
        this.username = username;
        this.display = display;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getDisplay() {
        return display;
    }

    public UserType getType() {
        return type;
    }
}
