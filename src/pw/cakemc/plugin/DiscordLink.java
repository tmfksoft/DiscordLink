package pw.cakemc.plugin;

import com.google.common.util.concurrent.FutureCallback;
import com.google.gson.Gson;
import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.Javacord;
import de.btobastian.javacord.entities.Channel;
import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.listener.message.MessageCreateListener;
import jdk.nashorn.internal.runtime.regexp.joni.Regex;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import pw.cakemc.plugin.http.Request;
import pw.cakemc.plugin.http.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DiscordLink extends JavaPlugin implements Listener {

    private String botToken = null;
    private DiscordAPI api;
    private List<KnownGuild> guilds = new ArrayList<KnownGuild>();
    private ExecutorService executor = Executors.newFixedThreadPool(1);

    // Default Formats.
    private String sendFormat = "<{player.display}> {message}";
    private String recieveFormat = "[{server.name}] <{user.name}> {message}";
    private String playerFormat = "{player.name}";
    private String avatarFormat = "http://cravatar.eu/helmavatar/{player.uuid}/256.png";

    // Le Debug!
    private boolean debug = false;

    // Cringe?
    private List<Player> OnlinePlayers = new ArrayList<Player>();

    public void onEnable() {
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(this,this);

        if (getConfig().contains("guilds")) {
            for (String sid : getConfig().getConfigurationSection("guilds").getKeys(false)) {
                KnownGuild guild = new KnownGuild(sid);
                guilds.add(guild);
                ConfigurationSection guildSection = getConfig().getConfigurationSection("guilds").getConfigurationSection(sid);

                for (String ch : guildSection.getConfigurationSection("channels").getKeys(false)) {
                    KnownChannel channel = new KnownChannel(ch, guild);
                    guild.addChannel(channel);

                    ConfigurationSection channelSection = guildSection.getConfigurationSection("channels").getConfigurationSection(ch);
                    if (channelSection.contains("webhook")) {
                        Request req = new Request(channelSection.getString("webhook"));
                        Future<Response> response = executor.submit(req);
                        String body = null;
                        try {
                            body = response.get().getBody();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                        if (debug) getLogger().info(body);
                        Gson gson = new Gson();

                        HashMap<String, String> webhook = gson.fromJson(body, HashMap.class);

                        WebHook hook = new WebHook(webhook.get("id"), channelSection.getString("webhook"));
                        if (webhook.containsKey("code")) {
                            getLogger().warning("Error handling webhook: "+webhook.get("message"));
                        } else {
                            hook.setGuild(guild);

                            hook.setAvatar(webhook.get("avatar"));
                            hook.setName(webhook.get("name"));
                            hook.setToken(webhook.get("token"));
                            hook.setChannel(channel);

                            channel.setWebhook(hook);
                        }

                    }
                    if (channelSection.contains("send")) channel.setSend(channelSection.getBoolean("send"));
                    if (channelSection.contains("recieve")) channel.setRecieve(channelSection.getBoolean("recieve"));

                }
            }
        }

        // Formats
        if (getConfig().contains("format")) {
            sendFormat = getConfig().getConfigurationSection("format").getString("send");
            recieveFormat = getConfig().getConfigurationSection("format").getString("recieve");
            avatarFormat = getConfig().getConfigurationSection("format").getString("avatar");
            playerFormat = getConfig().getConfigurationSection("format").getString("player");
        }
        if (getConfig().contains("token")) {
            botToken = getConfig().getString("token");
            if (botToken.trim().equalsIgnoreCase("")) return;

            api = Javacord.getApi(botToken, true);
            api.connect(new FutureCallback<DiscordAPI>() {

                @Override
                public void onSuccess(DiscordAPI api) {
                    if (getConfig().contains("serverAddress")) {
                        api.setGame(getConfig().getString("serverAddress"));
                    }

                    // register listener
                    api.registerListener(new MessageCreateListener() {
                        @Override
                        public void onMessageCreate(DiscordAPI api, Message message) {

                            // Ignore own messages.
                            if (message.getAuthor().getId() == api.getYourself().getId()) return;

                            KnownGuild guild = getGuild(message.getChannelReceiver().getServer().getId());
                            if (guild != null) {
                                KnownChannel channel = guild.getChannel(message.getChannelReceiver().getId());
                                if (channel != null) {
                                    // Ignore our own webhooks.
                                    if (channel.getWebhook() != null) {
                                        if (channel.getWebhook().getId().equalsIgnoreCase(message.getAuthor().getId()))
                                            return;
                                    }
                                    if (channel.canRecieve()) {
                                        // Only log messages in channels we care about
                                        getLogger().info("[Discord] "+message.getAuthor().getName() + ": " + message.getContent());

                                        // Format the message!
                                        String format = recieveFormat;

                                        /*
                                        # - {user.id} - The Discord users ID
                                        # - {user.name} - The Discord users username
                                        # - {user.nick} - The Discord users nickname (If they lack one it will default to their username}
                                        # - {server.id} - The ID of the Discord Server the message originated.
                                        # - {server.name} - The name of the Discord Server the message originated.
                                        # - {channel.id} - The ID of the Discord Channel the message originated.
                                        # - {channel.name} - The name of the Discord Channel the message originated.
                                        # - {message} - The contents of the message the user sent. (Image messages will be ignored)
                                         */

                                        format = format.replace("{user.id}", message.getAuthor().getId());
                                        format = format.replace("{user.name}", message.getAuthor().getName());
                                        String nick = message.getAuthor().getNickname(message.getChannelReceiver().getServer());
                                        if (nick == null) nick = message.getAuthor().getName();
                                        format = format.replace("{user.nick}", nick);

                                        format = format.replace("{server.id}", message.getChannelReceiver().getServer().getId());
                                        format = format.replace("{server.name}", message.getChannelReceiver().getServer().getName());

                                        format = format.replace("{channel.id}", message.getChannelReceiver().getId());
                                        format = format.replace("{channel.name}", message.getChannelReceiver().getName());

                                        format = ChatColor.translateAlternateColorCodes('&', format);

                                        format = format.replace("{message}", message.getContent());

                                        for (Player player : getServer().getOnlinePlayers()) {
                                            player.sendMessage(format);
                                        }
                                    }
                                }
                            }

                        }
                    });
                }

                @Override
                public void onFailure(Throwable t) {
                    t.printStackTrace();
                }
            });
        }
    }

    public void onDisable() {
        // Plugin disabled.
        executor.shutdown();
        if (api != null) api.disconnect();
    }

    public KnownGuild getGuild(String id) {
        for (KnownGuild guild : guilds) {
            if (guild.getId().equalsIgnoreCase(id)) {
                return guild;
            }
        }
        return null;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent ev) {
        OnlinePlayers.add(ev.getPlayer());
        for (KnownGuild guild : guilds) {
            for (KnownChannel channel : guild.getChannels()) {
                if (channel.canSend() && api != null) {
                    for (Channel ch : api.getChannels()) {
                        if (ch.getId().equalsIgnoreCase(channel.getName())) {
                            ch.sendMessage(ev.getPlayer().getName()+" joined the game");
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent ev) {
        if (!OnlinePlayers.contains(ev.getPlayer())) return;
        OnlinePlayers.remove(ev.getPlayer());
        for (KnownGuild guild : guilds) {
            for (KnownChannel channel : guild.getChannels()) {
                if (channel.canSend() && api != null) {
                    for (Channel ch : api.getChannels()) {
                        if (ch.getId().equalsIgnoreCase(channel.getName())) {
                            ch.sendMessage(ChatColor.stripColor(ev.getQuitMessage()));
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onKick(PlayerKickEvent ev) {
        OnlinePlayers.remove(ev.getPlayer());

        getLogger().info(ev.getLeaveMessage());

        for (KnownGuild guild : guilds) {
            for (KnownChannel channel : guild.getChannels()) {
                if (channel.canSend() && api != null) {
                    for (Channel ch : api.getChannels()) {
                        if (ch.getId().equalsIgnoreCase(channel.getName())) {
                            if (ev.getReason() != null) {
                                ch.sendMessage(ev.getPlayer().getName() + " was kicked from the game: " + ev.getReason());
                            } else {
                                ch.sendMessage(ev.getPlayer().getName() + " was kick from the game");
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent ev) {

        for (KnownGuild guild : guilds) {
            for (KnownChannel channel : guild.getChannels()) {
                if (channel.canSend() && api != null) {
                    for (Channel ch : api.getChannels()) {
                        if (ch.getId().equalsIgnoreCase(channel.getName())) {
                            if (channel.getWebhook() == null) {
                                ch.sendMessage(minecraftFormat(ev.getPlayer(), ev.getMessage(), sendFormat));
                            } else {
                                dispatchChat(ev.getPlayer(), ev.getMessage(), channel);
                            }
                        }
                    }
                } else {
                    if (channel.getWebhook() != null) {
                        dispatchChat(ev.getPlayer(), ev.getMessage(), channel);
                    }
                }
            }
        }
    }

    public void dispatchChat(Player player, String message, KnownChannel channel) {
        HashMap<String, String> args = new HashMap<String, String>();

        args.put("content", message);
        args.put("username", minecraftFormat(player, message, playerFormat));

        String avatar = minecraftFormat(player, message, avatarFormat);
        if (avatar != "") {
            args.put("avatar_url", avatar);
        }

        Request req = new Request(channel.getWebhook().getUrl(), args);
        req.setMethod("POST");
        Future<Response> response = executor.submit(req);
        try {
            String body = response.get().getBody();
            if (debug) getLogger().info(body);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Formatter
    public String minecraftFormat(Player player, String message, String format) {
        /*
        # - {player.name} - The Players username.
        # - {player.display} - The Players display name (Nickname)
        # - {player.uuid} - The players UUID
        # - {world} - The world the player is currently in.
        # // Towny Variables - If you use Towny //
        # - {town} - The town the player is in
        # - {nation} - The nation the player is in
        # // Permissions Variables - If you use a Vault supported permissions plugin //
        # - {group.name} - The name of the primary group the player is in.
        # // Economy Variables - If you use a Vault supported Economy plugin //
        # - {balance.value} - The players balance
         */

        format = format.replace("{player.name}", player.getName());
        format = format.replace("{player.display}", player.getDisplayName());
        format = format.replace("{player.uuid}", player.getUniqueId().toString());
        format = format.replace("{world}", player.getWorld().getName());
        format = format.replace("{message}", message);

        return format;
    }
}
