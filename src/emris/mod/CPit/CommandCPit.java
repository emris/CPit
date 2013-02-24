package emris.mods.CPit;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import TFC.TFCBlocks;
import TFC.TFCItems;
import TFC.TileEntities.TileEntityTerraLogPile;
import emris.mods.Air.PlayerUtils;

public class CommandCPit extends CommandBase {

	@Override
	public String getCommandName() {
		return "cpit";
	}

	@Override
	public String getCommandUsage(ICommandSender var1) {
		return "/" + getCommandName() + " [x] [z] [y] or [del] (max: 25 25 13)";
	}

	@Override
	public List getCommandAliases() {
		return null;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		Boolean argDel = false;
		Boolean argOK = false;
		int xSize = 0;
		int zSize = 0;
		int ySize = 0;
		
		if (args.length == 1 ) {
			if (args[0].equalsIgnoreCase("del")) {
				argDel = true;
			} else {
				xSize = checkArgs(args[0]);
				if (xSize > 0) {
					argOK = true;
					zSize = xSize;
					if (xSize > 13) { ySize = 13; } else { ySize = xSize; }
				}
			}
		} else if (args.length == 2) {
			xSize = checkArgs(args[0]);
			if (xSize > 0) {
				argOK = true;
				zSize = xSize;
				if (xSize > 13) { ySize = 13; } else { ySize = xSize; }
			}
			if (argOK) {
				argOK = false;
				zSize = checkArgs(args[1]);
				if (zSize > 0) {
					argOK = true;
				}
			}
		} else if (args.length == 3) {
			xSize = checkArgs(args[0]);
			if (xSize > 0) {
				argOK = true;
				zSize = xSize;
				if (xSize > 13) { ySize = 13; } else { ySize = xSize; }
			}
			if (argOK) {
				argOK = false;
				zSize = checkArgs(args[1]);
				if (zSize > 0) {
					argOK = true;
				}
			}
			if (argOK) {
				argOK = false;
				ySize = checkArgs(args[2]);
				if (ySize > 0 && ySize < 14) {
					argOK = true;
				}
			}
		}

		if (argOK) {
			makePit(sender, xSize, zSize, ySize);
		} else if (argDel) {
			delPit(sender);
		} else {
			sender.sendChatToPlayer(getCommandUsage(sender));
		}
	}

	private int checkArgs(String s) {
		int ret = 0;
		for (int i=2; i < 26; i++) {
			if (s.equalsIgnoreCase("" + i)) {
				ret = Integer.parseInt(s);
			}
		}
		return ret;
	}
	
	@Override
	public boolean canCommandSenderUseCommand(ICommandSender var1) {
		if (var1 instanceof EntityPlayerMP) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public List addTabCompletionOptions(ICommandSender var1, String[] var2) {
		return null;
	}

	private void makePit(ICommandSender sender, int xSize, int zSize, int ySize) {
		EntityPlayerMP curPlayer = (EntityPlayerMP) sender;
		MovingObjectPosition mop = PlayerUtils.getTargetBlock(curPlayer);
		if (mop != null) {
			World world = curPlayer.worldObj;
			//Looking direction: 0 -> South, 1 -> West, 2 -> North, 3 -> East 
			int look = MathHelper.floor_double((double)(curPlayer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

			int x = mop.blockX;
			int y = mop.blockY + 1;
			int z = mop.blockZ;
			
			// For the Box around the wood
			xSize += 2;
			ySize += 1;
			zSize += 2;
		
			switch(look){
				case 0:
					for (int zz = z; zz < (z + zSize); zz++) {
						for (int xx = x; xx > (x - xSize); xx--) {
							world.setBlockAndMetadataWithNotify(xx, y, zz, TFCBlocks.StoneIgInBrick.blockID, 1);
						}
					}
					
					TileEntityTerraLogPile te = null;
					for (int yy = y + 1; yy <= (y + ySize); yy++) {
						for (int zz= z + 1; zz < (z + zSize - 1); zz++) {
							for (int xx = x - 1; xx > (x - xSize + 1); xx--) {
								if (yy == y + ySize) {
									world.setBlockWithNotify(xx, yy, zz, Block.glass.blockID);
								} else {
									world.setBlockAndMetadataWithNotify(xx, yy, zz, TFCBlocks.LogPile.blockID, 1);
									te = (TileEntityTerraLogPile)world.getBlockTileEntity(xx, yy, zz);
									if (te != null) {
										te.storage[0] = new ItemStack(TFCItems.Logs, 4, 1);
										te.storage[1] = new ItemStack(TFCItems.Logs, 4, 1);
										te.storage[2] = new ItemStack(TFCItems.Logs, 4, 1);
										te.storage[3] = new ItemStack(TFCItems.Logs, 4, 1);
									}
								}
							}
						}
						
						world.setBlockAndMetadataWithNotify(x, yy, z, TFCBlocks.StoneIgInBrick.blockID, 1);
						world.setBlockAndMetadataWithNotify(x, yy, z + zSize - 1, TFCBlocks.StoneIgInBrick.blockID, 1);
						world.setBlockAndMetadataWithNotify(x - xSize + 1, yy, z, TFCBlocks.StoneIgInBrick.blockID, 1);
						world.setBlockAndMetadataWithNotify(x - xSize + 1, yy, z + zSize - 1, TFCBlocks.StoneIgInBrick.blockID, 1);

						for (int xx = x - 1; xx > (x - xSize + 1); xx--) {
							if (yy == y + ySize) {
								world.setBlockAndMetadataWithNotify(xx, yy, z, TFCBlocks.StoneIgInBrick.blockID, 1);
								world.setBlockAndMetadataWithNotify(xx, yy, z + zSize - 1, TFCBlocks.StoneIgInBrick.blockID, 1);
							} else {
								world.setBlockWithNotify(xx, yy, z, Block.glass.blockID);
								world.setBlockWithNotify(xx, yy, z + zSize - 1, Block.glass.blockID);
							}
						}
						for (int zz = z + 1; zz < (z + zSize - 1); zz++) {
							if (yy ==  y + ySize) {
								world.setBlockAndMetadataWithNotify(x, yy, zz, TFCBlocks.StoneIgInBrick.blockID, 1);
								world.setBlockAndMetadataWithNotify(x - xSize + 1, yy, zz, TFCBlocks.StoneIgInBrick.blockID, 1);
							} else {
								world.setBlockWithNotify(x, yy, zz, Block.glass.blockID);
								world.setBlockWithNotify(x - xSize + 1, yy, zz, Block.glass.blockID);
							}
						}
					}
					break;
				case 1:
					break;
				case 2:
					break;
				case 3:
					break;
			}
		}
	}

	private void delPit(ICommandSender sender) {
		int xSize = 0;
		int zSize = 0;
		int ySize = 0;
		Boolean pitOK = false;
		
		EntityPlayerMP curPlayer = (EntityPlayerMP) sender;
		MovingObjectPosition mop = PlayerUtils.getTargetBlock(curPlayer);
		if (mop != null) {
			World world = curPlayer.worldObj;
			//Looking direction: 0 -> South, 1 -> West, 2 -> North, 3 -> East 
			int look = MathHelper.floor_double((double)(curPlayer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

			int x = mop.blockX;
			int y = mop.blockY;
			int z = mop.blockZ;

			int bID = world.getBlockId(x, y, z);
			int meta = world.getBlockMetadata(x, y, z);

			switch(look) {
				case 0:
					if (bID == TFCBlocks.StoneIgInBrick.blockID && meta == 1) {
						int bID2 = world.getBlockId(x - 1, y + 1, z);
						int bID3 = world.getBlockId(x, y + 1, z + 1);
						if (bID2 == Block.glass.blockID && bID3 == Block.glass.blockID) {
							pitOK = true;
						}
					}
					
					if (pitOK) {
						int bbID = TFCBlocks.StoneIgInBrick.blockID;
						Boolean gotY = true;
						do {
							ySize += 1;
							if (world.getBlockId(x, y + ySize, z) == 0) { gotY = false; }
						} while (gotY);
						ySize -= 1;
						
						Boolean gotX = true;
						do {
							xSize +=1;
							if (world.getBlockId(x - xSize, y, z) != bbID) { gotX = false; }
						} while (gotX);
						
						Boolean gotZ = true;
						do {
							zSize += 1;
							if (world.getBlockId(x, y, z + zSize) != bbID) { gotZ = false; }
						} while (gotZ);
						
						for (int yy = y + ySize; yy >= y; yy--) {
							for (int zz = z; zz <= (z + zSize); zz++) {
								for (int xx = x; xx >= (x - xSize); xx--) {
									world.setBlockWithNotify(xx, yy, zz, 0);
								}
							}
						}
						sender.sendChatToPlayer("Done");
					} else {
						sender.sendChatToPlayer("I do not know this Charcoal Pit!");
					}
					break;
				case 1:
					break;
				case 2:
					break;
				case 3:
					break;
			}
			
		}
	}

}
