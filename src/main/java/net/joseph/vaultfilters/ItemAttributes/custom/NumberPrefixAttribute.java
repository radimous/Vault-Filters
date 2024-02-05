package net.joseph.vaultfilters.ItemAttributes.custom;

import com.simibubi.create.content.logistics.filter.ItemAttribute;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static net.joseph.vaultfilters.AttributeHelper.getAttributeDisplay;
import static net.joseph.vaultfilters.AttributeHelper.getModifierValue;
import static net.joseph.vaultfilters.AttributeHelper.getName;

public class NumberPrefixAttribute implements ItemAttribute {

    public static void register() {
        ItemAttribute.register(new NumberPrefixAttribute("dummy"));
    }
    String prefixname;

    public NumberPrefixAttribute(String prefixname) {
        this.prefixname = prefixname;
    }
    @Override
    public boolean appliesTo(ItemStack itemStack) {

        if (itemStack.getItem() instanceof VaultGearItem) {
            VaultGearData data = VaultGearData.read(itemStack);
            VaultGearModifier.AffixType type = VaultGearModifier.AffixType.PREFIX;
            List<VaultGearModifier<?>> prefixes = data.getModifiers(type);
            for (var prefix: prefixes) {
                if (getAttributeDisplay(prefix,itemStack, data, type).equals("BLANK")) {
                    return false;
                }
                if (getName(getAttributeDisplay(prefix,itemStack, data, type)).equals(getName(prefixname))) {
                    if (getModifierValue(getAttributeDisplay(prefix,itemStack, data, type)) >= getModifierValue(prefixname)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public List<ItemAttribute> listAttributesOf(ItemStack itemStack) {

        List<ItemAttribute> atts = new ArrayList<>();
       if (itemStack.getItem() instanceof VaultGearItem) {
           VaultGearData data = VaultGearData.read(itemStack);
           VaultGearModifier.AffixType type = VaultGearModifier.AffixType.PREFIX;
           List<VaultGearModifier<?>> prefixes = data.getModifiers(type);
           for (var prefix: prefixes) {
               if (getAttributeDisplay(prefix,itemStack, data, type).equals("BLANK")) {
                   return atts;
               }
               if (getModifierValue(getAttributeDisplay(prefix,itemStack, data, type)) != 0) {
                   atts.add(new NumberPrefixAttribute(getAttributeDisplay(prefix,itemStack, data, type)));
               }
           }
       }
        return atts;
    }

    @Override
    public String getTranslationKey() {
        return "prefix_number";
    }

    @Override
    public Object[] getTranslationParameters() {
        return new Object[]{prefixname};
    }

    @Override
    public void writeNBT(CompoundTag nbt) {
        nbt.putString("prefixNumber", this.prefixname);
    }

    @Override
    public ItemAttribute readNBT(CompoundTag nbt) {
        return new NumberPrefixAttribute(nbt.getString("prefixNumber"));
    }
}