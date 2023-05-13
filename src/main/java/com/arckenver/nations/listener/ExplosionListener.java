package com.arckenver.nations.listener;

import com.arckenver.nations.LanguageHandler;
import com.arckenver.nations.object.Nation;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.world.ExplosionEvent;

import com.arckenver.nations.ConfigHandler;
import com.arckenver.nations.DataHandler;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.format.TextColors;



public class ExplosionListener
{
	@Listener(order=Order.FIRST, beforeModifications = true)
	public void onExplosion(ExplosionEvent.Pre event)
	{
		if (!ConfigHandler.getNode("worlds").getNode(event.getTargetWorld().getName()).getNode("enabled").getBoolean())
		{
			return;
		}
		if (!DataHandler.getFlag("explosions", event.getExplosion().getLocation()))
		{
			Nation impactedNation = DataHandler.getNation(event.getExplosion().getLocation());
			User usr = event.getCause().getContext().get(EventContextKeys.OWNER).orElse(null);
			if(usr == null){
				event.setCancelled(true);
				return;
			}
			Player plr = usr.getPlayer().get();

			Nation playerNation = DataHandler.getNationOfPlayer(plr.getUniqueId());
			if(playerNation.getCurrentWar() == null){
				event.setCancelled(true);
				return;
			}

			if(playerNation.getCurrentWar().attacker == null){
				event.setCancelled(true);
				return;
			}
			if(playerNation.getCurrentWar().defender == null){
				event.setCancelled(true);
				return;
			}
			MessageChannel.TO_CONSOLE.send(Text.of(TextColors.AQUA, "This is a VALID war explosion: "+playerNation.getCurrentWar().attacker.getName()+" def: "+playerNation.getCurrentWar().defender.getName()));
			if(playerNation.getCurrentWar().attacker.equals(impactedNation) || playerNation.getCurrentWar().defender.equals(impactedNation)){ // Can't believe I gotta check both, turns out people like to steal others ICBMS.
				return;
			}

			event.setCancelled(true);
		}
	}
	
	@Listener(order=Order.FIRST, beforeModifications = true)
	public void onExplosion(ExplosionEvent.Post event)
	{
//		if (event.getTransactions().size() > 100)
//		{
//            event.setCancelled(true);
//		}
		for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
            BlockSnapshot blockSnapshot = transaction.getOriginal();
            if (blockSnapshot.getLocation().isPresent() &&

            		ConfigHandler.getNode("worlds").getNode(transaction.getOriginal().getLocation().get().getExtent().getName()).getNode("enabled").getBoolean() &&
            		!DataHandler.getFlag("explosions", blockSnapshot.getLocation().get()))
    		{				Nation impactedNation = DataHandler.getNation(event.getExplosion().getLocation());
				User usr = event.getCause().getContext().get(EventContextKeys.OWNER).orElse(null);
				if(usr == null){
					transaction.setValid(false);
					return;
				}
				Player plr = usr.getPlayer().get();

				Nation playerNation = DataHandler.getNationOfPlayer(plr.getUniqueId());
				if(playerNation.getCurrentWar() == null){
					transaction.setValid(false);
					return;
				}

				if(playerNation.getCurrentWar().attacker == null){
					transaction.setValid(false);
					return;
				}
				if(playerNation.getCurrentWar().defender == null){
					transaction.setValid(false);
					return;
				}
				MessageChannel.TO_CONSOLE.send(Text.of(TextColors.AQUA, "This is a VALID war explosion: attacker->"+playerNation.getCurrentWar().attacker.getName()+" deender->"+playerNation.getCurrentWar().defender.getName()));
				if(playerNation.getCurrentWar().attacker.equals(impactedNation) || playerNation.getCurrentWar().defender.equals(impactedNation)){ // Can't believe I gotta check both, turns out people like to steal others ICBMS.
					return;
				}


            	transaction.setValid(false);
    		}
		}

	}
}
