package necesse.gfx.forms.presets.debug.tools;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;
import necesse.engine.control.InputEvent;
import necesse.gfx.forms.presets.debug.DebugForm;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.InputTooltip;
import necesse.gfx.gameTooltips.ListGameTooltips;

public abstract class MouseDebugGameTool extends DebugGameTool {
   protected Function<InputEvent, Boolean> scroll;
   protected Function<InputEvent, Boolean> mouseMove;
   protected String scrollUsage;
   protected HashMap<Integer, Function<InputEvent, Boolean>> keyEvents = new HashMap();
   protected HashMap<Integer, String> keyUsages = new HashMap();

   public MouseDebugGameTool(DebugForm var1, String var2) {
      super(var1, var2);
   }

   public void setKeyUsage(int var1, String var2) {
      this.keyUsages.put(var1, var2);
   }

   public void onKeyEvent(int var1, Function<InputEvent, Boolean> var2, String var3) {
      this.keyEvents.put(var1, var2);
      this.setKeyUsage(var1, var3);
   }

   public void onKeyClick(int var1, Function<InputEvent, Boolean> var2, String var3) {
      this.onKeyEvent(var1, (var1x) -> {
         return var1x.state ? true : (Boolean)var2.apply(var1x);
      }, var3);
   }

   public void setLeftUsage(String var1) {
      this.setKeyUsage(-100, var1);
   }

   public void onLeftEvent(Function<InputEvent, Boolean> var1, String var2) {
      this.onKeyEvent(-100, var1, var2);
   }

   public void onLeftClick(Function<InputEvent, Boolean> var1, String var2) {
      this.onKeyClick(-100, var1, var2);
   }

   public void setRightUsage(String var1) {
      this.setKeyUsage(-99, var1);
   }

   public void onRightEvent(Function<InputEvent, Boolean> var1, String var2) {
      this.onKeyEvent(-99, var1, var2);
   }

   public void onRightClick(Function<InputEvent, Boolean> var1, String var2) {
      this.onKeyClick(-99, var1, var2);
   }

   public void setScrollUsage(String var1) {
      this.scrollUsage = var1;
   }

   public void onScroll(Function<InputEvent, Boolean> var1, String var2) {
      this.scroll = var1;
      this.setScrollUsage(var2);
   }

   public void onMouseMove(Function<InputEvent, Boolean> var1) {
      this.mouseMove = var1;
   }

   public boolean inputEvent(InputEvent var1) {
      if (this.parent.mainGame.formManager.isMouseOver(var1)) {
         return false;
      } else {
         try {
            if (var1.isMouseMoveEvent()) {
               if (this.mouseMove != null) {
                  return (Boolean)this.mouseMove.apply(var1);
               }
            } else if (var1.isMouseWheelEvent()) {
               if (this.scroll != null) {
                  return !var1.state || (Boolean)this.scroll.apply(var1);
               }
            } else {
               Function var2 = (Function)this.keyEvents.get(var1.getID());
               if (var2 != null) {
                  return (Boolean)var2.apply(var1);
               }
            }

            return false;
         } catch (Exception var3) {
            System.err.println(this.name + " debug tool error:");
            var3.printStackTrace();
            return true;
         }
      }
   }

   public GameTooltips getTooltips() {
      ListGameTooltips var1 = new ListGameTooltips(super.getTooltips());
      ArrayList var2 = new ArrayList();
      Iterator var3 = this.keyUsages.entrySet().iterator();

      Map.Entry var4;
      while(var3.hasNext()) {
         var4 = (Map.Entry)var3.next();
         if (var4.getValue() != null && !((String)var4.getValue()).isEmpty()) {
            var2.add(var4);
         }
      }

      if (this.scrollUsage != null && !this.scrollUsage.isEmpty()) {
         var2.add(new Map.Entry<Integer, String>() {
            public Integer getKey() {
               return -98;
            }

            public String getValue() {
               return MouseDebugGameTool.this.scrollUsage;
            }

            public String setValue(String var1) {
               return var1;
            }

            // $FF: synthetic method
            // $FF: bridge method
            public Object setValue(Object var1) {
               return this.setValue((String)var1);
            }

            // $FF: synthetic method
            // $FF: bridge method
            public Object getValue() {
               return this.getValue();
            }

            // $FF: synthetic method
            // $FF: bridge method
            public Object getKey() {
               return this.getKey();
            }
         });
      }

      var2.sort(Comparator.comparingInt(Map.Entry::getKey));
      var3 = var2.iterator();

      while(var3.hasNext()) {
         var4 = (Map.Entry)var3.next();
         var1.add((Object)(new InputTooltip((Integer)var4.getKey(), (String)var4.getValue())));
      }

      return var1;
   }
}
