package necesse.gfx.forms.components.lists;

import java.awt.Color;
import java.awt.Rectangle;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.control.ControllerEvent;
import necesse.engine.control.ControllerInput;
import necesse.engine.control.InputEvent;
import necesse.engine.localization.Localization;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientClient;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameUtils;
import necesse.engine.util.LevelIdentifier;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.inventory.container.travel.TravelDir;

public class FormScoreboardList extends FormGeneralList<ScoreElement> {
   private Client client;

   public FormScoreboardList(int var1, int var2, int var3, int var4, Client var5) {
      super(var1, var2, var3, var4, 30);
      this.client = var5;
      this.reset();
   }

   public void reset() {
      super.reset();
      if (this.client != null) {
         for(int var1 = 0; var1 < this.client.getSlots(); ++var1) {
            ClientClient var2 = this.client.getClient(var1);
            if (var2 != null) {
               this.elements.add(new ScoreElement(var2));
            }
         }
      }

   }

   public void slotChanged(int var1, ClientClient var2) {
      this.elements.removeIf((var1x) -> {
         return var1x.player.slot == var1;
      });
      if (var2 != null) {
         for(int var3 = 0; var3 < this.elements.size(); ++var3) {
            ScoreElement var4 = (ScoreElement)this.elements.get(var3);
            if (var4.player.slot > var1) {
               this.elements.add(var3, new ScoreElement(var2));
               return;
            }
         }

         this.elements.add(new ScoreElement(var2));
      }

   }

   public static void drawLatencyBars(int var0, int var1, int var2, int var3, int var4) {
      Color var5;
      byte var6;
      if (var0 <= 50) {
         var5 = new Color(50, 200, 50);
         var6 = 4;
      } else if (var0 <= 125) {
         var5 = new Color(200, 200, 50);
         var6 = 3;
      } else if (var0 <= 250) {
         var5 = new Color(250, 150, 50);
         var6 = 2;
      } else {
         var5 = new Color(250, 50, 50);
         var6 = 1;
      }

      for(int var7 = 0; var7 < 4; ++var7) {
         if (var7 <= var6 - 1) {
            int var8 = var2 / 10;
            int var9 = (var2 - var8) / 4 * (var7 + 1) + var8;
            Screen.initQuadDraw(var1, var9).color(Color.BLACK).draw(var3 + (var1 + 2) * var7 + 2, var4 + var2 - var9);
            Screen.initQuadDraw(var1 - 2, var9 - 2).color(var5).draw(var3 + (var1 + 2) * var7 + 3, var4 + var2 - var9 + 1);
         }
      }

   }

   public static int getLatencyBarsWidth(int var0) {
      return (var0 + 2) * 4;
   }

   public static boolean isMouseOverLatencyBar(int var0, int var1, InputEvent var2) {
      return var2 == null ? false : (new Rectangle(var0, var1, 32, 16)).contains(var2.pos.hudX, var2.pos.hudY);
   }

   public class ScoreElement extends FormListElement<FormScoreboardList> {
      private ClientClient player;

      public ScoreElement(ClientClient var2) {
         this.player = var2;
      }

      void draw(FormScoreboardList var1, TickManager var2, PlayerMob var3, int var4) {
         float var5 = this.isHovering() ? 0.1F : 0.0F;
         ClientClient var6 = var1.client.getClient();
         boolean var7 = this.player == var6;
         Screen.initQuadDraw(var1.width, var1.elementHeight - 4).color(var5, var5, var5, 0.5F).draw(0, 0);
         FontOptions var8 = new FontOptions(20);
         FontManager.bit.drawString(4.0F, 2.0F, GameUtils.maxString(this.player.getName(), var8, var1.width - 40), var8);
         FormScoreboardList.drawLatencyBars(this.player.latency, 5, 20, var1.width - 32, 4);
         boolean var9 = this.player.loadedPlayer && (this.player == var6 || this.player.isSameTeam(var6));
         if (!var7) {
            GameTexture var10 = var9 ? Settings.UI.island_known : Settings.UI.island_unknown;
            var10.initDraw().alpha(0.7F).draw(var1.width - 64, 1);
         }

         if (this.isHovering()) {
            if (FormScoreboardList.isMouseOverLatencyBar(var1.width - 32, 6, this.getMoveEvent())) {
               Screen.addTooltip(new StringTooltips(Localization.translate("ui", "latencytip", "latency", (Object)this.player.latency)), TooltipLocation.FORM_FOCUS);
            } else if (this.isMouseOverIsland(var1.width - 64, 1, this.getMoveEvent()) && !var7) {
               if (var9) {
                  Screen.addTooltip(new StringTooltips(this.getDirection(this.player, var6)), TooltipLocation.FORM_FOCUS);
               } else {
                  Screen.addTooltip(new StringTooltips(Localization.translate("ui", "scoredirunknown")), TooltipLocation.FORM_FOCUS);
               }
            }
         }

      }

      void onClick(FormScoreboardList var1, int var2, InputEvent var3, PlayerMob var4) {
         if (var3.getID() == -100) {
            var3.use();
            FormScoreboardList.this.playTickSound();
         }
      }

      void onControllerEvent(FormScoreboardList var1, int var2, ControllerEvent var3, TickManager var4, PlayerMob var5) {
         if (var3.getState() == ControllerInput.MENU_SELECT) {
            var3.use();
            FormScoreboardList.this.playTickSound();
         }
      }

      private boolean isMouseOverIsland(int var1, int var2, InputEvent var3) {
         return var3 == null ? false : (new Rectangle(var1, var2, 26, 26)).contains(var3.pos.hudX, var3.pos.hudY);
      }

      private String getDirection(ClientClient var1, ClientClient var2) {
         LevelIdentifier var3 = var1.getLevelIdentifier();
         LevelIdentifier var4 = var2.getLevelIdentifier();
         if (var3.isIslandPosition() && var4.isIslandPosition()) {
            TravelDir var5 = TravelDir.getDeltaDir(var4.getIslandX(), var4.getIslandY(), var3.getIslandX(), var3.getIslandY(), (TravelDir)null);
            String var6 = var5 == null ? Localization.translate("ui", "scoreyour") : var5.dirMessage.translate();
            return GameUtils.capitalize(var6 + " (" + (var3.getIslandX() - var4.getIslandX()) + ", " + (var3.getIslandY() - var4.getIslandY()) + ")");
         } else {
            return "N/A";
         }
      }

      // $FF: synthetic method
      // $FF: bridge method
      void onControllerEvent(FormGeneralList var1, int var2, ControllerEvent var3, TickManager var4, PlayerMob var5) {
         this.onControllerEvent((FormScoreboardList)var1, var2, var3, var4, var5);
      }

      // $FF: synthetic method
      // $FF: bridge method
      void onClick(FormGeneralList var1, int var2, InputEvent var3, PlayerMob var4) {
         this.onClick((FormScoreboardList)var1, var2, var3, var4);
      }

      // $FF: synthetic method
      // $FF: bridge method
      void draw(FormGeneralList var1, TickManager var2, PlayerMob var3, int var4) {
         this.draw((FormScoreboardList)var1, var2, var3, var4);
      }
   }
}
