package necesse.engine.commands;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;

public enum PermissionLevel {
   USER(new LocalMessage("misc", "permuser")),
   MODERATOR(new LocalMessage("misc", "permmod")),
   ADMIN(new LocalMessage("misc", "permadmin")),
   OWNER(new LocalMessage("misc", "permowner")),
   SERVER(new LocalMessage("misc", "permserver"), true);

   public final GameMessage name;
   public final boolean reserved;

   private PermissionLevel(GameMessage var3, boolean var4) {
      this.name = var3;
      this.reserved = var4;
   }

   private PermissionLevel(GameMessage var3) {
      this(var3, false);
   }

   public int getLevel() {
      return this.ordinal();
   }

   public static PermissionLevel getLevel(int var0) {
      PermissionLevel[] var1 = values();
      PermissionLevel var2 = var1[0];

      for(int var3 = 1; var3 < var1.length; ++var3) {
         if (!var1[var3].reserved && var0 >= var1[var3].getLevel()) {
            var2 = var1[var3];
         }
      }

      return var2;
   }

   // $FF: synthetic method
   private static PermissionLevel[] $values() {
      return new PermissionLevel[]{USER, MODERATOR, ADMIN, OWNER, SERVER};
   }
}
