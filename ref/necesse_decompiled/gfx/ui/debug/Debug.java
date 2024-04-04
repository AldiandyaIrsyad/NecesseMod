package necesse.gfx.ui.debug;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import necesse.engine.GlobalData;
import necesse.engine.control.InputEvent;
import necesse.engine.network.client.Client;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.level.maps.Level;

public abstract class Debug {
   private static int active = 0;
   private static ArrayList<Debug> states;
   private int currentY = 30;
   protected static final FontOptions smallFontOptions;
   protected static final FontOptions bigFontOptions;

   public Debug() {
   }

   private static void setup() {
      states = new ArrayList();
      states.add(new Debug() {
         protected void drawDebug(Client var1) {
         }
      });
      states.add(new DebugInfo());
      states.add(new DebugNetwork());
      states.add(new DebugPerformanceHistory());
      if (GlobalData.isDevMode()) {
         states.add(new DebugRegionPathCache());
      }

   }

   public static void submitChange() {
      if (states == null) {
         setup();
      }

      active = (active + 1) % states.size();
   }

   public static void reset() {
      if (states == null) {
         setup();
      }

      active = 0;
      Iterator var0 = states.iterator();

      while(var0.hasNext()) {
         Debug var1 = (Debug)var0.next();
         var1.onReset();
      }

   }

   public static boolean isActive() {
      return active != 0;
   }

   private static Debug getActiveDebug() {
      if (states == null) {
         setup();
      }

      return (Debug)states.get(active);
   }

   public static void draw(Client var0) {
      getActiveDebug().drawDebugPrivate(var0);
   }

   public static void drawHUD(Level var0, GameCamera var1, PlayerMob var2) {
      getActiveDebug().drawHUDPrivate(var0, var1, var2);
   }

   public static void submitInputEvent(InputEvent var0, Client var1) {
      getActiveDebug().submitDebugInputEvent(var0, var1);
   }

   private void drawDebugPrivate(Client var1) {
      this.initDraw();
      this.drawDebug(var1);
   }

   private void drawHUDPrivate(Level var1, GameCamera var2, PlayerMob var3) {
      this.initDraw();
      this.drawDebugHUD(var1, var2, var3);
   }

   protected abstract void drawDebug(Client var1);

   protected void drawDebugHUD(Level var1, GameCamera var2, PlayerMob var3) {
   }

   protected void submitDebugInputEvent(InputEvent var1, Client var2) {
   }

   protected void onReset() {
   }

   private void initDraw() {
      this.currentY = 30;
   }

   protected final int skipY(int var1) {
      int var2 = this.currentY;
      this.currentY += var1;
      return var2;
   }

   protected final void drawString(String var1) {
      FontManager.bit.drawString(10.0F, (float)this.currentY, var1, smallFontOptions);
      this.skipY(15);
   }

   protected final void drawStringBig(String var1) {
      FontManager.bit.drawString(10.0F, (float)this.currentY, var1, bigFontOptions);
      this.skipY(20);
   }

   static {
      smallFontOptions = (new FontOptions(12)).color(Color.WHITE);
      bigFontOptions = (new FontOptions(16)).color(Color.WHITE);
   }
}
