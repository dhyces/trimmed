Ported to 1.20.6
Release for NeoForge

Many things have changed in this version. Firstly, trim related maps have moved from the several maps found in
`maps/unchecked/whatever` to just `trimmed/maps/trimmed/trim_textures/material_suffixes.json` and 
`trimmed/maps/trimmed/trim_textures/material_suffixes/darker_material_suffixes.json`. The latter is used for any trim
material textures that are meant to be applied instead of the default, for example an iron ingot on iron armor will use 
the `iron_darker` texture instead.