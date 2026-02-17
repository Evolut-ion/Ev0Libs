package org.Ev0Mods.plugin.api.util;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.asset.type.model.config.Model;
import com.hypixel.hytale.server.core.entity.EntityUtils;
import com.hypixel.hytale.server.core.entity.LivingEntity;
import com.hypixel.hytale.server.core.entity.entities.BlockEntity;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.ecs.DropItemEvent;
import com.hypixel.hytale.server.core.event.events.ecs.InteractivelyPickupItemEvent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.inventory.container.SimpleItemContainer;
import com.hypixel.hytale.server.core.inventory.transaction.ItemStackTransaction;
import com.hypixel.hytale.server.core.modules.entity.DespawnComponent;
import com.hypixel.hytale.server.core.modules.entity.EntityModule;
import com.hypixel.hytale.server.core.modules.entity.component.*;
import com.hypixel.hytale.server.core.modules.entity.item.ItemComponent;
import com.hypixel.hytale.server.core.modules.entity.item.PickupItemComponent;
import com.hypixel.hytale.server.core.modules.entity.item.PreventItemMerging;
import com.hypixel.hytale.server.core.modules.entity.player.PlayerSettings;
import com.hypixel.hytale.server.core.modules.entity.tracker.NetworkId;
import com.hypixel.hytale.server.core.modules.physics.component.PhysicsValues;
import com.hypixel.hytale.server.core.modules.physics.component.Velocity;
import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import java.time.Instant;
import java.util.Objects;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ItemUtilsExtended {
    private static double centerCoord(double c) {
        double frac = c - Math.floor(c);
        if (Math.abs(frac - 0.5) < 0.01) return c; // already centered
        return c + 0.5; // integer block coordinate -> center
    }
    @Nonnull
    public static ComponentType<EntityStore, ItemComponent> getComponentItemType() {
        return EntityModule.get().getItemComponentType();
    }

    public static void interactivelyPickupItem(@Nonnull Ref<EntityStore> ref, @Nonnull ItemStack itemStack, @Nullable Vector3d origin, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        LivingEntity entity = (LivingEntity) EntityUtils.getEntity(ref, componentAccessor);
        InteractivelyPickupItemEvent event = new InteractivelyPickupItemEvent(itemStack);
        componentAccessor.invoke(ref, event);
        if (event.isCancelled()) {
            dropItem(ref, itemStack, componentAccessor);
        } else {
            Player playerComponent = (Player)componentAccessor.getComponent(ref, Player.getComponentType());
            if (playerComponent != null) {
                TransformComponent transformComponent = (TransformComponent)componentAccessor.getComponent(ref, TransformComponent.getComponentType());

                assert transformComponent != null;

                PlayerSettings playerSettingsComponent = (PlayerSettings)componentAccessor.getComponent(ref, PlayerSettings.getComponentType());
                if (playerSettingsComponent == null) {
                    playerSettingsComponent = PlayerSettings.defaults();
                }

                Holder<EntityStore> pickupItemHolder = null;
                Item item = itemStack.getItem();
                ItemContainer itemContainer = playerComponent.getInventory().getContainerForItemPickup(item, playerSettingsComponent);
                ItemStackTransaction transaction = itemContainer.addItemStack(itemStack);
                ItemStack remainder = transaction.getRemainder();
                if (remainder != null && !remainder.isEmpty()) {
                    int quantity = itemStack.getQuantity() - remainder.getQuantity();
                    if (quantity > 0) {
                        ItemStack itemStackClone = itemStack.withQuantity(quantity);
                        playerComponent.notifyPickupItem(ref, itemStackClone, (Vector3d)null, componentAccessor);
                        if (origin != null) {
                            pickupItemHolder = ItemComponent.generatePickedUpItem(itemStackClone, origin, componentAccessor, ref);
                        }
                    }

                    dropItem(ref, remainder, componentAccessor);
                } else {
                    playerComponent.notifyPickupItem(ref, itemStack, (Vector3d)null, componentAccessor);
                    if (origin != null) {
                        pickupItemHolder = ItemComponent.generatePickedUpItem(itemStack, origin, componentAccessor, ref);
                    }
                }

                if (pickupItemHolder != null) {
                    componentAccessor.addEntity(pickupItemHolder, AddReason.SPAWN);
                }
            } else {
                SimpleItemContainer.addOrDropItemStack(componentAccessor, ref, entity.getInventory().getCombinedHotbarFirst(), itemStack);
            }

        }
    }

    @Nullable
    public static Ref<EntityStore> throwItem(@Nonnull Ref<EntityStore> ref, @Nonnull ItemStack itemStack, float throwSpeed, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        DropItemEvent.Drop event = new DropItemEvent.Drop(itemStack, throwSpeed);
        componentAccessor.invoke(ref, event);
        if (event.isCancelled()) {
            return null;
        } else {
            throwSpeed = event.getThrowSpeed();
            itemStack = event.getItemStack();
            if (!itemStack.isEmpty() && itemStack.isValid()) {
                HeadRotation headRotationComponent = (HeadRotation)componentAccessor.getComponent(ref, HeadRotation.getComponentType());

                assert headRotationComponent != null;

                Vector3f rotation = headRotationComponent.getRotation();
                Vector3d direction = Transform.getDirection(rotation.getPitch(), rotation.getYaw());
                return throwItem(ref, componentAccessor, itemStack, direction, throwSpeed);
            } else {
                //HytaleLogger.getLogger().at(Level.WARNING).log("Attempted to throw invalid item %s at %s by %s", itemStack, throwSpeed, ref.getIndex());
                return null;
            }
        }
    }

    @Nullable
    public static Ref<EntityStore> throwItem(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> store, @Nonnull ItemStack itemStack, @Nonnull Vector3d throwDirection, float throwSpeed) {
        TransformComponent transformComponent = (TransformComponent)store.getComponent(ref, TransformComponent.getComponentType());

        assert transformComponent != null;

        //assert modelComponent != null;

        Vector3d throwPosition = transformComponent.getPosition().clone();
        //Model model = modelComponent.getModel();
        //throwPosition.add((double)0.0F, .5f, (double)0.0F).add(throwDirection);
        //HytaleLogger.getLogger().atInfo().log("generateItemDrop: pos=" + throwPosition + " item=" + itemStack);
        Holder<EntityStore> itemEntityHolder = ItemComponent.generateItemDrop(store, itemStack, throwPosition, Vector3f.ZERO, 0, -.1f, 0);
        if (itemEntityHolder == null) {
            //HytaleLogger.getLogger().atInfo().log("generateItemDrop returned null for item=" + itemStack + " at " + throwPosition);
            return null;
        } else {
            ItemComponent itemComponent = (ItemComponent)itemEntityHolder.getComponent(ItemComponent.getComponentType());
            if (itemComponent != null) {
                itemComponent.setPickupDelay(20000000000f);
                itemComponent.setRemovedByPlayerPickup(false);
                try {
                    final Instant currentTime = store.getResource(WorldTimeResource.getResourceType()).getGameTime();
                    Instant despawnTime = currentTime.plusSeconds(5);
                    itemEntityHolder.ensureAndGetComponent(DespawnComponent.getComponentType()).setDespawn(despawnTime);
                    //HytaleLogger.getLogger().atInfo().log("throwItem(generateItemDrop): despawnTime=" + despawnTime);
                } catch (Exception ignored) {}
            }

            Ref<EntityStore> added = store.addEntity(itemEntityHolder, AddReason.SPAWN);
            try {
                ((PhysicsValues)itemEntityHolder.ensureAndGetComponent(PhysicsValues.getComponentType())).replaceValues(new PhysicsValues(0,0,true));
            } catch (Exception ignored) {}
            try {
                itemEntityHolder.tryRemoveComponent(BoundingBox.getComponentType());
            } catch (Exception ignored) {}
            try {
                itemEntityHolder.ensureAndGetComponent(Intangible.getComponentType());
            } catch (Exception ignored) {}

            //HytaleLogger.getLogger().atInfo().log("addEntity returned=" + added + " for item=" + itemStack);
            return added;
        }
    }

    @Nullable
    public static Ref<EntityStore> throwItem(Vector3d pos, @Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> store, @Nonnull ItemStack itemStack, @Nonnull Vector3d throwDirection, float throwSpeed) {
        TransformComponent transformComponent = (TransformComponent)store.getComponent(ref, TransformComponent.getComponentType());

        assert transformComponent != null;

        //assert modelComponent != null;

        Vector3d throwPosition = transformComponent.getPosition().clone();
        //Model model = modelComponent.getModel();
        //throwPosition.add((double)0.0F, .5f, (double)0.0F).add(throwDirection);
        //HytaleLogger.getLogger().atInfo().log("generateItemDrop (pos overload): pos=" + new Vector3d(pos.x+.5,pos.y +1.5,pos.z+.5) + " item=" + itemStack);
        Holder<EntityStore> itemEntityHolder = ItemComponent.generateItemDrop(store, itemStack, new Vector3d(pos.x+.5,pos.y +1.5,pos.z+.5), Vector3f.ZERO, 0, -1, 0);
        if (itemEntityHolder == null) {
            //HytaleLogger.getLogger().atInfo().log("generateItemDrop returned null (pos overload) for item=" + itemStack);
            return null;
        } else {
            ItemComponent itemComponent = (ItemComponent)itemEntityHolder.getComponent(ItemComponent.getComponentType());
            if (itemComponent != null) {

                itemComponent.setPickupDelay(1400000);
                itemComponent.setRemovedByPlayerPickup(false);
                itemComponent.computeDynamicLight();
                PhysicsValues pv = new PhysicsValues(0,0,true);

                ((PhysicsValues)itemEntityHolder.ensureAndGetComponent(PhysicsValues.getComponentType())).replaceValues(pv);
                ((Velocity)itemEntityHolder.ensureAndGetComponent(Velocity.getComponentType())).set(-1, 0, 0);
                try {
                    final Instant currentTime = store.getResource(WorldTimeResource.getResourceType()).getGameTime();
                    Instant despawnTime = currentTime.plusSeconds(5);
                    itemEntityHolder.ensureAndGetComponent(DespawnComponent.getComponentType()).setDespawn(despawnTime);
                    //HytaleLogger.getLogger().atInfo().log("throwItem(pos overload): despawnTime=" + despawnTime);
                } catch (Exception ignored) {}
            }


            Ref<EntityStore> added = store.addEntity(itemEntityHolder, AddReason.SPAWN);
            try {
                itemEntityHolder.tryRemoveComponent(BoundingBox.getComponentType());
            } catch (Exception ignored) {}
            try {
                itemEntityHolder.ensureAndGetComponent(Intangible.getComponentType());
            } catch (Exception ignored) {}

            //HytaleLogger.getLogger().atInfo().log("addEntity returned=" + added + " (pos overload) for item=" + itemStack);
            return added;
        }
    }
    @Nullable
    public static Ref<EntityStore> throwItem(String side, Vector3d pos, @Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> store, @Nonnull ItemStack itemStack, @Nonnull Vector3d throwDirection, float throwSpeed) {
        TransformComponent transformComponent = (TransformComponent)store.getComponent(ref, TransformComponent.getComponentType());

        assert transformComponent != null;

        //assert modelComponent != null;

        //Vector3d throwPosition = transformComponent.getPosition().clone();
        //Model model = modelComponent.getModel();
        //throwPosition.add((double)0.0F, .5f, (double)0.0F).add(throwDirection);
        Vector3d spawnPos = new Vector3d(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5);
        Holder<EntityStore> itemEntityHolder = ItemComponent.generateItemDrop(store, itemStack, spawnPos, Vector3f.ZERO, 0, 0, 0);
        if (itemEntityHolder == null) {
            return null;
        } else {
            ItemComponent itemComponent = (ItemComponent)itemEntityHolder.getComponent(ItemComponent.getComponentType());
            if (itemComponent != null) {
                final Instant currentTime = store.getResource(WorldTimeResource.getResourceType()).getGameTime();
                // Despawn visuals after 5 seconds
                Instant despawnTime = currentTime.plusSeconds(5);
                itemEntityHolder.ensureAndGetComponent(DespawnComponent.getComponentType()).setDespawn(despawnTime);
                //HytaleLogger.getLogger().atInfo().log("throwItem(side overload): despawnTime=" + despawnTime);
                itemComponent.setPickupDelay(100000000);
                itemComponent.setRemovedByPlayerPickup(false);
                itemComponent.computeDynamicLight();
                PhysicsValues pv = new PhysicsValues(0,0,true);
                ((PhysicsValues)itemEntityHolder.ensureAndGetComponent(PhysicsValues.getComponentType())).replaceValues(pv);
                // remove physics component as in Ev0Libs so visuals don't collide
                try { itemEntityHolder.removeComponent(PhysicsValues.getComponentType()); } catch (Exception ignored) {}
                //HytaleLogger.getLogger().atInfo().log(side);

            }
            // Normalize side comparisons and set a sensible velocity for visuals (match Ev0Libs)
            try {
                itemEntityHolder.tryRemoveComponent(BoundingBox.getComponentType());
            } catch (Exception ignored) {}
            try {
                itemEntityHolder.ensureAndGetComponent(Intangible.getComponentType());
            } catch (Exception ignored) {}
            if (side != null && side.equalsIgnoreCase("Up")) {
                (itemEntityHolder.ensureAndGetComponent(Velocity.getComponentType())).set(0, -1, 0);
            } else if (side != null && side.equalsIgnoreCase("Down")) {
                (itemEntityHolder.ensureAndGetComponent(Velocity.getComponentType())).set(0, 1, 0);
            } else if (side != null && side.equalsIgnoreCase("North")) {
                (itemEntityHolder.ensureAndGetComponent(Velocity.getComponentType())).set(0, 0, 1);
            } else if (side != null && side.equalsIgnoreCase("South")) {
                (itemEntityHolder.ensureAndGetComponent(Velocity.getComponentType())).set(0, 0, -1);
            } else if (side != null && side.equalsIgnoreCase("East")) {
                (itemEntityHolder.ensureAndGetComponent(Velocity.getComponentType())).set(-1, 0, 0);
            } else if (side != null && side.equalsIgnoreCase("West")) {
                (itemEntityHolder.ensureAndGetComponent(Velocity.getComponentType())).set(1, 0, 0);
            } else {
                (itemEntityHolder.ensureAndGetComponent(Velocity.getComponentType())).set(0, 1, 0);
            }

            Ref<EntityStore> added = store.addEntity(itemEntityHolder, AddReason.SPAWN);
            //HytaleLogger.getLogger().atInfo().log("throwItem(side overload): addEntity returned=" + added + " for side=" + side + " spawnPos=" + spawnPos + " item=" + itemStack);
            return added;
        }
    }
    public static Ref<EntityStore> throwItem(String blockId, String side, Vector3d pos, @Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> store, @Nonnull ItemStack itemStack, @Nonnull Vector3d throwDirection, float throwSpeed) {
        TransformComponent transformComponent = (TransformComponent)store.getComponent(ref, TransformComponent.getComponentType());

        assert transformComponent != null;

        //assert modelComponent != null;

        Vector3d throwPosition = transformComponent.getPosition().clone();
        throwPosition.add((double)0.5F, .25, (double).5F);

        // Use block-centered spawn position like Ev0Libs
        Vector3d spawnPos = new Vector3d(pos.x + 0.5, pos.y + 0.2, pos.z + 0.5);
        Holder<EntityStore> itemEntityHolder = ItemComponent.generateItemDrop(store, itemStack, spawnPos, Vector3f.ZERO, 0, -1, 0);
        if (itemEntityHolder == null) {
            return null;
        } else {

            ItemComponent itemComponent = (ItemComponent)itemEntityHolder.getComponent(ItemComponent.getComponentType());
            if (itemComponent != null) {
                itemEntityHolder.ensureAndGetComponent(EntityScaleComponent.getComponentType()).setScale(.5f);
                itemComponent.setPickupDelay(100000000);
                itemComponent.setRemovedByPlayerPickup(false);
                itemComponent.computeDynamicLight();
                PhysicsValues pv = new PhysicsValues(0,0,true);
                ((PhysicsValues)itemEntityHolder.ensureAndGetComponent(PhysicsValues.getComponentType())).replaceValues(pv);
                // remove physics component as in Ev0Libs so visuals don't collide
                try { itemEntityHolder.removeComponent(PhysicsValues.getComponentType()); } catch (Exception ignored) {}
                
            }

            // Normalize side comparisons and set sensible velocity for visuals (match Ev0Libs)

            if (side != null && side.equalsIgnoreCase("Up")) {
                ((Velocity)itemEntityHolder.ensureAndGetComponent(Velocity.getComponentType())).set(0, -1, 0);
            } else if (side != null && side.equalsIgnoreCase("Down")) {
                ((Velocity)itemEntityHolder.ensureAndGetComponent(Velocity.getComponentType())).set(0, 1, 0);
            } else if (side != null && side.equalsIgnoreCase("North")) {
                ((Velocity)itemEntityHolder.ensureAndGetComponent(Velocity.getComponentType())).set(0, 0, 1);
            } else if (side != null && side.equalsIgnoreCase("South")) {
                ((Velocity)itemEntityHolder.ensureAndGetComponent(Velocity.getComponentType())).set(0, 0, -1);
            } else if (side != null && side.equalsIgnoreCase("East")) {
                ((Velocity)itemEntityHolder.ensureAndGetComponent(Velocity.getComponentType())).set(-1, 0, 0);
            } else if (side != null && side.equalsIgnoreCase("West")) {
                ((Velocity)itemEntityHolder.ensureAndGetComponent(Velocity.getComponentType())).set(1, 0, 0);
            } else {
                ((Velocity)itemEntityHolder.ensureAndGetComponent(Velocity.getComponentType())).set(0, 1, 0);
            }

            Ref<EntityStore> added = store.addEntity(itemEntityHolder, AddReason.SPAWN);
            //HytaleLogger.getLogger().atInfo().log("throwItem(blockId overload): addEntity returned=" + added + " spawnPos=" + spawnPos + " item=" + itemStack);
            return added;
        }
    }
    public static Ref<EntityStore> throwItem( @Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> store, @Nonnull ItemStack itemStack, @Nonnull Vector3d throwDirection, float throwSpeed, Vector3d pos) {
        TransformComponent transformComponent = (TransformComponent)store.getComponent(ref, TransformComponent.getComponentType());

        assert transformComponent != null;

        //assert modelComponent != null;

        Vector3d throwPosition = transformComponent.getPosition().clone();
        //Model model = modelComponent.getModel();
        throwPosition.add((double)0.0F, 0.0f, (double).5F);
        transformComponent.setPosition(new Vector3d(pos.x, pos.y-.5, pos.z));
        Holder<EntityStore> itemEntityHolder = ItemComponent.generateItemDrop(store, itemStack, pos, Vector3f.ZERO, 0, -.1f * throwSpeed, 0);
        if (itemEntityHolder == null) {
            return null;
        } else {
            ItemComponent itemComponent = (ItemComponent)itemEntityHolder.getComponent(ItemComponent.getComponentType());
            if (itemComponent != null) {
                itemComponent.setRemovedByPlayerPickup(false);
                itemComponent.setPickupDelay(2f);
                ((Velocity)itemEntityHolder.ensureAndGetComponent(Velocity.getComponentType())).set(0, 0, 0);

                try {
                    final Instant currentTime = store.getResource(WorldTimeResource.getResourceType()).getGameTime();
                    Instant despawnTime = currentTime.plusSeconds(5);
                    itemEntityHolder.ensureAndGetComponent(DespawnComponent.getComponentType()).setDespawn(despawnTime);
                    //HytaleLogger.getLogger().atInfo().log("throwItem(ref,pos overload): despawnTime=" + despawnTime);
                } catch (Exception ignored) {}

                TransformComponent tn = new TransformComponent(pos, Vector3f.ZERO);

            }

            return store.addEntity(itemEntityHolder, AddReason.SPAWN);
        }
    }

    @Nullable
    public static Ref<EntityStore> dropItem(@Nonnull Ref<EntityStore> ref, @Nonnull ItemStack itemStack, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        return throwItem(ref, itemStack, 1.0F, componentAccessor);
    }
}

