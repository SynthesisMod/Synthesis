package com.luna.synthesis.mixins.patcher;

import com.luna.synthesis.Synthesis;
import com.luna.synthesis.core.Config;
import gg.essential.api.utils.TrustedHostsUtil;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Pseudo
@Mixin(targets = "club.sk1er.patcher.screen.render.overlay.ImagePreview", remap = false)
public class ImagePreviewerMixin {

    private final Config config = Synthesis.getInstance().getConfig();

    // Probably not the way to do this since TrustedHostUtil::addTrustedHost exists BUT don't care.
    @Dynamic
    @Redirect(method = "handle(Ljava/lang/String;)V", at = @At(value = "INVOKE", target = "Lgg/essential/api/utils/TrustedHostsUtil;getTrustedHosts()Ljava/util/Set;"))
    public Set<TrustedHostsUtil.TrustedHost> equalsIgnoreCase(TrustedHostsUtil util) {
        if (config.patcherCustomImagePreviewer && !config.patcherCustomDomains.equals("")) {
            Set<TrustedHostsUtil.TrustedHost> newHosts = new HashSet<>(util.getTrustedHosts());
            for (String s : config.patcherCustomDomains.split(",")) {
                newHosts.add(new TrustedHostsUtil.TrustedHost(s, s, Collections.singleton(s)));
            }
            return newHosts;
        } else {
            return util.getTrustedHosts();
        }
    }
}
