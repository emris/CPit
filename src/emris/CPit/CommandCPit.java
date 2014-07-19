/**
 *  Copyright (C) 2013  emris
 *  https://github.com/emris/CPit
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package emris.CPit;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import com.bioxx.tfc.TFCBlocks;
import com.bioxx.tfc.TFCItems;
import com.bioxx.tfc.TileEntities.TELogPile;

public class CommandCPit extends CommandBase
{
	@Override
	public String getCommandName()
	{
		return "cpit";
	}

	@Override
	public String getCommandUsage(ICommandSender sender)
	{
		return "/" + getCommandName() + " [x] [z] [y] or [del] (max: 25 25 13)";
	}

	@Override
	public List getCommandAliases()
	{
		return null;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args)
	{
		Boolean argDel = false;
		Boolean argOK = false;
		int xSize = 0;
		int zSize = 0;
		int ySize = 0;

		if (args.length == 1 )
		{
			if (args[0].equalsIgnoreCase("del"))
			{
				argDel = true;
			}
			else
			{
				xSize = checkArgs(args[0]);
				if (xSize > 0)
				{
					argOK = true;
					zSize = xSize;
					if (xSize > 13) { ySize = 13; } else { ySize = xSize; }
				}
			}
		}
		else if (args.length == 2)
		{
			xSize = checkArgs(args[0]);
			if (xSize > 0)
			{
				argOK = true;
				zSize = xSize;
				if (xSize > 13) { ySize = 13; } else { ySize = xSize; }
			}
			if (argOK)
			{
				argOK = false;
				zSize = checkArgs(args[1]);
				if (zSize > 0)
					argOK = true;
			}
		}
		else if (args.length == 3)
		{
			xSize = checkArgs(args[0]);
			if (xSize > 0)
			{
				argOK = true;
				zSize = xSize;
				if (xSize > 13) { ySize = 13; } else { ySize = xSize; }
			}

			if (argOK)
			{
				argOK = false;
				zSize = checkArgs(args[1]);
				if (zSize > 0)
					argOK = true;
			}

			if (argOK)
			{
				argOK = false;
				ySize = checkArgs(args[2]);
				if (ySize > 0 && ySize < 14)
					argOK = true;
			}
		}

		if (argOK)
			makePit(sender, xSize, zSize, ySize);
		else if (argDel)
			delPit(sender);
		else
			sender.addChatMessage(new ChatComponentText(getCommandUsage(sender)));
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender)
	{
		if (sender instanceof EntityPlayerMP)
			return true;
		else
			return false;
	}

	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		return null;
	}

	private void makePit(ICommandSender sender, int xSize, int zSize, int ySize)
	{
		EntityPlayerMP curPlayer = (EntityPlayerMP) sender;
		MovingObjectPosition mop = PlayerUtils.getTargetBlock(curPlayer);
		if (mop != null)
		{
			World world = curPlayer.worldObj;
			// For the Box around the wood
			xSize += 2;
			ySize += 1;
			zSize += 2;

			//Looking direction: 0 -> South, 1 -> West, 2 -> North, 3 -> East 
			int look = MathHelper.floor_double((double)(curPlayer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
			int x = mop.blockX;
			int y = mop.blockY + 1;
			int z = mop.blockZ;

			int xz = 0;
			switch(look)
			{
			case 1:
				z -= xSize - 1;
				xz = xSize;
				xSize = zSize;
				zSize = xz;
				break;
			case 2:
				z -= xSize - 1;
				x += zSize - 1;
				break;
			case 3:
				x += zSize - 1;
				xz = xSize;
				xSize = zSize;
				zSize = xz;
				break;
			}

			TELogPile te = null;
			for (int zz = z; zz < (z + zSize); zz++)
			{
				for (int xx = x; xx > (x - xSize); xx--)
				{
					world.setBlock(xx, y, zz, TFCBlocks.StoneIgInBrick, 1, 0x2);
				}
			}

			for (int yy = y + 1; yy <= (y + ySize); yy++)
			{
				for (int zz= z + 1; zz < (z + zSize - 1); zz++)
				{
					for (int xx = x - 1; xx > (x - xSize + 1); xx--)
					{
						if (yy == y + ySize)
						{
							world.setBlock(xx, yy, zz, Blocks.glass);
						}
						else
						{
							world.setBlock(xx, yy, zz, TFCBlocks.LogPile, 1, 0x2);
							if(world.isRemote)
								world.markBlockForUpdate(xx, yy, zz);
							te = (TELogPile)world.getTileEntity(xx, yy, zz);
							if (te != null)
							{
								te.storage[0] = new ItemStack(TFCItems.Logs, 4, 1);
								te.storage[1] = new ItemStack(TFCItems.Logs, 4, 1);
								te.storage[2] = new ItemStack(TFCItems.Logs, 4, 1);
								te.storage[3] = new ItemStack(TFCItems.Logs, 4, 1);
							}
						}
					}
				}

				world.setBlock(x, yy, z, TFCBlocks.StoneIgInBrick, 1, 0x2);
				world.setBlock(x, yy, z + zSize - 1, TFCBlocks.StoneIgInBrick, 1, 0x2);
				world.setBlock(x - xSize + 1, yy, z, TFCBlocks.StoneIgInBrick, 1, 0x2);
				world.setBlock(x - xSize + 1, yy, z + zSize - 1, TFCBlocks.StoneIgInBrick, 1, 0x2);
				for (int xx = x - 1; xx > (x - xSize + 1); xx--)
				{
					if (yy == y + ySize)
					{
						world.setBlock(xx, yy, z, TFCBlocks.StoneIgInBrick, 1, 0x2);
						world.setBlock(xx, yy, z + zSize - 1, TFCBlocks.StoneIgInBrick, 1, 0x2);
					}
					else
					{
						world.setBlock(xx, yy, z, Blocks.glass);
						world.setBlock(xx, yy, z + zSize - 1, Blocks.glass);
					}
				}
				for (int zz = z + 1; zz < (z + zSize - 1); zz++)
				{
					if (yy ==  y + ySize)
					{
						world.setBlock(x, yy, zz, TFCBlocks.StoneIgInBrick, 1, 0x2);
						world.setBlock(x - xSize + 1, yy, zz, TFCBlocks.StoneIgInBrick, 1, 0x2);
					}
					else
					{
						world.setBlock(x, yy, zz, Blocks.glass);
						world.setBlock(x - xSize + 1, yy, zz, Blocks.glass);
					}
				}
			}
		}
	}

	private void delPit(ICommandSender sender)
	{
		EntityPlayerMP curPlayer = (EntityPlayerMP) sender;
		MovingObjectPosition mop = PlayerUtils.getTargetBlock(curPlayer);
		if (mop != null)
		{
			World world = curPlayer.worldObj;
			//Looking direction: 0 -> South, 1 -> West, 2 -> North, 3 -> East 
			int look = MathHelper.floor_double((double)(curPlayer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

			int x = mop.blockX;
			int y = mop.blockY;
			int z = mop.blockZ;

			Block bID = world.getBlock(x, y, z);
			Block pbID = TFCBlocks.StoneIgInBrick;
			int meta = world.getBlockMetadata(x, y, z);
			Boolean pitOK = false;

			if (bID == pbID && meta == 1)
			{
				Boolean gotXY;
				int sizeXY=0;
				switch(look)
				{
				case 1:
					gotXY = true;
					do {
						if (world.getBlock(x, y, z) == pbID) { z -= 1; } else { gotXY = false; }
					} while (gotXY);
					z += 1;
					break;
				case 2:
					gotXY = true;
					do {
						if (world.getBlock(x, y, z) == pbID) { x += 1; } else { gotXY = false; }
					} while (gotXY);
					x -= 1;

					gotXY = true;
					do {
						if (world.getBlock(x, y, z) == pbID) { z -= 1; } else { gotXY = false; }
					} while (gotXY);
					z += 1;
					break;
				case 3:
					gotXY = true;
					do {
						if (world.getBlock(x, y, z) == pbID) { x += 1; } else { gotXY = false; }
					} while (gotXY);
					x -= 1;
					break;
				}
				Block bID2 = world.getBlock(x - 1, y + 1, z);
				Block bID3 = world.getBlock(x, y + 1, z + 1);
				if (bID2 == Blocks.glass && bID3 == Blocks.glass)
					pitOK = true;
			}

			if (pitOK)
			{
				int xSize = 0;
				int zSize = 0;
				int ySize = 0;
				Boolean gotY = true;
				do {
					if (world.isAirBlock(x, y + ySize, z)) { gotY = false; }
					ySize += 1;
				} while (gotY);

				Boolean gotX = true;
				do {
					xSize +=1;
					if (world.getBlock(x - xSize, y, z) != pbID) { gotX = false; }
				} while (gotX);

				Boolean gotZ = true;
				do {
					zSize += 1;
					if (world.getBlock(x, y, z + zSize) != pbID) { gotZ = false; }
				} while (gotZ);

				TELogPile telp = null;
				TileEntity te = null;
				for (int yy = y + ySize; yy >= y; yy--)
				{
					for (int zz = z; zz <= (z + zSize); zz++)
					{
						for (int xx = x; xx >= (x - xSize); xx--)
						{
							te = world.getTileEntity(xx, yy, zz);
							if (te != null && te instanceof TELogPile)
							{
								telp = (TELogPile) te;
								telp.storage[0] = null;
								telp.storage[1] = null;
								telp.storage[2] = null;
								telp.storage[3] = null;
							}
							world.setBlockToAir(xx, yy, zz);
						}
					}
				}
				sender.addChatMessage(new ChatComponentText("Done"));
			}
			else
			{
				sender.addChatMessage(new ChatComponentText("NOT my Charcoal Pit!"));
			}
		}
	}

	private int checkArgs(String s)
	{
		int ret = 0;
		for (int i=2; i < 26; i++)
		{
			if (s.equalsIgnoreCase("" + i))
				ret = Integer.parseInt(s);
		}
		return ret;
	}

}
