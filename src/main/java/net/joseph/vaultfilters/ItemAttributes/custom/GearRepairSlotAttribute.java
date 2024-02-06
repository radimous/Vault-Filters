package net.joseph.vaultfilters.ItemAttributes.custom;

import com.simibubi.create.content.logistics.filter.ItemAttribute;
import iskallia.vault.gear.data.GearDataCache;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.item.tool.JewelItem;
import net.joseph.vaultfilters.IVFGearDataCache;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GearRepairSlotAttribute implements ItemAttribute {

    public static void register() {
        ItemAttribute.register(new GearRepairSlotAttribute("0"));
    }
    String repair;

    public GearRepairSlotAttribute(String repair) {
        this.repair = repair;
    }


    @Override
    public boolean appliesTo(ItemStack itemStack) {

        if (itemStack.getItem() instanceof VaultGearItem && !(itemStack.getItem() instanceof JewelItem)) {
            int repairSlots = ((IVFGearDataCache)GearDataCache.of(itemStack)).vaultfilters$getRepairSlots();
            int usedRepairSlots = ((IVFGearDataCache)GearDataCache.of(itemStack)).vaultfilters$getUsedRepairSlots();
            return repairSlots - usedRepairSlots >= Integer.parseInt(repair);
        }

        return false;
    }

    @Override
    public List<ItemAttribute> listAttributesOf(ItemStack itemStack) {

        List<ItemAttribute> atts = new ArrayList<>();
       if (itemStack.getItem() instanceof VaultGearItem && !(itemStack.getItem() instanceof JewelItem)) {
           int repairSlots = ((IVFGearDataCache)GearDataCache.of(itemStack)).vaultfilters$getRepairSlots();
           int usedRepairSlots = ((IVFGearDataCache)GearDataCache.of(itemStack)).vaultfilters$getUsedRepairSlots();
           atts.add(new GearRepairSlotAttribute(String.valueOf(repairSlots - usedRepairSlots)));
       }
        return atts;
    }

    @Override
    public String getTranslationKey() {
        return "repair";
    }

    @Override
    public Object[] getTranslationParameters() {
        return new Object[]{String.valueOf(repair)};
    }

    @Override
    public void writeNBT(CompoundTag nbt) {
        nbt.putString("repair", this.repair);
    }

    @Override
    public ItemAttribute readNBT(CompoundTag nbt) {
        return new GearRepairSlotAttribute(nbt.getString("repair"));
    }
}