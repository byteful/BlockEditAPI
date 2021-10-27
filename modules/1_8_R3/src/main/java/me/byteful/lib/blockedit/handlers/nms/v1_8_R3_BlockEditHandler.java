package me.byteful.lib.blockedit.handlers.nms;

import me.byteful.lib.blockedit.BlockEditHandler;
import me.byteful.lib.blockedit.BlockEditOption;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_8_R3.CraftChunk;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class v1_8_R3_BlockEditHandler implements BlockEditHandler {
  @Override
  public void updateBlock(@NotNull BlockEditOption option, @NotNull BlockState state) {
    final Location loc = state.getLocation();
    final MaterialData data = state.getData();
    final BlockPosition bp = new BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    final int id = data.getItemType().getId() + (data.getData() << 12);
    final IBlockData bd = net.minecraft.server.v1_8_R3.Block.getByCombinedId(id);
    final CraftChunk chunk = (CraftChunk) state.getChunk();

    chunk.getHandle().tileEntities.remove(bp);

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
    ((CraftChunk) state.getChunk()).getHandle().a(bp, bd);
  }

  private void updateBlock1(
      @NotNull CraftChunk chunk,
      @NotNull BlockPosition bp,
      @NotNull IBlockData bd) {
    ChunkSection cs = getSection(chunk.getHandle(), bp.getY());

    cs.setType(bp.getX() & 15, bp.getY() & 15, bp.getZ() & 15, bd);
  }

  private void updateBlock2(
      @NotNull CraftChunk chunk,
      @NotNull BlockPosition bp,
      @NotNull IBlockData bd) {
    ChunkSection cs = getSection(chunk.getHandle(), bp.getY());

    cs.getIdArray()[bp.getY() & 15 << 8 | bp.getZ() & 15 << 4 | bp.getX() & 15] =
        (char) Block.d.b(bd);
  }

  private ChunkSection getSection(@NotNull Chunk chunk, int y) {
    ChunkSection cs = chunk.getSections()[y >> 4];

    if (cs == null) {
      cs = new ChunkSection(y >> 4 << 4, true);
      chunk.getSections()[y >> 4] = cs;
    }

    return cs;
  }

  @Override
  public void updateChunk(@NotNull org.bukkit.World world, @NotNull Collection<Player> players, int x, int z, boolean doBlockUpdates) {
    if(players.isEmpty()) {
      return;
    }

    CraftWorld cw = (CraftWorld) world;
    final Chunk chunk = cw.getHandle().getChunkAt(x, z);

    if (doBlockUpdates && cw.isChunkLoaded(x, z)) {
      for (ChunkSection cs : chunk.getSections()) {
        if(cs != null) {
          cs.recalcBlockCounts();
        }
      }
      chunk.initLighting();
      int px = x << 4;
      int pz = z << 4;
      int height = cw.getMaxHeight() / 16;

      for (int idx = 0; idx < 64; ++idx) {
        final BlockPosition bp = new BlockPosition(px + idx / height, idx % height * 16, pz);
        final Block block = chunk.getType(bp);
        cw.getHandle().notifyAndUpdatePhysics(bp, chunk, block, block, 3);
      }

      final BlockPosition bp = new BlockPosition(px + 15, height * 16 - 1, pz + 15);
      final Block block = chunk.getType(bp);
      cw.getHandle().notifyAndUpdatePhysics(bp, chunk, block, block, 3);
    } else {
      players.forEach(player ->
          ((CraftPlayer) player)
          .getHandle()
          .playerConnection
          .sendPacket(new PacketPlayOutMapChunk(chunk, true, 65535)));
    }
  }
}
