package com.luna.synthesis.mixins.accessors;

import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ItemModelMesher.class)
public interface ItemModelMesherAccessor {

    @Invoker IBakedModel invokeGetItemModel(Item item, int meta);
}
