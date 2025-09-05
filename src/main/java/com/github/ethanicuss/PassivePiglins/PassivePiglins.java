package com.github.ethanicuss.PassivePiglins;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PassivePiglins implements ModInitializer {
	public static final String MOD_ID = "passivepiglins";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static TagKey<Item> registerTag(String path) {
		return TagKey.of(RegistryKeys.ITEM, new Identifier(path));
	}

	public static final TagKey<Item> PIGLIN_RESPECTED   = registerTag("piglin_respected");
	public static final TagKey<Item> PIGLIN_OPTRADE_ITEM= registerTag("piglin_optrade");
	public static final TagKey<Item> PIGLIN_BARTER_ITEM = registerTag("piglin_barter_item");


	public static final Identifier PIGLIN_OPTRADES = new Identifier("gameplay/piglin_optrades");
	public static final Item PIGLIN_COIN = new Item(new FabricItemSettings());
	public static final Item PIGLIN_FORTUNE = new Item(new FabricItemSettings());
	public static final Item PIGLIN_TOTEM = new Item(new FabricItemSettings());

	@Override
	public void onInitialize() {
		Registry.register(Registries.ITEM, new Identifier(MOD_ID, "piglin_coin"), PIGLIN_COIN);
		Registry.register(Registries.ITEM, new Identifier(MOD_ID, "piglin_fortune"), PIGLIN_FORTUNE);
		Registry.register(Registries.ITEM, new Identifier(MOD_ID, "piglin_totem"), PIGLIN_TOTEM);

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(entries -> {
			entries.add(PIGLIN_TOTEM);
		});

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {
			entries.add(PIGLIN_COIN);
			entries.add(PIGLIN_FORTUNE);
		});

		LOGGER.info("PassivePiglins is active!");
	}
}