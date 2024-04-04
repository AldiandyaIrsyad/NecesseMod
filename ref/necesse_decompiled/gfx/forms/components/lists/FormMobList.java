package necesse.gfx.forms.components.lists;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Stream;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.control.ControllerEvent;
import necesse.engine.control.ControllerInput;
import necesse.engine.control.InputEvent;
import necesse.engine.localization.Localization;
import necesse.engine.network.packet.PacketSpawnMob;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.presets.debug.DebugForm;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.level.maps.Level;

public abstract class FormMobList extends FormGeneralList<MobElement> {
   private String filter;
   private ArrayList<MobElement> allElements;

   public FormMobList(int var1, int var2, int var3, int var4) {
      super(var1, var2, var3, var4, 40);
      this.setFilter("");
   }

   public void reset() {
      super.reset();
      this.allElements = new ArrayList();
   }

   public void populateIfNotAlready() {
      if (this.allElements.isEmpty()) {
         this.addElements((var1) -> {
            this.allElements.add(new MobElement(var1));
         });
         this.allElements.sort(Comparator.comparing((var0) -> {
            return var0.displayMob == null ? var0.mobConstructor.getDisplayName() : var0.displayMob.getStringID();
         }));
         this.setFilter(this.filter);
         this.resetScroll();
      }

   }

   public void setFilter(String var1) {
      if (var1 != null) {
         this.filter = var1.toLowerCase();
         this.elements = new ArrayList();
         Stream var10000 = this.allElements.stream().filter((var1x) -> {
            return var1x.mobConstructor.getDisplayName().toLowerCase().contains(var1) || var1x.displayMob != null && var1x.displayMob.getStringID().toLowerCase().contains(var1) || var1x.displayMob != null && var1x.displayMob.getDisplayName().toLowerCase().contains(var1);
         });
         List var10001 = this.elements;
         Objects.requireNonNull(var10001);
         var10000.forEach(var10001::add);
         this.limitMaxScroll();
      }
   }

   public abstract void addElements(Consumer<MobConstructor> var1);

   public abstract void onClicked(MobConstructor var1);

   public class MobElement extends FormListElement<FormMobList> {
      public MobConstructor mobConstructor;
      public Mob displayMob;

      public MobElement(MobConstructor var2) {
         this.mobConstructor = var2;
         this.displayMob = var2.construct((Level)null, -1, -1);
      }

      void draw(FormMobList var1, TickManager var2, PlayerMob var3, int var4) {
         Color var5 = this.isHovering() ? Settings.UI.highlightTextColor : Settings.UI.activeTextColor;
         FontOptions var6 = (new FontOptions(16)).color(var5);
         String var7 = GameUtils.maxString(this.mobConstructor.getDisplayName(), var6, var1.width - 20);
         FontManager.bit.drawString(10.0F, 0.0F, var7, var6);
         FontOptions var8 = var6.copy().size(12);
         String var9 = GameUtils.maxString("Armor: " + (this.displayMob == null ? "??" : this.displayMob.getArmor()) + ", HP: " + (this.displayMob == null ? "??" : this.displayMob.getMaxHealth()), var8, var1.width - 20);
         FontManager.bit.drawString(10.0F, 16.0F, var9, var8);
         String var10 = GameUtils.maxString("Hostile: " + (this.displayMob == null ? "??" : (this.displayMob.isHostile ? "Yes" : "No")), var8, var1.width - 20);
         FontManager.bit.drawString(10.0F, 28.0F, var10, var8);
      }

      void onClick(FormMobList var1, int var2, InputEvent var3, PlayerMob var4) {
         if (var3.getID() == -100) {
            FormMobList.this.playTickSound();
            var1.onClicked(this.mobConstructor);
         }
      }

      void onControllerEvent(FormMobList var1, int var2, ControllerEvent var3, TickManager var4, PlayerMob var5) {
         if (var3.getState() == ControllerInput.MENU_SELECT) {
            FormMobList.this.playTickSound();
            var1.onClicked(this.mobConstructor);
            var3.use();
         }
      }

      public void drawControllerFocus(ControllerFocus var1) {
         super.drawControllerFocus(var1);
         Screen.addControllerGlyph(Localization.translate("ui", "selectbutton"), ControllerInput.MENU_SELECT);
      }

      // $FF: synthetic method
      // $FF: bridge method
      void onControllerEvent(FormGeneralList var1, int var2, ControllerEvent var3, TickManager var4, PlayerMob var5) {
         this.onControllerEvent((FormMobList)var1, var2, var3, var4, var5);
      }

      // $FF: synthetic method
      // $FF: bridge method
      void onClick(FormGeneralList var1, int var2, InputEvent var3, PlayerMob var4) {
         this.onClick((FormMobList)var1, var2, var3, var4);
      }

      // $FF: synthetic method
      // $FF: bridge method
      void draw(FormGeneralList var1, TickManager var2, PlayerMob var3, int var4) {
         this.draw((FormMobList)var1, var2, var3, var4);
      }
   }

   public abstract static class MobConstructor {
      public String displayName;

      public MobConstructor(String var1) {
         this.displayName = var1;
      }

      public String getDisplayName() {
         return this.displayName;
      }

      public abstract Mob construct(Level var1, int var2, int var3);

      public void spawn(DebugForm var1, Level var2, int var3, int var4) {
         Mob var5 = this.construct(var2, var3, var4);
         if (var5 != null) {
            var5.resetUniqueID();
            var5.onSpawned(var3, var4);
            var1.client.network.sendPacket(new PacketSpawnMob(var5));
         }
      }
   }
}
