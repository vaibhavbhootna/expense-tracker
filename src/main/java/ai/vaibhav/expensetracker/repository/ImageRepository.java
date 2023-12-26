package ai.vaibhav.expensetracker.repository;

import ai.vaibhav.expensetracker.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

}
