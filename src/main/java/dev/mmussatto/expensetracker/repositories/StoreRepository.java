/*
 * Created by murilo.mussatto on 27/02/2023
 */

package dev.mmussatto.expensetracker.repositories;

import dev.mmussatto.expensetracker.domain.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository<T extends Store> extends JpaRepository<T, Integer> {
}
