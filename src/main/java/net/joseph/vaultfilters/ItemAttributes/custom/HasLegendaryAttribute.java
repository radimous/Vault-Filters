package net.joseph.vaultfilters.ItemAttributes.custom;

import com.simibubi.create.content.logistics.filter.ItemAttribute;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.data.GearDataCache;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import net.joseph.vaultfilters.IVFGearDataCache;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class HasLegendaryAttribute implements ItemAttribute {

    public static void register() {
        ItemAttribute.register(new HasLegendaryAttribute("dummy"));
    }
    String legendary;

    public HasLegendaryAttribute(String legendary) { this.legendary = legendary;}

    public static boolean hasLegendary(ItemStack itemStack) {
        GearDataCache gearDataCache = GearDataCache.of(itemStack);
        return ((IVFGearDataCache) gearDataCache).hasLegendaryAttribute();
    }
    @Override
    public boolean appliesTo(ItemStack itemStack) {

        if (itemStack.getItem() instanceof VaultGearItem) {
            return hasLegendary(itemStack);
        }

        return false;
    }

    @Override
    public List<ItemAttribute> listAttributesOf(ItemStack itemStack) {

       List<ItemAttribute> atts = new ArrayList<>();
       if (itemStack.getItem() instanceof VaultGearItem) {
           if (hasLegendary(itemStack)) {
               atts.add(new HasLegendaryAttribute("legendary"));
           }
       }
        return atts;
    }

    @Override
    public String getTranslationKey() {
        return "has_legendary";
    }
    @Override
    public Object[] getTranslationParameters() {
        return new Object[]{legendary};
    }
    @Override
    public void writeNBT(CompoundTag nbt) {
        nbt.putString("legendary", this.legendary);
    }

    @Override
    public ItemAttribute readNBT(CompoundTag nbt) {
        return new HasLegendaryAttribute(nbt.getString("legendary"));
    }


}