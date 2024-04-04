package necesse.entity.projectile.modifiers;

import java.awt.geom.Point2D;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.IDData;
import necesse.engine.registries.ProjectileModifierRegistry;
import necesse.entity.mobs.Mob;
import necesse.entity.projectile.Projectile;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;

public class ProjectileModifier {
   public final IDData idData = new IDData();
   public Projectile projectile;

   public final String getStringID() {
      return this.idData.getStringID();
   }

   public final int getID() {
      return this.idData.getID();
   }

   public ProjectileModifier() {
      ProjectileModifierRegistry.instance.applyIDData(this.getClass(), this.idData);
   }

   public void setupSpawnPacket(PacketWriter var1) {
   }

   public void applySpawnPacket(PacketReader var1) {
   }

   public void setupPositionPacket(PacketWriter var1) {
   }

   public void applyPositionPacket(PacketReader var1) {
   }

   public void init() {
   }

   public void initChildProjectile(Projectile var1, float var2, int var3) {
   }

   public void postInit() {
   }

   public void onMoveTick(Point2D.Float var1, double var2) {
   }

   public boolean onHit(Mob var1, LevelObjectHit var2, float var3, float var4, boolean var5, ServerClient var6) {
      return false;
   }

   public void doHitLogic(Mob var1, LevelObjectHit var2, float var3, float var4) {
   }

   public Level getLevel() {
      return this.projectile.getLevel();
   }
}
