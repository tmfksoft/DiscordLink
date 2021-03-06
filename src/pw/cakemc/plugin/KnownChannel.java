package pw.cakemc.plugin;


import org.bukkit.configuration.ConfigurationSection;

public class KnownChannel {
    private WebHook webhook = null;
    private String name = "";
    private KnownGuild guild;
    private boolean send = false;
    private boolean recieve = false;
    private ConfigurationSection options;

    public KnownChannel(String name, KnownGuild guild, ConfigurationSection opts) {
        this.name = name;
        this.guild = guild;
        this.options = opts;
    }

    // Getters
    public KnownGuild getGuild() {
        return this.guild;
    }
    public String getName() {
        return this.name;
    }
    public WebHook getWebhook() {
        return this.webhook;
    }
    public boolean canSend() {
        return this.send;
    }
    public boolean canRecieve() {
        return this.recieve;
    }
    public ConfigurationSection getOptions() {
        return this.options;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }
    public void setWebhook(WebHook webhook) {
        this.webhook = webhook;
    }
    public void setSend(boolean send) {
        this.send = send;
    }
    public void setRecieve(boolean recieve) {
        this.recieve = recieve;
    }
}
