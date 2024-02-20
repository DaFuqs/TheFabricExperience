package de.dafuqs.thefabricexperience.api.storage;

import de.dafuqs.thefabricexperience.*;
import de.dafuqs.thefabricexperience.impl.storage.*;
import net.fabricmc.fabric.api.lookup.v1.block.*;
import net.fabricmc.fabric.api.lookup.v1.item.*;
import net.fabricmc.fabric.api.transfer.v1.context.*;
import net.fabricmc.fabric.api.transfer.v1.transaction.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import org.jetbrains.annotations.*;

/**
 * An object that can store experience.
 *
 * <p><ul>
 *     <li>{@link #supportsInsertion} and {@link #supportsExtraction} can be used to tell if insertion and extraction
 *     functionality are possibly supported by this storage.</li>
 *     <li>{@link #insert} and {@link #extract} can be used to insert or extract resources from this storage.</li>
 *     <li>{@link #getAmount} and {@link #getCapacity} can be used to query the current amount and capacity of this storage.
 *     There is no guarantee that the current amount of experience can be extracted,
 *     nor that something can be inserted if capacity > amount.
 *     If you want to know, you can simulate the operation with {@link #insert} and {@link #extract}.
 *     </li>
 * </ul>
 *
 * @see Transaction
 */
@SuppressWarnings({"unused", "UnstableApiUsage"})
public interface ExperienceStorage {
	/**
	 * Sided block access to experience storages.
	 * The {@code Direction} parameter may be null, meaning that the full storage (ignoring side restrictions) should be queried.
	 * Refer to {@link BlockApiLookup} for documentation on how to use this field.
	 *
	 * <p>The system is push based. That means that experience sources are responsible for pushing experience to nearby machines.
	 * Machines and wires should NOT pull experience from other sources.
	 *
	 * <p>{@link de.dafuqs.thefabricexperience.api.storage.base.SimpleExperienceStorage} and {@link de.dafuqs.thefabricexperience.api.storage.base.SimpleSidedExperienceContainer} are provided as base implementations.
	 *
	 * <p>When the operations supported by an experience storage change,
	 * that is if the return value of {@link ExperienceStorage#supportsInsertion} or {@link ExperienceStorage#supportsExtraction} changes,
	 * the storage should notify its neighbors with a block update so that they can refresh their connections if necessary.
	 *
	 * <p>This may be queried safely both on the logical server and on the logical client threads.
	 * On the server thread (i.e. with a server world), all transfer functionality is always supported.
	 * On the client thread (i.e. with a client world), contents of queried ExperienceStorages are unreliable and should not be modified.
	 */
	BlockApiLookup<ExperienceStorage, @Nullable Direction> SIDED = BlockApiLookup.get(new Identifier(TheFabricExperience.MOD_ID, "sided_experience"), ExperienceStorage.class, Direction.class);

	/**
	 * Item access to experience storages.
	 * Querying should always happen through {@link ContainerItemContext#find}.
	 *
	 * <p>{@link SimpleItemExperienceStorageImpl} is provided as an implementation example.
	 * Instances of it can be optained through {@link de.dafuqs.thefabricexperience.api.storage.base.SimpleExperienceStorageItem#createStorage}.
	 * Custom implementations should treat the context as a wrapper around a single slot,
	 * and always check the current item variant and amount before any operation, like {@code SimpleItemExperienceStorageImpl} does it.
	 * The check can be handled by {@link de.dafuqs.thefabricexperience.api.storage.base.DelegatingExperienceStorage}.
	 *
	 * <p>This may be queried both client-side and server-side.
	 * Returned APIs should behave the same regardless of the logical side.
	 */
	ItemApiLookup<ExperienceStorage, ContainerItemContext> ITEM = ItemApiLookup.get(new Identifier(TheFabricExperience.MOD_ID, "sided_experience"), ExperienceStorage.class, ContainerItemContext.class);

	/**
	 * Always empty experience storage.
	 */
	ExperienceStorage EMPTY = ExperienceImpl.EMPTY;

	/**
	 * Return false if calling {@link #insert} will absolutely always return 0, or true otherwise or in doubt.
	 *
	 * <p>Note: This function is meant to be used by cables or other devices that can transfer experience to know if
	 * they should interact with this storage at all.
	 */
	default boolean supportsInsertion() {
		return true;
	}

	/**
	 * Try to insert up to some amount of experience into this storage.
	 *
	 * @param maxAmount The maximum amount of experience to insert. May not be negative.
	 * @param transaction The transaction this operation is part of.
	 * @return A nonnegative integer not greater than maxAmount: the amount that was inserted.
	 */
	long insert(long maxAmount, TransactionContext transaction);

	/**
	 * Return false if calling {@link #extract} will absolutely always return 0, or true otherwise or in doubt.
	 *
	 * <p>Note: This function is meant to be used by cables or other devices that can transfer experience to know if
	 * they should interact with this storage at all.
	 */
	default boolean supportsExtraction() {
		return true;
	}

	/**
	 * Try to extract up to some amount of experience from this storage.
	 *
	 * @param maxAmount The maximum amount of experience to extract. May not be negative.
	 * @param transaction The transaction this operation is part of.
	 * @return A nonnegative integer not greater than maxAmount: the amount that was extracted.
	 */
	long extract(long maxAmount, TransactionContext transaction);

	/**
	 * Return the current amount of experience that is stored.
	 */
	long getAmount();

	/**
	 * Return the maximum amount of experience that could be stored.
	 */
	long getCapacity();
}