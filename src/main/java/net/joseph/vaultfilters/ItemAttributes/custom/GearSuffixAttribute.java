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
        for (int i = 0; i < suffixes.size(); i++) {
            name = suffixes.get(i).getAttribute().getReader().getModifierName();
            if (name.equals("")) {
                if (NumberPrefixAttribute.getName(getSuffixDisplay(i, itemStack)).equals(this.suffixname)) {
                    return true;
                }
            }
            if (name.contains("Cloud")) {
                if (getName(getSuffixDisplay(i, itemStack)).equals(this.suffixname)) {
                    return true;
                }
            }
            if (name.equals(this.suffixname)) {
                return true;
            }

        }
        if (this.suffixname.equals("Empty Slot")) {
            return hasEmptySuffix(itemStack);
        }
        return false;
    }

    public boolean hasEmptySuffix(ItemStack itemStack) {
        VaultGearData data = VaultGearData.read(itemStack);
        List<VaultGearModifier<?>> suffixes = data.getModifiers(VaultGearModifier.AffixType.SUFFIX);
        return suffixes.size() < getSuffixCount(itemStack);
    }
    public int getSuffixCount(ItemStack itemStack) {
        VaultGearData data =VaultGearData.read(itemStack);
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

           for (int i = 0; i < suffixes.size(); i++) {
               if (!suffixes.get(i).getAttribute().getReader().getModifierName().contains("Cloud")) {
                   atts.add(new GearSuffixAttribute(suffixes.get(i).getAttribute().getReader().getModifierName()));
               } else  if (!suffixes.get(i).getAttribute().getReader().getModifierName().equals("")){
                   atts.add(new GearSuffixAttribute(getName(getSuffixDisplay(i,itemStack))));
               }
               else {
                   atts.add(new GearPrefixAttribute(NumberPrefixAttribute.getName(getSuffixDisplay(i,itemStack))));
               }
           }
           if (hasEmptySuffix(itemStack)) {
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

    public Optional<MutableComponent> getDisplay2(VaultGearModifier modifier, VaultGearData data, VaultGearModifier.AffixType type, ItemStack stack) {
        return Optional.ofNullable(modifier.getAttribute().getReader().getDisplay(modifier, data, type, stack));
    }
    public Optional<MutableComponent> getDisplay(VaultGearModifier modifier, VaultGearData data, VaultGearModifier.AffixType type, ItemStack stack) {
        return getDisplay2(modifier, data, type, stack).map(VaultGearModifier.AffixCategory.NONE.getModifierFormatter());
    }
    public String getSuffixDisplay(int index, ItemStack itemStack) {
        VaultGearData data = VaultGearData.read(itemStack);
        VaultGearModifier modifier = data.getModifiers(VaultGearModifier.AffixType.SUFFIX).get(index);
        return (getDisplay(modifier, data, VaultGearModifier.AffixType.SUFFIX, itemStack).get().getString());
    }
}