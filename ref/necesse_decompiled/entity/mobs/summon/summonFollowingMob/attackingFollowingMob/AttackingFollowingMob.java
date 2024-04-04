package necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob;

import java.util.stream.Stream;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.EnchantmentRegistry;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.summon.summonFollowingMob.SummonedFollowingMob;
import necesse.gfx.camera.GameCamera;
import necesse.inventory.enchants.ToolItemEnchantment;
import necesse.inventory.enchants.ToolItemModifiers;

public abstract class AttackingFollowingMob extends SummonedFollowingMob {
   protected GameDamage damage = new GameDamage(0.0F);
   private ToolItemEnchantment enchantment;
   private ModifierValue<?>[] modifiers;

   public AttackingFollowingMob(int var1) {
      super(var1);
      this.enchantment = ToolItemEnchantment.noEnchant;
      this.modifiers = new ModifierValue[0];
   }

   public void updateDamage(GameDamage var1) {
      this.damage = var1;
   }

   public void setEnchantment(ToolItemEnchantment var1) {
      this.enchantment = var1;
      this.modifiers = new ModifierValue[]{new ModifierValue(BuffModifiers.ALL_DAMAGE, (Float)var1.getModifier(ToolItemModifiers.DAMAGE)), new ModifierValue(BuffModifiers.SPEED, (Float)var1.getModifier(ToolItemModifiers.SUMMONS_SPEED)), new ModifierValue(BuffModifiers.CHASER_RANGE, (Float)var1.getModifier(ToolItemModifiers.SUMMONS_TARGET_RANGE)), new ModifierValue(BuffModifiers.KNOCKBACK_OUT, (Float)var1.getModifier(ToolItemModifiers.KNOCKBACK))};
   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      this.setEnchantment((ToolItemEnchantment)EnchantmentRegistry.getEnchantment(var1.getNextShortUnsigned(), ToolItemEnchantment.class, ToolItemEnchantment.noEnchant));
   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      var1.putNextShortUnsigned(this.enchantment.getID());
   }

   public float getSpeedModifier() {
      if (this.isFollowing()) {
         Mob var1 = this.getAttackOwner();
         if (var1 != null) {
            return (Float)var1.buffManager.getModifier(BuffModifiers.SUMMONS_SPEED) * super.getSpeedModifier();
         }
      }

      return super.getSpeedModifier();
   }

   public boolean canTarget(Mob var1) {
      return var1.isHostile || var1.getUniqueID() == this.getFollowerFocusMobUniqueID();
   }

   public int getFollowerFocusMobUniqueID() {
      if (this.isServer()) {
         ServerClient var1 = this.getFollowingServerClient();
         if (var1 != null) {
            return var1.summonFocus == null ? -1 : var1.summonFocus.getUniqueID();
         }
      } else if (this.isClient()) {
         ClientClient var2 = this.getFollowingClientClient();
         if (var2 != null) {
            return var2.summonFocusMobUniqueID;
         }
      }

      return -1;
   }

   public Mob getFollowerFocusMob() {
      if (this.isServer()) {
         ServerClient var1 = this.getFollowingServerClient();
         if (var1 != null) {
            return var1.summonFocus;
         }
      } else if (this.isClient()) {
         ClientClient var2 = this.getFollowingClientClient();
         if (var2 != null) {
            if (var2.summonFocusMobUniqueID == -1) {
               return null;
            }

            return GameUtils.getLevelMob(var2.summonFocusMobUniqueID, this.getLevel(), true);
         }
      }

      return null;
   }

   public boolean onMouseHover(GameCamera var1, PlayerMob var2, boolean var3) {
      return !var3 ? false : super.onMouseHover(var1, var2, var3);
   }

   public Stream<ModifierValue<?>> getDefaultModifiers() {
      return this.modifiers == null ? super.getDefaultModifiers() : Stream.of(this.modifiers);
   }
}
