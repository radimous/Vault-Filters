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
    public static boolean isNumber(String num) {
        char c = num.charAt(0);
        int ascii = c;
        if (ascii >= 48 && ascii <= 57) {
            return true;
        }
        return false;
    }
    public static double getModifierValue(String modifier) {
        boolean flag = false;
        int flagint = 0;
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
        for (int i = 0; i < modifier.length(); i++) {
            if (isNumber(String.valueOf(modifier.charAt(i)))) {
                flag = true;
                flagint = i;
                i = 100000;
            }
        }

        if (!flag) {
            return 0;
        }
        String tempnum = String.valueOf(modifier.charAt(flagint));
        for (int i = flagint+1; i < modifier.length(); i++) {
            if (isNumber(String.valueOf(modifier.charAt(i))) || String.valueOf(modifier.charAt(i)).equals(".")) {
                tempnum = tempnum + (modifier.charAt(i));
            } else {
                i = 100000;
            }
        }
        return Double.parseDouble(tempnum);

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
        int flagint = 0;
        for (int i = 0; i < modifier.length(); i++) {
            if (Character.isAlphabetic(modifier.charAt(i))) {
                flagint = i;
                i = 1000;
            }
        }
        String name = "";
        for (int i = flagint; i <modifier.length(); i++) {
            name = name + modifier.charAt(i);
        }
        return name;
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