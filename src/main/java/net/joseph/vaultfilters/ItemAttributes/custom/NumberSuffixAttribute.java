package net.joseph.vaultfilters.ItemAttributes.custom;

import com.simibubi.create.content.logistics.filter.ItemAttribute;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import net.joseph.vaultfilters.AttributeHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static net.joseph.vaultfilters.AttributeHelper.getAttributeDisplay;

public class NumberSuffixAttribute implements ItemAttribute {

    public static void register() {
        ItemAttribute.register(new NumberSuffixAttribute("dummy"));
    }
    String suffixname;

    public NumberSuffixAttribute(String suffixname) {
        this.suffixname = suffixname;
    }
    public static double getModifierValue(String modifier) {
        if (modifier.contains("Cloud")) {
            if (modifier.contains("IV")) {
                return 4;
            }
            if (modifier.contains("V")) {
                return 5;
            }
            if (modifier.contains("III")) {
                return 3;
            }
            if (modifier.contains("II")) {
                return 2;
            }
            if (modifier.contains("I")) {
                return 1;
            }
        }
        return AttributeHelper.getModifierValue(modifier);

    }
    public static String getCloudName(String modifier) {
        if (modifier.contains("Healing")) {
            return "Healing Cloud";
        }
        if (modifier.contains("Poison")) {
            return "Poison Cloud";
        }
        if (modifier.contains("Slowness")) {
            return "Slowness Cloud";
        }
        if (modifier.contains("Fear")) {
            return "Fear Cloud";
        }
        if (modifier.contains("Chilling")) {
            return "Chilling Cloud";
        }
        return "Effect Cloud";
    }
    public static String getName(String modifier) {
        if (modifier.contains("Cloud")) {
            return getCloudName(modifier);
        }
        return AttributeHelper.getName(modifier);
    }
    @Override
    public boolean appliesTo(ItemStack itemStack) {

        if (itemStack.getItem() instanceof VaultGearItem) {
            VaultGearData data = VaultGearData.read(itemStack);
            VaultGearModifier.AffixType type = VaultGearModifier.AffixType.SUFFIX;
            List<VaultGearModifier<?>> suffixes = data.getModifiers(type);
            for (var suffix: suffixes) {
                if (getAttributeDisplay(suffix,itemStack, data, type).equals("BLANK")) {
                    return false;
                }
                if (getName(getAttributeDisplay(suffix,itemStack, data, type)).equals(getName(suffixname))) {
                    if (getModifierValue(getAttributeDisplay(suffix,itemStack, data, type)) >= getModifierValue(suffixname)) {
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
           VaultGearModifier.AffixType type = VaultGearModifier.AffixType.SUFFIX;
           List<VaultGearModifier<?>> suffixes = data.getModifiers(type);
           for (var suffix: suffixes) {
               if (getAttributeDisplay(suffix,itemStack, data, type).equals("BLANK")) {
                   return atts;
               }
               if (getModifierValue(getAttributeDisplay(suffix,itemStack, data,type)) != 0) {
                   atts.add(new NumberSuffixAttribute(getAttributeDisplay(suffix,itemStack, data, type)));
               }
           }
       }
        return atts;
    }

    @Override
    public String getTranslationKey() {
        return "suffix_number";
    }

    @Override
    public Object[] getTranslationParameters() {
        return new Object[]{suffixname};
    }

    @Override
    public void writeNBT(CompoundTag nbt) {
        nbt.putString("suffixNumber", this.suffixname);
    }

    @Override
    public ItemAttribute readNBT(CompoundTag nbt) {
        return new NumberSuffixAttribute(nbt.getString("suffixNumber"));
    }
}