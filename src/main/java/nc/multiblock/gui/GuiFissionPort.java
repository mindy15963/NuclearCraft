package nc.multiblock.gui;

import nc.Global;
import nc.gui.NCGui;
import nc.gui.element.GuiItemRenderer;
import nc.multiblock.container.ContainerFissionPort;
import nc.multiblock.fission.tile.TileFissionPort;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiFissionPort extends NCGui {
	
	protected final TileFissionPort port;
	protected final ResourceLocation gui_textures;

	public GuiFissionPort(EntityPlayer player, TileFissionPort port) {
		super(new ContainerFissionPort(player, port));
		this.port = port;
		gui_textures = new ResourceLocation(Global.MOD_ID + ":textures/gui/container/" + "fission_port" + ".png");
	}
	
	@Override
	public void renderTooltips(int mouseX, int mouseY) {
		
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		int fontColor = port.getMultiblock() != null && port.getMultiblock().isReactorOn ? -1 : 15641088;
		String s = port.getDisplayName().getUnformattedText();
		fontRenderer.drawString(s, xSize / 2 - fontRenderer.getStringWidth(s) / 2, 6, fontColor);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1F, 1F, 1F, 1F);
		mc.getTextureManager().bindTexture(gui_textures);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		
		if (!port.getFilterStacks().get(0).isEmpty()) {
			new GuiItemRenderer(guiLeft + 44, guiTop + 35, 0.5F, port.getFilterStacks().get(0)).draw();
		}
	}
	
	@Override
	public void initGui() {
		super.initGui();
		initButtons();
	}
	
	public void initButtons() {
		
	}
}
