/*
 * Copyright 2012-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.system;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.transaction.ReactiveTransaction;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import reactor.core.publisher.Mono;

@TestConfiguration
public class TestTransactionConfig {

	public Integer getCount() {
		return count;
	}

	private Integer count = 0;

	@Bean
	public ReactiveTransactionManager reactiveTransactionManager() {
		return new TestTransactionManager();
	}

	public class TestTransactionManager implements ReactiveTransactionManager {

		@Override
		public @NotNull Mono<ReactiveTransaction> getReactiveTransaction(TransactionDefinition definition) {
			count++;
			return Mono.just(new TestTransactionManager.DummyReactiveTransaction());
		}

		@Override
		public @NotNull Mono<Void> commit(@NotNull ReactiveTransaction transaction) {
			return Mono.empty();
		}

		@Override
		public @NotNull Mono<Void> rollback(@NotNull ReactiveTransaction transaction) {
			return Mono.empty();
		}

		private static class DummyReactiveTransaction implements ReactiveTransaction {

			@Override
			public void setRollbackOnly() {
			}

		}

	}

}
