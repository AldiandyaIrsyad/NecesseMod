package necesse.entity.levelEvent.mobAbilityLevelEvent;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.function.Function;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.util.Ray;
import necesse.engine.util.RayLinkedList;
import necesse.entity.ParticleBeamHandler;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobHitCooldowns;
import necesse.gfx.GameResources;
import necesse.gfx.gameTexture.GameSprite;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.LevelObjectHit;

public class FireDanceLevelEvent extends MobAbilityLevelEvent {
   protected float targetX;
   protected float targetY;
   protected float currentDistance;
   protected float expandSpeed = 50.0F;
   protected GameDamage damage;
   protected int knockback;
   protected Color color;
   protected int ticker;
   protected int aliveTime;
   protected MobHitCooldowns hitCooldowns = new MobHitCooldowns();
   private ParticleBeamHandler beamHandler;

   public FireDanceLevelEvent() {
   }

   public FireDanceLevelEvent(Mob var1, GameRandom var2, float var3, float var4, GameDamage var5, int var6, Color var7, int var8) {
      super(var1, var2);
      this.targetX = var3;
      this.targetY = var4;
      this.damage = var5;
      this.knockback = var6;
      this.color = var7;
      this.aliveTime = var8;
   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      var1.putNextFloat(this.targetX);
      var1.putNextFloat(this.targetY);
      this.damage.writePacket(var1);
      var1.putNextInt(this.knockback);
      var1.putNextInt(this.color.getRGB());
      var1.putNextFloat(this.currentDistance);
      var1.putNextShortUnsigned(this.ticker);
      var1.putNextInt(this.aliveTime);
   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      this.targetX = var1.getNextFloat();
      this.targetY = var1.getNextFloat();
      this.damage = GameDamage.fromReader(var1);
      this.knockback = var1.getNextInt();
      this.color = new Color(var1.getNextInt());
      this.currentDistance = var1.getNextFloat();
      this.ticker = var1.getNextShortUnsigned();
      this.aliveTime = var1.getNextInt();
   }

   public boolean isNetworkImportant() {
      return true;
   }

   public void tickMovement(float var1) {
      super.tickMovement(var1);
      if (!this.isOver()) {
         this.currentDistance += this.expandSpeed * var1 / 250.0F;
         Point2D.Float var2 = GameMath.normalize(this.targetX - this.owner.x, this.targetY - this.owner.y);
         float var3 = Math.min(this.owner.getDistance(this.targetX, this.targetY), this.currentDistance);
         RayLinkedList var4 = GameUtils.castRay(this.level, (double)this.owner.x, (double)this.owner.y, (double)var2.x, (double)var2.y, (double)var3, 0, (CollisionFilter)null);
         if (this.level.tickManager().isGameTick()) {
            Iterator var5 = var4.iterator();

            while(var5.hasNext()) {
               Ray var6 = (Ray)var5.next();
               this.handleHits(var6, this::canHit, (Function)null);
            }
         }

         if (this.isClient()) {
            this.updateTrail(var4, this.level.tickManager().getDelta(), var3);
         }

      }
   }

   public void clientTick() {
      super.clientTick();
      if (this.owner == null || this.owner.removed()) {
         this.over();
      }

      ++this.ticker;
      if (this.ticker * 50 >= this.aliveTime) {
         this.over();
      }

   }

   public void serverTick() {
      super.serverTick();
      if (this.owner == null || this.owner.removed()) {
         this.over();
      }

      ++this.ticker;
      if (this.ticker * 50 >= this.aliveTime) {
         this.over();
      }

   }

   public boolean canHit(Mob var1) {
      return !var1.canBeHit(this) ? false : this.hitCooldowns.canHit(var1);
   }

   public void clientHit(Mob var1, Packet var2) {
      super.clientHit(var1, var2);
      this.hitCooldowns.startCooldown(var1);
      var1.startHitCooldown();
   }

   public void serverHit(Mob var1, Packet var2, boolean var3) {
      this.hitCooldowns.startCooldown(var1);
      var1.isServerHit(this.damage, this.owner.dx, this.owner.dy, (float)this.knockback, this.owner);
   }

   private void updateTrail(RayLinkedList<LevelObjectHit> var1, float var2, float var3) {
      if (this.beamHandler == null) {
         this.beamHandler = (new ParticleBeamHandler(this.level)).color(this.color).thickness(80, 40).speed(100.0F).sprite(new GameSprite(GameResources.chains, 7, 0, 32));
      }

      this.beamHandler.update(var1, var2);
   }

   public void over() {
      if (this.beamHandler != null) {
         this.beamHandler.dispose();
      }

      super.over();
   }
}
