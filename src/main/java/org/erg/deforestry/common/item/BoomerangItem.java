package org.erg.deforestry.common.item;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.erg.deforestry.common.entity.BoomerangEntity;
import org.erg.deforestry.common.registries.DeforestryEntityTypes;
import org.erg.deforestry.common.registries.DeforestrySounds;

public class BoomerangItem extends Item {

    public BoomerangItem(Item.Properties props) {
        super(props.durability(200));
    }

    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int usedDuration) {
        BoomerangEntity boomerang = new BoomerangEntity(
                (EntityType<? extends BoomerangEntity>) DeforestryEntityTypes.BOOMERANG_ENTITY.get(),
                level,
                entity,
                stack
        );
        if(entity instanceof Player player) {
            player.getInventory().removeItem(stack);
        }
        float durationDelta = (((float) this.getUseDuration(stack) - usedDuration) % 20) / 20.0f;
        boomerang.tossFromRotation(entity, entity.getXRot(), entity.getYRot(), 0.0f, durationDelta * 2.0f, 1.0f);
        level.addFreshEntity(boomerang);
        level.playSound((Player) null, entity, DeforestrySounds.BOOMERANG_THROW.get(), SoundSource.PLAYERS, 1.0f, 1.0f);
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }

}
