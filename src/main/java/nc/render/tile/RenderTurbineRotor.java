package nc.render.tile;

import nc.init.NCBlocks;
import nc.multiblock.turbine.Turbine;
import nc.multiblock.turbine.TurbineRotorBladeUtil;
import nc.multiblock.turbine.TurbineRotorBladeUtil.TurbinePartDir;
import nc.multiblock.turbine.tile.TileTurbineController;
import nc.multiblock.turbine.tile.TileTurbineRotorBlade;
import nc.multiblock.turbine.tile.TileTurbineRotorShaft;
import nc.multiblock.turbine.tile.TileTurbineRotorStator;
import nc.util.NCMath;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumFacing.AxisDirection;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderTurbineRotor extends TileEntitySpecialRenderer<TileTurbineController> {
	
	private static final Minecraft MC = Minecraft.getMinecraft();
	
	@Override
	public boolean isGlobalRenderer(TileTurbineController tile) {
		return tile.isRenderer && tile.isMultiblockAssembled();
	}
	
	@Override
	public void render(TileTurbineController tile, double posX, double posY, double posZ, float partialTicks, int destroyStage, float alpha) {
		if(!tile.isRenderer || !tile.isMultiblockAssembled()) return;
		
		Turbine turbine = tile.getMultiblock();
		EnumFacing dir = turbine.flowDir;
		if (dir == null) return;
		
		IBlockState shaftState = NCBlocks.turbine_rotor_shaft.getDefaultState().withProperty(TurbineRotorBladeUtil.DIR, turbine.getShaftDir());
		
		boolean[] isStatorSet = new boolean[turbine.getFlowLength()];
		for (TileTurbineRotorStator stator : turbine.getPartMap(TileTurbineRotorStator.class).values()) {
			isStatorSet[dir.getAxisDirection() == AxisDirection.POSITIVE ? turbine.getFlowLength() - stator.getDepth() - 1 : stator.getDepth()] = true;
		}
		
		BlockRendererDispatcher renderer = MC.getBlockRendererDispatcher();
		float brightness = tile.getWorld().getLightBrightness(turbine.getMinimumInteriorPlaneCoord(dir, turbine.getFlowLength()/2, turbine.bladeLength - 1, turbine.bladeLength - 1));
		MC.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		
		GlStateManager.pushMatrix();
		GlStateManager.color(1F, 1F, 1F, 1F);
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 0, 15*16);
		
		BlockPos pos = tile.getPos();
		double r = turbine.getRotorRadius();
		double rX = -turbine.getMaxX() + pos.getX() + (dir.getAxis() == Axis.X ? 0D : r);
		double rY = -turbine.getMaxY() + pos.getY() + (dir.getAxis() == Axis.Y ? 0D : r);
		double rZ = -turbine.getMaxZ() + pos.getZ() + (dir.getAxis() == Axis.Z ? 0D : r);
		
		double scale = r/Math.sqrt(r*r + NCMath.square(turbine.shaftWidth)/4D);
		
		GlStateManager.pushMatrix();
		GlStateManager.translate(posX - rX, posY - rY, posZ - rZ);
		GlStateManager.scale(dir.getAxis() == Axis.X ? 1D : scale, dir.getAxis() == Axis.Y ? 1D : scale, dir.getAxis() == Axis.Z ? 1D : scale);
		long systemTime = Minecraft.getSystemTime();
		if (!MC.isGamePaused()) turbine.rotorAngle = (turbine.rotorAngle + (systemTime - turbine.prevRenderTime)*turbine.angVel) % 360F;
		turbine.prevRenderTime = systemTime;
		GlStateManager.rotate(turbine.rotorAngle, dir.getAxis() == Axis.X ? 1F : 0F, dir.getAxis() == Axis.Y ? 1F : 0F, dir.getAxis() == Axis.Z ? 1F : 0F);
		GlStateManager.translate(-pos.getX() + rX, -pos.getY() + rY, -pos.getZ() + rZ);
		
		for (TileTurbineRotorShaft shaft : turbine.getPartMap(TileTurbineRotorShaft.class).values()) {
			if (!shaft.render || isStatorSet[shaft.depth]) continue;
			GlStateManager.pushMatrix();
			pos = shaft.getPos();
			GlStateManager.translate(pos.getX(), pos.getY(), pos.getZ());
			GlStateManager.rotate(-90F, 0F, 1F, 0F);
			renderer.renderBlockBrightness(shaftState, brightness);
			GlStateManager.popMatrix();
		}
		
		for (TileTurbineRotorBlade blade : turbine.getPartMap(TileTurbineRotorBlade.class).values()) {
			GlStateManager.pushMatrix();
			pos = blade.bladePos();
			TurbinePartDir bladeDir = blade.getDir();
			GlStateManager.translate(pos.getX(), pos.getY(), pos.getZ());
			GlStateManager.translate(0.5D, 0.5D, 0.5D);
			GlStateManager.rotate(blade.getRenderRotation()*(dir.getAxisDirection() == AxisDirection.POSITIVE ^ dir.getAxis() == Axis.X ? 1F : -1F), bladeDir == TurbinePartDir.X ? 1F : 0F, bladeDir == TurbinePartDir.Y ? 1F : 0F, bladeDir == TurbinePartDir.Z ? 1F : 0F);
			GlStateManager.translate(-0.5D, -0.5D, -0.5D);
			GlStateManager.rotate(-90F, 0F, 1F, 0F);
			renderer.renderBlockBrightness(blade.getRenderState(), brightness);
			GlStateManager.popMatrix();
		}
		GlStateManager.popMatrix();
		
		GlStateManager.pushMatrix();
		pos = tile.getPos();
		GlStateManager.translate(posX - rX, posY - rY, posZ - rZ);
		GlStateManager.scale(dir.getAxis() == Axis.X ? 1D : scale, dir.getAxis() == Axis.Y ? 1D : scale, dir.getAxis() == Axis.Z ? 1D : scale);
		GlStateManager.translate(-pos.getX() + rX, -pos.getY() + rY, -pos.getZ() + rZ);
		
		for (TileTurbineRotorShaft shaft : turbine.getPartMap(TileTurbineRotorShaft.class).values()) {
			if (!shaft.render || !isStatorSet[shaft.depth]) continue;
			GlStateManager.pushMatrix();
			pos = shaft.getPos();
			GlStateManager.translate(pos.getX(), pos.getY(), pos.getZ());
			GlStateManager.rotate(-90F, 0F, 1F, 0F);
			renderer.renderBlockBrightness(shaftState, brightness);
			GlStateManager.popMatrix();
		}
		
		for (TileTurbineRotorStator stator : turbine.getPartMap(TileTurbineRotorStator.class).values()) {
			GlStateManager.pushMatrix();
			pos = stator.bladePos();
			TurbinePartDir bladeDir = stator.getDir();
			GlStateManager.translate(pos.getX(), pos.getY(), pos.getZ());
			GlStateManager.translate(0.5D, 0.5D, 0.5D);
			GlStateManager.rotate(stator.getRenderRotation()*(dir.getAxisDirection() == AxisDirection.POSITIVE ^ dir.getAxis() == Axis.X ? 1F : -1F), bladeDir == TurbinePartDir.X ? 1F : 0F, bladeDir == TurbinePartDir.Y ? 1F : 0F, bladeDir == TurbinePartDir.Z ? 1F : 0F);
			GlStateManager.translate(-0.5D, -0.5D, -0.5D);
			GlStateManager.rotate(-90F, 0F, 1F, 0F);
			renderer.renderBlockBrightness(stator.getRenderState(), brightness);
			GlStateManager.popMatrix();
		}
		GlStateManager.popMatrix();
		
		GlStateManager.popMatrix();
	}
}
