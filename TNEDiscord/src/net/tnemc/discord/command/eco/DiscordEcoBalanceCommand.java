package net.tnemc.discord.command.eco;

import github.scarsz.discordsrv.dependencies.jda.core.EmbedBuilder;
import net.tnemc.core.TNE;
import net.tnemc.discord.command.DiscordCommand;

import java.awt.*;

/**
 * The New Economy Minecraft Server Plugin
 * <p>
 * Created by creatorfromhell on 7/7/2019.
 * <p>
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/ or send a letter to
 * Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 * Created by creatorfromhell on 06/30/2017.
 */
public class DiscordEcoBalanceCommand extends DiscordCommand {
  @Override
  public String name() {
    return "balance";
  }

  @Override
  public String role() {
    return TNE.configurations().getString("Discord.Roles.Balance");
  }

  @Override
  public EmbedBuilder help() {
    EmbedBuilder builder = new EmbedBuilder();
    builder.setTitle("!eco balance");
    builder.setDescription("Usage: !eco balance");
    builder.addField("Description", "Checks your minecraft balance.", false);
    builder.setColor(new Color(190, 57, 0));
    return builder;
  }
}