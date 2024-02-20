package de.dafuqs.thefabricexperience.api.storage.base;

import de.dafuqs.thefabricexperience.api.storage.*;
import net.fabricmc.fabric.api.transfer.v1.storage.*;
import net.fabricmc.fabric.api.transfer.v1.transaction.*;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.function.*;

/**
 * An experience storage that delegates to another experience storage,
 * with an optional boolean supplier to check that the storage is still valid.
 * This can be used for easier item experience storage implementation, or overridden for custom delegation logic.
 */
@SuppressWarnings({"deprecation", "UnstableApiUsage"})
public class DelegatingExperienceStorage implements ExperienceStorage {
	protected final Supplier<ExperienceStorage> backingStorage;
	protected final BooleanSupplier validPredicate;

	/**
	 * Create a new instance.
	 * @param backingStorage Storage to delegate to.
	 * @param validPredicate A function that can return false to prevent any operation, or true to call the delegate as usual.
	 *                       {@code null} can be passed if no filtering is necessary.
	 */
	public DelegatingExperienceStorage(ExperienceStorage backingStorage, @Nullable BooleanSupplier validPredicate) {
		this(() -> backingStorage, validPredicate);
		Objects.requireNonNull(backingStorage);
	}

	/**
	 * More general constructor that allows the backing storage to change over time.
	 */
	public DelegatingExperienceStorage(Supplier<ExperienceStorage> backingStorage, @Nullable BooleanSupplier validPredicate) {
		this.backingStorage = Objects.requireNonNull(backingStorage);
		this.validPredicate = validPredicate == null ? () -> true : validPredicate;
	}

	@Override
	public boolean supportsInsertion() {
		return validPredicate.getAsBoolean() && backingStorage.get().supportsInsertion();
	}

	@Override
	public long insert(long maxAmount, TransactionContext transaction) {
		StoragePreconditions.notNegative(maxAmount);

		if (validPredicate.getAsBoolean()) {
			return backingStorage.get().insert(maxAmount, transaction);
		} else {
			return 0;
		}
	}

	@Override
	public boolean supportsExtraction() {
		return validPredicate.getAsBoolean() && backingStorage.get().supportsExtraction();
	}

	@Override
	public long extract(long maxAmount, TransactionContext transaction) {
		StoragePreconditions.notNegative(maxAmount);

		if (validPredicate.getAsBoolean()) {
			return backingStorage.get().extract(maxAmount, transaction);
		} else {
			return 0;
		}
	}

	@Override
	public long getAmount() {
		if (validPredicate.getAsBoolean()) {
			return backingStorage.get().getAmount();
		} else {
			return 0;
		}
	}

	@Override
	public long getCapacity() {
		if (validPredicate.getAsBoolean()) {
			return backingStorage.get().getCapacity();
		} else {
			return 0;
		}
	}
}