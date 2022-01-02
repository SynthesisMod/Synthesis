package com.luna.synthesis.utils;

import com.luna.synthesis.Comment;
import com.luna.synthesis.mixins.accessors.ItemModelMesherAccessor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.Item;

@Comment("This is a walmart solution. There's probably a better way to do what I'm trying to do.")
public class MixinUtils {

    public static GuiTextField inputField;

    public static IBakedModel getItemModel(ItemModelMesher imm, Item item, int meta) {
        ItemModelMesherAccessor imma = (ItemModelMesherAccessor) imm;
        return imma.invokeGetItemModel(item, meta);
    }

}
