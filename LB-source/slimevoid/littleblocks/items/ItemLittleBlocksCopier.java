package slimevoid.littleblocks.items;

import java.util.HashMap;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import slimevoid.lib.data.ReadWriteLock;
import slimevoid.littleblocks.core.LBCore;
import slimevoid.littleblocks.core.LoggerLittleBlocks;
import slimevoid.littleblocks.core.lib.CommandLib;
import slimevoid.littleblocks.core.lib.ResourceLib;
import slimevoid.littleblocks.handlers.LittleBlocksRotationHandler;
import slimevoid.littleblocks.network.packets.PacketLittleNotify;
import slimevoid.littleblocks.tileentities.TileEntityLittleBlocks;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemLittleBlocksCopier extends Item {

	public static HashMap<EntityPlayer, TileEntityLittleBlocks> selectedLittleTiles = new HashMap<EntityPlayer, TileEntityLittleBlocks>();
	public static ReadWriteLock tileLock = new ReadWriteLock();

	public ItemLittleBlocksCopier(int id) {
		super(id);
		this.setNoRepair();
		this.setCreativeTab(CreativeTabs.tabTools);
	}

	@Override
	public boolean onItemUseFirst(ItemStack itemstack, EntityPlayer entityplayer, World world, int x, int y, int z, int l, float a, float b, float c) {
		if (!world.isRemote) {
			if (entityplayer.capabilities.isCreativeMode) {
				if (entityplayer.isSneaking()) {
					TileEntity tileentity = world.getBlockTileEntity(x, y, z);
					if (tileentity != null && tileentity instanceof TileEntityLittleBlocks) {
						TileEntityLittleBlocks tilelb = (TileEntityLittleBlocks)tileentity;
						LittleBlocksRotationHandler tileToRotate = new LittleBlocksRotationHandler(world, entityplayer, tilelb, x, y, z, l);
						tileToRotate.rotateTile();
					}
					return true;
				} else {
					TileEntity tileentity = world.getBlockTileEntity(x, y, z);
					if (tileentity != null && tileentity instanceof TileEntityLittleBlocks) {
						try {
							tileLock.writeLock(world);
							selectedLittleTiles.put(
									entityplayer,
									(TileEntityLittleBlocks) tileentity);
							tileLock.writeUnlock();
						} catch (InterruptedException e) {
							LoggerLittleBlocks
									.getInstance("ItemLittleBlocksCopier")
										.writeStackTrace(e);
						}
						return true;
					} else if (tileentity == null) {
						try {
							tileLock.readLock(world);
							if (!selectedLittleTiles.containsKey(entityplayer)) {
								tileLock.readUnlock();
								return false;
							}
							TileEntityLittleBlocks selectedLittleTile = selectedLittleTiles
									.get(entityplayer);
							tileLock.readUnlock();
							int xx = x, yy = y, zz = z;
							if (selectedLittleTile != null) {
								if (l == 0) {
									--yy;
								}
	
								if (l == 1) {
									++yy;
								}
	
								if (l == 2) {
									--zz;
								}
	
								if (l == 3) {
									++zz;
								}
	
								if (l == 4) {
									--xx;
								}
	
								if (l == 5) {
									++xx;
								}
								world.setBlock(xx, yy, zz, LBCore.littleBlocksID);
								TileEntity newtile = world.getBlockTileEntity(
										xx,
										yy,
										zz);
								if (newtile != null && newtile instanceof TileEntityLittleBlocks) {
									TileEntityLittleBlocks newtilelb = (TileEntityLittleBlocks) newtile;
									tileLock.readLock(world);
									TileEntityLittleBlocks oldtile = selectedLittleTiles
											.get(entityplayer);
									tileLock.readUnlock();
									for (int x1 = 0; x1 < LBCore.littleBlocksSize; x1++) {
										for (int y1 = 0; y1 < LBCore.littleBlocksSize; y1++) {
											for (int z1 = 0; z1 < LBCore.littleBlocksSize; z1++) {
												if (oldtile.getContent(x1, y1, z1) > 0) {
													newtilelb.setContent(
															x1,
															y1,
															z1,
															oldtile.getContent(
																	x1,
																	y1,
																	z1),
															oldtile.getMetadata(
																	x1,
																	y1,
																	z1));
												}
											}
										}
									}
									newtilelb.onInventoryChanged();
									world.markBlockForUpdate(xx, yy, zz);
								}
							}
							return true;
						} catch (InterruptedException e) {
							LoggerLittleBlocks
									.getInstance("ItemLittleBlocksCopier")
										.writeStackTrace(e);
						}
					}
				}
			} else {
				PacketDispatcher.sendPacketToPlayer(
						new PacketLittleNotify(
								CommandLib.COPIER_MESSAGE).getPacket(),
								(Player) entityplayer);
			}
		}
		return false;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public String getTextureFile() {
		return ResourceLib.ITEM_SPRITE_PATH;
	}
}
