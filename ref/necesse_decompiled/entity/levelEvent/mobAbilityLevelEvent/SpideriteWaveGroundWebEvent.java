package necesse.entity.levelEvent.mobAbilityLevelEvent;

import java.awt.Rectangle;
import java.awt.Shape;
import necesse.engine.Screen;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobHitCooldowns;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.particle.Particle;
import necesse.entity.particle.SpideriteWaveWebParticle;
import necesse.gfx.GameResources;
import necesse.level.maps.LevelObjectHit;

public class SpideriteWaveGroundWebEvent extends GroundEffectEvent {
   private GameDamage damage = new GameDamage(0.0F);
   protected MobHitCooldowns hitCooldowns = new MobHitCooldowns(1000);
   protected int tickCounter;
   protected int hitCounter;
   private final long lifetime = 10000L;
   private SpideriteWaveWebParticle particle;

   public SpideriteWaveGroundWebEvent() {
   }

   public SpideriteWaveGroundWebEvent(Mob var1, int var2, int var3, GameRandom var4, GameDamage var5) {
      super(var1, var2, var3, var4);
      this.damage = var5;
   }

   public void addSaveData(SaveData var1) {
      super.addSaveData(var1);
      SaveData var2 = new SaveData("damage");
      this.damage.addSaveData(var2);
      var1.addSaveData(var2);
   }

   public void applyLoadData(LoadData var1) {
      super.applyLoadData(var1);
      LoadData var2 = var1.getFirstLoadDataByName("damage");
      if (var2 != null) {
         try {
            this.damage = GameDamage.fromLoadData(var1);
         } catch (Exception var4) {
            this.damage = new GameDamage(0.0F);
            System.err.println("Could not load damage from " + this.getStringID() + " event");
            var4.printStackTrace();
         }
      }

   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      this.damage.writePacket(var1);
   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      GameDamage.fromReader(var1);
   }

   public void init() {
      super.init();
      this.tickCounter = 0;
      if (this.isClient()) {
         this.level.entityManager.addParticle(this.particle = new SpideriteWaveWebParticle(this.level, (float)this.x, (float)this.y, 10000L), true, Particle.GType.CRITICAL);
         Screen.playSound(GameResources.fizz, SoundEffect.effect((float)this.x, (float)this.y).volume(0.5F).pitch(GameRandom.globalRandom.getFloatBetween(0.5F, 1.0F)));
      }

   }

   public Shape getHitBox() {
      byte var1 = 24;
      byte var2 = 24;
      return new Rectangle(this.x - var1 / 2, this.y - var2 / 2, var1, var2);
   }

   public void clientHit(Mob var1) {
      var1.startHitCooldown();
      this.hitCooldowns.startCooldown(var1);
      ++this.hitCounter;
      if (this.hitCounter >= 10) {
         this.over();
      }

   }

   public void serverHit(Mob var1, boolean var2) {
      if (var2 || this.hitCooldowns.canHit(var1)) {
         var1.addBuff(new ActiveBuff(BuffRegistry.Debuffs.SPIDER_WEB_SLOW, var1, 1.0F, this.owner), true);
         var1.isServerHit(this.damage, 0.0F, 0.0F, 0.0F, this.owner);
         ++this.hitCounter;
         if (this.hitCounter >= 10) {
            this.over();
         }

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
      if ((long)this.tickCounter > 200L) {
         this.over();
      } else {
         super.clientTick();
      }

   }

   public void serverTick() {
      ++this.tickCounter;
      if ((long)this.tickCounter > 200L) {
         this.over();
      } else {
         super.serverTick();
      }

   }

   public void over() {
      super.over();
      if (this.particle != null) {
         this.particle.despawnNow();
      }

   }
}
