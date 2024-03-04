package com.arckenver.nations.listener;

import com.arckenver.nations.object.Nation;
import com.arckenver.nations.object.War;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.IndirectEntityDamageSource;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.filter.cause.All;

import com.arckenver.nations.ConfigHandler;
import com.arckenver.nations.DataHandler;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class PvpListener
{
	
	@Listener(order=Order.FIRST, beforeModifications = true)
	public void onEntityDamagedByPlayer(DamageEntityEvent event, @All(ignoreEmpty=false) EntityDamageSource[] sources)
	{
		if (!ConfigHandler.getNode("worlds").getNode(event.getTargetEntity().getWorld().getName()).getNode("enabled").getBoolean())
		{
			return;
		}
		Entity attacker = null;
		for (int i = 0; i < sources.length; i++)
		{
			if (sources[i].getSource().getType() == EntityTypes.PLAYER
					|| (sources[i] instanceof IndirectEntityDamageSource && ((IndirectEntityDamageSource) sources[i]).getIndirectSource().getType() == EntityTypes.PLAYER))
			{
				attacker = sources[i].getSource();
			}
		}
		if (attacker != null && event.getTargetEntity().getType() == EntityTypes.PLAYER)
		{
			if(DataHandler.getNationOfPlayer(attacker.getUniqueId()) != null) {
				Nation insideNation = DataHandler.getNation(attacker.getLocation());
				Nation playerNation = DataHandler.getNationOfPlayer(attacker.getUniqueId());
				War war = playerNation.getCurrentWar();
				if (war != null && war.attacker != null && war.defender != null) {
					if (war.attacker.equals(insideNation) || war.defender.equals(insideNation)) {
						// We are inside of a nation so we can log the death for this player to the current war nation stats handler.
						if(war.attacker.equals(playerNation)){
							// Player is the attacker.
							if(((Player) event.getTargetEntity()).getHealthData().health().get() <= 0){
								Sponge.getServer().getPlayer(attacker.getUniqueId()).get().sendMessage(Text.of(TextColors.RED,"You killed an enemy combatant"));
							}
						}else{
							// Player is the defender.
							if(((Player) event.getTargetEntity()).getHealthData().health().get() <= 0){
								Sponge.getServer().getPlayer(attacker.getUniqueId()).get().sendMessage(Text.of(TextColors.RED,"You killed an enemy combatant"));

							}
						}
						return; // This enables PVP in that players claim.
					}
				}
			}

			if (!DataHandler.getFlag("pvp", attacker.getLocation()) || !DataHandler.getFlag("pvp", event.getTargetEntity().getLocation()))
			{
				event.setCancelled(true);
				return;
			}
		}
	}
}
