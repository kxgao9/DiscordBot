package li.jeffrey;

import java.util.List;

import li.jeffrey.events.*;
import li.jeffrey.events.games.*;
import li.jeffrey.events.games.paranoia.*;
import li.jeffrey.events.mod.*;
import li.jeffrey.events.music.*;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

/**
 * A Discord Bot that includes moderator, music, and other miscellaneous commands
 *
 * @author Jeffrey Li
 */
public class Bot extends ListenerAdapter {

    private static String prefix = "!";
    private static String myID = ""; // User ID of server owner
    private static String botToken = ""; // Bot token
    private static JDA jda;

    public static void main(String[] args) throws Exception {
        jda = JDABuilder.createDefault(botToken)
                .enableIntents(GatewayIntent.GUILD_MEMBERS).build();
        jda.getPresence().setStatus(OnlineStatus.DO_NOT_DISTURB);
        jda.getPresence().setActivity(Activity.playing(prefix + "help"));
        addListeners();
    }

    public static void addListeners() {
        jda.addEventListener(new Bot());
        jda.addEventListener(new Debug(myID, jda));
        jda.addEventListener(new VerifyEvent(jda, prefix));
        jda.addEventListener(new SpamPingEvent(jda, prefix));
        jda.addEventListener(new ChatModEvent(jda, prefix, myID));
        jda.addEventListener(new VoiceModEvent(jda, prefix, myID));
        jda.addEventListener(new MusicEvent(jda, prefix));
        jda.addEventListener(new ServerModEvent(jda, prefix, myID));
        jda.addEventListener(new HelpEvent(jda, prefix));
        jda.addEventListener(new JoinHomeworkEvent(jda, prefix));
        jda.addEventListener(new SongRequestEvent(jda, prefix));
        jda.addEventListener(new DiceRollEvent(jda, prefix));
        jda.addEventListener(new AskQuestionEvent(jda, prefix));
        jda.addEventListener(new WouldYouRatherEvent(jda, prefix));
    }

    public static void updatePrefix(String newPrefix) {
        prefix = newPrefix;
        jda.getPresence().setActivity(Activity.playing(prefix + "help"));
        updateListeners();
    }

    public static void updateListeners() {
        List<Object> listeners = jda.getRegisteredListeners();
        for (Object i : listeners) {
            jda.removeEventListener(i);
        }
        addListeners();
    }


    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.getMessage().getContentRaw().contains("discord.gg") && !event.getAuthor().isBot()) {
            event.getChannel().deleteMessageById(event.getMessageId()).complete();
        }
        if (event.getMessage().getContentRaw().equals(prefix + "invite")) {
            String invite = ""; // Discord Invite Link
            event.getChannel().sendMessage("Use this link to invite members to the server: " + invite).complete();
        }
        if (event.getMessage().getContentRaw().contains(prefix + "update") && event.getAuthor().getId().equals(myID)) {
            String[] newChar = event.getMessage().getContentRaw().split(" ");
            if (newChar.length == 1)
                return;
            event.getChannel().sendMessage("New prefix is now: " + newChar[1]).complete();
            updatePrefix(newChar[1]);
        }
    }

}

