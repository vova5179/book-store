package mate.academy.booksstore.repository;

import java.util.Optional;
import mate.academy.booksstore.model.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {

    Optional<ShoppingCart> findByUserEmail(String email);
}
