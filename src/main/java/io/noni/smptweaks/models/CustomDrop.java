package io.noni.smptweaks.models;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record CustomDrop(
        @Nullable ItemStack itemStack,
        @Nullable List<String> commands
) {}
