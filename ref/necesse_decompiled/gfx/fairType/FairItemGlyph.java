package necesse.gfx.fairType;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.Collections;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.control.InputEvent;
import necesse.engine.util.FloatDimension;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameBackground;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.inventory.InventoryItem;

public class FairItemGlyph implements FairGlyph {
   public final int size;
   public final List<InventoryItem> items;
   private boolean isHovering;

   public FairItemGlyph(int var1, List<InventoryItem> var2) {
      this.size = var1;
      this.items = var2;
   }

   public FairItemGlyph(int var1, InventoryItem var2) {
      this(var1, Collections.singletonList(var2));
   }

   public FloatDimension getDimensions() {
      return new FloatDimension((float)(this.size + 8), (float)(this.size + 4));
   }

   public void updateDimensions() {
   }

   public void handleInputEvent(float var1, float var2, InputEvent var3) {
      if (var3.isMouseMoveEvent()) {
         Dimension var4 = this.getDimensions().toInt();
         this.isHovering = (new Rectangle((int)var1 + 1, (int)var2 - var4.height, var4.width, var4.height)).contains(var3.pos.hudX, var3.pos.hudY);
      }

   }

   public void draw(float var1, float var2, Color var3) {
      InventoryItem var4 = this.getCurrentDrawnItem();
      var4.drawIcon((PlayerMob)null, (int)var1 + 1, (int)var2 - this.size - 6, this.size + 8);
      if (this.isHovering) {
         Screen.addTooltip(var4.getTooltip((PlayerMob)null, new GameBlackboard()), GameBackground.getItemTooltipBackground(), TooltipLocation.FORM_FOCUS);
      }

   }

   public long getTime() {
      return System.currentTimeMillis();
   }

   public int getCycleTime() {
      return 1000;
   }

   public InventoryItem getCurrentDrawnItem() {
      long var1 = this.getTime();
      int var3 = this.getCycleTime();
      int var4 = (int)(var1 / (long)var3);
      int var5 = Math.floorMod(var4, this.items.size());
      return (InventoryItem)this.items.get(var5);
   }

   public FairGlyph getTextBoxCharacter() {
      return this;
   }

   public String getParseString() {
      return TypeParsers.getItemParseString(this.getCurrentDrawnItem());
   }
}
