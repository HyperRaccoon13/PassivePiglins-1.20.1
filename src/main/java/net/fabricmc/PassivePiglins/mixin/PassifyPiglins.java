package net.fabricmc.PassivePiglins.mixin;

import net.fabricmc.PassivePiglins.PassivePiglins;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;


@Mixin(PiglinBrain.class)
public abstract class PassifyPiglins {

	@Inject(method = "getPreferredTarget", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	private static void injected(PiglinEntity piglin, CallbackInfoReturnable<Optional<? extends LivingEntity>> cir) {
		Brain<PiglinEntity> brain = piglin.getBrain();
		Optional<PlayerEntity> optional3 = brain.getOptionalMemory(MemoryModuleType.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD);
		try {
			if (optional3.isPresent()) {
				if (optional3.get().getStackInHand(Hand.OFF_HAND).isIn(PassivePiglins.PIGLIN_RESPECTED) || optional3.get().getStackInHand(Hand.MAIN_HAND).isIn(PassivePiglins.PIGLIN_RESPECTED)) {
					cir.setReturnValue(Optional.empty());
				}
			}
		}
		catch (Exception e){

		}
	}

	@Inject(method = "consumeOffHandItem", at = @At("HEAD"), cancellable = true)
	private static void consumeOffHandItem(PiglinEntity piglin, boolean barter, CallbackInfo ci) {
		ItemStack itemStack = piglin.getStackInHand(Hand.OFF_HAND);
		if (itemStack.isIn(PassivePiglins.PIGLIN_OPTRADE_ITEM)) {
			piglin.setStackInHand(Hand.OFF_HAND, ItemStack.EMPTY);
			Optional<PlayerEntity> optional = piglin.getBrain().getOptionalMemory(MemoryModuleType.NEAREST_VISIBLE_PLAYER);
			if (optional.isPresent()) {
				piglin.swingHand(Hand.OFF_HAND);
				//ItemStack itemStackGive = new ItemStack(Items.SPONGE);
				LootTable lootTable = piglin.world.getServer().getLootManager().getTable(PassivePiglins.PIGLIN_OPTRADES);
				List<ItemStack> itemList = lootTable.generateLoot((new LootContext.Builder((ServerWorld)piglin.world)).parameter(LootContextParameters.THIS_ENTITY, piglin).random(piglin.world.random).build(LootContextTypes.BARTER));
				if (!itemList.isEmpty()) {
					piglin.swingHand(Hand.OFF_HAND);
					Iterator var3 = itemList.iterator();

					while(var3.hasNext()) {
						ItemStack itemStackGive = (ItemStack)var3.next();
						LookTargetUtil.give(piglin, itemStackGive, optional.get().getPos().add(0.0, 1.0, 0.0));
					}
				}

			}
		}
	}

	@Inject(method = "acceptsForBarter", at = @At("HEAD"), cancellable = true)
	private static void acceptsForBarter(ItemStack stack, CallbackInfoReturnable<Boolean> cir){
		if (stack.isIn(PassivePiglins.PIGLIN_BARTER_ITEM)){
			cir.setReturnValue(true);
		}
		else {
			cir.setReturnValue(false);
		}
	}
}
