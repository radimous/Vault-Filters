package net.joseph.vaultfilters.ItemAttributes.custom;

import com.simibubi.create.content.logistics.filter.ItemAttribute;
import iskallia.vault.config.gear.VaultGearTierConfig;
import iskallia.vault.gear.VaultGearHelper;
import iskallia.vault.gear.VaultGearModifierHelper;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.attribute.config.ConfigurableAttributeGenerator;
import iskallia.vault.gear.data.GearDataCache;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.gear.reader.VaultGearModifierReader;
import iskallia.vault.item.tool.JewelItem;
import lv.id.bonne.vaulthunters.jewelsorting.utils.IExtraGearDataCache;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
            Integer jewelSize = ((IExtraGearDataCache)GearDataCache.of(itemStack)).getExtraJewelSize();
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
            Integer jewelSize = ((IExtraGearDataCache) GearDataCache.of(itemStack)).getExtraJewelSize();
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