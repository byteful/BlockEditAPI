package me.byteful.lib.blockedit.handlers;

import me.byteful.lib.blockedit.BlockEditHandler;
import me.byteful.lib.blockedit.BlockEditOption;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class BlockEditBukkitHandler implements BlockEditHandler {
  @Override
  public void updateBlock(@NotNull BlockEditOption option, @NotNull BlockState state) {
    state.update(true, false);
  }

  @Override
  public void updateChunk(@NotNull World world, @NotNull Collection<Player> players, int x, int z, boolean doBlockUpdates) {
    // Already handled with updateBlock()
  }
}
