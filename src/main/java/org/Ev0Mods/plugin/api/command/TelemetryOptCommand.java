package org.Ev0Mods.plugin.api.command;

import org.Ev0Mods.plugin.api.Ev0Config;
import org.Ev0Mods.plugin.api.Ev0Log;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

/**
 * Command: /ev0stelemetry [on|off]
 *
 * Allows players with admin permission to opt in or out of telemetry reporting.
 * When telemetry is disabled, no heartbeats, errors, or crash reports are sent
 * to the Ev0 backend.  The setting is persisted in Ev0Lib_config.properties.
 *
 * Usage:
 *   /ev0stelemetry       – shows the current opt-in state
 *   /ev0stelemetry on    – enable telemetry
 *   /ev0stelemetry off   – disable telemetry
 */
public class TelemetryOptCommand extends AbstractPlayerCommand {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    private final OptionalArg<String> stateArg;

    public TelemetryOptCommand() {
        super("ev0stelemetry", "View or change telemetry opt-in status");
        this.stateArg = withOptionalArg("state", "on|off to enable/disable, or omit to check status", ArgTypes.STRING);
    }

    @Override
    protected void execute(CommandContext ctx, Store<EntityStore> store,
                           Ref<EntityStore> ref, PlayerRef player, World world) {
        if (stateArg.provided(ctx)) {
            String action = stateArg.get(ctx).toLowerCase();
            switch (action) {
                case "on":
                case "true":
                case "enable":
                    Ev0Config.setTelemetryOptIn(true);
                    ctx.sendMessage(tag().insert(Message.raw("Telemetry enabled.").color("green")));
                    ctx.sendMessage(Message.raw("Errors, crashes, and heartbeats will be sent to the Ev0 backend.").color("gray"));
                    Ev0Log.info(LOGGER, "Telemetry enabled by " + ctx.sender());
                    return;

                case "off":
                case "false":
                case "disable":
                    Ev0Config.setTelemetryOptIn(false);
                    ctx.sendMessage(tag().insert(Message.raw("Telemetry disabled.").color("gold")));
                    ctx.sendMessage(Message.raw("No data will be sent to the Ev0 backend.").color("gray"));
                    Ev0Log.info(LOGGER, "Telemetry disabled by " + ctx.sender());
                    return;

                default:
                    ctx.sendMessage(tag().insert(
                        Message.raw("Unknown option: " + action + ". Use ").color("gray")
                            .insert(Message.raw("/ev0stelemetry on").color("white"))
                            .insert(Message.raw(" or ").color("gray"))
                            .insert(Message.raw("/ev0stelemetry off").color("white"))
                            .insert(Message.raw(".").color("gray"))
                    ));
                    return;
            }
        }

        // No argument – show current state
        boolean enabled = Ev0Config.isTelemetryOptIn();
        Message statusMsg = enabled
            ? Message.raw("enabled").color("green")
            : Message.raw("disabled").color("red");
        ctx.sendMessage(tag().insert(
            Message.raw("Telemetry is currently ").color("gray")
                .insert(statusMsg)
                .insert(Message.raw(".").color("gray"))
        ));
        ctx.sendMessage(tag().insert(
            Message.raw("Use ").color("gray")
                .insert(Message.raw("/ev0stelemetry on").color("white"))
                .insert(Message.raw(" or ").color("gray"))
                .insert(Message.raw("/ev0stelemetry off").color("white"))
                .insert(Message.raw(" to change.").color("gray"))
        ));
    }

    /** Build the "[Ev0Lib]" tag prefix. */
    private static Message tag() {
        return Message.raw("[").color("gray")
                .insert(Message.raw("Ev0Lib").color("aqua"))
                .insert(Message.raw("] ").color("gray"));
    }
}
