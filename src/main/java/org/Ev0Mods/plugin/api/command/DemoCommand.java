package org.Ev0Mods.plugin.api.command;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.Ev0Mods.plugin.api.ui.SimpleDemoPage;

/**
 * Command: /demo
 * Opens a simple reference UI demo page to show how server-side UI works.
 */
public class DemoCommand extends AbstractPlayerCommand {

    public DemoCommand() {
        super("demo", "Open a demo UI page");
    }

    @Override
    protected void execute(CommandContext ctx, Store<EntityStore> store,
                           Ref<EntityStore> ref, PlayerRef player, World world) {
        SimpleDemoPage.open(player, store);
    }
}