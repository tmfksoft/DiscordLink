package pw.cakemc.plugin;

import java.util.ArrayList;
import java.util.List;


public class KnownGuild {
    private String id;
    private List<KnownChannel> channels = new ArrayList<KnownChannel>();

    public KnownGuild(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public List<KnownChannel> getChannels() {
        return this.channels;
    }
    public KnownChannel getChannel(String name) {
        for (KnownChannel channel : this.channels) {
            if (channel.getName().equalsIgnoreCase(name)) {
                return channel;
            }
        }
        return null;
    }
    public void addChannel(KnownChannel channel) {
        this.channels.add(channel);
    }
    public void removeChannel(KnownChannel channel) {
        if (channels.contains(channel)) {
            channels.remove(channel);
        }
    }
}
