package org.hopto.nexoff.race.service;


import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hopto.nexoff.race.domain.Authority;
import org.hopto.nexoff.race.domain.Bid;
import org.hopto.nexoff.race.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service("userService")
@Repository
public class UserServiceImpl implements UserService {

	@PersistenceContext
	private EntityManager em;
	
	@Autowired
	private AuthorityService authorityService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	@Transactional(readOnly = true)
	public List<User> findAll() {
		List<User> users = em.createNamedQuery("User.findAll", User.class)
				.getResultList();
		return users;
	}

	@Override
	@Transactional(readOnly = true)
	public User findById(Long id) {
		User user = em.find(User.class, id);
		user.setPassword(null);
		return user;
	}

	@Override
	public User update(User user) {
		User refreshUser = em.find(User.class, user.getId());
		refreshUser.setEmail(user.getEmail());
		refreshUser.setFio(user.getFio());
		if(user.getPassword() != "") {
			refreshUser.setPassword(passwordEncoder.encodePassword(user.getPassword(), refreshUser.getUsername()));
		}
		return user;
	}
	
	@Override
	public User create(User user) {
		List<Authority> authorities = new ArrayList<Authority>();
		authorities.add(authorityService.findByAuthority("ROLE_USER"));
		user.setPassword(passwordEncoder.encodePassword(user.getPassword(), user.getUsername()));
		user.setAuthorities(authorities);
		user.setMoney(100);
		em.persist(user);
		return user;
	}
	

	@Override
	public void delete(User user) {
		User mergeUser = em.merge(user);
		em.remove(mergeUser);

	}

	@Override
	@Transactional(readOnly=true)
	public User loadUserByUsername(String username){
		User user = em.createNamedQuery("User.findByUsernameWithAuthrities", User.class).setParameter("username", username).getSingleResult();
		return user;
	}
	
	@Override
	@Transactional(readOnly=true)
	public Boolean isUniqueUsername(User user) {
		Long l = (Long) em.createNamedQuery("User.isUniqueUsername").setParameter("username", user.getUsername()).getSingleResult();
		return (l > 0) ? false : true;
	}

	@Override
	@Transactional(propagation=Propagation.MANDATORY)
	public void debit(User user, Double money) {
		User refreshUser = em.find(User.class, user.getId());
		refreshUser.setMoney(refreshUser.getMoney() - money);
	}
	
	@Override
	@Transactional(propagation=Propagation.MANDATORY)
	public void credit(User user, Bid bid) {
		user.setMoney(user.getMoney() + bid.getAmount() * bid.getRace().getCoeff());
	}
	
	
}
