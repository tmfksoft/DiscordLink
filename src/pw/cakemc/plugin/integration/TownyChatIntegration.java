package pw.cakemc.plugin.integration;

import com.palmergames.bukkit.TownyChat.events.AsyncChatHookEvent;
import org.bukkit.event.EventHandler;
import pw.cakemc.plugin.DiscordLink;
import pw.cakemc.plugin.events.DiscordRelayEvent;

public class TownyChatIntegration implements Integration {

    private DiscordLink parent;

    public TownyChatIntegration(DiscordLink parent) {
        this.parent = parent;
    }

    @EventHandler
    public void onRelay(DiscordRelayEvent ev) {
        if (ev.getType() != DiscordRelayEvent.EventType.CHAT) return;
        if (ev.getDestination() != DiscordRelayEvent.Destination.DISCORD) return;
        //parent.getLogger().info("[TCHAT] Relay Event "+ev.getMessage());
        // ev.setCancelled(true);
    }

    @EventHandler
    public void onTownyChat(AsyncChatHookEvent ev) {
        //parent.getLogger().info("TownyChat: "+ev.getChannel().getName()+": "+ev.getMessage());
    }
}
