package me.byteful.lib.blockedit;

import me.byteful.lib.blockedit.data.ChunkLocation;
import me.byteful.lib.blockedit.handlers.BlockEditBukkitHandler;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public final class BlockEditAPI {
  @NotNull
  private static final Set<ChunkLocation> CHUNK_BUFFER = new HashSet<>();

  private static BlockEditHandler handler;
  private static BlockEditOption option;

  public static void load(@NotNull BlockEditOption option) {
    BlockEditAPI.option = option;
    if(option == BlockEditOption.BUKKIT) {
      handler = new BlockEditBukkitHandler();
      System.out.println("[BlockEditAPI] Loaded default Bukkit handlers...");
    } else {
      final String nmsVersion =
          Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
      try {
        handler =
            Class.forName(
                    "me.byteful.lib.blockedit.handlers.nms." + nmsVersion + "_BlockEditHandler")
                .asSubclass(BlockEditHandler.class)
                .getConstructor()
                .newInstance();
        System.out.println("[BlockEditAPI] Found NMS support for version: " + nmsVersion);
      } catch (ReflectiveOperationException ignored) {
        System.out.println(
            "[BlockEditAPI] Failed to find a NMS compatible handler for version: " + nmsVersion);
        System.out.println("[BlockEditAPI] Defaulting to Bukkit handler methods...");
        handler = new BlockEditBukkitHandler();
      }
    }
  }

  public static void load(@NotNull BlockEditOption option, @NotNull BlockEditHandler handler) {
    BlockEditAPI.option = option;
    BlockEditAPI.handler = handler;
  }

  public static void setOption(@NotNull BlockEditOption option) {
    BlockEditAPI.option = option;

    if(option == BlockEditOption.BUKKIT) {
      System.out.println("[BlockEditAPI] Switching to default Bukkit handlers...");
      handler = new BlockEditBukkitHandler();
      System.out.println("[BlockEditAPI] Loaded default Bukkit handlers...");
    }
  }

  private static void runChecks(@NotNull Block block) {
    if (BlockEditAPI.handler == null) {
      throw new IllegalStateException("Please initialize BlockEditAPI before using it!");
    }

    if(!Bukkit.isPrimaryThread()) {
      throw new IllegalStateException("Cannot run operations asynchronously with BlockEditAPI!");
    }

    if(block.getY() > block.getWorld().getMaxHeight() || block.getY() < 0) {
      throw new IllegalArgumentException("Cannot modify block that is out of world bounds!");
    }
  }

  public static void updateBlockState(@NotNull BlockState state) {
    runChecks(state.getBlock());

    CHUNK_BUFFER.add(new ChunkLocation(state.getBlock()));
    handler.updateBlock(option, state);
  }

  public static void updateChunks(boolean fixLighting, boolean applyPhysics) {
    CHUNK_BUFFER.forEach(
        chunk -> {
          for (Player player : chunk.getWorld().getPlayers()) {
            final ChunkLocation playerChunk = new ChunkLocation(player.getLocation().getBlock());
            if(chunk.distance(playerChunk) <= Bukkit.getViewDistance() && chunk.getWorld().isChunkLoaded(chunk.getX(), chunk.getZ())) {
              handler.updateChunk(player, chunk.getX(), chunk.getZ(), fixLighting, applyPhysics);
            }
          }
        });
    CHUNK_BUFFER.clear();
  }
}
