package net.joseph.vaultfilters;

import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.attribute.VaultGearModifier.AffixType;
import iskallia.vault.gear.data.VaultGearData;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class AttributeHelper {
    //TODO: look at the generics here
    public static String getAttributeDisplay(VaultGearModifier modifier, ItemStack itemStack, VaultGearData data, AffixType type) {
        if ((getDisplay(modifier, data, type, itemStack)).isEmpty()) {
            return "BLANK";
        }
        return (getDisplay(modifier, data, type, itemStack).get().getString());
    }
    public static Optional<MutableComponent> getDisplay(VaultGearModifier modifier, VaultGearData data, VaultGearModifier.AffixType type, ItemStack stack) {
        return getDisplay2(modifier, data, type, stack).map(VaultGearModifier.AffixCategory.NONE.getModifierFormatter());
    }
    public static Optional<MutableComponent> getDisplay2(VaultGearModifier modifier, VaultGearData data, VaultGearModifier.AffixType type, ItemStack stack) {
        return Optional.ofNullable(modifier.getAttribute().getReader().getDisplay(modifier, data, type, stack));
    }
    public static boolean isNumber(char c) {
        return 48 <= c && c <= 57;
    }
    public static double getModifierValue(String modifier) {
        boolean isNumber = false;
        int start = 0;
        for (int i = 0; i < modifier.length(); i++) {
            if (isNumber(modifier.charAt(i))) {
                isNumber = true;
                start = i;
                break;
            }
        }
        if (!isNumber) {
            return 0;
        }
        int end = start;
        for (int i = start + 1; i < modifier.length(); i++) {
            if (isNumber(modifier.charAt(i)) || modifier.charAt(i) == '.') {
                end = i;
            } else {
                break;
            }
        }
        return Double.parseDouble(modifier.substring(start, end + 1));
    }

    public static String getName(String modifier) {
        int start = 0;
        for (int i = 0; i < modifier.length(); i++) {
            if (Character.isAlphabetic(modifier.charAt(i))) {
                start = i;
                break;
            }
        }
        int end = 0;
        for (int i = start; i <modifier.length(); i++) {
            end = i;
        }
        return modifier.substring(start, end + 1);
    }

}
