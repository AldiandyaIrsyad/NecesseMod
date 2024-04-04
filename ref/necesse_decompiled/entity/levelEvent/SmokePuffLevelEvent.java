package necesse.entity.levelEvent;

import java.awt.Color;
import java.util.Collection;
import java.util.Collections;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.entity.particle.Particle;
import necesse.entity.particle.SmokePuffParticle;
import necesse.level.maps.regionSystem.RegionPosition;

public class SmokePuffLevelEvent extends LevelEvent {
   public float x;
   public float y;
   public int res;
   public Color color;

   public SmokePuffLevelEvent() {
   }

   public SmokePuffLevelEvent(float var1, float var2, int var3, Color var4) {
      this.x = var1;
      this.y = var2;
      this.res = var3;
      this.color = var4;
   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      this.x = var1.getNextFloat();
      this.y = var1.getNextFloat();
      this.res = var1.getNextShortUnsigned();
      this.color = new Color(var1.getNextInt());
   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      var1.putNextFloat(this.x);
      var1.putNextFloat(this.y);
      var1.putNextShortUnsigned(this.res);
      var1.putNextInt(this.color.getRGB());
   }

   public void init() {
      super.init();
      if (this.isClient()) {
         this.level.entityManager.addParticle((Particle)(new SmokePuffParticle(this.level, this.x, this.y, this.res, this.color)), Particle.GType.IMPORTANT_COSMETIC);
      }

      this.over();
   }

   public Collection<RegionPosition> getRegionPositions() {
      return Collections.singleton(this.level.regionManager.getRegionPosByTile((int)(this.x / 32.0F), (int)(this.y / 32.0F)));
   }
}
