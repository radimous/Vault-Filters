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

public class NumberPrefixAttribute implements ItemAttribute {

    public static void register() {
        ItemAttribute.register(new NumberPrefixAttribute("dummy"));
    }
    String prefixname;

    public NumberPrefixAttribute(String prefixname) {
        this.prefixname = prefixname;
    }
    public Optional<MutableComponent> getDisplay2(VaultGearModifier modifier, VaultGearData data, VaultGearModifier.AffixType type, ItemStack stack) {
        return Optional.ofNullable(modifier.getAttribute().getReader().getDisplay(modifier, data, type, stack));
    }
    public Optional<MutableComponent> getDisplay(VaultGearModifier modifier, VaultGearData data, VaultGearModifier.AffixType type, ItemStack stack) {


        return getDisplay2(modifier, data, type, stack).map(VaultGearModifier.AffixCategory.NONE.getModifierFormatter());
    }
    public  String getPrefixDisplay(int index, ItemStack itemStack, VaultGearData data) {
        VaultGearModifier modifier = data.getModifiers(VaultGearModifier.AffixType.PREFIX).get(index);
        if ((getDisplay(modifier, data, VaultGearModifier.AffixType.PREFIX, itemStack)).isEmpty()) {
            return "BLANK";
        }
        return (getDisplay(modifier, data, VaultGearModifier.AffixType.PREFIX, itemStack).get().getString());
    }
    public int getPrefixCount(VaultGearData data) {
        return data.getModifiers(VaultGearModifier.AffixType.PREFIX).size();
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
    @Override
    public boolean appliesTo(ItemStack itemStack) {

        if (itemStack.getItem() instanceof VaultGearItem) {
            VaultGearData data = VaultGearData.read(itemStack);
            for (int i = 0; i < getPrefixCount(data); i++) {
                if (getPrefixDisplay(i,itemStack, data).equals("BLANK")) {
                    return false;
                }
                if (getName(getPrefixDisplay(i,itemStack, data)).equals(getName(prefixname))) {
                    if (getModifierValue(getPrefixDisplay(i,itemStack, data)) >= getModifierValue(prefixname)) {
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
           for (int i = 0; i < getPrefixCount(data); i++) {
               if (getPrefixDisplay(i,itemStack, data).equals("BLANK")) {
                   return atts;
               }
               if (getModifierValue(getPrefixDisplay(i,itemStack, data)) != 0) {
                   atts.add(new NumberPrefixAttribute(getPrefixDisplay(i,itemStack, data)));
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