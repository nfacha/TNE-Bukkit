package net.tnemc.core.common.menu.consumable.menu.icon;

import net.tnemc.core.common.menu.MenuClickType;
import net.tnemc.core.common.menu.icon.IconType;
import org.bukkit.entity.Player;

/**
 * The New Economy Minecraft Server Plugin
 * <p>
 * Created by creatorfromhell on 8/3/2021.
 * <p>
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/ or send a letter to
 * Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 * Created by creatorfromhell on 06/30/2017.
 */
public class IconClick {

  private IconType icon;
  private String menu;
  private Player player;
  private MenuClickType clickType;

  public IconClick(IconType icon, String menu, Player player, MenuClickType clickType) {
    this.icon = icon;
    this.menu = menu;
    this.player = player;
    this.clickType = clickType;
  }

  public IconType getIcon() {
    return icon;
  }

  public void setIcon(IconType icon) {
    this.icon = icon;
  }

  public String getMenu() {
    return menu;
  }

  public void setMenu(String menu) {
    this.menu = menu;
  }

  public Player getPlayer() {
    return player;
  }

  public void setPlayer(Player player) {
    this.player = player;
  }

  public MenuClickType getClickType() {
    return clickType;
  }

  public void setClickType(MenuClickType clickType) {
    this.clickType = clickType;
  }
}
