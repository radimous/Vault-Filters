package net.joseph.vaultfilters.ItemAttributes.custom;

import com.simibubi.create.content.logistics.filter.ItemAttribute;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.item.tool.JewelItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class NumberImplicitAttribute implements ItemAttribute {

    public static void register() {
        ItemAttribute.register(new NumberImplicitAttribute("dummy"));
    }
    String implicitname;

    public NumberImplicitAttribute(String implicitname) {
        this.implicitname = implicitname;
    }
    public Optional<MutableComponent> getDisplay2(VaultGearModifier modifier, VaultGearData data, VaultGearModifier.AffixType type, ItemStack stack) {
        return Optional.ofNullable(modifier.getAttribute().getReader().getDisplay(modifier, data, type, stack));
    }
    public Optional<MutableComponent> getDisplay(VaultGearModifier modifier, VaultGearData data, VaultGearModifier.AffixType type, ItemStack stack) {


        return getDisplay2(modifier, data, type, stack).map(VaultGearModifier.AffixCategory.NONE.getModifierFormatter());
    }
    public String getImplicitDisplay(int index, ItemStack itemStack, VaultGearData data) {
        VaultGearModifier modifier = data.getModifiers(VaultGearModifier.AffixType.IMPLICIT).get(index);
        if ((getDisplay(modifier, data, VaultGearModifier.AffixType.IMPLICIT, itemStack)).isEmpty()) {
            return "BLANK";
        }
        return (getDisplay(modifier, data, VaultGearModifier.AffixType.IMPLICIT, itemStack).get().getString());
    }
    public int getImplicitCount(VaultGearData data) {
        return data.getModifiers(VaultGearModifier.AffixType.IMPLICIT).size();
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

        if (itemStack.getItem() instanceof VaultGearItem  && !(itemStack.getItem() instanceof JewelItem)) {
            VaultGearData data = VaultGearData.read(itemStack);
            for (int i = 0; i < getImplicitCount(data); i++) {
                if (getImplicitDisplay(i,itemStack, data).equals("BLANK")) {
                    return false;
                }
                if (getName(getImplicitDisplay(i,itemStack, data)).equals(getName(implicitname))) {
                    if (getModifierValue(getImplicitDisplay(i,itemStack, data)) >= getModifierValue(implicitname)) {
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
       if (itemStack.getItem() instanceof VaultGearItem && !(itemStack.getItem() instanceof JewelItem)) {
           VaultGearData data = VaultGearData.read(itemStack);
           for (int i = 0; i < getImplicitCount(data); i++) {
               if (getImplicitDisplay(i,itemStack, data).equals("BLANK")) {
                   return atts;
               }
               if (getModifierValue(getImplicitDisplay(i,itemStack, data)) != 0) {
                   atts.add(new NumberImplicitAttribute(getImplicitDisplay(i,itemStack, data)));
               }
           }
       }
        return atts;
    }

    @Override
    public String getTranslationKey() {
        return "implicit_number";
    }

    @Override
    public Object[] getTranslationParameters() {
        return new Object[]{implicitname};
    }

    @Override
    public void writeNBT(CompoundTag nbt) {
        nbt.putString("implicitNumber", this.implicitname);
    }

    @Override
    public ItemAttribute readNBT(CompoundTag nbt) {
        return new NumberImplicitAttribute(nbt.getString("implicitNumber"));
    }
}