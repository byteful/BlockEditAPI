package me.byteful.lib.blockedit;

import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface BlockEditHandler {
  void updateBlock(@NotNull BlockEditOption option, @NotNull BlockState state);

  void updateChunk(@NotNull World world, @NotNull Collection<Player> players, int x, int z, boolean doBlockUpdates);
}
