package io.noni.smptweaks.models;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public record CustomDropSet(
        @Nullable Integer xp,
        @Nullable Map<CustomDrop, Float> possibleDrops,
        @Nullable List<String> commands,
        boolean discardVanillaDrops
) {}
