package emris.mods.CPit;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

public class PlayerUtils {
	private EntityPlayer thePlayer;
	
	public static MovingObjectPosition getTargetBlock(EntityPlayer P) {
		float v1 = 1.0F;
		double v2 = P.prevPosX + (P.posX - P.prevPosX) * v1;
		double v3 = P.prevPosY + (P.posY - P.prevPosY) * v1 + 1.62D - P.yOffset;
		double v4 = P.prevPosZ + (P.posZ - P.prevPosZ) * v1;
		Vec3 v5 = Vec3.vec3dPool.getVecFromPool(v2, v3, v4);
		
		float v6 = P.prevRotationYaw + (P.rotationYaw - P.prevRotationYaw) * v1;
		float v7 = P.prevRotationPitch + (P.rotationPitch - P.prevRotationPitch) * v1;
		
		float v8 = MathHelper.cos(-v6 * 0.017453292F - (float)Math.PI);
		float v9 = MathHelper.sin(-v6 * 0.017453292F - (float)Math.PI);
		float v10 = -MathHelper.cos(-v7 * 0.017453292F);
		float v11 = MathHelper.sin(-v7 * 0.017453292F);
		float v12 = v8 * v10;
		float v13 = v9 * v10;
		double v14 = 5.0D; 
		Vec3 v15 = v5.addVector(v13 * v14, v11 * v14, v12 * v14);
		
//        P.sendChatToPlayer("<x" + v2 + ", y" + v3 + ", z" + v4 + ">");
        
        return P.worldObj.rayTraceBlocks_do(v5, v15, true);
	}
}
