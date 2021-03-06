package pw.cakemc.plugin.integration;

import com.palmergames.bukkit.TownyChat.Chat;
import com.palmergames.bukkit.TownyChat.channels.Channel;
import com.palmergames.bukkit.TownyChat.channels.channelTypes;
import com.palmergames.bukkit.TownyChat.events.AsyncChatHookEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import pw.cakemc.plugin.DiscordLink;
import pw.cakemc.plugin.events.DiscordRelayEvent;

import java.util.Base64;
import java.util.List;
import java.util.Map;

public class TownyChatIntegration implements Integration {

    private DiscordLink parent;
    private Chat townyChat;

    public TownyChatIntegration(DiscordLink parent) {
        townyChat = (Chat) parent.getServer().getPluginManager().getPlugin("TownyChat");
        this.parent = parent;
        this.parent.setHandledExternally(true);
        Map<String, Channel> channels = townyChat.getChannelsHandler().getAllChannels();

        for (Map.Entry<String, Channel> ch : channels.entrySet()) {
            Channel channel = ch.getValue();
            channel.setHooked(true);
        }

    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onRelay(DiscordRelayEvent ev) {
        if (ev.getType() != DiscordRelayEvent.EventType.CHAT) return;
        if (ev.getDestination() != DiscordRelayEvent.Destination.DISCORD) return;


        String[] parts = ev.getMessage().split(Character.toString('\0'));
        if (parts.length >= 3) {
            parent.getLogger().info("Relaying towny channel chatter.");
            ev.setMessage(parts[0]+parts[2]);

            String decoded = new String(Base64.getDecoder().decode(parts[1]));
            String[] info = decoded.split(":");
            parent.getLogger().info(info+"");
            if (info.length >= 2) {
                if (info[0].equalsIgnoreCase("townychat")) {
                    Channel channel = townyChat.getChannelsHandler().getChannel(info[1]);

                    String message = ev.getMessage();
                    message = message.replace("{channel}", channel.getName());
                    ev.setMessage(message);

                    if (ev.getChannel().getOptions() != null) {
                        if (ev.getChannel().getOptions().contains("towny")) {
                            List<String> relayedChannels = ev.getChannel().getOptions().getStringList("towny");
                            boolean relay = false;
                            parent.getLogger().info(relayedChannels.toString());
                            for (String relayed : relayedChannels) {
                                if (channel.getName().equalsIgnoreCase(relayed)) {
                                    relay = true;
                                }
                            }
                            if (!relay) ev.setCancelled(true);
                        } else {
                            // No channels setup ~ Nothing to relay.
                            ev.setCancelled(true);
                        }
                    } else {
                        // No channels setup ~ Nothing to relay.
                        ev.setCancelled(true);
                    }

                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent ev) {
        String[] parts = ev.getMessage().split(Character.toString('\0'));
        if (parts.length >= 3) {
            ev.setMessage(parts[0]+parts[2]);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onTownyChat(AsyncChatHookEvent ev) {
        String prefix = "TownyChat:" + ev.getChannel().getName();
        byte[] encodedBytes = Base64.getEncoder().encode(prefix.getBytes());
        prefix = Character.toString((char) 0) + new String(encodedBytes) + Character.toString((char) 0);

        ev.getAsyncPlayerChatEvent().setMessage(prefix + ev.getMessage());
        this.parent.handleChatEvent(ev.getAsyncPlayerChatEvent());
    }
}
