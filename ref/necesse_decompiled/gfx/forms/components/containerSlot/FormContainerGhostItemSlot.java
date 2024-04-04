package necesse.gfx.forms.components.containerSlot;

import java.awt.Color;
import java.awt.Rectangle;
import necesse.engine.Screen;
import necesse.engine.network.client.Client;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTexture.GameSprite;
import necesse.inventory.InventoryItem;

public class FormContainerGhostItemSlot extends FormContainerSlot {
   public InventoryItem ghostItem;

   public FormContainerGhostItemSlot(Client var1, int var2, int var3, int var4) {
      super(var1, var2, var3, var4);
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      super.draw(var1, var2, var3);
      InventoryItem var4 = this.getContainerSlot().getItem();
      if (var4 == null) {
         if (this.ghostItem != null) {
            GameSprite var5 = this.ghostItem.item.getItemSprite(this.ghostItem, var2);
            if (var5 != null) {
               Color var6 = this.ghostItem.item.getDrawColor(this.ghostItem, var2);
               var5.initDraw().color(var6).alpha(0.5F).size(32).draw(this.getX() + 4, this.getY() + 4);
            } else {
               this.ghostItem.item.draw(this.ghostItem, var2, this.getX() + 4, this.getY() + 4, false);
            }

            if (this.isHovering() && !Screen.input().isKeyDown(-100) && !Screen.input().isKeyDown(-99)) {
               this.addItemTooltips(this.ghostItem, var2);
            }
         }
      } else {
         this.ghostItem = null;
      }

   }
}
