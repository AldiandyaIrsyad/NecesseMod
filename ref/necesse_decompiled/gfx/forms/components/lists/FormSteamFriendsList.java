package necesse.gfx.forms.components.lists;

import com.codedisaster.steamworks.SteamException;
import com.codedisaster.steamworks.SteamFriends;
import com.codedisaster.steamworks.SteamFriendsCallback;
import com.codedisaster.steamworks.SteamID;
import com.codedisaster.steamworks.SteamNativeHandle;
import com.codedisaster.steamworks.SteamFriends.FriendFlags;
import com.codedisaster.steamworks.SteamFriends.PersonaState;
import java.awt.Color;
import java.nio.ByteBuffer;
import java.util.HashMap;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.control.ControllerEvent;
import necesse.engine.control.ControllerInput;
import necesse.engine.control.InputEvent;
import necesse.engine.localization.Localization;
import necesse.engine.steam.SteamData;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;

public abstract class FormSteamFriendsList extends FormGeneralList<FriendElement> {
   protected SteamFriends steamFriends = new SteamFriends(new SteamFriendsCallback() {
      public void onPersonaStateChange(SteamID var1, SteamFriends.PersonaChange var2) {
         FriendElement var3 = FormSteamFriendsList.this.getFriend(var1);
         if (var3 != null) {
            switch (var2) {
               case Name:
                  var3.onNameChanged();
                  break;
               case Status:
                  var3.onStatusChanged();
                  break;
               case GamePlayed:
                  var3.onGameChanged();
            }
         }

      }

      public void onGameOverlayActivated(boolean var1, boolean var2, int var3) {
         System.out.println("Activated overlay: " + var1);
      }
   });
   protected HashMap<Integer, GameTexture> avatars = new HashMap();

   public FormSteamFriendsList(int var1, int var2, int var3, int var4) {
      super(var1, var2, var3, var4, 36);
      this.reset();
   }

   public void reset() {
      super.reset();
      if (this.steamFriends != null) {
         for(int var1 = 0; var1 < this.steamFriends.getFriendCount(FriendFlags.Immediate); ++var1) {
            SteamID var2 = this.steamFriends.getFriendByIndex(var1, FriendFlags.Immediate);
            FriendElement var3 = new FriendElement(var2);
            if (this.shouldShow(var3)) {
               this.elements.add(var3);
            }
         }

         this.sort();
      }

   }

   public boolean shouldShow(FriendElement var1) {
      return true;
   }

   protected void sort() {
      this.elements.sort((var0, var1) -> {
         if (var0.inGame != var1.inGame) {
            return var0.inGame ? -1 : 1;
         } else {
            boolean var2 = var0.status == PersonaState.Offline;
            boolean var3 = var1.status == PersonaState.Offline;
            if (var2 != var3) {
               return var2 ? 1 : -1;
            } else {
               boolean var4 = var0.status == PersonaState.Online;
               boolean var5 = var1.status == PersonaState.Online;
               if (var4 != var5) {
                  return var4 ? -1 : 1;
               } else {
                  return var0.name.compareTo(var1.name);
               }
            }
         }
      });
   }

   protected FriendElement getFriend(SteamID var1) {
      return (FriendElement)this.elements.stream().filter((var1x) -> {
         return SteamNativeHandle.getNativeHandle(var1x.steamID) == SteamNativeHandle.getNativeHandle(var1);
      }).findFirst().orElse((Object)null);
   }

   protected abstract void onFriendClicked(FriendElement var1);

   public void dispose() {
      super.dispose();
      this.steamFriends.dispose();
      this.avatars.values().forEach(GameTexture::delete);
   }

   protected class FriendElement extends FormListElement<FormSteamFriendsList> {
      public final SteamID steamID;
      protected String name;
      protected SteamFriends.PersonaState status;
      protected boolean inGame;
      public final SteamFriends.FriendGameInfo gameInfo;
      private int avatar = -1;

      public FriendElement(SteamID var2) {
         this.steamID = var2;
         this.gameInfo = new SteamFriends.FriendGameInfo();
         this.onNameChanged();
         this.onStatusChanged();
         this.onGameChanged();
      }

      public void onNameChanged() {
         this.name = FormSteamFriendsList.this.steamFriends.getFriendPersonaName(this.steamID);
      }

      public void onStatusChanged() {
         this.status = FormSteamFriendsList.this.steamFriends.getFriendPersonaState(this.steamID);
         FormSteamFriendsList.this.sort();
      }

      public void onGameChanged() {
         this.inGame = FormSteamFriendsList.this.steamFriends.getFriendGamePlayed(this.steamID, this.gameInfo);
         FormSteamFriendsList.this.sort();
      }

      public void generateAvatarTexture() {
         if (FormSteamFriendsList.this.avatars.containsKey(this.avatar)) {
            ((GameTexture)FormSteamFriendsList.this.avatars.get(this.avatar)).delete();
         }

         this.avatar = FormSteamFriendsList.this.steamFriends.getSmallFriendAvatar(this.steamID);
         if (this.avatar != 0 && this.avatar != 1) {
            int var1 = SteamData.getUtils().getImageWidth(this.avatar);
            int var2 = SteamData.getUtils().getImageHeight(this.avatar);

            try {
               ByteBuffer var3 = ByteBuffer.allocateDirect(var1 * var2 * 4);
               SteamData.getUtils().getImageRGBA(this.avatar, var3);
               GameTexture var4 = new GameTexture("friendAvatar" + this.steamID.toString(), var1, var2, var3);
               var4.makeFinal();
               FormSteamFriendsList.this.avatars.put(this.avatar, var4);
            } catch (SteamException var5) {
               var5.printStackTrace();
            }
         }

      }

      void draw(FormSteamFriendsList var1, TickManager var2, PlayerMob var3, int var4) {
         if (this.avatar == -1) {
            this.generateAvatarTexture();
         }

         GameTexture var5 = (GameTexture)FormSteamFriendsList.this.avatars.get(this.avatar);
         if (var5 != null) {
            var5.initDraw().size(32, 32).draw(4, 0);
         } else {
            Settings.UI.icon_unknown.initDraw().color(Settings.UI.activeButtonTextColor).draw(16 - Settings.UI.icon_unknown.getWidth() / 2, 16 - Settings.UI.icon_unknown.getHeight() / 2);
         }

         FontOptions var6 = (new FontOptions(20)).color(Settings.UI.highlightTextColor);
         String var7 = GameUtils.maxString(this.name, var6, FormSteamFriendsList.this.width - 44);
         FontManager.bit.drawString(40.0F, 0.0F, var7, var6);
         String var8;
         Color var9;
         if (this.inGame) {
            var9 = Settings.UI.successTextColor;
            if (this.gameInfo.getGameID() == 1169040L) {
               var8 = Localization.translate("ui", "playinggame", "game", "Necesse");
            } else {
               var8 = Localization.translate("ui", "playingother");
            }

            if (this.status != PersonaState.Online) {
               var8 = var8 + " (" + SteamData.getFriendStatusMessage(this.status).translate() + ")";
            }
         } else {
            var8 = SteamData.getFriendStatusMessage(this.status).translate();
            if (this.status == PersonaState.Offline) {
               var9 = Settings.UI.inactiveTextColor;
            } else {
               var9 = Settings.UI.activeTextColor;
            }
         }

         FontOptions var10 = (new FontOptions(16)).color(var9);
         String var11 = GameUtils.maxString(var8, var10, FormSteamFriendsList.this.width - 35);
         FontManager.bit.drawString(40.0F, 20.0F, var8, var10);
         if (this.isMouseOver(var1)) {
            StringTooltips var12 = new StringTooltips();
            if (!var7.equals(this.name)) {
               var12.add(this.name);
            }

            if (!var8.equals(var11)) {
               var12.add(var8);
            }

            if (var12.getSize() != 0) {
               Screen.addTooltip(var12, TooltipLocation.FORM_FOCUS);
            }
         }

      }

      void onClick(FormSteamFriendsList var1, int var2, InputEvent var3, PlayerMob var4) {
         var1.onFriendClicked(this);
      }

      void onControllerEvent(FormSteamFriendsList var1, int var2, ControllerEvent var3, TickManager var4, PlayerMob var5) {
         if (var3.getState() == ControllerInput.MENU_SELECT) {
            var1.onFriendClicked(this);
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
         this.onControllerEvent((FormSteamFriendsList)var1, var2, var3, var4, var5);
      }

      // $FF: synthetic method
      // $FF: bridge method
      void onClick(FormGeneralList var1, int var2, InputEvent var3, PlayerMob var4) {
         this.onClick((FormSteamFriendsList)var1, var2, var3, var4);
      }

      // $FF: synthetic method
      // $FF: bridge method
      void draw(FormGeneralList var1, TickManager var2, PlayerMob var3, int var4) {
         this.draw((FormSteamFriendsList)var1, var2, var3, var4);
      }
   }
}
