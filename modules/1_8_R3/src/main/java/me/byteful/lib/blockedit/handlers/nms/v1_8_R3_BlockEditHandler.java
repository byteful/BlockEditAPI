package me.byteful.lib.blockedit.handlers.nms;

import me.byteful.lib.blockedit.BlockEditHandler;
import me.byteful.lib.blockedit.BlockEditOption;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class v1_8_R3_BlockEditHandler implements BlockEditHandler {
  @Override
  public void updateBlock(@NotNull BlockEditOption option, @NotNull BlockState state) {
    switch (option) {
      case NMS_SAFE: {
        updateBlock0(option, state);
        break;
      }

      case NMS_FAST: {
        updateBlock1(option, state);
        break;
      }

      case NMS_UNSAFE: {
        updateBlock2(option, state);
        break;
      }
    }
  }

  private void updateBlock0(@NotNull BlockEditOption option, @NotNull BlockState state) {

  }

  private void updateBlock1(@NotNull BlockEditOption option, @NotNull BlockState state) {

  }

  private void updateBlock2(@NotNull BlockEditOption option, @NotNull BlockState state) {

  }

  @Override
  public void updateChunk(@NotNull Player player, int x, int z, boolean updateLight, boolean updatePhysics) {
    CraftWorld world = (CraftWorld) player.getWorld();
    final Chunk chunk = world.getHandle().getChunkAt(x, z);

    if (updateLight) {
      if (world.isChunkLoaded(x, z)) {
        int px = x << 4;
        int pz = z << 4;
        int height = world.getMaxHeight() / 16;

        for (int idx = 0; idx < 64; ++idx) {
          final BlockPosition bp = new BlockPosition(px + idx / height, idx % height * 16, pz);
          final Block block = chunk.getType(bp);
          world.getHandle().notifyAndUpdatePhysics(bp, chunk, block, block, updatePhysics ? 3 : 2);
        }

        final BlockPosition bp = new BlockPosition(px + 15, height * 16 - 1, pz + 15);
        final Block block = chunk.getType(bp);
        world.getHandle().notifyAndUpdatePhysics(bp, chunk, block, block, updatePhysics ? 3 : 2);
      }
    } else {
      ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutMapChunk(chunk, true, 65535));
    }
  }
}
