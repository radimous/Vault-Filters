package net.joseph.vaultfilters.mixin;

import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.data.GearDataCache;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import lv.id.bonne.vaulthunters.jewelsorting.vaulthunters.mixin.InvokerGearDataCache;
import net.joseph.vaultfilters.IVFGearDataCache;
import net.minecraft.nbt.ByteTag;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Mixin(value = GearDataCache.class, remap = false)
public class MixinGearDataCache implements IVFGearDataCache {
    @Inject(
        method = {"createCache"},
        at = {@At(
            value = "INVOKE",
            target = "Liskallia/vault/gear/data/GearDataCache;getJewelColorComponents()Ljava/util/List;"
        )},
        locals = LocalCapture.CAPTURE_FAILSOFT
    )
    private static void extraCreateCache(ItemStack stack, CallbackInfo ci, GearDataCache cache) {
        if (stack.getItem() instanceof VaultGearItem) {
            populateLegendaryCache(cache, stack);
        }
    }

    private static void populateLegendaryCache(GearDataCache cache, ItemStack itemStack) {
        VaultGearData data = VaultGearData.read(itemStack);
        List<VaultGearModifier<?>> affixes = new ArrayList<>();
        affixes.addAll(data.getModifiers(VaultGearModifier.AffixType.PREFIX));
        affixes.addAll(data.getModifiers(VaultGearModifier.AffixType.SUFFIX));
        boolean legendaryModifier = affixes.stream().anyMatch(prefix -> prefix.getCategory() == VaultGearModifier.AffixCategory.LEGENDARY);
        ((InvokerGearDataCache) cache)
            .callQueryCache("VFlegendary", tag -> ((ByteTag) tag).getAsByte(), ByteTag::valueOf, null, Function.identity(), stack -> legendaryModifier ? (byte) 1 : (byte) 0);
    }

    @Override
    public boolean hasLegendaryAttribute() {
        return ((InvokerGearDataCache) this)
            .callQueryCache("VFlegendary", tag -> ((ByteTag) tag).getAsByte(), ByteTag::valueOf, null, Function.identity(), stack -> {
                VaultGearData data = VaultGearData.read(stack);
                List<VaultGearModifier<?>> affixes = new ArrayList<>();
                affixes.addAll(data.getModifiers(VaultGearModifier.AffixType.PREFIX));
                affixes.addAll(data.getModifiers(VaultGearModifier.AffixType.SUFFIX));
                boolean legendaryModifier = affixes.stream().anyMatch(prefix -> prefix.getCategory() == VaultGearModifier.AffixCategory.LEGENDARY);
                return legendaryModifier ? (byte) 1 : (byte) 0;
            }) == 1;
    }
}
