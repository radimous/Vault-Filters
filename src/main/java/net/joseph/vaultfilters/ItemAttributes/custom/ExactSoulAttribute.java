package net.joseph.vaultfilters.ItemAttributes.custom;

import com.simibubi.create.content.logistics.filter.ItemAttribute;
import iskallia.vault.init.ModConfigs;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ExactSoulAttribute implements ItemAttribute {

    public static void register() {
        ItemAttribute.register(new ExactSoulAttribute("dummy"));
    }
    String soulValue;

    public ExactSoulAttribute(String soulValue) { this.soulValue = soulValue;}

    public static boolean hasSoulValue(ItemStack itemStack) {

        return ModConfigs.VAULT_DIFFUSER.getDiffuserOutputMap().containsKey((itemStack.getItem().getRegistryName()));
    }
    public static int getSoulValue(ItemStack itemStack) {
        return ModConfigs.VAULT_DIFFUSER.getDiffuserOutputMap().get(itemStack.getItem().getRegistryName());
    }
    @Override
    public boolean appliesTo(ItemStack itemStack) {

       if (!hasSoulValue(itemStack)) {
           return false;
       }
       return (getSoulValue(itemStack) == Integer.parseInt(soulValue));
    }

    @Override
    public List<ItemAttribute> listAttributesOf(ItemStack itemStack) {

        List<ItemAttribute> atts = new ArrayList<>();
           if (hasSoulValue(itemStack)) {
               atts.add(new ExactSoulAttribute(String.valueOf(getSoulValue(itemStack))));
           }
        return atts;
    }

    @Override
    public String getTranslationKey() {
        return "exact_soul";
    }
    @Override
    public Object[] getTranslationParameters() {
        return new Object[]{soulValue};
    }
    @Override
    public void writeNBT(CompoundTag nbt) {
        nbt.putString("exactSoul", this.soulValue);
    }

    @Override
    public ItemAttribute readNBT(CompoundTag nbt) {
        return new ExactSoulAttribute(nbt.getString("exactSoul"));
    }


}