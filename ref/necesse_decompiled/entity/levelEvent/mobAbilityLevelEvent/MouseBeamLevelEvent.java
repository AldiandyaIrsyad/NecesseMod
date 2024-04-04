package necesse.entity.levelEvent.mobAbilityLevelEvent;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.function.Function;
import necesse.engine.GlobalData;
import necesse.engine.control.ControllerInput;
import necesse.engine.control.Input;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketMouseBeamEventUpdate;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.util.Ray;
import necesse.engine.util.RayLinkedList;
import necesse.entity.ParticleBeamHandler;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.gameTexture.GameSprite;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.LevelObjectHit;

public class MouseBeamLevelEvent extends MobAbilityLevelEvent {
   public int seed;
   public float currentAngle;
   public int targetAngle;
   protected float speed;
   public float height = 14.0F;
   protected int bounces;
   protected float resilienceGain;
   protected float distance;
   protected GameDamage damage;
   protected int knockback;
   protected Color color;
   public PlayerMob player;
   public int hitCooldown;
   public float appendAttackSpeedModifier;
   public long lastResilienceGainTime;
   private ParticleBeamHandler beamHandler;

   public MouseBeamLevelEvent() {
   }

   public MouseBeamLevelEvent(Mob var1, int var2, int var3, int var4, float var5, float var6, GameDamage var7, int var8, int var9, float var10, int var11, float var12, Color var13) {
      super(var1, new GameRandom((long)var4));
      this.seed = var4;
      this.speed = var5;
      this.distance = var6;
      this.damage = var7;
      this.knockback = var8;
      this.hitCooldown = var9;
      this.appendAttackSpeedModifier = var10;
      this.bounces = Math.min(var11, 100);
      this.resilienceGain = var12;
      this.color = var13;
      this.targetAngle = (int)GameMath.fixAngle(GameMath.getAngle(new Point2D.Float((float)var2 - var1.x, (float)var3 - var1.y)));
      this.currentAngle = (float)this.targetAngle;
   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      this.seed = var1.getNextShortUnsigned();
      this.targetAngle = var1.getNextShortUnsigned();
      this.currentAngle = var1.getNextFloat();
      this.speed = var1.getNextFloat();
      this.distance = var1.getNextFloat();
      this.bounces = var1.getNextByteUnsigned();
      this.color = new Color(var1.getNextInt());
      this.knockback = var1.getNextInt();
      this.hitCooldown = var1.getNextInt();
      this.appendAttackSpeedModifier = var1.getNextFloat();
      this.damage = GameDamage.fromReader(var1);
   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      var1.putNextShortUnsigned(this.seed);
      var1.putNextShortUnsigned(this.targetAngle);
      var1.putNextFloat(this.currentAngle);
      var1.putNextFloat(this.speed);
      var1.putNextFloat(this.distance);
      var1.putNextByteUnsigned(this.bounces);
      var1.putNextInt(this.color.getRGB());
      var1.putNextInt(this.knockback);
      var1.putNextInt(this.hitCooldown);
      var1.putNextFloat(this.appendAttackSpeedModifier);
      this.damage.writePacket(var1);
   }

   public boolean isNetworkImportant() {
      return true;
   }

   public void init() {
      super.init();
      if (this.owner instanceof PlayerMob) {
         this.player = (PlayerMob)this.owner;
      } else {
         this.over();
      }

   }

   public void tickMovement(float var1) {
      super.tickMovement(var1);
      if (!this.player.isAttacking || this.player.attackSeed != this.seed) {
         this.over();
      }

      if (!this.isOver()) {
         RayLinkedList var2 = null;
         if (this.currentAngle != (float)this.targetAngle) {
            float var3 = 20.0F;
            float var4 = (float)((double)var3 / (6.283185307179586 * (double)this.distance / 360.0));
            float var5 = this.speed / this.getAttackSpeedModifier();

            for(float var6 = var5 * var1 / 250.0F; var6 > 0.0F; var2 = this.checkRayHitbox()) {
               float var7 = Math.min(var4, var6);
               var6 -= var7;
               float var8 = GameMath.getAngleDifference(this.currentAngle, (float)this.targetAngle);
               if (Math.abs(var8) < var7) {
                  this.currentAngle = (float)this.targetAngle;
               } else if (var8 < 0.0F) {
                  this.currentAngle = GameMath.fixAngle(this.currentAngle + var7);
               } else if (var8 > 0.0F) {
                  this.currentAngle = GameMath.fixAngle(this.currentAngle - var7);
               }
            }
         }

         if (var2 == null) {
            var2 = this.checkRayHitbox();
         }

         if (this.isClient()) {
            this.updateTrail(var2, this.level.tickManager().getDelta());
         }

      }
   }

   public void clientTick() {
      super.clientTick();
      if (!this.isOver()) {
         if (this.player == this.level.getClient().getPlayer()) {
            GameCamera var1 = GlobalData.getCurrentState().getCamera();
            int var2;
            if (Input.lastInputIsController && !ControllerInput.isCursorVisible()) {
               Point2D.Float var3 = this.player.getControllerAimDir();
               var2 = (int)GameMath.fixAngle(GameMath.getAngle(var3));
            } else {
               var2 = (int)GameMath.fixAngle(GameMath.getAngle(new Point2D.Float((float)var1.getMouseLevelPosX() - this.player.x, (float)var1.getMouseLevelPosY() - this.player.y)));
            }

            if (this.targetAngle != var2) {
               this.targetAngle = var2;
               this.level.getClient().network.sendPacket(new PacketMouseBeamEventUpdate(this, this.targetAngle, this.currentAngle));
            }
         }

      }
   }

   private RayLinkedList<LevelObjectHit> checkRayHitbox() {
      Point2D.Float var1 = GameMath.getAngleDir(this.currentAngle);
      RayLinkedList var2 = GameUtils.castRay(this.level, (double)this.player.x, (double)this.player.y, (double)var1.x, (double)var1.y, (double)this.distance, this.bounces, (new CollisionFilter()).projectileCollision().addFilter((var0) -> {
         return !var0.object().object.attackThrough;
      }));
      Iterator var3 = var2.iterator();

      while(var3.hasNext()) {
         Ray var4 = (Ray)var3.next();
         this.handleHits(var4, this::canHit, (Function)null);
      }

      return var2;
   }

   public int getHitCooldown(LevelObjectHit var1) {
      return this.getHitCooldown();
   }

   public void hit(LevelObjectHit var1) {
      super.hit(var1);
      var1.getLevelObject().attackThrough(this.damage, this.owner);
   }

   public boolean canHit(Mob var1) {
      if (!var1.canBeHit(this)) {
         return false;
      } else if (!this.player.toolHits.containsKey(var1.getHitCooldownUniqueID())) {
         return true;
      } else {
         return (Long)this.player.toolHits.get(var1.getHitCooldownUniqueID()) + (long)this.getHitCooldown() < this.player.getWorldEntity().getTime();
      }
   }

   public float getAttackSpeedModifier() {
      return 1.0F / this.damage.type.calculateTotalAttackSpeedModifier(this.owner, this.appendAttackSpeedModifier);
   }

   public int getHitCooldown() {
      return Math.round((float)this.hitCooldown * this.getAttackSpeedModifier());
   }

   public void clientHit(Mob var1, Packet var2) {
      super.clientHit(var1, var2);
      this.player.toolHits.put(var1.getHitCooldownUniqueID(), this.player.getWorldEntity().getTime());
      var1.startHitCooldown();
   }

   public void serverHit(Mob var1, Packet var2, boolean var3) {
      this.player.toolHits.put(var1.getHitCooldownUniqueID(), this.player.getWorldEntity().getTime());
      var1.isServerHit(this.damage, var1.x - this.player.x, var1.y - this.player.y, (float)this.knockback, this.player);
      if (var1.canGiveResilience(this.owner) && this.resilienceGain != 0.0F && var1.getTime() >= this.lastResilienceGainTime + (long)this.hitCooldown) {
         this.owner.addResilience(this.resilienceGain);
         this.lastResilienceGainTime = var1.getTime();
      }

   }

   private void updateTrail(RayLinkedList<LevelObjectHit> var1, float var2) {
      if (this.beamHandler == null) {
         this.beamHandler = (new ParticleBeamHandler(this.level)).color(this.color).thickness(40, 5).speed(this.distance / 6.0F).height(this.height).sprite(new GameSprite(GameResources.chains, 7, 0, 32));
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
