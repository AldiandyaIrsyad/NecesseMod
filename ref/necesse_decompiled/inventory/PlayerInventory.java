package necesse.inventory;

import java.util.ArrayList;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.PlayerMob;

public class PlayerInventory extends Inventory {
   public final PlayerMob player;
   public boolean sizeCanChange;
   private boolean canBeUsedForCrafting;
   private boolean canLock;
   private final int invID;

   public PlayerInventory(PlayerMob var1, int var2, boolean var3, boolean var4, boolean var5, int var6) {
      super(var2);
      this.player = var1;
      this.sizeCanChange = var3;
      this.canBeUsedForCrafting = var4;
      this.canLock = var5;
      this.invID = var6;
   }

   public PlayerInventory(ArrayList<PlayerInventory> var1, PlayerMob var2, int var3, boolean var4, boolean var5, boolean var6) {
      this(var2, var3, var4, var5, var6, var1.size());
      var1.add(this);
   }

   public void override(Inventory var1) {
      this.override(var1, this.sizeCanChange, false);
   }

   public boolean canLockItem(int var1) {
      return this.canLock;
   }

   public boolean canBeUsedForCrafting() {
      return this.canBeUsedForCrafting;
   }

   public void setItem(int var1, InventoryItem var2, boolean var3) {
      super.setItem(var1, var2, var3);
      if (var2 != null && this.player.getLevel() != null && this.player.isServer()) {
         ServerClient var4 = this.player.getServerClient();
         var4.markObtainItem(var2.item.getStringID());
      }

   }

   public int getInventoryID() {
      return this.invID;
   }
}
