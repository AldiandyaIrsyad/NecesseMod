package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs;

import java.awt.geom.Point2D;
import necesse.engine.localization.Localization;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.mobAbilityLevelEvent.DawnSwirlEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.DawnFireballProjectile;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.toolItem.ToolItem;
import necesse.level.maps.Level;

public class DawnSetBonusBuff extends SetBonusBuff {
   public DawnSetBonusBuff() {
   }

   public void init(ActiveBuff var1, BuffEventSubscriber var2) {
   }

   public void clientTick(ActiveBuff var1) {
      Mob var2 = var1.owner;
      Level var3 = var2.getLevel();
      if (var3 != null && var2.isVisible() && !var3.getWorldEntity().isNight()) {
         var3.entityManager.addParticle(var2.x + (float)(GameRandom.globalRandom.nextGaussian() * 10.0), var2.y + (float)(GameRandom.globalRandom.nextGaussian() * 8.0), Particle.GType.IMPORTANT_COSMETIC).sizeFades(5, 10).lifeTime(1500).movesConstantAngle((float)GameRandom.globalRandom.getIntBetween(0, 360), 3.0F).flameColor(45.0F).givesLight(50.0F, 1.0F).height(16.0F);
      }

   }

   public void onItemAttacked(ActiveBuff var1, int var2, int var3, PlayerMob var4, int var5, InventoryItem var6, PlayerInventorySlot var7, int var8) {
      super.onItemAttacked(var1, var2, var3, var4, var5, var6, var7, var8);
      Level var9 = var1.owner.getLevel();
      if (!var9.getWorldEntity().isNight() && var9.isServer() && var6.item instanceof ToolItem) {
         ToolItem var10 = (ToolItem)var6.item;
         if (var10.getDamageType(var6) == DamageTypeRegistry.MELEE) {
            this.AddDawnSwirl(var1, var4, var9, var2, var3);
         } else if (var10.getDamageType(var6) == DamageTypeRegistry.RANGED) {
            this.shootDawnFireball(var1, var4, var9, var2, var3);
         }
      }

   }

   public ListGameTooltips getTooltip(ActiveBuff var1, GameBlackboard var2) {
      ListGameTooltips var3 = super.getTooltip(var1, var2);
      var3.add((String)Localization.translate("itemtooltip", "dawnset"), 400);
      return var3;
   }

   private void AddDawnSwirl(ActiveBuff var1, PlayerMob var2, Level var3, int var4, int var5) {
      String var6 = "dawnswirl";
      long var7 = var1.getGndData().getLong(var6);
      if (var7 + 500L < var3.getWorldEntity().getTime()) {
         var1.getGndData().setLong(var6, var3.getWorldEntity().getTime());
         GameRandom var9 = GameRandom.globalRandom;
         float var10 = (Float)var2.buffManager.getModifier(BuffModifiers.ALL_DAMAGE);
         var10 += (Float)var2.buffManager.getModifier(BuffModifiers.MELEE_DAMAGE);
         var10 = (Float)BuffModifiers.ALL_DAMAGE.finalLimit(var10);
         float var11 = 25.0F * var10;
         int var12 = (Integer)var2.buffManager.getModifier(BuffModifiers.ARMOR_PEN_FLAT);
         GameDamage var13 = new GameDamage(var11, (float)var12);
         DawnSwirlEvent var14 = new DawnSwirlEvent(var2, var2.getX(), var2.getY(), var9, var13);
         var3.entityManager.addLevelEvent(var14);
      }

   }

   private void shootDawnFireball(ActiveBuff var1, PlayerMob var2, Level var3, int var4, int var5) {
      String var6 = "dawnfireball";
      long var7 = var1.getGndData().getLong(var6);
      float var9 = (Float)var2.buffManager.getModifier(BuffModifiers.ATTACK_SPEED);
      var9 += (Float)var2.buffManager.getModifier(BuffModifiers.RANGED_ATTACK_SPEED);
      var9 = (Float)BuffModifiers.ATTACK_SPEED.finalLimit(var9);
      int var10 = Math.round(500.0F * (1.0F / var9));
      if (var7 + (long)var10 < var3.getWorldEntity().getTime()) {
         var1.getGndData().setLong(var6, var3.getWorldEntity().getTime());
         Point2D.Float var11 = GameMath.normalize(var2.x - (float)var4, var2.y - (float)var5);
         float var12 = GameMath.getAngle(var11);
         float var13 = 100.0F * (Float)var2.buffManager.getModifier(BuffModifiers.PROJECTILE_VELOCITY);
         float var14 = (Float)var2.buffManager.getModifier(BuffModifiers.ALL_DAMAGE);
         var14 += (Float)var2.buffManager.getModifier(BuffModifiers.MELEE_DAMAGE);
         var14 = (Float)BuffModifiers.ALL_DAMAGE.finalLimit(var14);
         float var15 = 25.0F * var14;
         int var16 = (Integer)var2.buffManager.getModifier(BuffModifiers.ARMOR_PEN_FLAT);
         GameDamage var17 = new GameDamage(var15, (float)var16);
         DawnFireballProjectile var18 = new DawnFireballProjectile(var3, var2, var2.x, var2.y, var12 - 100.0F, var13, 750, var17, 0, false);
         DawnFireballProjectile var19 = new DawnFireballProjectile(var3, var2, var2.x, var2.y, var12 - 80.0F, var13, 750, var17, 0, false);
         var3.entityManager.projectiles.add(var18);
         var3.entityManager.projectiles.add(var19);
      }

   }
}
