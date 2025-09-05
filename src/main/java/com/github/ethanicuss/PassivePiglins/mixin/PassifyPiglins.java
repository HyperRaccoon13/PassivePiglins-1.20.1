package com.github.ethanicuss.PassivePiglins.mixin;

import com.github.ethanicuss.PassivePiglins.PassivePiglins;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;


@Mixin(PiglinBrain.class)
public abstract class PassifyPiglins {

	@Inject(method = "getPreferredTarget", at = @At("HEAD"), cancellable = true)
	private static void injected(PiglinEntity piglin, CallbackInfoReturnable<Optional<? extends LivingEntity>> cir) {
		Brain<PiglinEntity> brain = piglin.getBrain();
		Optional<PlayerEntity> nearestNoGold = brain.getOptionalRegisteredMemory(MemoryModuleType.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD);

		if (nearestNoGold.isPresent()) {
			PlayerEntity playerEntity = nearestNoGold.get();
			boolean holdingTotem = playerEntity.getStackInHand(Hand.MAIN_HAND).isIn(PassivePiglins.PIGLIN_RESPECTED) || playerEntity.getStackInHand(Hand.OFF_HAND).isIn(PassivePiglins.PIGLIN_RESPECTED);
			if (holdingTotem) {
				cir.setReturnValue(Optional.empty());
			}
		}
	}

	@Inject(method = "consumeOffHandItem", at = @At("HEAD"), cancellable = true)
	private static void pp$consumeOffHandItem(PiglinEntity piglin, boolean barter, CallbackInfo ci) {
		ItemStack offhand = piglin.getStackInHand(Hand.OFF_HAND);
		if (!offhand.isIn(PassivePiglins.PIGLIN_OPTRADE_ITEM)) return;

		piglin.setStackInHand(Hand.OFF_HAND, ItemStack.EMPTY);
		Optional<PlayerEntity> target = piglin.getBrain().getOptionalRegisteredMemory(MemoryModuleType.NEAREST_VISIBLE_PLAYER);
		target.ifPresent(t -> piglin.swingHand(Hand.OFF_HAND));
		ServerWorld server = (ServerWorld) piglin.getWorld();

		LootTable table = server.getServer().getLootManager()
				.getLootTable(PassivePiglins.PIGLIN_OPTRADES);

		LootContextParameterSet params = new LootContextParameterSet.Builder(server)
				.add(LootContextParameters.THIS_ENTITY, piglin)
				.build(LootContextTypes.BARTER);

		List<ItemStack> drops = table.generateLoot(params);

		if (!drops.isEmpty()) {
			piglin.swingHand(Hand.OFF_HAND);
			Vec3d dropPos = target.map(Entity::getPos).orElse(piglin.getPos()).add(0, 1, 0);

			for (ItemStack out : drops) {
				LookTargetUtil.give(piglin, out, dropPos);
			}
		}
		ci.cancel();
	}

	@Inject(method = "acceptsForBarter", at = @At("HEAD"), cancellable = true)
	private static void pp$acceptsForBarter(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(stack.isIn(PassivePiglins.PIGLIN_BARTER_ITEM));
	}
}