package net.joseph.vaultfilters.ItemAttributes.custom;

import com.simibubi.create.content.logistics.filter.ItemAttribute;
import iskallia.vault.gear.VaultGearState;
import iskallia.vault.gear.charm.CharmEffect;
import iskallia.vault.gear.data.GearDataCache;
import iskallia.vault.item.gear.CharmItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static iskallia.vault.item.gear.CharmItem.getCharm;

public class CharmGodAttribute implements ItemAttribute {

    public static void register() {
        ItemAttribute.register(new CharmGodAttribute("dummy"));
    }
    String god;
    public static String getCharmGod(ItemStack itemStack) {
       String tooltip = ((CharmEffect.Config<?>)getCharm(itemStack).get().getCharmConfig().getConfig()).getAttribute().getReader().getModifierName();
       if (tooltip.contains("Velara")) {
           return "Velara";
       }
        if (tooltip.contains("Idona")) {
            return "Idona";
        }
        if (tooltip.contains("Tenos")) {
            return "Tenos";
        }
        if (tooltip.contains("Wendarr")) {
            return "Wendarr";
        }
        return tooltip;
    }
    public CharmGodAttribute(String god) {
        this.god = god;
    }


    @Override
    public boolean appliesTo(ItemStack itemStack) {


        if (itemStack.getItem() instanceof CharmItem && GearDataCache.of(itemStack).getState() == VaultGearState.IDENTIFIED) {
            return (getCharmGod(itemStack).equals(god));
        }
        return false;
    }

    @Override
    public List<ItemAttribute> listAttributesOf(ItemStack itemStack) {

        List<ItemAttribute> atts = new ArrayList<>();

       if (itemStack.getItem() instanceof CharmItem && GearDataCache.of(itemStack).getState() == VaultGearState.IDENTIFIED) {
           atts.add(new CharmGodAttribute(getCharmGod(itemStack)));
       }
        return atts;
    }

    @Override
    public String getTranslationKey() {
        return "charm_god";
    }

    @Override
    public Object[] getTranslationParameters() {
        return new Object[]{god};
    }

    @Override
    public void writeNBT(CompoundTag nbt) {
        nbt.putString("charm_god", this.god);
    }

    @Override
    public ItemAttribute readNBT(CompoundTag nbt) {
        return new CharmGodAttribute(nbt.getString("charm_god"));
    }
}