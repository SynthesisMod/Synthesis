package com.luna.synthesis.mixins;

import com.luna.synthesis.Synthesis;
import net.minecraft.block.BlockPane;
import net.minecraft.block.BlockStainedGlassPane;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BlockStainedGlassPane.class)
public class BlockStainedGlassPaneMixin extends BlockPane {

    protected BlockStainedGlassPaneMixin(Material materialIn, boolean canDrop) {
        super(materialIn, canDrop);
    }

    @Shadow
    public static final PropertyEnum<EnumDyeColor> COLOR = PropertyEnum.create("color", EnumDyeColor.class);

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        if (Synthesis.getInstance() != null && Synthesis.getInstance().getConfig() != null) {
            if (Synthesis.getInstance().getConfig().utilitiesColorlessPanes) {
                return state.withProperty(NORTH, canPaneConnectTo(worldIn, pos, EnumFacing.NORTH))
                        .withProperty(SOUTH, canPaneConnectTo(worldIn, pos, EnumFacing.SOUTH))
                        .withProperty(WEST, canPaneConnectTo(worldIn, pos, EnumFacing.WEST))
                        .withProperty(EAST, canPaneConnectTo(worldIn, pos, EnumFacing.EAST))
                        .withProperty(COLOR, EnumDyeColor.SILVER);
            }
        }
        return super.getActualState(state, worldIn, pos);
    }
}
