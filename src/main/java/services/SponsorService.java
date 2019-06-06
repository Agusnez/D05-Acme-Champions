
package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import repositories.SponsorRepository;
import security.LoginService;
import security.UserAccount;
import security.UserAccountService;
import domain.Actor;
import domain.Box;
import domain.Sponsor;
import forms.RegisterSponsorForm;

@Service
@Transactional
public class SponsorService {

	// Managed Repository ------------------------
	@Autowired
	private SponsorRepository		sponsorRepository;

	// Suporting services ------------------------

	@Autowired
	private ActorService			actorService;

	@Autowired
	private BoxService				boxService;

	@Autowired
	private UserAccountService		userAccountService;

	@Autowired
	private Validator				validator;

	@Autowired
	private ConfigurationService	configurationService;


	public Sponsor create() {

		Sponsor result;
		result = new Sponsor();

		final UserAccount userAccount = this.userAccountService.createSponsor();
		result.setUserAccount(userAccount);

		return result;

	}

	public Collection<Sponsor> findAll() {

		Collection<Sponsor> result;
		result = this.sponsorRepository.findAll();
		Assert.notNull(result);
		return result;
	}

	public Sponsor findOne(final int sponsorId) {

		Assert.notNull(sponsorId);
		Sponsor result;
		result = this.sponsorRepository.findOne(sponsorId);
		return result;
	}

	public Sponsor save(final Sponsor sponsor) {

		Assert.notNull(sponsor);
		Sponsor result;

		final Collection<String> makes = this.configurationService.findConfiguration().getMakes();
		Assert.isTrue(makes.contains(sponsor.getCreditCard().getMake()));

		if (sponsor.getId() != 0) {

			final Actor actor = this.actorService.findByPrincipal();
			Assert.notNull(actor);

			Assert.isTrue(actor.getId() == sponsor.getId());

			this.actorService.checkEmail(sponsor.getEmail(), true);
			this.actorService.checkPhone(sponsor.getPhone());

			final Date now = new Date(System.currentTimeMillis() - 1000);

			Assert.isTrue(sponsor.getCreditCard().getExpYear() - 1900 >= now.getYear());
			Assert.isTrue(sponsor.getCreditCard().getExpMonth() - 1 >= now.getMonth() || sponsor.getCreditCard().getExpYear() - 1900 > now.getYear());

			final String phone = this.actorService.checkPhone(sponsor.getPhone());
			sponsor.setPhone(phone);

			final String newUrl = this.configurationService.checkURL(sponsor.getPhoto());
			sponsor.setPhoto(newUrl);

			result = this.sponsorRepository.save(sponsor);

		} else {

			this.actorService.checkEmail(sponsor.getEmail(), true);
			this.actorService.checkPhone(sponsor.getPhone());

			String pass = sponsor.getUserAccount().getPassword();

			final Md5PasswordEncoder code = new Md5PasswordEncoder();

			pass = code.encodePassword(pass, null);

			final UserAccount userAccount = sponsor.getUserAccount();
			userAccount.setPassword(pass);

			sponsor.setUserAccount(userAccount);

			final Date now = new Date(System.currentTimeMillis() - 1000);

			Assert.isTrue(sponsor.getCreditCard().getExpYear() - 1900 >= now.getYear());
			Assert.isTrue(sponsor.getCreditCard().getExpMonth() - 1 >= now.getMonth() || sponsor.getCreditCard().getExpYear() - 1900 > now.getYear());

			final String phone = this.actorService.checkPhone(sponsor.getPhone());
			sponsor.setPhone(phone);

			final String newUrl = this.configurationService.checkURL(sponsor.getPhoto());
			sponsor.setPhoto(newUrl);

			result = this.sponsorRepository.save(sponsor);

			Box inBox, outBox, trashBox, spamBox;
			inBox = this.boxService.create();
			outBox = this.boxService.create();
			trashBox = this.boxService.create();
			spamBox = this.boxService.create();

			inBox.setName("in box");
			outBox.setName("out box");
			trashBox.setName("trash box");
			spamBox.setName("spam box");

			inBox.setActor(result);
			outBox.setActor(result);
			trashBox.setActor(result);
			spamBox.setActor(result);

			final Collection<Box> boxes = new ArrayList<>();
			boxes.add(spamBox);
			boxes.add(trashBox);
			boxes.add(inBox);
			boxes.add(outBox);

			inBox = this.boxService.saveNewActor(inBox);
			outBox = this.boxService.saveNewActor(outBox);
			trashBox = this.boxService.saveNewActor(trashBox);
			spamBox = this.boxService.saveNewActor(spamBox);

		}
		return result;
	}

	public Sponsor reconstruct(final RegisterSponsorForm form, final BindingResult binding) {

		final Sponsor sponsor = this.create();

		this.validator.validate(form, binding);

		sponsor.setName(form.getName());
		sponsor.setSurnames(form.getSurnames());
		sponsor.setPhoto(form.getPhoto());
		sponsor.setEmail(form.getEmail());
		sponsor.setPhone(form.getPhone());
		sponsor.setAddress(form.getAddress());
		sponsor.setCreditCard(form.getCreditCard());
		sponsor.getUserAccount().setUsername(form.getUsername());
		sponsor.getUserAccount().setPassword(form.getPassword());

		return sponsor;

	}

	public Sponsor reconstruct(final Sponsor sponsor, final BindingResult binding) {

		final Sponsor result;

		final Sponsor sponsorBBDD = this.findOne(sponsor.getId());

		if (sponsorBBDD != null) {

			sponsor.setUserAccount(sponsorBBDD.getUserAccount());

			this.validator.validate(sponsor, binding);

		}
		result = sponsor;

		return result;

	}

	public void flush() {
		this.sponsorRepository.flush();
	}

	// Other business methods -----------------------

	public Sponsor findByPrincipal() {
		final Sponsor sponsor;
		UserAccount userAccount;

		userAccount = LoginService.getPrincipal();
		Assert.notNull(userAccount);
		sponsor = this.findByUserAccount(userAccount);
		Assert.notNull(sponsor);

		return sponsor;
	}

	public Sponsor findByUserAccount(final UserAccount userAccount) {
		Assert.notNull(userAccount);

		Sponsor result;

		result = this.sponsorRepository.findByUserAccountId(userAccount.getId());

		return result;
	}

}
