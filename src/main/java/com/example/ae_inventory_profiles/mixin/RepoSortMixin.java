package com.example.ae_inventory_profiles.mixin;

import appeng.api.config.SortDir;
import appeng.api.config.SortOrder;
import appeng.api.stacks.AEKey;
import appeng.client.gui.me.common.Repo;
import com.example.ae_inventory_profiles.IpnSortProxy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.Comparator;

@Mixin(Repo.class)
public abstract class RepoSortMixin {
    @Unique
    @Nullable
    private Comparator<AEKey> aeinventoryprofiles$CUSTOM_ASC = null;

    @Unique
    @Nullable
    private Comparator<AEKey> aeinventoryprofiles$CUSTOM_DESC = null;

    @Inject(method = "getKeyComparator", at = @At("RETURN"), cancellable = true)
    public void onGetKeyComparator(SortOrder sortBy, SortDir sortDir, CallbackInfoReturnable<Comparator<AEKey>> cir) {
        if (sortBy == SortOrder.MOD) {
            if (aeinventoryprofiles$CUSTOM_ASC == null) {
                aeinventoryprofiles$CUSTOM_ASC = new IpnSortProxy().thenComparing(cir.getReturnValue());
            }
            if (sortDir == SortDir.ASCENDING) {
                cir.setReturnValue(aeinventoryprofiles$CUSTOM_ASC);
                return;
            }
            if (aeinventoryprofiles$CUSTOM_DESC == null) {
                aeinventoryprofiles$CUSTOM_DESC = aeinventoryprofiles$CUSTOM_ASC.reversed();
            }
            cir.setReturnValue(aeinventoryprofiles$CUSTOM_DESC);
        }
    }
}
