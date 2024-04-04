package necesse.entity.levelEvent.mobAbilityLevelEvent;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Point2D;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.util.GroundPillar;
import necesse.engine.util.GroundPillarList;
import necesse.engine.util.Ray;
import necesse.engine.util.RayLinkedList;
import necesse.entity.manager.GroundPillarHandler;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobHitCooldowns;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.light.GameLight;

public class SlimeGreatbowEvent extends GroundEffectEvent {
   protected final GroundPillarList<SlimePillar> pillars = new GroundPillarList();
   protected int tickCounter;
   protected MobHitCooldowns hitCooldowns;
   protected GameDamage damage;
   protected float resilienceGain;
   protected int knockback;
   protected int eventDuration = 6000;
   protected Rectangle hitbox;

   public SlimeGreatbowEvent() {
   }

   public SlimeGreatbowEvent(Mob var1, int var2, int var3, GameRandom var4, GameDamage var5, float var6, int var7) {
      super(var1, var2, var3, var4);
      this.damage = var5;
      this.resilienceGain = var6;
      this.knockback = var7;
   }

   public void init() {
      super.init();
      this.tickCounter = 0;
      this.hitCooldowns = new MobHitCooldowns();
      this.hitbox = new Rectangle(this.x - 50, this.y - 50, 100, 100);
      new RayLinkedList();
      int var2 = Integer.MAX_VALUE;
      int var3 = Integer.MAX_VALUE;
      int var4 = Integer.MAX_VALUE;
      int var5 = Integer.MAX_VALUE;
      int[] var6 = new int[]{4, 5, 5, 6, 5, 5, 4};
      Point2D.Float[] var7 = new Point2D.Float[]{new Point2D.Float((float)(this.hitbox.x + this.hitbox.width), (float)this.hitbox.getCenterY()), new Point2D.Float((float)this.hitbox.getCenterX(), (float)(this.hitbox.y + this.hitbox.height)), new Point2D.Float((float)this.hitbox.x, (float)this.hitbox.getCenterY()), new Point2D.Float((float)this.hitbox.getCenterX(), (float)this.hitbox.y)};

      for(int var8 = 0; var8 < var7.length; ++var8) {
         Point2D.Float var9 = GameMath.normalize(var7[var8].x - (float)this.hitbox.getCenterX(), var7[var8].y - (float)this.hitbox.getCenterY());
         RayLinkedList var1 = GameUtils.castRay(this.level, (double)((float)this.hitbox.getCenterX()), (double)((float)this.hitbox.getCenterY()), (double)var9.x, (double)var9.y, var7[var8].distance((double)((float)this.hitbox.getCenterX()), (double)((float)this.hitbox.getCenterY())), 0, (new CollisionFilter()).projectileCollision().addFilter((var0) -> {
            return var0.object().object.isWall || var0.object().object.isRock;
         }));
         if (!var1.isEmpty()) {
            Ray var10 = (Ray)var1.getFirst();
            if (var10.targetHit != null) {
               switch (var8) {
                  case 0:
                     var4 = (int)var10.x2;
                     break;
                  case 1:
                     var5 = (int)var10.y2;
                     break;
                  case 2:
                     var2 = (int)var10.x2;
                     break;
                  case 3:
                     var3 = (int)var10.y2;
               }
            }
         }
      }

      if (var2 == Integer.MAX_VALUE) {
         var2 = this.hitbox.x;
      }

      if (var4 == Integer.MAX_VALUE) {
         var4 = this.hitbox.x + this.hitbox.width;
      }

      if (var3 == Integer.MAX_VALUE) {
         var3 = this.hitbox.y;
      }

      if (var5 == Integer.MAX_VALUE) {
         var5 = this.hitbox.y + this.hitbox.height;
      }

      this.hitbox = new Rectangle(var2, var3, var4 - var2, var5 - var3);
      if (this.isClient()) {
         GameRandom var21 = new GameRandom((long)this.getUniqueID());
         this.level.entityManager.addPillarHandler(new GroundPillarHandler<SlimePillar>(this.pillars) {
            protected boolean canRemove() {
               return SlimeGreatbowEvent.this.isOver();
            }

            public double getCurrentDistanceMoved() {
               return 0.0;
            }
         });
         byte var22 = 15;
         byte var23 = 8;
         float var11 = ((float)this.hitbox.height - (float)var22) / (float)(var6.length - 1);

         for(int var12 = 0; var12 < var6.length; ++var12) {
            int var13 = var6[var12];
            float var14 = (float)(this.hitbox.y + var22) + var11 * (float)var12;
            float var15 = ((float)this.hitbox.width - (float)var23) / (float)(var13 - 1);

            for(int var16 = 0; var16 < var13; ++var16) {
               float var17 = (float)(this.hitbox.x + var23) + var15 * (float)var16;
               int var18 = var21.getIntBetween(-var23 / 3, var23 / 3);
               int var19 = var21.getIntBetween(-var22 / 2, var22 / 2);
               Point2D.Float var20 = new Point2D.Float(var17 + (float)var18, var14 + (float)var19);
               this.pillars.add(new SlimePillar((int)var20.x, (int)var20.y, 0.0, this.getLocalTime(), this.eventDuration));
            }
         }
      }

      if (!this.isClient()) {
         GameUtils.streamTargets(this.owner, this.hitbox).filter((var1x) -> {
            return var1x.canBeHit(this) && var1x.getHitBox().intersects(this.hitbox);
         }).forEach((var1x) -> {
            var1x.isServerHit(this.damage, var1x.x - (float)this.x, var1x.y - (float)this.y, (float)this.knockback, this);
            if (var1x.canGiveResilience(this.owner) && this.resilienceGain != 0.0F) {
               this.owner.addResilience(this.resilienceGain);
               this.resilienceGain = 0.0F;
            }

         });
      }

   }

   public Shape getHitBox() {
      return this.hitbox;
   }

   public void clientHit(Mob var1) {
      var1.startHitCooldown();
      var1.addBuff(new ActiveBuff(BuffRegistry.Debuffs.SLIME_GREATBOW_SLOW_TARGET_DEBUFF, var1, 1.0F, this.owner), true);
   }

   public void serverHit(Mob var1, boolean var2) {
      if (var2 || this.hitCooldowns.canHit(var1)) {
         var1.addBuff(new ActiveBuff(BuffRegistry.Debuffs.SLIME_GREATBOW_SLOW_TARGET_DEBUFF, var1, 1.0F, this.owner), true);
         this.hitCooldowns.startCooldown(var1);
      }

   }

   public void hitObject(LevelObjectHit var1) {
      var1.getLevelObject().attackThrough(this.damage, this.owner);
   }

   public boolean canHit(Mob var1) {
      return super.canHit(var1) && this.hitCooldowns.canHit(var1);
   }

   public void clientTick() {
      ++this.tickCounter;
      if (this.tickCounter > 20 * (this.eventDuration / 1000)) {
         this.over();
      } else {
         super.clientTick();
      }

   }

   public void serverTick() {
      ++this.tickCounter;
      if (this.tickCounter > 20 * (this.eventDuration / 1000)) {
         this.over();
      } else {
         super.serverTick();
      }

   }

   public static class SlimePillar extends GroundPillar {
      public GameTextureSection texture;
      public boolean mirror;

      public SlimePillar(int var1, int var2, double var3, long var5, int var7) {
         super(var1, var2, var3, var5);
         this.mirror = GameRandom.globalRandom.nextBoolean();
         this.texture = null;
         GameTexture var8 = GameResources.slimeGreatbowSlime;
         if (var8 != null) {
            int var9 = var8.getHeight();
            int var10 = GameRandom.globalRandom.nextInt(var8.getWidth() / var9);
            this.texture = (new GameTextureSection(GameResources.slimeGreatbowSlime)).sprite(var10, 0, var9);
         }

         this.behaviour = new GroundPillar.TimedBehaviour(var7, 0, 250);
      }

      public DrawOptions getDrawOptions(Level var1, long var2, double var4, GameCamera var6) {
         GameLight var7 = var1.getLightLevel(this.x / 32, this.y / 32);
         int var8 = var6.getDrawX(this.x);
         int var9 = var6.getDrawY(this.y);
         double var10 = this.getHeight(var2, var4);
         int var12 = (int)(var10 * (double)this.texture.getHeight());
         return this.texture.section(0, this.texture.getWidth(), 0, var12).initDraw().mirror(this.mirror, false).light(var7).pos(var8 - this.texture.getWidth() / 2, var9 - var12 + 5);
      }
   }
}
