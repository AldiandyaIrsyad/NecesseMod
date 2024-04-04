package necesse.entity.levelEvent.mobAbilityLevelEvent;

import java.awt.geom.Ellipse2D;
import necesse.engine.Screen;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.particle.EvilsProtectorBombParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;

public class EvilsProtectorBombAttackEvent extends MobAbilityLevelEvent implements Attacker {
   protected long spawnTime;
   protected int x;
   protected int y;
   protected GameDamage damage;
   protected boolean playedStartSound;
   private final int warningTime = 1000;

   public EvilsProtectorBombAttackEvent() {
   }

   public EvilsProtectorBombAttackEvent(Mob var1, int var2, int var3, GameRandom var4, GameDamage var5) {
      super(var1, var4);
      this.spawnTime = var1.getWorldEntity().getTime();
      this.x = var2;
      this.y = var3;
      this.damage = var5;
   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      this.spawnTime = var1.getNextLong();
      this.x = var1.getNextInt();
      this.y = var1.getNextInt();
   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      var1.putNextLong(this.spawnTime);
      var1.putNextInt(this.x);
      var1.putNextInt(this.y);
   }

   public void init() {
      super.init();
      if (this.isClient()) {
         this.level.entityManager.addParticle((Particle)(new EvilsProtectorBombParticle(this.level, (float)this.x, (float)this.y, this.spawnTime, 1000L)), Particle.GType.CRITICAL);
      }

   }

   public void clientTick() {
      if (!this.isOver()) {
         long var1 = this.level.getWorldEntity().getTime() - this.spawnTime;
         if (var1 > 1000L && !this.playedStartSound) {
            Screen.playSound(GameResources.magicbolt2, SoundEffect.effect((float)this.x, (float)this.y));
            this.playedStartSound = true;
         }

         if (var1 > 1200L) {
            Screen.playSound(GameResources.firespell1, SoundEffect.effect((float)this.x, (float)this.y).volume(0.5F));
            this.over();
         }

      }
   }

   public void serverTick() {
      if (!this.isOver()) {
         long var1 = this.level.getWorldEntity().getTime() - this.spawnTime;
         if (var1 > 1200L) {
            Ellipse2D.Float var3 = new Ellipse2D.Float((float)(this.x - 38), (float)(this.y - 30), 76.0F, 60.0F);
            GameUtils.streamTargets(this.owner, GameUtils.rangeTileBounds(this.x, this.y, 5)).filter((var2) -> {
               return var2.canBeHit(this.owner) && var3.intersects(var2.getHitBox());
            }).forEach((var1x) -> {
               var1x.isServerHit(this.damage, (float)(var1x.getX() - this.x), (float)(var1x.getY() - this.y), 100.0F, this.owner);
            });
            this.over();
         }

      }
   }

   public GameMessage getAttackerName() {
      return (GameMessage)(this.owner != null ? this.owner.getAttackerName() : new StaticMessage("EP_BOMB_ATTACK"));
   }

   public DeathMessageTable getDeathMessages() {
      return this.owner != null ? this.owner.getDeathMessages() : DeathMessageTable.fromRange("generic", 8);
   }

   public Mob getFirstAttackOwner() {
      return this.owner;
   }
}
