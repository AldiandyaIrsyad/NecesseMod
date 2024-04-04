package necesse.entity.levelEvent.mobAbilityLevelEvent;

import java.awt.Rectangle;
import java.awt.Shape;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobHitCooldowns;
import necesse.entity.particle.Particle;
import necesse.entity.particle.QueenSpiderSpitParticle;
import necesse.level.maps.LevelObjectHit;

public class QueenSpiderSpitEvent extends GroundEffectEvent {
   public GameDamage damage;
   public float lingerTimeSeconds;
   protected int tickCounter;
   protected MobHitCooldowns hitCooldowns;
   public static Rectangle hitBox = new Rectangle(-25, -20, 50, 40);

   public QueenSpiderSpitEvent() {
   }

   public QueenSpiderSpitEvent(Mob var1, int var2, int var3, GameRandom var4, GameDamage var5, float var6) {
      super(var1, var2, var3, var4);
      this.damage = var5;
      this.lingerTimeSeconds = var6;
   }

   public boolean isNetworkImportant() {
      return true;
   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      this.damage.writePacket(var1);
      var1.putNextFloat(this.lingerTimeSeconds);
      var1.putNextInt(this.tickCounter);
   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      this.damage = GameDamage.fromReader(var1);
      this.lingerTimeSeconds = var1.getNextFloat();
      this.tickCounter = var1.getNextInt();
   }

   public void init() {
      super.init();
      this.hitCooldowns = new MobHitCooldowns();
      if (this.isClient()) {
         long var1 = (long)(this.lingerTimeSeconds * 1000.0F) - (long)(this.tickCounter * 50);
         this.level.entityManager.addParticle(new QueenSpiderSpitParticle(this.level, (float)this.x, (float)this.y, var1), true, Particle.GType.CRITICAL);
      }

   }

   public Shape getHitBox() {
      return new Rectangle(this.x + hitBox.x, this.y + hitBox.y, hitBox.width, hitBox.height);
   }

   public void clientHit(Mob var1) {
      var1.startHitCooldown();
      this.hitCooldowns.startCooldown(var1);
   }

   public void serverHit(Mob var1, boolean var2) {
      if (var2 || this.hitCooldowns.canHit(var1)) {
         var1.isServerHit(this.damage, 0.0F, 0.0F, 0.0F, this.owner);
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
      if ((float)this.tickCounter > 20.0F * this.lingerTimeSeconds) {
         this.over();
      } else {
         super.clientTick();
      }

   }

   public void serverTick() {
      ++this.tickCounter;
      if ((float)this.tickCounter > 20.0F * this.lingerTimeSeconds) {
         this.over();
      } else {
         super.serverTick();
      }

   }
}
