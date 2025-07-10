package com.example.ae_inventory_profiles;

import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyTypesInternal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.anti_ad.mc.ipnext.config.ConfigEnumsExtKt;
import org.anti_ad.mc.ipnext.config.GuiSettings;
import org.anti_ad.mc.ipnext.config.SortingMethodIndividual;
import org.anti_ad.mc.ipnext.ingame.VanillaAccessorsKt;
import org.anti_ad.mc.ipnext.item.ItemType;
import org.anti_ad.mc.ipnext.item.rule.Rule;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Comparator;

public class IpnSortProxy implements Comparator<AEKey> {
    private static MethodHandle getItemTypeHandle;
    private final Rule rule;

    public IpnSortProxy() {
        rule = ConfigEnumsExtKt.rule((SortingMethodIndividual) GuiSettings.INSTANCE.getREGULAR_SORT_ORDER().getValue(), GuiSettings.INSTANCE.getREGULAR_CUSTOM_RULE().getValue());
        if (getItemTypeHandle == null) {
            getHandle();
        }
    }

    private static void getHandle() {
        try {
            MethodHandles.Lookup lookup = MethodHandles.publicLookup();
            getItemTypeHandle = lookup.findStatic(
                    VanillaAccessorsKt.class,
                    "get(itemType)",
                    MethodType.methodType(ItemType.class, ItemStack.class)
            );
            if (!(getItemTypeHandle.invoke(new ItemStack(Items.STONE)) instanceof ItemType)) {
                getItemTypeHandle = null;
            }
        } catch (Throwable e) {
            getItemTypeHandle = null;
        }
    }

    private static ItemType getItemType(ItemStack stack) {
        try {
            return (ItemType) getItemTypeHandle.invoke(stack);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static int getTypeRegistryId(AEKey key) {
        return AEKeyTypesInternal.getRegistry().getId(key.getType());
    }

    public int compareItem(AEItemKey o1, AEItemKey o2) {
        return rule.compare(getItemType(o1.toStack()), getItemType(o2.toStack()));
    }

    @Override
    public int compare(AEKey o1, AEKey o2) {
        if (o1 instanceof AEItemKey k1 && o2 instanceof AEItemKey k2) {
            return compareItem(k1, k2);
        } else {
            return Integer.compare(getTypeRegistryId(o1), getTypeRegistryId(o2));
        }
    }
}
