package pw.cakemc.plugin.integration;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.RegisteredServiceProvider;
import pw.cakemc.plugin.DiscordLink;
import pw.cakemc.plugin.events.DiscordRelayEvent;

/**
 * Created by Thomas on 07/06/2017.
 */
public class VaultIntegration implements Integration {

    private DiscordLink parent;

    private Economy eco = null;
    private Permission perms = null;

    public VaultIntegration(DiscordLink parent) {
        this.parent = parent;
        setupPermissions();
        setupEconomy();
    }

    @EventHandler
    public void onRelay(DiscordRelayEvent ev) {
        if (!ev.getType().equals(DiscordRelayEvent.EventType.CHAT)) return;
        if (!ev.getDestination().equals(DiscordRelayEvent.Destination.DISCORD)) return;

        Player player = parent.getServer().getPlayer(ev.getUser().getUsername());
        String format = ev.getMessage();

        if (perms != null && perms.hasGroupSupport()) {
            format = format.replace("{group.name}", perms.getPrimaryGroup(player));
        } else {
            format = format.replace("{group.name}", "");
        }

        if (eco != null && eco.hasAccount(player)) {
            format = format.replace("{balance.value}",Double.toString(eco.getBalance(player)));
            format = format.replace("{balance.provider}", eco.getName());
        } else {
            format = format.replace("{balance.value}", "");
            format = format.replace("{balance.provider}", "");
        }

        ev.setMessage(format);

    }

    private boolean setupEconomy() {
        if (parent.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = parent.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        eco = rsp.getProvider();
        return eco != null;
    }
    private boolean setupPermissions() {
        if (parent.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Permission> rsp = parent.getServer().getServicesManager().getRegistration(Permission.class);
        if (rsp == null) {
            return false;
        }
        perms = rsp.getProvider();
        return perms != null;
    }
}
