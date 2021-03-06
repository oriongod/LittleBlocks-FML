package slimevoid.littleblocks.blocks.core;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import slimevoid.littleblocks.blocks.BlockLittleBlocks;
import slimevoid.littleblocks.core.LBCore;
import slimevoid.littleblocks.tileentities.TileEntityLittleBlocks;

public class CollisionRayTrace {

	public static List<Object[]> rayTraceLittleBlocks(BlockLittleBlocks littleBlocks, Vec3 player, Vec3 view, int i, int j, int k, List<Object[]> returns, int[][][] content, TileEntityLittleBlocks tile) {
		float m = LBCore.littleBlocksSize;
		for (int x = 0; x < content.length; x++) {
			for (int y = 0; y < content[x].length; y++) {
				for (int z = 0; z < content[x][y].length; z++) {
					if (content[x][y][z] > 0) {
						Block block = Block.blocksList[content[x][y][z]];
						if (block != null) {
							block.collisionRayTrace(
									(World) tile.getLittleWorld(),
									(i << 3) + x,
									(j << 3) + y,
									(k << 3) + z,
									player,
									view);
							Object[] ret = littleBlocks.rayTraceBound(
									AxisAlignedBB.getBoundingBox(
											(x + block.getBlockBoundsMinX()) / m,
											(y + block.getBlockBoundsMinY()) / m,
											(z + block.getBlockBoundsMinZ()) / m,
											(x + block.getBlockBoundsMaxX()) / m,
											(y + block.getBlockBoundsMaxY()) / m,
											(z + block.getBlockBoundsMaxZ()) / m),
									i,
									j,
									k,
									player,
									view);
							if (ret != null) {
								returns.add(new Object[] {
										ret[0],
										ret[1],
										ret[2],
										x,
										y,
										z });
							}
						}
					}
				}
			}
		}
		return returns;
	}

	public static List<Object[]> collisionRayTracerX(BlockLittleBlocks littleBlocks, World world, Vec3 player, Vec3 view, int x, int y, int z, int xx, List<Object[]> returns) {
		int m = LBCore.littleBlocksSize;
		int block = world.getBlockId(x, y, z); // -X
		if (block > 0 && Block.blocksList[block].isOpaqueCube()) {
			for (int yy = 0; yy < m; yy++) {
				for (int zz = 0; zz < m; zz++) {
					Object[] ret = littleBlocks.rayTraceBound(
							AxisAlignedBB.getBoundingBox(
									xx / (float) m,
									yy / (float) m,
									zz / (float) m,
									(xx + 1) / (float) m,
									(yy + 1) / (float) m,
									(zz + 1) / (float) m),
							x,
							y,
							z,
							player,
							view);
					if (ret != null) {
						returns.add(new Object[] {
								ret[0],
								ret[1],
								ret[2],
								xx,
								yy,
								zz });
					}
				}
			}
		}
		return returns;
	}

	public static List<Object[]> collisionRayTracerY(BlockLittleBlocks littleBlocks, World world, Vec3 player, Vec3 view, int x, int y, int z, int yy, List<Object[]> returns) {
		int m = LBCore.littleBlocksSize;
		int block = world.getBlockId(x, y, z); // DOWN
		if (block > 0 && Block.blocksList[block].isOpaqueCube()) {
			for (int xx = 0; xx < m; xx++) {
				for (int zz = 0; zz < m; zz++) {
					Object[] ret = littleBlocks.rayTraceBound(
							AxisAlignedBB.getBoundingBox(
									xx / (float) m,
									yy / (float) m,
									zz / (float) m,
									(xx + 1) / (float) m,
									(yy + 1) / (float) m,
									(zz + 1) / (float) m),
							x,
							y,
							z,
							player,
							view);
					if (ret != null) {
						returns.add(new Object[] {
								ret[0],
								ret[1],
								ret[2],
								xx,
								yy,
								zz });
					}
				}
			}
		}
		return returns;
	}

	public static List<Object[]> collisionRayTracerZ(BlockLittleBlocks littleBlocks, World world, Vec3 player, Vec3 view, int x, int y, int z, int zz, List<Object[]> returns) {
		int m = LBCore.littleBlocksSize;
		int block = world.getBlockId(x, y, z); // -Z
		if (block > 0 && Block.blocksList[block].isOpaqueCube()) {
			for (int yy = 0; yy < m; yy++) {
				for (int xx = 0; xx < m; xx++) {
					Object[] ret = littleBlocks.rayTraceBound(
							AxisAlignedBB.getBoundingBox(
									xx / (float) m,
									yy / (float) m,
									zz / (float) m,
									(xx + 1) / (float) m,
									(yy + 1) / (float) m,
									(zz + 1) / (float) m),
							x,
							y,
							z,
							player,
							view);
					if (ret != null) {
						returns.add(new Object[] {
								ret[0],
								ret[1],
								ret[2],
								xx,
								yy,
								zz });
					}
				}
			}
		}
		return returns;
	}

	public static List<Object[]> collisionRayTracer(BlockLittleBlocks littleBlocks, World world, Vec3 player, Vec3 view, int x, int y, int z, List<Object[]> returns) {
		int m = LBCore.littleBlocksSize;
		/*
		 * UP
		 */
		returns = collisionRayTracerY(
				littleBlocks,
				world,
				player,
				view,
				x,
				y - 1,
				z,
				-1,
				returns);
		/*
		 * DOWN
		 */
		returns = collisionRayTracerY(
				littleBlocks,
				world,
				player,
				view,
				x,
				y + 1,
				z,
				m,
				returns);
		/*
		 * -X
		 */
		returns = collisionRayTracerX(
				littleBlocks,
				world,
				player,
				view,
				x - 1,
				y,
				z,
				-1,
				returns);
		/*
		 * +X
		 */
		returns = collisionRayTracerX(
				littleBlocks,
				world,
				player,
				view,
				x + 1,
				y,
				z,
				m,
				returns);
		/*
		 * -Z
		 */
		returns = collisionRayTracerZ(
				littleBlocks,
				world,
				player,
				view,
				x,
				y,
				z - 1,
				-1,
				returns);
		/*
		 * +Z
		 */
		returns = collisionRayTracerZ(
				littleBlocks,
				world,
				player,
				view,
				x,
				y,
				z + 1,
				m,
				returns);
		return returns;
	}
}
