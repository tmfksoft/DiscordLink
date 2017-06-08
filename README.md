# DiscordLink
*Relays messages between Discord and Minecraft*

Welcome to the source for DiscordLink.
PRs and Issues are welcome.
However, please do not fork DiscordLink without permission.
You are welcome to build from source and use it on your server.

However forking with the intent to redistribute under a different name is STRICTLY NOT ALLOWED.
That is classed as stealing please do not do it.

## Compiling
Compilation is pretty easy. You will need to have maven installed.
When maven is installed run `mvn package` in the root directory of the plugin.

## Future Features
There are some features planned for future releases, as a result these features a present in this repository.
Planned features include:

 - Support for GroupManager (Prefixes & Suffixes)
 - Support for Essentials Muting

**_Don't see a feature in there you'd like?_**

DiscordLink provides its own event named `DiscordRelayEvent` you could write a plugin to listen to this event and modify the messages.
That's how DiscordLink currently does it for its features.
Alternatively you can create a PR to add it yourself or create an Issue requesting a feature.

## Current Features
DiscordLink already has support for the following plugins and features.

 - Support for Towny in the form of variables in chat for town names and so on.
 - Support for TownyChat, ignores messages directed to nonpublic channels such as Town, Nation and Admin channels.
 - Support for Permission Groups to show the players permission group in chat.
 - Support for Economy plugins to show the players balance in chat. (For w.e reason)

## Branching
DiscordLink's repo uses multiple branches.
They are as follows:

 - Master: Latest live release. This repo is only updated when a new release is ready.
 - Development: Latest version of development code. Future versions and their code will be found here. When ready Development will be merged into Master.