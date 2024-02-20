package de.dafuqs.thefabricexperience.impl.storage;

import de.dafuqs.thefabricexperience.api.storage.*;
import de.dafuqs.thefabricexperience.api.storage.base.*;
import net.fabricmc.fabric.api.transfer.v1.context.*;
import net.fabricmc.fabric.api.transfer.v1.item.*;
import net.fabricmc.fabric.api.transfer.v1.storage.*;
import net.fabricmc.fabric.api.transfer.v1.transaction.*;
import net.minecraft.item.*;
import org.jetbrains.annotations.*;

/**
 * Note: instances of this class do not perform any context validation,
 * that is handled by the DelegatingExperienceStorage they are wrapped behind.
 */
@ApiStatus.Internal
@SuppressWarnings({"UnstableApiUsage"})
public class SimpleItemExperienceStorageImpl implements ExperienceStorage {
	
	public static ExperienceStorage createSimpleStorage(ContainerItemContext ctx, long capacity, long maxInsert, long maxExtract) {
		StoragePreconditions.notNegative(capacity);
		StoragePreconditions.notNegative(maxInsert);
		StoragePreconditions.notNegative(maxExtract);

		Item startingItem = ctx.getItemVariant().getItem();

		return new DelegatingExperienceStorage(
				new SimpleItemExperienceStorageImpl(ctx, capacity, maxInsert, maxExtract),
				() -> ctx.getItemVariant().isOf(startingItem) && ctx.getAmount() > 0
		);
	}

	private final ContainerItemContext ctx;
	private final long capacity;
	private final long maxInsert, maxExtract;

	private SimpleItemExperienceStorageImpl(ContainerItemContext ctx, long capacity, long maxInsert, long maxExtract) {
		this.ctx = ctx;
		this.capacity = capacity;
		this.maxInsert = maxInsert;
		this.maxExtract = maxExtract;
	}

	/**
	 * Try to set the experience of the stack to {@code experienceAmountPerCount}, return true if success.
	 */
	private boolean trySetExperience(long experienceAmountPerCount, long count, TransactionContext transaction) {
		ItemStack newStack = ctx.getItemVariant().toStack();
		SimpleExperienceStorageItem.setStoredExperienceUnchecked(newStack, experienceAmountPerCount);
		ItemVariant newVariant = ItemVariant.of(newStack);

		// Try to convert exactly `count` items.
		try (Transaction nested = transaction.openNested()) {
			if (ctx.extract(ctx.getItemVariant(), count, nested) == count && ctx.insert(newVariant, count, nested) == count) {
				nested.commit();
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean supportsInsertion() {
		return maxInsert > 0;
	}

	@Override
	public long insert(long maxAmount, TransactionContext transaction) {
		long count = ctx.getAmount();

		long maxAmountPerCount = maxAmount / count;
		long currentAmountPerCount = getAmount() / count;
		long insertedPerCount = Math.min(maxInsert, Math.min(maxAmountPerCount, capacity - currentAmountPerCount));

		if (insertedPerCount > 0) {
			if (trySetExperience(currentAmountPerCount + insertedPerCount, count, transaction)) {
				return insertedPerCount * count;
			}
		}

		return 0;
	}

	@Override
	public boolean supportsExtraction() {
		return maxExtract > 0;
	}

	@Override
	public long extract(long maxAmount, TransactionContext transaction) {
		long count = ctx.getAmount();

		long maxAmountPerCount = maxAmount / count;
		long currentAmountPerCount = getAmount() / count;
		long extractedPerCount = Math.min(maxExtract, Math.min(maxAmountPerCount, currentAmountPerCount));

		if (extractedPerCount > 0) {
			if (trySetExperience(currentAmountPerCount - extractedPerCount, count, transaction)) {
				return extractedPerCount * count;
			}
		}

		return 0;
	}

	@Override
	public long getAmount() {
		return ctx.getAmount() * SimpleExperienceStorageItem.getStoredExperienceUnchecked(ctx.getItemVariant().getNbt());
	}

	@Override
	public long getCapacity() {
		return ctx.getAmount() * capacity;
	}
	
}