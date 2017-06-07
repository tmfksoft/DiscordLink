package pw.cakemc.plugin.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import pw.cakemc.plugin.KnownChannel;
import pw.cakemc.plugin.RelayUser;

public class DiscordRelayEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;
    private EventType type;
    private RelayUser user;
    private String message;
    private KnownChannel channel;
    private Destination destination;

    public enum EventType {
        CHAT, KICK, QUIT, JOIN
    }
    public enum Destination {
        DISCORD, MINECRAFT
    }

    public DiscordRelayEvent(EventType type, Destination destination, RelayUser user, KnownChannel channel, String message) {
        this.type = type;
        this.destination = destination;
        this.user = user;
        this.message = message;
        this.channel = channel;
    }

    public RelayUser getUser() {
        return user;
    }

    public String getMessage() {
        return message;
    }

    public KnownChannel getChannel() {
        return channel;
    }

    public Destination getDestination() {
        return this.destination;
    }

    public EventType getType() {
        return this.type;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }
}
