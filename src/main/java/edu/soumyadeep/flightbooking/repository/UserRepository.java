package edu.soumyadeep.flightbooking.repository;


import edu.soumyadeep.flightbooking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {}
