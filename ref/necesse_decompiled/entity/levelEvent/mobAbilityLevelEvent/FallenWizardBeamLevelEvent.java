package necesse.entity.levelEvent.mobAbilityLevelEvent;

import java.awt.Color;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.Iterator;
import java.util.function.Function;
import necesse.engine.Screen;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.sound.LinesSoundEmitter;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundPlayer;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.util.LineHitbox;
import necesse.engine.util.Ray;
import necesse.engine.util.RayLinkedList;
import necesse.entity.ParticleBeamHandler;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobHitCooldowns;
import necesse.entity.mobs.hostile.bosses.FallenWizardMob;
import necesse.gfx.GameResources;
import necesse.gfx.gameTexture.GameSprite;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.LevelObjectHit;

public class FallenWizardBeamLevelEvent extends MobAbilityLevelEvent implements LinesSoundEmitter {
   public int seed;
   public long startTime;
   public int sweepTime;
   public float startAngle;
   public float endAngle;
   public float height = 14.0F;
   protected int bounces;
   protected float distance;
   protected GameDamage damage;
   protected int knockback;
   protected MobHitCooldowns hitCooldowns;
   private RayLinkedList<LevelObjectHit> lastRays;
   private ParticleBeamHandler beamHandler;
   private FallenWizardMob fallenWizardMob;
   private SoundPlayer sound;

   public FallenWizardBeamLevelEvent() {
   }

   public FallenWizardBeamLevelEvent(Mob var1, float var2, float var3, long var4, int var6, int var7, float var8, GameDamage var9, int var10, int var11, int var12) {
      super(var1, new GameRandom((long)var7));
      this.startAngle = var2;
      this.endAngle = var3;
      this.startTime = var4;
      this.sweepTime = var6;
      this.seed = var7;
      this.distance = var8;
      this.damage = var9;
      this.knockback = var10;
      this.hitCooldowns = new MobHitCooldowns(var11);
      this.bounces = Math.min(var12, 100);
   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      this.seed = var1.getNextShortUnsigned();
      this.startAngle = var1.getNextFloat();
      this.endAngle = var1.getNextFloat();
      this.startTime = var1.getNextLong();
      this.sweepTime = var1.getNextInt();
      this.distance = var1.getNextFloat();
      this.bounces = var1.getNextByteUnsigned();
      this.hitCooldowns = new MobHitCooldowns(var1.getNextInt());
   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      var1.putNextShortUnsigned(this.seed);
      var1.putNextFloat(this.startAngle);
      var1.putNextFloat(this.endAngle);
      var1.putNextLong(this.startTime);
      var1.putNextInt(this.sweepTime);
      var1.putNextFloat(this.distance);
      var1.putNextByteUnsigned(this.bounces);
      var1.putNextInt(this.hitCooldowns.hitCooldown);
   }

   public boolean isNetworkImportant() {
      return true;
   }

   public float getCurrentAngle() {
      long var1 = this.level.getWorldEntity().getTime();
      long var3 = var1 - this.startTime;
      if (var3 >= (long)this.sweepTime) {
         this.over();
         return this.endAngle;
      } else {
         float var5 = Math.max(0.0F, (float)var3 / (float)this.sweepTime);
         float var6 = this.endAngle - this.startAngle;
         return this.startAngle + var6 * var5;
      }
   }

   public void init() {
      super.init();
      if (this.owner instanceof FallenWizardMob) {
         this.fallenWizardMob = (FallenWizardMob)this.owner;
      }

      if (this.owner != null && this.isClient()) {
         Screen.playSound(GameResources.magicbolt4, SoundEffect.effect(this).falloffDistance(1250).volume(1.5F).pitch(0.7F));
      }

   }

   public void tickMovement(float var1) {
      super.tickMovement(var1);
      if (this.owner.removed()) {
         this.over();
      }

      RayLinkedList var2 = this.checkRayHitbox();
      if (var2 != null && this.isClient()) {
         this.updateTrail(var2, this.level.tickManager().getDelta());
      }

   }

   public void clientTick() {
      super.clientTick();
      float var1 = this.getCurrentAngle();
      if (!this.isOver()) {
         if (this.sound == null || this.sound.isDone()) {
            this.sound = Screen.playSound(GameResources.laserBeam1, SoundEffect.effect(this).falloffDistance(750).pitch(1.0F).volume(1.2F));
            if (this.sound != null) {
               this.sound.fadeIn(1.0F);
            }
         }

         if (this.sound != null) {
            long var2 = this.level.getWorldEntity().getTime();
            long var4 = this.startTime + (long)this.sweepTime - var2;
            if (var4 >= 500L) {
               this.sound.refreshLooping(1.0F);
            }
         }

         Point2D.Float var6;
         if (this.fallenWizardMob != null) {
            var6 = GameMath.getAngleDir(var1);
            this.fallenWizardMob.showAttack((int)(this.fallenWizardMob.x + var6.x * 1000.0F), (int)(this.fallenWizardMob.y + var6.y * 1000.0F), false);
         } else {
            var6 = GameMath.getAngleDir(var1);
            this.owner.showAttack((int)(this.owner.x + var6.x * 1000.0F), (int)(this.owner.y + var6.y * 1000.0F), false);
         }

      }
   }

   public void serverTick() {
      super.serverTick();
      float var1 = this.getCurrentAngle();
      if (!this.isOver()) {
         Point2D.Float var2;
         if (this.fallenWizardMob != null) {
            var2 = GameMath.getAngleDir(var1);
            this.fallenWizardMob.showAttack((int)(this.fallenWizardMob.x + var2.x * 1000.0F), (int)(this.fallenWizardMob.y + var2.y * 1000.0F), false);
         } else {
            var2 = GameMath.getAngleDir(var1);
            this.owner.showAttack((int)(this.owner.x + var2.x * 1000.0F), (int)(this.owner.y + var2.y * 1000.0F), false);
         }

      }
   }

   private RayLinkedList<LevelObjectHit> checkRayHitbox() {
      float var1 = this.getCurrentAngle();
      if (this.isOver()) {
         return null;
      } else {
         Point2D.Float var2 = GameMath.getAngleDir(var1);
         this.lastRays = GameUtils.castRay(this.level, (double)(this.owner.x + var2.x * 30.0F), (double)(this.owner.y + var2.y * 30.0F), (double)var2.x, (double)var2.y, (double)this.distance, this.bounces, (new CollisionFilter()).projectileCollision());
         Iterator var3 = this.lastRays.iterator();

         while(var3.hasNext()) {
            Ray var4 = (Ray)var3.next();
            this.handleHits(new LineHitbox(var4, 10.0F), this::canHit, (Function)null);
         }

         return this.lastRays;
      }
   }

   public int getHitCooldown(LevelObjectHit var1) {
      return this.hitCooldowns.hitCooldown;
   }

   public void hit(LevelObjectHit var1) {
      super.hit(var1);
      var1.getLevelObject().attackThrough(this.damage, this.owner);
   }

   public boolean canHit(Mob var1) {
      return var1.canBeHit(this) && this.hitCooldowns.canHit(var1);
   }

   public void clientHit(Mob var1, Packet var2) {
      super.clientHit(var1, var2);
      this.hitCooldowns.startCooldown(var1);
      var1.startHitCooldown();
   }

   public void serverHit(Mob var1, Packet var2, boolean var3) {
      this.hitCooldowns.startCooldown(var1);
      var1.isServerHit(this.damage, var1.x - this.owner.x, var1.y - this.owner.y, (float)this.knockback, this.owner);
   }

   private void updateTrail(RayLinkedList<LevelObjectHit> var1, float var2) {
      if (this.beamHandler == null) {
         this.beamHandler = (new ParticleBeamHandler(this.level)).particleColor(new Color(83, 0, 165)).particleThicknessMod(4.0F).thickness(25, 20).speed(this.distance / 6.0F).height(this.height).sprite(new GameSprite(GameResources.chains, 8, 0, 32));
      }

      this.beamHandler.update(var1, var2);
   }

   public void over() {
      if (this.beamHandler != null) {
         this.beamHandler.dispose();
      }

      if (this.owner != null) {
         this.owner.isAttacking = false;
      }

      super.over();
   }

   public Iterable<Line2D> getSoundLines() {
      return (Iterable)(this.lastRays == null ? Collections.emptyList() : GameUtils.mapIterable(this.lastRays.iterator(), (var0) -> {
         return var0;
      }));
   }
}
