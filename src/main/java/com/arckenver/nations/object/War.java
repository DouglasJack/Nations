package com.arckenver.nations.object;

import com.arckenver.nations.ConfigHandler;
import com.arckenver.nations.DataHandler;
import com.arckenver.nations.NationsPlugin;
import com.arckenver.nations.serializer.NationDeserializer;
import com.arckenver.nations.serializer.NationSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import java.io.File;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class War {

    private UUID uuid;
    public Nation attacker;
    public Nation defender;
    private BigDecimal attackerBefore_balance;
    private BigDecimal defenderenderBefore_balance;

    private UUID temp_attacker;
    private UUID temp_defender;

    private LocalDateTime endDate;




    public War(UUID uuid,Nation attacker, Nation defender){
        this.uuid = uuid;
        this.attacker = attacker;
        this.defender = defender;
        DataHandler.wars.put(uuid,this);

        LocalDateTime localNow = LocalDateTime.now();


        endDate = localNow.plusDays(1);

        Optional<Account> optAccount = NationsPlugin.getEcoService().getOrCreateAccount("nation-" + attacker.getUUID().toString());
        attackerBefore_balance = optAccount.get().getBalance(NationsPlugin.getEcoService().getDefaultCurrency());
        Optional<Account> optdefenderAccount = NationsPlugin.getEcoService().getOrCreateAccount("nation-" + defender.getUUID().toString());
        defenderenderBefore_balance = optdefenderAccount.get().getBalance(NationsPlugin.getEcoService().getDefaultCurrency());


    }


    public War(UUID uuid, UUID attacker, UUID defender,LocalDateTime end){
        this.uuid = uuid;
        this.temp_attacker = attacker;
        this.temp_defender = defender;
        this.endDate = end;

        DataHandler.wars.put(uuid,this);
    }

    public void implementAttDef(){
        attacker = DataHandler.getNation(temp_attacker);
        defender = DataHandler.getNation(temp_defender);

        attacker.setCurrentWar(this);
        attacker.setInWar(true);
        defender.setCurrentWar(this);
        defender.setInWar(true);

        LocalDateTime localNow = LocalDateTime.now();
        Duration remainingDuration = Duration.between(localNow, endDate);

        // ** CONFIG ** length of days: Replace 1.
        Sponge.getScheduler().createTaskBuilder()
                .async()
                .execute(this::endWar)
                .delay(remainingDuration.toMinutes(), TimeUnit.MINUTES)
                .submit(NationsPlugin.getInstance());
    }

    // This really shouldn't be in here, but eh.
    public boolean canWar(){
        // Checks to see if both nations have the requirements to go into a war.

        /// ** CONFUG ** plusDays between wars.
        LocalDateTime localNow = LocalDateTime.now();
        if((attacker.getLastWarTime().plusDays(1).isBefore(localNow) && defender.getLastWarTime().plusDays(1).isBefore(localNow))){
            ((Player) Sponge.getServer().getPlayer(attacker.getPresident()).orElse(null)).sendMessage(Text.of(TextColors.RED,"You or your enemy have been in a war too recently."+attacker.getLastWarTime().plusDays(1)));
            return false;
        }
        /// ** CONFIG ** allow of users to set the minimum citizens required to war.
        if(!(attacker.getCitizens().size() > 0 && defender.getCitizens().size() > 0)){
            ((Player) Sponge.getServer().getPlayer(attacker.getPresident()).orElse(null)).sendMessage(Text.of(TextColors.RED,"You or your enemy do not have enough citizens to initate a war."));
            return false;
        }

        Optional<Account> optAccount = NationsPlugin.getEcoService().getOrCreateAccount("nation-" + attacker.getUUID().toString());
        BigDecimal attackerbalance = optAccount.get().getBalance(NationsPlugin.getEcoService().getDefaultCurrency());
        Optional<Account> optdefenderAccount = NationsPlugin.getEcoService().getOrCreateAccount("nation-" + defender.getUUID().toString());
        BigDecimal defenderbalance = optdefenderAccount.get().getBalance(NationsPlugin.getEcoService().getDefaultCurrency());

        // ** CONFIG ** allow users to set the minimum balance of both people in war before engaging.
        if(!(attackerbalance.compareTo(new BigDecimal(5000)) >= 0 && defenderbalance.compareTo(new BigDecimal(5000)) >= 0)){
            ((Player) Sponge.getServer().getPlayer(attacker.getPresident()).orElse(null)).sendMessage(Text.of(TextColors.RED,"You or your enemy do not have enough money in their accounts."));
            return false;
        }

        return true;
    }

    public void war_Handshake(){
        // This method will make sure both nations agree to war.
        // attacker nation already agreed, so ask defender.


        Player player = Sponge.getServer().getPlayer(defender.getPresident()).orElse(null);
        if (player != null) {
            List<Text> messages = Arrays.asList(
                    Text.builder().append(Text.of(TextColors.DARK_AQUA,"WAR REQUEST FROM "+attacker.getName())).build(),
                    Text.builder().append(Text.of(TextColors.GRAY,"Wars last for 24 hours, with possible financial losses or gains")).build(),
                    Text.builder().append(Text.of(TextColors.GRAY,"Enemy Citizens: "+attacker.getCitizens().size()+", Your Citizens: "+defender.getCitizens().size())).build(),
                    Text.builder().append(Text.of(TextColors.AQUA,"Would you like to accept the war request?")).build(),
                    Text.builder().append(Text.of(TextColors.GREEN, "Yes")).onClick(TextActions.executeCallback(src -> {
                        List<Player> players = new ArrayList<>(Sponge.getServer().getOnlinePlayers());
                        List<Text> msg = Arrays.asList(
                                Text.builder().append(Text.of(TextColors.DARK_RED,"A WAR HAS BEGUN!")).build(),
                                Text.builder().append(Text.of(TextColors.DARK_RED,attacker.getName()+" vs "+defender.getName())).build()
                        );
                        for (Player plr : players) {
                            plr.sendMessages(msg);
                        }
                        LocalDateTime localNow = LocalDateTime.now();
                        // ** CONFIG **  (daysBetweenWarDelay * warLengthDays)
                        attacker.setLastWarTime(localNow.plusDays(1 * 1));
                        defender.setLastWarTime(localNow.plusDays(1 * 1));
                        endDate = localNow.plusDays(1 * 1);
                        attacker.setCurrentWar(this);
                        defender.setCurrentWar(this);
                        attacker.inWar = true;
                        defender.inWar = true;

                        // ** CONFIG ** length of days: Replace 1.
                        Sponge.getScheduler().createTaskBuilder()
                                .async()
                                .execute(this::endWar)
                                .delay(1, TimeUnit.MINUTES)
                                .submit(NationsPlugin.getInstance());
                    })).build(),
                    Text.builder().append(Text.of(TextColors.RED, "No")).onClick(TextActions.executeCallback(src -> {
                        src.sendMessage(Text.of("You have denied the war request.."));
                        ((Player) Sponge.getServer().getPlayer(attacker.getPresident()).orElse(null)).sendMessage(Text.of(TextColors.RED,defender.getName()+" has rejected your war declaration."));
                    })).build()
            );
            player.sendMessages(messages);
        }

    }

    public void war_Immediate(Nation attacker, Nation defender){
        // This method will INSTANTLY send both to war.

    }


    public Runnable endWar(){
        LocalDateTime localNow = LocalDateTime.now();
        attacker.setLastWarTime(localNow);
        defender.setLastWarTime(localNow);
        attacker.inWar = false; // Honestly not needed, was needed during developing. Don't want to remove it.
        defender.inWar = false;

        DataHandler.wars.remove(this);

        List<Player> players = new ArrayList<>(Sponge.getServer().getOnlinePlayers());
        List<Text> messages = Arrays.asList(
                Text.builder().append(Text.of(TextColors.GREEN,"A WAR HAS ENDED!")).build(),
                Text.builder().append(Text.of(TextColors.GREEN,attacker.getName()+" vs "+defender.getName())).build()
        );
        for (Player plr : players) {
            plr.sendMessages(messages);
        }
        return null;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }
}
