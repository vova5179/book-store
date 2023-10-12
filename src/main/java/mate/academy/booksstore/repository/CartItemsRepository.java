package mate.academy.booksstore.repository;

import mate.academy.booksstore.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemsRepository extends JpaRepository<CartItem, Long> {
}
