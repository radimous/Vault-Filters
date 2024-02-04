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

public class NumberSuffixAttribute implements ItemAttribute {

    public static void register() {
        ItemAttribute.register(new NumberSuffixAttribute("dummy"));
    }
    String suffixname;

    public NumberSuffixAttribute(String suffixname) {
        this.suffixname = suffixname;
    }
    public Optional<MutableComponent> getDisplay2(VaultGearModifier modifier, VaultGearData data, VaultGearModifier.AffixType type, ItemStack stack) {
        return Optional.ofNullable(modifier.getAttribute().getReader().getDisplay(modifier, data, type, stack));
    }
    public Optional<MutableComponent> getDisplay(VaultGearModifier modifier, VaultGearData data, VaultGearModifier.AffixType type, ItemStack stack) {


        return getDisplay2(modifier, data, type, stack).map(VaultGearModifier.AffixCategory.NONE.getModifierFormatter());
    }
    public String getSuffixDisplay(int index, ItemStack itemStack, VaultGearData data) {
        VaultGearModifier modifier = data.getModifiers(VaultGearModifier.AffixType.SUFFIX).get(index);
        if ((getDisplay(modifier, data, VaultGearModifier.AffixType.SUFFIX, itemStack)).isEmpty()) {
            return "BLANK";
        }
        return (getDisplay(modifier, data, VaultGearModifier.AffixType.SUFFIX, itemStack).get().getString());
    }
    public int getSuffixCount(VaultGearData data) {
        return data.getModifiers(VaultGearModifier.AffixType.SUFFIX).size();
    }
    public static boolean isNumber(char c) {
        return 48 <= c && c <= 57;
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
        int end = 0;
        for (int i = start + 1; i < modifier.length(); i++) {
            if (isNumber(modifier.charAt(i)) || modifier.charAt(i) == '.') {
                end = i;
            } else {
                break;
            }
        }
        return Double.parseDouble(modifier.substring(start, end + 1));

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
    @Override
    public boolean appliesTo(ItemStack itemStack) {

        if (itemStack.getItem() instanceof VaultGearItem) {
            VaultGearData data = VaultGearData.read(itemStack);
            for (int i = 0; i < getSuffixCount(data); i++) {
                if (getSuffixDisplay(i,itemStack, data).equals("BLANK")) {
                    return false;
                }
                if (getName(getSuffixDisplay(i,itemStack, data)).equals(getName(suffixname))) {
                    if (getModifierValue(getSuffixDisplay(i,itemStack, data)) >= getModifierValue(suffixname)) {
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
           for (int i = 0; i < getSuffixCount(data); i++) {
               if (getSuffixDisplay(i,itemStack, data).equals("BLANK")) {
                   return atts;
               }
               if (getModifierValue(getSuffixDisplay(i,itemStack, data)) != 0) {
                   atts.add(new NumberSuffixAttribute(getSuffixDisplay(i,itemStack, data)));
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