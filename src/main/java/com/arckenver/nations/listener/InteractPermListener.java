package com.arckenver.nations.listener;

import java.util.Optional;

import com.arckenver.nations.object.Nation;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.hanging.ItemFrame;
import org.spongepowered.api.entity.living.ArmorStand;
import org.spongepowered.api.entity.living.monster.Monster;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.CollideBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.arckenver.nations.ConfigHandler;
import com.arckenver.nations.DataHandler;
import com.arckenver.nations.LanguageHandler;

public class InteractPermListener
{

	@Listener
	public void onCollideBlock(CollideBlockEvent event, @First Player player)
	{
		if (player.hasPermission("nations.admin.bypass.perm.build"))
		{
			return;
		}
		Nation nation = DataHandler.getNationOfPlayer(player.getUniqueId());
		Nation blocknation = DataHandler.getNation(event.getTargetLocation());

		if(nation.getCurrentWar() != null){
			if(nation.getCurrentWar().attacker != null && nation.getCurrentWar().defender != null){
				if(nation.getCurrentWar().attacker.equals(blocknation) || nation.getCurrentWar().defender.equals(blocknation)){
					return;
				}
			}
		}
		if (ConfigHandler.getNode("worlds").getNode(event.getTargetLocation().getExtent().getName()).getNode("enabled").getBoolean()
				&& !ConfigHandler.isWhitelisted("build", event.getTargetBlock().getType().getId())
				&& !DataHandler.getPerm("build", player.getUniqueId(), event.getTargetLocation()))
		{
			event.setCancelled(true);
		}
	}

	@Listener(order=Order.FIRST, beforeModifications = true)
	public void onInteract(InteractBlockEvent event, @First Player player)
	{
		if (!ConfigHandler.getNode("worlds").getNode(player.getWorld().getName()).getNode("enabled").getBoolean())
		{
			return;
		}
		if (player.hasPermission("nations.admin.bypass.perm.interact"))
		{
			return;
		}
		Optional<ItemStack> optItem = player.getItemInHand(HandTypes.MAIN_HAND);
		if (optItem.isPresent() && (ConfigHandler.isWhitelisted("use", optItem.get().getType().getId()) || optItem.get().getType().equals(ItemTypes.GOLDEN_AXE) && ConfigHandler.getNode("others", "enableGoldenAxe").getBoolean(true)))
			return;
		event.getTargetBlock().getLocation().ifPresent(loc -> {
			Nation nation = DataHandler.getNationOfPlayer(player.getUniqueId());
			Nation blocknation = DataHandler.getNation(loc);

			if(nation.getCurrentWar() != null){
				if(nation.getCurrentWar().attacker != null && nation.getCurrentWar().defender != null){
					if(nation.getCurrentWar().attacker.equals(blocknation) || nation.getCurrentWar().defender.equals(blocknation)){
						return;
					}
				}
			}

			if (!DataHandler.getPerm("interact", player.getUniqueId(), loc))
			{

				event.setCancelled(true);
				if (loc.getBlockType() != BlockTypes.STANDING_SIGN && loc.getBlockType() != BlockTypes.WALL_SIGN)
					player.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_PERM_INTERACT));
			}
		});
	}

	@Listener(order=Order.FIRST, beforeModifications = true)
	public void onInteract(InteractEntityEvent event, @First Player player)
	{
		if (!ConfigHandler.getNode("worlds").getNode(player.getWorld().getName()).getNode("enabled").getBoolean())
		{
			return;
		}
		if (player.hasPermission("nations.admin.bypass.perm.interact"))
		{
			return;
		}
		Entity target = event.getTargetEntity();
		if (target instanceof Player || target instanceof Monster)
		{
			return;
		}
		if (target instanceof ItemFrame || target instanceof ArmorStand)
		{
			if (player.hasPermission("nations.admin.bypass.perm.build"))
			{
				return;
			}

			Nation nation = DataHandler.getNationOfPlayer(player.getUniqueId());
			Nation blocknation = DataHandler.getNation(event.getTargetEntity().getLocation());

			if(nation.getCurrentWar() != null){
				if(nation.getCurrentWar().attacker != null && nation.getCurrentWar().defender != null){
					if(nation.getCurrentWar().attacker.equals(blocknation) || nation.getCurrentWar().defender.equals(blocknation)){
						return;
					}
				}
			}

			if (!DataHandler.getPerm("build", player.getUniqueId(), event.getTargetEntity().getLocation()))
			{
				event.setCancelled(true);
				player.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_PERM_BUILD));
			}
			return;
		}
		if (!DataHandler.getPerm("interact", player.getUniqueId(), event.getTargetEntity().getLocation()))
		{
			event.setCancelled(true);
			player.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_PERM_INTERACT));
		}
	}
}
