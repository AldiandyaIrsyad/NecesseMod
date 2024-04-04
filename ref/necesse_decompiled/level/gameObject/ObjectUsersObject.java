package necesse.level.gameObject;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ObjectUserMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.interfaces.OEUsers;
import necesse.level.maps.Level;

public interface ObjectUsersObject {
   default Stream<ObjectUserMob> streamObjectUsers(Level var1, int var2, int var3) {
      ObjectEntity var4 = var1.entityManager.getObjectEntity(var2, var3);
      return var4 instanceof OEUsers ? ((OEUsers)var4).streamUsers(var1).filter((var0) -> {
         return var0 instanceof ObjectUserMob;
      }).map((var0) -> {
         return (ObjectUserMob)var0;
      }) : Stream.empty();
   }

   default List<ObjectUserMob> getObjectUsers(Level var1, int var2, int var3) {
      return (List)this.streamObjectUsers(var1, var2, var3).collect(Collectors.toList());
   }

   void tickUser(Level var1, int var2, int var3, Mob var4);

   void stopUsing(Level var1, int var2, int var3, Mob var4);

   boolean drawsUsers();

   boolean preventsUsersPushed();

   Rectangle getUserSelectBox(Level var1, int var2, int var3, Mob var4);

   Point getUserAppearancePos(Level var1, int var2, int var3, Mob var4);

   default boolean isValidUser(Level var1, int var2, int var3, Mob var4) {
      return true;
   }
}
