package de.dafuqs.thefabricexperience.impl.storage;

import de.dafuqs.thefabricexperience.api.storage.*;
import de.dafuqs.thefabricexperience.api.storage.base.*;
import net.fabricmc.fabric.api.transfer.v1.transaction.*;
import org.jetbrains.annotations.*;

@ApiStatus.Internal
public class ExperienceImpl {
	
	static {
		ExperienceStorage.ITEM.registerFallback((stack, ctx) -> {
			if (stack.getItem() instanceof SimpleExperienceStorageItem experienceStorageItem) {
				return SimpleExperienceStorageItem.createStorage(ctx, experienceStorageItem.getExperienceCapacity(stack), experienceStorageItem.getMaxExperienceInput(stack), experienceStorageItem.getMaxExperienceOutput(stack));
			} else {
				return null;
			}
		});
	}

	public static final ExperienceStorage EMPTY = new ExperienceStorage() {
		@Override
		public boolean supportsInsertion() {
			return false;
		}

		@Override
		public long insert(long maxAmount, TransactionContext transaction) {
			return 0;
		}

		@Override
		public boolean supportsExtraction() {
			return false;
		}

		@Override
		public long extract(long maxAmount, TransactionContext transaction) {
			return 0;
		}

		@Override
		public long getAmount() {
			return 0;
		}

		@Override
		public long getCapacity() {
			return 0;
		}
	};
	
}