package necesse.level.maps.presets.set;

import necesse.level.maps.presets.Preset;

public interface PresetSet<T> {
   <C extends Preset> C replacePreset(T var1, C var2);
}
