package necesse.entity.objectEntity;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.BuffRegistry;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ObjectUserMob;
import necesse.entity.objectEntity.interfaces.OEUsers;
import necesse.level.gameObject.ObjectUsersObject;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;

public class BedObjectEntity extends ObjectEntity implements OEUsers {
   public final OEUsers.Users users = this.constructUsersObject(2000L);

   public BedObjectEntity(Level var1, int var2, int var3) {
      super(var1, "bed", var2, var3);
   }

   public void setupContentPacket(PacketWriter var1) {
      super.setupContentPacket(var1);
      this.users.writeUsersSpawnPacket(var1);
   }

   public void applyContentPacket(PacketReader var1) {
      super.applyContentPacket(var1);
      this.users.readUsersSpawnPacket(var1, this);
   }

   public void serverTick() {
      super.serverTick();
      this.users.serverTick(this);
   }

   public void clientTick() {
      super.clientTick();
      this.users.clientTick(this);
   }

   public boolean canUse(Mob var1) {
      return !this.isInUse();
   }

   public OEUsers.Users getUsersObject() {
      return this.users;
   }

   public void updateUserPosition(Mob var1) {
      LevelObject var2 = this.getLevelObject();
      ArrayList var3 = new ArrayList();
      Iterator var4 = var2.getMultiTile().getAdjacentTiles(this.getX(), this.getY(), true).iterator();

      while(var4.hasNext()) {
         Point var5 = (Point)var4.next();
         int var6 = var5.x * 32 + 16;
         int var7 = var5.y * 32 + 16;
         if (!var1.collidesWith(this.getLevel(), var6, var7)) {
            var3.add(var5);
         }
      }

      Comparator var8 = Comparator.comparingDouble((var1x) -> {
         return var1x.distance((double)this.getX(), (double)this.getY());
      });
      var8 = var8.thenComparing((var2x, var3x) -> {
         return var2.rotation != 0 && var2.rotation != 2 ? Math.abs(var2x.x - this.getX()) - Math.abs(var3x.x - this.getX()) : Math.abs(var2x.y - this.getY()) - Math.abs(var3x.y - this.getY());
      });
      var3.stream().min(var8).ifPresent((var1x) -> {
         var1.setPos((float)(var1x.x * 32 + 16), (float)(var1x.y * 32 + 16), true);
         var1.sendMovementPacket(true);
      });
   }

   public void onUsageChanged(Mob var1, boolean var2) {
      if (var1 instanceof ObjectUserMob) {
         LevelObject var3 = this.getLevelObject();
         if (var2) {
            if (!this.removed()) {
               ((ObjectUserMob)var1).startUsingObject(var3, (ObjectUsersObject)var3.object, this);
            }
         } else {
            ((ObjectUserMob)var1).clearUsingObject(this.getLevel(), this.getX(), this.getY());
            var1.buffManager.removeBuff(BuffRegistry.SLEEPING, var1.isServer());
            this.updateUserPosition(var1);
         }
      }

   }

   public void onIsInUseChanged(boolean var1) {
   }

   public void remove() {
      super.remove();
      this.users.onRemoved(this);
   }
}
