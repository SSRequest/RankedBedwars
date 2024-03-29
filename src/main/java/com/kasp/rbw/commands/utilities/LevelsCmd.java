package com.kasp.rbw.commands.utilities;

import com.kasp.rbw.CommandSubsystem;
import com.kasp.rbw.EmbedType;
import com.kasp.rbw.commands.Command;
import com.kasp.rbw.instance.Embed;
import com.kasp.rbw.instance.Level;
import com.kasp.rbw.instance.Player;
import com.kasp.rbw.instance.cache.LevelCache;
import com.kasp.rbw.instance.cache.PlayerCache;
import com.kasp.rbw.messages.Msg;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class LevelsCmd extends Command {
    public LevelsCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        if (args.length != 1) {
            msg.replyEmbeds(new Embed(EmbedType.ERROR, "Invalid Arguments", Msg.getMsg("wrong-usage").replaceAll("%usage%", getUsage()), 1).build()).queue();
            return;
        }

        Embed embed = new Embed(EmbedType.DEFAULT, "All levels and info", "", 1);
        String levels = "";
        for (Level l : LevelCache.getLevels().values()) {
            if (l.getLevel() != 0) {
                String rewards = "";
                for (String s : l.getRewards()) {
                    if (s.startsWith("GOLD")) {
                        rewards += s.split("=")[1] + " Gold ";
                    }
                }
                if (rewards.equals("")) {
                    levels += "**" + l.getLevel() + "** — Needed XP: `" + l.getNeededXP() + "`\n";
                }
                else {
                    levels += "**" + l.getLevel() + "** — Needed XP: `" + l.getNeededXP() + "` Rewards: `" + rewards + "`\n";
                }

            }
        }

        Player player = PlayerCache.getPlayer(sender.getId());

        embed.addField("Your level", player.getLevel().getLevel() + " `(" + player.getXp() + "/" + LevelCache.getLevel(player.getLevel().getLevel() + 1).getNeededXP() + " XP)`", false);
        embed.addField("All levels", levels, false);
        msg.replyEmbeds(embed.build()).queue();
    }
}
