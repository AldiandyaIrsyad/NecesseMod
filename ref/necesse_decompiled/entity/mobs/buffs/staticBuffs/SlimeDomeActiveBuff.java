package necesse.entity.mobs.buffs.staticBuffs;

import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.HumanDrawBuff;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.light.GameLight;

public class SlimeDomeActiveBuff extends Buff implements HumanDrawBuff {
   public GameTexture slimeDomeTexture;

   public SlimeDomeActiveBuff() {
      this.isVisible = true;
      this.isImportant = true;
   }

   public void init(ActiveBuff var1, BuffEventSubscriber var2) {
      var1.setModifier(BuffModifiers.INCOMING_DAMAGE_MOD, 0.0F);
      var1.setModifier(BuffModifiers.KNOCKBACK_INCOMING_MOD, 1.0F);
      var1.setMaxModifier(BuffModifiers.FRICTION, 0.25F, 1000);
      var1.setModifier(BuffModifiers.BOUNCY, true);
      var1.setModifier(BuffModifiers.SPEED, 0.5F);
   }

   public void loadTextures() {
      super.loadTextures();
      this.slimeDomeTexture = GameTexture.fromFile("particles/slimedome");
   }

   public void onWasHit(ActiveBuff var1, MobWasHitEvent var2) {
      super.onWasHit(var1, var2);
      if (!var2.wasPrevented) {
         var1.getGndData().setLong("timeHit", var1.owner.getTime());
      }

   }

   public float getBouncyAnimation(long var1, int var3) {
      float var4;
      if (var1 > (long)var3) {
         var4 = GameUtils.getAnimFloat(var1 - (long)var3, 200);
         return (float)(Math.sin((double)var4 * Math.PI * 2.0) + 1.0) / 2.0F;
      } else {
         var4 = Math.abs((float)var1 / (float)var3 - 1.0F);
         return (float)Math.abs((Math.cos(18.84955592153876 / (double)(var4 + 0.5F)) + 1.0) / 2.0);
      }
   }

   public void addHumanDraw(ActiveBuff var1, HumanDrawOptions var2) {
      if (var1.owner.buffManager.hasBuff(BuffRegistry.SLIME_DOME_ACTIVE)) {
         long var3 = var1.getGndData().getLong("timeHit");
         long var7 = var1.owner.getTime() - var3;
         final float var5;
         final float var6;
         if (var3 != 0L && var7 <= 2000L) {
            float var9 = this.getBouncyAnimation(2000L - var7, 2000);
            float var10 = GameMath.lerp(var9, 0.85F, 1.15F);
            float var11 = GameMath.lerp(var9, 1.15F, 0.85F);
            float var12 = (float)var7 / 2000.0F;
            var5 = GameMath.lerp(var12, var10, 1.0F);
            var6 = GameMath.lerp(var12, var11, 1.0F);
         } else {
            var5 = 1.0F;
            var6 = 1.0F;
         }

         var2.addTopDraw(new HumanDrawOptions.HumanDrawOptionsGetter() {
            public DrawOptions getDrawOptions(PlayerMob var1, int var2, int var3, int var4, int var5x, int var6x, int var7, int var8, int var9, boolean var10, boolean var11, GameLight var12, float var13, GameTexture var14) {
               int var15 = (int)((float)var8 * var5);
               int var16 = (int)((float)var9 * var6);
               int var17 = var8 - var15;
               int var18 = var9 - var16;
               return SlimeDomeActiveBuff.this.slimeDomeTexture.initDraw().size(var15, var16).light(var12).pos(var6x + var17 / 2 + 2, var7 + var18 / 2 + 2);
            }
         });
      }

   }
}
