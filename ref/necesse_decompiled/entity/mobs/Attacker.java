package necesse.entity.mobs;

import java.util.HashSet;
import java.util.LinkedList;
import necesse.engine.localization.message.GameMessage;

public interface Attacker {
   int DEFAULT_RESPAWN_TIME = 5000;

   GameMessage getAttackerName();

   DeathMessageTable getDeathMessages();

   Mob getFirstAttackOwner();

   default Mob getAttackOwner() {
      LinkedList var1 = this.getAttackOwnerChain();
      return var1.isEmpty() ? this.getFirstAttackOwner() : (Mob)var1.getLast();
   }

   default LinkedList<Mob> getAttackOwnerChain() {
      LinkedList var1 = new LinkedList();
      HashSet var2 = new HashSet();

      Mob var4;
      for(Mob var3 = this.getFirstAttackOwner(); var3 != null; var3 = var4) {
         var1.addLast(var3);
         var2.add(var3);
         var4 = var3.getFirstAttackOwner();
         if (var4 == null) {
            return var1;
         }

         if (var3 == var4 || var2.contains(var4)) {
            return var1;
         }
      }

      return var1;
   }

   default HashSet<Mob> getAttackOwners() {
      HashSet var1 = new HashSet();

      Mob var3;
      for(Mob var2 = this.getFirstAttackOwner(); var2 != null; var2 = var3) {
         var1.add(var2);
         var3 = var2.getFirstAttackOwner();
         if (var3 == null) {
            return var1;
         }

         if (var2 == var3 || var1.contains(var3)) {
            return var1;
         }
      }

      return var1;
   }

   default boolean isInAttackOwnerChain(Mob var1) {
      return this.getAttackOwners().contains(var1);
   }

   default boolean removed() {
      Mob var1 = this.getAttackOwner();
      return var1 != null ? var1.removed() : false;
   }

   default int getAttackerUniqueID() {
      Mob var1 = this.getAttackOwner();
      return var1 != null ? var1.getUniqueID() : -1;
   }

   default int getRespawnTime() {
      return 5000;
   }

   default DeathMessageTable getDeathMessages(String var1, int var2) {
      return DeathMessageTable.fromRange(var1, var2);
   }
}
