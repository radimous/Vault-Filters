package net.joseph.vaultfilters.ItemAttributes.custom;

import com.simibubi.create.content.logistics.filter.ItemAttribute;
import iskallia.vault.config.gear.VaultGearTierConfig;
import iskallia.vault.gear.VaultGearHelper;
import iskallia.vault.gear.VaultGearModifierHelper;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.attribute.config.ConfigurableAttributeGenerator;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.gear.reader.VaultGearModifierReader;
import iskallia.vault.item.tool.JewelItem;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JewelSizeAttribute implements ItemAttribute {

    public static void register() {
        ItemAttribute.register(new JewelSizeAttribute("0"));
    }
    String size;

    public JewelSizeAttribute(String size) {
        this.size = size;
    }


    @Override
    public boolean appliesTo(ItemStack itemStack) {

        if (itemStack.getItem() instanceof JewelItem) {
            VaultGearData data = VaultGearData.read(itemStack);
            List<VaultGearModifier<?>> implicits = data.getModifiers(VaultGearModifier.AffixType.IMPLICIT);
            if (implicits.size() == 0) {
                return false;
            }
            return ((int) getModifierValue(getDisplay(implicits.get(0), data, VaultGearModifier.AffixType.IMPLICIT, itemStack).toString()) <= Integer.parseInt(size));
        }

        return false;
    }

    @Override
    public List<ItemAttribute> listAttributesOf(ItemStack itemStack) {

        List<ItemAttribute> atts = new ArrayList<>();
       if (itemStack.getItem() instanceof JewelItem) {
           VaultGearData data = VaultGearData.read(itemStack);
           List<VaultGearModifier<?>> implicits = data.getModifiers(VaultGearModifier.AffixType.IMPLICIT);
           if (implicits.size() == 0) {
               return atts;
           }
           atts.add(new JewelSizeAttribute(String.valueOf(Integer.valueOf((int) getModifierValue(getDisplay(implicits.get(0),data, VaultGearModifier.AffixType.IMPLICIT, itemStack).toString())))));
       }
        return atts;
    }


    @Override
    public String getTranslationKey() {
        return "size";
    }

    @Override
    public Object[] getTranslationParameters() {
        return new Object[]{String.valueOf(size)};
    }

    @Override
    public void writeNBT(CompoundTag nbt) {
        nbt.putString("size", this.size);
    }

    @Override
    public ItemAttribute readNBT(CompoundTag nbt) {
        return new JewelSizeAttribute(nbt.getString("size"));
    }

    public Optional<MutableComponent> getDisplay2(VaultGearModifier modifier, VaultGearData data, VaultGearModifier.AffixType type, ItemStack stack) {
        return Optional.ofNullable(modifier.getAttribute().getReader().getDisplay(modifier, data, type, stack));
    }
    public Optional<MutableComponent> getDisplay(VaultGearModifier modifier, VaultGearData data, VaultGearModifier.AffixType type, ItemStack stack) {
        return getDisplay2(modifier, data, type, stack).map(VaultGearModifier.AffixCategory.NONE.getModifierFormatter());
    }
    //FIXME: WTF IS THIS
    public final double getModifierValue(String modifier) {
        boolean flag = false;
        int flagint = 0;
        for (int i = 0; i < modifier.length(); i++) {
            if (isNumber(String.valueOf(modifier.charAt(i)))) {
                flag = true;
                flagint = i;
                i = 100000;
            }
        }

        if (!flag) {
            return 1;
        }
        String tempnum = String.valueOf(modifier.charAt(flagint));
        for (int i = flagint+1; i < modifier.length(); i++) {
            if (isNumber(String.valueOf(modifier.charAt(i)))) {
                tempnum = tempnum + (modifier.charAt(i));
            } else {
                i = 100000;
            }
        }
        return Integer.parseInt(tempnum);

    }
    public static boolean isNumber(String num) {
        char c = num.charAt(0);
        int ascii = c;
        if (ascii >= 48 && ascii <= 57) {
            return true;
        }
        return false;
    }
}