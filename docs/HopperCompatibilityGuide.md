**Hopper Compatibility Guide — How to port HopperProcessor fixes into other mods (for GPT-5 mini)**

This guide explains, step-by-step, how we adapted Hopper behavior in the server plugin and the exact changes to look for and apply when making other mods compatible with the prerelease server. It is written so an automated assistant (GPT-5 mini) or a human engineer can follow the patterns and reproduce the work on other mods.

**Goal**: Ensure item transfer logic and visuals behave correctly when moving items between containers and between hopper components, while avoiding dropped/vanished items.

**High-level strategy**
- Locate the component handling hopper transfer logic (block-state `HopperProcessor` and component `HopperComponent`).
- Ensure every successful item transfer path calls the visual-spawn helper (e.g. `spawnVisualFor(...)`).
- Make visuals visual-only (non-physics) so they don't get ejected by block collisions.
- Add chained-container export support (hopper -> hopper/component or hopper -> state-backed container) and verify add/remove semantics.
- Add diagnostics and verification to prevent item disappearance.

**Search targets / patterns**
- Methods: `tryTransferToOrFromContainer`, `tryImportFromContainer`, `spawnVisualFor`, `tryPickupItemEntities`.
- Calls to `addItemStackToSlot(...)`, `removeItemStackFromSlot(...)`, `getItemContainer()` and `ItemStackSlotTransaction` handling.

**Concrete changes and code patterns**
1) Spawn visuals on successful add
- After a successful `ItemStackSlotTransaction tx = target.addItemStackToSlot(...);` and `tx.succeeded() == true`, call the visual helper immediately before removing the items from source:
  - `spawnVisualFor(stackToVisualize, exportPhase, pos, side, nearbyBuffer);`
- Add defensive try/catch around `spawnVisualFor` so it never blocks transfer:
  - `try { spawnVisualFor(...); } catch (Throwable ignored) {}`

2) Make visuals non-physics
- Instead of replacing `PhysicsValues` with zero values, remove the physics component entirely so the engine does not apply block-collision ejection:
  - `try { itemEntityHolder.removeComponent(PhysicsValues.getComponentType()); } catch (Exception ignored) {}`
- Optionally remove or set an `Intangible` or `BoundingBox` component to avoid collisions.

3) Chained hopper (component-first) export
- Detect adjacent component-backed `HopperComponent` and attempt to obtain its `ItemContainer` via its `getItemContainer()` method (use reflection if the other mod/component is not in the same package).
- Call `addItemStackToSlot` on that container and verify the transaction.

4) Generic export-to-state-backed container
- Before import path completes, attempt a push into the adjacent block state `ItemContainer` when present (use the engine API that your mod and the engine expose).
- Handle unknown container types gracefully using reflection; fall back to original import logic if push fails.

5) Verify target acceptance before removing source
- Immediately after `tx.succeeded()`, read the target slot via `target.getItemStack((short)slot)` and confirm the quantity increased as expected.
- If verification fails, attempt a rollback: call `target.removeItemStackFromSlot(slot, removedAmount)` to undo and log an error. Do not remove the source stack until verification passes.

6) Keep or suppress visuals as desired
- By default keep visuals for visibility. If you need silent chaining, skip `spawnVisualFor` when the destination is another hopper component (add a conditional flag).

**Diagnostics to add**
- Minimal logs at decision points help reproduce crashes:
  - `[HopperDiag] exportAttempt side=%s pos=%s state=%s container=%b`
  - `[HopperDiag] addItemStackToSlot tx=%s slot=%d before=%s after=%s`
  - `[Visual] spawnVisualFor called stack=%s exportPhase=%b`
- Place logs around `addItemStackToSlot`, the verification readback, and any reflection-based container lookup.

**Build & test**
- Build commands from repo root on Windows:

```powershell
./gradlew.bat compileJava
./gradlew.bat shadowJar
```

- Test scenarios to exercise the change:
  - Hopper -> chest export
  - Hopper -> hopper -> hopper chained transfer (3+ in a row)
  - Export when a player is standing nearby (visuals spawn toward player/contact)
- Capture logs for any failed transfer and include them with the failing steps.

**Safety and compatibility notes**
- Use reflection defensively: catch `NoSuchMethodException`, `ClassNotFoundException`, and `IllegalAccessException`.
- Rollback attempts should be best-effort: log failures but don't blow up the tick loop.
- Keep changes minimal and revertible; prefer to add small helper methods rather than large sweeping refactors.

**Quick checklist**
- [ ] Confirm `spawnVisualFor` exists and is invoked on every successful transfer path.
- [ ] Remove physics components from visual entities (avoid block ejection).
- [ ] Add chained export support for component-backed hoppers.
- [ ] Add generic export-to-container attempt for state-backed containers.
- [ ] Verify `addItemStackToSlot` acceptance before removing source; rollback if necessary.
- [ ] Add lightweight diagnostics and rebuild.

If you want, I can also create a PR template that inserts these diagnostics and skeleton changes into other mod repos automatically. Tell me which repo to target and I will prepare a PR branch and patch.