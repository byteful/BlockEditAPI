package me.byteful.lib.blockedit.data;

import com.google.common.base.Objects;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

public class ChunkLocation {
  @NotNull
  private final String world;
  private final int x, z;

  @NotNull
  public World getWorld() {
    return java.util.Objects.requireNonNull(Bukkit.getWorld(world));
  }

  public int getX() {
    return x;
  }

  public int getZ() {
    return z;
  }

  public ChunkLocation(@NotNull World world, int x, int z) {
    this.world = world.getName();
    this.x = x;
    this.z = z;
  }

  public ChunkLocation(@NotNull Block block) {
    this(block.getWorld(), block.getX() >> 4, block.getZ() >> 4);
  }

  public int distance(@NotNull ChunkLocation other) {
    return (int) Math.hypot(Math.abs(other.x - x), Math.abs(other.z - z));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ChunkLocation that = (ChunkLocation) o;
    return x == that.x && z == that.z && Objects.equal(world, that.world);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(world, x, z);
  }
}
