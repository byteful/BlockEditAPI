package me.byteful.lib.blockedit;

import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface BlockEditHandler {
  void updateBlock(@NotNull BlockEditOption option, @NotNull BlockState state);

  void updateChunk(@NotNull Player player, int x, int z, boolean updateLight, boolean updatePhysics);
}
