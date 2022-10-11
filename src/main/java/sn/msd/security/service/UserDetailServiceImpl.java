package sn.msd.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import sn.msd.model.Compte;
import sn.msd.repository.CompteRepository;

public class UserDetailServiceImpl implements UserDetailsService {
	@Autowired
	private CompteRepository compteRepository;

	
	public UserDetails loadUserByUsername(String username) {
		Compte compte;
		try {

			compte = compteRepository.findByLogin(username);
			/** 
			 * A décommenter si une fois le Guard côté front end est à jour pour 
			 * permettre aux utilisateurs de se déconnecter automatiquement si toute fois le compte est désactivé
			 **/
			if(!compte.isEnabled()) {
				new UsernameNotFoundException("Compte non actif");
				return null;
			}
			return UserDetailImp.build(compte);
		} catch (UsernameNotFoundException e) {
			new UsernameNotFoundException("Identifiant incorrect" + e);
		}

		return null;
	}

	public UserDetails loadUserByUsernameAndActiveToken(String username, String token) {
		Compte compte;
		try {

			compte = compteRepository.findByLoginAndActiveToken(username,token.trim());
			/** 
			 * A décommenter si une fois le Guard côté front end est à jour pour 
			 * permettre aux utilisateurs de se déconnecter automatiquement si toute fois le compte est désactivé
			 **/
			if(!compte.isEnabled()) {
				new UsernameNotFoundException("Compte non actif");
				return null;
			}
			return UserDetailImp.build(compte);
		} catch (UsernameNotFoundException e) {
			new UsernameNotFoundException("Identifiant incorrect" + e);
		}

		return null;
	}

}
