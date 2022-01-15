package weather2.util;

import com.corosus.coroutil.util.CoroUtilCompatibility;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TallGrassBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import weather2.config.ConfigTornado;

import java.util.Calendar;

public class WeatherUtil {
	
    public static boolean isPaused() {
    	if (Minecraft.getInstance().isPaused()) return true;
    	return false;
    }
    
    public static boolean isPausedSideSafe(Level world) {
    	//return false if server side because it cant be paused legit
    	if (!world.isClientSide) return false;
    	return isPausedForClient();
    }
    
    public static boolean isPausedForClient() {
    	if (Minecraft.getInstance().isPaused()) return true;
    	return false;
    }

    public static boolean isAprilFoolsDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        //test
        //return calendar.get(Calendar.MONTH) == Calendar.MARCH && calendar.get(Calendar.DAY_OF_MONTH) == 25;

        return calendar.get(Calendar.MONTH) == Calendar.APRIL && calendar.get(Calendar.DAY_OF_MONTH) == 1;
    }

    public static boolean shouldRemoveBlock(BlockState blockID)
    {
        //water no
        if (blockID.getMaterial() == Material.WATER)
        {
            return false;
        }

        return true;
    }

    public static boolean isOceanBlock(Block blockID)
    {
        return false;
    }

    public static boolean isSolidBlock(Block id)
    {
        return (id == Blocks.STONE ||
                id == Blocks.COBBLESTONE ||
                id == Blocks.SANDSTONE);
    }

    public static boolean shouldGrabBlock(Level parWorld, BlockState state)
    {
        //TODO: 1.14 unbork tornado grabbing
        //return false;
        //TODO: 1.14 uncomment

        //TODO: block tags for logs, also im gonna go particles instead

        try
        {
            ItemStack itemStr = new ItemStack(Items.DIAMOND_AXE);

            Block block = state.getBlock();

            boolean result = true;

            if (ConfigTornado.Storm_Tornado_GrabCond_List)
            {
                try {

                    if (!ConfigTornado.Storm_Tornado_GrabListBlacklistMode)
                    {
                        if (!((Boolean)blockIDToUseMapping.get(block)).booleanValue()) {
                            result = false;
                        }
                    }
                    else
                    {
                        if (((Boolean)blockIDToUseMapping.get(block)).booleanValue()) {
                            result = false;
                        }
                    }
                } catch (Exception e) {
                    //sometimes NPEs, just assume false if so
                    result = false;
                }
            } else {

                if (ConfigTornado.Storm_Tornado_GrabCond_StrengthGrabbing)
                {
                    float strMin = 0.0F;
                    float strMax = 0.74F;

                    if (block == null)
                    {
                        result = false;
                        return result; //force return false to prevent unchecked future code outside scope
                    } else {

                        float strVsBlock = block.getBlockHardness(block.defaultBlockState(), parWorld, new BlockPos(0, 0, 0)) - (((itemStr.getStrVsBlock(block.defaultBlockState()) - 1) / 4F));

                        //System.out.println(strVsBlock);
                        if (/*block.getHardness() <= 10000.6*/ (strVsBlock <= strMax && strVsBlock >= strMin) ||
                                (state.getMaterial() == Material.WOOD) ||
                                state.getMaterial() == Material.WOOL ||
                                state.getMaterial() == Material.PLANT ||/*
                                state.getMaterial() == Material.VINE ||*/
                                block instanceof BlockTallGrass)
                        {
    	                    /*if (block.blockMaterial == Material.water) {
    	                    	return false;
    	                    }*/
                            if (!safetyCheck(block))
                            {
                                result = false;
                            }
                        } else {
                            result = false;
                        }


                    }
                }

                if (ConfigTornado.Storm_Tornado_RefinedGrabRules) {
                    if (block == Blocks.DIRT || block == Blocks.GRASS || block == Blocks.SAND || block instanceof BlockLog/* || block.blockMaterial == Material.wood*/) {
                        result = false;
                    }
                    if (!CoroUtilCompatibility.canTornadoGrabBlockRefinedRules(state)) {
                        result = false;
                    }
                }
            }

            if (block == CommonProxy.blockWeatherMachine) {
                result = false;
            }

            return result;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
    }

    public static boolean safetyCheck(BlockState state)
    {
        Block id = state.getBlock();
        if (id != Blocks.BEDROCK && id != Blocks.LOG && id != Blocks.CHEST && id != Blocks.JUKEBOX/* && id != Block.waterMoving.blockID && id != Block.waterStill.blockID */)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
}
