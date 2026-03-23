/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.component.AddReason
 *  com.hypixel.hytale.component.ComponentAccessor
 *  com.hypixel.hytale.component.ComponentType
 *  com.hypixel.hytale.component.Holder
 *  com.hypixel.hytale.component.Ref
 *  com.hypixel.hytale.component.system.EcsEvent
 *  com.hypixel.hytale.math.vector.Transform
 *  com.hypixel.hytale.math.vector.Vector3d
 *  com.hypixel.hytale.math.vector.Vector3f
 *  com.hypixel.hytale.server.core.asset.type.item.config.Item
 *  com.hypixel.hytale.server.core.entity.EntityUtils
 *  com.hypixel.hytale.server.core.entity.LivingEntity
 *  com.hypixel.hytale.server.core.entity.entities.Player
 *  com.hypixel.hytale.server.core.event.events.ecs.DropItemEvent$Drop
 *  com.hypixel.hytale.server.core.event.events.ecs.InteractivelyPickupItemEvent
 *  com.hypixel.hytale.server.core.inventory.ItemStack
 *  com.hypixel.hytale.server.core.inventory.container.ItemContainer
 *  com.hypixel.hytale.server.core.inventory.container.SimpleItemContainer
 *  com.hypixel.hytale.server.core.inventory.transaction.ItemStackTransaction
 *  com.hypixel.hytale.server.core.modules.entity.DespawnComponent
 *  com.hypixel.hytale.server.core.modules.entity.EntityModule
 *  com.hypixel.hytale.server.core.modules.entity.component.BoundingBox
 *  com.hypixel.hytale.server.core.modules.entity.component.EntityScaleComponent
 *  com.hypixel.hytale.server.core.modules.entity.component.HeadRotation
 *  com.hypixel.hytale.server.core.modules.entity.component.Intangible
 *  com.hypixel.hytale.server.core.modules.entity.component.TransformComponent
 *  com.hypixel.hytale.server.core.modules.entity.item.ItemComponent
 *  com.hypixel.hytale.server.core.modules.entity.player.PlayerSettings
 *  com.hypixel.hytale.server.core.modules.physics.component.PhysicsValues
 *  com.hypixel.hytale.server.core.modules.physics.component.Velocity
 *  com.hypixel.hytale.server.core.modules.time.WorldTimeResource
 *  com.hypixel.hytale.server.core.universe.world.storage.EntityStore
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package org.Ev0Mods.plugin.api.util;

import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.system.EcsEvent;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.entity.EntityUtils;
import com.hypixel.hytale.server.core.entity.LivingEntity;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.ecs.DropItemEvent;
import com.hypixel.hytale.server.core.event.events.ecs.InteractivelyPickupItemEvent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.inventory.container.SimpleItemContainer;
import com.hypixel.hytale.server.core.inventory.transaction.ItemStackTransaction;
import com.hypixel.hytale.server.core.modules.entity.DespawnComponent;
import com.hypixel.hytale.server.core.modules.entity.EntityModule;
import com.hypixel.hytale.server.core.modules.entity.component.BoundingBox;
import com.hypixel.hytale.server.core.modules.entity.component.EntityScaleComponent;
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import com.hypixel.hytale.server.core.modules.entity.component.Intangible;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.item.ItemComponent;
import com.hypixel.hytale.server.core.modules.entity.player.PlayerSettings;
import com.hypixel.hytale.server.core.modules.physics.component.PhysicsValues;
import com.hypixel.hytale.server.core.modules.physics.component.Velocity;
import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.time.Instant;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ItemUtilsExtended {
    private static double centerCoord(double c) {
        double frac = c - Math.floor(c);
        if (Math.abs(frac - 0.5) < 0.01) {
            return c;
        }
        return c + 0.5;
    }

    @Nonnull
    public static ComponentType<EntityStore, ItemComponent> getComponentItemType() {
        return EntityModule.get().getItemComponentType();
    }

    public static void interactivelyPickupItem(@Nonnull Ref<EntityStore> ref, @Nonnull ItemStack itemStack, @Nullable Vector3d origin, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        LivingEntity entity = (LivingEntity)EntityUtils.getEntity(ref, componentAccessor);
        InteractivelyPickupItemEvent event = new InteractivelyPickupItemEvent(itemStack);
        componentAccessor.invoke(ref, (EcsEvent)event);
        if (event.isCancelled()) {
            ItemUtilsExtended.dropItem(ref, itemStack, componentAccessor);
        } else {
            Player playerComponent = (Player)componentAccessor.getComponent(ref, Player.getComponentType());
            if (playerComponent != null) {
                TransformComponent transformComponent = (TransformComponent)componentAccessor.getComponent(ref, TransformComponent.getComponentType());
                assert (transformComponent != null);
                PlayerSettings playerSettingsComponent = (PlayerSettings)componentAccessor.getComponent(ref, PlayerSettings.getComponentType());
                if (playerSettingsComponent == null) {
                    playerSettingsComponent = PlayerSettings.defaults();
                }
                Holder pickupItemHolder = null;
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
                            pickupItemHolder = ItemComponent.generatePickedUpItem((ItemStack)itemStackClone, (Vector3d)origin, componentAccessor, ref);
                        }
                    }
                    ItemUtilsExtended.dropItem(ref, remainder, componentAccessor);
                } else {
                    playerComponent.notifyPickupItem(ref, itemStack, (Vector3d)null, componentAccessor);
                    if (origin != null) {
                        pickupItemHolder = ItemComponent.generatePickedUpItem((ItemStack)itemStack, (Vector3d)origin, componentAccessor, ref);
                    }
                }
                if (pickupItemHolder != null) {
                    componentAccessor.addEntity(pickupItemHolder, AddReason.SPAWN);
                }
            } else {
                SimpleItemContainer.addOrDropItemStack(componentAccessor, ref, (ItemContainer)entity.getInventory().getCombinedHotbarFirst(), (ItemStack)itemStack);
            }
        }
    }

    @Nullable
    public static Ref<EntityStore> throwItem(@Nonnull Ref<EntityStore> ref, @Nonnull ItemStack itemStack, float throwSpeed, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        DropItemEvent.Drop event = new DropItemEvent.Drop(itemStack, throwSpeed);
        componentAccessor.invoke(ref, (EcsEvent)event);
        if (event.isCancelled()) {
            return null;
        }
        throwSpeed = event.getThrowSpeed();
        itemStack = event.getItemStack();
        if (!itemStack.isEmpty() && itemStack.isValid()) {
            HeadRotation headRotationComponent = (HeadRotation)componentAccessor.getComponent(ref, HeadRotation.getComponentType());
            assert (headRotationComponent != null);
            Vector3f rotation = headRotationComponent.getRotation();
            Vector3d direction = Transform.getDirection((float)rotation.getPitch(), (float)rotation.getYaw());
            return ItemUtilsExtended.throwItem(ref, componentAccessor, itemStack, direction, throwSpeed);
        }
        return null;
    }

    @Nullable
    public static Ref<EntityStore> throwItem(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> store, @Nonnull ItemStack itemStack, @Nonnull Vector3d throwDirection, float throwSpeed) {
        TransformComponent transformComponent = (TransformComponent)store.getComponent(ref, TransformComponent.getComponentType());
        assert (transformComponent != null);
        Vector3d throwPosition = transformComponent.getPosition().clone();
        Holder itemEntityHolder = ItemComponent.generateItemDrop(store, (ItemStack)itemStack, (Vector3d)throwPosition, (Vector3f)Vector3f.ZERO, (float)0.0f, (float)-0.1f, (float)0.0f);
        if (itemEntityHolder == null) {
            return null;
        }
        ItemComponent itemComponent = (ItemComponent)itemEntityHolder.getComponent(ItemComponent.getComponentType());
        if (itemComponent != null) {
            itemComponent.setPickupDelay(2.0E10f);
            itemComponent.setRemovedByPlayerPickup(false);
            try {
                Instant currentTime = ((WorldTimeResource)store.getResource(WorldTimeResource.getResourceType())).getGameTime();
                Instant despawnTime = currentTime.plusSeconds(5L);
                ((DespawnComponent)itemEntityHolder.ensureAndGetComponent(DespawnComponent.getComponentType())).setDespawn(despawnTime);
            }
            catch (Exception currentTime) {
                // empty catch block
            }
        }
        Ref added = store.addEntity(itemEntityHolder, AddReason.SPAWN);
        try {
            ((PhysicsValues)itemEntityHolder.ensureAndGetComponent(PhysicsValues.getComponentType())).replaceValues(new PhysicsValues(0.0, 0.0, true));
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            itemEntityHolder.tryRemoveComponent(BoundingBox.getComponentType());
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            itemEntityHolder.ensureAndGetComponent(Intangible.getComponentType());
        }
        catch (Exception exception) {
            // empty catch block
        }
        return added;
    }

    @Nullable
    public static Ref<EntityStore> throwItem(Vector3d pos, @Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> store, @Nonnull ItemStack itemStack, @Nonnull Vector3d throwDirection, float throwSpeed) {
        TransformComponent transformComponent = (TransformComponent)store.getComponent(ref, TransformComponent.getComponentType());
        assert (transformComponent != null);
        Vector3d throwPosition = transformComponent.getPosition().clone();
        Holder itemEntityHolder = ItemComponent.generateItemDrop(store, (ItemStack)itemStack, (Vector3d)new Vector3d(pos.x + 0.5, pos.y + 1.5, pos.z + 0.5), (Vector3f)Vector3f.ZERO, (float)0.0f, (float)-1.0f, (float)0.0f);
        if (itemEntityHolder == null) {
            return null;
        }
        ItemComponent itemComponent = (ItemComponent)itemEntityHolder.getComponent(ItemComponent.getComponentType());
        if (itemComponent != null) {
            itemComponent.setPickupDelay(1400000.0f);
            itemComponent.setRemovedByPlayerPickup(false);
            itemComponent.computeDynamicLight();
            PhysicsValues pv = new PhysicsValues(0.0, 0.0, true);
            ((PhysicsValues)itemEntityHolder.ensureAndGetComponent(PhysicsValues.getComponentType())).replaceValues(pv);
            ((Velocity)itemEntityHolder.ensureAndGetComponent(Velocity.getComponentType())).set(-1.0, 0.0, 0.0);
            try {
                Instant currentTime = ((WorldTimeResource)store.getResource(WorldTimeResource.getResourceType())).getGameTime();
                Instant despawnTime = currentTime.plusSeconds(5L);
                ((DespawnComponent)itemEntityHolder.ensureAndGetComponent(DespawnComponent.getComponentType())).setDespawn(despawnTime);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        Ref added = store.addEntity(itemEntityHolder, AddReason.SPAWN);
        try {
            itemEntityHolder.tryRemoveComponent(BoundingBox.getComponentType());
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            itemEntityHolder.ensureAndGetComponent(Intangible.getComponentType());
        }
        catch (Exception exception) {
            // empty catch block
        }
        return added;
    }

    @Nullable
    public static Ref<EntityStore> throwItem(String side, Vector3d pos, @Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> store, @Nonnull ItemStack itemStack, @Nonnull Vector3d throwDirection, float throwSpeed) {
        TransformComponent transformComponent = (TransformComponent)store.getComponent(ref, TransformComponent.getComponentType());
        assert (transformComponent != null);
        Vector3d spawnPos = new Vector3d(pos.x, pos.y + 0.5, pos.z + 0.5);
        Holder itemEntityHolder = ItemComponent.generateItemDrop(store, (ItemStack)itemStack, (Vector3d)spawnPos, (Vector3f)Vector3f.ZERO, (float)0.0f, (float)0.0f, (float)0.0f);
        if (itemEntityHolder == null) {
            return null;
        }
        ItemComponent itemComponent = (ItemComponent)itemEntityHolder.getComponent(ItemComponent.getComponentType());
        if (itemComponent != null) {
            Instant currentTime = ((WorldTimeResource)store.getResource(WorldTimeResource.getResourceType())).getGameTime();
            Instant despawnTime = currentTime.plusSeconds(5L);
            ((DespawnComponent)itemEntityHolder.ensureAndGetComponent(DespawnComponent.getComponentType())).setDespawn(despawnTime);
            itemComponent.setPickupDelay(1.0E8f);
            itemComponent.setRemovedByPlayerPickup(false);
            itemComponent.computeDynamicLight();
            PhysicsValues pv = new PhysicsValues(0.0, 0.0, true);
            ((PhysicsValues)itemEntityHolder.ensureAndGetComponent(PhysicsValues.getComponentType())).replaceValues(pv);
            try {
                itemEntityHolder.removeComponent(PhysicsValues.getComponentType());
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        try {
            itemEntityHolder.tryRemoveComponent(BoundingBox.getComponentType());
        }
        catch (Exception currentTime) {
            // empty catch block
        }
        try {
            itemEntityHolder.ensureAndGetComponent(Intangible.getComponentType());
        }
        catch (Exception currentTime) {
            // empty catch block
        }
        if (side != null && side.equalsIgnoreCase("Up")) {
            ((Velocity)itemEntityHolder.ensureAndGetComponent(Velocity.getComponentType())).set(0.0, -1.0, 0.0);
        } else if (side != null && side.equalsIgnoreCase("Down")) {
            ((Velocity)itemEntityHolder.ensureAndGetComponent(Velocity.getComponentType())).set(0.0, 1.0, 0.0);
        } else if (side != null && side.equalsIgnoreCase("North")) {
            ((Velocity)itemEntityHolder.ensureAndGetComponent(Velocity.getComponentType())).set(0.0, 0.0, 1.0);
        } else if (side != null && side.equalsIgnoreCase("South")) {
            ((Velocity)itemEntityHolder.ensureAndGetComponent(Velocity.getComponentType())).set(0.0, 0.0, -1.0);
        } else if (side != null && side.equalsIgnoreCase("East")) {
            ((Velocity)itemEntityHolder.ensureAndGetComponent(Velocity.getComponentType())).set(-1.0, 0.0, 0.0);
        } else if (side != null && side.equalsIgnoreCase("West")) {
            ((Velocity)itemEntityHolder.ensureAndGetComponent(Velocity.getComponentType())).set(1.0, 0.0, 0.0);
        } else {
            ((Velocity)itemEntityHolder.ensureAndGetComponent(Velocity.getComponentType())).set(0.0, 1.0, 0.0);
        }
        Ref added = store.addEntity(itemEntityHolder, AddReason.SPAWN);
        return added;
    }

    public static Ref<EntityStore> throwItem(String blockId, String side, Vector3d pos, @Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> store, @Nonnull ItemStack itemStack, @Nonnull Vector3d throwDirection, float throwSpeed) {
        Vector3d spawnPos = new Vector3d(pos.x + 0.5, pos.y + 0.2, pos.z + 0.5);
        Holder itemEntityHolder = ItemComponent.generateItemDrop(store, (ItemStack)itemStack, (Vector3d)spawnPos, (Vector3f)Vector3f.ZERO, (float)0.0f, (float)0.0f, (float)0.0f);
        if (itemEntityHolder == null) {
            return null;
        }
        ItemComponent itemComponent = (ItemComponent)itemEntityHolder.getComponent(ItemComponent.getComponentType());
        if (itemComponent != null) {
            ((EntityScaleComponent)itemEntityHolder.ensureAndGetComponent(EntityScaleComponent.getComponentType())).setScale(0.5f);
            itemComponent.setPickupDelay(1.0E8f);
            itemComponent.setRemovedByPlayerPickup(false);
            itemComponent.computeDynamicLight();
            PhysicsValues pv = new PhysicsValues(0.0, 0.0, true);
            ((PhysicsValues)itemEntityHolder.ensureAndGetComponent(PhysicsValues.getComponentType())).replaceValues(pv);
            try {
                itemEntityHolder.removeComponent(PhysicsValues.getComponentType());
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        if (side != null && side.equalsIgnoreCase("Up")) {
            ((Velocity)itemEntityHolder.ensureAndGetComponent(Velocity.getComponentType())).set(0.0, -1.0, 0.0);
        } else if (side != null && side.equalsIgnoreCase("Down")) {
            ((Velocity)itemEntityHolder.ensureAndGetComponent(Velocity.getComponentType())).set(0.0, 1.0, 0.0);
        } else if (side != null && side.equalsIgnoreCase("North")) {
            ((Velocity)itemEntityHolder.ensureAndGetComponent(Velocity.getComponentType())).set(0.0, 0.0, 1.0);
        } else if (side != null && side.equalsIgnoreCase("South")) {
            ((Velocity)itemEntityHolder.ensureAndGetComponent(Velocity.getComponentType())).set(0.0, 0.0, -1.0);
        } else if (side != null && side.equalsIgnoreCase("East")) {
            ((Velocity)itemEntityHolder.ensureAndGetComponent(Velocity.getComponentType())).set(-1.0, 0.0, 0.0);
        } else if (side != null && side.equalsIgnoreCase("West")) {
            ((Velocity)itemEntityHolder.ensureAndGetComponent(Velocity.getComponentType())).set(1.0, 0.0, 0.0);
        } else {
            ((Velocity)itemEntityHolder.ensureAndGetComponent(Velocity.getComponentType())).set(0.0, 1.0, 0.0);
        }
        Ref added = store.addEntity(itemEntityHolder, AddReason.SPAWN);
        try {
            itemEntityHolder.tryRemoveComponent(BoundingBox.getComponentType());
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            itemEntityHolder.ensureAndGetComponent(Intangible.getComponentType());
        }
        catch (Exception exception) {
            // empty catch block
        }
        return added;
    }

    public static Ref<EntityStore> throwItem(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> store, @Nonnull ItemStack itemStack, @Nonnull Vector3d throwDirection, float throwSpeed, Vector3d pos) {
        TransformComponent transformComponent = (TransformComponent)store.getComponent(ref, TransformComponent.getComponentType());
        assert (transformComponent != null);
        Vector3d throwPosition = transformComponent.getPosition().clone();
        throwPosition.add(0.0, 0.0, 0.5);
        transformComponent.setPosition(new Vector3d(pos.x, pos.y - 0.5, pos.z));
        Holder itemEntityHolder = ItemComponent.generateItemDrop(store, (ItemStack)itemStack, (Vector3d)pos, (Vector3f)Vector3f.ZERO, (float)0.0f, (float)(-0.1f * throwSpeed), (float)0.0f);
        if (itemEntityHolder == null) {
            return null;
        }
        ItemComponent itemComponent = (ItemComponent)itemEntityHolder.getComponent(ItemComponent.getComponentType());
        if (itemComponent != null) {
            itemComponent.setRemovedByPlayerPickup(false);
            itemComponent.setPickupDelay(2.0f);
            ((Velocity)itemEntityHolder.ensureAndGetComponent(Velocity.getComponentType())).set(0.0, 0.0, 0.0);
            try {
                Instant currentTime = ((WorldTimeResource)store.getResource(WorldTimeResource.getResourceType())).getGameTime();
                Instant despawnTime = currentTime.plusSeconds(5L);
                ((DespawnComponent)itemEntityHolder.ensureAndGetComponent(DespawnComponent.getComponentType())).setDespawn(despawnTime);
            }
            catch (Exception exception) {
                // empty catch block
            }
            TransformComponent transformComponent2 = new TransformComponent(pos, Vector3f.ZERO);
        }
        return store.addEntity(itemEntityHolder, AddReason.SPAWN);
    }

    @Nullable
    public static Ref<EntityStore> dropItem(@Nonnull Ref<EntityStore> ref, @Nonnull ItemStack itemStack, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        return ItemUtilsExtended.throwItem(ref, itemStack, 1.0f, componentAccessor);
    }
}

