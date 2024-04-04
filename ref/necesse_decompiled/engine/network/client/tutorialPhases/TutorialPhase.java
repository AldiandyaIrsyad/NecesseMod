package necesse.engine.network.client.tutorialPhases;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import necesse.engine.Settings;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientTutorial;
import necesse.engine.state.MainGame;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.fairType.FairType;
import necesse.gfx.fairType.FairTypeDrawOptions;
import necesse.gfx.forms.events.FormEventListener;
import necesse.gfx.forms.events.FormInputEvent;
import necesse.gfx.gameFont.FontOptions;

public class TutorialPhase {
   public static final Color TUTORIAL_TEXT_COLOR = new Color(200, 50, 50);
   public static final int TUTORIAL_TEXT_MAX_WIDTH = 200;
   public static final FontOptions TUTORIAL_TEXT_OPTIONS = (new FontOptions(16)).outline();
   public ClientTutorial tutorial;
   public Client client;
   private boolean isOver;

   public TutorialPhase(ClientTutorial var1, Client var2) {
      this.tutorial = var1;
      this.client = var2;
      this.isOver = false;
   }

   public void start() {
   }

   public void end() {
   }

   public void updateObjective(MainGame var1) {
   }

   public void tick() {
   }

   public void drawOverForm(PlayerMob var1) {
   }

   public void over() {
      this.isOver = true;
   }

   public boolean isOver() {
      return this.isOver;
   }

   public void setObjective(MainGame var1, GameMessage var2) {
      var1.formManager.setTutorialContent(var2, (GameMessage)null);
   }

   public void setObjective(MainGame var1, String var2) {
      this.setObjective(var1, (GameMessage)(new LocalMessage("tutorials", var2)));
   }

   public void setObjective(MainGame var1, GameMessage var2, GameMessage var3, FormEventListener<FormInputEvent> var4) {
      var1.formManager.setTutorialContent(var2, var3, var4);
   }

   public void setObjective(MainGame var1, String var2, String var3, FormEventListener<FormInputEvent> var4) {
      this.setObjective(var1, (GameMessage)(new LocalMessage("tutorials", var2)), (GameMessage)(new LocalMessage("tutorials", var3)), var4);
   }

   public FairTypeDrawOptions getTextDrawOptions(FairType var1) {
      return var1.getDrawOptions(FairType.TextAlign.CENTER, 200, false, true);
   }

   public FairType getTutorialText(String var1) {
      return (new FairType()).append(TUTORIAL_TEXT_OPTIONS, var1);
   }

   public DrawOptions getLevelTextDrawOptions(FairTypeDrawOptions var1, int var2, int var3, GameCamera var4, PlayerMob var5, boolean var6) {
      int var7 = var4.getDrawX(var2);
      int var8 = var4.getDrawY(var3);
      int var9 = -1;
      Rectangle var10 = var4.getBounds();
      var10.x += 25;
      var10.y += 25;
      var10.width -= 50;
      var10.width -= 50;
      if (!var10.contains(var2, var3)) {
         int var11 = Math.min(var4.getWidth(), var4.getHeight());
         int var12 = var4.getWidth() / 2;
         int var13 = var4.getHeight() / 2;
         Point2D.Float var14 = GameMath.normalize((float)(var2 - (var4.getX() + var12)), (float)(var3 - (var4.getY() + var13)));
         var7 = var12 + (int)(var14.x * (float)var11 / 3.0F);
         var8 = var13 + (int)(var14.y * (float)var11 / 3.0F);
         var9 = (int)var5.getDistance((float)var2, (float)var3);
      }

      FairTypeDrawOptions var15 = null;
      if (var9 != -1) {
         var15 = (new FairType()).append(TUTORIAL_TEXT_OPTIONS, "\n(" + (int)GameMath.pixelsToMeters((float)var9) + "m)").getDrawOptions(FairType.TextAlign.CENTER, 200, false, true);
      }

      return this.getTextDrawOptions(var1, var15, var7, var8, var9 == -1 && var6);
   }

   public DrawOptions getTextDrawOptions(FairTypeDrawOptions var1, FairTypeDrawOptions var2, int var3, int var4, boolean var5) {
      DrawOptionsList var6 = new DrawOptionsList();
      var6.add(() -> {
         int var5x = var4 - (var5 ? 10 : 0);
         if (var2 != null) {
            var5x -= var2.getBoundingBox().height;
            var2.draw(var3, var5x, TUTORIAL_TEXT_COLOR);
         }

         if (var1 != null) {
            var5x -= var1.getBoundingBox().height;
            var1.draw(var3, var5x, TUTORIAL_TEXT_COLOR);
         }

      });
      if (var5) {
         var6.add(Settings.UI.tutorial_arrow.initDraw().pos(var3 - Settings.UI.tutorial_arrow.getWidth() / 2, var4 - Settings.UI.tutorial_arrow.getHeight()));
      }

      return var6;
   }
}
