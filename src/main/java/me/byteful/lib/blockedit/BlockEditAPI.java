package me.byteful.lib.blockedit;

import me.byteful.lib.blockedit.data.ChunkLocation;
import me.byteful.lib.blockedit.handlers.BlockEditBukkitHandler;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The core class of BlockEditAPI. All available methods are found here.
 */
public final class BlockEditAPI {
  @NotNull
  private static final Set<ChunkLocation> CHUNK_BUFFER = new HashSet<>();

  private static BlockEditHandler handler;
  private static BlockEditOption option;

  /**
   * Loads BlockEditAPI and automatically finds the BlockEditHandler from the provided option and server version.
   *
   * @param option the option to use
   */
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

  /**
   * Loads BlockEditAPI with the provided option and handler.
   *
   * @param option the option to use
   * @param handler the handler to use
   */
  public static void load(@NotNull BlockEditOption option, @NotNull BlockEditHandler handler) {
    BlockEditAPI.option = option;
    BlockEditAPI.handler = handler;
  }

  /**
   * Updates/changes the option to the provided option.
   *
   * @param option the new option
   */
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

    if(!block.getChunk().isLoaded()) {
      block.getChunk().load(false);
    }
  }

  /**
   * Updates the given BlockState with the set option.
   *
   * @param state the blockstate to update
   */
  public static void updateBlockState(@NotNull BlockState state) {
    runChecks(state.getBlock());

    CHUNK_BUFFER.add(new ChunkLocation(state.getBlock()));
    handler.updateBlock(option, state);
  }

  /**
   * Updates all the chunks in the buffer. This will push all changes done to the blocks to all players who can currently see the modified chunks.
   *
   * @param doBlockUpdates true if lighting and physics should be updated
   */
  public static void updateChunks(boolean doBlockUpdates) {
    CHUNK_BUFFER.forEach(
        chunk -> {
          final List<Player> players = chunk.getWorld().getPlayers();
          players.removeIf(player -> {
            final ChunkLocation playerChunk = new ChunkLocation(player.getLocation().getBlock());
            if(chunk.distance(playerChunk) >= Bukkit.getViewDistance() && chunk.getWorld().isChunkLoaded(chunk.getX(), chunk.getZ())) {
              return true;
            }

            return false;
          });

          handler.updateChunk(chunk.getWorld(), players, chunk.getX(), chunk.getZ(), doBlockUpdates);
        });
    CHUNK_BUFFER.clear();
  }
}
