package com.kasp.rbw.commands.moderation;

import com.kasp.rbw.CommandSubsystem;
import com.kasp.rbw.EmbedType;
import com.kasp.rbw.commands.Command;
import com.kasp.rbw.config.Config;
import com.kasp.rbw.instance.Embed;
import com.kasp.rbw.instance.Player;
import com.kasp.rbw.instance.cache.PlayerCache;
import com.kasp.rbw.messages.Msg;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.time.LocalDateTime;
import java.util.Objects;

public class StrikeCmd extends Command {
    public StrikeCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        if (args.length < 3) {
            Embed reply = new Embed(EmbedType.ERROR, "Invalid Arguments", Msg.getMsg("wrong-usage").replaceAll("%usage%", getUsage()), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        String ID = args[1].replaceAll("[^0-9]", "");
        String reason = msg.getContentRaw().replaceAll(args[0], "").replaceAll(args[1], "").trim();

        Player player = PlayerCache.getPlayer(ID);
        player.setStrikes(player.getStrikes()+1);

        int strikes = player.getStrikes();
        if (player.getStrikes() > 5) {
            strikes = 5;
        }

        int bantime = Integer.parseInt(Config.getValue("strike-" + strikes));

        Embed embed = new Embed(EmbedType.DEFAULT, player.getIgn() + " has been striked", "", 1);
        embed.addField("Strikes:", player.getStrikes()-1 + " -> " + player.getStrikes(), false);
        embed.addField("Reason:", reason, false);

        Embed success = new Embed(EmbedType.SUCCESS, "", "Successfully striked <@" + ID + ">", 1);

        if (bantime != 0) {
            if (player.ban(LocalDateTime.now().plusHours(bantime), "[STRIKE] " + reason)) {

                embed.addField("You are now banned for: ", "`" + bantime + " hours`", false);
                embed.setDescription("<@" + ID + "> Please do `=fix` after " + bantime + "h if you're still not unbanned");
            }
            else {
                embed.addField("WARNING", "I couldn't ban the player since they're already banned", false);
                success.addField("WARNING", "I couldn't ban the player since they're already banned", false);
            }
        }

        msg.replyEmbeds(success.build()).queue();
        if (!Objects.equals(Config.getValue("ban-channel"), null)) {
            guild.getTextChannelById(Config.getValue("ban-channel")).sendMessage("<@" + ID + ">").setEmbeds(embed.build()).queue();
        }
    }
}