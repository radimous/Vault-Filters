package net.joseph.vaultfilters.attributes.abstracts;

import com.simibubi.create.content.logistics.filter.ItemAttribute;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.attribute.ability.AbilityLevelAttribute;
import iskallia.vault.gear.attribute.custom.EffectAvoidanceGearAttribute;
import iskallia.vault.gear.attribute.custom.EffectCloudAttribute;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.gear.reader.VaultGearModifierReader;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.base.Skill;
import net.joseph.vaultfilters.mixin.EffectCloudAccessor;
import net.joseph.vaultfilters.mixin.EffectCloudAttributeAccessor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public abstract class AffixAttribute extends StringAttribute {
    private static final DecimalFormat FORMAT = new DecimalFormat("0.##");

    protected AffixAttribute(String value) {
        super(value);
    }

    public abstract boolean shouldList(VaultGearModifier.AffixType type, VaultGearModifier<?> modifier);

    public boolean appliesTo(VaultGearModifier.AffixType type, ItemStack itemStack) {
        return hasModifier(type, itemStack);
    }

    public boolean hasModifier(VaultGearModifier.AffixType type, ItemStack itemStack) {
        if (itemStack.getItem() instanceof VaultGearItem) {
            for (VaultGearModifier<?> modifier : VaultGearData.read(itemStack).getModifiers(type)) {
                String name = getName(type, modifier, false);
                return this.value.equals(name);
            }
        }
        return false;
    }

    public static <T> String getName(VaultGearModifier.AffixType type,  VaultGearModifier<T> modifier, boolean includeLevel) {
        if (modifier.getValue() instanceof EffectCloudAttribute cloudAttribute) {
            EffectCloudAttribute.EffectCloud cloud = ((EffectCloudAttributeAccessor) cloudAttribute).getEffectCloud();
            boolean whenHit = modifier.getAttribute().getReader().getModifierName().contains("Hit");
            String tooltip = ((EffectCloudAccessor) cloud).getTooltip();
            String cloudType = tooltip.substring(0, tooltip.lastIndexOf(' ')) + " Cloud";
            String level = tooltip.substring(tooltip.lastIndexOf(' ') + 1);
            level = level.isBlank() ? "I" : level;
            return cloudType + (includeLevel ? level : "") + (whenHit ? " when Hit" : "");
        }

        if (modifier.getValue() instanceof AbilityLevelAttribute levelAttribute) {
            String ability = levelAttribute.getAbility().equals("all_abilities")
                    ? "All Abilities"
                    : ModConfigs.ABILITIES.getAbilityById(levelAttribute.getAbility()).map(Skill::getName).orElse("");
            int levelChange = levelAttribute.getLevelChange();
            return (includeLevel ? "+" + levelChange : "Adds") + " to level of " + ability;
        }

        if (modifier.getValue() instanceof EffectAvoidanceGearAttribute avoidanceAttribute) {
            String avoidanceType = avoidanceAttribute.getEffect().getDisplayName().getString() + " Avoidance";
            return (includeLevel ? "+" + FORMAT.format(avoidanceAttribute.getChance() * 100) : "") + avoidanceType;
        }

        VaultGearModifierReader<T> reader = modifier.getAttribute().getReader();
        MutableComponent levelDisplay = reader.getDisplay(modifier, type);
        return includeLevel && levelDisplay != null
                ? levelDisplay.getString()
                : reader.getModifierName();
    }

    public List<VaultGearModifier<?>> getModifiers(ItemStack itemStack, VaultGearModifier.AffixType type) {
        if (itemStack.getItem() instanceof VaultGearItem) {
            return new ArrayList<>(VaultGearData.read(itemStack).getModifiers(type));
        }
        return new ArrayList<>();
    }

    @Override
    public String getValue(ItemStack itemStack) {
        return null;
    }

    @Override
    public List<ItemAttribute> listAttributesOf(ItemStack itemStack) {
        List<ItemAttribute> attributes = new ArrayList<>();
        for (VaultGearModifier.AffixType type : VaultGearModifier.AffixType.values()) {
            for (VaultGearModifier<?> modifier : getModifiers(itemStack, type)) {
                if (shouldList(type, modifier)) {
                    attributes.add(withValue(getName(type, modifier, false)));
                }
            }
        }
        return attributes;
    }

    @Override
    public ItemAttribute readNBT(CompoundTag compoundTag) {
        String key = getTranslationKey();
        byte type = compoundTag.getTagType(key);
        if (type == CompoundTag.TAG_STRING) {
            return withValue(compoundTag.getString(key));
        } else {
            String legacy = compoundTag.getString(getLegacyKey());
            if (legacy.contains("Cloud")) {

            } else if (legacy.contains("level")) {

            } else if (legacy.isBlank()) {

            }
            return withValue("Can't load attribute");
        }
    }
}
