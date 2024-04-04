package necesse.gfx.forms.presets.debug.tools;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import necesse.engine.Screen;
import necesse.gfx.forms.components.chat.ChatMessageList;
import necesse.gfx.forms.presets.debug.DebugForm;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;
import necesse.level.maps.levelData.settlementData.SettlementRoom;

public class RoomAnalyzerGameTool extends MouseDebugGameTool {
   public RoomAnalyzerGameTool(DebugForm var1) {
      super(var1, "Room analyzer");
      this.onLeftClick((var2) -> {
         Level var3 = this.getLevel();
         SettlementLevelData var4 = new SettlementLevelData();
         var4.setLevel(var3);
         SettlementRoom var5 = new SettlementRoom(var4, (HashMap)null, this.getMouseTileX(), this.getMouseTileY());
         List var6 = var5.getDebugTooltips();
         ChatMessageList var10001 = var1.client.chat;
         Objects.requireNonNull(var10001);
         var6.forEach(var10001::addMessage);
         return true;
      }, "Analyze room");
      this.onRightClick((var1x) -> {
         Screen.clearGameTool(this);
         return true;
      }, "Cancel");
   }

   public void init() {
   }
}
