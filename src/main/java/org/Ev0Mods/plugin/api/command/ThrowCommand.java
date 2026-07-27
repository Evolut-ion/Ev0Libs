package org.Ev0Mods.plugin.api.command;

import org.Ev0Mods.plugin.api.telemetry.Ev0Telemetry;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

/**
 * Command: /throw
 * Sends a test error report (GitHub issue + website + Discord embed) to verify
 * the telemetry reporting pipeline. Source of truth is the GitHub issue.
 */
public class ThrowCommand extends AbstractPlayerCommand {

    public ThrowCommand() {
        super("throw", "Send a test error report to verify telemetry reporting");
    }

    @Override
    protected void execute(CommandContext ctx, Store<EntityStore> store,
                           Ref<EntityStore> ref, PlayerRef player, World world) {
        RuntimeException ex = new RuntimeException("Test exception from /throw command — telemetry check");

        // Report the handled error as a GitHub-backed report (source of truth)
        Ev0Telemetry.sendHandledError(ex, "ThrowCommand.execute");

        // Throw to also test the crash handler — the GitHub report already exists to back it
        throw ex;
    }
}
