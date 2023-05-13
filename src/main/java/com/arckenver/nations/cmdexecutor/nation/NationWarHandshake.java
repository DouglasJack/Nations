package com.arckenver.nations.cmdexecutor.nation;

import com.arckenver.nations.DataHandler;
import com.arckenver.nations.LanguageHandler;
import com.arckenver.nations.cmdelement.NationNameElement;
import com.arckenver.nations.object.Nation;
import com.arckenver.nations.object.War;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.UUID;

public class NationWarHandshake implements CommandExecutor {
    public static void create(CommandSpec.Builder cmd) {
        cmd.child(CommandSpec.builder()
                .description(Text.of(""))
                .permission("nations.command.nation.warhandshake")
                .arguments(GenericArguments.optional(new NationNameElement(Text.of("nation"))))
                .executor(new NationWarHandshake())
                .build(), "war");
    }


    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!(src instanceof Player)){return CommandResult.success();}


        Player player = (Player) src;
        Nation nation = DataHandler.getNationOfPlayer(player.getUniqueId());
        src.sendMessage(Text.of(TextColors.RED,"WAR STATUS: "+nation.getCurrentWar()));
        if(!nation.getPresident().equals(player.getUniqueId())){
            src.sendMessage(Text.of(TextColors.RED,"Speak to your nation leader to initiate a war."+nation.getPresident()+" -> "+player.getUniqueId()));
            return CommandResult.success();
        }

        if(nation == null) {
            src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_NONATION));
            return CommandResult.success();
        }

        if (!args.<String>getOne("nation").isPresent()){
            src.sendMessage(Text.of(TextColors.YELLOW, "/n war <nation>"));
            return CommandResult.success();
        }

        Nation DefenderNation = DataHandler.getNation(args.<String>getOne("nation").get());
        src.sendMessage(Text.of(TextColors.GREEN,"Defender Nation: "+DefenderNation.getName()));
        if(DefenderNation == null){
            src.sendMessage(Text.of(TextColors.RED, LanguageHandler.ERROR_BADNATIONNAME));
            return CommandResult.success();
        }

        War NW = new War(UUID.randomUUID(),nation,DefenderNation);
        if(NW.canWar()){
            if(!Sponge.getServer().getPlayer(DefenderNation.getPresident()).isPresent()){
                src.sendMessage(Text.of(TextColors.RED,DefenderNation.getName()+" leader is not online."));
                return CommandResult.success();
            }
            src.sendMessage(Text.of(TextColors.GREEN,"Initiated a war request with "+DefenderNation.getName()));
            NW.war_Handshake();
        }else{
            //src.sendMessage(Text.of(TextColors.RED,"You or the other nation do not meet war requirements"));
        }




        return CommandResult.success();
    }
}
