package com.pahimar.ee3.tileentity;

import com.pahimar.ee3.helper.ItemHelper;
import com.pahimar.ee3.lib.Strings;
import com.pahimar.ee3.network.PacketTypeHandler;
import com.pahimar.ee3.network.packet.PacketTileWithItemUpdate;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.packet.Packet;

/**
 * Equivalent-Exchange-3
 * <p/>
 * TileAludel
 *
 * @author pahimar
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
public class TileAludel extends TileEE implements IInventory
{

    /**
     * The ItemStacks that hold the items currently being used in the Aludel
     */
    private ItemStack[] inventory;

    public static final int INVENTORY_SIZE = 4;

    public static final int FUEL_INVENTORY_INDEX = 0;
    public static final int INPUT_INVENTORY_INDEX = 1;
    public static final int DUST_INVENTORY_INDEX = 2;
    public static final int OUTPUT_INVENTORY_INDEX = 3;

    public TileAludel()
    {

        inventory = new ItemStack[INVENTORY_SIZE];
    }

    @Override
    public int getSizeInventory()
    {

        return inventory.length;
    }

    @Override
    public ItemStack getStackInSlot(int slotIndex)
    {

        return inventory[slotIndex];
    }

    @Override
    public ItemStack decrStackSize(int slotIndex, int decrementAmount)
    {

        ItemStack itemStack = getStackInSlot(slotIndex);
        if (itemStack != null)
        {
            if (itemStack.stackSize <= decrementAmount)
            {
                setInventorySlotContents(slotIndex, null);
            }
            else
            {
                itemStack = itemStack.splitStack(decrementAmount);
                if (itemStack.stackSize == 0)
                {
                    setInventorySlotContents(slotIndex, null);
                }
            }
        }

        return itemStack;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slotIndex)
    {

        ItemStack itemStack = getStackInSlot(slotIndex);
        if (itemStack != null)
        {
            setInventorySlotContents(slotIndex, null);
        }
        return itemStack;
    }

    @Override
    public void setInventorySlotContents(int slotIndex, ItemStack itemStack)
    {

        inventory[slotIndex] = itemStack;
        if (itemStack != null && itemStack.stackSize > getInventoryStackLimit())
        {
            itemStack.stackSize = getInventoryStackLimit();
        }
    }

    @Override
    public String getInvName()
    {

        return this.hasCustomName() ? this.getCustomName() : Strings.CONTAINER_ALUDEL_NAME;
    }

    @Override
    public int getInventoryStackLimit()
    {

        return 64;
    }

    @Override
    public void openChest()
    {

    }

    @Override
    public void closeChest()
    {

    }

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound)
    {

        super.readFromNBT(nbtTagCompound);

        // Read in the ItemStacks in the inventory from NBT
        NBTTagList tagList = nbtTagCompound.getTagList("Items");
        inventory = new ItemStack[this.getSizeInventory()];
        for (int i = 0; i < tagList.tagCount(); ++i)
        {
            NBTTagCompound tagCompound = (NBTTagCompound) tagList.tagAt(i);
            byte slotIndex = tagCompound.getByte("Slot");
            if (slotIndex >= 0 && slotIndex < inventory.length)
            {
                inventory[slotIndex] = ItemStack.loadItemStackFromNBT(tagCompound);
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound)
    {

        super.writeToNBT(nbtTagCompound);

        // Write the ItemStacks in the inventory to NBT
        NBTTagList tagList = new NBTTagList();
        for (int currentIndex = 0; currentIndex < inventory.length; ++currentIndex)
        {
            if (inventory[currentIndex] != null)
            {
                NBTTagCompound tagCompound = new NBTTagCompound();
                tagCompound.setByte("Slot", (byte) currentIndex);
                inventory[currentIndex].writeToNBT(tagCompound);
                tagList.appendTag(tagCompound);
            }
        }
        nbtTagCompound.setTag("Items", tagList);
    }

    @Override
    public boolean isInvNameLocalized()
    {

        return this.hasCustomName();
    }

    @Override
    public boolean isItemValidForSlot(int slotIndex, ItemStack itemStack)
    {

        return true;
    }

    @Override
    public Packet getDescriptionPacket()
    {

        ItemStack itemStack = getStackInSlot(INPUT_INVENTORY_INDEX);

        if (itemStack != null && itemStack.stackSize > 0)
        {
            return PacketTypeHandler.populatePacket(new PacketTileWithItemUpdate(xCoord, yCoord, zCoord, orientation, state, customName, itemStack.itemID, itemStack.getItemDamage(), itemStack.stackSize, ItemHelper.getColor(itemStack)));
        }
        else
        {
            return super.getDescriptionPacket();
        }
    }

    @Override
    public void onInventoryChanged()
    {

        worldObj.updateAllLightTypes(xCoord, yCoord, zCoord);

        if (worldObj.getBlockTileEntity(xCoord, yCoord + 1, zCoord) instanceof TileGlassBell)
        {
            worldObj.updateAllLightTypes(xCoord, yCoord + 1, zCoord);
        }
    }

    @Override
    public String toString()
    {

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(super.toString());

        stringBuilder.append("TileAludel Data - ");
        for (int i = 0; i < inventory.length; i++)
        {
            if (i != 0)
            {
                stringBuilder.append(", ");
            }

            if (inventory[i] != null)
            {
                stringBuilder.append(String.format("inventory[%d]: %s", i, inventory[i].toString()));
            }
            else
            {
                stringBuilder.append(String.format("inventory[%d]: empty", i));
            }
        }
        stringBuilder.append("\n");

        return stringBuilder.toString();
    }
}
