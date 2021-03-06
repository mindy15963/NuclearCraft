package nc;

import java.util.List;
import java.util.Locale;

import com.google.common.collect.Lists;

import nc.config.NCConfig;
import nc.enumm.IFissionFuelEnum;
import nc.enumm.IHeatSinkEnum;
import nc.enumm.IMetaEnum;
import nc.enumm.MetaEnums;
import nc.multiblock.turbine.TurbineDynamoCoilType;
import nc.radiation.RadiationHelper;
import nc.recipe.ProcessorRecipe;
import nc.util.InfoHelper;
import nc.util.Lang;
import nc.util.NCMath;
import nc.util.UnitHelper;
import net.minecraft.util.IStringSerializable;

public class NCInfo {
	
	// Fission Fuel
	
	public static <T extends Enum<T> & IFissionFuelEnum> String[][] fissionFuelInfo(T[] values) {
		String[][] info = new String[values.length][];
		for (int i = 0; i < values.length; i++) {
			List<String> list = Lists.newArrayList(
					Lang.localise("item." + Global.MOD_ID + ".fission_fuel.base_time.desc", UnitHelper.applyTimeUnit(values[i].getBaseTime()*NCConfig.fission_fuel_time_multiplier, 3)),
					Lang.localise("item." + Global.MOD_ID + ".fission_fuel.base_heat.desc", UnitHelper.prefix(values[i].getBaseHeat(), 5, "H/t")),
					Lang.localise("item." + Global.MOD_ID + ".fission_fuel.base_efficiency.desc", Math.round(100D*values[i].getBaseEfficiency()) + "%"),
					Lang.localise("item." + Global.MOD_ID + ".fission_fuel.criticality.desc", values[i].getCriticality() + " N/t")
					);
			if (values[i].getSelfPriming()) {
				list.add(Lang.localise("item." + Global.MOD_ID + ".fission_fuel.self_priming.desc"));
			}
			info[i] = list.toArray(new String[list.size()]);
		}
		return info;
	}
	
	// Fission Heat Sinks
	
	private static <T extends Enum<T> & IStringSerializable & IHeatSinkEnum> String[][] heatSinkFixedInfo(T[] values) {
		String[][] info = new String[values.length][];
		for (int i = 0; i < values.length; i++) {
			info[i] = new String[] {sinkCoolingRateString(values[i])};
		}
		return info;
	}
	
	private static <T extends Enum<T> & IHeatSinkEnum> String sinkCoolingRateString(T type) {
		return Lang.localise("tile." + Global.MOD_ID + ".solid_fission_sink.cooling_rate") + " " + type.getCooling() + " H/t";
	}
	
	public static String[][] heatSinkFixedInfo() {
		return heatSinkFixedInfo(MetaEnums.HeatSinkType.values());
	}
	
	public static String[][] heatSinkFixedInfo2() {
		return heatSinkFixedInfo(MetaEnums.HeatSinkType2.values());
	}
	
	public static String[][] heatSinkInfo() {
		MetaEnums.HeatSinkType[] values = MetaEnums.HeatSinkType.values();
		String[][] info = new String[values.length][];
		for (int i = 0; i < values.length; i++) {
			info[i] = InfoHelper.formattedInfo(sinkInfoString(values[i]));
		}
		return info;
	}
	
	public static String[][] heatSinkInfo2() {
		MetaEnums.HeatSinkType2[] values = MetaEnums.HeatSinkType2.values();
		String[][] info = new String[values.length][];
		for (int i = 0; i < values.length; i++) {
			info[i] = InfoHelper.formattedInfo(sinkInfoString2(values[i]));
		}
		return info;
	}
	
	private static String sinkInfoString(MetaEnums.HeatSinkType type) {
		return Lang.localise("tile." + Global.MOD_ID + ".solid_fission_sink." + type.getName() + ".desc");
	}
	
	private static String sinkInfoString2(MetaEnums.HeatSinkType2 type) {
		return Lang.localise("tile." + Global.MOD_ID + ".solid_fission_sink2." + type.getName() + ".desc");
	}
	
	// Fission Neutron Sources
	
	public static String[][] neutronSourceFixedInfo() {
		MetaEnums.NeutronSourceType[] values = MetaEnums.NeutronSourceType.values();
		String[][] info = new String[values.length][];
		for (int i = 0; i < values.length; i++) {
			info[i] = new String[] {
					Lang.localise("info." + Global.MOD_ID + ".fission_source.efficiency.fixd", Math.round(100D*values[i].getEfficiency()) + "%"),
					};
		}
		return info;
	}
	
	public static String[][] neutronSourceInfo() {
		MetaEnums.NeutronSourceType[] values = MetaEnums.NeutronSourceType.values();
		String[][] info = new String[values.length][];
		for (int i = 0; i < values.length; i++) {
			info[i] = InfoHelper.formattedInfo(Lang.localise("tile." + Global.MOD_ID + ".fission_source.desc"));
		}
		return info;
	}
	
	// Fission Moderators
	
	public static String[] fissionModeratorFixedInfo(ProcessorRecipe moderatorInfo) {
		return new String[] {
				Lang.localise("info." + Global.MOD_ID + ".moderator.fixd"),
				Lang.localise("info." + Global.MOD_ID + ".moderator.flux_factor.fixd", moderatorInfo.getFissionModeratorFluxFactor() + " N/t"),
				Lang.localise("info." + Global.MOD_ID + ".moderator.efficiency.fixd", Math.round(100D*moderatorInfo.getFissionModeratorEfficiency()) + "%")
				};
	}
	
	public static String[] fissionModeratorInfo() {
		return InfoHelper.formattedInfo(Lang.localise("info." + Global.MOD_ID + ".moderator.desc", NCConfig.fission_neutron_reach, NCConfig.fission_neutron_reach/2));
	}
	
	// Fission Reflectors
	
	public static String[] fissionReflectorFixedInfo(ProcessorRecipe reflectorInfo) {
		return new String[] {
				Lang.localise("info." + Global.MOD_ID + ".reflector.fixd"),
				Lang.localise("info." + Global.MOD_ID + ".reflector.reflectivity.fixd", Math.round(100D*reflectorInfo.getFissionReflectorReflectivity()) + "%"),
				Lang.localise("info." + Global.MOD_ID + ".reflector.efficiency.fixd", Math.round(100D*reflectorInfo.getFissionReflectorEfficiency()) + "%")
				};
	}
	
	public static String[] fissionReflectorInfo() {
		return InfoHelper.formattedInfo(Lang.localise("info." + Global.MOD_ID + ".reflector.desc"));
	}
	
	// Dynamo Coils
	
	public static String[][] dynamoCoilFixedInfo() {
		String[][] info = new String[TurbineDynamoCoilType.values().length][];
		info[0] = new String[] {};
		for (int i = 0; i < TurbineDynamoCoilType.values().length; i++) {
			info[i] = new String[] {coilConductivityString(i)};
		}
		return info;
	}
	
	public static String[][] dynamoCoilInfo() {
		String[][] info = new String[TurbineDynamoCoilType.values().length][];
		info[0] = new String[] {};
		for (int i = 0; i < TurbineDynamoCoilType.values().length; i++) {
			info[i] = InfoHelper.formattedInfo(coiInfoString(i));
		}
		return info;
	}
	
	private static String coilConductivityString(int meta) {
		return Lang.localise("tile." + Global.MOD_ID + ".turbine_dynamo_coil.conductivity") + " " + NCMath.decimalPlaces(100D*TurbineDynamoCoilType.values()[meta].getConductivity(), 1) + "%";
	}
	
	private static String coiInfoString(int meta) {
		return Lang.localise("tile." + Global.MOD_ID + ".turbine_dynamo_coil." + TurbineDynamoCoilType.values()[meta].name().toLowerCase(Locale.ROOT) + ".desc");
	}
	
	// Speed Upgrade
	
	public static final String[] POLY_POWER = new String[] {"linearly", "quadratically", "cubicly", "quarticly", "quinticly", "sexticly", "septicly", "octicly", "nonicly", "decicly", "undecicly", "duodecicly", "tredecicly", "quattuordecicly", "quindecicly"};
	
	public static String[][] upgradeInfo() {
		String[][] info = new String[MetaEnums.UpgradeType.values().length][];
		for (int i = 0; i < MetaEnums.UpgradeType.values().length; i++) {
			info[i] = InfoHelper.EMPTY_ARRAY;
		}
		info[0] = InfoHelper.formattedInfo(Lang.localise("item.nuclearcraft.upgrade.speed_desc", powerString(NCConfig.speed_upgrade_power_laws[0]), powerString(NCConfig.speed_upgrade_power_laws[1])));
		info[1] = InfoHelper.formattedInfo(Lang.localise("item.nuclearcraft.upgrade.energy_desc", powerString(NCConfig.energy_upgrade_power_laws[0])));
		return info;
	}
	
	private static String powerString(double power) {
		return (power == (int)power ? "" : Lang.localise("info.nuclearcraft.approximately" + " ")) + POLY_POWER[(int)Math.round(power) - 1];
	}
	
	// Extra Ore Drops
	
	public static <T extends Enum<T> & IStringSerializable & IMetaEnum> String[][] oreDropInfo(String type, T[] values, int[] configIds, int[] metas) {
		String[][] info = new String[values.length][];
		for (int i = 0; i < values.length; i++) {
			info[i] = null;
		}
		for (int i = 0; i < configIds.length; i++) {
			String unloc = "item." + Global.MOD_ID + "." + type + "." + values[metas[i]].getName() + ".desc";
			if (Lang.canLocalise(unloc) && NCConfig.ore_drops[configIds[i]]) info[metas[i]] = InfoHelper.formattedInfo(Lang.localise(unloc));
		}
		
		return info;
	}
	
	public static <T extends Enum<T> & IStringSerializable & IMetaEnum> String[][] dustOreDropInfo() {
		return oreDropInfo("dust", MetaEnums.DustType.values(), new int[] {1, 2}, new int[] {9, 10});
	}
	
	public static <T extends Enum<T> & IStringSerializable & IMetaEnum> String[][] gemOreDropInfo() {
		return oreDropInfo("gem", MetaEnums.GemType.values(), new int[] {0, 3, 5, 6}, new int[] {0, 2, 3, 4});
	}
	
	public static <T extends Enum<T> & IStringSerializable & IMetaEnum> String[][] gemDustOreDropInfo() {
		return oreDropInfo("gem_dust", MetaEnums.GemDustType.values(), new int[] {4}, new int[] {6});
	}
	
	// Rad Shielding
	
	public static String[][] radShieldingInfo() {
		String[][] info = new String[MetaEnums.RadShieldingType.values().length][];
		for (int i = 0; i < MetaEnums.RadShieldingType.values().length; i++) {
			info[i] = InfoHelper.formattedInfo(Lang.localise("item.nuclearcraft.rad_shielding.desc" + (NCConfig.radiation_hardcore_containers > 0D ? "_hardcore" : ""), RadiationHelper.resistanceSigFigs(NCConfig.radiation_shielding_level[i])));
		}
		return info;
	}
}
