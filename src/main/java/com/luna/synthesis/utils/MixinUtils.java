package com.luna.synthesis.utils;

import com.luna.synthesis.mixins.accessors.ItemModelMesherAccessor;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.Item;

// This is the absolute wrong way to do this BUT until I figure out something better, shrug
public class MixinUtils {

    public static GuiTextField inputField;

    public static IBakedModel getItemModel(ItemModelMesher imm, Item item, int meta) {
        ItemModelMesherAccessor imma = (ItemModelMesherAccessor) imm;
        return imma.invokeGetItemModel(item, meta);
    }

}
