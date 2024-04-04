package necesse.entity.mobs.buffs.staticBuffs;

import java.awt.Color;
import java.util.concurrent.atomic.AtomicReference;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.HumanDrawBuff;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.gameTexture.GameTexture;

public class StarBarrierBuff extends Buff implements HumanDrawBuff {
   public GameTexture starBarrierTexture;

   public StarBarrierBuff() {
      this.canCancel = false;
      this.isImportant = true;
   }

   public void loadTextures() {
      super.loadTextures();
      this.starBarrierTexture = GameTexture.fromFile("particles/starbarrier");
   }

   public void init(ActiveBuff var1, BuffEventSubscriber var2) {
   }

   public int getStackSize() {
      return 4;
   }

   public boolean shouldDrawDuration(ActiveBuff var1) {
      return false;
   }

   public void clientTick(ActiveBuff var1) {
      super.clientTick(var1);
      if (var1.owner.isVisible() && var1.getStacks() < 4) {
         Mob var2 = var1.owner;
         GameRandom var3 = GameRandom.globalRandom;
         AtomicReference var4 = new AtomicReference(var3.nextFloat() * 360.0F);
         float var5 = (float)(150 - 30 * var1.getStacks());
         var2.getLevel().entityManager.addParticle(var2.x + GameMath.sin((Float)var4.get()) * var5, var2.y + GameMath.cos((Float)var4.get()) * var5, Particle.GType.CRITICAL).sprite(GameResources.magicSparkParticles.sprite(var3.nextInt(4), 0, 22)).color(new Color(184, 174, 255)).givesLight(247.0F, 0.3F).height(20.0F).moves((var4x, var5x, var6, var7, var8) -> {
            float var9 = (Float)var4.accumulateAndGet(var5x * 2.5F * (float)(var1.getStacks() * 2) / 250.0F, Float::sum);
            var4x.x = var2.x + GameMath.sin(var9) * var5;
            var4x.y = var2.y + GameMath.cos(var9) * var5;
         }).lifeTime(1000).sizeFades(16, 24);
      }

   }

   public void addHumanDraw(ActiveBuff var1, HumanDrawOptions var2) {
      if (var1.owner.buffManager.hasBuff(BuffRegistry.STAR_BARRIER_BUFF)) {
         var2.addTopDraw((var2x, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14, var15) -> {
            return this.starBarrierTexture.initDraw().sprite((int)(var2x.getLocalTime() / 100L) % 4, 0, 64).size(var9, var10).pos(var7, var8).alpha(0.25F * (float)var1.getStacks());
         });
      }

   }
}
