package pw.cakemc.plugin.integration;

import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import org.bukkit.event.EventHandler;
import pw.cakemc.plugin.DiscordLink;
import pw.cakemc.plugin.events.DiscordRelayEvent;

public class TownyIntegration implements Integration {

    private DiscordLink parent;

    public TownyIntegration(DiscordLink parent) {
        this.parent = parent;
    }

    @EventHandler
    public void relayChat(DiscordRelayEvent ev) {
        if (!ev.getType().equals(DiscordRelayEvent.EventType.CHAT)) return;
        if (!ev.getDestination().equals(DiscordRelayEvent.Destination.DISCORD)) return;
        if (ev.isCancelled()) return;

        Resident resident;
        String format = ev.getMessage();
        try {
            resident = TownyUniverse.getDataSource().getResident(ev.getUser().getUsername());

            // Are we in a town?
            if (resident.hasTown()) {
                format = format.replace("{town}", resident.getTown().getName());

                // Are we in a nation?
                if (resident.getTown().hasNation()) {
                    format = format.replace("{nation}", resident.getTown().getNation().getName());
                } else {
                    format = format.replace("{nation}", "");
                }
            } else {
                format = format.replace("{town}", "");
                format = format.replace("{nation}", "");
            }

            // Does the resident have a title?
            if (resident.hasTitle()) {
                format = format.replace("{title}", resident.getTitle());
            } else {
                format = format.replace("{title}", "");
            }

        } catch (NotRegisteredException e) {
            e.printStackTrace();
        }
        ev.setMessage(format);
    }
}
