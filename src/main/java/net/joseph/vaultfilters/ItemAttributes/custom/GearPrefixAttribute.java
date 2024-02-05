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
import static net.joseph.vaultfilters.AttributeHelper.getName;

public class GearPrefixAttribute implements ItemAttribute {

    public static void register() {
        ItemAttribute.register(new GearPrefixAttribute("dummy"));
    }
    String prefixname;

    public GearPrefixAttribute(String prefixname) {
        this.prefixname = prefixname;
    }

    public boolean hasPrefix(ItemStack itemStack) {
        VaultGearData data = VaultGearData.read(itemStack);
        List<VaultGearModifier<?>> prefixes = data.getModifiers(VaultGearModifier.AffixType.PREFIX);

        for (var prefix : prefixes) {
            String name = prefix.getAttribute().getReader().getModifierName();
            if (name.equals("")) {
                if (getName(getAttributeDisplay(prefix, itemStack, data, VaultGearModifier.AffixType.PREFIX)).equals(this.prefixname)) {
                    return true;
                }
            }
            if (name.equals(this.prefixname)) {
                return true;
            }


        }
        if (this.prefixname.equals("Empty Slot")) {
            return hasEmptyPrefix(data);
        }
        return false;
    }

    public boolean hasEmptyPrefix(VaultGearData data) {
        List<VaultGearModifier<?>> prefixes = data.getModifiers(VaultGearModifier.AffixType.PREFIX);
        return prefixes.size() < getPrefixCount(data);
    }
    public int getPrefixCount(VaultGearData data) {
        return data.getFirstValue(ModGearAttributes.PREFIXES).orElse(0);
    }
    @Override
    public boolean appliesTo(ItemStack itemStack) {

        if (itemStack.getItem() instanceof VaultGearItem) {
            return (hasPrefix(itemStack));
        }

        return false;
    }

    @Override
    public List<ItemAttribute> listAttributesOf(ItemStack itemStack) {

        List<ItemAttribute> atts = new ArrayList<>();
       if (itemStack.getItem() instanceof VaultGearItem) {
           VaultGearData data = VaultGearData.read(itemStack);
           List<VaultGearModifier<?>> prefixes = data.getModifiers(VaultGearModifier.AffixType.PREFIX);

           for (var prefix: prefixes) {
               if (!prefix.getAttribute().getReader().getModifierName().equals("")) {
                   atts.add(new GearPrefixAttribute(prefix.getAttribute().getReader().getModifierName()));
               }
               else {
                   atts.add(new GearPrefixAttribute(getName(getAttributeDisplay(prefix,itemStack, data, VaultGearModifier.AffixType.PREFIX))));
               }
           }
           if (hasEmptyPrefix(data)) {
               atts.add(new GearPrefixAttribute("Empty Slot"));
           }
       }
        return atts;
    }

    @Override
    public String getTranslationKey() {
        return "prefix";
    }

    @Override
    public Object[] getTranslationParameters() {
        return new Object[]{prefixname};
    }

    @Override
    public void writeNBT(CompoundTag nbt) {
        nbt.putString("prefix", this.prefixname);
    }

    @Override
    public ItemAttribute readNBT(CompoundTag nbt) {
        return new GearPrefixAttribute(nbt.getString("prefix"));
    }

}