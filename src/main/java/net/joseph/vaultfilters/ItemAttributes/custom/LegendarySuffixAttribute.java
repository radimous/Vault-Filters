package net.joseph.vaultfilters.ItemAttributes.custom;

import com.simibubi.create.content.logistics.filter.ItemAttribute;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class LegendarySuffixAttribute implements ItemAttribute {

    public static void register() {
        ItemAttribute.register(new LegendarySuffixAttribute("dummy"));
    }
    String suffixname;

    public LegendarySuffixAttribute(String suffixname) {
        this.suffixname = suffixname;
    }

    public boolean hasAsLegendarySuffix(ItemStack itemStack) {
        VaultGearData data = VaultGearData.read(itemStack);
        List<VaultGearModifier<?>> suffixes = data.getModifiers(VaultGearModifier.AffixType.SUFFIX);

        for (VaultGearModifier<?> suffix : suffixes) {
            if (suffix.getAttribute().getReader().getModifierName().equals(this.suffixname)) {
                if (suffix.getCategory() == VaultGearModifier.AffixCategory.LEGENDARY) {
                    return true;
                }
            }

        }
        return false;
    }


    @Override
    public boolean appliesTo(ItemStack itemStack) {

        if (itemStack.getItem() instanceof VaultGearItem) {
            return (hasAsLegendarySuffix(itemStack));
        }

        return false;
    }

    @Override
    public List<ItemAttribute> listAttributesOf(ItemStack itemStack) {

        List<ItemAttribute> atts = new ArrayList<>();
       if (itemStack.getItem() instanceof VaultGearItem) {
           VaultGearData data = VaultGearData.read(itemStack);
           List<VaultGearModifier<?>> suffixes = data.getModifiers(VaultGearModifier.AffixType.SUFFIX);

           for (VaultGearModifier<?> suffix : suffixes) {
               if (suffix.getCategory() == VaultGearModifier.AffixCategory.LEGENDARY) {
                   atts.add(new LegendarySuffixAttribute(suffix.getAttribute().getReader().getModifierName()));
               }
           }
       }
        return atts;
    }

    @Override
    public String getTranslationKey() {
        return "legendary_suffix";
    }

    @Override
    public Object[] getTranslationParameters() {
        return new Object[]{suffixname};
    }

    @Override
    public void writeNBT(CompoundTag nbt) {
        nbt.putString("legendarySuffix", this.suffixname);
    }

    @Override
    public ItemAttribute readNBT(CompoundTag nbt) {
        return new LegendarySuffixAttribute(nbt.getString("legendarySuffix"));
    }
}