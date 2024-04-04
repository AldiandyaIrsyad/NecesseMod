package necesse.entity.mobs;

import java.util.function.Consumer;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketOEUseUpdateFullRequest;
import necesse.engine.network.packet.PacketPlayerMovement;
import necesse.engine.network.server.ServerClient;
import necesse.engine.tickManager.TickManager;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.interfaces.OEUsers;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.level.gameObject.ObjectUsersObject;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;

public interface ObjectUserMob {
   void setUsingObject(ObjectUserActive var1);

   ObjectUserActive getUsingObject();

   void clearUsingObject();

   default void clearUsingObject(Level var1, int var2, int var3) {
      ObjectUserActive var4 = this.getUsingObject();
      if (var4 != null && var4.level.isSamePlace(var1) && var4.tileX == var2 && var4.tileY == var3) {
         this.clearUsingObject();
      }

   }

   DrawOptions getUserDrawOptions(Level var1, int var2, int var3, TickManager var4, GameCamera var5, PlayerMob var6, Consumer<HumanDrawOptions> var7);

   default void writeObjectUserPacket(PacketWriter var1) {
      ObjectUserActive var2 = this.getUsingObject();
      if (var2 != null) {
         var1.putNextBoolean(true);
         var1.putNextShortUnsigned(var2.tileX);
         var1.putNextShortUnsigned(var2.tileY);
      } else {
         var1.putNextBoolean(false);
      }

   }

   default void readObjectUserPacket(PacketReader var1) {
      boolean var2 = var1.getNextBoolean();
      ObjectUserActive var3 = this.getUsingObject();
      if (var2) {
         int var4 = var1.getNextShortUnsigned();
         int var5 = var1.getNextShortUnsigned();
         if (var3 == null || var3.tileX != var4 || var3.tileY != var5) {
            Mob var6 = (Mob)this;
            Level var7 = var6.getLevel();
            if (var7 != null) {
               if (var7.isServer() && var6.isPlayer) {
                  ServerClient var13 = ((PlayerMob)var6).getServerClient();
                  var13.sendPacket(new PacketPlayerMovement(var13, true));
               } else {
                  ObjectEntity var8 = var7.entityManager.getObjectEntity(var4, var5);
                  if (var8 instanceof OEUsers) {
                     OEUsers var9 = (OEUsers)var8;
                     if (var9.isMobUsing(var6)) {
                        var9.onUsageChanged(var6, true);
                     } else if (var7.isClient()) {
                        var7.getClient().network.sendPacket(new PacketOEUseUpdateFullRequest(var9));
                     }
                  }
               }
            }
         }
      } else if (var3 != null) {
         Mob var10 = (Mob)this;
         Level var11 = var10.getLevel();
         if (var11 != null && var11.isServer() && var10.isPlayer) {
            ServerClient var12 = ((PlayerMob)var10).getServerClient();
            var12.sendPacket(new PacketPlayerMovement(var12, true));
         } else {
            var3.stopUsing();
            this.clearUsingObject();
         }
      }

   }

   default ObjectUserActive startUsingObject(LevelObject var1, ObjectUsersObject var2, final OEUsers var3) {
      ObjectUserActive var4 = new ObjectUserActive(var1.level, var1.tileX, var1.tileY, var2) {
         public void keepUsing() {
            var3.startUser(this.mob());
         }
      };
      this.setUsingObject(var4);
      ((Mob)this).dismount();
      return var4;
   }
}
