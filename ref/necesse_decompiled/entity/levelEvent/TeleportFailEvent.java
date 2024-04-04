package necesse.entity.levelEvent;

import java.awt.Color;
import java.util.Collection;
import java.util.Collections;
import necesse.engine.Screen;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.level.maps.regionSystem.RegionPosition;

public class TeleportFailEvent extends LevelEvent {
   private int x;
   private int y;

   public TeleportFailEvent() {
   }

   public TeleportFailEvent(int var1, int var2) {
      this.x = var1;
      this.y = var2;
   }

   public TeleportFailEvent(Mob var1) {
      this(var1.getX(), var1.getY());
   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      this.x = var1.getNextInt();
      this.y = var1.getNextInt();
   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      var1.putNextInt(this.x);
      var1.putNextInt(this.y);
   }

   public void init() {
      super.init();
      if (this.isClient()) {
         Screen.playSound(GameResources.teleportfail, SoundEffect.effect((float)this.x, (float)this.y).pitch(0.7F));

         for(int var1 = 0; var1 < 10; ++var1) {
            Particle.GType var2 = var1 <= 3 ? Particle.GType.CRITICAL : Particle.GType.COSMETIC;
            this.level.entityManager.addParticle((float)(this.x + (int)(GameRandom.globalRandom.nextGaussian() * 8.0)), (float)this.y, var2).movesConstant((float)GameRandom.globalRandom.nextGaussian() * 5.0F, (float)GameRandom.globalRandom.nextGaussian() * 5.0F).color(new Color(100, 100, 100)).height((float)GameRandom.globalRandom.nextInt(40)).lifeTime(600);
         }
      }

      this.over();
   }

   public Collection<RegionPosition> getRegionPositions() {
      return Collections.singleton(this.level.regionManager.getRegionPosByTile(this.x / 32, this.y / 32));
   }
}
