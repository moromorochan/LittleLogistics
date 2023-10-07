package dev.murad.shipping.entity.custom.train.wagon;

import com.simibubi.create.foundation.item.ItemHandlerWrapper;
import dev.murad.shipping.setup.ModEntityTypes;
import dev.murad.shipping.setup.ModItems;
import dev.murad.shipping.util.InventoryUtils;
import dev.murad.shipping.util.ItemHandlerVanillaContainerWrapper;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.stream.IntStream;

public class ChestCarEntity extends AbstractWagonEntity implements ItemHandlerVanillaContainerWrapper, MenuProvider {
    protected final ItemStackHandler itemHandler = createHandler();
    protected final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> itemHandler);
    public ChestCarEntity(EntityType<ChestCarEntity> p_38087_, Level p_38088_) {
        super(p_38087_, p_38088_);
    }

    public ChestCarEntity(Level level, Double aDouble, Double aDouble1, Double aDouble2) {
        super(ModEntityTypes.CHEST_CAR.get(), level, aDouble, aDouble1, aDouble2);

    }

    @Override
    public void remove(RemovalReason r) {
        if (!this.level().isClientSide) {
            Containers.dropContents(this.level(), this, this);
        }
        super.remove(r);
    }

    private ItemStackHandler createHandler() {
        return new ItemStackHandler(27);
    }

    @Override
    public ItemStack getPickResult() {
        return new ItemStack(ModItems.CHEST_CAR.get());
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand){
        if(!this.level().isClientSide){
            player.openMenu(this);
        }
        return InteractionResult.CONSUME;
    }

    @Nullable
    public AbstractContainerMenu createMenu(int pContainerId, @NotNull Inventory pInventory, Player pPlayer) {
        if (pPlayer.isSpectator()) {
            return null;
        } else {
            return ChestMenu.threeRows(pContainerId, pInventory, this);
        }
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag t) {
        super.addAdditionalSaveData(t);
        t.put("inv", itemHandler.serializeNBT());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag t) {
        super.readAdditionalSaveData(t);
        itemHandler.deserializeNBT(t.getCompound("inv"));
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return handler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public ItemStackHandler getRawHandler() {
        return itemHandler;
    }

    @Override
    public boolean stillValid(@NotNull Player pPlayer) {
        if (this.isRemoved()) {
            return false;
        } else {
            return !(this.distanceToSqr(pPlayer) > 64.0D);
        }
    }
}
