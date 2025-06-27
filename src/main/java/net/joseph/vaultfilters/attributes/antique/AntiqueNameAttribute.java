package net.joseph.vaultfilters.attributes.antique;

import iskallia.vault.antique.Antique;
import iskallia.vault.config.AntiquesConfig;
import iskallia.vault.item.AntiqueItem;
import net.joseph.vaultfilters.attributes.abstracts.StringAttribute;
import net.minecraft.world.item.ItemStack;

public class AntiqueNameAttribute extends StringAttribute {
    public AntiqueNameAttribute(String value) {
        super(value);
    }

    @Override
    public String getValue(ItemStack itemStack) {
        if(itemStack.getItem() instanceof AntiqueItem) {
            Antique antique = AntiqueItem.getAntique(itemStack);

            if(antique == null) {
                return null;
            }

            AntiquesConfig.Entry entry = antique.getConfig();

            if(entry != null) {
                if(entry.getInfo() != null) {
                    return entry.getInfo().getName();
                }
            }
        }
        return null;
    }

    @Override
    public String getTranslationKey() {
        return "antique_name";
    }
}