package net.fabricmc.PassivePiglins;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PassivePiglins implements ModInitializer {
	public static final String MOD_ID = "passivepiglins";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static TagKey<Item> register(String id) {
		return TagKey.of(Registry.ITEM_KEY, new Identifier(id));
	}
	public static final TagKey<Item> PIGLIN_RESPECTED = register("piglin_respected");
	public static final TagKey<Item> PIGLIN_OPTRADE_ITEM = register("piglin_optrade");
	public static final TagKey<Item> PIGLIN_BARTER_ITEM = register("piglin_barter_item");
	public static final Identifier PIGLIN_OPTRADES = new Identifier("gameplay/piglin_optrades");
	public static final Item PIGLIN_COIN = new Item(new FabricItemSettings());
	public static final Item PIGLIN_FORTUNE = new Item(new FabricItemSettings());
	public static final Item PIGLIN_TOTEM = new Item(new FabricItemSettings());
	@Override
	public void onInitialize() {
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, "piglin_coin"), PIGLIN_COIN);
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, "piglin_fortune"), PIGLIN_FORTUNE);
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, "piglin_totem"), PIGLIN_TOTEM);
		LOGGER.info("PassivePiglins is active!");
	}//PiglinBrain btw
}
