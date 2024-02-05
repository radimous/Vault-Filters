package net.joseph.vaultfilters.ItemAttributes.custom;

import com.simibubi.create.content.logistics.filter.ItemAttribute;
import iskallia.vault.gear.data.GearDataCache;
import iskallia.vault.item.tool.JewelItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class JewelRarityAttribute implements ItemAttribute {

    public static void register() {
        ItemAttribute.register(new JewelRarityAttribute("dummy"));
    }
    String rarity;
    public static String rarityToJewel(String rarity) {
        return switch (rarity) {
            case "COMMON" -> "Chipped";
            case "RARE" -> "Flawed";
            case "EPIC" -> "Flawless";
            case "OMEGA" -> "Perfect";
            default -> "NULL";
        };
    }

    public JewelRarityAttribute(String rarity) {
        this.rarity = rarity;
    }


    @Override
    public boolean appliesTo(ItemStack itemStack) {

        if (itemStack.getItem() instanceof JewelItem) {
            var rarity = GearDataCache.of(itemStack).getRarity();
            if (rarity == null) {
                return false;
            }
            return (rarity.toString()).equals(this.rarity);
        }
        return false;
    }

    @Override
    public List<ItemAttribute> listAttributesOf(ItemStack itemStack) {

        List<ItemAttribute> atts = new ArrayList<>();
       if (itemStack.getItem() instanceof JewelItem) {
           var rarity = GearDataCache.of(itemStack).getRarity();
           if (rarity == null) {
              return atts;
           }
           atts.add(new JewelRarityAttribute(rarityToJewel(rarity.toString())));
       }
        return atts;
    }

    @Override
    public String getTranslationKey() {
        return "jewel_rarity";
    }

    @Override
    public Object[] getTranslationParameters() {
        return new Object[]{rarity};
    }

    @Override
    public void writeNBT(CompoundTag nbt) {
        nbt.putString("jewelRarity", this.rarity);
    }

    @Override
    public ItemAttribute readNBT(CompoundTag nbt) {
        return new JewelRarityAttribute(nbt.getString("jewelRarity"));
    }
}