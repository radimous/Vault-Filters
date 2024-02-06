package net.joseph.vaultfilters.ItemAttributes.custom;

import com.simibubi.create.content.logistics.filter.ItemAttribute;
import iskallia.vault.gear.data.GearDataCache;
import iskallia.vault.item.tool.JewelItem;
import net.joseph.vaultfilters.IVFGearDataCache;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class JewelSizeAttribute implements ItemAttribute {

    public static void register() {
        ItemAttribute.register(new JewelSizeAttribute("0"));
    }
    String size;

    public JewelSizeAttribute(String size) {
        this.size = size;
    }

    @Override
    public boolean appliesTo(ItemStack itemStack) {

        if (itemStack.getItem() instanceof JewelItem) {
            Integer jewelSize = ((IVFGearDataCache)GearDataCache.of(itemStack)).vaultfilters$getExtraJewelSize();
            if (jewelSize == null) {
                return false;
            }
            return jewelSize <= Integer.parseInt(size);
        }

        return false;
    }

    @Override
    public List<ItemAttribute> listAttributesOf(ItemStack itemStack) {

        List<ItemAttribute> atts = new ArrayList<>();
        if (itemStack.getItem() instanceof JewelItem) {
            Integer jewelSize = ((IVFGearDataCache) GearDataCache.of(itemStack)).vaultfilters$getExtraJewelSize();
            if (jewelSize == null) {
                return atts;
            }
            atts.add(new JewelSizeAttribute(String.valueOf(jewelSize)));
        }
        return atts;
    }


    @Override
    public String getTranslationKey() {
        return "size";
    }

    @Override
    public Object[] getTranslationParameters() {
        return new Object[]{String.valueOf(size)};
    }

    @Override
    public void writeNBT(CompoundTag nbt) {
        nbt.putString("size", this.size);
    }

    @Override
    public ItemAttribute readNBT(CompoundTag nbt) {
        return new JewelSizeAttribute(nbt.getString("size"));
    }
}