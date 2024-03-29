package com.kasp.rbw.commands.utilities;

import com.kasp.rbw.CommandSubsystem;
import com.kasp.rbw.EmbedType;
import com.kasp.rbw.commands.Command;
import com.kasp.rbw.instance.Embed;
import com.kasp.rbw.instance.Player;
import com.kasp.rbw.instance.Queue;
import com.kasp.rbw.instance.cache.QueueCache;
import com.kasp.rbw.messages.Msg;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class QueuesCmd extends Command {

    public QueuesCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        if (args.length != 1) {
            Embed reply = new Embed(EmbedType.ERROR, "Invalid Arguments", Msg.getMsg("wrong-usage").replaceAll("%usage%", getUsage()), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        if (QueueCache.getQueues().size() < 1) {
            Embed reply = new Embed(EmbedType.ERROR, "Error", "There are currently no queues set up. Add one using `=addqueue`!", 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        Embed embed = new Embed(EmbedType.DEFAULT, "All server queues", "this also shows **currently** queueing players", 1);

        for (Queue q : QueueCache.getQueues().values()) {

            String content = "There are currently no players playing";
            if (q.getPlayers().size() > 0) {
                content = "";

                for (Player p : q.getPlayers()) {
                    content += "<@" + p.getID() + ">\n";
                }
            }

            embed.addField(guild.getVoiceChannelById(q.getID()).getName() + " - `" + q.getPlayers().size() + "/" + q.getPlayersEachTeam() * 2 + "`",
                    content, false);
        }

        msg.replyEmbeds(embed.build()).queue();
    }
}
