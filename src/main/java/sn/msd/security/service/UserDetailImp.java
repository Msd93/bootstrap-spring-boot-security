package sn.msd.security.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;import sn.msd.model.Compte;
import sn.msd.model.Privilege;
import sn.msd.model.Role;

public class UserDetailImp implements UserDetails {

	private static final long serialVersionUID = 1L;

	private Long id;

	private String username;

	private String email;

	private String password;

	private Collection<? extends GrantedAuthority> authorities;

	public UserDetailImp(Long id, String username, String email, String password,
			Collection<? extends GrantedAuthority> authorities) {
		this.id = id;
		this.username = username;
		this.email = email;
		this.password = password;
		this.authorities = authorities;
	}

	public static UserDetailImp build(Compte compte) {
		Collection<Role> roles = compte.getRoles();
		List<Privilege> privileges = new ArrayList<Privilege>();

		for (Role role : roles) {
			privileges.addAll(role.getPrivileges());
		}

		List<GrantedAuthority> authorities = privileges.stream()
				.map(privilege -> new SimpleGrantedAuthority(privilege.getNom())).collect(Collectors.toList());

		return new UserDetailImp(compte.getId(), compte.getLogin(), compte.getUser().getEmail(), compte.getPwd(), authorities);
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	public Long getId() {
		return id;
	}

	public String getEmail() {
		return email;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		UserDetailImp user = (UserDetailImp) o;
		return Objects.equals(id, user.id);
	}

}
