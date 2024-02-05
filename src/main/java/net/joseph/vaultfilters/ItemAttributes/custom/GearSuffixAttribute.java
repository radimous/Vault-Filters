package net.joseph.vaultfilters.ItemAttributes.custom;

import com.simibubi.create.content.logistics.filter.ItemAttribute;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.init.ModGearAttributes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static net.joseph.vaultfilters.AttributeHelper.getAttributeDisplay;
import static net.joseph.vaultfilters.ItemAttributes.custom.NumberSuffixAttribute.getName;

public class GearSuffixAttribute implements ItemAttribute {

    public static void register() {
        ItemAttribute.register(new GearSuffixAttribute("dummy"));
    }
    String suffixname;

    public GearSuffixAttribute(String suffixname) {
        this.suffixname = suffixname;
    }

    public boolean hasSuffix(ItemStack itemStack) {
        VaultGearData data = VaultGearData.read(itemStack);
        List<VaultGearModifier<?>> suffixes = data.getModifiers(VaultGearModifier.AffixType.SUFFIX);
        String name;
        for (var suffix : suffixes) {
            name = suffix.getAttribute().getReader().getModifierName();
            if (name.equals("")) {
                if (getName(getAttributeDisplay(suffix, itemStack, data, VaultGearModifier.AffixType.SUFFIX)).equals(this.suffixname)) {
                    return true;
                }
            }
            if (name.contains("Cloud")) {
                if (getName(getAttributeDisplay(suffix, itemStack, data, VaultGearModifier.AffixType.SUFFIX)).equals(this.suffixname)) {
                    return true;
                }
            }
            if (name.equals(this.suffixname)) {
                return true;
            }

        }
        if (this.suffixname.equals("Empty Slot")) {
            return hasEmptySuffix(data);
        }
        return false;
    }

    public boolean hasEmptySuffix(VaultGearData data) {
        List<VaultGearModifier<?>> suffixes = data.getModifiers(VaultGearModifier.AffixType.SUFFIX);
        return suffixes.size() < getSuffixCount(data);
    }
    public int getSuffixCount(VaultGearData data) {
        return data.getFirstValue(ModGearAttributes.SUFFIXES).orElse(0);
    }
    @Override
    public boolean appliesTo(ItemStack itemStack) {

        if (itemStack.getItem() instanceof VaultGearItem) {
            return (hasSuffix(itemStack));
        }

        return false;
    }

    @Override
    public List<ItemAttribute> listAttributesOf(ItemStack itemStack) {

        List<ItemAttribute> atts = new ArrayList<>();
       if (itemStack.getItem() instanceof VaultGearItem) {
           VaultGearData data = VaultGearData.read(itemStack);
           List<VaultGearModifier<?>> suffixes = data.getModifiers(VaultGearModifier.AffixType.SUFFIX);

           for (var suffix: suffixes) {
               if (!suffix.getAttribute().getReader().getModifierName().contains("Cloud")) {
                   atts.add(new GearSuffixAttribute(suffix.getAttribute().getReader().getModifierName()));
               } else  if (!suffix.getAttribute().getReader().getModifierName().equals("")){
                   atts.add(new GearSuffixAttribute(getName(getAttributeDisplay(suffix,itemStack, data, VaultGearModifier.AffixType.SUFFIX))));
               }
               else {
                   atts.add(new GearPrefixAttribute(getName(getAttributeDisplay(suffix,itemStack, data, VaultGearModifier.AffixType.SUFFIX))));
               }
           }
           if (hasEmptySuffix(data)) {
               atts.add(new GearSuffixAttribute("Empty Slot"));
           }
       }
        return atts;
    }

    @Override
    public String getTranslationKey() {
        return "suffix";
    }

    @Override
    public Object[] getTranslationParameters() {
        return new Object[]{suffixname};
    }


    @Override
    public void writeNBT(CompoundTag nbt) {
        nbt.putString("suffix", this.suffixname);
    }
    @Override
    public ItemAttribute readNBT(CompoundTag nbt) {
        return new GearSuffixAttribute(nbt.getString("suffix"));
    }

}