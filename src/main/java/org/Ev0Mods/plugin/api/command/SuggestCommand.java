package org.Ev0Mods.plugin.api.command;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.Ev0Mods.plugin.api.telemetry.TelemetryReportPage;

public class SuggestCommand extends AbstractPlayerCommand {

    public SuggestCommand() {
        super("suggest", "Submit a suggestion to the Ev0 development team");
    }

    @Override
    protected void execute(CommandContext ctx, Store<EntityStore> store,
                           Ref<EntityStore> ref, PlayerRef player, World world) {
        TelemetryReportPage.open(player, store, "suggestion");
    }
}
