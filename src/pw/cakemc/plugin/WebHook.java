package pw.cakemc.plugin;

public class WebHook {
    private String name;
    private KnownChannel channel;
    private String token;
    private String avatar;
    private KnownGuild guild;
    private String id;
    private String url;

    public WebHook(String id, String url) {
        this.id = id;
        this.url = url;
    }

    // Getters
    public String getId() {
        return this.id;
    }
    public String getName() {
        return this.name;
    }
    public KnownChannel getChannel() {
        return this.channel;
    }
    public String getToken() {
        return this.token;
    }
    public String getAvatar() {
        return this.avatar;
    }
    public KnownGuild getGuild() {
        return this.guild;
    }
    public String getUrl(){
        return this.url;
    }

    // Setters
    public void setName(String name){
        this.name = name;
    }
    public void setChannel(KnownChannel channel) {
        this.channel = channel;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    public void setGuild(KnownGuild guild) {
        this.guild = guild;
    }

    // Misc
}
