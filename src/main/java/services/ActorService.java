
package services;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import repositories.ActorRepository;
import security.LoginService;
import security.UserAccount;
import security.UserAccountService;
import domain.Actor;

@Service
@Transactional
public class ActorService {

	//Managed Repository ---------------------------------------------------
	@Autowired
	private ActorRepository			actorRepository;

	//Supporting services --------------------------------------------------

	@Autowired
	private ConfigurationService	configurationService;

	@Autowired
	private UserAccountService		userAccountService;

	@Autowired
	private AdministratorService	administratorService;

	@Autowired
	private MessageService			messageService;

	@Autowired
	private SponsorshipService		sponsorshipService;

	@Autowired
	private FinderService			finderService;


	//Simple CRUD methods --------------------------------------------------

	public Collection<Actor> findAll() {

		final Collection<Actor> actors = this.actorRepository.findAll();

		Assert.notNull(actors);

		return actors;
	}

	public Actor findOne(final int ActorId) {

		final Actor actor = this.actorRepository.findOne(ActorId);

		//Assert.notNull(actor);

		return actor;
	}

	public Actor save(final Actor a) {

		final Actor actor = this.actorRepository.save(a);

		Assert.notNull(actor);

		return actor;
	}

	public void delete(final Actor actor) {
		Assert.notNull(actor);
		Assert.isTrue(actor.getId() != 0);
		Assert.isTrue(this.actorRepository.exists(actor.getId()));
		this.actorRepository.delete(actor);
	}

	//Other business methods----------------------------

	public Actor findByPrincipal() {
		Actor a;
		UserAccount userAccount;

		userAccount = LoginService.getPrincipal();
		Assert.notNull(userAccount);
		a = this.findByUserAccount(userAccount);
		Assert.notNull(a);

		return a;
	}

	public Actor findByUserAccount(final UserAccount userAccount) {
		Assert.notNull(userAccount);
		Actor result;
		result = this.actorRepository.findByUserAccountId(userAccount.getId());
		return result;
	}

	public UserAccount findUserAccount(final Actor actor) {
		Assert.notNull(actor);
		UserAccount result;
		result = this.userAccountService.findByActor(actor);
		return result;
	}

	public Actor editPersonalData(final Actor actor) {
		Assert.notNull(actor);
		UserAccount userAccount;

		userAccount = LoginService.getPrincipal();
		Assert.isTrue(actor.getUserAccount().equals(userAccount));
		final Actor result = this.save(actor);

		return result;
	}

	public void checkEmail(final String email, final Boolean isAdmin) {

		if (!isAdmin) {
			final boolean checkEmailOthers = email.matches("^[\\w]+@(?:[a-zA-Z0-9]+\\.)+[a-zA-Z0-9]+|(([\\w]\\s)*[\\w])+<\\w+@(?:[a-zA-Z0-9]+\\.)+[a-zA-Z0-9]+>$");
			Assert.isTrue(checkEmailOthers);
		} else {
			final boolean checkEmailAdmin = email.matches("^[\\w]+@((?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]+){0,1}|(([\\w]\\s)*[\\w])+<\\w+@((?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]+){0,1}>$");
			Assert.isTrue(checkEmailAdmin);
		}
	}

	public String checkPhone(final String phone) {
		String res = phone;

		//Esto es para contar el número de dígitos que contiene 
		int count = 0;
		for (int i = 0, len = phone.length(); i < len; i++)
			if (Character.isDigit(phone.charAt(i)))
				count++;

		if (StringUtils.isNumericSpace(phone) && count == 4) {
			res.replaceAll("\\s+", ""); //quitar espacios
			res = this.configurationService.findConfiguration().getCountryCode() + " " + phone;
			Assert.isTrue(res.contains(this.configurationService.findConfiguration().getCountryCode() + " " + phone));

		}
		return res;

	}

	public Boolean existActor(final int actorId) {
		Boolean res = false;

		final Actor actor = this.actorRepository.findOne(actorId);

		if (actor != null)
			res = true;

		return res;

	}

	public void flush() {
		this.actorRepository.flush();
	}

}
