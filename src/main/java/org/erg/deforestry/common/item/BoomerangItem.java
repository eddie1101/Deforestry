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
import org.erg.deforestry.Deforestry;
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
        float durationDelta = (float) this.getUseDuration(stack) - usedDuration;
        float power = durationDelta / 20.0f;
        power = (power * power + power * 2.0f) / 3.0f;
        if(power > 1.0f) {
            power = 1.0f;
        } else if(power < 0.2f) {
            return;
        }

        BoomerangEntity boomerang = new BoomerangEntity(
                (EntityType<? extends BoomerangEntity>) DeforestryEntityTypes.BOOMERANG_ENTITY.get(),
                level,
                entity,
                stack
        );
        if(entity instanceof Player player) {
            player.getInventory().removeItem(stack);
        }

        boomerang.shootFromRotation(entity, entity.getXRot(), entity.getYRot(), 0.0f, power * 2.0f, 0.5f + power / 2.0f);
        level.addFreshEntity(boomerang);
        level.playSound((Player) null, entity, DeforestrySounds.BOOMERANG_THROW.get(), SoundSource.PLAYERS, 0.5f + power, 1.0f + power / 2.0f);
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }

}
