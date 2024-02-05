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

import static net.joseph.vaultfilters.AttributeHelper.getAttributeDisplay;
import static net.joseph.vaultfilters.AttributeHelper.getModifierValue;
import static net.joseph.vaultfilters.AttributeHelper.getName;

public class NumberImplicitAttribute implements ItemAttribute {

    public static void register() {
        ItemAttribute.register(new NumberImplicitAttribute("dummy"));
    }
    String implicitname;

    public NumberImplicitAttribute(String implicitname) {
        this.implicitname = implicitname;
    }

    @Override
    public boolean appliesTo(ItemStack itemStack) {

        if (itemStack.getItem() instanceof VaultGearItem  && !(itemStack.getItem() instanceof JewelItem)) {
            VaultGearData data = VaultGearData.read(itemStack);
            List<VaultGearModifier<?>> implicits =  data.getModifiers(VaultGearModifier.AffixType.IMPLICIT);
            for (var implicit : implicits) {
                if (getAttributeDisplay(implicit,itemStack, data, VaultGearModifier.AffixType.IMPLICIT).equals("BLANK")) {
                    return false;
                }
                if (getName(getAttributeDisplay(implicit,itemStack, data, VaultGearModifier.AffixType.IMPLICIT)).equals(getName(implicitname))) {
                    if (getModifierValue(getAttributeDisplay(implicit,itemStack, data, VaultGearModifier.AffixType.IMPLICIT)) >= getModifierValue(implicitname)) {
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
           List<VaultGearModifier<?>> implicits =  data.getModifiers(VaultGearModifier.AffixType.IMPLICIT);
           for (var implicit : implicits) {
               if (getAttributeDisplay(implicit,itemStack, data, VaultGearModifier.AffixType.IMPLICIT).equals("BLANK")) {
                   return atts;
               }
               if (getModifierValue(getAttributeDisplay(implicit,itemStack, data, VaultGearModifier.AffixType.IMPLICIT)) != 0) {
                   atts.add(new NumberImplicitAttribute(getAttributeDisplay(implicit,itemStack, data, VaultGearModifier.AffixType.IMPLICIT)));
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