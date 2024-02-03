package net.joseph.vaultfilters.ItemAttributes.custom;

import com.simibubi.create.content.logistics.filter.ItemAttribute;
import iskallia.vault.gear.VaultGearState;
import iskallia.vault.gear.data.GearDataCache;
import iskallia.vault.item.gear.CharmItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static iskallia.vault.item.gear.CharmItem.*;

public class CharmAffinityAttribute implements ItemAttribute {

    public static void register() {
        ItemAttribute.register(new CharmAffinityAttribute("0"));
    }
    String affinity;

    public CharmAffinityAttribute(String affinity) {
        this.affinity = affinity;
    }
    public static int getCharmAffinity(ItemStack itemStack) {
        return Math.round(getValue(itemStack) * 100.0F);
    }

    @Override
    public boolean appliesTo(ItemStack itemStack) {

        if (itemStack.getItem() instanceof CharmItem && GearDataCache.of(itemStack).getState() == VaultGearState.IDENTIFIED) {
            return (getCharmAffinity(itemStack) >= Integer.parseInt(this.affinity));
        }

        return false;
    }

    @Override
    public List<ItemAttribute> listAttributesOf(ItemStack itemStack) {

        List<ItemAttribute> atts = new ArrayList<>();
       if (itemStack.getItem() instanceof CharmItem && GearDataCache.of(itemStack).getState() == VaultGearState.IDENTIFIED) {
           atts.add(new CharmAffinityAttribute(String.valueOf(getCharmAffinity(itemStack))));
       }
        return atts;
    }

    @Override
    public String getTranslationKey() {
        return "charm_affinity";
    }

    @Override
    public Object[] getTranslationParameters() {
        return new Object[]{affinity +"%"};
    }

    @Override
    public void writeNBT(CompoundTag nbt) {
        nbt.putString("charmAffinity", this.affinity);
    }

    @Override
    public ItemAttribute readNBT(CompoundTag nbt) {
        return new CharmAffinityAttribute(nbt.getString("charmAffinity"));
    }
}