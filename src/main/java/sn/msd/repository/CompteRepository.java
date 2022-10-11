package sn.msd.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import sn.msd.model.Compte;

public interface CompteRepository extends JpaRepository<Compte, Long>{

	Compte findByLogin(String login);
	
	Compte findByLoginAndActiveToken(String login , String activeToken);
}
