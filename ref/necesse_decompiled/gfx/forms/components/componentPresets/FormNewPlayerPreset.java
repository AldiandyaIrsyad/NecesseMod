package necesse.gfx.forms.components.componentPresets;

import java.awt.Color;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.NetworkClient;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameEyes;
import necesse.gfx.GameHair;
import necesse.gfx.GameSkin;
import necesse.gfx.HumanLook;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormButton;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormHorizontalIntScroll;
import necesse.gfx.forms.components.FormHorizontalScroll;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormPlayerIcon;
import necesse.gfx.forms.components.FormSlider;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalSlider;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.floatMenu.ColorSelectorFloatMenu;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;

public class FormNewPlayerPreset extends Form {
   private PlayerMob newPlayer;
   public final FormPlayerIcon icon;
   public final FormHorizontalIntScroll rotate;
   public final FormHorizontalIntScroll hair;
   public final FormHorizontalIntScroll hairColor;
   public final FormHorizontalIntScroll skin;
   public final FormHorizontalIntScroll eyes;
   public final FormSlider shirtRed;
   public final FormSlider shirtGreen;
   public final FormSlider shirtBlue;
   public final FormSlider shoesRed;
   public final FormSlider shoesGreen;
   public final FormSlider shoesBlue;
   public final FormLocalTextButton reset;
   public final FormLocalTextButton randomize;
   private PlayerMob undoPlayer;

   public FormNewPlayerPreset(int var1, int var2, int var3) {
      super(var3, 0);
      this.setPosition(var1, var2);
      this.drawBase = false;
      this.newPlayer = new PlayerMob(0L, (NetworkClient)null);
      this.icon = (FormPlayerIcon)this.addComponent(new FormPlayerIcon(8, 4, 128, 128, this.newPlayer));
      short var4 = 144;
      int var5 = var3 - var4 - 16;
      FormFlow var6 = new FormFlow(5);
      this.reset = (FormLocalTextButton)this.addComponent(new FormLocalTextButton("ui", "resetappearance", var4, var6.next(25), var5, FormInputSize.SIZE_20, ButtonColor.BASE));
      this.rotate = (FormHorizontalIntScroll)this.addComponent(new FormHorizontalIntScroll(var4, var6.next(20), var5, FormHorizontalScroll.DrawOption.string, new LocalMessage("ui", "rotate"), 2, 0, 3));
      this.hair = (FormHorizontalIntScroll)this.addComponent(new FormHorizontalIntScroll(var4, var6.next(20), var5, FormHorizontalScroll.DrawOption.string, new LocalMessage("ui", "hair"), 1, 0, GameHair.getTotalHair() - 1));
      this.hairColor = (FormHorizontalIntScroll)this.addComponent(new FormHorizontalIntScroll(var4, var6.next(20), var5, FormHorizontalScroll.DrawOption.string, new LocalMessage("ui", "haircolor"), 0, 0, GameHair.getTotalHairColors() - 1));
      this.skin = (FormHorizontalIntScroll)this.addComponent(new FormHorizontalIntScroll(var4, var6.next(20), var5, FormHorizontalScroll.DrawOption.string, new LocalMessage("ui", "skin"), 0, 0, GameSkin.getTotalSkins() - 1));
      this.eyes = (FormHorizontalIntScroll)this.addComponent(new FormHorizontalIntScroll(var4, var6.next(20), var5, FormHorizontalScroll.DrawOption.string, new LocalMessage("ui", "eyes"), 0, 0, GameEyes.getTotalColors() - 1));
      var6.next(15);
      this.randomize = (FormLocalTextButton)this.addComponent(new FormLocalTextButton("ui", "randomappearance", 8, 120, 128, FormInputSize.SIZE_20, ButtonColor.BASE));
      int var7 = Math.max(var6.next(), this.icon.getY() + this.icon.getHeight() + 8);
      FontOptions var8 = new FontOptions(12);
      int var9 = var3 / 2 - 20;
      FormFlow var10 = new FormFlow(var7);
      this.addComponent(new FormLocalLabel("ui", "shirtcolor", new FontOptions(12), -1, 8, var10.next(15)));
      ((FormLocalTextButton)this.addComponent(new FormLocalTextButton("ui", "selectcolor", 8, var10.next(25), var9, FormInputSize.SIZE_20, ButtonColor.BASE))).onClicked((var1x) -> {
         final Color var2 = new Color(this.newPlayer.look.getShirtColor().getRGB());
         ((FormButton)var1x.from).getManager().openFloatMenu(new ColorSelectorFloatMenu(var1x.from, var2) {
            public void onApplied(Color var1) {
               if (var1 != null) {
                  var1 = HumanLook.limitClothesColor(var1);
                  FormNewPlayerPreset.this.newPlayer.look.setShirtColor(var1);
                  FormNewPlayerPreset.this.newPlayer.getInv().giveLookArmor();
                  FormNewPlayerPreset.this.resetUndo();
                  FormNewPlayerPreset.this.updateComponents();
               } else {
                  FormNewPlayerPreset.this.newPlayer.look.setShirtColor(var2);
                  FormNewPlayerPreset.this.newPlayer.getInv().giveLookArmor();
               }

            }

            public void onSelected(Color var1) {
               var1 = HumanLook.limitClothesColor(var1);
               FormNewPlayerPreset.this.newPlayer.look.setShirtColor(var1);
               FormNewPlayerPreset.this.newPlayer.getInv().giveLookArmor();
            }
         });
      });
      this.shirtRed = (FormSlider)this.addComponent((FormLocalSlider)var10.nextY(new FormLocalSlider("ui", "colorred", 8, 190, this.newPlayer.look.getShirtColor().getRed(), 50, 200, var9, var8), 5));
      this.shirtGreen = (FormSlider)this.addComponent((FormLocalSlider)var10.nextY(new FormLocalSlider("ui", "colorgreen", 8, 215, this.newPlayer.look.getShirtColor().getGreen(), 50, 200, var9, var8), 5));
      this.shirtBlue = (FormSlider)this.addComponent((FormLocalSlider)var10.nextY(new FormLocalSlider("ui", "colorblue", 8, 240, this.newPlayer.look.getShirtColor().getBlue(), 50, 200, var9, var8), 5));
      FormFlow var11 = new FormFlow(var7);
      this.addComponent(new FormLocalLabel("ui", "shoescolor", new FontOptions(12), -1, var3 - var9 - 8, var11.next(15)));
      ((FormLocalTextButton)this.addComponent(new FormLocalTextButton("ui", "selectcolor", var3 - var9 - 8, var11.next(25), var9, FormInputSize.SIZE_20, ButtonColor.BASE))).onClicked((var1x) -> {
         final Color var2 = new Color(this.newPlayer.look.getShoesColor().getRGB());
         ((FormButton)var1x.from).getManager().openFloatMenu(new ColorSelectorFloatMenu(var1x.from, var2) {
            public void onApplied(Color var1) {
               if (var1 != null) {
                  var1 = HumanLook.limitClothesColor(var1);
                  FormNewPlayerPreset.this.newPlayer.look.setShoesColor(var1);
                  FormNewPlayerPreset.this.newPlayer.getInv().giveLookArmor();
                  FormNewPlayerPreset.this.resetUndo();
                  FormNewPlayerPreset.this.updateComponents();
               } else {
                  FormNewPlayerPreset.this.newPlayer.look.setShoesColor(var2);
                  FormNewPlayerPreset.this.newPlayer.getInv().giveLookArmor();
               }

            }

            public void onSelected(Color var1) {
               var1 = HumanLook.limitClothesColor(var1);
               FormNewPlayerPreset.this.newPlayer.look.setShoesColor(var1);
               FormNewPlayerPreset.this.newPlayer.getInv().giveLookArmor();
            }
         });
      });
      this.shoesRed = (FormSlider)this.addComponent((FormLocalSlider)var11.nextY(new FormLocalSlider("ui", "colorred", var3 - var9 - 8, 190, this.newPlayer.look.getShoesColor().getRed(), 50, 200, var9, var8), 5));
      this.shoesGreen = (FormSlider)this.addComponent((FormLocalSlider)var11.nextY(new FormLocalSlider("ui", "colorgreen", var3 - var9 - 8, 215, this.newPlayer.look.getShoesColor().getGreen(), 50, 200, var9, var8), 5));
      this.shoesBlue = (FormSlider)this.addComponent((FormLocalSlider)var11.nextY(new FormLocalSlider("ui", "colorblue", var3 - var9 - 8, 240, this.newPlayer.look.getShoesColor().getBlue(), 50, 200, var9, var8), 5));
      this.setHeight(Math.max(var10.next(), var11.next()));
      this.loadDefault();
      this.rotate.onChanged((var1x) -> {
         this.icon.setRotation((Integer)this.rotate.getValue());
         this.resetUndo();
      });
      this.skin.onChanged((var1x) -> {
         this.newPlayer.look.setSkin((Integer)this.skin.getValue());
         this.resetUndo();
      });
      this.hair.onChanged((var1x) -> {
         this.newPlayer.look.setHair((Integer)this.hair.getValue());
         this.resetUndo();
      });
      this.hairColor.onChanged((var1x) -> {
         this.newPlayer.look.setHairColor((Integer)this.hairColor.getValue());
         this.resetUndo();
      });
      this.eyes.onChanged((var1x) -> {
         this.newPlayer.look.setEyeColor((Integer)this.eyes.getValue());
         this.resetUndo();
      });
      this.shirtRed.onChanged((var1x) -> {
         this.newPlayer.look.setShirtColor(new Color(this.shirtRed.getValue(), this.shirtGreen.getValue(), this.shirtBlue.getValue()));
         this.newPlayer.getInv().giveLookArmor();
         this.resetUndo();
      });
      this.shirtGreen.onChanged((var1x) -> {
         this.newPlayer.look.setShirtColor(new Color(this.shirtRed.getValue(), this.shirtGreen.getValue(), this.shirtBlue.getValue()));
         this.newPlayer.getInv().giveLookArmor();
         this.resetUndo();
      });
      this.shirtBlue.onChanged((var1x) -> {
         this.newPlayer.look.setShirtColor(new Color(this.shirtRed.getValue(), this.shirtGreen.getValue(), this.shirtBlue.getValue()));
         this.newPlayer.getInv().giveLookArmor();
         this.resetUndo();
      });
      this.shoesRed.onChanged((var1x) -> {
         this.newPlayer.look.setShoesColor(new Color(this.shoesRed.getValue(), this.shoesGreen.getValue(), this.shoesBlue.getValue()));
         this.newPlayer.getInv().giveLookArmor();
         this.resetUndo();
      });
      this.shoesGreen.onChanged((var1x) -> {
         this.newPlayer.look.setShoesColor(new Color(this.shoesRed.getValue(), this.shoesGreen.getValue(), this.shoesBlue.getValue()));
         this.newPlayer.getInv().giveLookArmor();
         this.resetUndo();
      });
      this.shoesBlue.onChanged((var1x) -> {
         this.newPlayer.look.setShoesColor(new Color(this.shoesRed.getValue(), this.shoesGreen.getValue(), this.shoesBlue.getValue()));
         this.newPlayer.getInv().giveLookArmor();
         this.resetUndo();
      });
      this.reset.onClicked((var1x) -> {
         if (this.undoPlayer != null) {
            this.undoDefault();
         } else {
            this.loadDefault(true);
         }

      });
      this.randomize.onClicked((var1x) -> {
         this.randomize();
         this.resetUndo();
      });
   }

   public void reset() {
      this.resetUndo();
      this.setPlayer(new PlayerMob(0L, (NetworkClient)null));
   }

   private void loadDefault(boolean var1) {
      if (var1) {
         this.undoPlayer = this.newPlayer;
         this.reset.setLocalization("ui", "undoreset");
      }

      this.setPlayer(new PlayerMob(0L, (NetworkClient)null));
   }

   public void loadDefault() {
      this.loadDefault(false);
   }

   private void undoDefault() {
      if (this.undoPlayer != null) {
         this.newPlayer = this.undoPlayer;
         this.setPlayer(this.newPlayer);
         this.resetUndo();
      }
   }

   private void resetUndo() {
      if (this.undoPlayer != null) {
         this.undoPlayer = null;
         this.reset.setLocalization("ui", "resetappearance");
      }
   }

   private void randomize() {
      this.newPlayer.look.randomizeLook();
      this.updateComponents();
   }

   public void setPlayer(PlayerMob var1) {
      this.newPlayer = var1;
      this.updateComponents();
   }

   public void setLook(HumanLook var1) {
      this.newPlayer.look = var1;
      this.updateComponents();
   }

   public void updateComponents() {
      this.hair.setValue(this.newPlayer.look.getHair() % GameHair.getTotalHair());
      this.newPlayer.look.setHair((Integer)this.hair.getValue());
      this.hairColor.setValue(this.newPlayer.look.getHairColor() % GameHair.getTotalHairColors());
      this.newPlayer.look.setHairColor((Integer)this.hairColor.getValue());
      this.skin.setValue(this.newPlayer.look.getSkin() % GameSkin.getTotalSkins());
      this.newPlayer.look.setSkin((Integer)this.skin.getValue());
      this.eyes.setValue(this.newPlayer.look.getEyeColor());
      this.shirtRed.setValue(this.newPlayer.look.getShirtColor().getRed());
      this.shirtGreen.setValue(this.newPlayer.look.getShirtColor().getGreen());
      this.shirtBlue.setValue(this.newPlayer.look.getShirtColor().getBlue());
      this.shoesRed.setValue(this.newPlayer.look.getShoesColor().getRed());
      this.shoesGreen.setValue(this.newPlayer.look.getShoesColor().getGreen());
      this.shoesBlue.setValue(this.newPlayer.look.getShoesColor().getBlue());
      this.icon.setPlayer(this.newPlayer);
      this.newPlayer.getInv().giveLookArmor();
   }

   public PlayerMob getNewPlayer() {
      this.newPlayer.getInv().giveStarterItems();
      return this.newPlayer;
   }
}
