package me.byteful.lib.blockedit.handlers.nms;

import me.byteful.lib.blockedit.BlockEditHandler;
import me.byteful.lib.blockedit.BlockEditOption;
import net.minecraft.core.BlockPosition;
import net.minecraft.network.protocol.game.PacketPlayOutMapChunk;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.Chunk;
import net.minecraft.world.level.chunk.ChunkSection;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_17_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class v1_17_R1_BlockEditHandler implements BlockEditHandler {
  @Override
  public void updateBlock(@NotNull BlockEditOption option, @NotNull BlockState state) {
    final Location loc = state.getLocation();
    final BlockPosition bp = new BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    final IBlockData bd = ((CraftBlockData) state.getBlockData()).getState();
    final CraftChunk chunk = (CraftChunk) state.getChunk();

    chunk.getHandle().l.remove(bp);

    switch (option) {
      case NMS_SAFE:
        {
          updateBlock0(bp, bd, state);
          break;
        }

      case NMS_FAST:
        {
          updateBlock1(chunk, bp, bd);
          break;
        }

      case NMS_UNSAFE:
        {
          updateBlock2(chunk, bp, bd);
          break;
        }
    }
  }

  private void updateBlock0(
      @NotNull BlockPosition bp, @NotNull IBlockData bd, @NotNull BlockState state) {
    ((CraftChunk) state.getChunk()).getHandle().setType(bp, bd, false);
  }

  private void updateBlock1(
      @NotNull CraftChunk chunk,
      @NotNull BlockPosition bp,
      @NotNull IBlockData bd) {
    ChunkSection cs = getSection(chunk.getHandle(), bp.getY());

    cs.setType(bp.getX() & 15, bp.getY() & 15, bp.getZ() & 15, bd, false);
  }

  private void updateBlock2(
      @NotNull CraftChunk chunk,
      @NotNull BlockPosition bp,
      @NotNull IBlockData bd) {
    ChunkSection cs = getSection(chunk.getHandle(), bp.getY());

    cs.getBlocks().b(bp.getX() & 15, bp.getY() & 15, bp.getZ() & 15, bd);
  }

  private ChunkSection getSection(@NotNull Chunk chunk, int y) {
    ChunkSection cs = chunk.getSections()[y >> 4];

    if (cs == null) {
      cs = new ChunkSection(y >> 4 << 4);
      chunk.getSections()[y >> 4] = cs;
    }

    return cs;
  }

  @Override
  public void updateChunk(@NotNull Player player, int x, int z, boolean doBlockUpdates) {
    CraftWorld world = (CraftWorld) player.getWorld();
    final Chunk chunk = world.getHandle().getChunkAt(x, z);

    if (doBlockUpdates && world.isChunkLoaded(x, z)) {
      for (ChunkSection cs : chunk.getSections()) {
        if(cs != null) {
          cs.recalcBlockCounts();
        }
      }
      int px = x << 4;
      int pz = z << 4;
      int height = world.getMaxHeight() / 16;

      for (int idx = 0; idx < 64; ++idx) {
        final BlockPosition bp = new BlockPosition(px + idx / height, idx % height * 16, pz);
        final Block block = chunk.getType(bp).getBlock();
        world.getHandle().notifyAndUpdatePhysics(bp, chunk, block.getBlockData(), block.getBlockData(), block.getBlockData(), 2, 512);
      }

      final BlockPosition bp = new BlockPosition(px + 15, height * 16 - 1, pz + 15);
      final Block block = chunk.getType(bp).getBlock();
      world.getHandle().notifyAndUpdatePhysics(bp, chunk, block.getBlockData(), block.getBlockData(), block.getBlockData(), 2, 512);
    } else {
      ((CraftPlayer) player)
          .getHandle()
          .b
          .sendPacket(new PacketPlayOutMapChunk(chunk));
    }
  }
}
