package me.byteful.lib.blockedit.handlers;

import me.byteful.lib.blockedit.BlockEditHandler;
import me.byteful.lib.blockedit.BlockEditOption;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BlockEditBukkitHandler implements BlockEditHandler {
  @Override
  public void updateBlock(@NotNull BlockEditOption option, @NotNull BlockState state) {
    state.update(true, false);
  }

  @Override
  public void updateChunk(@NotNull Player player, int x, int z, boolean updateLight, boolean updatePhysics) {
    // Chunk is already updated in updateBlock(...);
  }
}
