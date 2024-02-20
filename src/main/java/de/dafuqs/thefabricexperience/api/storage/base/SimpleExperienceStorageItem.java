package de.dafuqs.thefabricexperience.api.storage.base;

import de.dafuqs.thefabricexperience.api.storage.*;
import de.dafuqs.thefabricexperience.impl.storage.*;
import net.fabricmc.fabric.api.transfer.v1.context.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import org.jetbrains.annotations.*;

/**
 * Simple experience containing item. If this is implemented on an item:
 * <ul>
 *     <li>The experience will directly be stored in the NBT.</li>
 *     <li>Helper functions in this class to work with the stored experience can be used.</li>
 *     <li>An ExperienceStorage will automatically be provided for queries through {@link ExperienceStorage#ITEM}.</li>
 * </ul>
 */
public interface SimpleExperienceStorageItem {
	
	String STORED_XP_KEY = "stored_xp";

	/**
	 * Return a base experience storage implementation for items, with fixed capacity, and per-operation insertion and extraction limits.
	 * This is used internally for items that implement SimpleExperienceItem, but it may also be used outside of that.
	 * The experience is stored in the {@code stored_xp} tag of the stacks, the same as the constant {@link #STORED_XP_KEY}.
	 *
	 * <p>Stackable experience containers are supported just fine, and they will distribute experience evenly.
	 * For example, insertion of 3 units of experience into a stack of 2 items using this class will either insert 0 or 2 depending on the remaining capacity.
	 */
	static ExperienceStorage createStorage(ContainerItemContext ctx, long capacity, long maxInsert, long maxExtract) {
		return SimpleItemExperienceStorageImpl.createSimpleStorage(ctx, capacity, maxInsert, maxExtract);
	}

	/**
	 * @param stack Current stack.
	 * @return The max experience that can be stored in this item stack (ignoring current stack size).
	 */
	long getExperienceCapacity(ItemStack stack);

	/**
	 * @param stack Current stack.
	 * @return The max amount of experience that can be inserted in this item stack (ignoring current stack size) in a single operation.
	 */
	long getMaxExperienceInput(ItemStack stack);

	/**
	 * @param stack Current stack.
	 * @return The max amount of experience that can be extracted from this item stack (ignoring current stack size) in a single operation.
	 */
	long getMaxExperienceOutput(ItemStack stack);

	/**
	 * @return The experience stored in the stack. Count is ignored.
	 */
	default long getStoredExperience(ItemStack stack) {
		return getStoredExperienceUnchecked(stack);
	}

	/**
	 * Directly set the experience stored in the stack. Count is ignored.
	 * It's up to callers to ensure that the new amount is >= 0 and <= capacity.
	 */
	default void setStoredExperience(ItemStack stack, long newAmount) {
		setStoredExperienceUnchecked(stack, newAmount);
	}

	/**
	 * Try to use exactly {@code amount} experience if there is enough available and return true if successful,
	 * otherwise do nothing and return false.
	 * @throws IllegalArgumentException If the count of the stack is not exactly 1!
	 */
	default boolean tryUseExperience(ItemStack stack, long amount) {
		if (stack.getCount() != 1) {
			throw new IllegalArgumentException("Invalid count: " + stack.getCount());
		}

		long newAmount = getStoredExperience(stack) - amount;

		if (newAmount < 0) {
			return false;
		} else {
			setStoredExperience(stack, newAmount);
			return true;
		}
	}

	/**
	 * @return The currently stored experience, ignoring the count and without checking the current item.
	 */
	static long getStoredExperienceUnchecked(ItemStack stack) {
		return getStoredExperienceUnchecked(stack.getNbt());
	}

	/**
	 * @return The currently stored experience of this raw tag.
	 */
	static long getStoredExperienceUnchecked(@Nullable NbtCompound nbt) {
		return nbt != null ? nbt.getLong(STORED_XP_KEY) : 0;
	}

	/**
	 * Set the experience, ignoring the count and without checking the current item.
	 */
	static void setStoredExperienceUnchecked(ItemStack stack, long newAmount) {
		if (newAmount == 0) {
			// Make sure newly crafted experience containers stack with emptied ones.
			stack.removeSubNbt(STORED_XP_KEY);
		} else {
			stack.getOrCreateNbt().putLong(STORED_XP_KEY, newAmount);
		}
	}
}