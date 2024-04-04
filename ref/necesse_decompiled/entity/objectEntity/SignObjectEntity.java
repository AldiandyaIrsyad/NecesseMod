package necesse.entity.objectEntity;

import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.fairType.FairType;
import necesse.gfx.fairType.FairTypeDrawOptions;
import necesse.gfx.forms.presets.containerComponent.object.SignContainerForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.FairTypeTooltip;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.level.maps.Level;

public class SignObjectEntity extends ObjectEntity {
   private String text;
   private FairTypeDrawOptions textDrawOptions;
   private int textDrawFontSize;

   public SignObjectEntity(Level var1, int var2, int var3) {
      super(var1, "sign", var2, var3);
      this.setText("");
   }

   public void addSaveData(SaveData var1) {
      super.addSaveData(var1);
      var1.addSafeString("text", this.text);
   }

   public void applyLoadData(LoadData var1) {
      super.applyLoadData(var1);
      this.setText(var1.getSafeString("text", ""));
   }

   public void setupContentPacket(PacketWriter var1) {
      super.setupContentPacket(var1);
      String var2 = this.getTextString();
      var1.putNextString(var2);
   }

   public void applyContentPacket(PacketReader var1) {
      super.applyContentPacket(var1);
      this.setText(var1.getNextString());
   }

   public void setText(String var1) {
      String var2 = this.text;
      this.text = var1.trim();
      if (!this.text.equals(var2)) {
         this.textDrawOptions = null;
      }

   }

   private FairTypeDrawOptions getTextDrawOptions() {
      if (this.textDrawOptions == null || this.textDrawOptions.shouldUpdate() || this.textDrawFontSize != Settings.tooltipTextSize) {
         FairType var1 = new FairType();
         FontOptions var2 = (new FontOptions(Settings.tooltipTextSize)).outline();
         var1.append(var2, this.text);
         var1.applyParsers(SignContainerForm.getParsers(var2));
         this.textDrawOptions = var1.getDrawOptions(FairType.TextAlign.LEFT, 400, true, true);
         this.textDrawFontSize = var2.getSize();
      }

      return this.textDrawOptions;
   }

   public String getTextString() {
      return this.text;
   }

   public void onMouseHover(PlayerMob var1, boolean var2) {
      super.onMouseHover(var1, var2);
      Screen.addTooltip(new FairTypeTooltip(this.getTextDrawOptions()), TooltipLocation.INTERACT_FOCUS);
   }

   public GameTooltips getMapTooltips() {
      ListGameTooltips var1 = new ListGameTooltips(new StringTooltips(this.getObject().getDisplayName() + ":"));
      var1.add((Object)(new FairTypeTooltip(this.getTextDrawOptions())));
      return var1;
   }
}
