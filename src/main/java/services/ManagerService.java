
package services;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import repositories.ManagerRepository;
import security.Authority;
import security.LoginService;
import security.UserAccount;
import security.UserAccountService;
import domain.Actor;
import domain.Manager;
import forms.RegisterManagerForm;

@Service
@Transactional
public class ManagerService {

	// Managed Repository ------------------------
	@Autowired
	private ManagerRepository	managerRepository;

	// Suporting services ------------------------

	@Autowired
	private ActorService		actorService;

	@Autowired
	private UserAccountService	userAccountService;

	@Autowired
	private Validator			validator;


	// Methods -----------------------------------

	public Manager create() {

		Manager result;
		result = new Manager();

		final UserAccount userAccount = this.userAccountService.createManager();
		result.setUserAccount(userAccount);

		return result;

	}

	public Collection<Manager> findAll() {

		Collection<Manager> result;
		result = this.managerRepository.findAll();
		Assert.notNull(result);
		return result;
	}

	public Manager findOne(final int managerId) {

		Assert.notNull(managerId);
		Manager result;
		result = this.managerRepository.findOne(managerId);
		return result;
	}

	public Manager save(final Manager manager) {
		Assert.notNull(manager);
		Manager result;

		if (manager.getId() != 0) {
			final Authority admin = new Authority();
			admin.setAuthority(Authority.ADMIN);

			final Actor actor = this.actorService.findByPrincipal();
			Assert.notNull(actor);

			Assert.isTrue(actor.getId() == manager.getId() || actor.getUserAccount().getAuthorities().contains(admin));

			this.actorService.checkEmail(manager.getEmail(), false);
			this.actorService.checkPhone(manager.getPhone());

			final String phone = this.actorService.checkPhone(manager.getPhone());
			manager.setPhone(phone);

			result = this.managerRepository.save(manager);

		} else {

			String pass = manager.getUserAccount().getPassword();

			final Md5PasswordEncoder code = new Md5PasswordEncoder();

			pass = code.encodePassword(pass, null);

			final UserAccount userAccount = manager.getUserAccount();
			userAccount.setPassword(pass);

			manager.setUserAccount(userAccount);

			this.actorService.checkEmail(manager.getEmail(), false);
			this.actorService.checkPhone(manager.getPhone());

			final String phone = this.actorService.checkPhone(manager.getPhone());
			manager.setPhone(phone);

			result = this.managerRepository.save(manager);

		}
		return result;

	}

	public Manager findByPrincipal() {
		Manager manager;
		UserAccount userAccount;

		userAccount = LoginService.getPrincipal();
		Assert.notNull(userAccount);
		manager = this.findByUserAccount(userAccount);
		Assert.notNull(manager);

		return manager;
	}

	public Manager findByUserAccount(final UserAccount userAccount) {
		Assert.notNull(userAccount);

		Manager result;

		result = this.managerRepository.findByUserAccountId(userAccount.getId());

		return result;
	}

	public Manager reconstruct(final RegisterManagerForm form, final BindingResult binding) {

		this.validator.validate(form, binding);

		final Manager manager = this.create();

		manager.setName(form.getName());
		manager.setSurnames(form.getSurnames());
		manager.setPhoto(form.getPhoto());
		manager.setEmail(form.getEmail());
		manager.setPhone(form.getPhone());
		manager.setAddress(form.getAddress());
		manager.getUserAccount().setUsername(form.getUsername());
		manager.getUserAccount().setPassword(form.getPassword());

		return manager;

	}

	public Manager reconstruct(final Manager manager, final BindingResult binding) {

		final Manager result;

		final Manager managerBBDD = this.findOne(manager.getId());

		if (managerBBDD != null) {

			manager.setUserAccount(managerBBDD.getUserAccount());

			this.validator.validate(manager, binding);

		}
		result = manager;
		return result;

	}
}